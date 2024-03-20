package com.skloch.game;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;

/**
 * An object that stores a position and dimensions, but can also store extra properties when loaded from a tilemap.
 * Player can interact and trigger events with these objects.
 */
public class GameObject extends Rectangle {
    public MapProperties properties;

    public float centreX;

    public float centreY;



    // Create like a normal rectangle

    /**
     * Creates an instance of a GameObject without MapProperties, functions similarily to just a normal rectangle
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public GameObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        properties = new MapProperties();

        centreX = x + width / 2;
        centreY = y + height / 2;
    }

    /**
     * Creates a GameObject using the MapProperties stored in a object when exported with the Tiled map editor
     * Sets x, y, width and height, as well as loading all other properties which can be fetched with get()
     *
     * @param objectProperties An instance of MapProperties loaded from an object layer
     * @param scale How much do scale the object's coordinates by, if the map is also scaled up
     */
    public GameObject(MapProperties objectProperties, float scale) {
        super(
                (float) objectProperties.get("x") * scale,
                (float) objectProperties.get("y") * scale,
                (float) objectProperties.get("width") * scale,
                (float) objectProperties.get("height") * scale
        );
        properties = objectProperties;

        centreX = x + width / 2;
        centreY = y + height / 2;
    }


    /**
     * Puts an Object class into the GameObject's properties
     * @param key The key of the object
     * @param value The object to pass
     */
    public void put(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * Gets a property from a key
     * @param key
     * @return The property as an object, needs to be cast
     */
    public Object get(String key) {
        return properties.get(key);
    }

    /**
     * @param key A key to be checked in the object's properties
     * @return True if the object has a property with this key
     */
    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    /**
     * Sets the new Y coordinate of the centre of the object
     * @param y
     */
    public void setCentreY(float y) {
        this.centreY = y;
    }

    /**
     * Sets the new X coordinate of the centre of the object
     * @param x
     */
    public void setCentreX(float x) {
        this.centreX = x;
    }


}
