package com.oyika.assignment.repository;

import com.oyika.assignment.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

//    @Query(value = "SELECT i FROM Image i WHERE i.etag = :etag")
//    public List<Image> findByCustomQuery(String etag);
    List<Image> findByEtag(String etag);
}
