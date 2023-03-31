package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Characters.*;

import java.util.ArrayList;

public class GameScreen extends BaseScreen {
    private Player player;
    private BaseActor baseCoin;
    private ArrayList<BaseActor> coinList;
    private ArrayList<BaseActor> wallList;
    private ArrayList<BaseActor> removeList;

    private int tileSize = 32;
    private int tileCountWidth = 30;
    private int tileCountHeight = 30;

    final int mapWidth = tileSize * tileCountWidth;
    final int mapHeight = tileSize * tileCountHeight;

    private TiledMap tiledMap;
    private OrthographicCamera tiledCamera;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private int[] backgroundLayers = { 0, 1 };
    private int[] foregroundLayers = { 2 };


    private Sound collectSound;
    private SpriteBatch batch;
    private Texture idle;
    private TextureAtlas textureAtlasLeft;
    private TextureAtlas textureAtlasRight;
    private TextureAtlas textureAtlasUp;
    private TextureAtlas textureAtlasDown;
    private Animation<TextureRegion> animationLeft;
    private Animation<TextureRegion> animationRight;
    private Animation<TextureRegion> animationUp;
    private Animation<TextureRegion> animationDown;
    private float elapsedTime = 0f;


    private Music bgm;

    private boolean status = true;
    private boolean wanderingStatus = true;
    private int timeCheck = 0;

    private int score = 0;
    private int scoreCoin = 0;
    private int finalScore = 0;
    private String yourScore;
    BitmapFont bitmapFont;


    //POLYMORPH
    private Enemy tesEnemy;
    private Enemy tesEnemy2;


    private long lastDropTime;
    private Array<Rectangle> rainDrops;

    public GameScreen(BaseGame g) {
        super(g);
    }

