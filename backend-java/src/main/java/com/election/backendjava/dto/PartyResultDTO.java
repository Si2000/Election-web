package com.election.backendjava.dto;

import java.util.List;

public class PartyResultDTO {
    private long id;
    private String partyName;
    private String place;
    private String totalVotes;
    private List<CandidateResultDTO> candidates;

    public PartyResultDTO(long id, String partyName, String place, String totalVotes, List<CandidateResultDTO> candidates) {
        this.id = id;
        this.partyName = partyName;
        this.place = place;
        this.totalVotes = totalVotes;
        this.candidates = candidates;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(String totalVotes) {
        this.totalVotes = totalVotes;
    }

    public List<CandidateResultDTO> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidateResultDTO> candidates) {
        this.candidates = candidates;
    }
}
