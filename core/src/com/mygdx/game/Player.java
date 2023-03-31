package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Characters.Entities;

public class Player extends AnimatedActor implements Entities {
    private Vector2 velocity;
    private Vector2 acceleration;
    private float maxSpeed;
    private float deceleration;
    private boolean autoAngle;

    public Player() {
        velocity = new Vector2();
        acceleration = new Vector2();
        maxSpeed = 200;
        deceleration = 0;
        autoAngle = false;
    }

    //VELOCITY METHODS
    @Override
    public void setVelocityXY(float vx, float vy) { velocity.set(vx, vy); }

    @Override
    public void addVelocityXY(float vx, float vy) { velocity.add(vx, vy); }

    @Override
    public void setVelocityAS(float angleDeg, float speed) {
        velocity.x = speed * MathUtils.cosDeg(angleDeg);
        velocity.y = speed * MathUtils.sinDeg(angleDeg); }

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

    }

    public void copy(Player original) {
        super.copy(original);
        this.velocity = new Vector2(original.velocity);
        this.acceleration = new Vector2(original.acceleration);
        this.maxSpeed = original.maxSpeed;
        this.deceleration = original.deceleration;
        this.autoAngle = original.autoAngle;
    }

    public Player clone() {
        Player newbie = new Player();
        newbie.copy(this);
        return newbie;
    }
}

