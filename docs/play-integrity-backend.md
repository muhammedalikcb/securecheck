# Verifying Play Integrity on a server

The app can ask Google Play for an integrity token, but it cannot judge that token itself. The verdict inside is
encrypted for your server, and a client that decoded it on the device would be asking a possibly tampered device to
vouch for itself. The in-app check therefore reports only "a token was issued" and shows as informational.

## Enable it in the app

Set your Google Cloud project number before building:

```bash
SECURECHECK_INTEGRITY_PROJECT_NUMBER=123456789012 ./gradlew assembleRelease
```

Without it the check shows as "not configured" and does not count against the score.

## What the server must do

1. **Issue a nonce.** Create a random value per request (at least 16 bytes, URL-safe Base64) and remember it
   with a short expiry. Bind it to the action being protected, for example a hash of the request body.
2. **Receive the token** from the app together with that nonce.
3. **Decode it with Google.** Call `decodeIntegrityToken` of the Play Integrity API with a service account that
   has access to your project. Do not decode locally with your own keys unless you manage them as Google documents.
4. **Check the response**, in this order:
   * `requestDetails.nonce` equals the nonce you issued, and it has not been used before.
   * `requestDetails.requestPackageName` is your package name, and the request is recent.
   * `appIntegrity.appRecognitionVerdict` is `PLAY_RECOGNIZED`, and `certificateSha256Digest` matches your
     signing certificate.
   * `deviceIntegrity.deviceRecognitionVerdict` contains `MEETS_DEVICE_INTEGRITY` (or the stronger label you need).
5. **Decide on the result on the server.** Never send "passed" back for the app to trust.

## Key attestation

The app's `key_attestation` check reads the boot state from a hardware-backed key certificate. The same rule applies:
for a decision that matters, send the certificate chain to your server and verify it against Google's attestation
root certificates, and check the challenge you issued.

## Why this is documented rather than built in

A backend means an account, hosting and keys, and this project ships none. Anything shipped here would be a sample
that looks like protection without being any.
