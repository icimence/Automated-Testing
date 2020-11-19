import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;

import java.io.IOException;

public class AnalysisProcess {
    public static void run(String[] args) throws CancelException, ClassHierarchyException, InvalidClassFileException, IOException {
        InputProcess input = new InputProcess(args);
        MakeGraphProcess graph = new MakeGraphProcess(input.ClassPath);
        CHACallGraph cg = graph.cg;
        graph.MakeClassGraph();
        graph.MakeMethodGraph();
        graph.outputDotContent();
    }
}
