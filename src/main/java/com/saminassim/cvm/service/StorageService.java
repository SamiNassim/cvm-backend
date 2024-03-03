package com.saminassim.cvm.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {

    void init();
    void store(MultipartFile file);
    Path load(String filename);
    Resource loadAsResource(String filename);
    void deleteFile(String filename);
    void deleteAll();
}