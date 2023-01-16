package com.oyika.assignment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDTO {
    private String bucketName;
    private String fileName;
    private String filePath;
    private String etag;
}
