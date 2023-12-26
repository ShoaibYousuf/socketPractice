package com.example.socketpractice

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.Socket
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var inboxData = ArrayList<InboxModel2>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SocketInstance.setSocket()
        val mSocket = SocketInstance.getSocket()

        // Ensure that the socket is connected before listening for events
        if (!mSocket.connected()) {
            SocketInstance.establishConnection()
        }

        mSocket.on("connect") {
            Log.e("Connected", "Socket is connected")
            runOnUiThread {
                Toast.makeText(this, "Socket is connected", Toast.LENGTH_SHORT).show()
            }

            val json = JSONObject()
            json.put("userId", "6319d9a709caa304effc4700")
            SocketInstance.allUnreadResponseListener = object : AllUnreadResponseListener {
                override fun onAllUnreadResponse(allUnreadResponse: List<InboxModel2>) {
                    inboxData.clear()
                    inboxData.addAll(allUnreadResponse)
//                    notifyData()
                }
            }
            SocketInstance.sendAllUnreadEvent(json)
        }

        mSocket.on(Socket.EVENT_CONNECT_ERROR) { args: Array<out Any?> ->
            val errorMessage = args.getOrNull(0)?.toString() ?: "Unknown error"
            Log.e("ConnectionError", "Socket connection error: $errorMessage")
            runOnUiThread {
                Toast.makeText(this, "Socket connection error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }

        mSocket.on(Socket.EVENT_DISCONNECT) {
            Log.e("Disconnected", "Socket is disconnected")
            runOnUiThread {
                Toast.makeText(this, "Socket is disconnected", Toast.LENGTH_SHORT).show()
            }
        }

        // Check if the socket is connected before showing the toast
        if (mSocket.connected()) {
            Toast.makeText(this, "Socket is connected", Toast.LENGTH_SHORT).show()
            Log.e("Connected", "onCreate: Socket is connected ")
        }
    }
}
