package com.hfad.ansormarket.firebase

import com.google.firebase.messaging.FirebaseMessagingService

private const val CHANNEL_ID = "myChannel"

class MyFirebaseMessagingService : FirebaseMessagingService() {
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        val intent = Intent(this, OrdersFragment::class.java)
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val notificationID = Random.nextInt()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel(notificationManager)
//        }
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
//        )
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(message.data["title"])
//            .setContentText(message.data["message"])
//            .setSmallIcon(R.drawable.ic_stat_ic_notification)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .build()
//        notificationManager.notify(notificationID, notification)
//
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(notificationManager: NotificationManager) {
//        val channelName = "channelName"
//        val channel = NotificationChannel(
//            CHANNEL_ID,
//            channelName,
//            NotificationManager.IMPORTANCE_HIGH
//        ).apply {
//            description = "New Orders"
//            enableLights(true)
//            lightColor = Color.GREEN
//        }
//        notificationManager.createNotificationChannel(channel)
//    }
}