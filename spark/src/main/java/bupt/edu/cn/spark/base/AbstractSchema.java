package bupt.edu.cn.spark.base;

import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import java.io.Serializable;

/*
*   Spark 类型继承Serializable
*   produceBeanEncoder相当于 Encoder<T> tmp= Encoders.bean(T.class);
 * */

public abstract class AbstractSchema<T> implements Serializable {

    @SuppressWarnings("unchecked")
    public Encoder<T> produceBeanEncoder(){
        return (Encoder<T>) Encoders.bean(this.getClass());
    }
}
