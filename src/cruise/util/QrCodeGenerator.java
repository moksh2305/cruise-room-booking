package cruise.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.Path;

public class QrCodeGenerator {
    public static void generate(String data, String filePath) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(
                data, BarcodeFormat.QR_CODE, 200, 200);
        MatrixToImageWriter.writeToPath(matrix, "PNG", Path.of(filePath));
    }
}
