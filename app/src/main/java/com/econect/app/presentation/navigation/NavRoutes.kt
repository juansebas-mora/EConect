package com.econect.app.presentation.navigation

object NavRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Destinos raíz de cada tipo de usuario.
    // Se reemplazarán con nested graphs cuando existan las pantallas.
    const val CITIZEN_HOME = "citizen_home"
    const val RECYCLER_HOME = "recycler_home"

    // Ciudadano
    const val CITIZEN_PROFILE = "citizen_profile"
    const val ADD_MATERIAL = "add_material"
    const val MATERIAL_LIST = "material_list"

    // Reciclador
    const val RECYCLER_PROFILE = "recycler_profile"
    const val ROUTE_DETAIL = "route_detail/{routeId}"
    fun routeDetail(routeId: String) = "route_detail/$routeId"

    const val AVAILABLE_MATERIALS = "available_materials"
}
