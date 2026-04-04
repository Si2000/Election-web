package com.election.backendjava.dto;


public class CandidateDTO {
    private String id;
    private String name;
    private String partyName;
    private int votes;

    public CandidateDTO(String id, String name, String partyName, int votes) {
        this.id = id;
        this.name = name;
        this.partyName = partyName;
        this.votes = votes;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPartyName() { return partyName; }
    public int getVotes() { return votes; }

}