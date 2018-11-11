package cn.xiaowenjie;

public class FinallyDemo {
    public static void main(String[] args) {
        System.out.println(testNoException()); // 3 , finally
        System.out.println(testWithException()); // 3 , finally
    }

    public static int testNoException(){
        try{
            System.out.println("====1");
            byte[] b = new byte[2];
            return 1;
        }
        catch (Throwable e){
            System.out.println("====2");
            e.printStackTrace();
            return 2;
        }
        finally {
            System.out.println("====3");
            return 3;
        }
    }

    public static int testWithException(){
        try{
            System.out.println("----1");
            byte[] b = new byte[Integer.MAX_VALUE-1];

            return 1;
        }
        catch (Throwable e){
            System.out.println("----2");
            e.printStackTrace();
            return 2;
        }
        finally {
            System.out.println("----3");
            return 3;
        }
    }
}
