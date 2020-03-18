package com.cormicopiastudios.multiplayerpractice.GameEngine.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class PlayerComponent implements Component {
    public OrthographicCamera cam = null;
    public int tid = -1;
    public boolean remote = false;
}
