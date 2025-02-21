
buildscript {
    dependencies {
        classpath(libs.jetpack.navigation.safe.args.gradle.plugin)
        classpath(libs.hilt.gradle.plugin)
        classpath(libs.android.secrets.gradle.plugin)
    }
    repositories {
        google()
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
}