package com.example.backend;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;


import com.badlogic.gdx.math.Vector2;
import com.example.shared.MessageObject;
import com.example.shared.PersonObject;
import com.github.czyzby.websocket.serialization.Serializer;
import com.github.czyzby.websocket.serialization.impl.Base64Serializer;
import com.github.czyzby.websocket.serialization.impl.JsonSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;


//Note that server web socket implementation is not provided by gdx-websocket. This class uses an external library: Vert.x.
public class ServerLauncher {

    private final Serializer serializer;
    private final AtomicInteger idCounter = new AtomicInteger();
    private final Vertx vertx = Vertx.vertx();

    public static int numClients = 0;

    public static Map<Long, Vector2> m = Collections.synchronizedMap(new HashMap<>());



    public static void main(final String... args) throws Exception {
        new ServerLauncher().launch();
    }

    public ServerLauncher() {
        serializer = new JsonSerializer();
    }

    public static void updateNumClient(int change) {
        numClients += change;
    }

    public static int getNumClients() {
        return numClients;
    }

    private void launch() {
        System.out.println("Launching web socket server...");
        final HttpServer server = vertx.createHttpServer();
        server.websocketHandler(webSocket -> {
            // Printing received packets to console, sending response:
//            webSocket.frameHandler(frame -> handleFrame(webSocket, frame));


            // So i am wondering if i can create classes/function handlers to handle different incoming requests
            // i can spawn/use threads then to process the incoming requests.

//            1. Initialize the server socket
//            2. Wait for a client to connect
//            3. Accept the client connection
//            4. Create a daemon thread to support the client
//            Go back to step 2.


            Thread t = new ClientHandler(webSocket,serializer,idCounter,vertx);
            m.put(t.getId(),new Vector2());
            t.start();

        }).listen(8000);

    }

    public static void updateMap(long id, float x, float y) {
        m.get(id).x = x;
        m.get(id).y = y;
    }

}