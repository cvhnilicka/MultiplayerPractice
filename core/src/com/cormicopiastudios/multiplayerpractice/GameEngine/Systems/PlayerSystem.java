package com.cormicopiastudios.multiplayerpractice.GameEngine.Systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Components.PlayerComponent;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Views.PlayScreen;
import com.example.shared.PlayerPos;

import java.util.Map;

public class PlayerSystem extends IntervalIteratingSystem {
    private static final float MAX_STEP_TIME = 1/45f;
    ComponentMapper<PlayerComponent> pm;

    private Array<Entity> playerqueue;
    PlayScreen parent;

    public PlayerSystem(PlayScreen parent){
        super(Family.all(PlayerComponent.class).get(), MAX_STEP_TIME);
        pm = ComponentMapper.getFor(PlayerComponent.class);
        playerqueue = new Array<Entity>();
        this.parent = parent;
    }

    @Override
    protected void processEntity(Entity entity) {
        playerqueue.add(entity);
    }

    public void updateInterval() {
        super.updateInterval();
        Array<Long> tids = new Array<Long>();
        for (Entity e : playerqueue) {
            tids.add(Long.valueOf(e.getComponent(PlayerComponent.class).tid));
        }
        for(Map.Entry<Long, PlayerPos> e : parent.gameMaster.parent.m.entrySet()) {
            if(!tids.contains(e.getKey(), false)) {
                parent.createPlayerCharacter(e.getKey());
            }
        }
        playerqueue.clear();
    }
}
