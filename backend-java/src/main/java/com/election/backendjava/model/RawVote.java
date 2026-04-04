package com.election.backendjava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(columnList = "logicalRegionId")
})
public class RawVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int electionYear;

    private String logicalRegionId;

    private String partyId;
    private String candidateId;
    private int voteCount;

    public RawVote() {}

    public RawVote(int electionYear, String logicalRegionId, String partyId, String candidateId, int voteCount) {
        this.electionYear = electionYear;
        this.logicalRegionId = logicalRegionId;
        this.partyId = partyId;
        this.candidateId = candidateId;
        this.voteCount = voteCount;
    }

    public Long getId() { return id; }
    public int getElectionYear() { return electionYear; }

    public String getRegionId() {
        return logicalRegionId;
    }

    public String getPartyId() { return partyId; }
    public String getCandidateId() { return candidateId; }
    public int getVoteCount() { return voteCount; }
}