/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

public class ScaleImage {

//TODO: class can be removed    
    
//    @SuppressWarnings("static-access")
//    public static void scale(String src, int scalewidth, int scaleheight, String dest) {
//
////    sagex.api.Utility.SaveImageToFile(sagex.api.Utility.LoadImage(src),new File(dest), scalewidth,scaleheight);
//
//
//
//
////        Commented out to try sagetv native scaling
////        boolean preserveAlpha = src.contains(".png");
////        System.out.println("Scaling Image=" + src.toString());
////        System.out.println("Passed Dimensions=" + scalewidth + " x " + scaleheight);
////
////
////        int imageType = !preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
////
////
//        BufferedImage originalimage;
//        int i = 0;
//
//        try {
//
//            originalimage = ImageIO.read(new File(src));
//            Integer[] dims = GetDimsToScale(scalewidth, scaleheight, originalimage.getWidth(), originalimage.getHeight());
//             sagex.api.Utility.SaveImageToFile(sagex.api.Utility.LoadImage(src),new File(dest), dims[0],dims[1]);
//
////            int width = dims[0];
////            int height = dims[1];
////
////            BufferedImage scaledBI = new BufferedImage(width, height, imageType);
////
////            Graphics2D g = scaledBI.createGraphics();
////
////
////            if (preserveAlpha) {
////
////                g.setComposite(AlphaComposite.Src);
////            }
////
////
////
////            g.drawImage(originalimage, 0, 0, width, height, null);
////
////            g.dispose();
////
////
////            ImageIO.write(scaledBI, preserveAlpha ? "png" : "jpg", new File(dest));
////
//        } catch (IOException ex) {
//            System.out.println("Problem Scaling images" + ScaleImage.class.getName() + ex);
//        } catch (Exception e) {
//
//
//            System.out.println("Problem loading original image may be corrupt" + ScaleImage.class.getName() + e);
//        }
//
//    }
//
//    public static Integer[] GetDimsToScale(int width, int height, int w, int h) {
//        System.out.println("Original width and height=" + w + " x " + h);
//        if (width < w && height < h) {
//
//            if (width > height) {
//                if (height < 0) {
//                    height = 1;
//                }
//                h = (h * width) / w;
//                w = width;
//            } else {
//                if (width < 0) {
//                    width = 1;
//                }
//                w = (w * height) / h;
//                h = height;
//            }
//        }
//        System.out.println("Returing new width and height=" + w + " x " + h);
//        return new Integer[]{w, h};
//
//    }
//
//    public static void main(String[] args) throws IOException {
//        scale("C:\\backdrop7.jpg", 200, 300, "c:\\simpsonscaled.jpg");
//
//    }
}
