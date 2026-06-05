# Verified Apps

Verified Apps is an app signing certificate hash viewer and verifier. It checks apps against a built-in database of known-good app signing fingerprints.

You can share or copy verification info for use outside the app. If an app is not in the database, you'll have the option to open a pre-filled [GitHub submission issue](https://github.com/privacyguides/verified-apps/issues/new?template=app-submission.yml) in your browser.

## Installation

[<img src="https://github.com/NeoApplications/Neo-Backup/blob/034b226cea5c1b30eb4f6a6f313e4dadcbb0ece4/badge_github.png?raw=true"
    alt="Get it on GitHub"
    height="80">](https://github.com/privacyguides/verified-apps-android/releases/latest)
[<img src="https://raw.githubusercontent.com/ImranR98/Obtainium/b1c8ac6f2ab08497189721a788a5763e28ff64cd/assets/graphics/badge_obtainium.png"
    alt="Get it on GitHub"
    height="80">](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22org.privacyguides.verifiedapps%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fprivacyguides%2Fverified-apps-android%22%2C%22author%22%3A%22privacyguides%22%2C%22name%22%3A%22Verified%20Apps%22%2C%22preferredApkIndex%22%3A0%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22filterReleaseTitlesByRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22filterReleaseNotesByRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22verifyLatestTag%5C%22%3Afalse%2C%5C%22sortMethodChoice%5C%22%3A%5C%22date%5C%22%2C%5C%22useLatestAssetDateAsReleaseDate%5C%22%3Afalse%2C%5C%22releaseTitleAsVersion%5C%22%3Atrue%2C%5C%22trackOnly%5C%22%3Afalse%2C%5C%22versionExtractionRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22matchGroupToUse%5C%22%3A%5C%22%5C%22%2C%5C%22versionDetection%5C%22%3Atrue%2C%5C%22releaseDateAsVersion%5C%22%3Afalse%2C%5C%22useVersionCodeAsOSVersion%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22invertAPKFilter%5C%22%3Afalse%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%2C%5C%22appName%5C%22%3A%5C%22%5C%22%2C%5C%22appAuthor%5C%22%3A%5C%22%5C%22%2C%5C%22shizukuPretendToBeGooglePlay%5C%22%3Afalse%2C%5C%22allowInsecure%5C%22%3Afalse%2C%5C%22exemptFromBackgroundUpdates%5C%22%3Afalse%2C%5C%22skipUpdateNotifications%5C%22%3Afalse%2C%5C%22about%5C%22%3A%5C%22%5C%22%2C%5C%22refreshBeforeDownload%5C%22%3Afalse%2C%5C%22includeZips%5C%22%3Afalse%2C%5C%22zippedApkFilterRegEx%5C%22%3A%5C%22%5C%22%7D%22%2C%22overrideSource%22%3Anull%7D)

```
org.privacyguides.verifiedapps
40:5C:6B:D2:CA:7C:3A:AE:8F:46:3C:6F:8B:55:BC:F0:DD:AC:43:1C:5E:D8:EA:FF:65:D1:06:C9:81:7A:20:7F
```

This app shouldn't/can't be used to verify itself. The easiest way to verify this app is with a trusted AppVerifier install.

> [!TIP]
> On GrapheneOS, the best way to achieve a trusted AppVerifier installation is to install Accrescent from the GrapheneOS App Store, then download AppVerifier from Accrescent.

You can additionally (or alternatively) verify the APK you have downloaded to your desktop computer was built on GitHub with the `gh` command-line tool:

```
gh attestation verify --owner privacyguides VerifiedApps.apk
```

## What is this?

This is a fork of [AppVerifier](https://github.com/soupslurpr/AppVerifier), but many components have been removed, so it no longer serves the same purpose. Notably, it no longer includes peer-to-peer verification via clipboard sharing. This app **only** checks apps against our crowdsourced database.

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

## Roadmap

- [ ] Official F-Droid repo coming *very* soon.

- [ ] We would like this app to serve as a [content provider](https://developer.android.com/guide/topics/providers/content-providers) for other apps, so that if anyone wishes to incorporate our dataset into their Android application, they can obtain the data from this app installed on the same device, also allowing us to issue updates independently.

- [ ] Submit to F-Droid Official - need to ensure reproducible builds work, avoiding F-Droid signing is critical.

- [ ] Submit to Accrescent

- [ ] Submit to Play Store
