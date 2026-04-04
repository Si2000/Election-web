package com.election.backendjava.utils.xml.transformers;

import com.election.backendjava.model.Election;
import com.election.backendjava.utils.xml.CandidateTransformer;
import com.election.backendjava.utils.xml.TagAndAttributeNames;

import com.election.backendjava.model.Candidate;
import java.util.Map;

/**
 * Handles candidate data from XML and stores it in the Election object.
 */
public class DutchCandidateTransformer implements CandidateTransformer, TagAndAttributeNames {

    private final Election election;

    /**
     * Creates a new transformer for handling candidate lists.
     *
     * @param election the election where candidates will be stored
     */
    public DutchCandidateTransformer(Election election) {
        this.election = election;
    }

    /**
     * Registers a candidate using XML data.
     *
     * @param electionData the candidate data extracted from XML
     */
    @Override
    public void registerCandidate(Map<String, String> electionData) {
        addCandidateToList(electionData);
    }

    /**
     * Adds a candidate to the main election list.
     *
     * @param electionData the candidate data extracted from XML
     */
    private void addCandidateToList(Map<String, String> electionData) {

        String partyId = electionData.get(AFFILIATION_IDENTIFIER + "-" + ID);

        String partyname = electionData.get(REGISTERED_NAME);
        String firstName = electionData.get(FIRST_NAME);
        String lastName = electionData.get(LAST_NAME);
        String initials = electionData.get(NAME_LINE);
        String gender = electionData.get(GENDER);
        String localityname = electionData.get(LOCALITY_NAME);
        String candidateId = electionData.get(CANDIDATE_IDENTIFIER + "-" + ID);

        int electionYear = election.getYear();


        Candidate candidate = new Candidate(partyId, partyname, firstName, lastName, initials, gender, localityname, electionYear, candidateId);


        election.getCandidates().add(candidate);
    }

    /**
     * Builds a full name using first name, prefix, and last name.
     * (Deze methode wordt niet meer aangeroepen, maar mag blijven staan)
     *
     * @param firstName   candidate's first name
     * @param namePrefix  candidate's prefix (e.g., "van", "de")
     * @param lastName    candidate's last name
     * @return combined full name
     */
    private String buildFullName(String firstName, String namePrefix, String lastName) {
        StringBuilder fullName = new StringBuilder();

        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName.append(firstName).append(" ");
        }

        if (namePrefix != null && !namePrefix.trim().isEmpty()) {
            fullName.append(namePrefix).append(" ");
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            fullName.append(lastName);
        }

        return fullName.toString().trim();
    }
}