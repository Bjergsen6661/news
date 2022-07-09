package com.news.files.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.news.api.controller.files.FileUploaderControllerApi;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.common.utils.FileUtils;
import com.news.files.resource.AliyunResource;
import com.news.files.service.UploaderService;
import com.news.model.user.bo.NewAdminBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文件上传api实现
 * @create 2022-06-19-11:10
 */
@Slf4j
@RestController
public class FileUploaderController implements FileUploaderControllerApi {

    @Autowired
    UploaderService uploaderService;

    @Autowired
    AliyunResource aliyunResource;

    @Autowired
    GridFSBucket gridFSBucket;

    @Override
    public R uploadFace(String userId, MultipartFile file) throws Exception {

        //用户未登录，无法获取用户信息
        if(StringUtils.isBlank(userId)){
            return R.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        //未上传文件
        if(file == null){
            return R.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }
        //上传的文件名为空
        String filename = file.getOriginalFilename();
        if(StringUtils.isBlank(filename)){
            return R.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        //至此，开始正常上传逻辑
        String path = "";
        //获取文件后缀
        String[] split = filename.split("\\.");
        String suffix = split[split.length - 1];
        //判断后缀是否符合预定义规范
        if(!suffix.equals("png") && !suffix.equals("jpg") && !suffix.equals("jpeg")){
            return R.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
        }

        //执行上传，使用阿里云OSS，上传并保存文件，返回文件路径
        path = uploaderService.uploadOss(file, userId, suffix);

        if(StringUtils.isBlank(path)){
            return R.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        //最后路径拼接上OSS的前缀路径 + path
        String finalUserFaceUrl = aliyunResource.getHost() + path;

        log.info(finalUserFaceUrl);

        return R.ok(finalUserFaceUrl);
    }

    @Override
    public R uploadToGridFS(NewAdminBo newAdminBo) throws Exception {

        //上传文件至MongoDB的GridFS
        return uploaderService.uploadToGridFs(newAdminBo);
    }

    @Override
    public void readInGridFS(String faceId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        //参数判断
        if(StringUtils.isBlank(faceId) || faceId.equalsIgnoreCase("null")){
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        //从GirdFS获取文件内容
        File adminFace = uploaderService.readGridFSByFaceId(faceId);

        //把图片输出到浏览器
        FileUtils.downloadFileByStream(response, adminFace);
    }

    @Override
    public R uploadSomeFiles(String userId,
                             MultipartFile[] files,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        // 用于存储多个图片的list，返回到前端
        List<String> imagesUrlList = new ArrayList<>();
        // 多文件上传必须不为空并且该文件数组长度大于0
        for (MultipartFile file : files) {
            //数组中文件如果为空的，直接continue，不进行上传操作
            if(file == null) continue;

            //文件名为空，直接continue，不进行上传操作
            String filename = file.getOriginalFilename();
            if(StringUtils.isBlank(filename)) continue;

            //至此，开始正常上传逻辑
            String path = "";
            //获取文件后缀
            String[] split = filename.split("\\.");
            String suffix = split[split.length - 1];
            //判断后缀是否符合预定义规范，多文件上传中途出问题直接continue，不进行上传操作
            if(!suffix.equals("png") && !suffix.equals("jpg") && !suffix.equals("jpeg")){
                continue;
            }

            //执行上传，使用阿里云OSS，上传并保存文件，返回文件路径
            path = uploaderService.uploadOss(file, userId, suffix);

            //上传后的path路径如果为空的，直接跳过
            if(StringUtils.isBlank(path)) continue;

            //最后路径拼接上OSS的前缀路径 + path
            String finalPicUrl = aliyunResource.getHost() + path;
            log.info("finalPicUrl：" + finalPicUrl);

            //将正确的url保存在imagesUrlList中
            imagesUrlList.add(finalPicUrl);
        }

        return R.ok(imagesUrlList);
    }

}
