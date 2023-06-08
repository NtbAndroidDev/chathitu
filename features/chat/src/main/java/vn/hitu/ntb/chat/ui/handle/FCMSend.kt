package vn.hitu.ntb.chat.ui.handle

import android.content.Context
import android.os.StrictMode
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/04/2023
 */
object FCMSend {
    private val BASE_URL = "https://fcm.googleapis.com/fcm/send"
    private val SERVER_KEY = "key=AAAAdn93vW8:APA91bF3tZE1LfwCf0-dUfnFBqGoAKyA1nT8ONJ6fdHCT56pClSGyS8uEGSj6Mg4YBTuzplHyxXUOhVsAtaE7fODQFre5SZxRHEo0VKhDq5o4PU7zK-mYHogaqMsIGwxKk_SeHpV7P4W"

    fun pushNotification(context: Context?, token: String?, title: String?, body: String?, avt : String?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val queue: RequestQueue = Volley.newRequestQueue(context)
        try {
            val json = JSONObject()
            json.put("to", token)
            val notification = JSONObject()
            notification.put("title", title)
            notification.put("body", body)
            notification.put("avt", avt)
            json.put("data", notification)
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, BASE_URL, json,
                Response.Listener { response -> println("FCM $response") },
                Response.ErrorListener {
                    println("FCM error!")
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Content-Type"] = "application/json"
                    params["Authorization"] = SERVER_KEY
                    return params
                }
            }
            queue.add(jsonObjectRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}