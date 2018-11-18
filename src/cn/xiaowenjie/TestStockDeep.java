package cn.xiaowenjie;

public class TestStockDeep {

    public static  int count=0;

    public static  void recursion(int i1, float f1, long l1, double d1){
        count++;

        // 局部变量
        int i2=1; // 1 word = 32bit
        float f2 = 2f; // 1 word
        long l2 = 3L; // 2 word
        double d2 = 4; // 2 word

        Object o1= null; // 1 word
        Object o2= null; // 1 word

        recursion(i1,f1,l1, d1);
    }

    public static void main(String[] args) {
        // Exception in thread "main" java.lang.OutOfMemoryError: Requested array size exceeds VM limit
        byte[] bs = new byte[Integer.MAX_VALUE-1];

        try {
            recursion(1,2,3,4);
        }
        catch (Throwable e){
            System.out.println(count);
            e.printStackTrace();
        }
    }

    // 复用槽位
    public static  void recursion2(int i1){
        {
            int i2 = i1;
            System.out.println(i2);
        }

        int i3 = 1;
    }

    // 局部变量表是4 word
    public static  void recursion3(double d1){
        {
            double d2 = d1; // 2 word
            System.out.println(d2);
        }

        int i3 = 1; // 1 word 复用 d2的第一个1槽位
        int i4 = 2; // 1 word 复用 d2的第二个操作
    }






}
