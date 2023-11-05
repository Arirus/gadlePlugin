package com.example.buildsrc.model

import com.example.buildsrc.javacheck.JavaClassLoader
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

open class JavaCheckTask : DefaultTask() {

    @InputFiles
    var classSource: FileCollection? = null

    @TaskAction
    fun doCheck() {
        println("JavaCheckTask start")

        val classLoader = JavaClassLoader()

        val result = classLoader.loadClass(classSource)

        println("*** ${result.size}")
        result.forEach{
            println("***** ${it.key}")
            it.value.forEach {
                println("******* ${it.name}")
            }
        }


    }

}