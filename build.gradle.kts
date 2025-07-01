plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.palantir.git-version") version "3.0.0"
}

group = "com.example"
version = gitVersion()
description = "Async Dungeon Generator Plugin for Paper 1.21.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
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
            "description" to project.description,
            "apiVersion" to "1.21"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    
    runServer {
        minecraftVersion("1.21.1")
    }
    
    // Task to copy JAR to Paper server plugins folder
    register<Copy>("copyToServer") {
        dependsOn("jar")
        from("build/libs")
        into("D:/paper-1.21.7/plugins")
        include("*.jar")
        
        doLast {
            println("Plugin JAR copied to D:/paper-1.21.7/plugins")
        }
    }
    
    // Task to build and copy in one command
    register("deployToServer") {
        dependsOn("build", "copyToServer")
        group = "deployment"
        description = "Builds the plugin and copies it to the Paper server plugins folder"
        
        doLast {
            println("Plugin built and deployed to Paper server!")
        }
    }
}
