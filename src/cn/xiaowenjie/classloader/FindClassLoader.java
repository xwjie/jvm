package cn.xiaowenjie.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *  使用-Xbootclasspath可以把包含 另外一份HelloLoader.class 的目录加到启动的classpath中。
 *  理论上应该有bootstrap ClassLoader加载。
 *  但我们可以先在自己的app loader里面加载他。（必须在使用他之前）
 */
public class FindClassLoader {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ClassLoader loader = FindClassLoader.class.getClassLoader();

        byte[] bytes = loadClassBytes("cn.xiaowenjie.classloader.HelloLoader");

        Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);

        defineClass.setAccessible(true);
        defineClass.invoke(loader, bytes, 0, bytes.length);
        defineClass.setAccessible(false);

        HelloLoader helloLoader = new HelloLoader();
        System.out.println(helloLoader.getClass().getClassLoader());
        helloLoader.print();
    }

    private static byte[] loadClassBytes(String s) {
    }
}
