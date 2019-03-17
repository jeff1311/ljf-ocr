package com.ljf.ocr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 宸ュ叿绫�
 * @author ljf
 * @since 2019-03-14
 */
public class ImgUtil {

    private static Logger logger = LoggerFactory.getLogger("ImgUtil");

    /**
     * 鏍规嵁璁剧疆鐨勫楂樼瓑姣斾緥鍘嬬缉鍥剧墖鏂囦欢<br> 鍏堜繚瀛樺師鏂囦欢锛屽啀鍘嬬缉銆佷笂浼�
     * @param oldFile  瑕佽繘琛屽帇缂╃殑鏂囦欢
     * @param newFile  鏂版枃浠�
     * @param width  瀹藉害 //璁剧疆瀹藉害鏃讹紙楂樺害浼犲叆0锛岀瓑姣斾緥缂╂斁锛�
     * @param height 楂樺害 //璁剧疆楂樺害鏃讹紙瀹藉害浼犲叆0锛岀瓑姣斾緥缂╂斁锛�
     * @return 杩斿洖鍘嬬缉鍚庣殑鏂囦欢鐨勫叏璺緞
     */
    public static int zipImgAuto(InputStream oldFile, File newFile, int width, int height) {
        try {

            Image srcFile = ImageIO.read(oldFile);
            int w = srcFile.getWidth(null);
            int h = srcFile.getHeight(null);
            double ratio;
            if(width > 0){
                ratio = width / (double) w;
                height = (int) (h * ratio);
            }else{
                if(height > 0){
                    ratio = height / (double) h;
                    width = (int) (w * ratio);
                }
            }

            String srcImgPath = newFile.getAbsoluteFile().toString();

            String suffix = srcImgPath.substring(srcImgPath.lastIndexOf(".") + 1,srcImgPath.length());

            BufferedImage buffImg = null;
            if(suffix.equals("png")){
                buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }else{
                buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            }

            Graphics2D graphics = buffImg.createGraphics();
            graphics.setBackground(new Color(255,255,255));
            graphics.setColor(new Color(255,255,255));
            graphics.fillRect(0, 0, width, height);
            graphics.drawImage(srcFile.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

            ImageIO.write(buffImg, suffix, new File(srcImgPath));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return height;
    }

    /**
     * 瑁佸壀鍥剧墖
     * @param filePath
     * @param x
     * @param y
     * @param w
     * @param h
     * @param binaryFlag 鏄惁寮�鍚簩鍊煎寲
     * @return
     */
    public static BufferedImage cropImage(String filePath, int x, int y, int w, int h,boolean binaryFlag){
        //棣栧厛閫氳繃ImageIo涓殑鏂规硶锛屽垱寤轰竴涓狪mage + InputStream鍒板唴瀛�
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //鍐嶆寜鐓ф寚瀹氭牸寮忔瀯閫犱竴涓猂eader
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader imgReader = it.next();
        // 鍐嶉�氳繃ImageReader缁戝畾 InputStream
        imgReader.setInput(iis);
        //璁剧疆鎰熷叴瓒ｇ殑婧愬尯鍩�
        ImageReadParam par = imgReader.getDefaultReadParam();
        par.setSourceRegion(new Rectangle(x, y, w, h));
        //浠� reader寰楀埌BufferImage
        BufferedImage bi = null;
        try {
            bi = imgReader.read(0, par);

            //浜屽�煎寲
            if(binaryFlag){
                bi = binary(bi,bi);
            }

            //灏咮uffeerImage鍐欏嚭閫氳繃ImageIO 娴嬭瘯鐢�
//			ImageIO.write(bi, "jpg", new File(Constants.getFileUploadPath() + "/1087/test.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi;
    }

    /**
     * 浜屽�煎寲
     * @param src
     * @param dest
     * @return
     */
    public static BufferedImage binary(BufferedImage src, BufferedImage dest) {
        int width = src.getWidth();
        int height = src.getHeight();

        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];

        getRGB(src, 0, 0, width, height, inPixels);
        int index = 0;
        int means = getThreshold(inPixels, height, width);
        for (int row = 0; row < height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                if (tr > means) {
                    tr = tg = tb = 255;//鐧�
                } else {
                    tr = tg = tb = 0;//榛�
                }
                outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
            }
        }
        setRGB(dest, 0, 0, width, height, outPixels);
        return dest;
    }

    private static int getThreshold(int[] inPixels, int h, int w) {
        int iniThreshold = 120;
        int finalThreshold = 0;
        int temp[] = new int[inPixels.length];
        for (int index = 0; index < inPixels.length; index++) {
            temp[index] = (inPixels[index] >> 16) & 0xff;
        }
        List<Integer> sub1 = new ArrayList<Integer>();
        List<Integer> sub2 = new ArrayList<Integer>();
        int means1 = 0, means2 = 0;
        while (finalThreshold != iniThreshold) {
            finalThreshold = iniThreshold;
            for (int i = 0; i < temp.length; i++) {
                if (temp[i] <= iniThreshold) {
                    sub1.add(temp[i]);
                } else {
                    sub2.add(temp[i]);
                }
            }
            means1 = getMeans(sub1);
            means2 = getMeans(sub2);
            sub1.clear();
            sub2.clear();
            iniThreshold = (means1 + means2) / 2;
        }
        finalThreshold -= 10;
        return finalThreshold;
    }

    private static int getMeans(List<Integer> data) {
        int result = 0;
        int size = data.size();
        for (Integer i : data) {
            result += i;
        }
        return (result / size);
    }

    public static void setRGB(BufferedImage image, int x, int y, int w,int h, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB){
            image.getRaster().setDataElements(x, y, w, h, pixels);
        }else{
            image.setRGB(x, y, w, h, pixels, 0, w);
        }
    }

