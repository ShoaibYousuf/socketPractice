package com.example.socketpractice

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.net.URISyntaxException


const val URL = "https://chat-api-stage.bazaarghar.com/chat/v1/message"

object SocketInstance : Application() {
    lateinit var mInboxSocket: Socket
    private lateinit var networkManager: NetworkManager
    private val RECONNECTION_ATTEMPT = 10
    private val CONNECTION_TIMEOUT: Long = 30000

    @Synchronized
    fun setSocket() {
        try {
            val options = IO.Options()
            options.auth = getParam()
            options.timeout = CONNECTION_TIMEOUT
            options.reconnection = true
            options.reconnectionAttempts = RECONNECTION_ATTEMPT
            options.reconnectionDelay = 1000
            options.path =  "/socket.io/"
            options.transports = arrayOf(WebSocket.NAME)
            val okHttpClient = OkHttpClient()
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient)
            IO.setDefaultOkHttpCallFactory(okHttpClient)
            options.callFactory = okHttpClient
            options.webSocketFactory = okHttpClient
            mInboxSocket = IO.socket(URL, options)



            mInboxSocket.on(Socket.EVENT_CONNECT) {
                Log.d("SocketConnection", "Socket connected")
                mInboxSocket.on("allUnread") {
                    val messageJson = JSONObject(it[0].toString())
                    Log.d("allUnread", "$messageJson")
                }
                getChineseBellNotification()
                mInboxSocket.off("allNotifications")
            }





            mInboxSocket.on(Socket.EVENT_CONNECT_ERROR) { args ->
                val errorMessage = args.getOrNull(0)?.toString() ?: "Unknown error"
                Log.e("SocketConnection", "Connect error: $errorMessage")
            }

            // Handle other events as needed
        } catch (e: URISyntaxException) {
            // Handle the exception
            Log.e("SocketConnection", "Error creating socket: ${e.message}")
        }
    }
    var allUnreadResponseListener: AllUnreadResponseListener? = null

    /**
     * The purpose of this method is to connect with the socket for inbox
     */
    private fun makeConnectionForAuthInbox() {
        registerConnectionInBoxAttributes()
        mInboxSocket.connect()
    }
    private fun registerConnectionInBoxAttributes() {
        try {
            mInboxSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectionError)
            mInboxSocket.on(Socket.EVENT_DISCONNECT, onServerDisconnect)
            mInboxSocket.on(Socket.EVENT_CONNECT, onServerConnect)

            registerHandlersInBox()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    private fun registerHandlersInBox() {
        try {
            mInboxSocket.on("allUnread", allUnreadCallback)/*
            mInboxSocket.on("room-join", joinRoomInboxCallback)
            mInboxSocket.on("newChatMessage", chatMessageCallback)*/
//            mInboxSocket.io().on(Manager.EVENT_TRANSPORT, inboxCallback)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun sendAllUnreadEvent(request: Any) {
        try {
            if (mInboxSocket.connected()) {
                mInboxSocket.emit("allUnread", request)
            }
        } catch (e: java.lang.Exception) {

        }
    }

    private fun getChineseBellNotification() {
        try {
            // register chat events and callback
            val requestObject = JSONObject()
            requestObject.put("page", 1)
            requestObject.put("limit", 10)
            mInboxSocket.emit("allNotifications", requestObject)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getParam(): Map<String, String> {
        // Posting parameters to login url
        val params: MutableMap<String, String> = HashMap()
        params["token"] = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2MzE5ZDlhNzA5Y2FhMzA0ZWZmYzQ3MDAiLCJpYXQiOjE3MDMyMjk5NzYsImV4cCI6MTcwMzMxNjM3Nn0.Mi62MQZE6iT_HSNJVwdQ9JT16j0RbdhcU5m8SzM7XQQ"
        Log.e("AuthToken", params.toString())
        return params
    }


    @Synchronized
    fun getSocket(): Socket {
        return mInboxSocket
    }

    @Synchronized
    fun establishConnection() {
        mInboxSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mInboxSocket.disconnect()
    }

    private val onConnectionError =
        Emitter.Listener { args ->
            Log.e("Socket Response", "onConnectionError>> ${args[0]}")
            networkManager.onConnectionError(
                0,
                args
            )
        }

    private val onServerDisconnect =
        Emitter.Listener { args ->
            Log.e("Socket Response", "onServerDisconnection")
            networkManager.onServerDisconnection(
                2,
                args
            )
        }

    private val onServerConnect =
        Emitter.Listener { args ->
            Log.e("Socket Response", "onServerConnected >>> ")
            networkManager.networkCallReceive(
                1,
                args
            )
        }

    private val allUnreadCallback =
        Emitter.Listener { args ->
            try {
                val gson = Gson()
                val typeToken = object : TypeToken<List<InboxModel2>>() {}.type
                val allUnreadResponse =
                    gson.fromJson<List<InboxModel2>>(args[0].toString(), typeToken)
                if (allUnreadResponseListener != null) {
                    allUnreadResponseListener!!.onAllUnreadResponse(allUnreadResponse)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("Response allUnread", e.printStackTrace().toString())
            }

        }



}


