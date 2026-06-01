package pt.isel.ipw.services.results

import pt.isel.ipw.domain.DTO.output.prove.CreateProveUploadUrlResponse
import pt.isel.ipw.domain.DTO.output.prove.ProveAccessUrlResponse
import pt.isel.ipw.domain.process.Prove
import pt.isel.ipw.services.errors.Either
import pt.isel.ipw.services.errors.ProveError


typealias CreateProveUploadUrlResult = Either<ProveError, CreateProveUploadUrlResponse>
typealias CreateProveResult = Either<ProveError, Int>
typealias GetProcessProvesResult = Either<ProveError, List<Prove>>
typealias GetProveAccessUrlResult = Either<ProveError, ProveAccessUrlResponse>
typealias DeleteProveResult = Either<ProveError, Unit>