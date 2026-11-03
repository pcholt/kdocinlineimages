# KDoc Inline Images

IntelliJ Platform plugin (Android Studio) that renders Markdown images in KDoc popups and rendered comments.

## Project decisions
- Unit tests: required. Coverage of the platform-independent code (`core` package) must stay at or above 80% (`./gradlew koverVerify`). The `ide` adapter package is excluded from the gate.
- Docker: not required.
- Clean architecture: not decided. Platform-independent logic lives in `core` and IntelliJ adapters in `ide`, to keep logic unit-testable.

## Layout
- `core`: image syntax parsing, path resolution, HTML rewriting. No IntelliJ imports.
- `ide`: documentation providers registered in `plugin.xml`.