    @Override
    public void create() {
        //initialize player
        player = new Player();

        //ENEMY (POLYMORPH)
        tesEnemy = new Enemy1();
        tesEnemy2 = new Enemy2();

        //MUSIC
        bgm = Gdx.audio.newMusic(Gdx.files.internal("sound & music/music.ogg"));
        collectSound = Gdx.audio.newSound(Gdx.files.internal("sound & music/pickupCoin.ogg"));

        bgm.setVolume(0.1f);
        bgm.setLooping(true);
        bgm.play();

        bitmapFont = new BitmapFont();
        yourScore = "SCORE: 0";

        //player animation
        float t = 0.15f;
        player.storeAnimation("down", GameUtils.parseSpriteSheet(
                "sprites/down.png", 4, 2, new int[] {0,1}, t, PlayMode.LOOP_PINGPONG));
        player.storeAnimation("left", GameUtils.parseSpriteSheet(
                "sprites/left.png", 4, 2, new int[] {0,1 }, t, PlayMode.LOOP_PINGPONG));
        player.storeAnimation("right", GameUtils.parseSpriteSheet(
                "sprites/right.png", 4, 2, new int[] {0,1 }, t, PlayMode.LOOP_PINGPONG));
        player.storeAnimation("up", GameUtils.parseSpriteSheet(
                "sprites/up.png", 4, 2, new int[] { 0,1 }, t, PlayMode.LOOP_PINGPONG));
        player.setSize(48, 48);

        player.setEllipseBoundary(status);
        mainStage.addActor(player);

        //Enemy 1 Animation
        tesEnemy.storeAnimation("down", GameUtils.parseSpriteSheet(
                "enemy/enemy.png", 12, 8, new int[] {48,49,50}, t, PlayMode.LOOP_PINGPONG));
        tesEnemy.storeAnimation("left", GameUtils.parseSpriteSheet(
                "enemy/enemy.png", 12, 8, new int[] {60,61,62}, t, PlayMode.LOOP_PINGPONG));
        tesEnemy.storeAnimation("right", GameUtils.parseSpriteSheet(
                "enemy/enemy.png", 12, 8, new int[] {72,73,74}, t, PlayMode.LOOP_PINGPONG));
        tesEnemy.storeAnimation("up", GameUtils.parseSpriteSheet(
                "enemy/enemy.png", 12, 8, new int[] {84,85,86}, t, PlayMode.LOOP_PINGPONG));
        tesEnemy.setSize(48, 48);

        tesEnemy.setEllipseBoundary(status);
        mainStage.addActor(tesEnemy);

        //Enemy 2 Animation
        tesEnemy2.storeAnimation("down", GameUtils.parseSpriteSheet(
                "enemy/enemy.png", 12, 8, new int[] {51,52,53}, t, PlayMode.LOOP_PINGPONG));
        tesEnemy2.storeAnimation("left", GameUtils.parseSpriteSheet(
                "enemy/enemy.png", 12, 8, new int[] {63,64,65}, t, PlayMode.LOOP_PINGPONG));
        tesEnemy2.storeAnimation("right", GameUtils.parseSpriteSheet(
                "enemy/enemy.png", 12, 8, new int[] {75,76,77}, t, PlayMode.LOOP_PINGPONG));
        tesEnemy2.storeAnimation("up", GameUtils.parseSpriteSheet(
                "enemy/enemy.png", 12, 8, new int[] {87,88,89}, t, PlayMode.LOOP_PINGPONG));
        tesEnemy2.setSize(48, 48);

        tesEnemy2.setEllipseBoundary(status);
        mainStage.addActor(tesEnemy2);



        //initialize base coin; additional coins will be cloned from it later
        baseCoin = new BaseActor();
        baseCoin.setTexture(new Texture(Gdx.files.internal("map/coin.png")));
        baseCoin.setEllipseBoundary(status);
        coinList = new ArrayList<BaseActor>();

        wallList = new ArrayList<BaseActor>();

        removeList = new ArrayList<BaseActor>();

        //set up tile map, renderer, camera
        tiledMap = new TmxMapLoader().load("map/map011.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        tiledCamera = new OrthographicCamera();
        tiledCamera.setToOrtho(false, viewWidth, viewHeight);
        tiledCamera.update();

        batch = new SpriteBatch();
        idle = new Texture(Gdx.files.internal("sprites/idle.png"));
        textureAtlasLeft = new TextureAtlas(Gdx.files.internal("sprites/left.pack"));
        textureAtlasRight = new TextureAtlas(Gdx.files.internal("sprites/right.pack"));
        textureAtlasUp = new TextureAtlas(Gdx.files.internal("sprites/up.pack"));
        textureAtlasDown = new TextureAtlas(Gdx.files.internal("sprites/down.pack"));
        animationLeft = new Animation<TextureRegion>(1/7f,textureAtlasLeft.getRegions());
        animationRight = new Animation<TextureRegion>(1/7f,textureAtlasRight.getRegions());
        animationUp = new Animation<TextureRegion>(1/7f,textureAtlasUp.getRegions());
        animationDown = new Animation<TextureRegion>(1/7f,textureAtlasDown.getRegions());

        //untuk mengecek layer object (contohnya koin)
        MapObjects objects = tiledMap.getLayers().get("ObjectData").getObjects();
        for (MapObject object : objects) {
            String name = object.getName();

            RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
            Rectangle r = rectangleMapObject.getRectangle();

            switch (name) {
                case "player":
                    player.setPosition(r.x, r.y);
                    break;
                case "coin":
                    BaseActor coin = baseCoin.clone();
                    coin.setPosition(r.x, r.y);
                    mainStage.addActor(coin);
                    coinList.add(coin);
                    break;
                case "tesEnemy":
                    tesEnemy.setPosition(r.x, r.y);
                    break;
                case "tesEnemy2":
                    tesEnemy2.setPosition(r.x, r.y);
                    break;
                default:
                    System.err.println("Unknown tilemap object " + name);
            }
        }

        //untuk mengecek layer walls
        objects = tiledMap.getLayers().get("PhysicsData").getObjects();
        for (MapObject object : objects) {
            RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
            Rectangle r = rectangleMapObject.getRectangle();

            BaseActor solid = new BaseActor();
            solid.setPosition(r.x, r.y);
            solid.setSize(r.width, r.height);
            solid.setRectangleBoundary();
            wallList.add(solid);
        }
    }

    @Override
    public void update(float delta) {
        //player movement
        float playerSpeed = 500;
        float tesEnemySpeed = 500;
        player.setVelocityXY(0, 0);
        tesEnemy.setVelocityXY(0,0);
        tesEnemy2.setVelocityXY(0,0);

        //input handling PLAYER
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            player.setVelocityXY(-playerSpeed, 0);
            player.setActiveAnimation("left");
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            player.setVelocityXY(playerSpeed, 0);
            player.setActiveAnimation("right");
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            player.setVelocityXY(0, playerSpeed);
            player.setActiveAnimation("up");
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            player.setVelocityXY(0, -playerSpeed);
            player.setActiveAnimation("down");
        }
        if (player.getSpeed() < 1) {
            player.pauseAnimation();
            player.setAnimationFrame(1);
        } else {
            player.startAnimation();
        }

        //Enemy Wandering
        timeCheck++;
        if(timeCheck > 0 && timeCheck <= 200){
            tesEnemy.setVelocityXY(-tesEnemySpeed, 0);
            tesEnemy.setActiveAnimation("left");

            tesEnemy2.setVelocityXY(-tesEnemySpeed, 0);
            tesEnemy2.setActiveAnimation("left");
        }else if(timeCheck > 200 && timeCheck <= 400){
            tesEnemy.setVelocityXY(tesEnemySpeed, 0);
            tesEnemy.setActiveAnimation("right");
            tesEnemy2.setVelocityXY(tesEnemySpeed, 0);
            tesEnemy2.setActiveAnimation("right");
        }else if(timeCheck > 400 && timeCheck <= 600){
            tesEnemy.setVelocityXY(0, tesEnemySpeed);
            tesEnemy.setActiveAnimation("up");
            tesEnemy2.setVelocityXY(-0, tesEnemySpeed);
            tesEnemy2.setActiveAnimation("up");
        } else if(timeCheck > 600 && timeCheck <= 800){
            tesEnemy.setVelocityXY(0, -tesEnemySpeed);
            tesEnemy.setActiveAnimation("down");
            tesEnemy2.setVelocityXY(0, -tesEnemySpeed);
            tesEnemy2.setActiveAnimation("down");
        } else if(timeCheck > 800){
            timeCheck = 0;
        }


        //collision detection
        for (BaseActor wall : wallList) {
            player.overlaps(wall, true);
            tesEnemy.overlaps(wall, true);
            tesEnemy2.overlaps(wall, true);
        }

        //Overlaps Player and Enemy
        if(player.overlaps(tesEnemy, false) || player.overlaps(tesEnemy2, false)){
            game.setScreen(new GameOver(game));
            bgm.stop();
        }

        for (BaseActor coin : coinList) {
            if (player.overlaps(coin, false)) {
                collectSound.play( 0.1f);
                removeList.add(coin);
                scoreCoin++;
                if(scoreCoin == 15){
                    finalScore += 2;
                    scoreCoin = 0;
                }
            }
        }

        //SISTEM SCORE
        score++;
        if(score == 50){
            finalScore += 1;
            score = 0;
            yourScore = "SCORE: " + finalScore;
        }

        if(finalScore == 150){
            game.setScreen(new GameOverWin(game));
            bgm.stop();
        }

        //clean the removeList
        for (BaseActor ba : removeList) {
            ba.destroy();

        }

        //camera adjustment
        Camera mainCamera = mainStage.getCamera();

        //center camera on player
        mainCamera.position.x = player.getX() + player.getOriginX();
        mainCamera.position.y = player.getY() + player.getOriginY();

        //bound camera to layout
        mainCamera.position.x = MathUtils.clamp(
                mainCamera.position.x, viewWidth / 2, mapWidth - viewWidth / 2);
        mainCamera.position.y = MathUtils.clamp(
                mainCamera.position.y, viewHeight / 2, mapHeight - viewHeight / 2);
        mainCamera.update();

        //adjust tilemap camera to stay in sync with main camera
        tiledCamera.position.x = mainCamera.position.x;
        tiledCamera.position.y = mainCamera.position.y;
        tiledCamera.update();
        tiledMapRenderer.setView(tiledCamera);
        rainDrops = new Array<Rectangle>();
    }

