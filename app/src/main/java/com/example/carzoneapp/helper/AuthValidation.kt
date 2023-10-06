package com.example.carzoneapp.helper

import android.content.Context
import android.util.Patterns
import com.example.carzoneapp.R
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Matcher
import java.util.regex.Pattern
object AuthValidation {
    fun isValidEmail(email: String): Boolean {
        if (email != "") {
            val mailContainCars = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val checkMailNumbers = email.split("@")[0]
            val checkIfMailNumbers: Int? = checkMailNumbers.toIntOrNull()
            return when {
                mailContainCars -> {
                    false
                }
                email.length < 14 -> {
                    false
                }
                checkIfMailNumbers != null -> {
                    false
                }
                else -> {
                    true
                }
            }
        } else {
            return false
        }
    }

    fun validatePassword(
        context: Context,
        userPass: TextInputLayout
    ): Boolean {
        val pass = userPass.editText?.text.toString()
        val pat = Pattern.compile("[A-Z][^A-Z]*$")
        val matchCapital: Matcher = pat.matcher(pass)

        var lastCapitalIndex = -1
        if (matchCapital.find()) {
            lastCapitalIndex = matchCapital.start()
        }
        val patNumber = Pattern.compile("[0-9]")
        val matchNumber: Matcher = patNumber.matcher(pass)
        var lastNum = -1
        if (matchNumber.find()) {
            lastNum = matchNumber.start()
        }
        if (pass != "") {
            val specialPass = Pattern.compile("[.!@#\$%&*()_+=|<>?{}\\\\\\[\\]~-]")
            val b = specialPass.matcher(pass)
            val passContainCars = b.find()

            when {
                pass.length < 8 -> {
                    userPass.helperText = context.resources.getString(R.string.min_password)
                    userPass.requestFocus()
                    return false
                }
                passContainCars -> {
                    userPass.helperText =
                        context.resources.getString(R.string.password_no_spec_characters)
                    userPass.requestFocus()
                    return false
                }
                lastCapitalIndex == -1 -> {
                    userPass.helperText = context.getString(R.string.cap_letter)
                    userPass.requestFocus()
                    return false
                }
                lastNum == -1 -> {
                    userPass.helperText = context.getString(R.string.one_num_needed)
                    userPass.requestFocus()
                    return false
                }
                else -> {
                    userPass.helperText = null
                    return true
                }
            }
        } else {
            userPass.helperText = context.resources.getString(R.string.password_empty)
            userPass.requestFocus()

            return false

        }

    }

    fun validateMobile(mobile: String): Boolean {
        return when (mobile.length) {
            11 -> {
                val emailSplit = mobile.split("")
                val mobileFirstThreeNumber: String = emailSplit[1] + emailSplit[2] + emailSplit[3]
                mobileFirstThreeNumber == "010" || mobileFirstThreeNumber == "011" || mobileFirstThreeNumber == "012" || mobileFirstThreeNumber == "015"
            }

            else -> {
                false
            }
        }


    }
}