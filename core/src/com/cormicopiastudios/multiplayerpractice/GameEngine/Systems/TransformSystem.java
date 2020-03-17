package com.cormicopiastudios.multiplayerpractice.GameEngine.Systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Components.BodyComponent;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Components.PlayerComponent;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Components.TransformComponent;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Views.PlayScreen;
import com.cormicopiastudios.multiplayerpractice.MultiplayerPractice;
import com.example.shared.PlayerPos;

public class TransformSystem extends IteratingSystem {

    private PooledEngine engine;
    private PlayScreen parent;

    ComponentMapper<PlayerComponent> pm;
    ComponentMapper<BodyComponent> b2m;
    ComponentMapper<TransformComponent> tm;

    public TransformSystem(PooledEngine engine, PlayScreen parent) {
        super(Family.all(TransformComponent.class).get());
        this.engine = engine;
        this.parent = parent;
        pm = ComponentMapper.getFor(PlayerComponent.class);
        b2m = ComponentMapper.getFor(BodyComponent.class);
        tm = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent pc = pm.get(entity);
        TransformComponent tc = tm.get(entity);
        BodyComponent body = b2m.get(entity);

        if (pc.remote && parent.gameMaster.parent.m.containsKey((Long)pc.tid)) {
            PlayerPos p = parent.gameMaster.parent.m.get((Long)pc.tid);
            System.out.println(pc.tid + ":" + p.x + ":" + p.y);
            tc.position.x = p.x;
            tc.position.y = p.y;
            body.body.setTransform(p.x,p.y,body.body.getAngle());

        }


    }
}
