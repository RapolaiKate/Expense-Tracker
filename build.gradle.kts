// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "2.3.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.2" apply false

}
buildscript {

    dependencies {
        classpath("com.android.tools.build:gradle:8.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.3.0")
        classpath("com.google.gms.google-services:com.google.gms.google-services.gradle.plugin:4.4.0")

    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
