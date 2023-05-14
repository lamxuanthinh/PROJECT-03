package com.example.project03.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project03.R
import com.example.project03.data.Product
import com.example.project03.databinding.ItemShowProductBinding

class ShowProductsAdapter () : RecyclerView.Adapter<ShowProductsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemShowProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun blind(product: Product) {
            Glide.with(itemView).load(product.images[0]).into(binding.imgshow)
            binding.tvproductshow.text = product.name
            binding.tvcategoryshow.text = "Categry: "+product.category
        }
    }
    private val diffCallback= object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem:Product): Boolean {
            return oldItem ==newItem
        }
    }
    val differ= AsyncListDiffer(this,diffCallback)
    // Tạo ViewHolder mới
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShowProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Gán giá trị cho ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product=differ.currentList[position]
        holder.blind(product)
        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }
    }

    // Trả về số lượng item trong danh sách
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Interface xử lý sự kiện click
    interface OnItemClickListener {
        fun onItemClick(hotel: Product)
    }
    var onClick: ((Product) -> Unit)? =null
}
