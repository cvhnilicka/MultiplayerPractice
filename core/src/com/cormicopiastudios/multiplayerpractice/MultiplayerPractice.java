package com.cormicopiastudios.multiplayerpractice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonValue;
import com.cormicopiastudios.multiplayerpractice.GameEngine.GameMaster;
import com.cormicopiastudios.multiplayerpractice.Menus.LoadingScreen;
import com.cormicopiastudios.multiplayerpractice.Menus.MainMenu;
import com.example.shared.MessageObject;
import com.example.shared.PersonObject;
import com.example.shared.PlayerPos;
import com.example.shared.ServerState;
import com.example.shared.SharedUtils;
import com.github.czyzby.websocket.AbstractWebSocketListener;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.google.gwt.core.client.JsonUtils;


import com.google.gwt.json.client.*;


import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class MultiplayerPractice extends Game {
	private SpriteBatch batch;
	private WebSocket socket;


	private String message = "Connecting...";


	private LoadingScreen loadingScreen;
	public final static int LOADING = 1;

	private MainMenu mainMenu;
	public final static int MAINMENU = 2;

	private GameMaster gameMaster;
	public final static int GAME = 3;

	public Map<Long, PlayerPos> m = new HashMap<>();

	public Long tid;

	private int i = 0;

	@Override
	public void create() {
		batch = new SpriteBatch();
		// Note: you can also use WebSockets.newSocket() and WebSocket.toWebSocketUrl() methods.
		socket = ExtendedNet.getNet().newWebSocket("127.0.0.1", 8765);
//		socket = WebSockets.newSocket("http://127.0.0.1:8000");
		socket.addListener((WebSocketListener) getListener());
		changeScreen(LOADING);
		m = new HashMap<>();

	}

	public void connectSocket() {
		socket.connect();
		mainMenu = new MainMenu(this);
		this.setScreen(mainMenu);
	}

	public void sendMessage() {
		final MessageObject message = new MessageObject();
		message.message = "This is a new message " + i;
		socket.send(message);
		final ServerState getState = new ServerState();
		getState.numClients = -1;
		socket.send(getState);
		i++;
	}

	public WebSocket getSocket() {
		return socket;
	}

	private AbstractWebSocketListener getListener() {
		return new AbstractWebSocketListener() {

			@Override
			public boolean onOpen(final WebSocket webSocket) {
				System.out.println("Connected");
				message = "Connected!";
				final PersonObject myMessage = new PersonObject();
				myMessage.name = "Cormick";
				webSocket.send(myMessage);
				return FULLY_HANDLED;
			}

			@Override
			public boolean onClose(final WebSocket webSocket, final WebSocketCloseCode code, final String reason) {
				message = "Disconnected!";
				System.out.println("Disconnected");
				return FULLY_HANDLED;
			}

			@Override
			protected boolean onMessage(final WebSocket webSocket, final Object packet) {
				if (packet instanceof PersonObject) {
					final PersonObject jsonMessage = (PersonObject) packet;
					message = jsonMessage.name + jsonMessage.id + "!";
					tid = jsonMessage.thread;
				}
				if (packet instanceof MessageObject) {
					final MessageObject me = (MessageObject)packet;
					gameMaster.instance.createPlayerCharacter(me.id);
					System.out.println(me.message);
					m.put(Long.valueOf(me.id), new PlayerPos());
				}
				if (packet instanceof ServerState) {
					ServerState temp = (ServerState)packet;
					SharedUtils.stringToMap(m,temp.json);
//					System.out.println(m);
				}
				return FULLY_HANDLED;
			}
		};
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		WebSockets.closeGracefully(socket); // Null-safe closing method that catches and logs any exceptions.
		batch.dispose();
	}

	public Map getM() {return m;}




	public void changeScreen(int screen) {
		switch (screen) {
			// need to add cases
			case LOADING: if (loadingScreen == null) loadingScreen = new LoadingScreen(this);
				this.setScreen(loadingScreen);
				break;
			case MAINMENU: if (mainMenu == null) mainMenu = new MainMenu(this);
				this.setScreen(mainMenu);
				break;
			case GAME: gameMaster = new GameMaster(this, socket);
				break;

		}
	}
}
