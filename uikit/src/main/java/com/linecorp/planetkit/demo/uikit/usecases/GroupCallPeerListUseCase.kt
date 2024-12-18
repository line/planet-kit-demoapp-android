package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.demo.uikit.model.GroupCallPeerListModel
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitGroupCallRepository
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitUser
import com.linecorp.planetkit.session.conference.subgroup.PlanetKitConferencePeer

class GroupCallPeerListUseCase(
    private val planetKitConferenceRepository: PlanetKitGroupCallRepository
): UseCase {
    private var listener: Listener? = null
    private val peerListModel = planetKitConferenceRepository.peerListModel

    private val peerListListener = object :GroupCallPeerListModel.Listener{
        override fun onListUpdated(list: List<PlanetKitConferencePeer>) {
            listener?.onListUpdated(list.map { UIKitUser(it.userId, it.serviceId, it.displayName)})
        }

        override fun onAdded(list: List<PlanetKitConferencePeer>) {
            listener?.onAdded(list.map { UIKitUser(it.userId, it.serviceId, it.displayName)})
        }

        override fun onRemoved(list: List<PlanetKitConferencePeer>) {
            listener?.onRemoved(list.map { UIKitUser(it.userId, it.serviceId, it.displayName)})
        }
    }

    init {
        peerListModel.addListener(peerListListener)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
        val list = peerListModel.peerList
        if (list.isNotEmpty()) {
            listener.onListUpdated(list.map { UIKitUser(it.userId, it.serviceId, it.displayName) })
        }
    }

    interface Listener {
        fun onListUpdated(list: List<UIKitUser>)
        fun onAdded(list: List<UIKitUser>)
        fun onRemoved(list: List<UIKitUser>)
    }

    override fun onCleared() {
        peerListModel.removeListener(peerListListener)
        listener = null
    }
}