package com.election.backendjava.repository;

import com.election.backendjava.model.RawVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawVoteRepository extends JpaRepository<RawVote, Long> {
    List<RawVote> findByElectionYear(int year);
}