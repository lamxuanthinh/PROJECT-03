package com.example.productsadder

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.productsadder.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var selectedImage = mutableListOf<Uri>()
    private val selectedColor = mutableListOf<Int>()
    private val productsStorage = Firebase.storage.reference
    private val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(this).setTitle("Color Products")
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColor.add(it.color)
                            updateColors()
                        }
                    }
                }).setNegativeButton("Cancel") { colorPicker, _ ->
                    colorPicker.dismiss()
                }.show()
        }

        val selectImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data

                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        (0 until count).forEach {
                            var imageUri = intent.clipData?.getItemAt(it)?.uri
                            imageUri?.let {
                                selectedImage.add(it)
                            }
                        }
                    } else {
                        val imageUri = intent?.data
                        imageUri?.let {
                            selectedImage.add(it)
                        }
                    }
                    updateImages()
                }
            }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImagesActivityResult.launch(intent)
        }
    }

    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImage.size.toString()
    }


    private fun updateColors() {
        var colors = ""
        selectedColor.forEach {
            colors = "$colors ${Integer.toHexString(it)}"
        }
        binding.tvSelectedColors.text = colors
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.saveProduct) {
            val productionValidation = validationInformation()
            if (!productionValidation) {
                Toast.makeText(this, "Check Your Inputs", Toast.LENGTH_LONG).show()
                return false
            }
            saveProduct()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.offerPercentage.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val size = getSizeList(binding.edSizes.text.toString().trim())
        var imagesByteArrays = getImagesByteArray()
        var images = mutableListOf<String>()

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                showLoading()
            }

            try {
                async {
                    imagesByteArrays.forEach {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imagesStorage = productsStorage.child("products/images/$id")
                            val result = imagesStorage.putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }.await()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    hideLoading()
                }
            }

            val products = Product(
                UUID.randomUUID().toString(),
                name,
                category,
                price.toFloat(),
                if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                if (description.isEmpty()) null else description,
                if (selectedColor.isEmpty()) null else selectedColor,
                size,
                images
            )

            firestore.collection("Products").add(products).addOnSuccessListener {
                hideLoading()
                binding.edName.setText("")
                binding.edCategory.setText("")
                binding.edPrice.setText("")
                binding.offerPercentage.setText("")
                binding.edDescription.setText("")
                binding.edSizes.setText("")



            }.addOnFailureListener {
                hideLoading()
                Log.e("Error", it.message.toString())
            }
        }
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
        this@MainActivity.recreate()
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun getImagesByteArray(): List<ByteArray> {
        val imageByteArray = mutableListOf<ByteArray>()
        selectedImage.forEach {
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                imageByteArray.add(stream.toByteArray())
            }
        }
        return imageByteArray
    }

    private fun getSizeList(sizesStr: String): List<String>? {
        if (sizesStr.isEmpty()) return null
        val sizeList = sizesStr.split(",")
        return sizeList
    }

    private fun validationInformation(): Boolean {
        if (binding.edPrice.text.toString().trim().isEmpty()) return false
        if (binding.edName.text.toString().trim().isEmpty()) return false
        if (binding.edCategory.text.toString().trim().isEmpty()) return false
        if (selectedImage.isEmpty()) return false
        return true
    }
}