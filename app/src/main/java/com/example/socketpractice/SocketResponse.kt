package com.example.socketpractice

interface SocketResponse {
}

interface AllUnreadResponseListener {
    fun onAllUnreadResponse(
        allUnreadResponse: List<InboxModel2>
    )
}