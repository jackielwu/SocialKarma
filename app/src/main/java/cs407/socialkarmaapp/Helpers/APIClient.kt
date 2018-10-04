package cs407.socialkarmaapp.Helpers

import okhttp3.*
import java.io.IOException

object APIClient {
    private const val baseURL = "localhost:8080"

    fun getMeetups(lastStartTime: Int?, callback: Callback) {
        var url: String
        lastStartTime?.let {
            url = baseURL + "/meetups?endAt=" + lastStartTime
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(callback)
        } ?: run {
            url = baseURL + "/meeutps"
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(callback)
        }
    }
}