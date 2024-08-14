package com.omgodse.notally

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.omgodse.notally.room.AlarmDetails

class AlarmService : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ALARM", "received notification for alarm!")
        Toast.makeText(context, "Reminder!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val INTENT_FLAGS = PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE

        fun scheduleAlarm(context: Context, alarm: AlarmDetails) {
            val intent = Intent(context, AlarmService::class.java)
            val sender = PendingIntent.getBroadcast(context, alarm.hashCode(), intent, INTENT_FLAGS)
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            manager.setExact(AlarmManager.RTC_WAKEUP, alarm.time, sender)
        }

        fun cancelAlarm(context: Context, alarm: AlarmDetails) {
            val intent = Intent(context, AlarmService::class.java)
            val sender = PendingIntent.getBroadcast(context, alarm.hashCode(), intent, INTENT_FLAGS)
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(sender)
        }
        @RequiresApi(31)
        fun canScheduleAlarms(context: Context): Boolean {
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            return manager.canScheduleExactAlarms()
        }
    }

}