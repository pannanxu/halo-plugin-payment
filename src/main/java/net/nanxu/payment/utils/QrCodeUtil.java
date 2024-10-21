package net.nanxu.payment.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 二维码工具.
 *
 * @author: P
 **/
@Slf4j
public class QrCodeUtil {
    public static final String IMAGE_TYPE_PNG = "png";
    public static final String BASE64_PREFIX = "data:image/";
    public static final String QR_CODE_PREFIX = BASE64_PREFIX + "png;base64,";

    public static void main(String[] args) {
        System.out.println(encode("http://www.baidu.com"));
    }

    public static String encode(String content) {
        int width = 200;
        int height = 200;
        Map<EncodeHintType, Object> hints = new HashMap<>();
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置二维码边的空度，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        try {
            BitMatrix bitMatrix =
                new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height,
                    hints);
            return toBase64(bitMatrix);
        } catch (Exception ex) {
            log.error("生成二维码失败: {}", content, ex);
            return null;
        }
    }

    private static String toBase64(BitMatrix bitMatrix) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, IMAGE_TYPE_PNG, stream);
        String base64 = Base64.getEncoder().encodeToString(stream.toByteArray());
        return QR_CODE_PREFIX + base64;
    }
}
