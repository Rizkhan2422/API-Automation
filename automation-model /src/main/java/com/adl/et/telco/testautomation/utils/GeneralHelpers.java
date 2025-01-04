package com.adl.et.telco.testautomation.utils;

import io.qameta.allure.Step;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralHelpers {

    public static String getBasePath() {
        String path = GeneralHelpers.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        return path.replaceAll("common.utils.*", "");
    }

    public static boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * This method is used to compare two images
     *
     * @param inputimagepath
     * @param expectedimagepath
     */
    @Step
    public static boolean checkImageDiff(String inputimagepath, String expectedimagepath) throws IOException {
        /*
         Both images should be same width and height
         */
        //Set the path input image path
        BufferedImage img1 = ImageIO.read(new File(inputimagepath));
        BufferedImage img2;
        //Set the path expected image path
        img2 = ImageIO.read(new File(expectedimagepath));

        //get diff
        double p = getDifferencePercent(img1, img2);

        // check correctness
        double correctness = 100.00 - p;
        return correctness >= 99;
    }

    private static double getDifferencePercent(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();
        if (width != width2 || height != height2) {
            throw new IllegalArgumentException(String.format("Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
        }

        long diff = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
            }
        }
        long maxDiff = 3L * 255 * width * height;

        return 100.0 * diff / maxDiff;
    }

    private static int pixelDiff(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;
        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

}
