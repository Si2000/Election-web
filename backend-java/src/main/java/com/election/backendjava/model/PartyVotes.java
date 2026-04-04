package com.election.backendjava.model;


public class PartyVotes {
    private String partyId;
    private String partyName;
    private String place;
    private String placeId;
    private int totalVotes;
    private int electionYear;

    public PartyVotes(String partyId, String partyName, String place, String placeId, int totalVotes, int electionYear) {
        this.partyId = partyId;
        this.partyName = partyName;
        this.place = place;
        this.placeId = placeId;
        this.totalVotes = totalVotes;
        this.electionYear = electionYear;
    }

    public String getPlaceId() { return placeId; }
    public String getPartyId() { return partyId; }
}