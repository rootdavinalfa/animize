/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.util

import java.text.SimpleDateFormat
import java.util.*

class FriendlyTime {
    fun getFriendlyTime(needProcess : Date,targetDifference : Long) : String{

        val calDate = Calendar.getInstance()
        val tz = calDate.timeZone
        val formatDateFriendly = SimpleDateFormat("dd", Locale.getDefault())
        val formatMonth = SimpleDateFormat("M", Locale.getDefault())
        formatDateFriendly.timeZone = tz
        val day = formatDateFriendly.format(needProcess)
        val today = Integer.valueOf(formatDateFriendly.format(System.currentTimeMillis()))
        val friendly: String
        val diff = System.currentTimeMillis() - targetDifference
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000) % 24
        val diffDays = diff / (24 * 60 * 60 * 1000)
        friendly = if (Integer.valueOf(day) == today && Integer.valueOf(formatMonth.format(needProcess)) == Integer.valueOf(formatMonth.format(System.currentTimeMillis()))) {
            if (diffMinutes <= 58 && diffHours == 0L) {
                "Today, $diffMinutes Minutes ago"
            } else {
                "Today, $diffHours Hours ago"
            }
        } else {
            if (diffDays < 1) {
                "Yesterday, $diffHours Hours ago"
            }
            else if (diffDays > 5){
                val format = SimpleDateFormat("yyyy MMM dd", Locale.getDefault())
                format.timeZone = tz
                format.format(needProcess)
            }
            else {
                if (today - 1 == Integer.valueOf(day)) {
                    //friendly = "Yesterday, "+ diffHours +" Hours ago";
                    "Yesterday" + if (diffHours == 0L) "" else ", $diffHours Hours ago"
                } else {
                    diffDays.toString() + " Days " + if (diffHours == 0L) " Ago" else ", $diffHours Hours ago"
                    //friendly = "Yesterday"+ (diffHours==0?"":", "+diffHours+" Hours ago");
                }
            }
        }
        return friendly
    }
}