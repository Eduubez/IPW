package pt.isel.ipw.services.auth

class InvalidTokenException : RuntimeException()

class ExpiredLoginTokenException : RuntimeException()
class ExpiredAccessTokenException : RuntimeException()
class ExpiredRefreshTokenException : RuntimeException()