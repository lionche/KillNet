package com.che.killnet

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context




class MyApplication : Application() {
    //获取全局context
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context =applicationContext

    }

}