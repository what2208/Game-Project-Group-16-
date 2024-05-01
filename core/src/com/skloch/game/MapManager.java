package com.skloch.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapManager {

    private final TmxMapLoader mapLoader;
    private final HashMap<String, TiledMap> loadedMaps;
    private TiledMap currentMap;
    private MapProperties mapProperties;
    private OrthogonalTiledMapRenderer mapRenderer;
    public int[] backgroundLayers, foregroundLayers, objectLayers;
    private int mapSquareSize;


    public MapManager() {
        mapLoader = new TmxMapLoader();
        loadedMaps = new HashMap<>();
    }

    public TiledMap loadMap(String mapPath) {
        TiledMap map = null;
        try {
            if (loadedMaps.containsKey(mapPath)) {
                map =  loadedMaps.get(mapPath);
            } else {
                map = mapLoader.load(mapPath);
                loadedMaps.put(mapPath, map);
            }
        } catch (Exception e) {
            // Throw exception
            throw new RuntimeException("Failed to load the map");
        }

        mapProperties = map.getProperties();
        currentMap = map;
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        backgroundLayers = getLayerArrayFromMapProperties("backgroundLayers");
        foregroundLayers = getLayerArrayFromMapProperties("foregroundLayers");
        objectLayers = getLayerArrayFromMapProperties("objectLayers");
        return map;
    }

    public TiledMap getCurrentMap() {
        if (currentMap == null) {
            throw new RuntimeException("No map loaded yet");
        }
        return currentMap;
    }

    public Vector2 getMapDimensions() {
        float width = mapProperties.get("width", Integer.class);
        float height = mapProperties.get("height", Integer.class);
        return new Vector2(width, height);
    }

    public Vector2 getMapPixelDimensions() {
        Vector2 mapUnitDimensions = getMapDimensions();
        float tileWidth = mapProperties.get("tilewidth", Integer.class);
        return new Vector2(
                mapUnitDimensions.x * tileWidth,
                mapUnitDimensions.y * tileWidth
        );
    }

    public List<GameObject> getObjects() {
        List<GameObject> allObjects = new ArrayList<>();
        for (int layer : objectLayers) {
            // Get all objects on the layer
            MapObjects objects = currentMap.getLayers().get(layer).getObjects();
            // Loop through each, handing them to the player
            for (int i = 0; i < objects.getCount(); i++) {
                // Get the properties of each object
                MapProperties properties = objects.get(i).getProperties();
                allObjects.add(new GameObject(properties));
            }
        }
        return allObjects;
    }

    public Vector2 getSpawn() {
        for (int layer : objectLayers) {
            // Get all objects on the layer
            MapObjects objects = currentMap.getLayers().get(layer).getObjects();
            // Loop through each, handing them to the player
            for (int i = 0; i < objects.getCount(); i++) {
                // Get the properties of each object
                MapProperties properties = objects.get(i).getProperties();
                if (properties.get("spawn") != null) {
                    float x = (float)properties.get("x");
                    float y = (float)properties.get("y");
                    return new Vector2(x, y);
                }
            }
        }
        throw new RuntimeException("Spawn not set");
    }

    public void setCamera(OrthographicCamera camera) {
        mapRenderer.setView(camera);
    }

    public void renderForeground() {
        mapRenderer.render(foregroundLayers);
    }

    public void renderBackground() {
        mapRenderer.render(backgroundLayers);
    }

    public void dispose() {
        // Iterate through all the maps and dispose of them
        for (TiledMap map : loadedMaps.values()) {
            map.dispose();
        }
        mapRenderer.dispose();
    }

    private int[] getLayerArrayFromMapProperties(String key) {
        // The map should have a property called for example "backgroundLayers" which is a comma separated list of integers.
        // Put these integers into an int array
        try {
            String[] layersString = mapProperties.get(key, String.class).split(",");
            if (layersString.length == 0 || layersString[0].isEmpty()) {
                return new int[0];
            } else {
                int[] layers = new int[layersString.length];
                for (int i = 0; i < layersString.length; i++) {
                    if (layersString[i].isEmpty()) {
                        continue;
                    }
                    layers[i] = Integer.parseInt(layersString[i]);
                }
                return layers;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading layer: "+key);
        }
    }
}
