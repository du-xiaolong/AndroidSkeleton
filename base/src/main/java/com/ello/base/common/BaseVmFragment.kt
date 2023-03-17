package com.ello.base.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.ello.base.R
import com.ello.base.utils.inflateBindingWithGeneric
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import java.lang.reflect.ParameterizedType

/**
 * fragment基类，使用viewBinding
 * @author dxl
 */
abstract class BaseVmFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment() {

    lateinit var viewModel: VM
    lateinit var vb: VB

    private var loadingDialog: LoadingPopupView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        initView()
        initData()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        vb = inflateBindingWithGeneric(inflater, container, false)
        return vb.root
    }

    open fun initData() {

    }

    open fun initView() {

    }

    open fun observe() {
        viewModel.dialogStatus.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                dismissLoading()
            }
        }

        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            showLoading(it ?: "加载中")
        }
    }

    open fun initViewModel() {
        val type =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
        viewModel = ViewModelProvider(this).get(type)
    }

    fun showLoading(text: String = "加载中", dismissOnBack: Boolean = false) {
        if (loadingDialog == null) {
            loadingDialog = XPopup.Builder(requireContext())
                .dismissOnBackPressed(dismissOnBack)
                .asLoading(text, R.layout.layout_loading_dialog)
        }
        loadingDialog?.setTitle(text)?.show()
    }

    fun dismissLoading() {
        loadingDialog?.dismiss()
    }

    val ctx: Context
        get() = requireContext()

}