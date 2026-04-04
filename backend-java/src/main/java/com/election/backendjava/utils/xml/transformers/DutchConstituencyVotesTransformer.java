//package com.election.backendjava.utils.xml.transformers;
//
//import com.election.backendjava.model.*;
//import com.election.backendjava.utils.xml.TagAndAttributeNames;
//import com.election.backendjava.utils.xml.VotesTransformer;
//
//import java.util.Map;
//
//public class DutchConstituencyVotesTransformer implements VotesTransformer, TagAndAttributeNames {
//    private final Election election;
//    private final int electionYear;
//
//    private PartyVote currentPartyVote;
//
//    public DutchConstituencyVotesTransformer(Election election) {
//        this.election = election;
//        this.electionYear = election.getYear();
//    }
//
//    @Override
//    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
//        String partyId = electionData.get(AFFILIATION_IDENTIFIER + "-" + ID);
//        String partyName = electionData.get(REGISTERED_NAME);
//        int totalVote = Integer.parseInt(electionData.get(VALID_VOTES));
//
//        String place;
//        String placeId;
//        if (aggregated) {
//            placeId = electionData.get(AUTHORITY_IDENTIFIER + "-" + ID);
//            place = electionData.get("AuthorityIdentifier");
//        } else {
//            placeId = electionData.get(REPORTING_UNIT_IDENTIFIER + "-" + ID);
//            place = electionData.get(REPORTING_UNIT_IDENTIFIER);
//        }
//
//        if (partyId != null && totalVote != 0) {
//            this.currentPartyVote = new PartyVote(partyId, partyName, place, placeId, totalVote, this.electionYear);
//            election.getPartyVotes().add(this.currentPartyVote);
//        }
//    }
//
//    @Override
//    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
//        String candidateid = electionData.get(CANDIDATE_IDENTIFIER_ID);
//        String candidateVote = electionData.get(VALID_VOTES);
//
//        if (candidateid != null && candidateVote != null && this.currentPartyVote != null) {
//            CandidateVote vote = new CandidateVote(candidateid, candidateVote);
//
//            this.currentPartyVote.addCandidateVote(vote);
//        }
//    }
//
//    @Override
//    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
//    }
//}