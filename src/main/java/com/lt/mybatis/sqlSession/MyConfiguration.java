package com.lt.mybatis.sqlSession;

import com.lt.mybatis.config.Function;
import com.lt.mybatis.config.MapperBean;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 读取与解析配置信息 并返回处理后的Environment
 */
public class MyConfiguration {
     private static ClassLoader loader = ClassLoader.getSystemClassLoader();

     public Connection build(String resource){
         System.out.println(resource);
         try{
             InputStream stream= loader.getResourceAsStream(resource);
             SAXReader reader = new SAXReader();
             Document document=reader.read(stream);
             Element root = document.getRootElement();
             return evalDataSource(root);
         }catch (Exception e){
             System.out.println("报错信息---"+e);
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
            System.out.println(name+":"+value);
            if(!name.equals("password")){
                if(name==null || value==null){
                    throw  new RuntimeException("[database] : <property> is not name or value");
                }
            }
            switch (name){
                case "url" : url=value; break;
                case "username" : username=value; break;
                case "password" : password=value;break;
                case "driverClassName" : driverClassName=value;break;
            }
        }
        Class.forName(driverClassName);
        System.out.println(driverClassName);
        Connection connection=null;
        try{
            System.out.println(url+username+password);
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

    public MapperBean readMapper(String path){
         MapperBean  mapper =new MapperBean();
         try {
             InputStream stream= loader.getResourceAsStream(path);
             SAXReader reader = new SAXReader();
             Document document=reader.read(stream);
             Element root =document.getRootElement();
             //把mapper节点的nameSpace值存为接口
             mapper.setInterfaceName(root.attributeValue("nameSpace").trim());
             //存储方法的List
             List<Function> list =new ArrayList<Function>();
             for(Iterator rootIter =root.elementIterator();rootIter.hasNext();){
                 //遍历根节点下的所有子节点
                 //用来存储方法信息
                 Function fun =new Function();
                 Element e = (Element) rootIter.next();
                 String sqltype = e.getName().trim();
                 String funcName = e.attributeValue("id").trim();
                 String sql = e.getText().trim();
                 String resultType = e.attributeValue("resultType").trim();
                 fun.setSqltype(sqltype);
                 fun.setFuncName(funcName);
                 Object newInstance=null;

                   try{
                       newInstance=Class.forName(resultType).newInstance();
                   } catch (IllegalAccessException e1) {
                       e1.printStackTrace();
                   } catch (InstantiationException e1) {
                       e1.printStackTrace();
                   } catch (ClassNotFoundException e1) {
                       e1.printStackTrace();
                   }
                   fun.setResultType(newInstance);
                   fun.setSql(sql);
                   list.add(fun);
              }
              mapper.setList(list);


         } catch (DocumentException e) {
             e.printStackTrace();
         }
        return mapper;
    }



}
