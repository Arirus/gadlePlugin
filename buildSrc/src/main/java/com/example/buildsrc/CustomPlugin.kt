package com.example.buildsrc

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import com.example.buildsrc.model.CustomConfigExtension
import com.example.buildsrc.javacheck.JavaCheckTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import java.util.Locale

class CustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        //创建名为 CustomConfig 的 extension
        project.extensions.create("CustomConfig", CustomConfigExtension::class.java)

        project.afterEvaluate {

            if (project.plugins.hasPlugin(AppPlugin::class.java)) {
                val appExtension = project.extensions.findByType<AppExtension>()
                appExtension?.applicationVariants?.forEach {
                    insertTask(project, it, appExtension)
                }
            } else if (project.plugins.hasPlugin(LibraryPlugin::class.java)) {
                val libraryExtension = project.extensions.findByType<LibraryExtension>()
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