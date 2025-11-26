/*
 * Farewell is an addon PaperMC plugin for BromineMC designed to sunset the server.
 *
 * Copyright (C) 2025 BromineMC
 * Copyright (C) 2025 VidTu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

plugins {
    id("java")
    alias(libs.plugins.run.paper)
}

// Extract the Minecraft version.
val artifactVersion: String = libs.versions.paper.get()
val mcVersion: String = artifactVersion.substringBefore('-')

// Language.
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

// Metadata.
group = "ru.brominemc.farewell"
base.archivesName = "Farewell"
version = "rolling"
description = "An addon PaperMC plugin for BromineMC designed to sunset the server."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") // Paper.
}

dependencies {
    // Annotations.
    compileOnly(libs.jspecify)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.error.prone.annotations)

    // Paper.
    compileOnly(libs.paper)
}

// Compile with UTF-8, Java 21, and with all debug options.
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-g", "-parameters"))
    options.release = 21
}

tasks.withType<ProcessResources> {
    // Filter with UTF-8.
    filteringCharset = "UTF-8"

    // Expand Minecraft version.
    inputs.property("mcVersion", mcVersion)
    filesMatching("paper-plugin.yml") {
        expand(inputs.properties)
    }
}

// Add LICENSE and manifest into the JAR file.
tasks.withType<Jar> {
    from("LICENSE")
    from("NOTICE")
    manifest {
        attributes(
            "Specification-Title" to "Farewell",
            "Specification-Version" to "rolling",
            "Specification-Vendor" to "BromineMC",
            "Implementation-Title" to "Farewell",
            "Implementation-Version" to "rolling",
            "Implementation-Vendor" to "VidTu"
        )
    }
}

tasks.runServer {
    // Configure the "runServer" task version.
    minecraftVersion(mcVersion)

    // Add Via* plugins for convenience.
    downloadPlugins {
        modrinth("viaversion", "5.5.1")
        modrinth("viabackwards", "5.5.1")
    }

    // Setup debug args.
    jvmArgs("@../dev/args.vm.txt")
    args(file("dev/args.app.txt").readLines())
}