    @Override
    public void render(float delta) {
        uiStage.act(delta);
        if (!isPaused()) {
            mainStage.act(delta);
            update(delta);
        }

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiledMapRenderer.render(backgroundLayers);
        mainStage.draw();
        tiledMapRenderer.render(foregroundLayers);
        uiStage.draw();
        elapsedTime += Gdx.graphics.getDeltaTime();

        //Pengaturan Skill Spells Enemy
        if (timeCheck > 300 && timeCheck <= 400) {
            tesEnemy.setTemp(1);
            tesEnemy.skill();
        }
        if (timeCheck > 500 && timeCheck <= 600) {
            tesEnemy.setTemp(2);
            tesEnemy.skill();
        }
//        if (timeCheck > 700 && timeCheck <= 800) {
//            tesEnemy.setTemp(4);
//            tesEnemy.skill();
//        }
        if (timeCheck > 100 && timeCheck <= 200) {
            tesEnemy.setTemp(3);
            tesEnemy.skill();
        }

        if (timeCheck > 100 && timeCheck < 150) {
            tesEnemy2.skill();
        }

        if (timeCheck > 350 && timeCheck < 400) {
            tesEnemy2.skill();
        }

        if (timeCheck > 450 && timeCheck < 500) {
            tesEnemy2.skill();
        }


        batch.begin();
        bitmapFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        bitmapFont.draw(batch, yourScore, 30, 450);
        batch.end();
    }

    public void cekColl() {
        if (player.getX() == tesEnemy.getSkillx() && player.getY() == tesEnemy.getSkilly()) {
            Gdx.app.log("Id", "Collision");
        }
        if (player.getX() == tesEnemy2.getSkillx() && player.getY() == tesEnemy2.getSkilly()) {
            Gdx.app.log("Id", "Collision");
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.P) togglePaused();
        if (keycode == Keys.R) game.setScreen(new GameScreen(game));
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void dispose(){
        bgm.dispose();
        batch.dispose();
    }
}

