package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText message;
    ImageView send,attach;
    RecyclerView rv;
    NestedScrollView scrollView;
    ArrayList<JSONObject> arrayData = new ArrayList<>();
    WebSocket webSocket;
    JSONObject object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = findViewById(R.id.msg);
        send = findViewById(R.id.send_btn);
        attach = findViewById(R.id.attach);
        rv = findViewById(R.id.rc_view);
        scrollView = findViewById(R.id.scrollChat);

        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        rv.setLayoutManager(manager);

        runSocket();

        MessageAdapter adapter = new MessageAdapter();
        rv.setAdapter(adapter);

        send.setOnClickListener(view -> {
            if(!(message.getText().length() ==0)){
                String msgContent = message.getText().toString();
                webSocket.send(msgContent);

                object = new JSONObject();
               // Toast.makeText(MainActivity.this, message.getText(), Toast.LENGTH_SHORT).show();
                try {
                    object.put("Message",msgContent);
                    object.put("byServer",false);
                    arrayData.add(object);
                    MessageAdapter adapter1 = new MessageAdapter();
                    rv.setAdapter(adapter1);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Log.i("Array Log", "Array:"+arrayData);


                //This will show latest type/received message at end of recyclerView
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
                rv.smoothScrollToPosition(adapter.getItemCount()-1);
            }else{
                message.setError("Empty");
               // Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_SHORT).show();
            }
        });


    }
    void runSocket(){
        Log.i("in socket", "runSocket: working");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("ws://192.168.177.168:8080").build();

        webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Log.e("WebSocket Failure", "Error: " + t.getMessage());
                super.onFailure(webSocket, t, response);
            }


            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.i("WebSocket", "onMessage: Message received - " + text);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         object = new JSONObject();
                        try {
                            object.put("Message",text);
                            object.put("byServer",true);
                            arrayData.add(object);
                            MessageAdapter adapter1 = new MessageAdapter();
                            rv.setAdapter(adapter1);
                        }
                        catch (JSONException e){
                            Toast.makeText(MainActivity.this, "Problem with parsing json object", Toast.LENGTH_SHORT).show();
                            Log.e("Error", "problem occurred during parsing object");
                        }
                    }
                });
                super.onMessage(webSocket, text);
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.i("WebSocket", "onOpen: Connection opened");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Websocket connection", "run: connected");
                        Toast.makeText(MainActivity.this, "connection made", Toast.LENGTH_SHORT).show();
                    }
                });
                super.onOpen(webSocket, response);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
            }
        });
    }

    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

        @NonNull
        @Override
        public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_container_layout,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
            JSONObject object1 = arrayData.get(position);
            try {
                Boolean byServer = object1.getBoolean("byServer");
                if(byServer){
                    holder.sender.setVisibility(View.GONE);
                    holder.receiver.setVisibility(View.VISIBLE);
                    holder.receiver.setText(object1.get("Message").toString());
                }else {
                    holder.receiver.setVisibility(View.GONE);
                    holder.sender.setVisibility(View.VISIBLE);
                    holder.sender.setText(object1.get("Message").toString());
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int getItemCount() {
            return arrayData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView sender,receiver;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                sender = itemView.findViewById(R.id.sender_tv);
                receiver = itemView.findViewById(R.id.receiver_tv);
            }
        }
    }


}