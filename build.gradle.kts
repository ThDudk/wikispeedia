plugins {
    id("java")
}

group = "io.github.thdudk"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/OneGraphLib.main.jar"))
    implementation("org.apache.commons:commons-text:1.13.0")
    implementation("com.univocity:univocity-parsers:2.1.1")
}