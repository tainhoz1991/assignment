package com.oyika.assignment.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "image")
@NoArgsConstructor
@Builder
@Getter @Setter
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(name = "bucket_name")
    private String bucketName;
    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Column(name = "file_path", nullable = false)
    private String filePath;
    @Column(name = "etag")
    private String etag;

    public Image(Long id, String bucketName, String fileName, String filePath, String etag) {
        this.id = id;
        this.bucketName = bucketName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.etag = etag;
    }
}
