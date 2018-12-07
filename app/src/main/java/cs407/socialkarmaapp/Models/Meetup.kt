package cs407.socialkarmaapp.Models

class Meetup(val meetupId: String, val title: String, val startTime: Long, val endTime: Long, val location: SKLocation, val organizer: String, val organizerName: String, val shortDescription: String?, val description: String?, val usersAttending: List<User>, val attending: Boolean?, val geolocation: String)

class SKLocation(val name: String?, val coordinates: Coordinates)

class Coordinates(val lat: Double, val lng: Double)

class User(val username: String)