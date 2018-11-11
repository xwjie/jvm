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

    }
}
