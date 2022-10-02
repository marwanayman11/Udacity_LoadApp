package com.example.loadapp

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0

@SuppressLint("UnspecifiedImmutableFlag", "WrongConstant")
fun NotificationManager.sendNotification(
    status: Boolean,
    body: String,
    applicationContext: Context
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    val extras = Bundle()
    extras.putBoolean("status", status)
    extras.putString("body", body)
    extras.putInt("nId", NOTIFICATION_ID)
    contentIntent.putExtras(extras)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val action = NotificationCompat.Action(
        R.drawable.ic_assistant_black_24dp,
        applicationContext.getString(R.string.check),
        contentPendingIntent
    )
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.resources.getString(R.string.notification_channel_id)
    )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.resources.getString(R.string.notification_title))
        .setContentText(applicationContext.resources.getString(R.string.notification_description))
        .setContentIntent(contentPendingIntent)
        .addAction(action)
        .setAutoCancel(true)
    notify(NOTIFICATION_ID, builder.build())


}

