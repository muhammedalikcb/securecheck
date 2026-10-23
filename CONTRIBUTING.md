# Contributing

Pull requests are welcome: new checks, better heuristics for existing ones, tests and translations.

## Setup

JDK 17 and the Android SDK (compile SDK 36). Then:

```bash
./gradlew spotlessCheck lintDebug testDebugUnitTest assembleDebug
```

The same command runs in CI. Lint and Kotlin warnings are errors, so a clean build is the bar.
`./gradlew spotlessApply` fixes formatting.

`scripts/verify-release-shrinking.sh` builds the release variant and checks in R8's `mapping.txt` that the code-shrinking
probe class was renamed. Run it after touching `proguard-rules.pro`. The instrumented smoke test in `app/src/androidTest`
needs a device or emulator: `./gradlew connectedDebugAndroidTest`.

## Adding a check

1. Add a `CheckId` entry. Its `key` is written to exported reports, so pick it once and never rename it.
   Give it a category, a severity and the OWASP MASVS control it informs.
2. Implement `SecurityCheck` under `data/check/<category>/`. Read the system only through the interfaces in
   `domain/scan` or `data/system`, so the check can be tested with fakes.
3. Report honestly: `finding` with a `Confidence`, `unavailable` with a reason when the check could not run, never
   `passed` because something threw.
4. Register it with `@IntoSet` in the module that binds the others.
5. Add the user-facing text in `presentation/content/CheckContent.kt` and in both `values/strings.xml` and
   `values-tr/strings.xml`.
6. Add tests under `app/src/test`. A detector without a test for both the finding and the clean case is not done.

## Style

* Kotlin official style, formatted by ktlint through Spotless.
* No `java.time` (minSdk 24 without desugaring); pass a `Locale` to every formatting call.
* Comments explain why, not what.
* Commit messages: `[Area] Imperative summary`, with a short body when the reason is not obvious.
