# DialogueForge

A Kotlin Multiplatform application built with Compose Multiplatform.  
Targets: **Desktop** (JVM), **Android**, **Web** (Kotlin/Wasm).

[Русская версия](README.ru.md)

---

## Requirements

| Tool | Version                                             |
|------|-----------------------------------------------------|
| JDK | 17+                                                 |
| Android SDK | API 37 (compile), API 28 (min)                      |
| Android Studio / IntelliJ IDEA | Latest stable recommended                           |
| Gradle | 9.5.0 (wrapper included — no manual install needed) |

---

## Setup

### 1. Clone the repository

```bash
git clone <repo-url>
cd DialogueForge
```

### 2. Create `local.properties`

This file is **required** for Android builds and is **not committed** to the repository (it contains a path specific to your machine).

Create `local.properties` in the project root:

```properties
sdk.dir=/path/to/your/android/sdk
```

**Examples:**

- **Windows:** `sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk`
- **macOS/Linux:** `sdk.dir=/Users/YourName/Library/Android/sdk`

> You can find this path in Android Studio: **File → Project Structure → SDK Location**.

### 3. Create `keystore.properties` (release builds only)

Required for signed APK / AAB builds. **Not committed** to the repository.

Create `keystore.properties` in the project root:

```properties
storeFile=/absolute/path/to/keystore.jks
storePassword=your-store-password
keyAlias=your-key-alias
keyPassword=your-key-password
```

> On Windows use forward slashes in the path: `C:/Users/YourName/.ssh/keystore.jks`

---

## Build All Platforms at Once

To compile all platforms without running them:

```bash
# Windows
gradlew.bat assemble

# Unix
./gradlew assemble
```

This builds: Desktop JAR + Android APK (debug) + Web (WASM production bundle).

---

## Desktop (JVM)

**Entry point:** `src/desktopMain/kotlin/Main.kt`  
Function: `fun main()` — creates a Compose `Window` and launches `App()`.

### Build

```bash
# Windows
gradlew.bat packageDistributionForCurrentOS

# Unix
./gradlew packageDistributionForCurrentOS
```

Output: `build/compose/binaries/main/`  
Formats: `.msi` / `.exe` on Windows, `.deb` on Linux.

### Run

```bash
# Windows
gradlew.bat run

# Unix
./gradlew run
```

### Debug in IDE

Open the project in IntelliJ IDEA or Android Studio.  
In the **Gradle** panel (right sidebar): `DialogueForge → Tasks → compose desktop → run`  
Right-click **run** → **Debug**.

Alternatively, open `src/desktopMain/kotlin/Main.kt` and click the green **Run** gutter icon next to `fun main()`.

---

## Android

**Entry point:** `src/androidMain/kotlin/io/github/dumbgreenfish/dialogueforge/MainActivity.kt`  
Class: `MainActivity : ComponentActivity` — calls `setContent { App() }` in `onCreate()`.  
Manifest: `src/androidMain/AndroidManifest.xml`

### Build APK

```bash
# Debug APK
gradlew.bat assembleDebug        # Windows
./gradlew assembleDebug          # Unix

# Release APK (requires keystore.properties)
gradlew.bat assembleRelease      # Windows
./gradlew assembleRelease        # Unix
```

Output: `build/outputs/apk/debug/` or `build/outputs/apk/release/`

### Build AAB (Google Play)

```bash
gradlew.bat bundleRelease        # Windows
./gradlew bundleRelease          # Unix
```

Output: `build/outputs/bundle/release/`  
Requires `keystore.properties` — see [Setup → Step 3](#3-create-keystoreproperties-release-builds-only).

### Install and Run on Device / Emulator

Connect a device (USB debugging on) or start an emulator, then:

```bash
gradlew.bat installDebug         # Windows
./gradlew installDebug           # Unix
```

### Debug in Android Studio

1. Open the project in Android Studio.
2. Select **MainActivity** run configuration in the top toolbar.
3. Click the **Debug** button (bug icon) or press `Shift+F9`.

Set breakpoints directly in `MainActivity.kt` or anywhere in `commonMain`.

---

## Web (Kotlin/Wasm)

**Entry point:** `src/wasmJsMain/kotlin/Main.kt`  
Function: `fun main()` — mounts the app into a `<canvas id="ComposeTarget">` element.  
HTML host page: `src/wasmJsMain/resources/index.html`

### Development Server (with hot reload)

```bash
gradlew.bat wasmJsBrowserDevelopmentRun    # Windows
./gradlew wasmJsBrowserDevelopmentRun      # Unix
```

Opens automatically at `http://localhost:8080`. Changes in `commonMain` trigger recompile and reload.

### Production Build

```bash
gradlew.bat wasmJsBrowserProductionWebpack    # Windows
./gradlew wasmJsBrowserProductionWebpack      # Unix
```

Output: `build/dist/wasmJs/productionExecutable/`  
Deploy the contents of this directory to any static hosting (Nginx, GitHub Pages, etc.).

### Debug in Browser

Run the development server, then open Chrome DevTools (`F12`).  
Source maps are included — you can set breakpoints on Kotlin source files in the **Sources** tab.

---

## License

This project is licensed under the **GNU General Public License v3.0**.  
See [LICENSE](LICENSE) for the full text, or visit https://www.gnu.org/licenses/gpl-3.0.html.
