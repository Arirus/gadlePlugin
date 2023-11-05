package com.example.buildsrc.javacheck.search

import javassist.CtClass

class LiterSearch:AbsSearch() {

    override fun type() = Type.Literally

    override fun doSearch(packageName: String, inputClass: CtClass) {

    }
}