package com.cormicopiastudios.multiplayerpractice.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cormicopiastudios.multiplayerpractice.MultiplayerPractice;
import com.github.czyzby.websocket.CommonWebSockets;

public class DesktopLauncher {
	public static void main (String[] arg) {
		CommonWebSockets.initiate();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new MultiplayerPractice(), config);
	}
}
