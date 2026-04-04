package com.election.backendjava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long dbId;

    private String partyId;

    private String candidateid;
    private String partyname;
    private String firstname;
    private String lastname;
    private String initials;
    private String gender;
    private String localityname;
    private String name;
    private String votes;
    private int electionYear;

    public Candidate(String partyId, String partyname, String firstname, String lastname, String initials, String gender, String localityname, int electionYear, String candidateid) {
        this.partyId = partyId;
        this.partyname = partyname;
        this.firstname = firstname;
        this.lastname = lastname;
        this.initials = initials;
        this.gender = gender;
        this.localityname = localityname;
        this.electionYear = electionYear;
        this.candidateid = candidateid;
    }

    public Candidate() {
    }

    public Candidate(String name, String votes) {
        this.name = name;
        this.votes = votes;
    }


    public long getDbId(){
        return dbId;
    }

    public String getPartyId() {
        return partyId;
    }

    public String getCandidateid() {
        return candidateid;
    }

    public String getParty() {
        return partyname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getInitials() {
        return initials;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getVotes() {
        return votes;
    }

    public String getFullName() {return firstname + " " + lastname;}

    public String getLocality() {
        return localityname;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public int getElectionYear() {
        return electionYear;
    }

    public void setElectionYear(int electionYear) {
        this.electionYear = electionYear;
    }

    public void setCandidateid(String candidateid) {
        this.candidateid = candidateid;
    }

    public void setFirstname(String first) {
    }

    public void setLastname(String last) {
    }

    public void setParty(String party) {
    }

    public void setGender(String gender) {
    }
}