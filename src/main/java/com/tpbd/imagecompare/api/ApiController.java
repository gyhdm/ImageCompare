package com.tpbd.imagecompare.api;

import com.google.gson.Gson;
import com.tpbd.imagecompare.service.ImageCompress;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("api")
public class ApiController {

    @Value("${upload.path}")
    private String uploadAnotherPath;
    @Value("${upload.base_path}")
    private String uploadPath;
    @Value("${server.port}")
    private String port;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${api.url}")
    private String apiUrl;

    private static final Map<String, String> MAP_CACHE = new ConcurrentHashMap<>();

    @RequestMapping("index")
    public Object index() {
        return "hello";
    }

    @RequestMapping("image/upload")
    public Object uploadImage(MultipartFile file, HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            String originalFilename = StringUtils.substringBeforeLast(file.getOriginalFilename(), ".")
                    + System.currentTimeMillis() + "."
                    + StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            Path targetPath = Paths.get(getUploadPath(), "images", originalFilename);
            if (!targetPath.getParent().toFile().exists()) {
                targetPath.getParent().toFile().mkdirs();
            }
            file.transferTo(targetPath);
            String thumbFilename = StringUtils.substringBeforeLast(originalFilename, ".") + ".jpg";
            Path thumbPath = Paths.get(getUploadPath(), "/images/thumbs", thumbFilename);
            if (!thumbPath.getParent().toFile().exists()) {
                thumbPath.getParent().toFile().mkdirs();
            }
            ImageCompress.createThumbnails(targetPath.toString(), thumbPath.toString());
            Path anotherPath = Paths.get(uploadAnotherPath, "/txbd/thumbs", thumbFilename);
            if (!anotherPath.getParent().toFile().exists()) {
                anotherPath.getParent().toFile().mkdirs();
            }
            Files.copy(thumbPath, anotherPath, StandardCopyOption.REPLACE_EXISTING);
            String imageId = Base64.getEncoder().encodeToString(targetPath.toString().getBytes(StandardCharsets.UTF_8));
            result.put("imageId", imageId);
            MAP_CACHE.put(imageId, thumbFilename);
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
    public Object compareImage(@RequestParam int id, @RequestParam String imageId1, @RequestParam String imageId2) {
        Map<String, Object> result = new HashMap<>();
        try {
            String image1 = new String(Base64.getDecoder().decode(imageId1), StandardCharsets.UTF_8);
            String image2 = new String(Base64.getDecoder().decode(imageId2), StandardCharsets.UTF_8);
            System.out.println(image1);
            System.out.println(image2);
            float percent = ImageCompress.compareImage(image1, image2);
            result.put("success", percent >= 95);
            result.put("percent", percent);
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                Gson gson = new Gson();
                Map<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("id", id);
                bodyMap.put("img1", "/uploads/images/txbd/thumbs/" + MAP_CACHE.get(imageId1));
                bodyMap.put("img2", "/uploads/images/txbd/thumbs/" + MAP_CACHE.get(imageId2));
                bodyMap.put("time", String.valueOf(System.currentTimeMillis() / 1000));
                bodyMap.put("status", percent >= 95 ? 1 : 2);
                String content = gson.toJson(bodyMap);
                System.out.println("开始上传数据" + content);
                HttpEntity<String> request = new HttpEntity<>(content, headers);
                ResponseEntity<String> postForEntity = restTemplate.postForEntity(apiUrl, request, String.class);
                if (postForEntity.getStatusCode() == HttpStatus.OK) {
                    String response = postForEntity.getBody();
                    System.out.println("数据上传成功" + response);
                }
            } catch (Exception e) {
                System.out.println("数据上传发生错误" + e.getMessage());
//                e.printStackTrace();
            }
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
