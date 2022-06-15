package com.onseen.livecare.models.Utils

class UtilsConfig {
    companion object {
        var enumAppEnvironment: EnumAppEnvironment = EnumAppEnvironment.SANDBOX
        var shouldLogForDebugging: Boolean = true
        var shouldEnableFabric = true
        var shouldMockCurrentLocation: Boolean = true
        var Constants_kDispatcherPhoneNumber = ""

        // TODO: Check the Google map key in Manifest file
        val GOOGLE_MAP_API_KEY = "AIzaSyAOMAR_viNda92ElBBr7Nnf1J4k_kOmu1c"
        val GOOGLE_DIRECTION_API_KEY = "AIzaSyBV7OSGeGGvCB-24MuD_7ZxWAO5UTH-2ms"
        val GOOGLE_PLACE_API_KEY = "AIzaSyAoqIjcNrO55NOrnekgjZnodil_Uks7_is"

    }
}