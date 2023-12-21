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
    ArrayList<String> arrayData = new ArrayList<>();


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

        send.setOnClickListener(view -> {
            if(!(message.getText().length() ==0)){
                String msg = message.getText().toString();
                Toast.makeText(MainActivity.this, message.getText(), Toast.LENGTH_SHORT).show();
                arrayData.add(msg);
                Log.i("Array Log", "Array:"+arrayData);
                MessageAdapter adapter = new MessageAdapter(arrayData);
                rv.setAdapter(adapter);
                scrollView.scrollTo(arrayData.size(),arrayData.size());
            }else{
                message.setError("Empty");
               // Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_SHORT).show();
            }
        });


    }
    void runSocket(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("").build();

        WebSocket webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Toast.makeText(MainActivity.this, "Connection closed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                super.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Toast.makeText(MainActivity.this, "Connection made:"+response, Toast.LENGTH_SHORT).show();
            }
        });

    }

    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
        ArrayList<String> data = new ArrayList<>();

        public MessageAdapter(ArrayList<String> data){
            this.data = data;
        }


        @NonNull
        @Override
        public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_container_layout,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

                if(position%2 ==0){
                    holder.sender.setVisibility(View.GONE);
                    holder.receiver.setVisibility(View.VISIBLE);
                    holder.receiver.setText(data.get(position));
                }else {
                    holder.sender.setVisibility(View.VISIBLE);
                    holder.receiver.setVisibility(View.GONE);
                    holder.sender.setText(data.get(position));
                }
        }

        @Override
        public int getItemCount() {
            return data.size();
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