import java.io.*;
import java.util.List;

public class PrintToFile {
    public static void print(List<String> output,String filePath) throws IOException {
        File file = new File(filePath);
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        for (String str:output){
            writer.write(str + "\n");
        }
        writer.flush();
        writer.close();
    }
}
