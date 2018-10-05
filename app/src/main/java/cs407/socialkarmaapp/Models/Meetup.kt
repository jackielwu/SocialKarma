package cs407.socialkarmaapp.Models

class Meetup(val title: String, val startTime: Int, val endTime: Int, val location: SKLocation, val organizer: String, val organizerName: String, val shortDescription: String?)

class SKLocation(val name: String?, val coordinates: Coordinates?)

class Coordinates(val lat: Double, val lng: Double)