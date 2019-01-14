import java.util.*;
import java.util.regex.Pattern;

public class Test1 {
    private int first;
    private String str;
    private Boolean aBoolean;

//    public static void main(String[] args) {
//        Test1 test1 = new Test1();
//        String str = "2000.元";
//        System.out.println(isNumeric(str));
//        List<String> arr = new ArrayList();
//        List<String> link = new LinkedList();
//        arr.add("1");
//        arr.add("2");
//        link.add("1");
//        link.add("2");
//        Map<String,String> map = new HashMap<>();
//
//        System.out.println("Arry "+arr.get(0));
//        System.out.println("Link "+link.get(0));
//        for (String a: arr
//             ) {
//            System.out.println(a);
//        }
//        for (String a: link
//                ) {
//            System.out.println(a);
//        }
//    }

    //判断字符串是否为数字
    public static boolean isNumeric(String str){
//        Pattern pattern = Pattern.compile("[0-9]*"); ^[-\+]?[\d]*$
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]");
        if(pattern.matcher(str).matches()){
            System.out.println("测试数字成功");
            return true;
        }else{
            return isDouble(str);
        }
    }

    //判断是否为浮点型
    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        System.out.println("测试浮点成功");
        return pattern.matcher(str).matches();
    }

    public String ifBBB(Boolean aBoolean,String s,int i){
        return s;
    }

    public void a(){
        int i = 9;
        try {
            i = i + 9;
            System.out.println(i);
        }catch (Exception e){
            System.out.println("1111111");
        }finally {
            System.out.println("最后   "+i);
        }
    }


}
