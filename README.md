# Targetprocess Connector

IntelliJ IDEA plugin that exposes [Targetprocess](https://www.targetprocess.com/) as a task server in IntelliJ's built-in Tasks &amp; Contexts framework. Browse, search, and switch between your Targetprocess assignables (user stories, bugs, features, tasks) without leaving the IDE.

## Features

- Targetprocess shows up as a server type under **Settings | Tools | Tasks | Servers**.
- Pulls assignables from `/api/v2/assignable`, filtered to items assigned to you and not in a final state.
- Server-side search by name or numeric id (uses Targetprocess's native query syntax — `NATIVE_SEARCH`).
- Recognises `TP-<id>` patterns to switch tasks via **Open Task...** (`Ctrl/Cmd+Shift+A` &rarr; "Open Task").
- Maps bugs and user stories to IntelliJ's task type icons.

## Requirements

- IntelliJ IDEA **2025.1.4.1** or newer (build `251+`), Community or Ultimate.
- A Targetprocess instance and a personal **access token** (Targetprocess: *Profile &rarr; Access Tokens*).

## Install

Until the plugin is on JetBrains Marketplace, install the local build:

```bash
./gradlew buildPlugin
```

Then in IntelliJ: **Settings | Plugins | &#x2699; | Install Plugin from Disk...** and pick `build/distributions/target-process-connector-<version>.zip`.

## Configure

1. **Settings | Tools | Tasks | Servers**.
2. Click **+** &rarr; **Targetprocess**.
3. Fill in:
   - **URL** — your Targetprocess base URL, e.g. `https://yourcompany.tpondemand.com`
   - **Access Token** — generated in Targetprocess
4. **Test** to verify the connection.

The token is passed as an `access_token` query parameter on each API call.

## Build &amp; Develop

```bash
./gradlew runIde         # launch a sandbox IDE with the plugin loaded
./gradlew build          # compile and package
./gradlew verifyPlugin   # run JetBrains plugin verifier against the target IDE
./gradlew test           # run tests
```

The plugin uses Java 21, Kotlin Gradle DSL, and IntelliJ Platform Gradle Plugin 2.7.1.

### Versioning

Semantic versioning. The version is declared once in `build.gradle.kts` (`version = ...`) and injected into `plugin.xml` at build time. Override per-build with `-PbuildVersion=1.2.3`.

## Project Layout

```
src/main/java/com/toxa/targetprocess/
    TargetprocessRepositoryType.java   # extension-point factory
    TargetprocessRepository.java       # REST client and query builder
    TargetprocessRepositoryEditor.java # settings panel
    Assignable.java                    # Task model
    TargetprocessResponse.java         # Jackson DTO
src/main/resources/META-INF/plugin.xml # plugin descriptor (id, deps, extensions)
src/main/resources/icons/              # bug / feature / other / plugin icons
```

## License

[MIT](LICENSE)
