package com.example.amap_test

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.SkinAppCompatDelegateImpl
import com.amap.api.maps.AMap
import com.amap.api.maps.MapsInitializer
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.NaviSetting
import com.amap.api.services.core.ServiceSettings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mAMap: AMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAMapPolicy()
        setContentView(R.layout.activity_main)
        initAMap()
    }

    private fun initAMapPolicy() {
        ServiceSettings.updatePrivacyShow(BaseApplication.getAppContext(), true, true)
        ServiceSettings.updatePrivacyAgree(BaseApplication.getAppContext(), true)
        MapsInitializer.updatePrivacyShow(BaseApplication.getAppContext(), true, true)
        MapsInitializer.updatePrivacyAgree(BaseApplication.getAppContext(), true)
        NaviSetting.updatePrivacyShow(BaseApplication.getAppContext(), true, true)
        NaviSetting.updatePrivacyAgree(BaseApplication.getAppContext(), true)
    }

    private fun initAMap() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ), 100
        )
        main_navi_view.onCreate(null)
        // todo 在这里报空指针异常，把 下面的 getDelegate（） 方法注释调，就可以正常运行了。所以是不是因为与 skin-support库不兼容
        mAMap = main_navi_view.map
    }

    @NonNull
    override fun getDelegate(): AppCompatDelegate {
        return SkinAppCompatDelegateImpl.get(this, this)
    }
}