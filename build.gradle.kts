group = "com.example"

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.palantir.git-version") version "3.0.0"
}

group = "com.example"

// Git-based versioning with fallback
val gitVersion: groovy.lang.Closure<String> by extra
version = try {
    gitVersion()
} catch (e: Exception) {
    // Fallback version if git is not available or crashes
    "1.0.0-SNAPSHOT"
}

description = "Async Dungeon Generator Plugin for Paper 1.21.3+ (Compatible with 1.21.4)"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.release.set(21)
    }
    
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    
    runServer {
        minecraftVersion("1.21.4")
    }
    
    // Task to copy JAR to Paper server plugins folder
    register<Copy>("copyToServer") {
        dependsOn("jar")
        from("build/libs")
        into("D:/dev-server-1.21.4-towny/plugins")
        include("*.jar")
        
        doFirst {
            // Remove old plugin JARs before copying new one
            val pluginsDir = file("D:/dev-server-1.21.4-towny/plugins")
            if (pluginsDir.exists()) {
                pluginsDir.listFiles()?.forEach { file ->
                    if (file.name.startsWith("GenDungeonAsync-") && file.name.endsWith(".jar")) {
                        println("Removing old plugin JAR: ${file.name}")
                        file.delete()
                    }
                }
            }
        }
        
        doLast {
            println("Plugin JAR copied to D:/dev-server-1.21.4-towny/plugins")
        }
    }
    
    // Task to build and copy in one command
    register("deployToServer") {
        dependsOn("clean", "copyToServer")
        group = "deployment"
        description = "Cleans, builds the plugin and copies it to the Paper server plugins folder"
        
        doLast {
            println("Plugin built and deployed to Paper server!")
        }
    }
}
