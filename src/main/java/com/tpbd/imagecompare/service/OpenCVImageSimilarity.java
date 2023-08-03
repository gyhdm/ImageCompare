//package com.tpbd.imagecompare.service;
//import org.opencv.core.*;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import java.util.ArrayList;
//import java.util.List;
///**
// * @author angus
// * @version 1.0.0
// * @Description
// * @createTime 2023年06⽉01⽇ 19:15:00
// */
//public class OpenCVImageSimilarity {
//    public static void main(String[] args) {
//        // 加载OpenCV库
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        // 读取两张图像。准备⽐对的图⽚
//        Mat image1 = Imgcodecs.imread("/Users/guoying/Downloads/20220929249.jpg");
//        Mat image2 = Imgcodecs.imread("/Users/guoying/Downloads/20220929250.jpg");
//        // 将图⽚处理成⼀样⼤
//        Imgproc.resize(image1, image1, image2.size());
//        Imgproc.resize(image2, image2, image1.size());
//        // 计算均⽅差（MSE）
//        double mse = calculateMSE(image1, image2);
//        System.out.println("均⽅差（MSE）: " + mse);
//        // 计算结构相似性指数（SSIM）
//        double ssim = calculateSSIM(image1, image2);
//        System.out.println("结构相似性指数（SSIM）: " + ssim);
//        // 计算峰值信噪⽐（PSNR）
//        double psnr = calculatePSNR(image1, image2);
//        System.out.println("峰值信噪⽐（PSNR）: " + psnr);
//        // 计算直⽅图
//        final double similarity = calculateHistogram(image1, image2);
//        System.out.println("图⽚相似度(直⽅图): " + similarity);
//        // 计算归⼀化交叉相关（NCC）
//// double ncc = calculateNCC(image1, image2);
//// System.out.println("归⼀化交叉相关（NCC）: " + ncc);
//    }
//    // 计算均⽅差（MSE）
//    private static double calculateHistogram(Mat image1, Mat image2) {
//        // 计算直⽅图
//        Mat hist1 = calculateHistogram(image1);
//        Mat hist2 = calculateHistogram(image2);
//        // 计算相似度
//        final double similarity = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CORREL);
//        return similarity;
//    }
//    // 计算均⽅差（MSE）
//    private static double calculateMSE(Mat image1, Mat image2) {
//        Mat diff = new Mat();
//        Core.absdiff(image1, image2, diff);
//        Mat squaredDiff = new Mat();
//        Core.multiply(diff, diff, squaredDiff);
//        Scalar mseScalar = Core.mean(squaredDiff);
//        return mseScalar.val[0];
//    }
//    // 计算结构相似性指数（SSIM）
//    private static double calculateSSIM(Mat image1, Mat image2) {
//        Mat image1Gray = new Mat();
//        Mat image2Gray = new Mat();
//        Imgproc.cvtColor(image1, image1Gray, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.cvtColor(image2, image2Gray, Imgproc.COLOR_BGR2GRAY);
//        MatOfFloat ssimMat = new MatOfFloat();
//        Imgproc.matchTemplate(image1Gray, image2Gray, ssimMat, Imgproc.CV_COMP_CORREL);
//        Scalar ssimScalar = Core.mean(ssimMat);
//        return ssimScalar.val[0];
//    }
//    // 计算峰值信噪⽐（PSNR）
//    private static double calculatePSNR(Mat image1, Mat image2) {
//        Mat diff = new Mat();
//        Core.absdiff(image1, image2, diff);
//        Mat squaredDiff = new Mat();
//        Core.multiply(diff, diff, squaredDiff);
//        Scalar mseScalar = Core.mean(squaredDiff);
//        double mse = mseScalar.val[0];
//        double psnr = 10.0 * Math.log10(255.0 * 255.0 / mse);
//        return psnr;
//    }
//    // 计算归⼀化交叉相关（NCC）
//// private static double calculateNCC(Mat image1, Mat image2) {
//// Mat image1Gray = new Mat();
//// Mat image2Gray = new Mat();
//// Imgproc.cvtColor(image1, image1Gray, Imgproc.COLOR_BGR2GRAY);
//// Imgproc.cvtColor(image2, image2Gray, Imgproc.COLOR_BGR2GRAY);
//// MatOfInt histSize = new MatOfInt(256);
//// MatOfFloat ranges = new MatOfFloat(0, 256);
//// Mat hist1 = new Mat();
//// Mat hist2 = new Mat();
////
//// Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX);
//// Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX);
//// double ncc = Core.compareHist(hist1, hist2, Imgproc.CV_COMP_CORREL);
//// return ncc;
//// }
//    private static Mat calculateHistogram(Mat image) {
//        Mat hist = new Mat();
//        // 设置直⽅图参数
//        MatOfInt histSize = new MatOfInt(256);
//        MatOfFloat ranges = new MatOfFloat(0, 256);
//        MatOfInt channels = new MatOfInt(0);
//        List<Mat> images = new ArrayList<>();
//        images.add(image);
//        // 计算直⽅图
//        Imgproc.calcHist(images, channels, new Mat(), hist, histSize, ranges);
//        return hist;
//    }
//}
