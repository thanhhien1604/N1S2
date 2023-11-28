/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;

/**
 *
 * @author ledin
 */
public class ZXingHelper {

    public static byte[] getQRCodeImage(String text, int width, int height, String outputPath) {
        try {
            QRCodeWriter qRCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qRCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);

            // Lưu hình ảnh vào tệp tin
            saveImage(byteArrayOutputStream.toByteArray(), outputPath);

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getBarCodeImage(String text, int width, int height, String outputPath) {
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            Writer writer = new Code128Writer();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
            
            saveImage(byteArrayOutputStream.toByteArray(), outputPath);
            
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private static void saveImage(byte[] imageBytes, String outputPath) {
        try ( FileOutputStream fos = new FileOutputStream(new File(outputPath))) {
            fos.write(imageBytes);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
