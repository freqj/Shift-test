plugins {
    id("java")
    id("application")
}

application {
    mainClass.set("com.example.Main")
}

group = "com.example"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}



tasks.test {
    useJUnitPlatform()
}
tasks.jar{
    manifest {
        attributes["Main-Class"] = "com.example.Main"
    }
}