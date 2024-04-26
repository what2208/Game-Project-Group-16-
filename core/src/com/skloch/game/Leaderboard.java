package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Leaderboard {
    /*
    Structure of the leaderboard JSON file:
    {
        "leaderboard": [
            {
                "name": "John",
                "score": 100
            },
            {
                "name": "Jane",
                "score": 200
            },
            {
                "name": "Alice",
                "score": 150
            }
        ],
        "max_entries": 10
    }
    */

    private String FILENAME = "leaderboard.json";
    private List<LeaderboardEntry> leaderboardData;
    private int maxEntries;

    // Inner class to represent a single entry in the leaderboard
    private static class LeaderboardEntry {
        public String name;
        public int score;
    }

    public Leaderboard() {
        // Load the leaderboard from a JSON file
        loadLeaderboard();
    }

    public int GetLeaderboardLength() {
        return maxEntries;
    }

    public String GetName(int index) {
        if (index >= leaderboardData.size())
            return "---"; // Return an empty string if the index is out of bounds
        return leaderboardData.get(index).name;
    }

    public int GetScore(int index) {
        if (index >= leaderboardData.size())
            return 0; // Return 0 if the index is out of bounds
        return leaderboardData.get(index).score;
    }

    public String GetLeaderboardText() {
        StringBuilder leaderboardText = new StringBuilder();
        for (int i = 0; i < leaderboardData.size(); i++) {
            leaderboardText.append(i + 1).append(". ").append(leaderboardData.get(i).name).append(": ").append(leaderboardData.get(i).score).append("\n");
        }
        return leaderboardText.toString();
    }

    public void AddScore(String name, int score) {
        // Create a new entry
        LeaderboardEntry newEntry = new LeaderboardEntry();
        newEntry.name = name;
        newEntry.score = score;

        // Add the entry to the leaderboard
        leaderboardData.add(newEntry);

        // Sort the leaderboard by score
        Collections.sort(leaderboardData, new Comparator<LeaderboardEntry>() {
            @Override
            public int compare(LeaderboardEntry entry1, LeaderboardEntry entry2) {
                return Integer.compare(entry2.score, entry1.score);
            }
        });

        // Remove the last entry if the leaderboard is too long
        if (leaderboardData.size() > maxEntries) {
            leaderboardData.remove(leaderboardData.size() - 1);
        }

        // Save the updated leaderboard to the JSON file
        saveLeaderboard();
    }

    // Method to load the leaderboard from a JSON file
    private void loadLeaderboard() {
        // Load the JSON file. If it doesn't exist, create a new one with default values
        FileHandle file = Gdx.files.local(FILENAME);
        if (!file.exists()) {
            leaderboardData = new ArrayList<>();
            maxEntries = 10;
            saveLeaderboard();
            return;
        }
        Json json = new Json();
        JsonValue jsonData = new Json().fromJson(null, file.readString());

        // Accessing data from the JSON object
        leaderboardData = new ArrayList<>();
        JsonValue leaderboard = jsonData.get("leaderboard");
        maxEntries = jsonData.getInt("max_entries");
        for (JsonValue entry = leaderboard.child(); entry != null; entry = entry.next()) {
            // Parsing the entry
            LeaderboardEntry leaderboardEntry = json.fromJson(LeaderboardEntry.class, entry.toString());
            leaderboardData.add(leaderboardEntry);
        }

        // Sorting the leaderboard by score
        Collections.sort(leaderboardData, new Comparator<LeaderboardEntry>() {
            @Override
            public int compare(LeaderboardEntry entry1, LeaderboardEntry entry2) {
                return Integer.compare(entry2.score, entry1.score);
            }
        });
    }

    // Method to save the leaderboard to a JSON file
    private void saveLeaderboard() {
        // Create a JSON object
        Json json = new Json();
        JsonValue jsonData = new JsonValue(JsonValue.ValueType.object);

        // Add the leaderboard data to the JSON object
        JsonValue leaderboard = new JsonValue(JsonValue.ValueType.array);
        for (LeaderboardEntry entry : leaderboardData) {
            JsonValue entryValue = new JsonValue(json.toJson(entry));
            leaderboard.addChild(entryValue);
        }
        jsonData.addChild("leaderboard", leaderboard);

        // Add the maximum number of entries to the JSON object
        jsonData.addChild("max_entries", new JsonValue(maxEntries));

        // Write the JSON object to the file
        FileHandle file = Gdx.files.local(FILENAME);
        System.out.println(jsonData);
        file.writeString(jsonData.toString(), false);
    }
}
