package com.example.buildsrc.javacheck.search

import javassist.CtClass

class FunctionSearch:AbsSearch() {
    override fun type(): Type = Type.Functional

    override fun doSearch(packageName: String, inputClass: CtClass) {

    }

}