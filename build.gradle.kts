plugins {
    id("java")
    id("org.openjfx.javafxplugin") version("0.1.0")
    application
}

javafx {
    version = ("23.0.1")
    modules = ( mutableListOf("javafx.controls","javafx.fxml") )//kotlin build scripting ist ein ding i guess
}
group = "de.uulm.in.vs.grn.p2a"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "de.uulm.in.vs.grn.p3a.main"
}


tasks.test {
    useJUnitPlatform()
}