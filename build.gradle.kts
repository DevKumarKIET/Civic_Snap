buildscript {
    val kotlin_version by extra("1.9.23")
    dependencies {
        classpath("com.google.gms:google-services:4.4.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.0")
    }
    repositories {
        mavenCentral()
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
}