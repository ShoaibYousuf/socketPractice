package com.example.socketpractice

interface NetworkManager {
    fun networkCallReceive(responseType: Int, args: Any)
    fun onConnectionError(error: Int, args: Any)
    fun onServerDisconnection(error: Int, args: Any)
}