# KDoc Inline Images

An Android Studio and IntelliJ IDEA plugin that shows images written in KDoc with Markdown syntax:

```kotlin
/**
 * Shows the login screen.
 *
 * ![Login screen](../../../../docs/login.png)
 */
fun showLogin() = Unit
```

The image appears in both the Quick Documentation popup (Ctrl+Q) and the rendered comment view
(Ctrl+Alt+Q, or the pencil icon in the gutter).

## Behaviour

- Syntax: `![alt text](path)`, optionally with a title, `![alt](path "title")`, or a path in angle
  brackets that may contain spaces, `![alt](<my images/logo.png>)`.
- Relative paths are resolved against the folder of the Kotlin source file. Absolute paths and
  `file:` URLs also work.
- An image that cannot be found stays as the plain link that Kotlin's own renderer produces.
- `http` and `https` images are not supported and also stay as plain links.
- The HTML `<img>` tag is not supported, because Kotlin's renderer drops it before the plugin sees it.
- KDoc without images is untouched and rendered by the built-in Kotlin renderer.
- In the popup, a KDoc that contains an image shows the declaration's first line as its definition,
  not the full highlighted signature.

## Compatibility

Built against Android Studio build `262.9437.185.2621.16467767` (IntelliJ platform 262) and tested there.
It has also been confirmed to work in IntelliJ IDEA. `since-build` is `262` with no upper limit; other
versions are untested.

## Install

1. Download `KDocInlineImages-0.1.1-signed.zip` (or build it, see below).
2. In Android Studio open **Settings → Plugins**, click the gear icon and choose **Install Plugin from Disk...**.
3. Select the zip file, accept the third-party plugin notice, and restart the IDE when asked.

To uninstall, open **Settings → Plugins → Installed**, find *KDoc Inline Images*, and choose **Uninstall**.

## Build the distribution

```bash
./gradlew buildPlugin
```

The installable zip is written to `build/distributions/`. By default Gradle downloads the Android
Studio version named by `androidStudioVersion` in `gradle.properties`. To build against an installed
Android Studio instead, pass its folder: `./gradlew buildPlugin -Pandroid.studio.home=/path/to/android-studio`.

## Signing

JetBrains Marketplace requires signed plugins. `./gradlew signPlugin` signs the zip when these
environment variables are set, and writes `build/distributions/KDocInlineImages-<version>-signed.zip`:

- `CERTIFICATE_CHAIN`: the certificate chain, in PEM format.
- `PRIVATE_KEY`: the encrypted private key, in PEM format.
- `PRIVATE_KEY_PASSWORD`: the private key's password.

Keep the key, certificate and password out of the repository. `./gradlew verifyPluginSignature`
checks a signed zip against `CERTIFICATE_CHAIN`.

## Release

Pushing a tag that starts with `v` runs `.github/workflows/release.yml`, which runs the tests and
coverage gate, builds and signs the plugin, and publishes a GitHub release with the signed zip attached. The tag must
match the version in `build.gradle.kts`, for example `v0.1.0` for version `0.1.0`.

## Develop

```bash
./gradlew test          # unit tests
./gradlew koverVerify   # coverage gate (80% minimum)
./gradlew runIde        # launches a sandbox Android Studio with the plugin loaded
./gradlew verifyPlugin -PverifierIdeHome=<path to an IDE installation>   # JetBrains Plugin Verifier
```

Coverage is measured on the platform-independent code in `com.pcholt.kdocinlineimages.core`. The classes
in `com.pcholt.kdocinlineimages.ide` are thin adapters to the IntelliJ Platform and are excluded from the
gate; pass `-PincludeIdeAdapters` to include them in the report.

## How it works

The plugin registers its own documentation providers ahead of the built-in ones. For a Kotlin declaration
whose KDoc contains Markdown images, it renders the KDoc with the Kotlin plugin's renderer, replaces each
`!` followed by a link with an `<img>` tag pointing at the resolved file, and returns the result for the
Quick Documentation popup. The rendered comment view is produced the same way for every KDoc comment.
In the popup, KDoc without images is left to the built-in provider. The plugin does not call the platform's internal or override-only APIs; the one remaining
Plugin Verifier note is that `InlineDocumentation`, the interface behind the rendered comment view, is
marked experimental.

## License

MIT, see [LICENSE](LICENSE).
