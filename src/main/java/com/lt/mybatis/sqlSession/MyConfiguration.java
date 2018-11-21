package com.lt.mybatis.sqlSession;

import com.mysql.jdbc.Driver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 读取与解析配置信息 并返回处理后的Environment
 */
public class MyConfiguration {
     private static ClassLoader loader = ClassLoader.getSystemClassLoader();

     public Connection build(String resource){
         try{
             InputStream stream= loader.getResourceAsStream(resource);
             SAXReader reader = new SAXReader();
             Document document=reader.read(stream);
             Element root = document.getRootElement();
             return evalDataSource(root);
         }catch (Exception e){
             throw new RuntimeException("error occured while evaling xml " + resource);
         }
             }

    private Connection evalDataSource(Element node) throws ClassNotFoundException {
         if(!node.getName().equals("database")){
             throw  new RuntimeException("node is not <database>");
         }
         String driverClassName = null;
         String url = null;
         String username =null;
         String password = null;
         //获取属性节点
        for(Object item : node.elements("property")){
            Element i= (Element) item;
            String value =getValue(i);
            String name=i.attributeValue("name");
            if(name==null || value==null){
                throw  new RuntimeException("[database] : <property> is not name or value");
            }

            switch (name){
                case "url" : url=value; break;
                case "username" : username=value; break;
                case "password" : password=value;break;
                case "driverClassName" : driverClassName=value;break;
            }
        }
        Class.forName(driverClassName);
        Connection connection=null;
        try{
            connection=DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
       return connection;
    }

    //获取property属性的值,如果有value值,则读取 没有设置value,则读取内容
    private  String getValue(Element node) {
        return node.hasContent() ? node.getText() : node.attributeValue("value");
    }





}
