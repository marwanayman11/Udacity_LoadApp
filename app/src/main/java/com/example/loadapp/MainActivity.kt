package com.example.loadapp

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        notificationManager = getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Loading
            download(
                when (group.checkedRadioButtonId) {
                    glide.id -> glide_url
                    retrofit.id -> retrofit_url
                    udacity.id -> udacity_url
                    else -> null
                }
            )
        }
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range", "UnspecifiedImmutableFlag")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            custom_button.buttonState = ButtonState.Completed
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = downloadManager.query(DownloadManager.Query().setFilterById(id!!))
            if (query.moveToFirst()) {
                val status =
                    when (query.getInt(query.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> true
                        else -> false
                    }
                val name = query.getString(query.getColumnIndex(DownloadManager.COLUMN_TITLE))
                val description =
                    query.getString(query.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
                val detailText = "$name - $description"
                notificationManager.sendNotification(status, detailText, applicationContext)

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun download(url: String?) {
        if (url != null) {
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url)
            when (url) {
                glide_url -> {
                    request.setTitle(getString(R.string.glide_name))
                    request.setDescription(getString(R.string.glide_description))
                }
                retrofit_url -> {
                    request.setTitle(getString(R.string.retrofit_name))
                    request.setDescription(getString(R.string.retrofit_description))
                }
                udacity_url -> {
                    request.setTitle(getString(R.string.app_name))
                    request.setDescription(getString(R.string.app_description))
                }
            }
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            custom_button.buttonState = ButtonState.Clicked
            Toast.makeText(applicationContext, getString(R.string.toast), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val glide_url =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val udacity_url =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val retrofit_url =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download"
            val notificationManager = getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}