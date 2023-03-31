package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BaseGame extends Game {

    Skin skin;
    public SpriteBatch batch;



    public BaseGame() {
        skin = new Skin();
        }

    public void create(){
        batch = new SpriteBatch();
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() { skin.dispose(); }

}
