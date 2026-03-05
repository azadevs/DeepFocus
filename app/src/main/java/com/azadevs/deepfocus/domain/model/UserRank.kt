package com.azadevs.deepfocus.domain.model

enum class UserRank(val minMinutes: Int, val label: String) {
    STARGAZER(0, "Stargazer"),
    NEBULA_VOYAGER(60, "Nebula Voyager"),
    GALACTIC_MASTER(300, "Galactic Master"),
    UNIVERSE_ARCHITECT(1200, "Universe Architect");

    companion object {
        fun fromMinutes(minutes: Int): UserRank {
            return entries.toTypedArray().findLast { minutes >= it.minMinutes } ?: STARGAZER
        }
    }
}
