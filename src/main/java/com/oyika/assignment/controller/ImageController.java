package com.oyika.assignment.controller;

import com.oyika.assignment.dto.ImageResponse;
import com.oyika.assignment.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/images")
public class ImageController {

    public static Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    ImageService imageService;

    @GetMapping
    public ResponseEntity<List<ImageResponse>> fetchImages() {
        List<ImageResponse> listImages = imageService.findAll();
        if (listImages.isEmpty()) {
            imageService.objectListingAndSaving();
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(listImages, HttpStatus.OK);
    }

    @GetMapping("/sync")
    public ResponseEntity<Map<String, List<ImageResponse>>> scan() {

        Map<String, List<ImageResponse>> resp = imageService.objectListingAndSaving();
        if (resp.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
