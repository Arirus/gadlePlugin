plugins {
    `maven-publish`
    `kotlin-dsl`
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

sourceSets {
    main {
        // 使用对方的源码
        java.srcDir("../buildSrc/src/main/java")
        // 使用对方的资源配置
        resources.srcDir("../buildSrc/build/resources/main")
    }
}

repositories {
    maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
    maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
    mavenCentral()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Version.KotlinVersion}")
    implementation("com.android.tools.build:gradle:${Version.AgpVersion}")

    implementation("com.google.code.gson:gson:2.8.2")/* Example Dependency *//* Depend on the kotlin plugin, since we want to access it in our plugin */
    implementation("org.javassist:javassist:3.28.0-GA")/* Depend on the default Gradle API's since we want to build a custom plugin */


    implementation(gradleApi())
    implementation(localGroovy())
}


afterEvaluate {
    publishing {
        publications {
            create(PluginConfig.ArtifactId, MavenPublication::class.java) {
                groupId = PluginConfig.GroupId
                artifactId = PluginConfig.ArtifactId
                version = PluginConfig.Version
                from(components["java"])
            }
        }
        repositories {
            maven {
                url = uri("../repo")
            }
//            maven {
//                url = uri("http://www.baidu.com")
//                credentials {
//                    username = "Arirus.Wang"
//                    password = "111"
//                }
//            }
        }
    }
}


