package com.ello.androidskeleton.animation

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.bumptech.glide.load.model.FileLoader
import com.ello.androidskeleton.databinding.ActivityAnimationBinding

class AnimationActivity : AppCompatActivity() {

    private lateinit var vb: ActivityAnimationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityAnimationBinding.inflate(layoutInflater).also { setContentView(it.root) }

        vb.root.post {
            val bgWidth = vb.progressBg.width

            ValueAnimator.ofFloat(0f, 100f).apply {
                duration = 20000
                addUpdateListener {
                    val value = it.animatedValue as Float
                    vb.tvProgress.text = String.format("%.2f%%", value)

                    val newWidth = (value / 100f * bgWidth).toInt()
                    vb.progress.layoutParams = vb.progress.layoutParams.apply {
                        width = newWidth
                    }

                    val set = ConstraintSet()
                    set.clone(vb.root)

                    if (newWidth > vb.tvProgress.width) {
                        set.connect(vb.tvProgress.id, ConstraintSet.END, vb.progress.id,ConstraintSet.END)
                        set.clear(vb.tvProgress.id, ConstraintSet.START)
                    }else {
                        set.connect(vb.tvProgress.id, ConstraintSet.START, vb.progress.id,ConstraintSet.END)
                        set.clear(vb.tvProgress.id, ConstraintSet.END)
                    }
                    set.applyTo(vb.root)

                }
                start()
            }
        }





    }
}