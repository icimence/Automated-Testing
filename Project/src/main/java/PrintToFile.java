import java.io.*;
import java.util.List;

public class PrintToFile {
    /**
     * 用来打印的函数，由于经常用到，作为独立函数。
     * @param output 要打印的内容
     * @param filePath 要打印的文件的路径
     * @throws IOException 抛出的异常
     */
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
