package com.mevron.rides.rider.domain

sealed interface DomainModel {
    data class Success(val data: Any) : DomainModel
    data class Error(val error: Throwable) : DomainModel
}