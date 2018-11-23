package cn.xiaowenjie.bytecode;

public class ConstDemo {

    public void test(){
        // const 指令 => iconst_1
        int a = 1;

        // bipush  = > bipush 127
        int b = 127;

        // sipush => sipush 128
        int c = 128;

        // ldc 指令, 33333 会作为常量，常量的index会作为参数
        // => ldc #2   // int 33333
        int a2 = 33333;

        float f = 0f;
        double d = 0d;
        long l  = 1L;
    }
}
