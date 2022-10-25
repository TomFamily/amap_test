package com.example.amap_test

import android.app.Activity
import android.app.Application
import org.jetbrains.annotations.NotNull
import java.util.*

/**
 *Created by arno.yang
 *Created on 2022/10/24 17:44
 *PackageName com.example.amap_test
 */
class BaseApplication: Application() {

    init {
        appContext = this
    }

    companion object {
        private lateinit var appContext: Application

        @JvmStatic
        @NotNull
        fun getAppContext(): Application {
            return appContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        SkinProxy.init(true, applicationContext as Application)
    }
}