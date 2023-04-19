package com.example.project03.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever.BitmapParams
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project03.Project03Application
import com.example.project03.util.Resource
import com.example.project03.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.project03.data.User
import com.example.project03.util.RegisterValidation
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
@HiltViewModel
class UserAccountViewMoldel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage:StorageReference,
    app:Application
) : AndroidViewModel(app){
    private val _user= MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()
    private val _updateInfo= MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()
    init {
        getUser()
    }
    fun getUser(){
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user =it.toObject(User::class.java)
                user?.let{
                    viewModelScope.launch {
                        _user.emit(Resource.Success(it))
                    }

                }


            }.addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun updateUser(user: User,imgUri: Uri?){
        val areInputValid= validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty() && user.lastName.trim().isNotEmpty()
        if (!areInputValid){
            viewModelScope.launch {
                _user.emit(Resource.Error("Check your inputs"))
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())

        }
        if (imgUri==null){
            saveUserInfomation(user,true)
        }else{
            saveUserInfomationWithNewImage(user,imgUri)
        }
    }

    private fun saveUserInfomationWithNewImage(user: User, imgUri: Uri) {
        viewModelScope.launch{
            try {
                val imageBitmap= MediaStore.Images.Media.getBitmap(getApplication<Project03Application>().contentResolver,imgUri)
                val byteArrayOutputStream =ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,96,byteArrayOutputStream)
                val imageByteArray=byteArrayOutputStream.toByteArray()
                val imageDirectory=storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result=imageDirectory.putBytes(imageByteArray).await()
                val imageUrl=result.storage.downloadUrl.await().toString()
                saveUserInfomation(user.copy(imagePath =imageUrl ),false)
            }catch (e:Exception){
                viewModelScope.launch {
                    _user.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun saveUserInfomation(user: User, shouldRetrieveOldImage: Boolean) {
        firestore.runTransaction { transition->
            val documentRef=firestore.collection("user").document(auth.uid!!)
            val currentUser=transition.get(documentRef).toObject(User::class.java)
            if(shouldRetrieveOldImage){
                val newUser= user.copy(imagePath = currentUser?.imagePath?:"")
                transition.set(documentRef,newUser)
            }else{
                transition.set(documentRef,user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Success(user))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error(it.message.toString()))
            }
        }
    }

}