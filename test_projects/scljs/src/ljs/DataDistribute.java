import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataDistribute {
    /**
     * 文件下发操作，只需要把sql语句给阿里云就行
     * @param initTableEntity 临时表实体
     * @param dbDataSource 来源数据库
     * @param distributeSql 下发口径
     * @param srcTablEntity 来源数据表
     * @return 是否成功
     * @throws Exception 抛出所有异常
     */
    public Serializable execute(String initTableEntity,DBDataSource dbDataSource,String distributeSql,String srcTablEntity)throws Exception{
//        1.下发数据初始化（数据清洗？）
//        参数：1.临时表实体；2.来源数据库；3.来源数据表；4.下发口径
//                先创建一张临时表
//        由表实体提供sql语句进行创建
//                将需要下发的数据装入临时表
//        在装入表之前，
//        在临时表进行清洗操作

        List sqls = new ArrayList();
        sqls.add(distributeSql);
//        2.执行下发（数据迁移？）
//        参数：1.临时表名；2.来源数据库；3.下发方式；4.下发口径；5.目标实体
//                将下发口径中的表名进行修改替换
//        根据下发方式选择不同的方法
//        下发方法包含两种（以文件的形式和表的形式）
//        将临时表中的数据通过下发口径下发到目标实体
//
//        3.数据检查（检查下发的数据条数是否正确？）
//        参数：1.质量检核规则；2.目标实体；
//        如果质量检核规则不为空
//                根据质量检核规则对目标实体中的数据进行检查

        return null;
    }
}
