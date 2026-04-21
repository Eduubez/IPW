package pt.isel.ipw.domain.process

data class Location(
    val id: Int,
    val district: String,
    val county: String,
    val street: String,
    val latitude: String,
    val longitude: String,
)