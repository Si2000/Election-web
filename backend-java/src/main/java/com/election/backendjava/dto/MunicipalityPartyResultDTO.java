package com.election.backendjava.dto;

public class MunicipalityPartyResultDTO {
    private String id;          // gemeente-ID, intern voor navigatie
    private String name;        // naam van de gemeente
    private String party;       // partijnaam
    private int votes;          // aantal stemmen

    public MunicipalityPartyResultDTO(String id, String name, String party, int votes) {
        this.id = id;
        this.name = name;
        this.party = party;
        this.votes = votes;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getParty() { return party; }
    public int getVotes() { return votes; }
}
