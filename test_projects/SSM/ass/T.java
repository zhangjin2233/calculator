public class T {
    public static void main(String[]args){
        String str = "aaaaa;bbbbbb";
        String[]a = str.split(";");
        int i = a.length;
        for (String s :a
             ) {
            System.out.println(1);

        }
        System.out.println(i);
        String w = test();
        System.out.println(w);
    }

    //测试try中return与finally中的执行顺序
    public static String test(){
        try {
            System.out.println("执行了try中的语句");
            return "执行try中的return";
        }catch (Exception e){

        }finally {
            System.out.println("执行了finally");
        }
        return "执行到最后";
    }
}
