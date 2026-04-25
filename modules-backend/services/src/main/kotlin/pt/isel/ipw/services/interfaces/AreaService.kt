package pt.isel.ipw.services.interfaces

import pt.isel.ipw.domain.DTO.output.area.AreaListResponse
import pt.isel.ipw.domain.DTO.output.area.AreaViewResponse
import pt.isel.ipw.services.errors.AreaError
import pt.isel.ipw.services.errors.Either

interface AreaService{
    fun getAllAreas(): Either<AreaError, AreaListResponse>
    fun getAreaById(id: Int): Either<AreaError, AreaViewResponse>
    fun updateAreaBoss(id: Int, bossId: Int): Either<AreaError, AreaViewResponse>
}