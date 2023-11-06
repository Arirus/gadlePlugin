package com.example.buildsrc.javacheck

import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.file.FileCollection
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import java.util.jar.JarFile

open class JavaClassLoader() {

    var classPool: ClassPool = ClassPool.getDefault()

    init {
//        classSource.files.forEach { file ->
//            classPool.appendClassPath(file.absolutePath)
//        }
    }

    fun loadClass(classSource: FileCollection?): Map<String, List<CtClass>?> {
        val result = mutableMapOf<String, MutableList<CtClass>?>()

        classSource?.files?.forEach {
            loadClass(classPool, it, result)
        }

        return result
    }

    fun loadClass(
        classPool: ClassPool, classFile: File, container: MutableMap<String, MutableList<CtClass>?>
    ) {
        val stack = Stack<File>()
        stack.push(classFile)

        while (!stack.empty()) {
            val f = stack.pop()

            if (f.isDirectory) {
                f.listFiles()?.forEach {
                    stack.push(it)
                }
            } else if (f.name.endsWith(".class")) {
                var stream: FileInputStream? = null
                try {
                    stream = FileInputStream(f);
                    val ctClass = classPool.makeClass(stream)
                    var list = container[f.name]
                    if (list == null) {
                        list = mutableListOf(ctClass)
                        container[f.name] = list
                    } else {
                        list.add(ctClass)
                    }
                } catch (e: Exception) {
                } finally {
                    stream?.close()
                }

            } else if (f.name.endsWith(".jar")) {
                loadClass(classPool, JarFile(f), container)
            }
        }
    }

    private fun loadClass(
        classPool: ClassPool, jarFile: JarFile, container: MutableMap<String, MutableList<CtClass>?>
    ) {
        val entries = jarFile.entries()
        entries.toList().filter { jarEntry ->
            jarEntry.name.endsWith(".class")
        }.forEach { f ->
            var stream: InputStream? = null
            try {
                stream = jarFile.getInputStream(f);
                val ctClass = classPool.makeClass(stream)
                var list = container[f.name]
                if (list == null) {
                    list = mutableListOf(ctClass)
                    container[f.name] = list
                } else {
                    list.add(ctClass)
                }
            } catch (e: Exception) {
            } finally {
                stream?.close()
            }

        }
    }
}