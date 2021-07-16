package com.guilhermegals.whatsnumber.core.common

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.guilhermegals.whatsnumber.R
import com.guilhermegals.whatsnumber.data.model.NumberStatus

/** Nessa classe é realizado customizações de Binding */

@BindingAdapter("android:goneUnless")
fun View.goneUnless(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("android:setTextByNumberStatus")
fun MaterialTextView.setTextByNumberStatus(numberStatus: NumberStatus?) {
    this.setText(
        when (numberStatus) {
            null, NumberStatus.Unknown -> R.string.number_status_unknown
            NumberStatus.Error -> R.string.number_status_error
            NumberStatus.GreaterThan -> R.string.number_status_greater_than
            NumberStatus.LessThan -> R.string.number_status_less_than
            NumberStatus.Win -> R.string.number_status_win
            NumberStatus.NoStatus -> R.string.number_status_waiting
        }
    )
}

@BindingAdapter("android:setEnabledByNumberStatus")
fun MaterialButton.setEnabledByNumberStatus(numberStatus: NumberStatus?) {
    this.isEnabled = when (numberStatus) {
        null, NumberStatus.Unknown, NumberStatus.Error, NumberStatus.Win -> false
        else -> true
    }
}

@BindingAdapter("error")
fun TextInputLayout.setError(errorTextResource: Int?) {
    if (errorTextResource != null && errorTextResource != 0) {
        this.error = this.context.getString(errorTextResource)
    } else {
        this.isErrorEnabled = false
    }
}

@BindingAdapter("android:setAttemptsText")
fun MaterialTextView.setAttemptsText(attempts: Int?) {
    attempts?.let {
        this.text = when {
            it == 1 -> this.context.getString(R.string.number_attempts_singular_message)
            it > 1 -> this.context.getString(R.string.number_attempts_plural_message, it.toString())
            else -> ""
        }
    }
}