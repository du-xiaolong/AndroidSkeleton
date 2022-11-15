package com.ello.androidskeleton.fragmentParams

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.ello.androidskeleton.R
import com.ello.androidskeleton.databinding.ActivityParamFragmentBinding
import com.ello.base.ktx.argument
import com.ello.base.ktx.intentExtra

class ParamFragmentActivity : AppCompatActivity() {

    class ParamsFragmentLauncher : ActivityResultContract<Bundle, Int>() {

        override fun createIntent(context: Context, input: Bundle): Intent {
            return Intent(context, ParamFragmentActivity::class.java).apply {
                putExtras(input)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Int {
            if (resultCode == RESULT_OK) {
                return intent?.getIntExtra("result", 0) ?: 0
            }
            return 0
        }

    }


    private var param1 by intentExtra<Int>()
    private var param2 by intentExtra(defaultValue = 0)
    private var param3 by intentExtra<Int>("param3")
    private var param4 by intentExtra("param4", defaultValue = 0)


    private lateinit var viewBinding: ActivityParamFragmentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding =
            ActivityParamFragmentBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        supportFragmentManager.beginTransaction()
            .add(R.id.flContainer, ParamsFragment.newInstance(param1, param2, param3, param4))
            .commitAllowingStateLoss()


    }

}