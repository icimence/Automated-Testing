import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnalysisProcess {
    public static ChangeInfoHandler changeInfo;
    private static MakeGraphProcess graph;
    private static CHACallGraph cg;
    public static void run(String[] args) throws CancelException, ClassHierarchyException, InvalidClassFileException, IOException {
        graph = new MakeGraphProcess(args[1]);
        System.out.println("cg has been made");
        cg = graph.cg;
        graph.MakeClassGraph();
        graph.MakeMethodGraph();
        System.out.println("graph made");
//        graph.outputDotContent();
        changeInfo = new ChangeInfoHandler(args[2]);
        if (args[0].equals("-c")){
            selectClass();
            System.out.println("CLASS ALL SELECTED");
        }
        else if (args[0].equals("-m")){
            selectMethod();
            System.out.println("METHOD ALL SELECTED");
        }
    }
    private static void selectClass() throws IOException {
        List<String> selectResult = new ArrayList<String>();
        List<String> changeClassList = new ArrayList<String>();
        for (String currentClass:changeInfo.classChanges){
            for (String temp: graph.relyList){
                String start=temp.split(" ")[1];
                String end=temp.split(" ")[0];
                if(start.compareTo(currentClass)==0 && end.contains("Test")){
                    changeClassList.add(end);
                }
            }
        }
        for(CGNode node:cg){
            if (node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    String classInnerName = method.getDeclaringClass().getName().toString();
                    String signature = method.getSignature();
                    if(changeClassList.contains(classInnerName)&& !signature.contains("<init>")&&!signature.contains("initialize()")&&!selectResult.contains(classInnerName+" "+signature)){
                        selectResult.add(classInnerName+" "+signature);
                    }
                }
            }
        }
        PrintToFile.print(selectResult,"selection-class.txt");
    }

    private static void selectMethod() throws IOException {
        List<String> selectResult=new ArrayList<String>();
        for (CGNode node : cg) {
            if (node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    String classInnerName = method.getDeclaringClass().getName().toString();
                    String signature = method.getSignature();
                    if(signature.contains("Test")){
                        if(containMethod(node,changeInfo.methodChanges,signature)&& !signature.contains("<init>")&&!selectResult.contains(classInnerName+" "+signature)){
                            selectResult.add(classInnerName+" "+signature);
                        }
                    }
                }
            }
        }
        PrintToFile.print(selectResult,"selection-method.txt");
    }
    private static Boolean containMethod(CGNode node,List<String> changes,String signature){
        Iterator<CGNode> succNodes=cg.getSuccNodes(node);
        if(!succNodes.hasNext()) return false;
        Boolean result=false;
        while(succNodes.hasNext()){
            CGNode x=succNodes.next();
            if (!"Application".equals(x.getMethod().getDeclaringClass().getClassLoader().toString())) continue;
            String succSignature = x.getMethod().getSignature();
            for(String single:changes){
                if(single.compareTo(succSignature)==0){
                    return true;
                }
            }
            result=containMethod(x,changes,succSignature);
            if(result){
                break;
            }
        }
        return result;
    }
}
