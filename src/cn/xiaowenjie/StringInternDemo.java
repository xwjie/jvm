package cn.xiaowenjie;

import java.util.ArrayList;
import java.util.List;

public class StringInternDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();

        int i = 0;

        while(true){
            list.add(("dd".repeat(1000)+String.valueOf(i++)).intern());
        }
    }
}
