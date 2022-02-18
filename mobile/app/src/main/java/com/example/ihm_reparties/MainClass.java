package com.example.ihm_reparties;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainClass extends AppCompatActivity {

    private static class EchoWebSocketListener extends WebSocketListener {
        private static final int CLOSE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            System.out.println("Socket opened");
        }
        @Override
        public void onMessage(WebSocket webSocket, String message) {
            System.out.println("Receive Message: " + message);
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            System.out.println("Receive Bytes : " + bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(CLOSE_STATUS, null);
            System.out.println("Closing Socket : " + code + " / " + reason);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
            System.out.println("Error : " + throwable.getMessage());
        }
    }

    public static void main(String[] args) {
        OkHttpClient mClient;
        mClient = new OkHttpClient();
        Request request = new Request.Builder().url("ws://localhost:4000").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket webSocket = mClient.newWebSocket(request, listener);
        webSocket.send("test");

    }
}
