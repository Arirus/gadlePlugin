package com.example.buildsrc

import com.android.build.gradle.AppExtension
import com.example.buildsrc.demo.DemoTask
import com.example.buildsrc.demo.DemoTransform
import com.example.buildsrc.model.CustomConfigExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class CustomPlugin : Plugin<Project> {

    companion object {
        const val EX_NAME = "CustomConfig"
    }

    override fun apply(project: Project) {
        //创建名为 CustomConfig 的 extension
        project.extensions.create(EX_NAME, CustomConfigExtension::class.java)

        // transform 注册
        project.extensions.findByType(AppExtension::class.java)?.registerTransform(DemoTransform())

        // task 注册
//        OldTask().register(project)
//        project.afterEvaluate {
//            project.extensions.findByType(AppExtension::class.java)?.applicationVariants?.forEach {
//
//                val resourceTask = it.mergeResourcesProvider.get()
//                val compileTask = it.javaCompileProvider.get()
////                val resourceTask =project.tasks.findByName("merge${it.name.capitalize()}Resources")
//
////                resourceTask.outputDir.get().files().files.forEach {
////                    println(" "+ it.absolutePath)
////                }
////                resourceTask.outputDir.get().asFileTree.filter { it.absolutePath.contains("/layout") }.files
//
//
//                val task =
//                    project.tasks.create("demoTask${it.name.capitalize()}", DemoTask::class.java)
//                task.resSource =
//                    it.allRawAndroidResources.asFileTree.matching{include("**/layout/**") }
//                task.resOutput = File(project.buildDir.path + "/${it.name}_demo_task/xml_scan.txt")
//
//                task.classSource = project.files(
//                    it.getCompileClasspath(null),
//                    compileTask.destinationDirectory.get().asFileTree
//                )
//                task.classOutput =
//                    File(project.buildDir.path + "/${it.name}_demo_task/class_scan.txt")
//
//                task.dependsOn(resourceTask, compileTask)
//
//            }
//        }
    }


}