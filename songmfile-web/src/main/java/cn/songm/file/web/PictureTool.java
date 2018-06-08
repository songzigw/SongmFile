package cn.songm.file.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PictureTool {

//	public static BufferedImage crop(BufferedImage source, int startX, int startY, int width, int height) {
//		int wOld = source.getWidth();
//		int hOld = source.getHeight();
//		
//		if (startX <= -1) startX = 0;
//		if (startY <= -1) startY = 0;
//		if (width <= -1) width = wOld - 1;
//		if (height <= -1) height = hOld - 1;
//		
//		BufferedImage result = new BufferedImage(width, height, source.getType());
//		for (int y = startY; y < height + startY; y++) {
//			for (int x = startX; x < width + startX; x++) {
//				int rgb = source.getRGB(x, y);
//				result.setRGB(x - startX, y - startY, rgb);
//			}
//		}
//		return result;
//	}
	
//	public static void main(String[] args) throws IOException {
//		File file = new File("/Users/zhangsong/Desktop/psb.jpeg");
//		BufferedImage img = ImageIO.read(file);
//		
//		File oFile = new File("/Users/zhangsong/Desktop/psb2.jpeg");
//		if (!oFile.exists()) {
//			System.out.println("abc");
//			oFile.mkdir();
//		}
//		ImageIO.write(crop(img, 0, 0, 100, 100), "jpeg", oFile);
//		System.out.println(".jpgs".substring(1));
//	}
}
