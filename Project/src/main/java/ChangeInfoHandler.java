import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChangeInfoHandler {
    public List<String> changes = new ArrayList<String>();
    public List<String> classChanges = new ArrayList<String>();
    public List<String> methodChanges = new ArrayList<String>();

    public ChangeInfoHandler(String changeInfoPath) throws IOException {
        FileReader fileReader = new FileReader(changeInfoPath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            if (str.trim().length() > 2) {
                changes.add(str);
            }
        }
        for (String a:changes){
            if (!classChanges.contains(a.split(" ")[0]))
                classChanges.add(a.split(" ")[0]);
            if (!methodChanges.contains(a.split(" ")[1]))
                methodChanges.add(a.split(" ")[1]);
        }
    }
}
