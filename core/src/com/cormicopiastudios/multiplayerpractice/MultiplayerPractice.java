package com.cormicopiastudios.multiplayerpractice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.shared.PersonObject;
import com.github.czyzby.websocket.AbstractWebSocketListener;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.net.ExtendedNet;
import java.net.URI;


public class MultiplayerPractice extends ApplicationAdapter {
	private SpriteBatch batch;
	private WebSocket socket;


	private String message = "Connecting...";

	@Override
	public void create() {
		batch = new SpriteBatch();
		// Note: you can also use WebSockets.newSocket() and WebSocket.toWebSocketUrl() methods.
		socket = ExtendedNet.getNet().newWebSocket("127.0.0.1", 8000);
//		socket = WebSockets.newSocket("http://127.0.0.1:8000");
		socket.addListener(getListener());
		socket.connect();
	}

	private AbstractWebSocketListener getListener() {
		return new AbstractWebSocketListener() {

			@Override
			public boolean onOpen(final WebSocket webSocket) {
				message = "Connected!";
				final PersonObject myMessage = new PersonObject();
				myMessage.name = "Cormick";
				webSocket.send(myMessage);
				return FULLY_HANDLED;
			}

			@Override
			public boolean onClose(final WebSocket webSocket, final WebSocketCloseCode code, final String reason) {
				message = "Disconnected!";
				return FULLY_HANDLED;
			}

			@Override
			protected boolean onMessage(final WebSocket webSocket, final Object packet) {
				if (packet instanceof PersonObject) {
					final PersonObject jsonMessage = (PersonObject) packet;
					message = jsonMessage.name + jsonMessage.id + "!";
				}
				return FULLY_HANDLED;
			}
		};
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.end();
	}

	@Override
	public void dispose() {
		WebSockets.closeGracefully(socket); // Null-safe closing method that catches and logs any exceptions.
		batch.dispose();
	}
}
