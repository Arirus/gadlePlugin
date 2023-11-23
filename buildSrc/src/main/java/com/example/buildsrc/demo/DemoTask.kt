package com.example.buildsrc.demo

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import groovy.util.Node
import groovy.xml.XmlParser
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileWriter
import java.util.Enumeration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.jar.JarEntry
import java.util.jar.JarFile

// 实例task，有两个功能
// 1.扫描layout里面的xml 文件，将文件名记录下来。
// 2.扫描.class 文件，将文件名记录下来
open class DemoTask : DefaultTask() {

    @InputFiles
    var resSource: FileCollection? = null

    @OutputFile
    var resOutput: File? = null


    @InputFiles
    var classSource: FileCollection? = null

    @OutputFile
    var classOutput: File? = null

    private val executor = Executors.newCachedThreadPool()
    private val fileLock: Lock = ReentrantLock()
    private val gson = Gson()
    private val xmlPraser = XmlParser()


    private fun flatXml(file: File, node: Node) {
        val content = JsonObject()
        content.addProperty("File Name",file.name)
        content.add("Strcut",doFlatNode(node))
        fileLock.lock()
        val writer = FileWriter(resOutput,true)
        writer.appendLine(gson.toJson(content))
        writer.appendLine("---")
        writer.flush()
        writer.close()
        fileLock.unlock()
    }

    private fun doFlatNode(node: Node):JsonElement{
        if (node.children().isEmpty()) {
            val result = JsonArray();
            result.add(node.name().toString());
            return result
        } else{
            val result = JsonObject();
            val arrays = JsonArray();
            node.children().forEach {
                arrays.add(doFlatNode(it as Node))
            }
            result.add(node.name().toString(),arrays)
            return result
        }
    }

    private fun flatClass(file: File){
        if (file.name.endsWith(".jar")) {
            flatJar(JarFile(file))
        } else if (file.name.endsWith(".class")) {
            val writer = FileWriter(classOutput, true)
            writer.appendLine(file.getName())
            writer.appendLine("---")
            writer.flush()
            writer.close()
        }
    }

    private fun flatJar(jar: JarFile){
        val entries: Enumeration<JarEntry> = jar.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.getName().endsWith(".class")) {
                val writer = FileWriter(classOutput,true)
                writer.appendLine(entry.getName())
                writer.appendLine("---")
                writer.flush()
                writer.close()
            }
        }
    }


    @TaskAction
    fun doAction() {

        println("---start")

        takeIf { resOutput?.exists() == true }?.let { resOutput?.delete() }
        resOutput?.createNewFile()

        takeIf { classOutput?.exists() == true }?.let { classOutput?.delete() }
        classOutput?.createNewFile()


        resSource?.asFileTree?.files?.forEach {
            executor.submit {
                flatXml(it,XmlParser().parse(it))
            }
        }

        classSource?.asFileTree?.files?.forEach {
            executor.submit {
                flatClass(it)
            }
        }


        // 关闭线程池，不再接受新的任务
        executor.shutdown()

        try {
            // 等待线程池中的任务全部完成，最多等待指定的时间（这里设置为1小时）
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                // 在超时后仍有任务未完成，可以选择继续等待或做其他处理
                // 例如，可以调用executorService.shutdownNow()来中断所有任务并立即退出
            }
        } catch (e: InterruptedException) {
            // 在等待过程中发生中断异常
            // 可以选择继续等待或做其他处理
        }

    }


}