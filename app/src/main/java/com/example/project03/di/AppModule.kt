package com.example.project03.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.project03.data.Category
import com.example.project03.firebase.FirebaseCommon
import com.example.project03.util.Constants.INTRODUCTION_SP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirebaseStoreDatabase() = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseCommon(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore) =
        FirebaseCommon(firestore, firebaseAuth)

    @Provides
    @Singleton
    fun providesStorage() = FirebaseStorage.getInstance().reference

    @Provides
    fun provideIntroductionSP(
        application: Application
    ) = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)

}