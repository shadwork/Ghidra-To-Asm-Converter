import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.shadwork.ghidra.asm.converter"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.mordant:mordant:2.2.0")
    implementation("com.github.ajalt.clikt:clikt:4.2.2")
    implementation("org.junit.jupiter:junit-jupiter:5.9.0")
    implementation("com.mikepenz:multiplatform-markdown-renderer-jvm:0.9.0")
    implementation("net.java.dev.jna:jna:5.13.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register("generateBuildConfig") {
    doLast {
        val configFile = file("src/${sourceSets.main.name}/kotlin/BuildConfig.kt")
        val content = "class BuildConfig {\n" +
                "    companion object {\n" +
                "        const val version = \"${version}\"\n" +
                "    }\n" +
                "}"
        configFile.parentFile.mkdirs()
        configFile.writeText(content)
    }
}

tasks.getByName("processResources").dependsOn("generateBuildConfig")