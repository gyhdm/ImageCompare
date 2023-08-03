package com.tpbd.imagecompare.api;

import com.tpbd.imagecompare.service.ImageCompress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("api")
public class ApiController {

    @Value("${upload.path}")
    private String uploadPath;
    @Value("${server.port}")
    private String port;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private ApplicationContext applicationContext;

    private static final Map<String, String> MAP_CACHE = new ConcurrentHashMap<>();

    @RequestMapping("index")
    public Object index() {
        return "hello";
    }

    @RequestMapping("image/upload")
    public Object uploadImage(MultipartFile file, HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            String originalFilename = file.getOriginalFilename();
            Path targetPath = Paths.get(getUploadPath(), "images", originalFilename);
            if (!targetPath.getParent().toFile().exists()) {
                targetPath.getParent().toFile().mkdirs();
            }
            file.transferTo(targetPath);
            String thumbFilename = originalFilename.substring(0, originalFilename.lastIndexOf(".")) + ".jpg";
            Path thumbPath = Paths.get(getUploadPath(), "images/thumbs", thumbFilename);
            if (!thumbPath.getParent().toFile().exists()) {
                thumbPath.getParent().toFile().mkdirs();
            }
            ImageCompress.createThumbnails(targetPath.toString(), thumbPath.toString());
            String imageId = Base64.getEncoder().encodeToString(targetPath.toString().getBytes(StandardCharsets.UTF_8));
            result.put("imageId", imageId);
            String basePath = getUrl();
            result.put("thumb", basePath + "static/images/thumbs/" + thumbFilename);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping("image/compare")
    public Object compareImage(@RequestParam String imageId1, @RequestParam String imageId2) {
        Map<String, Object> result = new HashMap<>();
        try {
            String image1 = new String(Base64.getDecoder().decode(imageId1));
            String image2 = new String(Base64.getDecoder().decode(imageId2));
            System.out.println(image1);
            System.out.println(image2);
            float percent = ImageCompress.compareImage(image1, image2);
            result.put("success", true);
            result.put("percent", percent);
        } catch (Exception e) {
            result.put("success", false);
            e.printStackTrace();
        }
        return result;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getUrl() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "http://" + address.getHostAddress() + ":" + port + contextPath + "/";
    }
}
