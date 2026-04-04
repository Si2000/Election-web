package com.election.backendjava.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This will hold the information for one specific election.<br/>
 * <b>This class is by no means production ready! You need to alter it extensively!</b>
 */
public class Election {
    private final String id;
    private int year;


    private final List<Party> parties = new ArrayList<Party>();
    private final List<Region> regions = new ArrayList<>();
    private final List<Municipality> municipalities = new ArrayList<>();
    private final List<Constituency> constituencies = new ArrayList<>();
    private final Map<String, String> candidateIdToName = new HashMap<>();
    private ElectionMetadata metadata;
    private List<MetaData>  metaDataResults = new ArrayList<>();
    private final List<RawVote> rawVotes = new ArrayList<>();

    private final List<Candidate> candidates = new ArrayList<>();

    public Election(String id) {
        this.id = id;
        this.parseYearFromId();
    }

    private void parseYearFromId() {
        try {
            String yearString = this.id.replaceAll("[^0-9]", "");
            if (yearString.length() >= 4) {
                this.year = Integer.parseInt(yearString.substring(0, 4));
            }
        } catch (NumberFormatException e) {
            System.err.println("Kon het jaartal niet correct parsen uit electionId: " + this.id);
            this.year = 0;
        }
    }


    @Override
    public String toString() {
        return "You have to create a proper election model yourself!";
    }

    public String getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public List<Party> getParties() {
        return parties;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public List<Municipality> getMunicipalities() {
        return municipalities;
    }

    public List<Constituency> getConstituencies() {
        return constituencies;
    }

    public Map<String, String> getCandidateIdToName() {
        return candidateIdToName;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void addCandidateMapping(String candidateId, String candidateName) {
        candidateIdToName.put(candidateId, candidateName);
    }


    public List<MetaData> getMetaDataResults() {
        return metaDataResults;
    }

    public void setMetaDataResults(List<MetaData> metaDataResults) {
        this.metaDataResults = metaDataResults;
    }

    public List<RawVote> getRawVotes() {
        return rawVotes;
    }

}