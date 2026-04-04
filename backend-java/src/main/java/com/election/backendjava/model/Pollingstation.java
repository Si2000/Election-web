package com.election.backendjava.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pollingstation implements Parties{
    private final String id;
    private final String name;
    private final List<Party> parties = new ArrayList<>();
    private final Map<String, String> metadata = new HashMap<>();


    public Pollingstation(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    @Override
    public List<Party> getParties(){
        return parties;
    }

    public Map<String, String> getMetadata(){
        return metadata;
    }

    public void addAllMetadata(Map<String, String> metadata) {
        this.metadata.putAll(metadata);
    }
}
