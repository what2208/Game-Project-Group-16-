package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player {
    public Rectangle sprite;
    public Rectangle feet;
    final HustleGame game;
    public int direction = 2; // 0 = up, 1 = right, 2 = down, 3 = left (like a clock)
    private TextureRegion currentFrame;
    private float stateTime = 0;
    private Array<Animation<TextureRegion>> walkingAnimation;
    private Array<Animation<TextureRegion>> idleAnimation;
    // Stats
    public float speed = 300f;
    public Array<Rectangle> collidables;
    public int scale = 4;
    private Rectangle bounds;

    public Player (final HustleGame game) {
        this.game = game;
        // Load the player's textures from the atlas
        TextureAtlas playerAtlas = new TextureAtlas(Gdx.files.internal("Sprites/Player/Player.atlas"));

        walkingAnimation = new Array<Animation<TextureRegion>>();
        idleAnimation = new Array<Animation<TextureRegion>>();

        // Load walking animation from Sprite atlas
        walkingAnimation.add(
                new Animation<TextureRegion>(0.25f, playerAtlas.findRegions("walk_back"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.25f, playerAtlas.findRegions("walk_right"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.25f, playerAtlas.findRegions("walk_front"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.25f, playerAtlas.findRegions("walk_left"), Animation.PlayMode.LOOP));
        // Load idle animation
        idleAnimation.add(
                new Animation<TextureRegion>(0.40f, playerAtlas.findRegions("idle_back"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.40f, playerAtlas.findRegions("idle_right"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.40f, playerAtlas.findRegions("idle_front"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.40f, playerAtlas.findRegions("idle_left"), Animation.PlayMode.LOOP)
        );

        collidables = new Array<Rectangle>();

        // Sprite is a rectangle covering the whole player
        sprite = new Rectangle(0, 0, 17*scale, 28*scale);
        // Feet is a rectangle just covering the player's feet, so is better for collision
        feet = new Rectangle(4*scale, 0, 9*scale, 7*scale);

    }

    public void move () {
        // Updates the player's position based on keys being pressed
        // Also updates the direction they are facing, and whether they are currently moving
        // And also does collision

        // To check collision, store the player's current position
        float oldX = sprite.x;
        float oldY = sprite.y;
        float oldFeetX = feet.x;

        // Move the player and their feet

        boolean moving = false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            sprite.x -= speed * Gdx.graphics.getDeltaTime();
            feet.x -= speed * Gdx.graphics.getDeltaTime();
            direction = 3;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            sprite.x += speed * Gdx.graphics.getDeltaTime();
            feet.x += speed * Gdx.graphics.getDeltaTime();
            direction = 1;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            sprite.y += speed * Gdx.graphics.getDeltaTime();
            feet.y += speed * Gdx.graphics.getDeltaTime();
            direction = 0;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            sprite.y -= speed * Gdx.graphics.getDeltaTime();
            feet.y -= speed * Gdx.graphics.getDeltaTime();
            direction = 2;
            moving = true;
        }

        // Check if the player's feet are inside an object
        for (Rectangle object : this.collidables) {
            // If they are, move them back to where they were
            // And set moving = false
            if (feet.overlaps(object)) {
                sprite.x = oldX;
                sprite.y = oldY;
                feet.y = oldY;
                feet.x = oldFeetX;
                moving = false;
                break;
            }

        }

        // Check the player is in bounds
        if (bounds != null) {
            // If player is out of bounds, move them back
            if (!feet.overlaps(bounds)) {
                sprite.x = oldX;
                sprite.y = oldY;
                feet.y = oldY;
                feet.x = oldFeetX;
                moving = false;
            }
        }

        // Increment the animation
        stateTime += Gdx.graphics.getDeltaTime();

        // Set the current frame of the animation
        // Show a different animation if the player is moving vs idling
        if (moving) {
            currentFrame = walkingAnimation.get(direction).getKeyFrame(stateTime);
        } else {
            currentFrame = idleAnimation.get(direction).getKeyFrame(stateTime);
        }

        // Round
//        sprite.x = Math.round(sprite.x);
//        sprite.y = Math.round(sprite.y);
//        feet.x = Math.round(feet.x);
//        feet.y = Math.round(feet.y);




    }


    public TextureRegion getCurrentFrame () {
        // Returns the current frame the player animation is on
        return currentFrame;
    }

    // Sets the player's collidable objects as an array of rectangles
    public void setCollidables (Array<Rectangle> collidables) {
        this.collidables = collidables;
    }

    // Adds a rectangle for the player to collide with
    public void addCollidable (Rectangle object) {
        this.collidables.add(object);
    }

    public float getX () {
        return sprite.getX();
    }

    public float getY () {
        return sprite.getY();
    }

    public void setX (float x) {
        this.sprite.setX(x);
        this.feet.setX(x + 4*scale);
    }

    public void setY (float y) {
        this.sprite.setY(y);
        this.feet.setY(y + 4*scale);
    }

    public void setPos (float x, float y) {
        this.setX(x);
        this.setY(y);
    }

    public void setBounds (Rectangle bounds) {
        // Set a rectangle that the player should not leave
        this.bounds = bounds;
    }

}
