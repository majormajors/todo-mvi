package com.mattmayers.todo.kext

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

private fun View.imm() = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

fun View.focusAndShowSoftKeyboard() {
    requestFocus()
    showSoftKeyboard()
}

fun View.showSoftKeyboard() {
    imm().toggleSoftInputFromWindow(applicationWindowToken, InputMethodManager.SHOW_FORCED, 0)
}

fun View.hideSoftKeyboard() {
    imm().hideSoftInputFromWindow(windowToken, 0)
}