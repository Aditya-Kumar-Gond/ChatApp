package com.example.chatapp;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class runSocket extends WebSocketListener {
    Activity activity;
    public runSocket(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject object = new JSONObject();
                try {
                    object.put("message",text);
                    object.put("byServer",true);
                }
                catch (JSONException e){
                    Toast.makeText(activity, "Problem with parsing json object", Toast.LENGTH_SHORT).show();
                    Log.e("Error", "problem occured during parsing object");
                }
            }
        });
        super.onMessage(webSocket, text);
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "connection made", Toast.LENGTH_SHORT).show();
            }
        });
        super.onOpen(webSocket, response);
    }
}
