package com.econect.app.data.model

import com.econect.app.data.local.db.entity.RouteEntity
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.Route
import com.econect.app.domain.model.RouteStatus
import com.econect.app.domain.model.RouteStop
import org.json.JSONArray
import org.json.JSONObject

fun RouteEntity.toDomain(): Route = Route(
    id = id,
    recyclerId = recyclerId,
    stops = parseRouteStops(stopsJson),
    date = date,
    status = RouteStatus.valueOf(status)
)

fun Route.toEntity(): RouteEntity = RouteEntity(
    id = id,
    recyclerId = recyclerId,
    date = date,
    status = status.name,
    stopsJson = serializeRouteStops(stops)
)

private fun parseRouteStops(json: String): List<RouteStop> {
    val array = JSONArray(json)
    return List(array.length()) { i ->
        val obj = array.getJSONObject(i)
        RouteStop(
            materialId = obj.getString("materialId"),
            citizenId = obj.getString("citizenId"),
            scheduledTime = obj.getString("scheduledTime"),
            location = LatLng(
                latitude = obj.getDouble("lat"),
                longitude = obj.getDouble("lng")
            ),
            // Compatibilidad retroactiva: campo ausente en datos anteriores → OTHER
            materialType = if (obj.has("materialType"))
                runCatching { MaterialType.valueOf(obj.getString("materialType")) }
                    .getOrDefault(MaterialType.OTHER)
            else MaterialType.OTHER
        )
    }
}

private fun serializeRouteStops(stops: List<RouteStop>): String {
    val array = JSONArray()
    stops.forEach { stop ->
        array.put(JSONObject().apply {
            put("materialId", stop.materialId)
            put("citizenId", stop.citizenId)
            put("scheduledTime", stop.scheduledTime)
            put("lat", stop.location.latitude)
            put("lng", stop.location.longitude)
            put("materialType", stop.materialType.name)
        })
    }
    return array.toString()
}
