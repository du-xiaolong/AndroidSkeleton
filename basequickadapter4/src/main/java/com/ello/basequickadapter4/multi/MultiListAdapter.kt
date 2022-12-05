package com.ello.basequickadapter4.multi

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.ello.basequickadapter4.R
import com.ello.basequickadapter4.databinding.ItemOthersBinding
import com.ello.basequickadapter4.databinding.ItemRedBinding
import com.ello.basequickadapter4.normal.NormalModel

/**
 * @author dxl
 * @date 2022-12-05  周一
 */
class MultiListAdapter : BaseMultiItemAdapter<NormalModel>() {

    companion object {
        const val TYPE_RED = 0
        const val TYPE_OTHERS = 1
    }


    init {
        addItemType(
            TYPE_RED,
            object : OnMultiItemAdapterListener<NormalModel, DataBindingHolder<ItemRedBinding>> {

                override fun onBind(
                    holder: DataBindingHolder<ItemRedBinding>,
                    position: Int,
                    item: NormalModel?
                ) {
                    holder.binding.root.setBackgroundColor(Color.RED)
                    holder.binding.tv.text = item?.text
                }

                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): DataBindingHolder<ItemRedBinding> {
                    return DataBindingHolder(R.layout.item_red, parent)
                }

            })
            .addItemType(
                TYPE_OTHERS,
                object :
                    OnMultiItemAdapterListener<NormalModel, DataBindingHolder<ItemOthersBinding>> {
                    override fun onBind(
                        holder: DataBindingHolder<ItemOthersBinding>,
                        position: Int,
                        item: NormalModel?
                    ) {
                        holder.binding.tv.text = item?.text
                    }

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): DataBindingHolder<ItemOthersBinding> {
                        return DataBindingHolder(R.layout.item_others, parent)
                    }

                })
            .onItemViewType { position, list ->
                if (list[position].type == 0) TYPE_RED else TYPE_OTHERS
            }

    }

}