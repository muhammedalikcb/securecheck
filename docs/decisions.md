# Design decisions

Short records of choices that look like omissions. Each says what was decided and what would change it.

## System font, not an embedded one

The app uses the platform font. On most devices that is Roboto or the manufacturer's own sans. An embedded font
would make the look identical everywhere, but it adds roughly 300 KB per weight to the APK, needs a license
review, and replaces the typeface a user already chose for their device, including their accessibility font
settings. Numbers use tabular figures through `NumericStyle`, so scores and counts already line up.

Revisit if screenshots from several manufacturers show a layout that breaks with their default font.

## One Gradle module

The app has about 80 source files in a single `:app` module. `domain/` has no Android types, so it could become a
pure Kotlin module. That would speed up nothing measurable at this size, add a second build configuration to keep
in sync (lint, detekt, kover, R8 rules), and nobody else consumes the module. The package layout already enforces
the boundary: `domain/` imports nothing from `data/` or `presentation/`.

Revisit if a second app or a CLI needs the scan engine, or if clean builds become slow enough to matter.

## History is encrypted with a Keystore key

Reports are sealed with AES-256-GCM. The key is generated in the Android Keystore and never leaves it. A report
file that starts with `{` is read as plain JSON, so history from the first v2 builds stays readable. A lost key or
a modified file fails the GCM tag and is skipped like any corrupt file. This protects copied files and backups;
it does not protect against root, which can use the key while the app runs. It is stated in the README.
