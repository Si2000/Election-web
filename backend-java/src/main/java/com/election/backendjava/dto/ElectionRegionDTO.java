package com.election.backendjava.dto;

import com.election.backendjava.model.ElectionRegion;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DTO that represents a region in the election hierarchy.
 * Used to send structured region data to the frontend.
 */
public class ElectionRegionDTO {
    private String id;
    private String name;
    private String category;
    private int totalVotes;
    private Map<String, Integer> votesPerParty;
    private List<ElectionRegionChildDTO> children;

    /**
     * Creates a DTO from an ElectionRegion domain object.
     * Maps region details and converts all child regions into child DTOs.
     */
    public ElectionRegionDTO(ElectionRegion node) {
        this.id = node.getId();
        this.name = node.getName();
        this.category = node.getCategory();
        this.totalVotes = node.getTotalVotes();
        this.votesPerParty = node.getVotesPerParty();
        this.children = node.getChildren().values().stream()
                .map(ElectionRegionChildDTO::new)
                .collect(Collectors.toList());
    }
    public String getId() { return id; }

    public String getName() { return name; }

    public String getCategory() { return category; }

    public int getTotalVotes() { return totalVotes; }

    public Map<String, Integer> getVotesPerParty() { return votesPerParty; }

    public List<ElectionRegionChildDTO> getChildren() { return children; }
}