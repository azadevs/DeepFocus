package com.azadevs.deepfocus.domain.model

enum class AmbientSoundMode(val title: String) {
    NONE("None"),
    RAIN("Rain"),
    FOREST("Forest"),
    CAFE("Cafe"),
    WHITE_NOISE("White Noise");
    
    companion object {
        fun fromString(value: String): AmbientSoundMode {
            return entries.find { it.name == value } ?: NONE
        }
    }
}
