package net.pside.androidthings.example.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import net.pside.androidthings.example.event.CloudMessageEvent
import org.greenrobot.eventbus.EventBus

import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        Timber.d("onMessageReceived: From: %s", remoteMessage!!.from)

        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("onMessageReceived: payload: %s", remoteMessage.data.toString())
        }

        if (remoteMessage.notification != null) {
            val body = remoteMessage.notification.body
            Timber.d("onMessageReceived: msgBody: %s", body)
            EventBus.getDefault().post(CloudMessageEvent(body))
        }
    }
}
