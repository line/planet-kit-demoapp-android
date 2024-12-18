package com.linecorp.planetkit.demo.uikit.repositories

import android.content.Context

class PlanetKitOneOnOneCallRepositoryContainer(val context: Context) {
    private val callRepositories = mutableMapOf<Int, PlanetKitOneOnOneCallRepository>()

    @Synchronized
    fun getRepository(instanceId: Int): PlanetKitOneOnOneCallRepository? {
        return callRepositories[instanceId]
    }

    @Synchronized
    fun setRepository(instanceId: Int, repository: PlanetKitOneOnOneCallRepository) {
        callRepositories[instanceId] = repository
    }

    @Synchronized
    fun removeRepository(instanceId: Int) {
        callRepositories.remove(instanceId)
    }
}