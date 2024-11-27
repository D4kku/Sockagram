plugins {
    id("java")
    id("org.openjfx.javafxplugin") version("0.1.0")
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
    implementation ("io.github.palexdev:materialfx:11.17.0")

}


tasks.test {
    useJUnitPlatform()
}
