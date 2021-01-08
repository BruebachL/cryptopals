package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {
    public static byte[] readBase64(String file)
    {
        String filetext = "";
        String line;
        try
        {
            BufferedReader input = new BufferedReader(new FileReader(new File(file)));
            while ((line = input.readLine()) != null)
            {
                filetext += line;
            }
            input.close();
        } catch (IOException e)
        {}
        return Base64Conversion.Base64toBytes(filetext);
    }
}
