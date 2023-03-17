package com.ello.base.common

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.ello.base.R
import com.ello.base.ktx.lllog
import com.ello.base.utils.inflateBindingWithGeneric
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import java.lang.reflect.ParameterizedType

/**
 * activity基类，使用viewBinding
 * @author dxl
 */
abstract class BaseVmActivity<VM : BaseViewModel, VB : ViewBinding> :
    AppCompatActivity() {

    lateinit var viewModel: VM
    lateinit var vb: VB

    private var loadingDialog: LoadingPopupView? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lllog(this.javaClass.simpleName, "页面")
        beforeInit()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //获取泛型的类
        val type =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
        viewModel = ViewModelProvider(this)[type]

        vb = inflateBindingWithGeneric(layoutInflater)
        setContentView(vb.root)

        observe()
        init(savedInstanceState)
    }


    open fun beforeInit() {

    }

    open fun init(savedInstanceState: Bundle?) {

    }

    open fun observe() {
        viewModel.dialogStatus.observe(this) {
            if (it)
                showLoading()
            else
                dismissLoading()
        }

        viewModel.progressLiveData.observe(this) {
            showLoading(it ?: "加载中")
        }
    }


    fun showLoading(text: String = "加载中", dismissOnBack: Boolean = false) {
        if (loadingDialog == null) {
            loadingDialog = XPopup.Builder(this).dismissOnBackPressed(dismissOnBack)
                .dismissOnTouchOutside(false)
                .asLoading(text, R.layout.layout_loading_dialog)
        }
        loadingDialog?.setTitle(text)?.show()
    }

    fun dismissLoading() {
        loadingDialog?.smartDismiss()
    }


}