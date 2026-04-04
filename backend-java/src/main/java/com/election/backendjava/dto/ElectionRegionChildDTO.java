package com.election.backendjava.dto;

import com.election.backendjava.model.ElectionRegion;

/**
 * DTO representing a child region within the election hierarchy.
 * Contains basic region information used in API responses.
 */
public class ElectionRegionChildDTO {
    private String id;
    private String name;
    private String category;
    private int totalVotes;

    /**
     * Creates a DTO from an ElectionRegion domain object.
     * Maps only essential child-region fields.
     *
     * @param node the election region to convert
     */
    public ElectionRegionChildDTO(ElectionRegion node) {
        this.id = node.getId();
        this.name = node.getName();
        this.category = node.getCategory();
        this.totalVotes = node.getTotalVotes();
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public String getCategory() { return category; }

    public int getTotalVotes() { return totalVotes; }
}