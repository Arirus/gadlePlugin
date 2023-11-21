package com.example.buildsrc.javacheck

import com.example.buildsrc.javacheck.search.AbsSearch
import com.example.buildsrc.javacheck.search.FunctionSearch
import com.example.buildsrc.javacheck.search.LiterSearch
import com.example.buildsrc.javacheck.search.RuleDetail
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

open class JavaCheckTask : DefaultTask() {

    @InputFiles
    var classSource: FileCollection? = null

    private var rules: RuleDetail? = null

    private val searchList = listOf<AbsSearch>(
        LiterSearch(), FunctionSearch()
    )

    @TaskAction
    fun doCheck() {
        println("JavaCheckTask start")

        val classLoader = JavaClassLoader()

        val result = classLoader.loadClass(classSource)

        println("*** ${result.size}")
        result.forEach {
            println("***** ${it.key} ${it.value?.size}")
        }


    }

}