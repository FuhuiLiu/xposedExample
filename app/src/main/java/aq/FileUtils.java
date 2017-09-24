package aq;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/9/24 0024.
 */

public class FileUtils {
    public static String readFile(String Path) throws IOException {
        FileInputStream fis = new FileInputStream(Path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] byteAry = new byte[1024];
        int nRead;
        do{
            nRead = fis.read(byteAry);
            if(nRead > 0)
                baos.write(byteAry, 0, nRead);
        }while(nRead > 0);
        return baos.toString();
    }
}
