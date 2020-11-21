import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;

import java.io.IOException;

public class Application {
    /**
     * 整个项目的入口函数
     * @param args 输入的参数
     * @throws CancelException  抛出的异常
     * @throws ClassHierarchyException  CG异常
     * @throws InvalidClassFileException 异常
     * @throws IOException 读写异常
     */
    public static void main(String[] args) throws CancelException, ClassHierarchyException, InvalidClassFileException, IOException {
        if (args.length != 3){
            System.err.println("Invalid Input Parameter Number,please check again!");
            System.exit(1);
        }
        else if (!(args[0].equals("-C")||args[0].equals("-c")|| args[0].equals("-M")|| args[0].equals("-m"))){
            System.err.println("Invalid Parameter");
            System.exit(2);
        }
        System.out.println("Project Start Successfully");
        AnalysisProcess.run(args);
    }
}
