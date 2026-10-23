#!/usr/bin/env bash
# Proves that R8 renamed ObfuscationProbe in the release build, which is what the "Code shrinking" check relies on.
# Usage: scripts/verify-release-shrinking.sh   (builds the release variant first)
set -euo pipefail
cd "$(dirname "$0")/.."

./gradlew --quiet assembleRelease
mapping=app/build/outputs/mapping/release/mapping.txt
probe='com.muhammedalikocabey.securecheck.data.check.app.ObfuscationProbe'

line=$(grep -m1 "^${probe} -> " "$mapping" || true)
if [ -z "$line" ]; then
  echo "FAIL: ${probe} is missing from mapping.txt (removed or kept), the shrinking check cannot work." >&2
  exit 1
fi
renamed=${line##* -> }
renamed=${renamed%:}
simple=${renamed##*.}
if [ "$simple" = "ObfuscationProbe" ]; then
  echo "FAIL: ${probe} kept its name in the release build." >&2
  exit 1
fi
echo "OK: ${probe} -> ${renamed}"
