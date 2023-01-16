package com.oyika.assignment.controller;

import com.oyika.assignment.model.Image;
import com.oyika.assignment.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/images")
public class ImageController {

    public static Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    ImageService imageService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Image>> fetchImages() {
        List<Image> listImages = imageService.findAll();

        imageService.objectListingAndSaving();
        if (listImages.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Image>>(listImages, HttpStatus.OK);
    }

    @RequestMapping(value = "/scan", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<Image>>> scan() {

        Map<String, List<Image>> resp = imageService.objectListingAndSaving();
        if (resp.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Map<String, List<Image>>>(resp, HttpStatus.OK);
    }
}
