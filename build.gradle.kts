buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Now Gradle can resolve this because repositories are defined here.
        classpath("com.google.gms:google-services:4.3.15")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
