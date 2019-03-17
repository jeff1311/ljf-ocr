package com.ljf.ocr;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Test {

    public static void main(String[] args){
        String opencvLib = Util.getClassPath() + "opencv/dll/opencv_java320.dll";
        System.load(opencvLib);
        String originalImgPath = "H:/opencv/test2.jpg";
        Mat mat = Imgcodecs.imread(originalImgPath);
        ShowImage window1 = new ShowImage(mat);
        window1.getFrame().setVisible(true);
        ShowImage window2 = new ShowImage(mat);
        window2.getFrame().setVisible(true);
    }

}
