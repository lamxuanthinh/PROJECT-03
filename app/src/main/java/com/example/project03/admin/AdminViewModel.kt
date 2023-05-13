package com.example.project03.admin

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project03.Project03Application
import com.example.project03.data.Admin
import com.example.project03.util.RegisterValidation
import com.example.project03.util.Resource
import com.example.project03.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app){
    private val _admin= MutableStateFlow<Resource<Admin>>(Resource.Unspecified())
    val adminup = _admin.asStateFlow()

    private val _updateAdInfo= MutableStateFlow<Resource<Admin>>(Resource.Unspecified())
    val updateAdminInfo = _updateAdInfo.asStateFlow()
    init {
        getAdmin()

    }
    fun getAdmin(){
        viewModelScope.launch {
            _admin.emit(Resource.Loading())
        }
        firestore.collection("admin").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user =it.toObject(Admin::class.java)
                user?.let{
                    viewModelScope.launch {
                        _admin.emit(Resource.Success(it))
                    }

                }


            }.addOnFailureListener {
                viewModelScope.launch {
                    _admin.emit(Resource.Error(it.message.toString()))
                }
            }
    }





    fun updateAdmin(admin: Admin, imgUri: Uri?){
        val areInputValid= validateEmail(admin.email) is RegisterValidation.Success
                && admin.firstName.trim().isNotEmpty() && admin.lastName.trim().isNotEmpty()
                && admin.phone.trim().isNotEmpty()
        if (!areInputValid){
            viewModelScope.launch {
                _admin.emit(Resource.Error("Check your inputs"))
            }
            return
        }

        viewModelScope.launch {
            _updateAdInfo.emit(Resource.Loading())

        }
        if (imgUri==null){
            saveAdminInfomation(admin,true)
        }else{
            saveAdminInfomationWithNewImage(admin,imgUri)
        }
    }

    private fun saveAdminInfomationWithNewImage(admin: Admin, imgUri: Uri) {
        viewModelScope.launch{
            try {
                val imageBitmap= MediaStore.Images.Media.getBitmap(getApplication<Project03Application>().contentResolver,imgUri)
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,96,byteArrayOutputStream)
                val imageByteArray=byteArrayOutputStream.toByteArray()
                val imageDirectory=storage.child("profileadminImages/${auth.uid}/${UUID.randomUUID()}")
                val result=imageDirectory.putBytes(imageByteArray).await()
                val imageUrl=result.storage.downloadUrl.await().toString()
                saveAdminInfomation(admin.copy(imgPath =imageUrl ),false)
            }catch (e:Exception){
                viewModelScope.launch {
                    _admin.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun saveAdminInfomation(admin: Admin, shouldRetrieveOldImage: Boolean) {
        firestore.runTransaction { transition->
            val documentRef=firestore.collection("admin").document(auth.uid!!)
            val currentUser=transition.get(documentRef).toObject(Admin::class.java)
            if(shouldRetrieveOldImage){
                val newUser= admin.copy(imgPath = currentUser?.imgPath?:"")
                transition.set(documentRef,newUser)
            }else{
                transition.set(documentRef,admin)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateAdInfo.emit(Resource.Success(admin))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateAdInfo.emit(Resource.Error(it.message.toString()))
            }
        }

    }
    fun logout(){
        auth.signOut()
    }

}