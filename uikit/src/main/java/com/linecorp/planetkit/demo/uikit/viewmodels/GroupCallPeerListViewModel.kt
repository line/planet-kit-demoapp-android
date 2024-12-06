package com.linecorp.planetkit.demo.uikit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitUser
import com.linecorp.planetkit.demo.uikit.usecases.GroupCallPeerListUseCase

class GroupCallPeerListViewModel(private val peerListUseCase: GroupCallPeerListUseCase): ViewModel() {
    private val _peerList = MutableLiveData<List<UIKitUser>?>(null)
    val peerList: LiveData<List<UIKitUser>?>
        get() = _peerList

    private val _addedPeerList = MutableLiveData<List<UIKitUser>?>(null)
    val addedPeerList: LiveData<List<UIKitUser>?>
        get() = _addedPeerList

    private val _removedPeerList = MutableLiveData<List<UIKitUser>?>(null)
    val removedPeerList: LiveData<List<UIKitUser>?>
        get() = _removedPeerList

    init {
        peerListUseCase.setListener (object: GroupCallPeerListUseCase.Listener{
            override fun onListUpdated(list: List<UIKitUser>) {
                _peerList.postValue(list)
            }

            override fun onAdded(list: List<UIKitUser>) {
                _addedPeerList.postValue(list)
            }

            override fun onRemoved(list: List<UIKitUser>) {
                _removedPeerList.postValue(list)
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        peerListUseCase.onCleared()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as UiKitApplication).appContainer.groupCallRepository
                GroupCallPeerListViewModel(GroupCallPeerListUseCase(repository))
            }
        }
    }
}