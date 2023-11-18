package com.example.freedrawing.mock


class FakeSocket(
    var endPoint: FakeSocket? = null,
    var dataListener: ((String) -> Unit)? = null
) {

    fun write(data: String) {
        endPoint?.dataListener?.invoke(data)
    }

    fun disconnectEndpoint() {
        endPoint = null
    }
}



