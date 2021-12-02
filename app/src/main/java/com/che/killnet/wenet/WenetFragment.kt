package com.che.killnet.wenet

import android.app.AlertDialog
import android.util.Log
import com.che.killnet.R
import com.che.killnet.base.BaseVMFragment
import com.che.killnet.databinding.FragmentWenetBinding
import com.che.killnet.utils.showToast
import com.che.killnet.wenet.model.SearchSessionsResponse


class WenetFragment : BaseVMFragment<FragmentWenetBinding, WenetViewModel>() {


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

        viewModel.ifVulnerableLiveData.observe(this,
            { result ->
                Log.d("cve", "token:$result")
                if (result) {
                    showCrashDialog()
                } else {
                    showNoCrashDialog()

                }

            })
    }

    private fun showDialog(list: List<SearchSessionsResponse.Sessions>) {


        val loginDevices = list.map { it.deviceType }.toTypedArray()

//        if (loginDevices.isNotEmpty()) {
//
//        }
        val selectDevices: MutableList<Int> = ArrayList()

        AlertDialog.Builder(requireContext(), R.style.UpdateDialogStyle)
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


    }

    private fun showCrashDialog() {
        AlertDialog.Builder(requireContext(), R.style.UpdateDialogStyle)
            .setTitle("Vulnerable!")
            .setIcon(R.drawable.ic_icon)
            .setCancelable(false)
            .setNeutralButton("Cancel") { dialog, which -> }
            .setPositiveButton("Crash") { dialog, which ->
                Log.e("cve", "Select To crash")
                viewModel.crashTarget(viewModel.name.value!!)
            }
            .show()

    }


    private fun showNoCrashDialog(){

        AlertDialog.Builder(requireContext(),R.style.UpdateDialogStyle)
            .setTitle("Not Vulnerable!")
            .setIcon(R.drawable.ic_icon)
            .setCancelable(false)
//            .setNeutralButton("Cancel") { dialog, which -> }
            .setPositiveButton("Cancel") { dialog, which ->
                Log.e("cve", "No crash")
            }
            .show()

    }


    override fun getSubLayoutId() = R.layout.fragment_wenet
    override fun getSubVMClass() = WenetViewModel::class.java

}

