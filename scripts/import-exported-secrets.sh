#!/usr/bin/env bash
# One-time SOPS migration helper (see secrets/MIGRATION.md): copy the files
# exported by export-secrets-once.yml into secrets/, then verify the values
# decrypt, both keystores open, the SSH key parses, and the service-account
# JSON is valid. Needs the age private key (default key file or SOPS_AGE_KEY).
#
# Usage: scripts/import-exported-secrets.sh <dir downloaded from the artifact>
set -euo pipefail
set +x  # never echo plaintext, even if xtrace is inherited

SRC="${1:?usage: $0 <dir containing the sops-migration-export artifact>}"
cd "$(dirname "$0")/.."

extract() { # <file> <key>
  sops decrypt --extract "[\"$2\"]" "secrets/$1"
}

fail=0
note_fail() {
  echo "FAIL: $*" >&2
  fail=1
}

# --- import -----------------------------------------------------------------
for f in android-signing.yaml google-play.yaml codeberg.yaml; do
  if [ ! -f "$SRC/$f" ]; then
    echo "Missing $SRC/$f — was the artifact downloaded completely?" >&2
    exit 1
  fi
  cp "$SRC/$f" "secrets/$f"
  # Re-align recipients with .sops.yaml in case they changed since the export.
  sops updatekeys --yes "secrets/$f" >/dev/null 2>&1 || true
  echo "Imported secrets/$f"
done

# --- value presence ----------------------------------------------------------
echo
echo "Checking imported values:"
check_keys() { # <file> <key>...
  local file="$1" v
  shift
  for key in "$@"; do
    if ! v="$(extract "$file" "$key" 2>/dev/null)"; then
      note_fail "$file: cannot decrypt $key (wrong age key?)"
    elif [ -z "$v" ] || [ "$v" = "CHANGEME" ] || [ "$v" = "null" ]; then
      note_fail "$file: $key is empty or a placeholder"
    else
      echo "  ok: $file: $key"
    fi
  done
}
check_keys android-signing.yaml \
  ANDROID_KEYSTORE_BASE64 ANDROID_KEYSTORE_PASSWORD ANDROID_KEY_ALIAS ANDROID_KEY_PASSWORD
check_keys google-play.yaml \
  PLAY_SERVICE_ACCOUNT_JSON PLAY_UPLOAD_KEYSTORE_BASE64 \
  PLAY_UPLOAD_KEYSTORE_PASSWORD PLAY_UPLOAD_KEY_ALIAS PLAY_UPLOAD_KEY_PASSWORD
check_keys codeberg.yaml CB_SYNC_TOKEN

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

# --- keystores really open --------------------------------------------------
check_keystore() { # <file> <b64-key> <pass-key> <alias-key> <label>
  local file="$1" b64="$2" passkey="$3" aliaskey="$4" label="$5"
  if ! command -v keytool >/dev/null 2>&1; then
    echo "  skip: keytool not on PATH; cannot verify $label keystore"
    return
  fi
  extract "$file" "$b64" | base64 -d > "$tmp/$label.keystore" 2>/dev/null || {
    note_fail "$label keystore is not valid base64"
    return
  }
  # password via env (:env) so it's not in the process list
  if KS_PASS="$(extract "$file" "$passkey")" \
     keytool -list -keystore "$tmp/$label.keystore" \
       -storepass:env KS_PASS -alias "$(extract "$file" "$aliaskey")" \
       >/dev/null 2>&1; then
    echo "  ok: $label keystore opens with the stored password and alias"
  else
    note_fail "$label keystore, password, or alias do not match"
  fi
}
echo
echo "Checking keystores (store password + alias):"
check_keystore android-signing.yaml ANDROID_KEYSTORE_BASE64 \
  ANDROID_KEYSTORE_PASSWORD ANDROID_KEY_ALIAS release
check_keystore google-play.yaml PLAY_UPLOAD_KEYSTORE_BASE64 \
  PLAY_UPLOAD_KEYSTORE_PASSWORD PLAY_UPLOAD_KEY_ALIAS play-upload

# --- service account JSON is JSON -------------------------------------------
if extract google-play.yaml PLAY_SERVICE_ACCOUNT_JSON | jq -e .client_email >/dev/null 2>&1; then
  echo "  ok: PLAY_SERVICE_ACCOUNT_JSON parses as a service-account key"
else
  note_fail "PLAY_SERVICE_ACCOUNT_JSON is not valid service-account JSON"
fi

echo
if [ "$fail" -ne 0 ]; then
  echo "Some checks FAILED — fix the values (sops edit secrets/<file>) before committing." >&2
  exit 1
fi
echo "All checks passed. Review and commit:"
echo "  git add secrets/ && git commit -m 'Populate SOPS secrets store'"
