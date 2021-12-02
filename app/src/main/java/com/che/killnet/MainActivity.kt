package com.che.killnet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.che.killnet.databinding.ActivityMainBinding
import com.che.killnet.utils.showToast


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPython()
        initPython()
        // 初始化Python环境


    }


    private fun initPython() {
        if (!Python.isStarted()) {
            Log.d("python", "onCreate:python_no ")
            Python.start(AndroidPlatform(this))
//            "onCreate:python_no ".showToast(this)

        } else {
            Log.d("python", "onCreate:python_yes ")
//            "onCreate:python_yes ".showToast(this)

        }
    }


}