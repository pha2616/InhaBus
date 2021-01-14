package com.example.inhabus

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.inhabus.navigation.AlarmFragment
import kotlinx.android.synthetic.main.fragment_alarm.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val notificationManager: NotificationManager = p0!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent: Intent = Intent(p0, PaymentFragment::class.java)

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pending = PendingIntent.getActivity(p0, 0, notificationIntent, 0)

        val builder = NotificationCompat.Builder(p0, "default")

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            builder.setSmallIcon(R.drawable.ic_launcher_foreground)

            val channel_name = "버스 예약 알림"
            val description = "오늘" + p1!!.getStringExtra("direction") + " " + p1!!.getStringExtra("city")+" "+ p1!!.getStringExtra("time")+" 버스 예약하셨습니다."
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel("default", channel_name, importance)
            channel.description = description

            if(notificationManager != null){
                notificationManager.createNotificationChannel(channel)


            }

        }else builder.setSmallIcon(R.mipmap.ic_launcher)

        builder.setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setTicker("{Time to watch some cool stuff!}")
            .setContentTitle("버스 예약 알림")
            .setContentText("오늘" + p1!!.getStringExtra("direction") + " " + p1!!.getStringExtra("city")+" "+ p1!!.getStringExtra("time")+" 버스 예약하셨습니다.")
            .setContentInfo("INFO")
            .setContentIntent(pending)

        if(notificationManager != null){
            notificationManager.notify(1234, builder.build())
        }
    }


}