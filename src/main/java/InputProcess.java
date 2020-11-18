public class InputProcess {
    public String ClassPath;
    public String ChangeInfoPath;
    public char type;//记录分析类型

    public InputProcess(String[] args) {
        String parameter = args[0];
        ClassPath = args[1];
        ChangeInfoPath = args[2];
        if (parameter.equals("-c"))
            type = 'c';
        else
            type = 'm';
    }
}
