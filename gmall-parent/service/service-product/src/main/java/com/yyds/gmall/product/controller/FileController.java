package com.yyds.gmall.product.controller;

/**
 * @ClassName: FileController
 * @Author: yyd
 * @Date: 2022/7/29/029
 * @Description:
 */

import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.product.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/***
 * 文件上传
 */
@RestController
@RequestMapping(value = "/admin/product")
public class FileController {

    @Value("${fileServer.url}")
    private String imgUrl;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping(value = "/fileUpload")
    public Result fileUpload(@RequestParam MultipartFile file) throws Exception{
        //返回文件的路径
        return Result.ok(imgUrl + FileUtil.uploadFile(file));
    }

}
