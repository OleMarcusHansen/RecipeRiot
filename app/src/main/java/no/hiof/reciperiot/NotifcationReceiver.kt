package no.hiof.reciperiot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotifcationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val service = NotificationService(context)
        service.showNotification(user = R.string.username.toString())
    }

}