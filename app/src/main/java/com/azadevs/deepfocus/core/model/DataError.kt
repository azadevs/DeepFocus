package com.azadevs.deepfocus.core.model

sealed class DataError : Throwable() {

    data object DatabaseError : DataError() {
        private fun readResolve(): Any = DatabaseError
    }

    data object UnknownError : DataError() {
        private fun readResolve(): Any = UnknownError
    }

}