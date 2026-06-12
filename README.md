# Verified Apps

Verified Apps is an app signing certificate hash viewer and verifier. It checks apps against a built-in database of known-good app signing fingerprints.

You can share or copy verification info for use outside the app. If an app is not in the database, you'll have the option to open a pre-filled [GitHub submission issue](https://github.com/privacyguides/verified-apps/issues/new?template=app-submission.yml) in your browser. We consider it extremely important for you to read this entire README file before using this app.

## Installation

[<img src="https://github.com/NeoApplications/Neo-Backup/blob/034b226cea5c1b30eb4f6a6f313e4dadcbb0ece4/badge_github.png?raw=true"
    alt="Get it on GitHub"
    height="80">](https://github.com/privacyguides/verified-apps-android/releases/latest)
[<img src="https://raw.githubusercontent.com/ImranR98/Obtainium/b1c8ac6f2ab08497189721a788a5763e28ff64cd/assets/graphics/badge_obtainium.png"
    alt="Get it on Obtainium"
    height="80">](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22org.privacyguides.verifiedapps%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fprivacyguides%2Fverified-apps-android%22%2C%22author%22%3A%22privacyguides%22%2C%22name%22%3A%22Verified%20Apps%22%2C%22preferredApkIndex%22%3A0%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22filterReleaseTitlesByRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22filterReleaseNotesByRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22verifyLatestTag%5C%22%3Afalse%2C%5C%22sortMethodChoice%5C%22%3A%5C%22date%5C%22%2C%5C%22useLatestAssetDateAsReleaseDate%5C%22%3Afalse%2C%5C%22releaseTitleAsVersion%5C%22%3Atrue%2C%5C%22trackOnly%5C%22%3Afalse%2C%5C%22versionExtractionRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22matchGroupToUse%5C%22%3A%5C%22%5C%22%2C%5C%22versionDetection%5C%22%3Atrue%2C%5C%22releaseDateAsVersion%5C%22%3Afalse%2C%5C%22useVersionCodeAsOSVersion%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22invertAPKFilter%5C%22%3Afalse%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%2C%5C%22appName%5C%22%3A%5C%22%5C%22%2C%5C%22appAuthor%5C%22%3A%5C%22%5C%22%2C%5C%22shizukuPretendToBeGooglePlay%5C%22%3Afalse%2C%5C%22allowInsecure%5C%22%3Afalse%2C%5C%22exemptFromBackgroundUpdates%5C%22%3Afalse%2C%5C%22skipUpdateNotifications%5C%22%3Afalse%2C%5C%22about%5C%22%3A%5C%22%5C%22%2C%5C%22refreshBeforeDownload%5C%22%3Afalse%2C%5C%22includeZips%5C%22%3Afalse%2C%5C%22zippedApkFilterRegEx%5C%22%3A%5C%22%5C%22%7D%22%2C%22overrideSource%22%3Anull%7D)
[<img src="https://f-droid.org/badge/get-it-on.png"
    alt="Get it on F-Droid"
    height="80">](https://privacyguides.github.io/fdroid/repo/)

```
org.privacyguides.verifiedapps
40:5C:6B:D2:CA:7C:3A:AE:8F:46:3C:6F:8B:55:BC:F0:DD:AC:43:1C:5E:D8:EA:FF:65:D1:06:C9:81:7A:20:7F
```

> [!NOTE]
> A separate build is published to Google Play under the package ID `org.privacyguides.verifiedapps.play`. It is built reproducibly from the same source tag, but is signed by Google via Play App Signing — so its signing certificate hash differs from the GitHub build **by design** and is **not** the fingerprint shown above. The GitHub build above remains the canonical, independently verifiable release; the distinct package ID means the two never collide and can be installed side by side. The Play build is produced and uploaded by the [`release-play.yml`](.github/workflows/release-play.yml) workflow. The exact Android App Bundle submitted to Play is attached to each GitHub release as `VerifiedApps-<version>-play.aab` for reference — it carries `gh attestation` provenance like the APK, but it is **not** meant for sideloading (Google re-signs the APKs it distributes from this bundle).

**This app shouldn't/can't be used to verify itself.** We do not recommend any specific way to verify the signing certificate of the APK file you've downloaded. However, many people use an AppVerifier install they trust to check the signatures of their APK files. Obtainium is another app which will display the signing certificate of apps it's downloaded.

> [!TIP]
> On GrapheneOS, the best way to achieve a trusted AppVerifier installation is to install Accrescent from the GrapheneOS App Store, then download AppVerifier from Accrescent.

You can additionally (or alternatively) verify the APK you have downloaded to your desktop computer was built by our automated workflows on GitHub with the `gh` command-line tool:

```
gh attestation verify --owner privacyguides VerifiedApps.apk
```

<details><summary>Example</summary>

When you verify the provenance of our APK file with the `gh` CLI, you will see a result like the following. 

> [!IMPORTANT]
> - Pay attention to the build repo, it should match this GitHub repository.
> - Pay attention to the build workflow, this is the workflow which built the APK file, which should generally be `release.yml` at the current tag number.
> - Pay attention to the signer repo, this is always `privacyguides/.github` for every project we build on GitHub.
> - Pay attention to the signer workflow, this is always our organization-wide `sign-artifact.yml` workflow.

<pre>
$ gh attestation verify --owner privacyguides VerifiedApps-26.6.1.apk 
Loaded digest sha256:40cf304f43368ca1611138b88e253c7c01b521eb6781517329952bcb28c7f868 for file://VerifiedApps-26.6.1.apk
Loaded 1 attestation from GitHub API

The following policy criteria will be enforced:
- Predicate type must match:................ https://slsa.dev/provenance/v1
- Source Repository Owner URI must match:... https://github.com/privacyguides
- Subject Alternative Name must match regex: (?i)^https://github.com/privacyguides/
- OIDC Issuer must match:................... https://token.actions.githubusercontent.com

✓ Verification succeeded!

The following 1 attestation matched the policy criteria

- Attestation #1
  - Build repo:..... <mark>privacyguides/verified-apps-android</mark>
  - Build workflow:. .github/workflows/<mark>release.yml@refs/tags/26.6.1</mark>
  - Signer repo:.... <mark>privacyguides/.github</mark>
  - Signer workflow: .github/workflows/<mark>sign-artifact.yml</mark>@5c41b37a937aab5e50262f3ab672fc9b9438dbf9
</pre>

</details>

## What is this?

This is a fork of [AppVerifier](https://github.com/soupslurpr/AppVerifier), but many components have been removed, so it no longer serves the same purpose. Notably, it no longer includes peer-to-peer verification via clipboard sharing. This app **only** checks apps against our crowdsourced database.

### App verifications & reliability

> [!IMPORTANT]
> We maintain and release this app and our dataset in good faith and on a best-effort basis. We are not committing to perfect security, immediate bug fixes, or comprehensive app coverage in our dataset. As always with security tools like this, you must consider your own threat model and plan accordingly. We cannot advise you personally on the appropriate security measures for your specific situation. Your use of this app is at your own risk, and you may not hold any contributors liable for any adverse events which may occur.

Please note that **mismatch** results may not always indicate an illegitimate app. For example, if we only have the F-Droid version of an app in our database, and you downloaded the app from Google Play where Google signed it with their own certificate, then your app would not match our database and will be flagged.

When you find a mismatch, we **always** recommend submitting that app information to our issue tracker, you will be prompted to do so within the app. We will work to verify the legitimacy of your submission. If the app you have is indeed legitimate, we will include it in the next app release. If the app you have is illegitimate, we will inform you in the submitted issue.

### What this app is good for

1. **Contributing to Privacy Guides' [crowdsourced database](https://github.com/privacyguides/verified-apps/) of developer signing certificates.**

   If an app on your device is listed as unknown or a mismatch in this app, you will be shown a button to make a submission to our issue tracker, and it will automatically fill out the submission form to make the process as easy as possible for you. You will still need an account with our issue tracker to make a submission.
  
2. **Comparing your installed apps to Privacy Guides' database without needing to trust any additional parties/developers.**
  
   We include our entire database locally in this app, which is built and signed by us. We therefore control the entire pipeline end-to-end, and once downloaded your local data can never be modified without updating the app to a new build.

### What this app is not good for

1. **Comparing your apps to anyone's hashes besides our crowdsourced database.**
  
   We've removed all clipboard/user-based verification tools, as many other apps already provide this functionality well, and we do not want to maintain parallel features unnecessarily.

## Alternatives

We recommend continuing to use the original [AppVerifier](https://github.com/soupslurpr/AppVerifier) for general-purpose signing key verification. There are also some forks such as [AppVerifierBG](https://github.com/RoundSalmon4/AppVerifierBG) which add additional features like a locally-stored personal database, database exports, and more.

The goal of our app is to be a streamlined and trusted source of information, and we will not be adding any features unrelated to our own dataset like the features AppVerifierBG is building. Our app could be used in conjunction with these to verify AppVerifier itself or other app verification forks (assuming they are in our database), or vice versa. This may be especially useful if you downloaded our app and the other app from different sources.
