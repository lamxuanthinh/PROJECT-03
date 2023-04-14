package com.example.project03.util

import android.util.Patterns


fun validateEmail(email: String): RegisterValidation {
    if (email.isEmpty()) return RegisterValidation.Failed("Email can not be empty")

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return RegisterValidation.Failed("Wrong email format")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {
    if (password.isEmpty()) return RegisterValidation.Failed("Password can not be empty")

    if (password.length < 6) return RegisterValidation.Failed("Password should contain 6 char")

    return RegisterValidation.Success
}

