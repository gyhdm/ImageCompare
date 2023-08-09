package com.tpbd.imagecompare.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageCompress {
    public static void main(String[] args) throws IOException {
        //291317
//        long start = System.currentTimeMillis();
//        Thumbnails.of(new File("/Users/guoying/Downloads/20220929250.tif"))
//                .scale(0.5f)
//                .rotate(90)
//                .outputQuality(0.1)
//                .outputFormat("jpg")
//                .toFile(new File("/Users/guoying/Downloads/test.jpg"));
        System.out.println(System.currentTimeMillis()/1000);
    }
//    public static void main(String[] args) throws Exception {
//        String a1 = tiffTuanJPG("/Users/guoying/Downloads/20220929250a.jpg");
//        String a2 = tiffTuanJPG("/Users/guoying/Downloads/20220929250.jpg");
//        float percent = compare(getData(a1),
//                getData(a2));
//        if (percent == 0) {
//            System.out.println("⽆法⽐较");
//        } else {
//            System.out.println("两张图⽚的相似度为：" + percent + "%");
//        }
//    }

    public static float compareImage(String source, String target) {
        return compare(getData(source), getData(target));
    }

    public static int[] getData(String name) {
        try {
            BufferedImage img = ImageIO.read(new File(name));
            BufferedImage slt = new BufferedImage(1000, 1000,
                    BufferedImage.TYPE_INT_RGB);
            slt.getGraphics().drawImage(img, 0, 0, 1000, 1000, null);
            // ImageIO.write(slt,"jpeg",new File("slt.jpg"));
            int[] data = new int[256];
            for (int x = 0; x < slt.getWidth(); x++) {
                for (int y = 0; y < slt.getHeight(); y++) {
                    int rgb = slt.getRGB(x, y);
                    Color myColor = new Color(rgb);
                    int r = myColor.getRed();
                    int g = myColor.getGreen();
                    int b = myColor.getBlue();
                    data[(r + g + b) / 3]++;
                }
            }
            // data 就是所谓图形学当中的直⽅图的概念
            return data;
        } catch (Exception exception) {
            System.out.println("有⽂件没有找到,请检查⽂件是否存在或路径是否正确");
            return null;
        }
    }

    public static float compare(int[] s, int[] t) {
        try {
            float result = 0F;
            for (int i = 0; i < 256; i++) {
                int abs = Math.abs(s[i] - t[i]);
                int max = Math.max(s[i], t[i]);
                result += (1 - ((float) abs / (max == 0 ? 1 : max)));
            }
            return (result / 256) * 100;
        } catch (Exception exception) {
            return 0;
        }
    }

    public static void createThumbnails(String source, String target) throws IOException {
        String format = source.substring(source.lastIndexOf(".") + 1);
        double scale = 0.1, rotate = 0, quality = 0.1;
        if (format.equalsIgnoreCase("dng")) {
            scale = 0.5;
            rotate = 90;
            quality = 0.5;
        }
        Thumbnails.of(new File(source))
                .scale(scale)
                .rotate(rotate)
                .outputQuality(quality)
                .outputFormat("jpg")
                .toFile(new File(target));
    }
//
//    public static String tiffTuanJPG(String filePath) throws FileNotFoundException {
//        String format = filePath.substring(filePath.lastIndexOf(".")+1);
//        if(format.equals("tiff")){
//            String turnJpgFile = filePath.replace("tif", "jpg");
//            File fileTiff = new File(turnJpgFile);
//            if(fileTiff.exists()){
//                System.out.println("该tiff⽂件已经转换为 JPG ⽂件："+turnJpgFile);
//                return turnJpgFile;
//            }
//            RenderedOp rd = JAI.create("fileload", filePath);//读取iff⽂件
//            OutputStream ops = null;
//            try {
//                ops = new FileOutputStream(turnJpgFile);
//                //⽂件存储输出流
////                JPEGEncodeParam param = new JPEGEncodeParam();
////                ImageEncoder image = ImageCodec.createImageEncoder("JPEG", ops, param); //指定输出格式
////                image.encode(rd);
//                ImageIO.write(rd, "JPEG", ops);
//                //解析输出流进⾏输出
//                ops.close();
//                System.out.println("tiff转换jpg成功:"+filePath);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return turnJpgFile;
//        }
//        return filePath;
//    }
}
