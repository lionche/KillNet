package com.che.killnet.wenet

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.NetworkUtils
import com.chaquo.python.Python
import com.che.killnet.MyApplication.Companion.context
import com.che.killnet.wenet.model.DeleteBean
import com.che.killnet.wenet.model.LoginPostBody


class WenetViewModel: ViewModel() {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //   检测网络

    val buttonState = MutableLiveData<String>()
//    lateinit var IpAddressByWifi: String
    var IpAddressByWifi = "1"


//    fun netCheck() {
////        获取 ConnectivityManager 的实例
//        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
////        使用此实例获取对应用当前默认网络的引用
////        val currentNetwork = connectivityManager.activeNetwork
////        通过对网络的引用，您的应用可以查询有关网络的信息
//
//
//        connectivityManager.registerDefaultNetworkCallback(object :
//            ConnectivityManager.NetworkCallback() {
////            override fun onAvailable(network: Network) {
////                Log.e("test123", "现在的网络是$")
////            }
////
////            override fun onLost(network : Network) {
////                Log.e("test123", "刚刚断开网络,刚才连接的是 " + network)
////            }
////
////            override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
////                Log.e("test123", "The default network changed capabilities: " + networkCapabilities)
////            }
//
//
//            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
//
//                IpAddressByWifi = NetworkUtils.getIpAddressByWifi()
//
//                Log.e("test123", "getIpAddressByWifi,$IpAddressByWifi")
//
////                IpAddressByWifi.showToast(context)
//                if("10.1" in IpAddressByWifi){
//                    buttonState.postValue("wifi_available")
//                }else{
//                    buttonState.postValue("wifi_not_available")
//                }
//            }
//        })
//
//
//    }


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
    /*
    利用cve-2020-0796蓝屏,执行python代码
     */
    private fun cve(inputString: String) {
        Log.d("cve", "ip to attack is $inputString")
        // 调用python代码
        val py = Python.getInstance()
        try {
            py.getModule("cve-2020-0796-crash").callAttr("killIp",inputString);
        } catch (e: Exception) {
            print(e.message)
        }
    }

    fun loginWenet() {

        //根据ip修改连接
//        var url =
//            "http://10.16.0.12:8081/?usermac=XX:XX:XX:XX:XX:XX&userip=MYIP&origurl=http://edge.microsoft.com/captiveportal/generate_204&nasip=10.100.0.1"
        var url =
            "http://10.16.0.21/?usermac=XX:XX:XX:XX:XX:XX&userip=MYIP&origurl=http://edge.microsoft.com/captiveportal/generate_204&nasip=10.100.0.1"
        url = url.replace("MYIP", IpAddressByWifi)

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