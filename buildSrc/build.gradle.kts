gradle.beforeProject{
    println("*** buildSrc beforeProject  $this" )
}

gradle.afterProject{
    println("*** buildSrc afterProject  $this" )
}
project.beforeEvaluate {
    println("*** buildSrc beforeEvaluate  $this" )
}
project.afterEvaluate {
    println("*** buildSrc afterEvaluate  $this" )
}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    kotlin("jvm") version "1.5.30"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}



repositories {
    maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
    maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
    mavenCentral()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
    implementation("com.android.tools.build:gradle:7.0.0")

    /* Example Dependency *//* Depend on the kotlin plugin, since we want to access it in our plugin */
    implementation("org.javassist:javassist:3.28.0-GA")/* Depend on the default Gradle API's since we want to build a custom plugin */
    implementation(gradleApi())
    implementation(localGroovy())
}

gradlePlugin {
    plugins {
        create("custom") {
            id = "CustomPlugin"
            implementationClass = "com.example.buildsrc.CustomPlugin"
        }
    }
}
