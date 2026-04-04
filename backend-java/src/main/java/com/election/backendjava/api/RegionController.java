package com.election.backendjava.api;

import com.election.backendjava.model.Region;
import com.election.backendjava.repository.RegionJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/regions")
public class RegionController {

    @Autowired
    private RegionJPARepository regionRepo;

    @PostMapping
    public Region addRegion(@RequestBody Region region) {
        return regionRepo.save(region);
    }

    @GetMapping
    public List<Region> getAllRegions() {
        return regionRepo.findAll();
    }

    @GetMapping("/{id}")
    public Region getRegionById(@PathVariable Long id) {
        return regionRepo.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteRegion(@PathVariable Long id) {
        regionRepo.deleteById(id);
    }
}

