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

    /**
     * 方法用来处理输入并且对参数进行对应的处理，创建图，根据参数是c还是m使用对应的方法进行处理
     * @param args
     * @throws CancelException
     * @throws ClassHierarchyException
     * @throws InvalidClassFileException
     * @throws IOException
     */
    public static void run(String[] args) throws CancelException, ClassHierarchyException, InvalidClassFileException, IOException {
        graph = new MakeGraphProcess(args[1]);
        System.out.println("CG Has Been Made");
        cg = graph.cg;
        System.out.println("Making ClassGraph");
        graph.MakeClassGraph();
        System.out.println("Making MethodGraph");
        graph.MakeMethodGraph();
        System.out.println("Graphs Have Been made Successfully");
//        graph.outputDotContent();
        changeInfo = new ChangeInfoHandler(args[2]);
        if (args[0].equals("-c")){
            System.out.println("Selecting Class");
            selectClass();
            System.out.println("CLASS ALL SELECTED");
        }
        else if (args[0].equals("-m")){
            System.out.println("Selecting Method");
            selectMethod();
            System.out.println("METHOD ALL SELECTED");
        }
        else if (args[0].equals("-C")){
            System.out.println("\"-C\" has been converted to \"-c\"\nSelecting Class");
            selectClass();
            System.out.println("CLASS ALL SELECTED");
        }
        else if (args[0].equals("-M")){
            System.out.println("\"-M\" has been converted to \"-m\"\nSelecting Method");
            selectMethod();
            System.out.println("Method ALL SELECTED");
        }
    }

    /**
     * 方法是用来处理-c参数时对类的依赖的解析，通过对之前创建图时创建的List<String> relyList进行循环，
     * 解析出所有的类依赖。
     * @throws IOException
     */
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

    /**
     * 同理，对方法依赖进行分析
     * @throws IOException
     */
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

    /**
     *
     * @param node
     * @param changes
     * @param signature
     * @return
     */
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
