package com.dabangvr.util;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;

/**
 * 序列化和反序列化对象
 *
 * 开发说明：由于存redis的时候传的value值是string类型的，但是目前实际上项目
 *         要存的更多的是一个对象，so
 *         ，这时候就需要把对象序列化成一个json
 *         ，在获取的时候反序列化这个json获取里边的对象
 */
public class JsonUtil {
    //private static Logger logger = Logger.getLogger(JsonUtil.class);
    private static String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        /*------序列化配置-----*/
        //对象的所有字段全部列入
        //        ALWAYS, 将序列化后的所有属性全部列出来，不管值是空还是不空的
        //        NON_NULL, 将只有值的属性列出来
        //        NON_DEFAULT,属性有默认值的不列出来，只列出有属性的值。如果手动set属性值和对象属性默认值一样，也会当成默认值
        //        NON_EMPTY,比NON_NULL  严格一些，因为源码里体现出isempty方法不仅判断值是否为空，还判断值的长度是否等于0
        //总的来说，除了always全部列出来之外，其他都是有条件去限定到底列出哪些属性的。
        //记忆方法
        //      不列_空的
        // 例如：NON_NULL
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);



        //取消默认转换timestamps形式
        //默认是true，所有时间格式都会转为时间戳。
        // 设置为false不让他转换，格式为：yyyy-mm-00T00:00:00.000+0000,但这不是我想要的
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        //所有的日期格式都统一为以下的样式，这才是我想要的时间格式
        objectMapper.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));

        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);




        /*------反序列化配置-----*/
        //忽略 在json字符串中存在 但在java对象中不存在对象属性的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }


    public static <T> String obj2String(T obj){
        if(obj == null){
            return null;
        }
        try {
            return  obj instanceof String ? (String)obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            System.out.println("obj2string error:"+e);
            //e.printStackTrace();
            return null;
        }
    }

    //封装可以返回格式化好的json字符串,,分行显示，整齐的一批
    public static <T> String obj2StringPretty(T obj){
        if(obj == null){
            return null;
        }
        try {
            return  obj instanceof String ? (String)obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            System.out.println("obj2string error:"+e);
            //e.printStackTrace();
            return null;
        }
    }



    /**
     *
     * 反序列化，注意：此方法有弊端，看完往下看下一个方法
     * 把字符串转成一个对象
     * @param str  字符串
     * @param clazz  转的对象
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String str,Class<T> clazz){
        if (StringUtils.isEmpty(str) || clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class)? (T)str : objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            System.out.println("obj2string error:"+e);
            //e.printStackTrace();
            return null;
        }
    }


    /**
     *
     * 反序列化，针对字符串的格式是list包含一个或多个bean的情况下
     *
     * @param str
     * @param typeReference  代表一个具体的类型
     * @param <T>
     * @return
     */
    public  static <T> T string2Obj(String str, TypeReference<T> typeReference){
        if (StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)? str: objectMapper.readValue(str,typeReference));
        } catch (Exception e) {
            System.out.println("obj2string error:"+e);
            //e.printStackTrace();
            return null;
        }
    }


    /**
     *
     * @param str
     * @param colllectionClass 传的这个class代表conllectionclass，用问好
     * @param elemtentClass ...可变查参数，可以一个或多个参数，多个参数可以传输组
     * @param <T>
     *
     *           <?>     为什么要用问号？
     *                   1）：因为对于集合是一个类型（colllectionClass）
     *                   2）：集合里边的元素也是一个类型（elemtentClass）
     *                   而返回的泛型作为一个整体，是一个list<user>。
     * @return
     */
    public  static <T> T string2Obj(String str,Class<?> colllectionClass,Class<?>... elemtentClass){

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(colllectionClass,elemtentClass);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            System.out.println("obj2string error:"+e);
            //e.printStackTrace();
            return null;
        }
    }

}
