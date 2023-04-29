package com.example.project03.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project03.data.CartProduct
import com.example.project03.databinding.CartProductItemBinding
import com.example.project03.helper.getProductPrice

class CartProductAdapter: RecyclerView.Adapter<CartProductAdapter.CartProductsViewHolder>() {
    inner class CartProductsViewHolder( val binding: CartProductItemBinding): RecyclerView.ViewHolder(binding.root){


        fun blind(cartProduct: CartProduct){
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text= cartProduct.product.name
                tvCartProductQuantity.text= cartProduct.quality.toString()

                val priceAfterPecentage = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                tvProductCartPrice.text = "$ ${String.format("%.2f",priceAfterPecentage)}"

                imageCartProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor?: Color.TRANSPARENT))
                tvCartProductSize.text = cartProduct.selectedSize?:"".also { imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }

            }
        }
    }

    private val diffCallback= object: DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id==newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem ==newItem
        }
    }
    val differ= AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct=differ.currentList[position]
        holder.blind(cartProduct)

        holder.itemView.setOnClickListener{
            onProductClick?.invoke(cartProduct)
        }

        holder.binding.imagePlus.setOnClickListener{
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.imageMinus.setOnClickListener{
            onMinusClick?.invoke(cartProduct)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onProductClick: ((CartProduct) -> Unit)? =null
    var onPlusClick: ((CartProduct) -> Unit)? =null
    var onMinusClick: ((CartProduct) -> Unit)? =null
}