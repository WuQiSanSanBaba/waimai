package com.wangqi.service.impl;

import com.wangqi.service.UpLoadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
@Service
public class UpLoadServiceImpl implements UpLoadService {

    @Override
    public String upLoadImg(MultipartFile file) {
        // 生成唯一图片名称 采用uuid 因为uuid不会重复
        String imgName = UUID.randomUUID().toString();
        //下面两行获取文件后缀名
        int lastIndexOf = file.getOriginalFilename().lastIndexOf(".");
        String last = file.getOriginalFilename().substring(lastIndexOf);
        try {
            InputStream inputStream = file.getInputStream();
            FileOutputStream fileOutputStream=new FileOutputStream("D:/upload/"+imgName+last);
            byte[] b=new byte[1024];
            while (inputStream.read(b,0,1024)!=-1){
                fileOutputStream.write(b);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgName+last;
    }
}
