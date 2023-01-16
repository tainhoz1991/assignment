package com.oyika.assignment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oyika.assignment.model.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class S3BucketListener {

    private static final Logger logger = LoggerFactory.getLogger(S3BucketListener.class);

    @Autowired
    private ImageService imageService;

    @SqsListener("${sqs.queue.name}")
    public void process(String payload, @Headers Map<String, Object> payloadHeaders) {
        logger.info("Incoming order payload {} with headers {}", payload, payloadHeaders);
        List<Image> imgs = getNewImageInfoFromSQSEvent(payload);
        imageService.processImage(imgs);
    }

    private List<Image> getNewImageInfoFromSQSEvent(String event) {
        ObjectMapper objectMapper = new ObjectMapper();
        S3EventNotification s3Event = null;
        try {
            s3Event = objectMapper.readValue(event, S3EventNotification.class);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        List<S3EventNotificationRecord> records = s3Event.getRecords();
        //Assume event has only one record
        if (records.isEmpty()) return null;
        List<Image> result = new ArrayList<>();
        for (S3EventNotificationRecord r : records) {

            // only process create object event
            if (!r.getEventName().equals("ObjectCreated:Put")) {
                return null;
            }
            String region = r.getAwsRegion();
            String bucketName = r.getS3().getBucket().getName();
            String fileName = r.getS3().getObject().getKey();
            String etag = r.getS3().getObject().geteTag();

            StringBuilder builder = new StringBuilder();
            builder.append("https://")
                    .append(bucketName)
                    .append(".s3.")
                    .append(region)
                    .append(".amazonaws.com/")
                    .append(fileName);
            Image img = new Image();
            img.setBucketName(bucketName);
            img.setFilePath(builder.toString());
            img.setEtag(etag);
            img.setFileName(fileName);
            result.add(img);
        }

        return result;
    }
}
