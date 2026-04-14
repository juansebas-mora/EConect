package com.econect.app.data.model

import com.econect.app.data.local.db.entity.UserEntity
import com.econect.app.domain.model.PreferredLocation
import com.econect.app.domain.model.Schedule
import com.econect.app.domain.model.User
import com.econect.app.domain.model.UserType
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.util.UUID

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    phone = phone,
    userType = UserType.valueOf(userType),
    preferredLocations = parsePreferredLocationList(preferredLocationsJson),
    availableSchedules = parseScheduleList(schedulesJson),
    rating = rating,
    ratingCount = ratingCount,
    preferredRecyclingCenterId = preferredRecyclingCenterId?.ifEmpty { null }
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    phone = phone,
    userType = userType.name,
    preferredLocationsJson = serializePreferredLocationList(preferredLocations),
    schedulesJson = serializeScheduleList(availableSchedules),
    rating = rating,
    ratingCount = ratingCount,
    preferredRecyclingCenterId = preferredRecyclingCenterId
)

private fun parsePreferredLocationList(json: String): List<PreferredLocation> {
    val array = JSONArray(json)
    return List(array.length()) { i ->
        val obj = array.getJSONObject(i)
        PreferredLocation(
            id = obj.optString("id").ifEmpty { UUID.randomUUID().toString() },
            latitude = obj.getDouble("lat"),
            longitude = obj.getDouble("lng"),
            label = obj.optString("label")
        )
    }
}

private fun serializePreferredLocationList(locations: List<PreferredLocation>): String {
    val array = JSONArray()
    locations.forEach { location ->
        array.put(JSONObject().apply {
            put("id", location.id)
            put("lat", location.latitude)
            put("lng", location.longitude)
            put("label", location.label)
        })
    }
    return array.toString()
}

private fun parseScheduleList(json: String): List<Schedule> {
    val array = JSONArray(json)
    return List(array.length()) { i ->
        val obj = array.getJSONObject(i)
        Schedule(
            dayOfWeek = DayOfWeek.valueOf(obj.getString("dayOfWeek")),
            startTime = obj.getString("startTime"),
            endTime = obj.getString("endTime")
        )
    }
}

private fun serializeScheduleList(schedules: List<Schedule>): String {
    val array = JSONArray()
    schedules.forEach { schedule ->
        array.put(JSONObject().apply {
            put("dayOfWeek", schedule.dayOfWeek.name)
            put("startTime", schedule.startTime)
            put("endTime", schedule.endTime)
        })
    }
    return array.toString()
}
