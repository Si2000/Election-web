package com.election.backendjava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(indexes = {
        @Index(columnList = "logicalNumber", unique = true)
})
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String logicalNumber;

    private String regionName;
    private String category;

    private String superLogicalNumber;

    public Region() {}

    public Region(String logicalNumber, String regionName, String category, String superLogicalNumber) {
        this.logicalNumber = logicalNumber;
        this.regionName = regionName;
        this.category = category;
        this.superLogicalNumber = superLogicalNumber;
    }

    public Long getId() { return id; }
    public String getLogicalNumber() { return logicalNumber; }
    public String getRegionName() { return regionName; }
    public String getCategory() { return category; }
    public String getSuperNumber() { return superLogicalNumber; }
    public String setRegionName(String regionName) { return this.regionName = regionName; }
}