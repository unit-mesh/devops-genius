plugins {
    java
    application
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)

    alias(libs.plugins.shadow)
}

group = "cc.unitmesh"
version = "0.1.1"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://repo.spring.io/snapshot")
        name = "Spring Snapshots"
    }
    maven(url = uri("https://packages.jetbrains.team/maven/p/ktls/maven"))
}

dependencies {
    implementation(libs.codedb.checkout)
    implementation(libs.archguard.analyser.diffChanges)

    implementation("cc.unitmesh:cocoa-core:0.4.3")
    implementation("cc.unitmesh:git-differ:0.4.3")
    implementation("cc.unitmesh:code-splitter:0.4.3")
    implementation("cc.unitmesh:git-commit-message:0.4.3")

    implementation("cc.unitmesh:connection:0.4.3")
    implementation("cc.unitmesh:openai:0.4.3")

//    implementation("cc.unitmesh:sentence-transformers:0.4.3")
//    implementation("cc.unitmesh:store-elasticsearch:0.4.3")

    implementation("cc.unitmesh:document:0.4.3")

    implementation(libs.kaml)
    implementation(libs.github.api)
    implementation(libs.gitlab4j.api)

    implementation(libs.clikt)
    implementation(libs.rxjava3)

    implementation(libs.kotlin.stdlib)
    implementation(libs.serialization.json)
    implementation(libs.logging.slf4j.api)

    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.test.junit.engine)
}


application {
    mainClass.set("cc.unitmesh.genius.MainKt")
}

tasks {
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "cc.unitmesh.genius.MainKt"))
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}
