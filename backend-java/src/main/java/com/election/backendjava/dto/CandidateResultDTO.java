package com.election.backendjava.dto;

public class CandidateResultDTO {
    private long id;
    private String shortCode;
    private String partyName;
    private String place;
    private String candidateVote;

    public CandidateResultDTO(long id, String shortCode, String partyName, String place, String candidateVote) {
        this.id = id;
        this.shortCode = shortCode;
        this.partyName = partyName;
        this.place = place;
        this.candidateVote = candidateVote;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
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

    public String getCandidateVote() {
        return candidateVote;
    }

    public void setCandidateVote(String candidateVote) {
        this.candidateVote = candidateVote;
    }
}
