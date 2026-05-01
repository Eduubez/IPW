package pt.isel.ipw.domain.Entities.area

data class AreaView(
    val id: Int,
    val name: String,
    val bossId: Int?,
    val bossName: String?
)
