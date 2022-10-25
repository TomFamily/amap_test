package com.example.amap_test

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import skin.support.SkinCompatManager
import skin.support.app.SkinAppCompatViewInflater
import skin.support.app.SkinCardViewInflater
import skin.support.constraint.app.SkinConstraintViewInflater
import skin.support.design.app.SkinMaterialViewInflater

/**
 * 换肤工具类
 *
 * 外部调用方式：
 * 1，插件换肤
 * SkinProxy.loadSkin(false, "night.skin",
{ preSkinName ->
}, { preSkinName, newApplySkinName ->
}, { errMsg ->
}
)
2，应用内换肤
SkinProxy.loadSkin(false, "night"）
 */
interface SkinApi {
    /**
     * 初始化
     */
    fun init(buildIn: Boolean, application: Application)

    /**
     * @param buildIn 是否应用内换肤
     * @param targetSkinName 目标皮肤名称
     * @param startBlock 开始换肤时回调
     * @param successBlock 换肤成功时回调
     * @param failedBlock 换肤失败时回调
     */
    fun loadSkin(
        buildIn: Boolean,
        targetSkinName: String,
        startBlock: ((preSkinName: String) -> Unit)? = null,
        successBlock: ((preSkinName: String, applySkinName: String) -> Unit)? = null,
        failedBlock: ((errMsg: String) -> Unit)? = null,
    )

    fun loadSkin(buildIn: Boolean, targetSkinName: String)
}

object SkinProxy : SkinApi {
    var mSkinMode = SkinMode.DAY
    private const val DATA_NAME = "THEME"

    enum class SkinMode {
        DAY,
        NIGHT
    }

    fun updateSkinMode(mode: SkinMode) {
        mSkinMode = mode
        if (mode == SkinMode.DAY) {
            SkinCompatManager.getInstance().restoreDefaultTheme()
        } else {
            loadSkin(true, "night")
        }
    }

    override fun init(buildIn: Boolean, application: Application) {
        mSkinMode = if (readDarkThemeState(application)) {
            SkinMode.NIGHT
        } else {
            SkinMode.DAY
        }
        SkinCompatManager.withoutActivity(application)
            .addInflater(SkinAppCompatViewInflater()) // 基础控件换肤初始化
            .addInflater(SkinMaterialViewInflater()) // material design 控件换肤初始化[可选]
            .addInflater(SkinConstraintViewInflater()) // ConstraintLayout 控件换肤初始化[可选]
            .addInflater(SkinCardViewInflater()) // CardView v7 控件换肤初始化[可选]
            .setSkinStatusBarColorEnable(false) // 关闭状态栏换肤，默认打开[可选]
            .setSkinWindowBackgroundEnable(false) // 关闭windowBackground换肤，默认打开[可选]
            .loadSkin()
    }

    override fun loadSkin(
        buildIn: Boolean,
        targetSkinName: String,
        startBlock: ((preSkinName: String) -> Unit)?,
        successBlock: ((preSkinName: String, applySkinName: String) -> Unit)?,
        failedBlock: ((errMsg: String) -> Unit)?,
    ) {
        SkinCompatManager.getInstance()
            .doLoadSkin(buildIn, targetSkinName, startBlock, { preSkinName, newApplySkinName ->
                successBlock?.invoke(preSkinName, newApplySkinName)
            }, failedBlock)
    }

    override fun loadSkin(buildIn: Boolean, targetSkinName: String) {
        SkinCompatManager.getInstance()
            .doLoadSkin(buildIn, targetSkinName, null, null)
    }

    fun isDay(): Boolean {
        return mSkinMode == SkinMode.DAY
    }

    private fun SkinCompatManager.doLoadSkin(
        buildIn: Boolean,
        targetSkinName: String,
        startBlock: ((preSkinName: String) -> Unit)? = null,
        successBlock: ((preSkinName: String, newApplySkinName: String) -> Unit)? = null,
        failedBlock: ((errMsg: String) -> Unit)? = null,
    ) {
        var param = SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN
        if (!buildIn) {
            param = SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS
        }
        loadSkin(
            targetSkinName,
            object : SkinCompatManager.SkinLoaderListener {
                override fun onStart() {
                    Log.d("skin", "onStart")
                    startBlock?.invoke(targetSkinName)
                }

                override fun onSuccess() {
                    Log.d("skin", "onSuccess")
                    successBlock?.invoke("", targetSkinName)
                }

                override fun onFailed(errMsg: String?) {
                    Log.d("skin", "onFailed")
                    failedBlock?.invoke(errMsg ?: "")
                }
            },
            param
        )
    }

    /**
     * 写入内存数据
     * 将当前的深色模式状态保持到内存，下次启动 APP 以读取
     * @param context Context
     * @param state 深色模式状态，true 打开状态，false 关闭状态
     */
    fun writeDarkThemeState(context: Context, state: Boolean) {
        context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
            .edit { putBoolean("boolean_dark_theme_state", state) }
    }

    /**
     * 读取内存数据
     */
    private fun readDarkThemeState(context: Context): Boolean {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
            .getBoolean("boolean_dark_theme_state", false)
    }
}