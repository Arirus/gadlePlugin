package com.example.plugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import xx.xxx.xxx.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("Arirus", "onCreate: ")
    }
}