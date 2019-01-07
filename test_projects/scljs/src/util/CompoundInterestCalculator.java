import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class CompoundInterestCalculator {


    /**
     * 定存复利计算
     * @param amount1 金额
     * @param interestRate 利率
     * @param period 周期
     * @param time 年限
     * @return 最后总额
     */
    public double calculateTotalAmount(double amount1,double interestRate,String period,int time){
        BigDecimal yearAmount = new BigDecimal(amount1);

        if("月".equals(period)) yearAmount = yearAmount.multiply(new BigDecimal(12));

        BigDecimal totalRate = new BigDecimal(1 + interestRate);
        BigDecimal finalMoney = yearAmount.multiply(totalRate);
        System.out.println("第 1 年总额： "+finalMoney);
        for (int i = 1; i < time; i++) {
            totalRate = totalRate.multiply(new BigDecimal(1 + interestRate));
            finalMoney = finalMoney.add(yearAmount.multiply(totalRate));
            System.out.println("第 "+(i+1)+" 年总额： "+finalMoney.doubleValue()+"  利率："+totalRate.doubleValue());
        }
        return finalMoney.doubleValue();
    }


    /**
     * 计算定期储蓄的总时间
     * @param finalMoney1 目标金额
     * @param interestRate 利率
     * @param period 定存周期
     * @param amount1 定存金额
     * @return
     */
    public double calculateTotalTime(double finalMoney1,double interestRate,String period,double amount1){
        double allTime ;
        BigDecimal yearAmount = new BigDecimal(amount1);

        if("月".equals(period)) yearAmount = yearAmount.multiply(new BigDecimal(12));

        BigDecimal totalRate = new BigDecimal(1 + interestRate);
        BigDecimal myFinalMoney = new BigDecimal(finalMoney1);
        BigDecimal finalMoney = yearAmount.multiply(totalRate);
        System.out.println("第 1 年总额： "+finalMoney);
        if (finalMoney.compareTo(myFinalMoney)>0){
            return 1;
        }
        int time = 2;
        for (int i = 1; i < time; i++) {
            time+=1;
            totalRate = totalRate.multiply(new BigDecimal(1 + interestRate));
            finalMoney = finalMoney.add(yearAmount.multiply(totalRate));
            System.out.println("第 "+(i+1)+" 年总额： "+finalMoney.doubleValue()+"  利率："+totalRate.doubleValue());
//            如果计算出的数额大于输入数额则退出循环
            if (finalMoney.compareTo(myFinalMoney)>0){
                time = i+1;
                break;
            }
        }
        return time;
    }

    /**
     * 计算日期间隔
     * @param beginDate 起始日期（小的日期）
     * @param endDate 结束日期（大的日期）
     * @return 间隔天数
     */
    public long calculate(LocalDate beginDate, LocalDate endDate){
        Long days = endDate.toEpochDay() - beginDate.toEpochDay();
        System.out.println(days);
        return days;
    }

    public static void main(String[] args) {
        double amount = 4000;
        double interestRate = 0.1;
        String period = "月";
        int time = 10;
        CompoundInterestCalculator calculator = new CompoundInterestCalculator();

//        计算总金额
        double d = calculator.calculateTotalAmount(amount,interestRate,period,time);
        System.out.println(d);
        System.out.println("一般"+amount*12*time);

//        计算总时间
//        double d = calculator.calculateTotalTime(1000000,0.1,"月",5000);
//        System.out.println(d);

//        LocalDate beginDate = LocalDate.of(2018,11,6);
//        LocalDate endDate = LocalDate.of(2019,1,4);
//        calculator.calculate(beginDate,endDate);
    }
}
