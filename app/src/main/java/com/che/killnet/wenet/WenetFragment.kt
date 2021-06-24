package com.che.killnet.wenet

import android.app.AlertDialog
import android.graphics.Color
import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat
import com.che.killnet.R
import com.che.killnet.base.BaseVMFragment
import com.che.killnet.databinding.FragmentWenetBinding
import com.che.killnet.utils.showToast
import com.che.killnet.wenet.model.SearchSessionsResponse


class WenetFragment : BaseVMFragment<FragmentWenetBinding, WenetViewModel>() {

    override fun onResume() {
        Log.d("test123", "onResume:检查网络 ")
        viewModel.netCheck()
        super.onResume()
    }

    override fun observerData() {
        binding.model = viewModel
        binding.lifecycleOwner = this

        viewModel.buttonState.observe(this,
                {
                    when (it) {
                        "2_devices" -> {
                            viewModel.searchDeviceLiveData.value = viewModel.authorization.value
                        }
                    }
                })

        viewModel.deviceLiveData.observe(this, { result ->
            val sessionsList = result.getOrNull()
            sessionsList?.let {
//                viewModel.deviceList = it as ArrayList<SearchSessionsResponse.Sessions>
                showDialog(it)
            } ?: let {
                Log.d("test123", "没有连接设备")
                "No Devices".showToast(requireContext())
            }
        })
        viewModel.loginLiveData.observe(this,
                { result ->


                    Log.d("test123", "$result")

                    result.getOrNull()?.apply {
                        Log.d("test123", "token:$token")
                        viewModel.authorization.value = token

                        viewModel.buttonState.value = "2_devices"

//                    if (this.statusCode == 200) {
//                        Log.d("test123", "登陆成功")
//                        "登陆成功啦".showToast(requireContext())
//                    } else {
//                        this.errorDescription.apply {
///*
//                            when (this.first()) {
//                                //pc already have 2 sessions
//                                'p' -> {
////                                    "已登陆2台设备".showToast(requireContext())
//                                    viewModel.buttonState.value = "2_devices"
//
//                                }
//                                //invalid username or password
//                                'i' -> {
//
//                                    "用户名或密码错误".showToast(requireContext())
//
//                                    viewModel.buttonState.value = "wrong_password"
//                                }
//
//                                //NAS no response
//                                'N' -> {
//                                    "是不是连错网了".showToast(requireContext())
//                                    viewModel.buttonState.value = "wifi_not_available"
//
//                                }
//                                //authentication rejected
//                                //删除了设备马上重新登陆会出现这个问题
//                                'a' -> {
////                                    "等会再登陆".showToast(requireContext())
//                                    sleep(800)
//                                    viewModel.loginWenet()
//
//                                }
//                                //Failed to login
//                                'F' -> {
//                                    "Failed to login".showToast(requireContext())
//                                    viewModel.buttonState.value = "wrong_password"
//
//                                }
//
//                            }
//*/
//
//                        }
//                        Log.d("test123", "登陆失败:原因:${this.errorDescription}")
//                    }
                    }
                })
        viewModel.deleteLiveData.observe(this,
                { result ->
                    result.getOrNull()?.let { Log.d("test123", "删除成功") }
                            ?: let {
                                Log.d(
                                        "test123",
                                        "设备码或者token错误"
                                )
                            }

                })
    }

    private fun showDialog(list: List<SearchSessionsResponse.Sessions>) {


        val loginDevices = list.map { it.deviceType }.toTypedArray()

        if (loginDevices.isNotEmpty()) {

        }
//
//        AlertDialog.Builder(requireContext()).apply {
//            setTitle("Select To Kill")
//            setCancelable(false)
//            setNeutralButton("Cancel") { dialog, which -> }
//            for (item in loginDevices) {
//                Log.e("test123", "showDialog: $item")
//            }
//            val selectDevices: MutableList<Int> = ArrayList()
//            setMultiChoiceItems(loginDevices, null) { dialog, which, isChecked ->
//                if (isChecked) {
//                    selectDevices.add(which)
//                } else {
//                    selectDevices.remove(which)
//                }
//            }
//            setPositiveButton("Kill") { dialog, which ->
//                for (deviceIndex in selectDevices) {
//                    viewModel.deleteDevice(
//                            viewModel.authorization.value!!, list.map { it.acct_unique_id }[deviceIndex]
//                    )
//                    Log.e("test123", "Select To Kill：${loginDevices[deviceIndex]}")
//                }
//            }
//            show()
//        }
        val selectDevices: MutableList<Int> = ArrayList()

        AlertDialog.Builder(requireContext(),R.style.UpdateDialogStyle)
                .setTitle("Select To Kill")
                .setCancelable(false)
                .setNeutralButton("Cancel") { dialog, which -> }
                .setMultiChoiceItems(loginDevices, null) { dialog, which, isChecked ->
                    if (isChecked) {
                        selectDevices.add(which)
                    } else {
                        selectDevices.remove(which)
                    }
                }
                .setPositiveButton("Kill") { dialog, which ->
                    for (deviceIndex in selectDevices) {
                        viewModel.deleteDevice(
                                viewModel.authorization.value!!, list.map { it.acct_unique_id }[deviceIndex]
                        )
                        Log.e("test123", "Select To Kill：${loginDevices[deviceIndex]}")
                    }
                }
                .show()
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(),R.color.teal_200))
//        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(),R.color.teal_200))


//    }


    }


    override fun getSubLayoutId() = R.layout.fragment_wenet
    override fun getSubVMClass() = WenetViewModel::class.java

}

