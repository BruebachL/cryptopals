package Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtils {
    public static byte[] readBase64(String file)
    {
        StringBuilder filetext = new StringBuilder();
        String line;
        try
        {
            BufferedReader input = new BufferedReader(new FileReader(file));
            while ((line = input.readLine()) != null)
            {
                filetext.append(line);
            }
            input.close();
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        return Base64Conversion.Base64toBytes(filetext.toString());
    }

    public static boolean detectECBblock(byte[][] in){
        for(int b = 0; b < in.length; b++){
            for(int bAgain = 0; bAgain < in.length; bAgain++){
                if(Arrays.equals(in[b], in[bAgain]) && b != bAgain){
                    return true;
                }
            }
        }
        return false;
    }

    public static String[] readLines(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String currentLine;
        ArrayList<String> linesList = new ArrayList<>();
        while ((currentLine = bufferedReader.readLine()) != null) {
            linesList.add(currentLine);
        }
        bufferedReader.close();
        return linesList.toArray(new String[]{});
    }
}
