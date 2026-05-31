package com.example.efishapp.feature.folder.domain.model

enum class Topic(
    val displayName: String,
    val iconName: String,
    val colorHex: Long
) {
    TRAVEL("Du lịch", "airplane", 0xFF4FC3F7),
    BUSINESS("Công việc", "work", 0xFF78909C),
    DAILY_LIFE("Đời thường", "home", 0xFFFFB74D),
    TECHNOLOGY("Công nghệ", "computer", 0xFF9575CD),
    FOOD("Ẩm thực", "restaurant", 0xFFE57373),
    HEALTH("Sức khỏe", "medical", 0xFFEF5350),
    EDUCATION("Học tập", "school", 0xFF81C784),
    SPORTS("Thể thao", "sports", 0xFFFF8A65),
    SCIENCE("Khoa học", "science", 0xFF7E57C2),
    MUSIC("Âm nhạc", "music", 0xFFEC407A),
    ART("Nghệ thuật", "art", 0xFFFFD54F),
    CUSTOM("Tùy chỉnh", "folder", 0xFF4C58BA);

    companion object {
        fun fromString(value: String): Topic {
            return entries.find { it.name == value } ?: CUSTOM
        }
    }
}
