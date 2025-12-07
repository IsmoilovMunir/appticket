package com.surnekev.ticketing.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeService {

    private static final int SIZE = 320;

    public byte[] generatePng(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (WriterException | IOException ex) {
            log.error("Failed to generate QR code", ex);
            throw new IllegalStateException("QR generation failed", ex);
        }
    }

    public String asDataUrl(byte[] pngBytes) {
        String base64 = Base64.getEncoder().encodeToString(pngBytes);
        return "data:image/png;base64," + base64;
    }
}

