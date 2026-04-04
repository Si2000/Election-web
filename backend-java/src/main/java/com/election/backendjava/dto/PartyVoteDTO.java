package com.election.backendjava.dto;

import java.util.List;

public class PartyVoteDTO {
    private long id;
    private String partyId;
    private String partyName;
    private String place;
    private String placeId;
    private int totalVotes;
    private int electionYear;
    private List<CandidateVoteDTO> candidateVotes;

    public PartyVoteDTO() {}

    public PartyVoteDTO(
            long id,
            String partyId,
            String partyName,
            String place,
            String placeId,
            int totalVotes,
            int electionYear,
            List<CandidateVoteDTO> candidateVotes
    ) {
        this.id = id;
        this.partyId = partyId;
        this.partyName = partyName;
        this.place = place;
        this.placeId = placeId;
        this.totalVotes = totalVotes;
        this.electionYear = electionYear;
        this.candidateVotes = candidateVotes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
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

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public int getElectionYear() {
        return electionYear;
    }

    public void setElectionYear(int electionYear) {
        this.electionYear = electionYear;
    }

    public List<CandidateVoteDTO> getCandidateVotes() {
        return candidateVotes;
    }

    public void setCandidateVotes(List<CandidateVoteDTO> candidateVotes) {
        this.candidateVotes = candidateVotes;
    }
}
