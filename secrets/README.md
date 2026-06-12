# CI secrets (SOPS)

CI secrets live here as [SOPS](https://github.com/getsops/sops)-encrypted
files. The only secret stored in GitHub is `SOPS_AGE_KEY`, the [age](https://age-encryption.org)
private key that decrypts them; everything else is reviewable ciphertext.
Any runner with a checkout and that one key can build and publish.

## Inventory

| File | Keys | Consumed by |
| --- | --- | --- |
| `android-signing.yaml` | `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` | `release.yml` (GitHub APK signing) |
| `google-play.yaml` | `PLAY_SERVICE_ACCOUNT_JSON`, `PLAY_UPLOAD_KEYSTORE_BASE64`, `PLAY_UPLOAD_KEYSTORE_PASSWORD`, `PLAY_UPLOAD_KEY_ALIAS`, `PLAY_UPLOAD_KEY_PASSWORD` | `release-play.yml` |
| `codeberg.yaml` | `CB_SYNC_TOKEN` | `sync-mirrors.yml`, `sync-releases.yml` |

Top-level YAML keys are the exact env var names the build expects;
`scripts/ci/sops-env.sh` exports them verbatim.

## Keys

Files are encrypted to both recipients in [`.sops.yaml`](../.sops.yaml); either
private key decrypts.

- **CI key** — in the `SOPS_AGE_KEY` GitHub Actions secret and the MAGIC Grants
  password manager. On a maintainer's machine sops finds it at
  `~/Library/Application Support/sops/age/keys.txt` (macOS) or
  `~/.config/sops/age/keys.txt` (Linux).
- **Recovery key** — maintainer-held, never in CI; the fallback if the CI key
  is lost.

## Editing

```sh
sops edit secrets/android-signing.yaml
sops set secrets/codeberg.yaml '["CB_SYNC_TOKEN"]' '"new-value"'
sops set secrets/android-signing.yaml \
  '["ANDROID_KEYSTORE_BASE64"]' "\"$(base64 -i release.keystore)\""
```

`static-analysis.yml` checks on every push that all files still decrypt.

## Adding a secret

1. `sops edit` the relevant file (or add a new `secrets/*.yaml`).
2. Add the key to the workflow's `sops-env.sh` call.
3. Update the inventory table above.

## Self-hosted / non-GitHub runners

Provide the private key as the `SOPS_AGE_KEY` env var, or as a file pointed to
by `SOPS_AGE_KEY_FILE`. Nothing else is GitHub-specific except `github.token`,
which GitHub-compatible runners supply.

## Rotating

- **A value:** `sops edit` + commit, and revoke the old value at its issuer.
- **An age key:** add the new recipient to `.sops.yaml`, run
  `sops updatekeys --yes secrets/*.yaml`, update `SOPS_AGE_KEY` and the password
  manager, then remove the old recipient and `updatekeys` again.

Ciphertext stays in git history: treat a leaked age key as a compromise of
every value ever encrypted to it, and rotate those values too.
