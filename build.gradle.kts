plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "com.toxa"
val pluginXmlVersion: String = file("src/main/resources/META-INF/plugin.xml")
    .readText()
    .let { Regex("<version>([^<]+)</version>").find(it)?.groupValues?.get(1) }
    ?: error("<version> not found in src/main/resources/META-INF/plugin.xml")

version = (findProperty("buildVersion") as String?) ?: pluginXmlVersion

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")

    intellijPlatform {
        create("IC", "2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.tasks")
    }
}

intellijPlatform {
    pluginConfiguration {
        version = project.version.toString()

        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            Bundle Jackson so the plugin loads in IntelliJ IDEA 2026.3 (fixes NoClassDefFoundError: com/fasterxml/jackson/databind/ObjectMapper).
        """.trimIndent()
    }

    publishing {
        token = providers.environmentVariable("JETBRAINS_MARKETPLACE_TOKEN")
        channels = listOf((findProperty("publishChannel") as String?) ?: "default")
    }

    pluginVerification {
        ides {
            create(org.jetbrains.intellij.platform.gradle.IntelliJPlatformType.IntellijIdeaCommunity, "2025.1.4.1")
        }
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
