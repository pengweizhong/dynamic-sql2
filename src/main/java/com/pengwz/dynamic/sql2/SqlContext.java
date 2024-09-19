package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.core.dml.insert.DataInserter;
import com.pengwz.dynamic.sql2.core.dml.insert.Insert;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.Select;
import com.pengwz.dynamic.sql2.core.dml.update.DataUpdater;
import com.pengwz.dynamic.sql2.core.dml.update.Update;


public class SqlContext {

    private SqlContext() {
    }

    //TODO 回头把扫描包配置作为参数传进来  比如指定扫描的数据源位置、实体类位置、是否开启候补实时加载表等等
    //当涉及子表嵌套查询时，是否分开多条SQL执行？
    //设置单独的数据库模式，比如大陆产的数据库，数据库太多适配麻烦，但是通常会使用mysql或者oracle语法
    //设置如果数据库版本不支持，是否启用兼容模式？用以实现同等效果
    public static SqlContext createSqlContext() {
        return new SqlContext();
    }

    public AbstractColumnReference select() {
        return new Select().loadColumReference();
    }


    public DataInserter insert() {
        return new Insert();
    }

    public DataUpdater update() {
        return new Update();
    }
}
