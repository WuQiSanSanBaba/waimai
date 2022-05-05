package com.wangqi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface UpLoadService {
    String upLoadImg(MultipartFile file);
}
