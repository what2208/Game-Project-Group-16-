package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player {
    // Hitboxes
    public Rectangle sprite, feet, eventHitbox;
    public float centreX, centreY;
    public int direction = 2; // 0 = up, 1 = right, 2 = down, 3 = left (like a clock)
    private TextureRegion currentFrame;
    private float stateTime = 0;
    private final Array<Animation<TextureRegion>> walkingAnimation, idleAnimation;
    // Stats
    public float speed = 300f;
    public Array<GameObject> collidables;
    public int scale = 4;
    private Rectangle bounds;
    private GameObject closestObject;
    public boolean frozen, moving;

    public Player () {
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

        collidables = new Array<GameObject>();

        // Sprite is a rectangle covering the whole player
        sprite = new Rectangle(0, 0, 17*scale, 28*scale);

        // Feet is a rectangle just covering the player's feet, so is better for collision
        feet = new Rectangle(4*scale, 0, 9*scale, 7*scale);

        // Hitbox for triggering events with objects
        float hitboxScaleX = 2.2f;
        float hitboxScaley = 1.7f;
        eventHitbox = new Rectangle(
                sprite.getX() - (sprite.getWidth()*hitboxScaleX - sprite.getWidth()) / 2,
                sprite.getY() - (sprite.getHeight()*hitboxScaley - sprite.getHeight()) / 2,
                sprite.getWidth()*hitboxScaleX,
                sprite.getHeight()*hitboxScaley
        );

    }

    public void move (float delta) {
        // Updates the player's position based on keys being pressed
        // Also updates the direction they are facing, and whether they are currently moving
        // And also does collision

        // To check collision, store the player's current position
        float oldX = sprite.x;
        float oldY = sprite.y;
        float oldFeetX = feet.x;

        // Move the player and their 2 other hitboxes

        moving = false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.setX(sprite.getX() - speed * delta); // Note: Setting all the values with a constant delta removes hitbox desyncing issues
            direction = 3;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.setX(sprite.getX() + speed * delta);
            direction = 1;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            this.setY(sprite.getY() + speed * delta);
            direction = 0;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            this.setY(sprite.getY() - speed * delta);
            direction = 2;
            moving = true;
        }
        // Check if the player's feet are inside an object, if they are, move them back in that axis
        for (GameObject object : this.collidables) {
            if (feet.overlaps(object)) {
                // Find the direction that the player needs to be moved back to
                // Reset x
                if (!(oldFeetX < object.x + object.width && oldFeetX + feet.width > object.x)) {
                    this.setX(oldX);
                }
                // If overlapping in y direction
                if (!(oldY < object.y + object.height && oldY + feet.height > object.y)) {
                    this.setY(oldY);
                }
                // The above two are essentially the same code as Rectangle.overlaps()
                // Just separated into the x and y dimensions
            }
        }


        // Check the player is in bounds
        if (bounds != null) {
            // If player is out of bounds, move them back
                if (feet.getX() < bounds.getX()) {
                sprite.x = bounds.getX()-4*scale;
                feet.x = sprite.x + 4*scale;
            }
            if (feet.getX()+feet.getWidth() > bounds.getWidth()) {
                sprite.x = (bounds.getWidth() - feet.getWidth()) - (4*scale);
                feet.x = sprite.x + 4*scale;
            }
            if (feet.getY() < bounds.getY()) {
                sprite.y = bounds.getY();
                feet.y = bounds.getY();
            }
            if (feet.getY()+feet.getHeight() > bounds.getHeight()) {
                sprite.y = bounds.getHeight()-feet.getHeight();
                feet.y = sprite.y;
            }
        }

        // Find the closest object to the player so they can interact with it
        recalcCentre(); // Just recalculates the centre of the player now we have moved them
        float distance = -1;
        closestObject = null;
        for (GameObject object : this.collidables) {
            if (eventHitbox.overlaps(object)) {
                // Check if this is the closest object to the player
                if (distance == -1 || distanceFrom(object) < distance) {
                    closestObject = object;
                    distance = distanceFrom(object);
                }
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

        // Bosh!

    }

    public GameObject getClosestObject () {
        return closestObject;
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean nearObject() {
        return closestObject != null;
    }

    public TextureRegion getCurrentFrame () {
        // Returns the current frame the player animation is on
        return currentFrame;
    }

    // Sets the player's collidable objects as an array of rectangles
    public void setCollidables (Array<GameObject> collidables) {
        this.collidables = collidables;
    }

    // Adds a rectangle for the player to collide with
    public void addCollidable (GameObject object) {
        this.collidables.add(object);
    }

    public float getX () {
        return sprite.getX();
    }

    public float getY () {
        return sprite.getY();
    }

    public float getCentreX () {
        return centreX;
    }
    public float getCentreY () {
        return centreY;
    }

    public void setX (float x) {
        this.sprite.setX(x);
        this.feet.setX(x + 4*scale);
        this.eventHitbox.setX(this.sprite.getX() - (this.eventHitbox.getWidth() - sprite.getWidth()) / 2);
        this.recalcCentre();
    }

    public void setY (float y) {
        this.sprite.setY(y);
        this.feet.setY(y);
        this.eventHitbox.setY(this.sprite.getY() - (this.eventHitbox.getHeight() - sprite.getHeight()) / 2);
        this.recalcCentre();
    }

    public void setPos (float x, float y) {
        this.setX(x);
        this.setY(y);
    }

    public void setBounds (Rectangle bounds) {
        // Set a rectangle that the player should not leave
        this.bounds = bounds;
    }

    private float distanceFrom (GameObject object) {
        // Returns the distance from an object
        return (float) Math.sqrt((Math.pow((centreX - object.centreX), 2) + Math.pow((centreY - object.centreY), 2)));
    }

    private void recalcCentre() {
        centreX = sprite.getX() + sprite.getWidth() / 2;
        centreY = sprite.getY() + sprite.getHeight() / 2;
    }

    public void setFrozen (boolean freeze) {
        this.frozen = freeze;
        if (freeze) {
            // Set to non-moving frame
            currentFrame = idleAnimation.get(direction).getKeyFrame(stateTime);
        }
    }
    public boolean isFrozen () {
        return this.frozen;
    }

}
