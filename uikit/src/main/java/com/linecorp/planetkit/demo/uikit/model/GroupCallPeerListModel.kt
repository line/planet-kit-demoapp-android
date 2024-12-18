package com.linecorp.planetkit.demo.uikit.model

import com.linecorp.planetkit.session.conference.PlanetKitConferencePeerListUpdatedParam
import com.linecorp.planetkit.session.conference.subgroup.PlanetKitConferencePeer

class GroupCallPeerListModel {

    private val _peerList = ArrayList<PlanetKitConferencePeer>()
    val peerList: List<PlanetKitConferencePeer>
        get() = _peerList.toList()

    private val _listenerList = ArrayList<Listener>()

    internal fun update(param: PlanetKitConferencePeerListUpdatedParam) {
        _peerList.addAll(param.addedPeers)
        _peerList.removeAll(param.removedPeers.toSet())

        _listenerList.forEach {
            it.onAdded(param.addedPeers)
            it.onRemoved(param.removedPeers)
            it.onListUpdated(peerList)
        }
    }

    fun clear() {
        _peerList.clear()
        _listenerList.clear()
    }

    fun addListener(listener: Listener) {
        _listenerList.add(listener)
    }

    fun removeListener(listener: Listener) {
        _listenerList.remove(listener)
    }

    interface Listener {
        fun onListUpdated(list: List<PlanetKitConferencePeer>)
        fun onAdded(list: List<PlanetKitConferencePeer>)
        fun onRemoved(list: List<PlanetKitConferencePeer>)
    }
}