package com.ello.androidskeleton.activityParams

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.ello.androidskeleton.databinding.ActivityParamsBinding
import com.ello.base.ktx.finishWithResult
import com.ello.base.ktx.intentExtra

class ParamsActivity : AppCompatActivity() {

    class ParamActivityLauncher : ActivityResultContract<Bundle, Int>() {
        override fun createIntent(context: Context, input: Bundle): Intent {
            return Intent(context, ParamsActivity::class.java).apply { putExtras(input) }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Int {
            if (resultCode == RESULT_OK) {
                return intent?.getIntExtra("result", 0) ?: 0
            }
            return 0
        }

    }

    private lateinit var viewBinding: ActivityParamsBinding

    //类型为Int?，相当于intent.extras.get("intParam1")
    private val intParam1 by intentExtra<Int>("intParam1")

    //类型为Int?，相当于intent.extras.get("intParam2")，省略参数
    private val intParam2 by intentExtra<Int>()

    //类型为Int，相当于intent.extras.getInt("intParam3")
    private val intParam3 by intentExtra("intParam3", 0)

    //类型为Int，相当于intent.extras.getInt("intParam4")
    private val intParam4 by intentExtra(defaultValue = 0)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityParamsBinding.inflate(layoutInflater)

        actionBar?.title = "activity传递参数"

        setContentView(viewBinding.root)

        viewBinding.textView.text = "intParam1 = $intParam1\nintParam2 = $intParam2\nintParam3 = $intParam3\nintParam4 = $intParam4\n"

        viewBinding.btnBackWithParam.setOnClickListener {
            finishWithResult("result" to 2022)
        }

    }




}