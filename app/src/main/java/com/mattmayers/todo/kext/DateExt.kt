package com.mattmayers.todo.kext

import java.util.*

fun Date.isTodayOrEarlier(): Boolean {
    val t = Date()
    return year <= t.year && month <= t.month && date <= t.date
}