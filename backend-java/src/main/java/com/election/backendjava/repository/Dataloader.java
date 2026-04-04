package com.election.backendjava.repository;

import com.election.backendjava.model.Election;
import com.election.backendjava.service.DutchElectionService;
import com.election.backendjava.service.ElectionCacheService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Dataloader implements CommandLineRunner {

    private final DutchElectionService electionService;
    private final CandidateRepository candidateRepo;
    private final RawVoteRepository rawVoteRepo;
    private final ElectionCacheService cacheService;

    public Dataloader(DutchElectionService electionService,
                      CandidateRepository candidateRepo,
                      RawVoteRepository rawVoteRepo,
                      ElectionCacheService cacheService) {
        this.electionService = electionService;
        this.candidateRepo = candidateRepo;
        this.rawVoteRepo = rawVoteRepo;
        this.cacheService = cacheService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("started dataloader");

//        loadAndSave("TK2023", "TK2023-Partial");

//        loadAndSave("TK2021", "TK2021-Partial");
//
//        loadAndSave("TK2025", "TK2025-Partial");

//        System.out.println("dataloader is finished");
        cacheService.buildCache();
    }

    private void loadAndSave(String electionId, String folderName) {
        System.out.println(">>> Processing " + electionId + " from folder " + folderName + "...");
        Election election = electionService.readResults(electionId, folderName);

        if (election != null) {
            // Sla kandidaten op in batches
            if (!election.getCandidates().isEmpty()) {
                System.out.println("   Saving " + election.getCandidates().size() + " candidates...");
                saveInBatches(candidateRepo, election.getCandidates());
            }

            // Sla stemmen op in batches
            if (!election.getRawVotes().isEmpty()) {
                System.out.println("   Saving " + election.getRawVotes().size() + " votes...");
                saveInBatches(rawVoteRepo, election.getRawVotes());
            }
        } else {
            System.out.println("!!! Election " + electionId + " not found or empty !!!");
        }
    }

    private <T> void saveInBatches(JpaRepository<T, ?> repo, List<T> entities) {
        if (entities == null || entities.isEmpty()) return;

        int batchSize = 5000;
        int total = entities.size();

        for (int i = 0; i < total; i += batchSize) {
            int end = Math.min(i + batchSize, total);
            List<T> batch = entities.subList(i, end);

            repo.saveAll(batch);
            repo.flush();

            if (end % 100000 == 0 || end == total) {
                System.out.println("      -> Saved " + end + " / " + total + " records...");
            }
        }
    }
}