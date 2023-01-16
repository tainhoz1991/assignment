#Oyika Assignment Homework

AWS S3 bucket is an object storage used to store data such images. Now you have a bucket which contains many images. Build a sprint boot microservice with following functions
1) loop through the images in the S3 bucket
2) store them in mysql table (design your own table schema )
3) a simple fetchImages API to return all the images in the JSON response

Bonus:
The bucket is constantly updated with new image. Write an efficient logic to update these new image data in the mysql table.

## Technologies
1. Java 1.8
2. Spring Boot 2.7.7
3. S3
4. SQS

## System Design
Do phạm vi của đề bài là develop miroservice để xử lý file trên S3 bucket,
vậy nên tôi xin phép bỏ qua layer authentication và authorization để tập trung
vào xử lý file.

![](system-design.png)