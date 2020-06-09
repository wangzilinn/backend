package com.wangzilin.site.services.impl;

import com.wangzilin.site.dao.FileDAO;
import com.wangzilin.site.model.file.Image;
import com.wangzilin.site.services.FileService;
import org.bson.types.Binary;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 9:22 PM 6/9/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Service
public class FileServiceImpl implements FileService {
    FileDAO fileDAO;

    final private static org.slf4j.Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    public FileServiceImpl(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    /**
     * @param image 传入的图片实体
     * @return 图片的Id
     */
    @Override
    public String addImage(Image image) {
        return fileDAO.addImage(image).getId();
    }

    /**
     * @param file 传入的文件实体
     * @return 图片的id
     */
    @Override
    public String addImage(MultipartFile file) {
        try {
            Image image = new Image();
            image.setName(file.getOriginalFilename());
            image.setCreatedTime(new Date());
            image.setContent(new Binary(file.getBytes()));
            image.setContentType(file.getContentType());
            image.setSize(file.getSize());
            return addImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteImage(String id) {
        fileDAO.deleteImageById(id);
    }
}