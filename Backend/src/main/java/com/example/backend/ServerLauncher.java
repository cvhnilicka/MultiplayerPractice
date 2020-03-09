package com.example.backend;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;


import com.example.shared.PersonObject;
import com.github.czyzby.websocket.serialization.Serializer;
import com.github.czyzby.websocket.serialization.impl.Base64Serializer;
import com.github.czyzby.websocket.serialization.impl.JsonSerializer;

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



    public static void main(final String... args) throws Exception {
        new ServerLauncher().launch();
    }

    public ServerLauncher() {
        serializer = new JsonSerializer();
    }

    private void launch() {
        System.out.println("Launching web socket server...");
        final HttpServer server = vertx.createHttpServer();
        server.websocketHandler(webSocket -> {
            // Printing received packets to console, sending response:
            webSocket.frameHandler(frame -> handleFrame(webSocket, frame));
            // Closing the socket in 5 seconds:
            vertx.setTimer(5000L, id -> webSocket.close());
        }).listen(8000);
    }

    private void handleFrame(final ServerWebSocket webSocket, final WebSocketFrame frame) {
        // Deserializing received message:
        final Object request = serializer.deserialize(frame.binaryData().getBytes());
        if (request instanceof PersonObject) {
            System.out.println("Received message: " + ((PersonObject) request).name);
        }

        // Sending a simple response message after 1 second:
        final PersonObject response = new PersonObject();
        response.id = idCounter.getAndIncrement();
        response.name = "Hello client ";
        vertx.setTimer(1000L, id -> webSocket.writeFinalBinaryFrame(Buffer.buffer(serializer.serialize(response))));
    }
}