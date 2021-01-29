package com.kk.hardware.wifidemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kk.hardware.wifi.IDeviceChangeListener
import com.kk.hardware.wifi.armlet.Armlet
import com.kk.hardware.wifi.armlet.HeartLowerCommandConfig
import com.kk.hardware.wifi.armlet.command.BindUserCommand
import com.kk.hardware.wifi.armlet.command.HeartRateEntity
import com.kk.hardware.wifidemo.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.rvList.adapter = mAdapter
        var currentUnit = 0
        mBinding.btnUnitSwitch.setOnClickListener {
            if (currentUnit > 6) currentUnit = 0
            mBinding.btnUnitSwitch.text = "单元切换：$currentUnit"
            Armlet.heartLowerConfig =
                HeartLowerCommandConfig(currentUnit++, 50, 0, 1, System.currentTimeMillis())
        }
        Armlet.setDeviceChangeListener(object : IDeviceChangeListener<HeartRateEntity> {
            override fun onAdded(device: HeartRateEntity) {
                mAdapter.addData(device)
            }

            override fun onChange(device: HeartRateEntity) {
                val index = mAdapter.data.indexOfFirst { it.deviceId == device.deviceId }
                if (index == -1) {
                    mAdapter.addData(device)
                } else {
                    mAdapter.setData(index, device)
                }
            }

            override fun onRemoved(device: HeartRateEntity) {
                mAdapter.data.firstOrNull { it.deviceId == device.deviceId }
                    ?.let { mAdapter.remove(it) }
            }
        })
        lifecycleScope.launch {
            Armlet.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Armlet.stop()
    }

    private val mAdapter = object :
        BaseQuickAdapter<HeartRateEntity, BaseViewHolder>(android.R.layout.simple_list_item_1) {
        init {
            setOnItemClickListener { _, _, position ->
                val device = getItemOrNull(position) ?: return@setOnItemClickListener
                if (device.userId > 0) {
                    Armlet.unbindDeviceByDeviceId(device.deviceId)
                    ToastUtils.showShort("解绑设备")
                } else {
                    val bind = BindUserCommand(position + 1, 172, 63f, 25, 1, 60, 500, 0, 1)
                    Armlet.bindDevice(device, bind)
                    ToastUtils.showShort("绑定设备")
                }
            }
        }

        override fun convert(holder: BaseViewHolder, item: HeartRateEntity) {
            SpanUtils.with(holder.getView(android.R.id.text1))
                .appendLine("${item.deviceId}")
                .appendLine("  -bindId：${item.userId}")
                .setFontSize(12, true).setForegroundColor(0xFF888888.toInt())
                .appendLine("  -心率：${item.heart}")
                .setFontSize(12, true).setForegroundColor(0xFF888888.toInt())
                .appendLine("  -消耗：${item.kcal}")
                .setFontSize(12, true).setForegroundColor(0xFF888888.toInt())
                .append("  -强度：${item.results[9]}")
                .setFontSize(12, true).setForegroundColor(0xFF888888.toInt())
                .create()
        }
    }
}