package com.cormicopiastudios.multiplayerpractice.GameEngine;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Views.PlayScreen;
import com.cormicopiastudios.multiplayerpractice.MultiplayerPractice;
import com.example.shared.PlayerPos;
import com.example.shared.ServerState;
import com.github.czyzby.websocket.WebSocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameMaster {

    public MultiplayerPractice parent;
    public PlayScreen instance;

    public final static int V_WIDTH = 48;
    public final static int V_HIEGHT = 48;




    // collision bits
    public static final short PLATFORM_BIT = 1;
    public static final short PLAYER_BIT = 2;
    public static final short ASTEROID_BIT = 4;

    private WebSocket socket;

    public GameMaster(MultiplayerPractice parent, WebSocket socket) {
        this.parent = parent;
        this.socket = socket;
        socket.connect();
        instance = new PlayScreen(this);
        parent.setScreen(instance);

    }

    public void sendPosUpdate(Vector2 pos) {
        if (socket.isOpen()) {
            final PlayerPos posUpdate = new PlayerPos();
            posUpdate.x = pos.x;
            posUpdate.y = pos.y;
            parent.m.put(parent.tid, posUpdate);
            socket.send(posUpdate);
        }
    }

    public void updateLocalFromServer() {
        if (socket.isOpen()) {
            final ServerState getState = new ServerState();
            getState.numClients = -1;
            getState.json = "";
            socket.send(getState);
        }
    }
}
