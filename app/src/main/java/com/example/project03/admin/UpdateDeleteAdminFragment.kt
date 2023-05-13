package com.example.hotelapplication.fragment.admin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.project03.R
import com.example.project03.databinding.FragmentAdminUpdateDeleteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UpdateDeleteAdminFragment:Fragment() {
    private lateinit var binding:FragmentAdminUpdateDeleteBinding
    private val args by navArgs<UpdateDeleteAdminFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAdminUpdateDeleteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productsshow=args.productadmin
//        var documentId=""
//        val adapter=ArrayAdapter(this,)


        binding.apply {
            val sizeL = productsshow.sizes?.joinToString(", ")
            editTextName.setText(productsshow.name)
            editTextCategory.setText(productsshow.category)
            editTextOfferPercentage.setText("${productsshow.offerPercentage}")
            editTextPrice.setText("${productsshow.price}")
            editTextDescription.setText(productsshow.description)
            edSizes.setText(sizeL)

        }
        binding.btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Delete Confirmation")
            builder.setMessage("Are you sure you want to delete this hotel?")
            builder.setPositiveButton("Yes") { dialog, which ->
                // Xóa hotelshow ở đây
                val db = FirebaseFirestore.getInstance()
                val collectionRef = db.collection("Products")

                collectionRef.whereEqualTo("id", "${productsshow.id}")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            document.reference.delete()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Error getting documents: ", exception)
                    }
                Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.adminMainFragment)
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
        binding.btnUpdate.setOnClickListener {
            val upname=binding.editTextName.text.toString()
            val upcategory=binding.editTextCategory.text.toString()
            val upOF=binding.editTextOfferPercentage.text.toString().toFloat()
            val updecription=binding.editTextDescription.text.toString()
            val upPrice=binding.editTextPrice.text.toString().toFloat()
            val upSize=getSizeList(binding.edSizes.text.toString().trim())


            val updateproductcurrent = FirebaseFirestore.getInstance().collection("Products").document("${productsshow.id}")
            val productupdates = hashMapOf(
                "name" to "${upname}",
                "price" to upPrice ,
                "category" to "${upcategory}",
                "offerPercentage" to upOF,
                "description" to "${updecription}",
                "sizes" to upSize,
                "idadmin" to FirebaseAuth.getInstance().currentUser!!.uid
            )
            updateproductcurrent.set(productupdates, SetOptions.merge()).addOnSuccessListener {
                findNavController().navigate(R.id.adminMainFragment)
            }.addOnFailureListener {

            }

        }


    }
    private fun getSizeList(sizesStr: String): List<String>? {
        if (sizesStr.isEmpty()) return null
        val sizeList = sizesStr.split(",")
        return sizeList
    }
}