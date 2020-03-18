package com.cormicopiastudios.multiplayerpractice;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.net.ExtendedNet;

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

	public HashMap<Integer, PlayerPos> m = new HashMap<>();

	public int tid;

	private int i = 0;

	@Override
	public void create() {
		Gdx.graphics.setWindowedMode(900,720);
		batch = new SpriteBatch();
		socket = ExtendedNet.getNet().newWebSocket("127.0.0.1", 8765);
		socket.addListener(getListener());
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

	private WebSocketListener getListener() {
		return new AbstractWebSocketListener() {

			@Override
			public boolean onOpen(final WebSocket webSocket) {
				System.out.println("Connected");
				message = "Connected!";
				final MessageObject messageO = new MessageObject();
				messageO.message = "This is a new message " + i;
				webSocket.send(messageO);
//				final PersonObject myMessage = new PersonObject();
//				webSocket.send(myMessage);
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
					message = jsonMessage.name + "!";
					tid = jsonMessage.thread;
					gameMaster.instance.setPlayerTid(tid);

				}
				if (packet instanceof MessageObject) {
					final MessageObject me = (MessageObject)packet;
					if (me.isLeaving) {
						System.out.println("Client has left");
						gameMaster.instance.removeRemotePlayer(me.id);
					} else {
						// add it
						gameMaster.instance.createPlayerCharacter(me.id);
						m.put(Integer.valueOf(me.id), new PlayerPos());
					}

//					System.out.println(me.message);

				}

				if (packet instanceof ServerState) {
					ServerState temp = (ServerState)packet;
					SharedUtils.stringToMap(m,temp.json);

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
