# AGENTS.md

This repository provides JNI bindings to the ISL (Integer Set Library) and packages them for Eclipse RCP/OSGi distribution via a p2 update site. Follow this guide precisely when contributing.

## Repository Overview
- **Goal:** deliver a Java API that wraps a JNI layer for ISL, ships native binaries for Windows (x86_64), Linux (x86_64), and macOS (aarch64), and publishes an Eclipse p2 update site.
- **Primary outputs:**
  - Java API bundle (`com.emmtrix.isl.core`)
  - JNI/native bridge bundles (`com.emmtrix.isl.jni`)
  - Platform-specific OSGi fragments with native libraries (`com.emmtrix.isl.native.<os>.<arch>`)
  - p2 update site containing a feature and category

## Architecture
- **Java API**
  - Pure Java facade in `bundles/com.emmtrix.isl.core`.
  - No direct `long` pointer exposure; native handles are wrapped in private fields.
  - Java types implement `AutoCloseable` with deterministic cleanup.
  - Thread-safety: assume **ISL contexts are NOT thread-safe**. Enforce or document in Java API.
- **JNI Bridge**
  - C/C++ JNI layer in `bundles/com.emmtrix.isl.jni`.
  - JNI only exposes safe operations; validate inputs; map errors to Java exceptions.
  - Handle ISL ref-counted objects; ensure `retain/release` semantics are correct.
- **Native Build**
  - Native build uses **CMake**. Do not introduce alternative native build systems.
  - CMake produces shared libraries for each target platform (Windows, Linux, macOS).
- **OSGi Fragments**
  - Each platform has its own fragment bundle with `Bundle-NativeCode`.
  - Fragment attaches to the host JNI bundle; native libs are located under `/native/<os>/<arch>/`.
- **p2 Repository**
  - Built using Maven + Tycho.
  - Exposes features and categories for Eclipse “Install New Software…”

## Directory Structure
Create and maintain the following layout:

```
/ (repo root)
  AGENTS.md
  README.md
  LICENSE
  pom.xml                      # Tycho parent
  /bundles
    /com.emmtrix.isl.core       # Java API bundle
    /com.emmtrix.isl.jni        # JNI bridge bundle
    /com.emmtrix.isl.native.win32.x86_64
    /com.emmtrix.isl.native.linux.x86_64
    /com.emmtrix.isl.native.macosx.aarch64
  /features
    /com.emmtrix.isl.feature    # Eclipse feature definition
  /releng
    /com.emmtrix.isl.site       # p2 update site (category.xml)
  /native
    /isl-wrapper                # C/C++ JNI wrapper sources
      /include
      /src
      CMakeLists.txt
  /tests
    /java                        # JUnit tests for Java API
    /osgi-smoke                  # OSGi runtime smoke test
    /p2-e2e                      # headless install-from-p2 test
  /scripts
    build-native.sh
    build-all.sh
  /ci
    /github-actions              # workflow templates or notes
```

Do **not** place unrelated artifacts in the repo root. Every new module must be added to `pom.xml` and follow the layout above.

## Build and Test Commands
Use pinned tool versions when possible (see `pom.xml` and `CMakeLists.txt` once added).

Examples (expected in CI and local dev):

- **Build native JNI library:**
  - `cmake -S native/isl-wrapper -B build/native`
  - `cmake --build build/native --config Release`
- **Build all (Java + OSGi + p2):**
  - `mvn -V -B -DskipTests=false clean verify`
- **Run Java unit tests:**
  - `mvn -pl tests/java -DskipTests=false test`
- **OSGi smoke test:**
  - `mvn -pl tests/osgi-smoke -DskipTests=false test`
- **p2 end-to-end test (headless):**
  - `mvn -pl tests/p2-e2e -DskipTests=false test`

If you add new tests, wire them into Maven so `mvn verify` runs them.

## JNI Design Constraints (MUST)
- **No raw pointers exposed to Java.** Use opaque handles, private fields, and factory methods.
- **Errors → Java exceptions.** Map native failures to specific Java exception types.
- **Ownership rules enforced.** Wrap ISL ref-counted objects and ensure predictable release.
- **Deterministic cleanup.** Java objects must implement `AutoCloseable` and release native resources.
- **Thread-safety.** Treat ISL contexts as not thread-safe; protect access or document restrictions.
- **Leak tests.** Provide tests that detect leaked native objects (e.g., reference counters).

## Quality Constraints
- **Java formatting:** Google Java Format (or `spotless` with `google-java-format`).
  - Don’t use tabs; 2 or 4 spaces are acceptable if the formatter enforces it.
- **C/C++ formatting:** clang-format with LLVM style; include `.clang-format` when added.
- **Testing:**
  - Unit tests for Java API.
  - OSGi runtime smoke tests (bundle resolves and loads native libs).
  - One end-to-end test installing from the p2 repository into a headless Eclipse instance.
- **Reproducible builds:**
  - Pin Maven, Tycho, CMake, and JDK versions in CI.
  - Prefer explicit dependency versions.
- **License/compliance:**
  - Track ISL license (MIT) and any native dependencies (e.g., GMP).
  - Document transitive native deps and their licenses in `NOTICE` or equivalent.

## Release Process
- **Versioning:**
  - Use semantic versioning (MAJOR.MINOR.PATCH).
  - Align bundle, feature, and p2 versions.
- **Tagging:**
  - Tag releases as `vX.Y.Z`.
- **Artifacts:**
  - Build native libraries for all supported platforms.
  - Build features and p2 update site.
  - Publish update site artifacts to the release pipeline or GitHub Pages (if configured).
- **Signing (optional but recommended):**
  - If signing is enabled, sign bundles and the p2 repo using Eclipse jarsigner and metadata signing.

## CI Expectations
- GitHub Actions builds on Windows, Linux, and macOS.
- CI must:
  - Build native JNI library on each platform.
  - Run Maven/Tycho build and tests.
  - Produce p2 update site artifacts.
  - Upload update-site ZIPs or publish as artifacts.
- CI should fail on formatting violations and test failures.

## Common Pitfalls
- **JNI crashes** from invalid ownership or missing `Release`.
- **Bundle-NativeCode** misconfiguration causing the fragment not to resolve.
- **Using raw pointers** in Java (forbidden).
- **Unpinned dependencies** causing non-reproducible builds.
- **Missing licensing info** for native dependencies.

## Definition of Done
- [ ] Java API implemented with no exposed raw pointers.
- [ ] JNI layer correctly maps errors to Java exceptions.
- [ ] Native binaries built for Windows (x86_64), Linux (x86_64), macOS (aarch64).
- [ ] OSGi fragments resolve with correct `Bundle-NativeCode` entries.
- [ ] p2 update site builds and installs in headless test.
- [ ] Unit tests, OSGi smoke tests, and p2 end-to-end tests pass.
- [ ] Formatting and linting checks pass.
- [ ] Licenses for ISL and transitive native deps documented.
