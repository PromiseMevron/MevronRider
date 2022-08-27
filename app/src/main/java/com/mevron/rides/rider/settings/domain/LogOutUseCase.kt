package com.mevron.rides.rider.settings.domain

import javax.inject.Inject

class LogOutUseCase @Inject constructor(private val repository: ISettingsRepo) {
    suspend operator fun invoke() = repository.signOut()
}