package com.thirteendollars.singlerent

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Damian Nowakowski on 20/04/2018.
 * mail: thirteendollars.com@gmail.com
 */
 class Utils {
    companion object {
        fun dateFromString(date: String): Date {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) // isoDateTime
            return format.parse(date)
        }
    }
}