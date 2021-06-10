plugins {
    kotlin("multiplatform") version "1.4.31"
}

val keystoneDir = projectDir.resolve("src\\nativeInterop\\cinterop\\keystone")

group = "me.fjock"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
                when (preset) {
                    presets["mingwX64"] -> linkerOpts(
                        "-L${keystoneDir.resolve("lib")}",
                        "-lkeystone"
                    )
                }
            }
        }
        compilations["main"].cinterops {
            val keystone by creating {
                when (preset) {
                    //presets["macosX64"] -> includeDirs("/opt/local/include/SDL2", "/usr/local/include/SDL2")
                    //presets["linuxX64"] -> includeDirs("/usr/include", "/usr/include/x86_64-linux-gnu", "/usr/include/SDL2")
                    presets["mingwX64"] -> includeDirs(keystoneDir.resolve("include"))
                }
            }
        }
    }

    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
    }
}
