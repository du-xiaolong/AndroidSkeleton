package com.ello.androidskeleton.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ello.androidskeleton.databinding.ActivityBroadCastBinding
import java.text.SimpleDateFormat
import java.util.*

class BroadCastActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityBroadCastBinding

    //系统时间改变，每分钟广播一次
    private lateinit var systemTimeReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding =
            ActivityBroadCastBinding.inflate(layoutInflater).also { setContentView(it.root) }


        val intentFilter = IntentFilter()
        listOf(
            Intent.ACTION_TIME_TICK,
            Intent.ACTION_AIRPLANE_MODE_CHANGED,
            Intent.ACTION_BATTERY_CHANGED,
            Intent.ACTION_CONFIGURATION_CHANGED,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_POWER_DISCONNECTED,
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_SCREEN_OFF
        ).forEach {
            intentFilter.addAction(it)
        }
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
//
                Log.d("ddxl", "action = ${intent?.action}")
                when(intent?.action) {
                    Intent.ACTION_BATTERY_CHANGED -> {
                        refreshBattery(intent.extras)
                    }
                    Intent.ACTION_AIRPLANE_MODE_CHANGED ->{
                        viewBinding.tvAirplane.text = if (intent.getBooleanExtra("state", false)) "已开启" else "未开启"
                    }
                    Intent.ACTION_TIME_TICK -> {
                        viewBinding.tvTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(System.currentTimeMillis()))
                    }

                }
            }

        }.also { systemTimeReceiver = it }, intentFilter)



        //先判断飞行模式状态，然后如果用户有修改，会在广播中回调
        viewBinding.tvAirplane.text = if (Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1) "已开启" else "未开启"

        //先设置时间，如果时间改变，会发出广播，再进行修改
        viewBinding.tvTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(System.currentTimeMillis()))

    }

    /**
     * 刷新电池状态，注册广播被动刷新
     *  因为电池参数刷新比较快，所以影响不大
     *  如果要主动查看，可以使用：
     *  BatteryManager manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
    manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
    manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
    manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
    manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);///当前电量百分比
    API >= 26 (8.0，O)
    manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);///充电状态
     */
    private fun refreshBattery(extras: Bundle?) {
        extras?:return
        with(viewBinding) {
            tvCurrentBattery.text = (extras.getInt(BatteryManager.EXTRA_LEVEL)).toString()
            tvTemperature.text = "${extras.getInt(BatteryManager.EXTRA_TEMPERATURE) / 10f}°C"
            tvVoltage.text = "${extras.getInt(BatteryManager.EXTRA_VOLTAGE) / 1000f}V"
            val healthMap = mapOf(
                BatteryManager.BATTERY_HEALTH_UNKNOWN to "未知",
                BatteryManager.BATTERY_HEALTH_GOOD to "良好",
                BatteryManager.BATTERY_HEALTH_OVERHEAT to "过热",
                BatteryManager.BATTERY_HEALTH_DEAD to "没电",
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE to "未知错误",
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE to "过压",
                BatteryManager.BATTERY_HEALTH_COLD to "温度过低"
            )

            val status =
                healthMap[extras.getInt(BatteryManager.EXTRA_HEALTH)] ?: "未知"


            val plugged = extras.getInt(BatteryManager.EXTRA_PLUGGED)

            tvStatus.text = status + "(" + (if (plugged == 0) "未充电" else "充电中") + ")"


        }

    }


    override fun onDestroy() {
        super.onDestroy()
        //释放资源
        if (this::systemTimeReceiver.isInitialized) {
            unregisterReceiver(systemTimeReceiver)
        }

    }
}