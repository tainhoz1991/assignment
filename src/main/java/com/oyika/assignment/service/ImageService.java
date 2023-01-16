package com.oyika.assignment.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.oyika.assignment.model.Image;
import com.oyika.assignment.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImageService {

    public static Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Value("${s3.bucket_name}")
    private String s3BucketName;

    private final AmazonS3Client s3Client;

    @Autowired
    private ImageRepository imageRepository;

    public ImageService(AmazonS3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void processImage(List<Image> image) {
        if (image.isEmpty()) {
            return;
        }
        //TODO: loop and check duplicate image
        imageRepository.saveAll(image);
    }

    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    public Map<String, List<Image>> objectListingAndSaving() {
        Map<String, List<Image>> result = new HashMap<>();
        ListObjectsV2Result listObjectsV2 = s3Client.listObjectsV2(s3BucketName);
        if (listObjectsV2.getObjectSummaries().isEmpty()) {
            return null;
        }

        List<Image> items = listObjectsV2.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .map(key -> mapS3ObjectToImage(s3BucketName, key))
                .collect(Collectors.toList());

        List<Image> duplicatedList = new ArrayList<>();
        List<Image> newList = new ArrayList<>();
        for (Image image : items) {
            List<Image> exists = imageRepository.findByEtag(image.getEtag());
            if (!exists.isEmpty()) {
                duplicatedList.add(exists.get(0));
                continue;
            }
            newList.add(image);
        }
        imageRepository.saveAll(newList);
        result.put("newList", newList);
        result.put("duplicatedList", duplicatedList);

        return result;
    }

    private Image mapS3ObjectToImage(String bucket, String key) {
        return Image.builder()
                .bucketName(bucket)
                .fileName(key)
                .filePath(s3Client.getUrl(bucket, key).toString())
                .etag(s3Client.getObjectMetadata(bucket, key).getETag())
                .build();
    }
}
