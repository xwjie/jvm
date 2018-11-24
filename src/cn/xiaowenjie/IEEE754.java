package cn.xiaowenjie;

public class IEEE754 {
    public static void main(String[] args) {
        float f = -5;

        // float的内部表示
        System.out.println(Integer.toBinaryString(Float.floatToRawIntBits(f)));


        double d = -5;

        // double 的内部表示
        System.out.println(Long.toBinaryString(Double.doubleToRawLongBits(d)));

        System.out.println(Integer.toBinaryString(Float.floatToRawIntBits(Float.MIN_NORMAL)));
        System.out.println(Long.toBinaryString(Double.doubleToRawLongBits(Double.MIN_NORMAL)));

        // 浮点数的大小比较为什么不能用等号？
        {
            float f1 = 0.1f;
            float f2 = 0.1f;

            System.out.println(f1 == f2); // true

            // 默认是 double
            System.out.println(15.1 * 100 + 0.9 * 100 == 16.0 * 100); //true

            // 默认是 double
            System.out.println(16.1 * 100 + 0.9 * 100 == 17.0 * 100); //false

            // float
            System.out.println(16.1f * 100 + 0.9f * 100 == 17.0f * 100); //true

            System.out.println(Float.compare(16.1f * 100 + 0.9f * 100, 17.0f * 100)); //0

            System.out.println((16.1 + 0.9) * 100 == 17.0 * 100); // true
        }

        {
            float f1 = 1f / 3;
            float f2 = 1f / 3;
            System.out.println(f1 == f2); // true
        }
    }
}
