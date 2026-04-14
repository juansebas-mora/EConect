package com.econect.app.data.remote.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.econect.app.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EConectFcmService : FirebaseMessagingService() {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onCreate() {
        super.onCreate()
        crearCanalNotificaciones()
    }

    /**
     * Se invoca cuando FCM asigna o rota el token del dispositivo.
     * Guarda el nuevo token en Firestore bajo users/{uid}/fcmToken.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .update("fcmToken", token)
    }

    /**
     * Se invoca cuando llega un mensaje FCM con la app en primer plano,
     * o en segundo plano si el mensaje no tiene payload de notificación.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val titulo = message.notification?.title ?: message.data["titulo"] ?: return
        val cuerpo = message.notification?.body ?: message.data["cuerpo"] ?: ""
        mostrarNotificacionLocal(titulo, cuerpo)
    }

    // ── Privado ──────────────────────────────────────────────────────────────

    private fun crearCanalNotificaciones() {
        val channelId = getString(com.econect.app.R.string.fcm_channel_id)
        val channelName = getString(com.econect.app.R.string.fcm_channel_name)
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Alertas de recogida y mensajes de EConect"
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun mostrarNotificacionLocal(titulo: String, cuerpo: String) {
        val channelId = getString(com.econect.app.R.string.fcm_channel_id)

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notificacion)
    }
}
