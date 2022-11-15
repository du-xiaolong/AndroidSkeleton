package com.ello.androidskeleton.fragmentParams

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ello.androidskeleton.R
import com.ello.androidskeleton.databinding.FragmentParamsBinding
import com.ello.base.ktx.argument
import com.ello.base.ktx.finishWithResult


class ParamsFragment : Fragment() {

    private var param1 by argument<Int>()
    private var param2 by argument(defaultValue = 0)
    private var param3 by argument<Int>("param3")
    private var param4 by argument("param4", defaultValue = 0)

    private lateinit var viewBinding:FragmentParamsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentParamsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.tv.text = "Fragment Params :\n param1 = $param1 \n param2 = $param2 \n param3 = $param3 \n param4 = $param4 \n"

        viewBinding.btnBack.setOnClickListener {
            finishWithResult("result" to 2023)
        }
    }

    companion object {

        fun newInstance(param1: Int?, param2: Int, param3: Int?, param4: Int) =
            ParamsFragment().apply {
                this.param1 = param1
                this.param2 = param2
                this.param3 = param3
                this.param4 = param4
            }
    }
}