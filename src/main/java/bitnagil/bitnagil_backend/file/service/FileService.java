package bitnagil.bitnagil_backend.file.service;


import bitnagil.bitnagil_backend.file.request.PresignedUrlRequest;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public Map<String, String> getPresignedUrls(List<PresignedUrlRequest> requests) {
        Map<String, String> responseMap = new HashMap<>();

        for (PresignedUrlRequest request : requests) {
            String prefix = request.getPrefix() != null ? request.getPrefix() : "";
            String fileName = request.getFileName();

            if (!prefix.isEmpty()) {
                fileName = createPath(prefix, fileName);
            }

            GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, fileName);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            responseMap.put(fileName, url.toString());
        }
        return responseMap;
    }

    // S3 Presigned URL 생성
    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPresignedUrlExpiration());

        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        return generatePresignedUrlRequest;
    }

    // Presigned URL 만료 시간 설정 (2분)
    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    private String createFileId() {
        return UUID.randomUUID().toString();
    }

    private String createPath(String prefix, String fileName) {
        String fileId = createFileId();
        return String.format("%s/%s", prefix, fileId + "-" + fileName);
    }
}

