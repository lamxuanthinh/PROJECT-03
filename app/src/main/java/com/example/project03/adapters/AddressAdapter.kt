package com.example.project03.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.project03.data.Address
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.project03.R
import com.example.project03.databinding.AddressRvItemBinding

class AddressAdapter: Adapter<AddressAdapter.AddressViewHolder>() {
    inner class AddressViewHolder(val  binding: AddressRvItemBinding):
            ViewHolder(binding.root){
                fun bind(address: Address,isSelected: Boolean){
                    binding.apply {
                        buttonAddress.text = address.addressTitle
                        if (isSelected){
                            buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_pink))
                        } else{
                            buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white
                            ))

                        }

                    }
                }

            }

    private val diffUtil = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
                return oldItem == newItem
            }

        }

        val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var selectedAddress = -1
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)

        holder.binding.buttonAddress.setOnClickListener {
            if (selectedAddress >= 0)
                notifyItemChanged(selectedAddress)
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }
    init {
        differ.addListListener{ _, _->
            notifyItemChanged(selectedAddress)

        }
    }

    var onClick: ((Address) -> Unit)? = null
}