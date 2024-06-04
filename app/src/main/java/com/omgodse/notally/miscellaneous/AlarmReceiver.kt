package com.omgodse.notally.miscellaneous

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.omgodse.notally.room.AlarmDetails
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Reminder!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val INTENT_FLAGS = PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE

        fun scheduleAlarm(context: Context, alarm: AlarmDetails) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val sender = PendingIntent.getBroadcast(context, alarm.hashCode(), intent, INTENT_FLAGS)
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val relTime = alarm.time - Calendar.getInstance().timeInMillis
            manager.setExact(AlarmManager.RTC_WAKEUP, relTime, sender)
        }

        fun cancelAlarm(context: Context, alarm: AlarmDetails) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val sender = PendingIntent.getBroadcast(context, alarm.hashCode(), intent, INTENT_FLAGS)
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(sender)
        }
    }

}