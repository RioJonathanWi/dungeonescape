package com.mygdx.game.Characters;

public interface Entities {
    public void setVelocityXY(float vx, float vy);
    public void addVelocityXY(float vx, float vy);
    public void setVelocityAS(float angleDeg, float speed);
    public float getSpeed();
    public void setSpeed(float s);
    public void setMaxSpeed(float ms);
    public float getMotionAngle();
    public void setAutoAngle(boolean b);
    public void setAccelerationXY(float ax, float ay);
    public void addAccelerationXY(float ax, float ay);
    public void setAccelerationAS(float angleDeg, float speed);
    public void addAccelerationAS(float angleDeg, float speed);
    public void accelerateForward(float speed);
    public void setDeceleration(float d);
}
