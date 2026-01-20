# Hin2n Project Context

## Project Overview
**Hin2n** is an Android application that serves as a client for the **n2n** peer-to-peer VPN protocol. It allows Android devices to join n2n virtual networks.
It supports multiple versions of the n2n protocol (v1, v2s, v2, v3) by integrating the original C source code via JNI (Java Native Interface).

## Architecture & Technologies
- **Platform:** Android
- **Languages:** 
  - **Java/Kotlin:** Android UI and application logic.
  - **C/C++:** Core n2n protocol implementation and system-level networking (tun/tap).
- **Build System:** 
  - **Gradle:** Main Android build system.
  - **CMake:** Used within Gradle to build the native C/C++ libraries (`edge_v3`, `slog`, `uip`, etc.).
- **Dependencies:**
  - **Git Submodules:** Used to manage different versions of n2n (located in `bundles/`).
  - **OpenSSL:** Linked for encryption support in n2n.

## Directory Structure
- `Hin2n_android/`: The root of the Android project.
  - `app/`: The main application module.
    - `src/main/java/`: Java/Kotlin source code.
    - `src/main/cpp/`: Native source code.
      - `n2n_v3/`: Source for n2n v3 (likely symlinked or copied from `bundles`).
      - `edge_jni/`: JNI bridge code connecting Java and C.
      - `tun2tap/`: Logic to bridge Android's VPNService (TUN) with n2n's expectation (TAP).
    - `CMakeLists.txt`: Native build configuration.
- `bundles/`: Contains external libraries and n2n source code fetched via git submodules.
  - `n2n_ntop_v3/`: n2n v3 source.
  - `zstd/`: Zstandard compression library.

## Building and Running

### Prerequisites
- JDK 11 or higher.
- Android SDK.
- Android NDK (for compiling C code).

### Build Commands
Navigate to the `Hin2n_android` directory:

1.  **Initialize Submodules:**
    ```bash
    git submodule update --init --recursive
    ```

2.  **Build APK:**
    ```bash
    ./gradlew assembleNormalArmDebug
    ```
    (Or `gradlew assembleNormalArmDebug` on Windows)

3.  **Clean:**
    ```bash
    ./gradlew clean
    ```

## Development Conventions
- **Native Integration:** The project heavily relies on JNI. Changes to the n2n core logic often require updates in `src/main/cpp` and potentially the Java JNI wrappers.
- **Versioning:** Support for multiple n2n versions is a key feature. `CMakeLists.txt` defines how these different versions are compiled and linked.
- **Permissions:** The app uses `VPNService`, which requires specific Android permissions and handling.

## Key Files
- `README.md`: Project documentation.
- `Hin2n_android/app/CMakeLists.txt`: Critical for understanding how the native libraries are built and linked.
- `Hin2n_android/build.gradle`: Project-level build configuration.
- `Hin2n_android/app/src/main/AndroidManifest.xml`: Android app manifest (permissions, components).
