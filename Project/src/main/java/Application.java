import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws CancelException, ClassHierarchyException, InvalidClassFileException, IOException {
        AnalysisProcess.run(args);
    }
}
