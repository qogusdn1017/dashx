import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.github.johnrengelman.shadow")

    dependencies {
        implementation(kotlin("stdlib"))
        //    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.properties["coroutinesVersion"]}")
    }

    tasks {
        processResources {
            filesMatching("**/*.yml") {
                expand(project.properties)
            }
            filteringCharset = "UTF-8"
        }
    }
}

project(":${rootProject.name}-paper") {
    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
//        maven("https://maven.enginehub.org/repo/")
    }

    @Suppress("GradlePackageUpdate")
    dependencies {
        compileOnly("io.papermc.paper:paper-api:${project.properties["mcVersion"]}-R0.1-SNAPSHOT")
        compileOnly("io.github.monun:tap-api:${project.properties["tapVersion"]}")
        compileOnly("io.github.monun:kommand-api:${project.properties["kommandVersion"]}")
        //    compileOnly("io.github.monun:invfx-api:${project.properties["invfxVersion"]}")
        //    compileOnly("io.github.monun:heartbeat-coroutines:${project.properties["hbCoroutinesVersion"]}")
        //    compileOnly("com.sk89q.worldedit:worldedit-bukkit:${"worldeditVersion"}")
    }

    tasks {
        shadowJar {
            archiveBaseName.set(project.name.removeSuffix("-paper"))
            archiveClassifier.set("")
            archiveVersion.set("")
        }

        create<Copy>("paperJar") {
            from(shadowJar)
            val paperPlugins = File(rootDir, ".server/plugins/")
            into(if (File(paperPlugins, "${project.name}.jar").exists()) File(paperPlugins, "update") else paperPlugins)
        }
    }
}

project(":${rootProject.name}-bungee") {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    dependencies {
        implementation("net.md-5:bungeecord-api:${project.properties["mcVersion"].toString().removeRange(4, 6)}-R0.1-SNAPSHOT")
    }

    tasks {
        shadowJar {
            archiveBaseName.set(project.name.removeSuffix("-bungee"))
            archiveClassifier.set("")
            archiveVersion.set("")
        }

        create<Copy>("bungeeJar") {
            from(shadowJar)
            val bungeePlugins = File(rootDir, ".bungee/plugins/")
            into(if (File(bungeePlugins, "${project.name}.jar").exists()) File(bungeePlugins, "update") else bungeePlugins)
        }
    }
}