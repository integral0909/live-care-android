package com.onseen.livecare.models.Utils

class DispatchGroupManager {
    var count: Int = 0
    var runnable: Runnable? = null

    constructor() {
        count = 0
    }

    @Synchronized fun enter() {
        count ++
    }
    @Synchronized fun leave() {
        count --
        notifyGroup()
    }

    fun notify(r: Runnable) {
        runnable = r
        notifyGroup()
    }

    fun notifyGroup() {
        if(count <= 0 && runnable != null) {
            runnable!!.run()
        }
    }
}