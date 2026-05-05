package pt.isel.ipw.domain.process

import pt.isel.ipw.domain.DTO.output.LocationResponse

data class Location(
    val id: Int,
    val district: String,
    val county: String,
    val street: String,
    val latitude: String,
    val longitude: String,
)
