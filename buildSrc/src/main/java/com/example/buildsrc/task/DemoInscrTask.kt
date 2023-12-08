package com.example.buildsrc.task

import com.android.build.gradle.internal.profile.AnalyticsService
import com.android.build.gradle.internal.tasks.NewIncrementalTask
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.provider.DefaultProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor
import org.gradle.workers.internal.DefaultWorkerExecutor
import java.io.File

open class DemoInscrTask:DefaultTask() {

    @InputFiles
    @Incremental
    var classSource: FileCollection? = null

    @OutputFile
    var diff: File? = null

    @TaskAction
    fun doCheck(inputChanges:InputChanges) {
        println("DemoInscrTask")
        println("@@@ ${inputChanges.isIncremental}")
        classSource?.let {
            inputChanges.getFileChanges(it).forEach {
                println("@@@ ${it.file.name} ${it.changeType.name}")
            }
        }

    }
}