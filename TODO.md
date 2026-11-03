# TODO

## Split `core` into its own Gradle module
- [ ] Create a `core` Gradle module containing `com.pcholt.kdocinlineimages.core` and its tests, with no IntelliJ Platform dependency.
- [ ] Make the plugin module depend on `core`, and keep only the `ide` adapters in it.
- [ ] Move the Kover coverage gate (80% minimum) to the `core` module and drop the `includeIdeAdapters` exclusion switch.
- [ ] Make the CI test and coverage step run without downloading an IDE; only `buildPlugin` should need one.
- [ ] Check the distribution zip still contains the core classes (bundle them into the plugin jar or ship the core jar in `lib/`).

## Compile against IntelliJ IDEA Community and compare with Android Studio
- [ ] Add an `intellijIdea("2026.2")` build variant (or Gradle property) alongside the Android Studio one.
- [ ] Build the plugin against IntelliJ IDEA Community and confirm it compiles with no source changes.
- [ ] Compare it with the Android Studio build: same plugin descriptor, same extension points, and the same compiled adapter classes apart from platform version metadata.
- [ ] Run the IntelliJ IDEA Community build in a sandbox IDE and check images in the popup and the rendered view.
- [ ] Run the plugin verifier against both IDE builds and compare the reports.
- [ ] Decide which IDE CI compiles against, and update the README compatibility section.
