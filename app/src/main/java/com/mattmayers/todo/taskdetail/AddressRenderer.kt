package com.mattmayers.todo.taskdetail

import android.location.Address

class AddressRenderer(val address: Address) {
    fun renderSingleLine(): String {
        return (0..address.maxAddressLineIndex)
                .joinToString(", ") { address.getAddressLine(it) }
    }
}