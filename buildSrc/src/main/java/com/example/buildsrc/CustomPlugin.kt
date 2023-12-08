package com.example.buildsrc

import com.example.buildsrc.task.TaskAdapter
import com.example.buildsrc.model.CustomConfigExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class CustomPlugin : Plugin<Project> {

    companion object {
        const val EX_NAME = "CustomConfig"
    }

    override fun apply(project: Project) {
        //创建名为 CustomConfig 的 extension
        project.extensions.create(EX_NAME, CustomConfigExtension::class.java)

        // transform 注册
//        project.extensions.findByType(AppExtension::class.java)?.registerTransform(DemoTransform())

        // task 注册
        TaskAdapter().register(project)
    }


}