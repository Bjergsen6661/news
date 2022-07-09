package com.news.files.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.news.common.exceptiopn.GraceException;
import com.news.common.jsonres.R;
import com.news.common.jsonres.ResponseStatusEnum;
import com.news.files.resource.AliyunResource;
import com.news.files.service.UploaderService;
import com.news.model.user.bo.NewAdminBo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.*;

import static com.news.api.BaseController.TEMP_PATH;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 文件上传业务实现
 * @create 2022-06-19-11:00
 */
@Slf4j
@Service
public class UploaderServiceImpl implements UploaderService {

    @Autowired
    private AliyunResource aliyunResource;

    @Autowired
    private Sid sid;

    @Autowired
    GridFSBucket gridFSBucket;

    @Override
    public String uploadOss(MultipartFile file,
                            String userId,
                            String fileExtName) throws Exception {

        // 构建ossClient实例
        OSS ossClient = new OSSClientBuilder()
                .build(aliyunResource.getEndpoint(),
                        aliyunResource.getAccesskeyid(),
                        aliyunResource.getAccesskeysecret());

        //自定义随机文件名
        String fileName = sid.nextShort();

        //文件路径：images/face/{userId}/{fileName}.{jpg}
        String myObjectName = aliyunResource.getObjectName() + "/" + userId + "/"
                + fileName + "." + fileExtName;

        try {
            //上传网络流
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(aliyunResource.getBucketName(), myObjectName, inputStream);

        }catch (OSSException oe){
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            //关闭客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return myObjectName;
    }

    @Override
    public R uploadToGridFs(NewAdminBo newAdminBo) throws Exception{
        //获取文件base64字符串
        String img64 = newAdminBo.getImg64();
        //转为bute数组
        byte[] bytes = new BASE64Decoder().decodeBuffer(img64.trim());
        //转为输入流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        //上传GridFS
        ObjectId o = gridFSBucket.uploadFromStream(newAdminBo.getUsername() + ".png", byteArrayInputStream);
        log.info("上传完成。 文件ID：" + o);

        //获取文件在GridFS中的主键id
        String fileId = o.toString();

        return R.ok(fileId);
    }

    @Override
    public File readGridFSByFaceId(String faceId) throws Exception{

        GridFSFindIterable gridFSFindIterable = null;
        try {
            //在MongoDB通过ID（_id）筛选出文件信息，存入时是Object类型
            gridFSFindIterable = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));

        } catch (IllegalArgumentException e) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        //获取第0条数据
        GridFSFile gridFSFile = gridFSFindIterable.first();
        if (gridFSFile == null) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        String fileName = gridFSFile.getFilename();
        log.info("filename: " + fileName);

        // 获取文件流，定义存放位置和名称
        File fileTemp = new File(TEMP_PATH);
        if (!fileTemp.exists()) {
            fileTemp.mkdirs();
        }

        File file = new File(TEMP_PATH + "/" + fileName);

        // 创建输出流
        OutputStream os = new FileOutputStream(file);

        // 执行下载，下载到本地磁盘，管理员头像不多，下载到服务器不会占用大量硬盘空间
        gridFSBucket.downloadToStream(new ObjectId(faceId), os);

        return file;
    }

}
