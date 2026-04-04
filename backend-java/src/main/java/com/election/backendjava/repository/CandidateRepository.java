package com.election.backendjava.repository;

import com.election.backendjava.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findByElectionYear(int year);
    /**
     * Zoekt kandidaten op basis van het verkiezingsjaar en de exacte naam van de partij.
     * Aanname: Je 'Candidate' entity heeft een relatie 'party' (naar een Party entity)
     * en die Party entity heeft een veld 'name'.
     */
    @Query("SELECT c FROM Candidate c WHERE c.electionYear = :year AND c.partyname = :partyName")
    List<Candidate> findByElectionYearAndPartyName(@Param("year") int year, @Param("partyName") String partyName);
}