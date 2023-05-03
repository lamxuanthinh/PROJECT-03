package com.example.project03.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project03.data.Product
import com.example.project03.databinding.BestDealItemBinding
import com.example.project03.databinding.SpecialRvItemBinding

class BestDealsAdapter:RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {
    inner class BestDealsViewHolder(private val binding: BestDealItemBinding ): RecyclerView.ViewHolder(binding.root){

        fun blind(product: Product){
            binding.apply{
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                product.offerPercentage?.let {
                    val remainingPricePercentage=1f-it
                    val priceAfterOffer= remainingPricePercentage*product.price
                    tvNewPrice.text="$ ${String.format("%.2f",priceAfterOffer)}"
                    tvOldPrice.paintFlags=tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                tvOldPrice.text="$ ${product.price}"
                tvBestDealName.text=product.name
            }

        }
    }
    private val diffCallback= object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem ==newItem
        }
    }
    val differ= AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product=differ.currentList[position]
        holder.blind(product)
        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick: ((Product) -> Unit)? =null
}