package com.github.spadger.mvvmc.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.spadger.mvvmc.demo.R
import com.github.spadger.mvvmc.demo.model.ContractResponse

class ContractListAdapter : ListAdapter<ContractResponse, ContractListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<ContractResponse>() {
        override fun areItemsTheSame(oldItem: ContractResponse, newItem: ContractResponse): Boolean =
            oldItem.Contract_Num == newItem.Contract_Num

        override fun areContentsTheSame(oldItem: ContractResponse, newItem: ContractResponse): Boolean =
            oldItem == newItem
    }
) {
    private var showLoading = false
    private var onItemClickListener: ((ContractResponse) -> Unit)? = null

    fun setOnItemClickListener(listener: (ContractResponse) -> Unit) {
        onItemClickListener = listener
    }

    fun showLoading(show: Boolean, recyclerView: RecyclerView? = null) {
        showLoading = show
        if (recyclerView != null) {
            recyclerView.post {
                notifyItemChanged(itemCount)
            }
        } else {
            notifyItemChanged(itemCount)
        }
    }

    override fun getItemCount(): Int = super.getItemCount() + if (showLoading) 1 else 0

    override fun getItemViewType(position: Int): Int = if (position == super.getItemCount()) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contract, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < super.getItemCount()) {
            val contract = getItem(position)
            holder.bind(contract)
            holder.itemView.setOnClickListener { onItemClickListener?.invoke(contract) }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(contract: ContractResponse) {
            itemView.findViewById<TextView>(R.id.tv_line_name)?.text = contract.Line_Name
            itemView.findViewById<TextView>(R.id.tv_contract_num)?.text = "合同号: ${contract.Contract_Num}"
            itemView.findViewById<TextView>(R.id.tv_load_street)?.text = "装货: ${contract.Load_Street}"
            itemView.findViewById<TextView>(R.id.tv_unload_street)?.text = "卸货: ${contract.Unload_Street}"
            itemView.findViewById<TextView>(R.id.tv_truck_fee)?.text = "运费: ${contract.Truck_Fee}元"
        }
    }
}