    public static void getRGB(BufferedImage image, int x, int y, int w,int h, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB){
            image.getRaster().getDataElements(x, y, w, h, pixels);
        }else{
            image.getRGB(x, y, w, h, pixels, 0, w);
        }
    }

    public static String ocr(BufferedImage img){
        ITesseract instance = new Tesseract();
        //璁剧疆璁粌搴撶殑浣嶇疆
        String classPath = Util.getClassPath();
        String dataPath = classPath + "ocr/tessdata";
        logger.info("Tesseract-OCR tessdata:{}",dataPath);
        instance.setDatapath(dataPath);
        instance.setLanguage("chi_sim");//chi_sim eng
        String result = null;
        try {
            result =  instance.doOCR(img);
            logger.info("OCR result:{}",result);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String ocr(File img){
        ITesseract instance = new Tesseract();
        //璁剧疆璁粌搴撶殑浣嶇疆
        String classPath = Util.getClassPath();
        String dataPath = classPath + "ocr/tessdata";
        logger.info("Tesseract-OCR tessdata:{}",dataPath);
        instance.setDatapath(dataPath);
        instance.setLanguage("chi_sim");//chi_sim eng
        String result = null;
        try {
            result =  instance.doOCR(img);
            logger.info("OCR result:{}",result);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 鎶婂師鍥捐浆鎹㈡垚浜岃繘鍒�
     * @param input
     * @return
     */
    public static byte[] toByteArray(InputStream input) {
        if (input == null) {
            return null;
        }
        ByteArrayOutputStream output = null;
        byte[] result = null;
        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 100];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            result = output.toByteArray();
            if (output != null) {
                output.close();
            }
        } catch (Exception e) {}
        return result;
    }

    /**
     * 鎶婁簩杩涘埗杞崲鎴愬浘鐗�
     * @param imagedata
     * @return
     */
    public static BufferedImage toBufferedImage(byte[] imagedata) {
        Image image = Toolkit.getDefaultToolkit().createImage(imagedata);
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
        }
        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

}
