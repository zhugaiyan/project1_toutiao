package com.nowcoder.toutiao.service;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.toutiao.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
    //设置好账号的ACCESS_KEY和SECRET_KEY
   // String ACCESS_KEY = "abNXnXBIlI6viRaOeRY6Hk-zc3V-NpjLcGfYz5kD";
   // String SECRET_KEY = "QP7Xja3FmP1Zyl-oxwQDCb7T6wCoEFKoO-0vht_5";
    //要上传的空间
    //String bucketname = "nowcoder";//需要修改

    String ACCESS_KEY = "LM9A2WN6gk-MVauUKe8teeQPRbjSRNpoNVIJHdb4";
    String SECRET_KEY = "378Vfpfv_6CxSzpuH19caI-f9pRRJSznFnvVrDJF";
    String bucketname = "nowcoder";//需要修改

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //创建上传对象
    UploadManager uploadManager = new UploadManager();

   // private static String QINIU_IMAGE_DOMAIN = "http://7xsetu.com1.z0.glb.clouddn.com/";//七牛给的默认网页

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        return auth.uploadToken(bucketname);
    }

    public String saveImage(MultipartFile file) throws IOException {
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            //调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());
            //打印返回的信息
            //System.out.println(res.bodyString());
            System.out.println(res.toString());

            if(res.isOK() && res.isJson()){
                String key = JSONObject.parseObject(res.bodyString()).get("key").toString();
                return ToutiaoUtil.QINIU_DOMAIN_PREFIX + key;
            }else{
                logger.error("七牛异常：" + res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            // 请求失败时打印的异常的信息
            logger.error("七牛异常:" + e.getMessage());
            return null;
        }
    }

}
