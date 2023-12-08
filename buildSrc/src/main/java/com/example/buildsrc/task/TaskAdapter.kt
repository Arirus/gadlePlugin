package com.example.buildsrc.task

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import com.example.buildsrc.javacheck.JavaCheckTask
import com.example.buildsrc.model.CustomConfigExtension
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.io.File
import java.util.Locale

// task适配器，在这里实现 task的注册
class TaskAdapter {

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

        //android 系统相关aar
//        extension.bootClasspath.forEach {
//            println("@@@ bootClasspath ${it}")
//        }
        //subproject 编译的classes.jar
        //R.jar
        //第三方依赖的jar
        //主project的 kotlin 编译出的 kotlin-class 文件夹
//        javaCompile.classpath.files.forEach {
//            println("@@@ javaCompile Classpath ${it}")
//        }

        // buildConfig.class
        // 当前项目的java文件编程成的class
//        javaCompile.destinationDirectory.asFileTree.files.forEach {
//            println("@@@ javaCompile destinationDirectory ${it}")
//        }

        //除了上面的 destinationDirectory 还有ap_generated_sources/debug/out 等 可以忽略这个文件夹
//        javaCompile.outputs.files.forEach {
//            println("@@@ javaCompile outputs ${it}")
//        }

        // 所有参与编译的 class 文件
        val classSource = project.files(
            extension.bootClasspath, javaCompile.classpath, javaCompile.destinationDirectory
        )

        //注册非/增量任务
        val task =
            when(configExtension.incremental){
                true ->{
                    project.tasks.create("demoInscrTask${variant.name.capitalized()}", DemoInscrTask::class.java){
                        this.classSource = classSource
                        this.diff = java.io.File(project.buildDir, "demoInscrTask.txt")
                    }
                }
                else->{
                    project.tasks.create("demoTask${variant.name.capitalized()}", DemoTask::class.java){
                        this.classSource = classSource
                        this.diff = File(project.buildDir,"demoInscrTask.txt")
                    }
                }
            }



        // 依赖 javaCompile
        task.mustRunAfter(javaCompile)
        task.dependsOn(javaCompile)

        // 依赖 assemble
        variant.assembleProvider.get().dependsOn(task)
    }

}