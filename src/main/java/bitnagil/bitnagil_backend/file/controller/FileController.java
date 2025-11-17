package bitnagil.bitnagil_backend.file.controller;

import bitnagil.bitnagil_backend.file.controller.spec.FileSpec;
import bitnagil.bitnagil_backend.file.request.PresignedUrlRequest;
import bitnagil.bitnagil_backend.file.service.FileService;
import bitnagil.bitnagil_backend.global.response.CustomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/files")
public class FileController implements FileSpec {
    private final FileService fileService;

    // S3 Presigned URL 발급 API
    @PostMapping("/presigned-urls")
    public CustomResponseDto<Map<String, String>> getPresignedUrls(@RequestBody List<PresignedUrlRequest> requests) {
        return CustomResponseDto.from(fileService.getPresignedUrls(requests));
    }
}
