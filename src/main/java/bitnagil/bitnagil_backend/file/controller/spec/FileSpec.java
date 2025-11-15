package bitnagil.bitnagil_backend.file.controller.spec;

import bitnagil.bitnagil_backend.file.request.PresignedUrlRequest;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import bitnagil.bitnagil_backend.global.swagger.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Tag(name = ApiTags.FILE)
public interface FileSpec {

    @Operation(summary = "S3 Presigned URL 발급")
    CustomResponseDto<Map<String, String>> getPresignedUrls(List<PresignedUrlRequest> requests);
}
