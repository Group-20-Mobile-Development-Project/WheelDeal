pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    // Remove the FAIL_ON_PROJECT_REPOS setting to allow buildscript repositories in module-level files.
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WheelDeal"
include(":app")
