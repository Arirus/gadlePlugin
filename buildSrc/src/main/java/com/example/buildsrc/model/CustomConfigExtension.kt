package com.example.buildsrc.model

import org.gradle.api.Action
import java.io.File

open class CustomConfigExtension {
    // bool类型
    var enable = false
    var incremental = false
    // 文件类型
    var config: File? = null

    // 数组类型
    internal var items1: MutableList<String> = mutableListOf()
    // 自定义类型
    internal val customItem = CustomItem()
    // 自定义类型一定要用自定义方法上传
    fun customItem(action: Action<CustomItem>) {
        action.execute(customItem)
    }

    fun items(action:MutableList<String>) {
        items1.addAll(action)
    }

}

open class CustomItem {
    var name = ""
    var enable = false
}