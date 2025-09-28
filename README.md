# geno-kotlin

Minimal Kotlin implementation + tests for the **Geno** module.

## Project layout

```
geno-kotlin/
├─ src/
│  ├─ main/kotlin/...      # library / app code
│  └─ test/kotlin/...      # JUnit tests
├─ build.gradle.kts
├─ settings.gradle.kts
└─ gradlew
```

## Requirements (macOS/Linux)

- JDK 17+
- Gradle Wrapper (included)

> Tip: on first use, make the wrapper executable:
>
> ```bash
> chmod +x ./gradlew
> ```

## Run the program

If a `main` entry-point is configured via the Gradle **application** plugin (e.g., `application { mainClass = "org.example.MainKt" }`), you can run:

```bash
./gradlew run
```

If there is no `main` yet, skip this step and just run tests.

## Run the tests

```bash
./gradlew test --tests "org.example.GenoTest"
```

## Clean build (optional)

```bash
./gradlew clean build -q
```

---
