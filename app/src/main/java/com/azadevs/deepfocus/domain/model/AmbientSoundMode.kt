package com.azadevs.deepfocus.domain.model

enum class AmbientSoundMode(val title: String) {
    NONE("None"),
    RAIN("Rain"),
    FOREST("Forest"),
    CAFE("Cafe"),
    FIRE("Fire");
    
    companion object {
        fun fromString(value: String): AmbientSoundMode {
            return when (value.lowercase()) {
                "rain" -> RAIN
                "forest" -> FOREST
                "cafe" -> CAFE
                "fire" -> FIRE
                else -> NONE
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            RAIN -> "rain"
            FOREST -> "forest"
            CAFE -> "cafe"
            FIRE -> "fire"
            NONE -> "none"
        }
    }
}
