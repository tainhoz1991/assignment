package com.oyika.assignment.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.oyika.assignment.dto.ImageDTO;
import com.oyika.assignment.dto.ImageResponse;
import com.oyika.assignment.mapper.ImageMapper;
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
    @Autowired
    private ImageMapper imageMapper;

    @Value("${s3.bucket_name}")
    private String s3BucketName;

    private final AmazonS3Client s3Client;

    @Autowired
    private ImageRepository imageRepository;

    public ImageService(AmazonS3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void processImage(List<ImageDTO> image) {
        if (image.isEmpty()) {
            return;
        }
        //TODO: loop and check duplicate image
        List<ImageDTO> newList = new ArrayList<>();
        for (ImageDTO img : image) {
            List<Image> exists = imageRepository.findByEtag(img.getEtag());
            if (!exists.isEmpty()) {
                continue;
            }
            newList.add(img);
        }
        imageRepository.saveAll(imageMapper.toEntity(newList));

    }

    public List<ImageResponse> findAll() {
        List<Image> entities = imageRepository.findAll();
        return imageMapper.toObjResponse(entities);
    }

    public Map<String, List<ImageResponse>> objectListingAndSaving() {
        Map<String, List<ImageResponse>> result = new HashMap<>();
        ListObjectsV2Result listObjectsV2 = s3Client.listObjectsV2(s3BucketName);
        if (listObjectsV2.getObjectSummaries().isEmpty()) {
            return null;
        }

        List<ImageDTO> items = listObjectsV2.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .map(key -> mapS3ObjectToImage(s3BucketName, key))
                .collect(Collectors.toList());

        List<ImageResponse> duplicatedList = new ArrayList<>();
        List<ImageDTO> newList = new ArrayList<>();
        for (ImageDTO image : items) {
            List<Image> exists = imageRepository.findByEtag(image.getEtag());
            if (!exists.isEmpty()) {
                duplicatedList.add(imageMapper.toObjResponse(exists.get(0)));
                continue;
            }
            newList.add(image);
        }
        List<Image> newEntities = imageRepository.saveAll(imageMapper.toEntity(newList));
        result.put("newList", imageMapper.toObjResponse(newEntities));
        result.put("duplicatedList", duplicatedList);

        return result;
    }

    private ImageDTO mapS3ObjectToImage(String bucket, String key) {
        return ImageDTO.builder()
                .bucketName(bucket)
                .fileName(key)
                .filePath(s3Client.getUrl(bucket, key).toString())
                .etag(s3Client.getObjectMetadata(bucket, key).getETag())
                .build();
    }
}
