package com.ello.basequickadapter4.normalWithDataBinding

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import com.ello.basequickadapter4.R
import com.ello.basequickadapter4.databinding.ItemNormalBinding
import com.ello.basequickadapter4.normal.NormalModel

/**
 * @author dxl
 * @date 2022-12-05  周一
 */
class NormalListDbAdapter : BaseQuickAdapter<NormalModel, DataBindingHolder<ItemNormalBinding>>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemNormalBinding> {
        return DataBindingHolder(R.layout.item_normal, parent)
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemNormalBinding>,
        position: Int,
        item: NormalModel?
    ) {
        holder.binding.apply {
            root.setBackgroundColor(item?.color!!)
            tv.text = item.text
        }
    }
}