package org.example.s3mockdemo;

import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class S3UploaderTest {
    @Test
    void testUpload() throws URISyntaxException {
        S3Mock mockS3 = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
        mockS3.start();

        S3Client s3Client = S3Client.builder()
                .endpointOverride(new URI("http://localhost:8001"))
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .region(Region.EU_CENTRAL_1)
                .build();
        s3Client.createBucket(CreateBucketRequest.builder().bucket("test-bucket").build());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket("test-bucket")
                .key("testfolder/testfile.json")
                .metadata(Map.of("contentType", "json"))
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.empty());

        long start = System.nanoTime();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
        long end = System.nanoTime();
        System.out.printf("listBuckets() took %.1f seconds.%n%s%n", (end - start) / 1e9, listBucketsResponse);
    }
}
