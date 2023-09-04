// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    alias(libs.plugins.androidLibrary) apply false
}

buildscript {
    repositories{
        google()
        mavenCentral()
        maven ( "https://jitpack.io" )
    }
    dependencies{
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
        classpath("com.google.gms:google-services:4.3.15")
    }

}
allprojects {
    repositories {
        google()
        maven ( "https://jitpack.io" )
        mavenCentral()
    }
}
true // Needed to make the Suppress annotation work for the plugins block