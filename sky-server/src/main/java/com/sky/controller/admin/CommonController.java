package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Api(tags = "通用接口")
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截图原文件的后缀名 1.png
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String newFileName = UUID.randomUUID() + suffix;

            //文件的请求路径
            String filepath = aliOssUtil.upload(file.getBytes(), newFileName);
            return Result.success(filepath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
            System.out.println("fuck you");


        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
