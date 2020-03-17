package com.cormicopiastudios.multiplayerpractice.GameEngine.Systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Components.BodyComponent;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Components.PlayerComponent;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Components.TransformComponent;
import com.cormicopiastudios.multiplayerpractice.GameEngine.GameMaster;
import com.cormicopiastudios.multiplayerpractice.GameEngine.controllers.InputController;

public class PhysicsSystem extends IntervalIteratingSystem {

    private static final float MAX_STEP_TIME = 1/45f;

    private World world;
//    private InputController controller;
    private Array<Entity> bodiesQueue;
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private InputController controller;
    private GameMaster master;



    public PhysicsSystem(World world, InputController controller, GameMaster master) {
        super(Family.all().get(), MAX_STEP_TIME);
        this.world = world;
        this.bodiesQueue = new Array<Entity>();
        this.controller = controller;
        this.master = master;
    }

    @Override
    protected void processEntity(Entity entity) {
        bodiesQueue.add(entity);
    }

    public void updateInterval() {
        super.updateInterval();
        world.step(MAX_STEP_TIME, 6, 2);
        for (Entity ent : bodiesQueue) {
            TransformComponent tfm = tm.get(ent);
            BodyComponent bodyComp = bm.get(ent);
            Vector2 pos = bodyComp.body.getPosition();
            if ((tfm.position.x != pos.x || tfm.position.y != pos.y) && !ent.getComponent(PlayerComponent.class).remote) {
                master.sendPosUpdate(pos);
            }
            tfm.position.x = pos.x;
            tfm.position.y = pos.y;


            bodyComp.body.setTransform(new Vector2(tfm.position.x,tfm.position.y), MathUtils.degreesToRadians * tfm.rotation);

            }

        bodiesQueue.clear();
    }

}
