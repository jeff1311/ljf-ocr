package com.ljf.ocr;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * ͼƬ����ʶ��
 * @author ljf
 * @since 2019-03-17
 */
public class OCR {

	static {
		//���뱾�ؿ�
    	String opencvLib = Util.getClassPath() + "opencv/dll/opencv_java320.dll";
        System.load(opencvLib);
	}
	
	public static String ocr(String imgPath,boolean show){
		//��ȡͼ��
		Mat src = Imgcodecs.imread(imgPath);
		int width = 1200;
		int height = width * src.height() / src.width();
		Size size = new Size(width, height);
		Imgproc.resize(src, src, size);
		if(show){			
			ImgUtil.window(src);
		}
		//�Ҷ�
		Mat gray = src.clone();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		if(show){			
			ImgUtil.window(gray);
		}
		//��ֵ��������Ӧ��
		int blockSize = 25;
	    int constValue = 30;
	    Imgproc.adaptiveThreshold(gray, gray, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, blockSize, constValue);
	    //��������
//	    Imgproc.medianBlur(gray, gray,3);
//	    Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,new Size(3,3));//ʹ��3*3�����ں�
//	    Imgproc.erode(gray, gray, kernel2, new Point(-1, -1), 1);
//	    //����
//	    Mat kerne3 = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,new Size(3,3));//ʹ��3*3�����ں�
//		Imgproc.dilate(gray, gray, kerne3, new Point(-1, -1), 1);//������ں�Ϊ��������8��
	    if(show){	    	
	    	ImgUtil.window(gray);
	    }
	    Mat binary = gray.clone();
	    //��ֵͼ��ɫ
	    Core.bitwise_not(binary, binary);
	    if(show){	    	
	    	ImgUtil.window(binary);
	    }
	    //��ֵ��
	    int blockSize2 = 25;
	    int constValue2 = 40;
	    Imgproc.adaptiveThreshold(gray, gray, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, blockSize2, constValue2);
	    if(show){	    	
	    	ImgUtil.window(gray);
	    }
	    //����
	    Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,new Size(3,3));//ʹ��3*3�����ں�
		Imgproc.dilate(gray, gray, kernel, new Point(-1, -1), 35);//������ں�Ϊ��������8��
		if(show){			
			ImgUtil.window(gray);
		}
		ImgUtil.findContours(gray,binary);
		return null;
	}
	
	public static void main(String[] args) {
		String imgPath = "H:/opencv/test16.jpg";
		ocr(imgPath,true);
	}
	
}
