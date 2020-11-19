import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MakeGraphProcess {
    public AnalysisScope scope;
    public HashMap<String,String> relyMap = new HashMap<String, String>();
    private List<String> methodDotFileContent = new ArrayList<String>();
    public CHACallGraph cg;

    public MakeGraphProcess(String Classpath) throws IOException, InvalidClassFileException, ClassHierarchyException, CancelException {
        String scopePath = "C:\\Users\\lenovo\\OneDrive - smail.nju.edu.cn\\大三内容\\自动化测试\\wala\\Automated-Testing\\src\\main\\resources\\scope.txt";
        String exclusionPath = "C:\\Users\\lenovo\\OneDrive - smail.nju.edu.cn\\大三内容\\自动化测试\\wala\\Automated-Testing\\src\\main\\resources\\scope.txt";
        scope = AnalysisScopeReader.readJavaScope(scopePath, new File(exclusionPath), MakeGraphProcess.class.getClassLoader());
        File classFile = new File(Classpath);
        List<File> classFileList = new ArrayList<File>();
        travelClassPath(classFile, classFileList);
        for (File file : classFileList) {
            scope.addClassFileToScope(ClassLoaderReference.Application, file);
        }
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
        cg = new CHACallGraph(cha);
        cg.init(eps);
    }

    /**
     * 方法用于遍历所有target文件夹，记录所有的class文件信息
     *
     * @param classFile     target文件路径，包括了所有的class文件
     * @param classFileList 用于存储所有的class文件，在MakeGraph Process中加入scope
     */
    private static void travelClassPath(File classFile, List<File> classFileList) {
        File[] allFilesInClassFilePath = classFile.listFiles();
        for (File file : allFilesInClassFilePath) {
            if (file.isDirectory()) {
                travelClassPath(file, classFileList);
            } else if (file.isFile() && file.getName().endsWith(".class")) {
                classFileList.add(file);
            }
        }
    }

    public void MakeClassGraph() {
        String firstSentence = "digraph class {";
        List<String> classDotFileContent = new ArrayList<String>();
        classDotFileContent.add(firstSentence);
        for (CGNode node : cg) {
            if (node.getMethod() instanceof ShrikeBTMethod){
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())){
                    String classInnerName = method.getDeclaringClass().getName().toString();
                    String signature = method.getSignature();
                    Iterator<CGNode> preNodes = cg.getPredNodes(node);
                    if (!preNodes.hasNext()){
                        continue;
                    }
                    while (preNodes.hasNext()){
                        CGNode a = preNodes.next();
                        if("Application".equals(a.getMethod().getDeclaringClass().getClassLoader().toString())) {
                            String preClassInnerName=a.getMethod().getDeclaringClass().getName().toString();
                            String inputString = "    \""+classInnerName+"\" -> \""+preClassInnerName+"\";";
                            if (!classDotFileContent.contains(inputString))
                                classDotFileContent.add("    \""+classInnerName+"\" -> \""+preClassInnerName+"\";");
                            if (relyMap.containsKey(preClassInnerName))
                                relyMap.put(preClassInnerName,classInnerName);
                        }
                    }
                }
            }
        }
        classDotFileContent.add("}");
    }

    public void MakeMethodGraph(){
        String title="digraph method {";
        List<String> methodDotFileContent = new ArrayList<String>();
        methodDotFileContent.add(title);
        for (CGNode node : cg) {
            if (node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    String classInnerName = method.getDeclaringClass().getName().toString();
                    String signature = method.getSignature();
                    Iterator<CGNode> predNodes= cg.getPredNodes(node);
                    if(!predNodes.hasNext()) continue;
                    while(predNodes.hasNext()){
                        CGNode x=predNodes.next();
                        if("Application".equals(x.getMethod().getDeclaringClass().getClassLoader().toString())) {
                            String presignature=x.getMethod().getSignature();
                            if (!methodDotFileContent.contains("    \""+signature+"\" -> \""+presignature+"\";"))
                                methodDotFileContent.add("    \""+signature+"\" -> \""+presignature+"\";");
                        }
                    }
                }
            }
        }
        methodDotFileContent.add("}");
    }

    public void outputDotContent(){
        String outPath = "";
    }
}
