package com.example.buildsrc.task

import com.example.buildsrc.javacheck.JavaClassLoader
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class DemoTask:DefaultTask() {

    @InputFiles
    var classSource: FileCollection? = null

    @OutputFile
    var diff: File? = null


    @TaskAction
    fun doCheck() {
        println("DemoTask")

        Thread.sleep(1000L*3)
        println(classSource?.asFileTree?.files?.size)



    }

}