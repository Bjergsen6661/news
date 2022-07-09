package com.news.files.service;

import com.news.common.jsonres.R;
import com.news.model.user.bo.NewAdminBo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文件上传业务
 * @create 2022-06-19-10:58
 */
public interface UploaderService {

    //使用阿里云OSS，上传并保存文件，返回文件路径
    public String uploadOss(MultipartFile file, String userId, String fileExtName) throws Exception;

    //上传文件至MongoDB的GridFS
    public R uploadToGridFs(NewAdminBo newAdminBo) throws Exception;

    //从GirdFS获取文件内容
    public File readGridFSByFaceId(String faceId) throws Exception;
}
