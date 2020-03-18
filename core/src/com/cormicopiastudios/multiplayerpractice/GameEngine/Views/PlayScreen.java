package com.cormicopiastudios.multiplayerpractice.GameEngine.Views;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Components.BodyComponent;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Components.PlayerComponent;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Components.TransformComponent;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Factories.BodyFactory;
import com.cormicopiastudios.multiplayerpractice.GameEngine.GameMaster;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Systems.PhysicsDebugSystem;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Systems.PhysicsSystem;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Systems.PlayerControlSystem;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Systems.PlayerSystem;
import com.cormicopiastudios.multiplayerpractice.GameEngine.Systems.TransformSystem;
import com.cormicopiastudios.multiplayerpractice.GameEngine.controllers.InputController;

public class PlayScreen implements Screen {

    static final float PPM = 32.f; // Sets the amount of pixels each meter of box2d object contain (will probably need to readjust this after the fact)


    // This gets the height and width of our camera frutsum based off the width and height of the screen and our pixel ration
    static final float FRUTSUM_W = Gdx.graphics.getWidth()/PPM;
    static final float FRUTSUM_H = Gdx.graphics.getHeight()/PPM;

    public GameMaster gameMaster;
    private World world;
    private BodyFactory bodyFactory;
    private PooledEngine engine;
    private Entity player;
    private InputController inputController;


    // camera
    private OrthographicCamera gamecam;

    private SpriteBatch batch;


    public PlayScreen(GameMaster master) {
        this.gameMaster = master;
        world = new World(new Vector2(0,-0.f),true);
        inputController = new InputController();
        batch = new SpriteBatch();

        gamecam = new OrthographicCamera(FRUTSUM_W, FRUTSUM_H);

        gamecam.position.set(FRUTSUM_W/2.f, FRUTSUM_H/2.f,0);

        bodyFactory = BodyFactory.getInstance(world);

        engine = new PooledEngine();

        engine.addSystem(new PhysicsSystem(world, inputController, gameMaster));
        engine.addSystem(new PhysicsDebugSystem(world,gamecam));
        createPlayer();
        engine.addSystem(new PlayerControlSystem(inputController, engine, gameMaster));
        engine.addSystem(new TransformSystem(engine,this));
        engine.addSystem(new PlayerSystem(this));


    }

    public void createPlayerCharacter(long tid) {
        float posx = -4;
        float posy = 0;
        // create the entity and all the components in it
        Entity newClient = engine.createEntity();
        BodyComponent b2BodyComponent = engine.createComponent(BodyComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);

        // create the data for the components
        b2BodyComponent.body = bodyFactory.makeBoxPolyBody(posx,posy,2,2,
                BodyFactory.FIXTURE_TYPE.STEEL, BodyDef.BodyType.DynamicBody,true);

        // set object pos
        transformComponent.position.set(posx,posy,0);
        transformComponent.scale.x = 2f;
        transformComponent.scale.y = 2f;
        b2BodyComponent.body.setUserData(newClient);
        playerComponent.tid = tid;
        playerComponent.remote = true;
        System.out.println("Remote Player TID: " + playerComponent.tid);

        // add components to entity
        newClient.add(b2BodyComponent);
        newClient.add(transformComponent);
        newClient.add(playerComponent);

        // add to engine
        engine.addEntity(newClient);
    }

    public void createPlayer() {
        float posx = -4;
        float posy = 0;
        // create the entity and all the components in it
        player = engine.createEntity();
        BodyComponent b2BodyComponent = engine.createComponent(BodyComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);

        // create the data for the components
        b2BodyComponent.body = bodyFactory.makeBoxPolyBody(posx,posy,2,2,
                BodyFactory.FIXTURE_TYPE.STEEL, BodyDef.BodyType.DynamicBody,true);

        // set object pos
        transformComponent.position.set(posx,posy,0);
        transformComponent.scale.x = 2f;
        transformComponent.scale.y = 2f;
        b2BodyComponent.body.setUserData(player);
        playerComponent.cam = gamecam;
        playerComponent.tid = gameMaster.parent.tid;
        playerComponent.remote = false;
        System.out.println("Local Player TID: " + playerComponent.tid);

        // add components to entity
        player.add(b2BodyComponent);
        player.add(transformComponent);
        player.add(playerComponent);

        // add to engine
        engine.addEntity(player);
    }



    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputController);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        engine.update(delta);
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
