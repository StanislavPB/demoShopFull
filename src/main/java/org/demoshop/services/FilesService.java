package org.demoshop.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.demoshop.dto.StandardResponseDto;
import org.demoshop.models.FileInfo;
import org.demoshop.repositories.FilesInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilesService {

    private final AmazonS3 amazonS3;

    private final FilesInfoRepository repository;


    @Transactional
    @SneakyThrows
    public StandardResponseDto upload(MultipartFile file) {

        String originalFileName = file.getOriginalFilename(); // получаем имя файла оригинальное

        String extension;

        if (originalFileName != null) {
            extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1); // получаем расширение
        } else {
            throw new IllegalArgumentException("null original file name");
        }

        String uuid = UUID.randomUUID().toString(); // генерация случайной строки в формате UUID
        String newFileName = uuid + "." + extension; // создаем новое имя файла

        // загрузка в digitalOcean


        InputStream inputStream = file.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType()); // сообщаем хранилищу какого типа у нас файл


        // создаем запрос на отправку файла
        PutObjectRequest request = new PutObjectRequest(
                "cohor35project",
                "images/" + newFileName,
                inputStream,
                metadata
        ).withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3.putObject(request);

        String link = amazonS3.getUrl("cohor35project", "images/" + newFileName).toString(); // получаем ссылку на файл

        FileInfo fileInfo = new FileInfo().builder().link(link).build();

        repository.save(fileInfo);

        return StandardResponseDto.builder()
                .message(link)
                .build();
    }
}
