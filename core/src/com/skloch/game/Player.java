package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * A class handling everything needed to control and draw a player, including animation, movement and collision
 */
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

    /**
     * A player character, contains methods to move the player and update animations, also includes collision handling
     * and can be used to trigger events of objects near the player.
     * Includes a feet hitbox for collision and an event hitbox for triggering objects.
     * Call move() then draw the result of getCurrentAnimation() to use
     *
     * @param avatar "avatar1" for the more masculine character, "avatar2" for the more feminine character,
     *               player animations are packed in the player_sprites atlas
     */
    public Player (String avatar) {
        // Load the player's textures from the atlas
        TextureAtlas playerAtlas = new TextureAtlas(Gdx.files.internal("Sprites/Player/player_sprites.atlas"));

        walkingAnimation = new Array<Animation<TextureRegion>>();
        idleAnimation = new Array<Animation<TextureRegion>>();

        // Load walking animation from Sprite atlas
        walkingAnimation.add(
                new Animation<TextureRegion>(0.25f, playerAtlas.findRegions(avatar + "_walk_back"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.25f, playerAtlas.findRegions(avatar + "_walk_right"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.25f, playerAtlas.findRegions(avatar + "_walk_front"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.25f, playerAtlas.findRegions(avatar + "_walk_left"), Animation.PlayMode.LOOP));
        // Load idle animation
        idleAnimation.add(
                new Animation<TextureRegion>(0.40f, playerAtlas.findRegions(avatar + "_idle_back"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.40f, playerAtlas.findRegions(avatar + "_idle_right"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.40f, playerAtlas.findRegions(avatar + "_idle_front"), Animation.PlayMode.LOOP),
                new Animation<TextureRegion>(0.40f, playerAtlas.findRegions(avatar + "_idle_left"), Animation.PlayMode.LOOP)
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

    /**
     * Handles all the logic involved in moving the player given keyboard inputs
     * If the player encounters an object, they will not be alowed to move into the space, but will attempt to
     * 'slide' off of it.
     * Also updates the player's animation
     *
     * <p></p>
     *
     * Also locates the nearest object after moving, which can be used to trigger events
     *
     * @param delta The time passed since the previous render
     */
    public void move (float delta) {
        // Updates the player's position based on keys being pressed
        // Also updates the direction they are facing, and whether they are currently moving
        // And also does collision

        moving = false;
        // To check collision, store the player's current position
        float oldX = sprite.x;
        float oldY = sprite.y;
        float oldFeetX = feet.x;

        // If not frozen, react to keyboard input presses
        if (!frozen) {
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
        }



        // Find the closest object to the player so they can interact with it
        recalcCentre(); // Just recalculates the centre of the player now we have moved them
        float distance = -1;
        closestObject = null;
        for (GameObject object : this.collidables) {
            // Check if this object is even interactable
            if (object.get("event") != null || object.get("text") != null) {
                if (eventHitbox.overlaps(object)) {
                    // Check if this is the closest object to the player
                    if (distance == -1 || distanceFrom(object) < distance) {
                        closestObject = object;
                        distance = distanceFrom(object);
                    }
                }
            }
        }

        // Increment the animation
        updateAnimation();

    }

    /**
     * Advances the current animation based on the time since the last render
     * The animation frame of the player can be grabbed with getCurrentFrame
     */
    public void updateAnimation() {
        stateTime += Gdx.graphics.getDeltaTime();
        // Set the current frame of the animation
        // Show a different animation if the player is moving vs idling
        if (moving) {
            currentFrame = walkingAnimation.get(direction).getKeyFrame(stateTime);
        } else {
            currentFrame = idleAnimation.get(direction).getKeyFrame(stateTime);
        }
    }

    /**
     * Returns whether the player's eventHitbox overlaps an object
     * Call getClosestObject to get the nearest
     *
     * @return true if a player is near enough an object to interact with it
     */
    public boolean nearObject() {
        return closestObject != null;
    }

    /**
     * Returns the object that is closest to the player, calculated during move()
     *
     * @return A GameObject that is closest
     */
    public GameObject getClosestObject () {
        return closestObject;
    }

    /**
     * Returns if the player is moving or not
     *
     * @return true if the player is moving
     */
    public boolean isMoving() {
        return moving;
    }

    /**
     * Sets the player's state to moving or not moving, a not moving character will just display an idle animation
     *
     * @param moving The boolean to set moving to
     */
    public void setMoving(boolean moving) {
        this.moving = moving;
    }


    /**
     * Returns the current frame the player's animation is on
     *
     * @return TextureRegion the frame of the player's animation
     */
    public TextureRegion getCurrentFrame () {
        // Returns the current frame the player animation is on
        return currentFrame;
    }

    /**
     * Sets the objects the player cannot move into as an Array of GameObjects
     *
     * @param collidables An array of GameObjects that the player should collide with
     */
    public void setCollidables (Array<GameObject> collidables) {
        this.collidables = collidables;
    }

    /**
     * Adds a GameObeject to the player's list of collidable objects
     *
     * @param object a GameObject for the player to collide with
     */
    public void addCollidable (GameObject object) {
        this.collidables.add(object);
    }

    /**
     * @return The X coordinate of the player
     */
    public float getX () {
        return sprite.getX();
    }
    /**
     * @return The Y coordinate of the player
     */
    public float getY () {
        return sprite.getY();
    }

    /**
     * @return The X coordinate of the centre point of the player's sprite rectangle
     */
    public float getCentreX () {
        return centreX;
    }
    /**
     * @return The Y coordinate of the centre point of the player's sprite rectangle
     */
    public float getCentreY () {
        return centreY;
    }

    /**
     * @return The Vector3 representation of the bottom left corner of the player's sprite hitbox
     */
    public Vector3 getPosAsVec3() {
        return new Vector3(
                sprite.getX(),
                sprite.getY(),
                0
        );
    }

    /**
     * Sets the x coordinate of the player, updating all 3 hitboxes at once as opposed to just the sprite rectangle
     */
    public void setX (float x) {
        this.sprite.setX(x);
        this.feet.setX(x + 4*scale);
        this.eventHitbox.setX(this.sprite.getX() - (this.eventHitbox.getWidth() - sprite.getWidth()) / 2);
        this.recalcCentre();
    }
    /**
     * Sets the Y coordinate of the player, updating all 3 hitboxes at once as opposed to just the sprite rectangle
     */
    public void setY (float y) {
        this.sprite.setY(y);
        this.feet.setY(y);
        this.eventHitbox.setY(this.sprite.getY() - (this.eventHitbox.getHeight() - sprite.getHeight()) / 2);
        this.recalcCentre();
    }

    /**
     *
     * @param x The X coordinate to set the player to
     * @param y The Y coordinate to set the player to
     */
    public void setPos (float x, float y) {
        this.setX(x);
        this.setY(y);
    }

    /**
     * Set a large rectangle that the player should be kept inside, set to null to set no bounds
     *
     * @param bounds The bounds of the playable map
     */
    public void setBounds (Rectangle bounds) {
        // Set a rectangle that the player should not leave
        this.bounds = bounds;
    }

    /**
     * Returns the euclidian distance from a GameObject to the centre of the player
     *
     * @param object The object to get the distance from
     * @return The distance from the object
     */
    private float distanceFrom (GameObject object) {
        return (float) Math.sqrt((Math.pow((centreX - object.centreX), 2) + Math.pow((centreY - object.centreY), 2)));
    }

    /**
     * Recalculates the centre of the player, useful after moving the player
     */
    private void recalcCentre() {
        centreX = sprite.getX() + sprite.getWidth() / 2;
        centreY = sprite.getY() + sprite.getHeight() / 2;
    }

    /**
     * Sets the player to frozen, a frozen player can be set to ignore keyboard inputs in render
     *
     * @param freeze The value to set frozen to
     */
    public void setFrozen (boolean freeze) {
        this.frozen = freeze;
        if (freeze) {
            // Set to non-moving frame
            currentFrame = idleAnimation.get(direction).getKeyFrame(stateTime);
        }
    }

    /**
     * @return true if the player is frozen
     */
    public boolean isFrozen () {
        return this.frozen;
    }

}
