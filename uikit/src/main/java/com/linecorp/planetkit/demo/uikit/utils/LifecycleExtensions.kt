package com.linecorp.planetkit.demo.uikit.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

inline fun <T : Any, L : LiveData<T>> LifecycleOwner.observeNotNull(
        liveData: L,
        crossinline body: (T) -> Unit
): Observer<T> {
    val observer = Observer<T> { value -> value.let(body) }
    liveData.observe(this, observer)
    return observer
}

fun <T : Any?, L : LiveData<T>> LifecycleOwner.observe(
        liveData: L,
        body: (T?) -> Unit
): Observer<T?> {
    val observer = Observer<T?> { value -> body(value) }
    liveData.observe(this, observer)
    return observer
}
