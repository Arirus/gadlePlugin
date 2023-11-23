package com.example.buildsrc.demo

import com.android.build.api.transform.Context
import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipInputStream


class DemoTransform:Transform() {

    override fun getName(): String = "DemoTans"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false;
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        transformInvocation?.inputs?.forEach {
            // 一、输入源为文件夹类型
            it.directoryInputs.forEach {directoryInput->
                //1、TODO 针对文件夹进行字节码操作，这个地方我们就可以做一些狸猫换太子，偷天换日的事情了
                //先对字节码进行修改，在复制给 dest

                //2、构建输出路径 dest
                val dest = transformInvocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                //3、将文件夹复制给 dest ，dest 将会传递给下一个 Transform
                FileUtils.copyDirectory(directoryInput.file,dest)
            }

            // 二、输入源为 jar 包类型
            it.jarInputs.forEach { jarInput->
                //2、构建输出路径 dest
                val jarOutput = transformInvocation.outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                try {
                    FileInputStream(jarInput.file).use {fis->
                        JarInputStream(fis).use {jis->

                            FileOutputStream(jarOutput).use {fos->
                                JarOutputStream(fos).use {jos->
                                    jis.nextJarEntry
                                }
                            }

                        }
                    }
                }catch (e:Exception){

                }


                //3、将 jar 包复制给 dest，dest 将会传递给下一个 Transform
                FileUtils.copyFile(jarInput.file,jarOutput)
            }
        }

    }
}