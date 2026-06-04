# Verified Apps

Verified Apps is an app signing certificate hash viewer and verifier. It checks apps against a built-in database of known-good app signing fingerprints.

You can share or copy verification info for use outside the app. If an app is not in the database, you'll have the option to open a pre-filled [GitHub submission issue](https://github.com/privacyguides/verified-apps/issues/new?template=app-submission.yml) in your browser.

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
