package com.example.buildsrc.demo


import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File


// 实例task，有两个功能
// 1.扫描layout里面的xml 文件，将文件名记录下来。
// 2.扫描.class 文件，将文件名记录下来
open class DemoTask:DefaultTask() {

    @InputFiles
    var resSource:FileCollection? = null

    @OutputFile
    var resOutput: File? = null


    @TaskAction
    fun doAction(){

        println("start")

    }


}