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
    }
}


