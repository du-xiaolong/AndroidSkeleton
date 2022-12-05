package com.ello.basequickadapter4.normal

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.ello.basequickadapter4.R

/**
 * @author dxl
 * @date 2022-12-05  周一
 */
class NormalListAdapter: BaseQuickAdapter<NormalModel, QuickViewHolder>() {

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: NormalModel?) {
        holder.itemView.setBackgroundColor(item?.color!!)
        holder.setText(R.id.tv, item.text)
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_normal, parent)
    }
}