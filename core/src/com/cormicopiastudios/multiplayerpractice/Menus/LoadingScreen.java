package com.cormicopiastudios.multiplayerpractice.Menus;

import com.badlogic.gdx.Screen;
import com.cormicopiastudios.multiplayerpractice.MultiplayerPractice;

public class LoadingScreen implements Screen {

    private MultiplayerPractice parent;

    public LoadingScreen(MultiplayerPractice parent) { this.parent = parent; }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        this.parent.changeScreen(MultiplayerPractice.MAINMENU);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
