# DialogueForge

Мультиплатформенное приложение на Kotlin с использованием Compose Multiplatform.  
Поддерживаемые платформы: **Desktop** (JVM), **Android**, **Web** (Kotlin/Wasm).

[English version](README.md)

---

## Требования

| Инструмент | Версия |
|------------|--------|
| JDK | 17+ |
| Android SDK | API 35 (компиляция), API 24 (минимум) |
| Android Studio / IntelliJ IDEA | Последняя стабильная версия |
| Gradle | 9.5.0 (входит в состав проекта; ручная установка не требуется) |

---

## Настройка

### 1. Клонирование репозитория

```bash
git clone <repo-url>
cd DialogueForge
```

### 2. Создание файла `local.properties`

Файл **обязателен** для сборки Android и **не хранится** в репозитории, поскольку содержит путь, специфичный для машины разработчика.

В корне проекта необходимо создать `local.properties` со следующим содержимым:

```properties
sdk.dir=/путь/к/android/sdk
```

**Примеры:**

- **Windows:** `sdk.dir=C\:\\Users\\ИмяПользователя\\AppData\\Local\\Android\\Sdk`
- **macOS/Linux:** `sdk.dir=/Users/ИмяПользователя/Library/Android/sdk`

> Путь можно получить в Android Studio: **File → Project Structure → SDK Location**.

---

## Сборка всех платформ

Компиляция всех платформ без запуска:

```bash
# Windows
gradlew.bat assemble

# Unix
./gradlew assemble
```

Результат: Desktop JAR, Android APK (debug), Web (WASM production bundle).

---

## Desktop (JVM)

**Точка входа:** `src/desktopMain/kotlin/Main.kt`  
Функция `fun main()` создаёт Compose `Window` и запускает `App()`.

### Сборка

```bash
# Windows
gradlew.bat packageDistributionForCurrentOS

# Unix
./gradlew packageDistributionForCurrentOS
```

Результат: `build/compose/binaries/main/`  
Форматы: `.msi` / `.exe` на Windows, `.deb` на Linux.

### Запуск

```bash
# Windows
gradlew.bat run

# Unix
./gradlew run
```

### Отладка в IDE

В IntelliJ IDEA или Android Studio в панели **Gradle**: `DialogueForge → Tasks → compose desktop → run`. Через контекстное меню задачи **run** запускается режим **Debug**.

Альтернативный способ — запуск в режиме **Debug** через gutter-иконку у объявления `fun main()` в файле `src/desktopMain/kotlin/Main.kt`.

---

## Android

**Точка входа:** `src/androidMain/kotlin/io/github/dumbgreenfish/dialogueforge/MainActivity.kt`  
Класс `MainActivity : ComponentActivity` вызывает `setContent { App() }` в методе `onCreate()`.  
Манифест: `src/androidMain/AndroidManifest.xml`

### Сборка APK

```bash
# Debug APK
gradlew.bat assembleDebug        # Windows
./gradlew assembleDebug          # Unix

# Release APK (требуется настроенная подпись)
gradlew.bat assembleRelease
./gradlew assembleRelease
```

Результат: `build/outputs/apk/debug/` или `build/outputs/apk/release/`.

### Установка на устройство или эмулятор

При подключённом устройстве (с включённой отладкой по USB) либо запущенном эмуляторе:

```bash
gradlew.bat installDebug         # Windows
./gradlew installDebug           # Unix
```

### Отладка в Android Studio

В Android Studio выбирается конфигурация запуска **MainActivity**, после чего запуск производится в режиме **Debug** (`Shift+F9`).

Точки останова могут быть установлены в `MainActivity.kt` и в любых файлах `commonMain`.

---

## Web (Kotlin/Wasm)

**Точка входа:** `src/wasmJsMain/kotlin/Main.kt`  
Функция `fun main()` монтирует приложение в элемент `<canvas id="ComposeTarget">`.  
HTML-страница: `src/wasmJsMain/resources/index.html`

### Сервер разработки

```bash
gradlew.bat wasmJsBrowserDevelopmentRun    # Windows
./gradlew wasmJsBrowserDevelopmentRun      # Unix
```

Приложение становится доступно по адресу `http://localhost:8080`. Изменения в `commonMain` приводят к автоматической перекомпиляции и обновлению страницы.

### Продакшн-сборка

```bash
gradlew.bat wasmJsBrowserProductionWebpack    # Windows
./gradlew wasmJsBrowserProductionWebpack      # Unix
```

Результат: `build/dist/wasmJs/productionExecutable/`.  
Содержимое директории пригодно для развёртывания на любом статическом хостинге (Nginx, GitHub Pages и т.п.).

### Отладка в браузере

При работающем сервере разработки отладка выполняется через Chrome DevTools (`F12`). Source maps включены в сборку, что позволяет устанавливать точки останова непосредственно в Kotlin-файлах во вкладке **Sources**.

---

## Лицензия

Проект распространяется под лицензией **GNU General Public License v3.0**.  
Полный текст — в файле [LICENSE](LICENSE), или на сайте https://www.gnu.org/licenses/gpl-3.0.html.
