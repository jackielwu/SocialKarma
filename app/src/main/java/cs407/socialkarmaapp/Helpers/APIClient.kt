package cs407.socialkarmaapp.Helpers

import android.location.Location
import com.google.android.gms.location.places.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object APIClient {
    private const val baseURL = "http://10.0.2.2:8080"

    fun getGeolocation(location: Location, callback: Callback) {
        val url = baseURL + "/geo?lat=" + location.latitude + "&lng=" + location.longitude
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(callback)
    }

    fun getPosts(geoLocation: String, lastStartTime: Int?, callback: Callback) {
        val url = baseURL + "/posts?geolocation=" + geoLocation
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(callback)
    }

    fun getMeetups(lastStartTime: Int?, callback: Callback) {
        var url: String
        lastStartTime?.let {
            url = baseURL + "/meetups?endAt=" + lastStartTime + "&userId=" + FirebaseAuth.getInstance().currentUser?.uid
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(callback)
        } ?: run {
            url = baseURL + "/meetups?userId=" + FirebaseAuth.getInstance().currentUser?.uid
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(callback)
        }
    }

    fun getMeetupDetail(meetupId: String, callback: Callback) {
        var url = baseURL + "/meetup/" + meetupId + "?userId=" + FirebaseAuth.getInstance().currentUser?.uid
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
        json.put("organizer",  FirebaseAuth.getInstance().currentUser?.uid)

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(callback)
    }

    fun postRsvpMeetup(meetupId: String, callback: Callback) {
        var url = baseURL + "/meetup/rsvp"
        val json = JSONObject()
        json.put("meetupId", meetupId)
        json.put("userId", FirebaseAuth.getInstance().currentUser?.uid)

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(callback)
    }

    fun postNewPost(location: Location, title: String, description: String, callback: Callback) {
        var url = baseURL + "/post"
        val json = JSONObject()
        json.put("title", title)
        json.put("content", description)
        val locationJson = JSONObject()
        locationJson.put("lat", location.latitude)
        locationJson.put("lng", location.longitude)
        json.put("location", locationJson)
        json.put("author", FirebaseAuth.getInstance().currentUser?.uid)

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(callback)
    }

    fun getComments(postId: String, callback: Callback) {
        var url = baseURL + "/post/comments"
        val json = JSONObject()
        json.put("postId", postId)

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(callback)
    }

    fun postNewComment(postId: String, comment: String, callback: Callback) {
        var url = baseURL + "/post/comment"
        val json = JSONObject()
        json.put("userId", FirebaseAuth.getInstance().currentUser?.uid)
        json.put("postId", postId)
        json.put("comment", comment)

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(callback)
    }

    fun postPostVote() {}
}