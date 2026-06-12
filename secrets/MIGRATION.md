# One-time migration: GitHub secrets → SOPS

Delete this file and `.github/workflows/export-secrets-once.yml` once done.
The plumbing is in place; `secrets/*.yaml` hold `CHANGEME` placeholders until
the steps below replace them.

## 1. Store the CI age key

Generated at `~/Library/Application Support/sops/age/keys.txt` (the recovery
key in `.sops.yaml` needs nothing here).

- Copy the `AGE-SECRET-KEY-...` line into the MAGIC Grants password manager.
- `gh secret set SOPS_AGE_KEY < ~/Library/Application\ Support/sops/age/keys.txt`

## 2. Push

`sync-mirrors.yml` will fail until step 3 (placeholder SSH key); harmless, it
catches up on the next green run.

## 3. Populate values

Recover them from the existing GitHub secrets:

```sh
gh workflow run export-secrets-once.yml
gh run watch
gh run download --name sops-migration-export --dir sops-migration-export
bash scripts/import-exported-secrets.sh sops-migration-export
rm -rf sops-migration-export
```

Or `sops edit` each file directly from the password manager. Then:

```sh
git add secrets/ && git commit -m "Populate SOPS secrets store" && git push
```

## 4. Retire the old GitHub secrets

Once the mirror and decrypt-check are green (ideally after a tagged release):

```sh
for s in ANDROID_KEYSTORE_BASE64 ANDROID_KEYSTORE_PASSWORD ANDROID_KEY_ALIAS \
         ANDROID_KEY_PASSWORD PLAY_SERVICE_ACCOUNT_JSON PLAY_UPLOAD_KEYSTORE_BASE64 \
         PLAY_UPLOAD_KEYSTORE_PASSWORD PLAY_UPLOAD_KEY_ALIAS PLAY_UPLOAD_KEY_PASSWORD \
         CB_SYNC_TOKEN; do gh secret delete "$s"; done
```

`ACTIONS_SSH_KEY` may be a shared org-level secret — check `gh secret list --org privacyguides`
before deleting. Then remove this file and the export workflow.
