package com.election.backendjava.model;

import java.util.ArrayList;
import java.util.List;

public class Party {
    private final String name;
    private int totalVotes;
    private final List<Candidate> candidates = new ArrayList<>();

    public Party(String name, int totalVotes) {
        this.name = name;
        this.totalVotes = totalVotes;
    }
    public Party(String name) {
        this.name = name;
    }

    public void addVotes(int votes) {
        this.totalVotes += votes;
    }

    public String getName() {
        return name;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }
}
