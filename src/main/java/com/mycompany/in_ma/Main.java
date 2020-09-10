/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.in_ma;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

/**
 *
 * @author Hi Kapi
 */
public class Main {

    public static void createImage(String image_name, String myString) {
        try {
            Code128Bean code128 = new Code128Bean();
            code128.setHeight(19f);
            code128.setModuleWidth(0.6);
            code128.setQuietZone(10);
            code128.doQuietZone(true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 240, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            code128.generateBarcode(canvas, myString);
            canvas.finish();
            //write to png file
            FileOutputStream fos = new FileOutputStream(image_name);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService services = Executors.newFixedThreadPool(30);
        List<String> listData = FileUtils.readLines(new File("data.txt"));
        for (String s : listData) {
            services.submit(() -> {
                File outputfile = new File("image.jpg");
                createImage("chillyfacts.png", s.trim());
                addWatermark("output/" + s.trim() + ".png");
            });
        }
        services.shutdown();
    }

    public static void addWatermark(String localImagePath) {

        try {
            BufferedImage image = ImageIO.read(new File("goc.jpg"));
            BufferedImage overlay = ImageIO.read(new File("chillyfacts.png"));

            // create the new image, canvas size is the max. of both image sizes
            int w = Math.max(image.getWidth(), overlay.getWidth());
            int h = Math.max(image.getHeight(), overlay.getHeight());
            BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            // paint both images, preserving the alpha channels
            Graphics g = combined.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.drawImage(overlay, 90, 850, null);

            ImageIO.write(combined, "PNG", new File(localImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
