package com.mygdx.game.Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy1 extends Enemy {
    private Vector2 velocity;
    private Vector2 acceleration;
    private float maxSpeed;
    private float deceleration;
    private boolean autoAngle;

    private SpriteBatch batch;
    private TextureAtlas textureAtlasLeft;
    private TextureAtlas textureAtlasRight;
    private TextureAtlas textureAtlasUp;
    private TextureAtlas textureAtlasDown;

    private Animation<TextureRegion> animationLeft;
    private Animation<TextureRegion> animationRight;
    private Animation<TextureRegion> animationUp;
    private Animation<TextureRegion> animationDown;

    private float elapsedTime = 0f;

    private float skillx;
    private float skilly;
    private int temp;
    private Rectangle fireball;

    public Enemy1() {
        velocity = new Vector2();
        acceleration = new Vector2();
        maxSpeed = 200;
        deceleration = 0;
        autoAngle = false;
        batch = new SpriteBatch();
        fireball = new Rectangle();
        fireball.x = 320;
        fireball.y = 32;
        setSkillx(fireball.x);
        setSkilly(fireball.y);

        textureAtlasLeft = new TextureAtlas(Gdx.files.internal("fireball/left.pack"));
        textureAtlasRight = new TextureAtlas(Gdx.files.internal("fireball/right.pack"));
        textureAtlasUp = new TextureAtlas(Gdx.files.internal("fireball/up.pack"));
        textureAtlasDown = new TextureAtlas(Gdx.files.internal("fireball/down.pack"));

        animationLeft = new Animation<TextureRegion>(1/7f,textureAtlasLeft.getRegions());
        animationRight = new Animation<TextureRegion>(1/7f,textureAtlasRight.getRegions());
        animationUp = new Animation<TextureRegion>(1/7f,textureAtlasUp.getRegions());
        animationDown = new Animation<TextureRegion>(1/7f,textureAtlasDown.getRegions());
    }

    @Override
    public void skill(){
        elapsedTime += Gdx.graphics.getDeltaTime();
        if (getTemp() == 1) {
            TextureRegion currentFrame = animationRight.getKeyFrame(elapsedTime,true);
            fireball.x += 250 * Gdx.graphics.getDeltaTime();
            batch.begin();
            batch.draw(currentFrame, fireball.x, fireball.y);
            batch.end();
        }
        if (getTemp() == 2) {
            TextureRegion currentFrame = animationUp.getKeyFrame(elapsedTime,true);
            fireball.y += 250 * Gdx.graphics.getDeltaTime();
            batch.begin();
            batch.draw(currentFrame, fireball.x, fireball.y);
            batch.end();
        }
        if (getTemp() == 3) {
            TextureRegion currentFrame = animationLeft.getKeyFrame(elapsedTime,true);
            fireball.x -= 250 * Gdx.graphics.getDeltaTime();
            batch.begin();
            batch.draw(currentFrame, fireball.x, fireball.y);
            batch.end();
        }
        if (getTemp() == 4) {
            TextureRegion currentFrame = animationDown.getKeyFrame(elapsedTime,true);
            fireball.y -= 250 * Gdx.graphics.getDeltaTime();
            batch.begin();
            batch.draw(currentFrame, fireball.x, fireball.y);
            batch.end();
        }
    }

    //VELOCITY METHODS
    @Override
    public void setVelocityXY(float vx, float vy) { velocity.set(vx, vy); }

    @Override
    public void addVelocityXY(float vx, float vy) { velocity.add(vx, vy); }

    @Override
    public void setVelocityAS(float angleDeg, float speed) {
        velocity.x = speed * MathUtils.cosDeg(angleDeg);
        velocity.y = speed * MathUtils.sinDeg(angleDeg);
    }

    @Override
    public float getSkillx() {
        return skillx;
    }

    @Override
    public float getSkilly() {
        return skilly;
    }

    //SPEED METHODS
    @Override
    public float getSpeed() { return velocity.len(); }

    @Override
    public void setSpeed(float s) { velocity.setLength(s); }

    @Override
    public void setMaxSpeed(float ms) { maxSpeed = ms; }

    //ANGLE METHODS
    @Override
    public float getMotionAngle() {
        return MathUtils.atan2(velocity.y, velocity.x) * MathUtils.radiansToDegrees;
    }

    @Override
    public void setAutoAngle(boolean b) { autoAngle = b; }

    //ACCELERATION METHODS
    @Override
    public void setAccelerationXY(float ax, float ay) { acceleration.set(ax, ay); }

    @Override
    public void addAccelerationXY(float ax, float ay) { acceleration.add(ax, ay); }

    @Override
    public void setAccelerationAS(float angleDeg, float speed) {
        acceleration.x = speed * MathUtils.cosDeg(angleDeg);
        acceleration.y = speed * MathUtils.sinDeg(angleDeg);
    }

    @Override
    public void addAccelerationAS(float angleDeg, float speed) { acceleration.add(
            speed * MathUtils.cosDeg(angleDeg),
            speed * MathUtils.sinDeg(angleDeg));
    }

    @Override
    public void accelerateForward(float speed) { setAccelerationAS(getRotation(), speed); }

    @Override
    public void setDeceleration(float d) { deceleration = d; }

    @Override
    public void act(float delta) {
        super.act(delta);
        velocity.add(acceleration.x * delta, acceleration.y * delta); //apply acceleration
        //decrease velocity when not accelerating
        if (acceleration.len() < 0.01) {
            float decelerateAmount = deceleration * delta;
            if (getSpeed() < decelerateAmount) {
                setSpeed(0);
            } else {
                setSpeed(getSpeed() - decelerateAmount);
            }
        }
        //cap at max speed
        if (getSpeed() > maxSpeed) {
            setSpeed(maxSpeed);
        }
        //apply velocity
        moveBy(velocity.x * delta, velocity.y * delta);
        //rotate img when moving
        if (autoAngle && getSpeed() > 0.1) {
            setRotation(getMotionAngle());
        }
    }

    public void copy(Enemy1 original) {
        super.copy(original);
        this.velocity = new Vector2(original.velocity);
        this.acceleration = new Vector2(original.acceleration);
        this.maxSpeed = original.maxSpeed;
        this.deceleration = original.deceleration;
        this.autoAngle = original.autoAngle;
    }

    @Override
    public Enemy1 clone() {
        Enemy1 newbie = new Enemy1();
        newbie.copy(this);
        return newbie;
    }

    @Override
    public void setSkillx(float skillx) {
        this.skillx = skillx;
    }

    @Override
    public void setSkilly(float skilly) {
        this.skilly = skilly;
    }

    @Override
    public int getTemp() {
        return temp;
    }

    @Override
    public void setTemp(int temp) {
        this.temp = temp;
    }
}
