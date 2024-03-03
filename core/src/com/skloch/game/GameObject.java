package com.skloch.game;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;

public class GameObject extends Rectangle {
    public MapProperties properties;
    // Create like a normal rectangle
    public GameObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        properties = new MapProperties();
    }
    // Or create from a Mapobject's properties, and the scale of the game
    public GameObject(MapProperties objectProperties, float scale) {
        super(
                (float) objectProperties.get("x") * scale,
                (float) objectProperties.get("y") * scale,
                (float) objectProperties.get("width") * scale,
                (float) objectProperties.get("height") * scale
        );
        properties = objectProperties;
    }


    // Extending methods from properties
    public void put(String key, Object value) {
        properties.put(key, value);
    }
    public Object get(String key) {
        return properties.get(key);
    }
    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }


}
