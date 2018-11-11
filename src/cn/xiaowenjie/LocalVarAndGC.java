package cn.xiaowenjie;

public class LocalVarAndGC {

    public static void main(String[] args) {
        System.out.println("-XX:+PrintGC/ PrintGCDetails");
        System.out.println("--------------1");
        gc();
        System.out.println("-------------2");
        gc2();
        System.out.println("-------------3");
    }

    public static  void gc(){
        {
            byte[] a = new byte[6*1024*1024];
        }

        // a 虽然失效，但仍然在局部变量表，无法gc
        System.gc();
    }

    public static  void gc2(){
        {
            byte[] a = new byte[6*1024*1024];
        }

        int c = 1;

        // a 虽然失效，但仍然在局部变量表，无法gc
        System.gc();
    }

}
