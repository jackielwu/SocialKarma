package cs407.socialkarmaapp.Helpers

import com.google.android.gms.location.places.Place
import com.google.gson.JsonObject
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object APIClient {
    private const val baseURL = "http://10.0.2.2:8080"

    fun getMeetups(lastStartTime: Int?, callback: Callback) {
        var url: String
        lastStartTime?.let {
            url = baseURL + "/meetups?endAt=" + lastStartTime + "&userId=" + "sidfjlhgnuinresjnfliaewrnflai234aiw"
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(callback)
        } ?: run {
            url = baseURL + "/meetups?userId=" + "sidfjlhgnuinresjnfliaewrnflai234aiw"
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(callback)
        }
    }

    fun getMeetupDetail(meetupId: String, callback: Callback) {
        var url = baseURL + "/meetup/" + meetupId + "?userId=" + "sidfjlhgnuinresjnfliaewrnflai234aiw"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(callback)
    }

    fun postNewMeetup(title: String, description: String?, startTime: Long, endTime: Long, location: Place, callback: Callback) {
        var url = baseURL + "/meetup"
        val json = JSONObject()
        json.put("title", title)
        if (description != null) {
            json.put("description", description)
        }
        json.put("startTime", startTime)
        json.put("endTime", endTime)
        val locationJson = JSONObject()
        val coordinatesJson = JSONObject()
        locationJson.put("name", location.name)
        coordinatesJson.put("lat", location.latLng.latitude)
        coordinatesJson.put("lng", location.latLng.longitude)
        locationJson.put("coordinates", coordinatesJson)
        json.put("location", locationJson)
        json.put("organizer", "sidfjlhgnuinresjnfliaewrnflai234aiw")

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(callback)
    }

    fun postRsvpMeetup(meetupId: String, callback: Callback) {
        var url = baseURL + "/meetup/rsvp"
        val json = JSONObject()
        json.put("meetupId", meetupId)
        json.put("userId", "sidfjlhgnuinresjnfliaewrnflai234aiw")

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(callback)
    }
}