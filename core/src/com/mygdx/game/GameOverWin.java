package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class GameOverWin extends ApplicationAdapter implements Screen {
    final BaseGame game;

    OrthographicCamera camera;

    Texture gameOver;
    Music sound;
    Sound click;
    BitmapFont bitmapFont;
    String touchAnywhere;

    public GameOverWin(BaseGame game){
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, camera.viewportWidth, camera.viewportHeight);

        gameOver = new Texture("labels/escaped.png");

        sound = Gdx.audio.newMusic(Gdx.files.internal("sound & music/gameoverwin.ogg"));
        sound.setVolume(0.1f);
        sound.play();

        click = Gdx.audio.newSound(Gdx.files.internal("sound & music/click.ogg"));

        bitmapFont = new BitmapFont();
        touchAnywhere = "Touch Anywhere To Restart!";

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        game.batch.begin();

        bitmapFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        float x = camera.viewportWidth / 2 + 170;
        float y = camera.viewportHeight / 2 + 100;
        float yExit = camera.viewportHeight / 2 + 30;
        float x2 = x - 300 / 2;

        game.batch.draw(gameOver, camera.viewportWidth / 2 + 75, camera.viewportHeight / 2 + 20, 500,500);
        bitmapFont.draw(game.batch, touchAnywhere, 225, 235);
        game.batch.end();

        if(Gdx.input.isTouched()){
            game.setScreen(new MainMenuScreen(game));
            sound.stop();
        }


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
        click.dispose();
        sound.stop();
    }
}

