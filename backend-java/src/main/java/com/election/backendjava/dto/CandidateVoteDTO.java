package com.election.backendjava.dto;

public class CandidateVoteDTO {
    private long id;
    private String candidateId;
    private String candidateVote;
    private String partyName;
    private String place;
    private int electionYear;

    public CandidateVoteDTO() {}

    public CandidateVoteDTO(long id, String candidateId, String candidateVote, String partyName, String place, int electionYear) {
        this.id = id;
        this.candidateId = candidateId;
        this.candidateVote = candidateVote;
        this.partyName = partyName;
        this.place = place;
        this.electionYear = electionYear;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getCandidateVote() { return candidateVote; }
    public void setCandidateVote(String candidateVote) { this.candidateVote = candidateVote; }

    public String getPartyName() { return partyName; }
    public void setPartyName(String partyName) { this.partyName = partyName; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public int getElectionYear() { return electionYear; }
    public void setElectionYear(int electionYear) { this.electionYear = electionYear; }
}
