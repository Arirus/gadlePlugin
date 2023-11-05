package com.example.buildsrc.javacheck.search

import javassist.CtClass

abstract class AbsSearch {
    enum class Type {
        Literally, Functional
    }

    abstract fun type():Type

    protected lateinit var searchPattern:String

    abstract fun doSearch(packageName:String, inputClass:CtClass)

}