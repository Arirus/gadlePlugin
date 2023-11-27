package com.example.buildsrc.demo

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.builder.utils.isValidZipEntryName
import com.example.buildsrc.utils.ClassUtils
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.util.jar.JarFile
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class DemoTransform : Transform() {

    override fun getName(): String = "DemoTans"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    //TransformManager.PROJECT_ONLY
    // R文件位置
    //jarInputs=[ImmutableJarInput{name=58275bbce80ef872bad7dc39a5c6019a1d46c6d9, file=/Users/didi/Desktop/self/gadlePlugin/app/build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/debug/R.jar, contentTypes=CLASSES, scopes=PROJECT, status=NOTCHANGED}]
    //folderInputs=[
    // kt 编译成 class文件位置
    // ImmutableDirectoryInput{name=bdf4c81b28d5ff34213857b290ab0b350f70d4bf, file=/Users/didi/Desktop/self/gadlePlugin/app/build/tmp/kotlin-classes/debug, contentTypes=CLASSES, scopes=PROJECT, changedFiles={}},
    // java 编译成的 class文件位置
    // ImmutableDirectoryInput{name=aa0425c0158a7d91dd6127dc3ef4fb712b7dc263, file=/Users/didi/Desktop/self/gadlePlugin/app/build/intermediates/javac/debug/classes, contentTypes=CLASSES, scopes=PROJECT, changedFiles={}}
    // ]


    //mutableSetOf(QualifiedContent.Scope.SUB_PROJECTS)
    //
    //jarInputs=[ImmutableJarInput{name=7199d5fadf1aabe4410e4ebbe669c19e69448bce, file=/Users/didi/Desktop/self/gadlePlugin/library/build/intermediates/runtime_library_classes_jar/debug/classes.jar, contentTypes=CLASSES, scopes=SUB_PROJECTS, status=NOTCHANGED}],
    //folderInputs=[]
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return mutableSetOf(QualifiedContent.Scope.PROJECT)
    }

    override fun isIncremental(): Boolean {
        return true;
    }

    override fun isCacheable(): Boolean {
        return true;
    }


    private fun transformFile(inputDir: File, inputFile: File, status: Status, outputDir: File) {
        val inputDirPath = inputDir.absolutePath
        val outputDirPath = outputDir.absolutePath

        val outputFile = File(inputFile.absolutePath.replace(inputDirPath, outputDirPath))

        when (status) {
            Status.NOTCHANGED -> {
                //do nothing
            }

            Status.ADDED, Status.CHANGED -> {
                FileUtils.touch(outputFile)
                val inputStream: InputStream = FileInputStream(inputFile)
                val fos = FileOutputStream(outputFile)
                val bytes = inputStream.readAllBytes()
                fos.write(bytes)
                fos.close()
                inputStream.close()
            }

            Status.REMOVED -> {
                takeIf { outputFile.exists() }?.apply {
                    outputFile.delete()
                }
            }
        }
    }

    private fun transformDir(inputDir: File, outputDir: File) {
        val inputDirPath = inputDir.absolutePath
        val outputDirPath = outputDir.absolutePath

        inputDir.walkTopDown().filter { it.isFile }.forEach { file ->
            val filePath: String = file.getAbsolutePath()
            val outputFile = File(filePath.replace(inputDirPath, outputDirPath))
            FileUtils.copyFile(file, outputFile)
            println("### copy ${file.absolutePath} ${outputFile.absolutePath}")
        }
    }

    private fun transformJar(inputJar: File, outputJar: File, status: Status?) {

        // Unzip.
        FileInputStream(inputJar).use { fis ->
            ZipInputStream(fis).use { zis ->
                // Zip.
                FileOutputStream(outputJar).use { fos ->
                    ZipOutputStream(fos).use { zos ->
                        var entry = zis.nextEntry
                        while (entry != null && isValidZipEntryName(entry)) {
                            if (!entry.isDirectory) {
                                zos.putNextEntry(ZipEntry(entry.name))
//                                if (classFilter(entry.name)) {
//                                    // Apply transform function.
//                                    applyFunction(zis, zos, function)
//                                } else {
                                zis.copyTo(zos)

                            }
                            entry = zis.nextEntry
                        }
                    }
                }
            }
        }


//        val file = JarFile(inputJar)
//
//        JarOutputStream(FileOutputStream(outputJar)).let {jos->
//            JarInputStream(FileInputStream(inputJar)).let {jis->
//                var entry= jis.nextJarEntry
//                val inputStream = file.getInputStream(entry)
//                while (entry!=null){
//                    val zipEntry = ZipEntry(entry.name)
//                    jos.putNextEntry(zipEntry)
//                    val sourceClassBytes = inputStream.readBytes()
//                    jos.write(sourceClassBytes)
//                    jos.closeEntry()
//                }
//            }
//        }

    }

    override fun transform(transformInvocation: TransformInvocation?) {
        transformInvocation?.inputs?.forEach {

            //获取增量
            val incremental = transformInvocation.isIncremental

            // 一、输入源为文件夹类型
            it.directoryInputs.forEach { directoryInput ->

                val dest = transformInvocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )

                if (incremental) {
                    directoryInput.changedFiles.forEach { file, status ->
                        transformFile(directoryInput.file, file, status, dest)
                    }
                } else {
                    transformDir(directoryInput.file, dest)
                }


            }

            // 二、输入源为 jar 包类型
            it.jarInputs.forEach { jarInput ->
                //2、构建输出路径 dest
                val jarOutput = transformInvocation.outputProvider.getContentLocation(
                    jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR
                )

                if (incremental) {
                    transformJar(jarInput.file, jarOutput, null)
                } else {
                    transformJar(jarInput.file, jarOutput, null)
                }

                //3、将 jar 包复制给 dest，dest 将会传递给下一个 Transform
//                FileUtils.copyFile(jarInput.file, jarOutput)
            }
        }

    }
}