//package com.election.backendjava.utils.xml.transformers;
//
//import com.election.backendjava.model.*;
//import com.election.backendjava.utils.xml.TagAndAttributeNames;
//import com.election.backendjava.utils.xml.VotesTransformer;
//
//import java.util.Map;
//
//public class DutchNationalVotesTransformer implements VotesTransformer, TagAndAttributeNames {
//
//    private final Election election;
//    private PartyResult currentPartyResult;
//
//    public DutchNationalVotesTransformer(Election election) {
//        this.election = election;
//    }
//
//    @Override
//    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
//
//        String partyId = electionData.get("AffiliationIdentifier-Id");
//        String partyName = electionData.get(REGISTERED_NAME);
//        String totalVote = electionData.get(VALID_VOTES);
//        String placeId = electionData.get("AuthorityIdentifier-Id");
//        String place = electionData.get("AuthorityIdentifier");
//
//        if (partyId != null && totalVote != null) {
//
//            currentPartyResult = new PartyResult(
//                    partyId,
//                    partyName,
//                    place,
//                    placeId,
//                    totalVote
//            );
//
//            // Belangrijk! Wordt later in DB geschreven via dataloader
//            election.getPartyResults().add(currentPartyResult);
//        }
//    }
//
//    @Override
//    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
//
//        String candidateId = electionData.get(CANDIDATE_IDENTIFIER_SHORT_CODE);
//        String candidateVote = electionData.get(VALID_VOTES);
//
//        if (candidateId != null && candidateVote != null && currentPartyResult != null) {
//
//            CandidateResult candidate = new CandidateResult(
//                    candidateId,
//                    candidateVote,
//                    currentPartyResult
//            );
//
//            // Bidirectioneel JPA koppelen
//            currentPartyResult.addCandidate(candidate);
//
//            // Later opgeslagen via cascade & dataloader
//            election.getCandidateResults().add(candidate);
//        }
//    }
//
//    @Override
//    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
//        String cast = electionData.get(CAST);
//        String totalCounted = electionData.get(TOTAL_COUNTED);
//        String rejectedInvalid = electionData.get("RejectedVotes-ongeldig");
//        String rejectedBlank = electionData.get("RejectedVotes-blanco");
//        String uncountedValidProxies = electionData.get("UncountedVotes-geldige volmachtbewijzen");
//        String uncountedOvercountedBallots = electionData.get("UncountedVotes-meer getelde stembiljetten");
//        String uncountedUndercountedBallots = electionData.get("UncountedVotes-minder getelde stembiljetten");
//        String uncountedTakenBallots = electionData.get("UncountedVotes-meegenomen stembiljetten");
//        String uncountedTooFewIssuedBallots = electionData.get("UncountedVotes-te weinig uitgereikte stembiljetten");
//        String uncountedTooManyIssuedBallots = electionData.get("UncountedVotes-te veel uitgereikte stembiljetten");
//        String uncountedNoExplanation = electionData.get("UncountedVotes-geen verklaring");
//        String uncountedOtherExplanation = electionData.get("UncountedVotes-andere verklaring");
//
//        MetaData metadata = new MetaData(
//                Integer.parseInt(cast),
//                Integer.parseInt(totalCounted),
//                Integer.parseInt(rejectedInvalid),
//                Integer.parseInt(rejectedBlank),
//                Integer.parseInt(uncountedValidProxies),
//                Integer.parseInt(uncountedOvercountedBallots),
//                Integer.parseInt(uncountedUndercountedBallots),
//                Integer.parseInt(uncountedTakenBallots),
//                Integer.parseInt(uncountedTooFewIssuedBallots),
//                Integer.parseInt(uncountedTooManyIssuedBallots),
//                Integer.parseInt(uncountedNoExplanation),
//                Integer.parseInt(uncountedOtherExplanation)
//        );
//
//        election.getMetaDataResults().add(metadata);
//    }
//}
