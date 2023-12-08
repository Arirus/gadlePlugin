package com.example.buildsrc.model

import java.io.File

open class CustomConfigExtension {
    var enable = false
    var incremental = false
    var config: File? = null
}