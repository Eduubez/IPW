package pt.isel.ipw.domain.DTO.output

data class LocationResponse(
    val id: Int,
    val district: String,
    val county: String,
    val street: String,
    val latitude: String,
    val longitude: String,
)



//fun Location.toResponse():LocationResponse