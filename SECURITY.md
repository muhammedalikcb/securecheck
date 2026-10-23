# Security policy

## Reporting a vulnerability

Please report security problems privately through
[GitHub security advisories](https://github.com/muhammedalikcb/securecheck/security/advisories/new).
Do not open a public issue for them.

Include the affected version, the device and Android version, and the steps to reproduce.
You will get a first reply within a week.

## What counts

* A way for another app or a remote party to read stored reports or settings.
* A way to make a report leave the device without the user sharing it.
* A flaw in how the app handles files, intents or the share flow.

## What does not

SecureCheck runs on the device it inspects, so a determined attacker with control of that device can
hide from any of its checks. Detection bypasses (a root hider that defeats the root check, a Frida build that
avoids the Frida check) are welcome as ordinary issues using the "False positive or false negative" template, not as
vulnerabilities. They are a known limit of client-side checks, described in the README.

## Supported versions

Only the latest release receives fixes.
