package pt.isel.ipw.services.results

import pt.isel.ipw.domain.DTO.output.area.AreaListResponse
import pt.isel.ipw.domain.DTO.output.area.AreaViewResponse
import pt.isel.ipw.services.errors.AreaError
import pt.isel.ipw.services.errors.Either

typealias GetAllAreasResult = Either<AreaError, AreaListResponse>
typealias GetAreaByIdResult = Either<AreaError, AreaViewResponse>
typealias UpdateAreaBossResult = Either<AreaError, AreaViewResponse>
