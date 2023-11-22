package com.example.buildsrc.demo

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import com.example.buildsrc.javacheck.JavaCheckTask
import com.example.buildsrc.model.CustomConfigExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import java.util.Locale

class OldTask {

    fun register(project:Project){
        // task 注册
        project.afterEvaluate {
            if (project.plugins.hasPlugin(AppPlugin::class.java)) {
                val appExtension = project.extensions.findByType(AppExtension::class.java)
                appExtension?.applicationVariants?.forEach {
                    insertTask(project, it, appExtension)
                }
            } else if (project.plugins.hasPlugin(LibraryPlugin::class.java)) {
                val libraryExtension = project.extensions.findByType(LibraryExtension::class.java)
                libraryExtension?.libraryVariants?.forEach {
                    insertTask(project, it, libraryExtension)
                }
            }
        }
    }


    private fun insertTask(project: Project, variant: BaseVariant, extension: BaseExtension) {
        val configExtension = project.extensions.findByType(CustomConfigExtension::class.java)
        if (configExtension?.enable != true) return
        println("@@@ start ${variant.name}")

        val javaCompile = variant.javaCompileProvider.get()

//        extension.bootClasspath.forEach {
//            println("@@@ bootClasspath ${it}")
//        }
//        javaCompile.classpath.files.forEach {
//            println("@@@ javaCompile Classpath ${it}")
//        }
//        javaCompile.destinationDirectory.asFileTree.files.forEach {
//            println("@@@ javaCompile destinationDirectory ${it}")
//        }
//        javaCompile.outputs.files.forEach {
//            println("@@@ javaCompile outputs ${it}")
//        }

        val classSource = project.files(
            extension.bootClasspath, javaCompile.classpath, javaCompile.destinationDirectory
        )

        val task = project.tasks.create("javaCheck${variant.name.toUpperCase(Locale.ROOT)}", JavaCheckTask::class.java) {
            this.classSource = classSource
        }

        // 依赖 javaCompile
        task.mustRunAfter(javaCompile)
        task.dependsOn(javaCompile)

        // 依赖 assemble
//        variant.assembleProvider.get().dependsOn(task)
    }

}