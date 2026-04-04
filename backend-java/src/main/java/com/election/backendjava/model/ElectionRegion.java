package com.election.backendjava.model;

import java.util.HashMap;
import java.util.Map;

public class ElectionRegion {

    private String id;
    private String name;
    private String category;
    private ElectionRegion parent;
    private Map<String, ElectionRegion> children = new HashMap<>();

    private int totalVotes = 0;
    private Map<String, Integer> votesPerParty = new HashMap<>();


    private Map<String, Integer> votesPerCompositeKey = new HashMap<>();

    public ElectionRegion(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public void addVote(int count, String partyId, String partyName, String candidateId) {
        this.totalVotes += count;
        this.votesPerParty.merge(partyName, count, Integer::sum);

        String compositeKey = partyId + "_" + candidateId;
        this.votesPerCompositeKey.merge(compositeKey, count, Integer::sum);

        if (this.parent != null) {
            this.parent.addVote(count, partyId, partyName, candidateId);
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getTotalVotes() { return totalVotes; }
    public Map<String, ElectionRegion> getChildren() { return children; }
    public Map<String, Integer> getVotesPerParty() { return votesPerParty; }

    public Map<String, Integer> getVotesPerCompositeKey() {
        return votesPerCompositeKey;
    }

    public void setParent(ElectionRegion parent) { this.parent = parent; }
    public void addChild(ElectionRegion child) {
        this.children.put(child.getId(), child);
        child.setParent(this);
    }
    public void setVotesPerParty(Map<String, Integer> votesPerParty) {
        this.votesPerParty = votesPerParty;
    }

    public ElectionRegion getParent() { return parent; }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public void setVotesPerCompositeKey(HashMap<Object, Object> objectObjectHashMap) {
    }
}