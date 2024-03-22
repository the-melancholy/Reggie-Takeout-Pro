package com.zjc.reggie.controller;

import com.zjc.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 处理文件上传与下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * 参数名不能乱写，需要和请求携带的参数保持一致
     * @param file 是一个临时文件，需要转存到指定位置，否则本次请求结束后会被删除
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        //得到文件原始名称
        String origin = file.getOriginalFilename();
        //使用UUID重新生成文件名，防止文件重名造成文件覆盖
        String suffix = origin.substring(origin.lastIndexOf("."));// .jpg
        String fileName = UUID.randomUUID().toString()+ suffix;
        File dir = new File(basePath);
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Result.success(fileName);

    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        FileInputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            //输入流，读取文件内容
           inputStream  = new FileInputStream(new File(basePath + name));
            //输出流，将文件写回浏览器，在浏览器中展示图片
            outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
