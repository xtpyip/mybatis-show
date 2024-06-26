package com.pyip.mybatis;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlSessionFactoryBuilder {
    public DefaultSqlSessionFactory build(Reader reader) {
        // dom4j对xml文件处理的类
        SAXReader saxReader = new SAXReader();
        try {
//            saxReader.setEntityResolver(new XMLMapperEntityResolver());
            // 获取reader文件流的document文档
            Document document = saxReader.read(new InputSource(reader));
            // 解析document xml文档中的配置信息
            Configuration configuration = parseConfiguration(document.getRootElement());
            // 向下传递configuration配置信息
            return new DefaultSqlSessionFactory(configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 将xml文件中的内容设置在configuration中
    public Configuration parseConfiguration(Element root) {
        Configuration configuration = new Configuration();
        configuration.setDataSource(dataSource(root.selectNodes("//dataSource")));
        configuration.setConnection(connection(configuration.dataSource));
        configuration.setMapperElement(mapperElement(root.selectNodes("mappers")));
        return configuration;
    }
    /**
     * 获取数据源配置信息
     * <dataSource type="POOLED">
          <property name="driver" value="com.mysql.jdbc.Driver"/>
          <property name="url" value="jdbc:mysql://127.0.0.1:3306/mybatis?useUnicode=true&amp;characterEncoding=utf8"/>
          <property name="username" value="root"/>
          <property name="password" value="123456"/>
     * </dataSource>
     */
    public Map<String, String> dataSource(List<Element> list) {
        // 设置初始容量为4的dataSource map集合
        Map<String, String> dataSource = new HashMap<>(4);
        Element element = list.get(0);
        List content = element.content();
        for (Object o : content) {
            Element e = (Element) o; // 对每一个<property>标签进行解析，并存入dataSource中
            String name = e.attributeValue("name");
            String value = e.attributeValue("value");
            dataSource.put(name, value);
        }

        return dataSource;
    }
    /**
     * 通过driver,url,username及password建立连接
     * 返回connection连接
     */
    public Connection connection(Map<String,String> dataSource){
        try{
            Class.forName(dataSource.get("driver"));
            return DriverManager.getConnection(dataSource.get("url"),
                    dataSource.get("username"),dataSource.get("password"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取sql语句信息
     *    <mappers>
     *       <mapper resource="mapper/User_Mapper.xml"/>
     *       <mapper resource="mapper/Home_Mapper.xml"/>
     *    </mappers>
     */
    public Map<String,XNode> mapperElement(List<Element> list){
        Map<String, XNode> map = new HashMap<>();
        Element element = list.get(0); // 应该只有一条
        List content = element.content(); // 获取所有mapper标签数据
        for (Object o : content) {
            Element e = (Element) o;
            // 获取xml数据路径 <mapper resource="mapper/User_Mapper.xml"/>
            // <mapper resource="mapper/Home_Mapper.xml"/>
            String resource = e.attributeValue("resource"); // mapper/User_Mapper.xml
            try {
                Reader reader = Resources.getResourceAsReader(resource);
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(new InputSource(reader));
                Element root = document.getRootElement();
                // 命名空间
                // <mapper namespace="com.pyip.mybatis.test.dao.IUserDao">
                String namespace = root.attributeValue("namespace");

                //SELECT
                List<Element> selectNodes = root.selectNodes("select");
                for (Element node : selectNodes) {
                    String id = node.attributeValue("id");
                    String parameterType = node.attributeValue("parameterType");
                    String resultType = node.attributeValue("resultType");
                    String sql = node.getText();
                    // "?" 匹配
                    Map<Integer, String> parameter = new HashMap<>();
                    Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                    Matcher matcher = pattern.matcher(sql);
                    // 将sql语句中的所有的#{xxx}更换为?，并将每个xxx的位置记录为key,值记录为xxx
                    for (int i = 1; matcher.find(); i++) {
                        String g1 = matcher.group(1);
                        String g2 = matcher.group(2);
                        parameter.put(i,g2);
                        sql = sql.replace(g1,"?");
                    }
                    XNode xNode = new XNode();
                    xNode.setNamespace(namespace);
                    xNode.setId(id);
                    xNode.setParameterType(parameterType);
                    xNode.setResultType(resultType);
                    xNode.setSql(sql);
                    xNode.setParameter(parameter);
                    map.put(namespace+"."+id,xNode);
                }
            }catch (Exception g){
                g.printStackTrace();
            }
        }
        return map;

    }

}
