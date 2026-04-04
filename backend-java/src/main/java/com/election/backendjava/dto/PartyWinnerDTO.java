package com.election.backendjava.dto;

import java.util.Map;

public class PartyWinnerDTO {
    private String place;
    private String partyName;
    private int totalVotes;
    private final Map<String, Integer> losers;

    public PartyWinnerDTO(String place, String partyName, int totalVotes,  Map<String, Integer> losers) {
        this.losers = losers;
        this.place = place;
        this.partyName = partyName;
        this.totalVotes = totalVotes;
    }

    public Map<String, Integer> getLosers() {
        return losers;
    }
    public String getPlace() { return place; }
    public String getPartyName() { return partyName; }
    public int getTotalVotes() { return totalVotes; }
}
