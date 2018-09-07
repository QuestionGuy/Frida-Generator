package guyi;

import java.io.*;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        List<Class<?>> classList = new LinkedList<>();
        for(String className : args){
            try {
                classList.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                System.out.println(String.format("unable to find %s", className));
            }
        }

        writeHooksToFile("hook.js", classList);
    }

    private static void writeHooksToFile(String s, List<Class<?>> classList) throws IOException {
        OutputStream outputStream = null;
        try {

            outputStream = new FileOutputStream(s);
            for (Class c : classList) {
                writeHookToFile(outputStream, c);
            }
        } finally {
            if(outputStream != null) {
                outputStream.close();
            }
        }
    }

    private static void writeHookToFile(OutputStream outputStream, Class c) {
        PrintWriter writer = new PrintWriter(outputStream);
        String classJsName = c.getName().replace('.', '_');
        writer.println("var " + classJsName +" = Java.use(\"" + c.getName() + "\");");
        for(Method m : c.getMethods()) {
            Class<?>[] classArray = m.getParameterTypes();
            StringBuilder argsSb = new StringBuilder();
            for(int i = 0;i < classArray.length;i++){
                argsSb.append(classArray[i].getName());
                if(i == classArray.length - 1) {
                    // do not write trailing comma
                    argsSb.append(',');
                }
            }
            writer.println(classJsName + "." + m.getName() + ".overload(" + argsSb.toString() + ").implementation =" +
                    " function() { this." + m.getName() + ".call(this,arguments);  }" );
        }
    }
}
