package com.example.backend;

import com.badlogic.gdx.Gdx;
import com.example.shared.MessageObject;
import com.example.shared.PersonObject;
import com.example.shared.ServerState;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.serialization.Serializer;

import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

public class ClientHandler extends Thread {

    final ServerWebSocket wbs;
    final AtomicInteger idCounter;
    final Serializer serializer;
    final Vertx vertx;

    public ClientHandler(ServerWebSocket websocket, Serializer serializer, AtomicInteger idCounter, Vertx vertx) {
        this.wbs = websocket;
        this.idCounter = idCounter;
        this.serializer = serializer;
        this.vertx = vertx;
        ServerLauncher.updateNumClient(1);
        System.out.println("New Client Handler");
    }

    @Override
    public void run(){
        wbs.frameHandler( frame -> handleFrame(wbs,frame));

//        if (wbs.)
        wbs.closeHandler(v -> {
            System.out.println("The socket has been closed");
            ServerLauncher.updateNumClient(-1);
        });
    }

    private void handleSocketClosed(final ServerWebSocket webSocket) {
        System.out.println("Closing out socket");
        webSocket.end();
    }

    private void handleFrame(final ServerWebSocket webSocket, final WebSocketFrame frame) {
        // Deserializing received message:
        final Object request = serializer.deserialize(frame.binaryData().getBytes());
        if (request instanceof PersonObject) {
            System.out.println("Received PERSON: " + ((PersonObject) request).name);
        }
        if (request instanceof MessageObject) {
            System.out.println("Received MESSAGE: " + ((MessageObject) request).message);

        }

        if (request instanceof ServerState) {
            ServerState state = new ServerState();
            state.numClients = ServerLauncher.numClients;
            webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(state)));
        }

        // Sending a simple response message after 1 second:
        final PersonObject response = new PersonObject();
        response.id = idCounter.getAndIncrement();
        response.name = "Hello client ";
        webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(response)));
//        vertx.setTimer(1000L, id -> webSocket.writeFinalBinaryFrame(Buffer.buffer(serializer.serialize(response))));
    }

}
