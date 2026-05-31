data class Folder(
    val id: String = "",
    val name : String,
    val vocabularyCnt: Int = 0,
    val color: String,
    val priority: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

