package com.news.api.controller.files;

import com.news.common.jsonres.R;
import com.news.model.user.bo.NewAdminBo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文件上传api
 * @create 2022-06-19-11:06
 */

@Api(value = "文件上传", tags = {"文件上传的controller"})
@RequestMapping("/fs")
public interface FileUploaderControllerApi {

    @ApiOperation(value = "上传用户头像", notes = "上传用户头像请求", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public R uploadFace(@RequestParam String userId, MultipartFile file) throws Exception;

    //文件上传到MongoDB的GridFS存储
    @PostMapping("/uploadToGridFS")
    public R uploadToGridFS(@RequestBody NewAdminBo newAdminBo) throws Exception;

    //从GridFS读取文件
    @GetMapping("/readInGridFS")
    public void readInGridFS(String faceId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception;

    //上传多张图片
    @PostMapping("/uploadSomeFiles")
    public R uploadSomeFiles(String userId,
                             MultipartFile[] files,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception;

}
