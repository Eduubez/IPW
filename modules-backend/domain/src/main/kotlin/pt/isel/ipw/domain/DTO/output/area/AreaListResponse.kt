package pt.isel.ipw.domain.DTO.output.area

import pt.isel.ipw.domain.Entities.area.AreaView

data class AreaListResponse(
    val areas: List<AreaView>
)
