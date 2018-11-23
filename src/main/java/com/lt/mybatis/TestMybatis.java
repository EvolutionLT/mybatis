package com.lt.mybatis;

import com.lt.mybatis.bean.User;
import com.lt.mybatis.mapper.UserMapper;
import com.lt.mybatis.sqlSession.MySqlsession;

public class TestMybatis {
     public static void main(String[] args){
         MySqlsession sqlsession=new MySqlsession();
         UserMapper mapper = sqlsession.getMapper(UserMapper.class);
         User user = mapper.getUserById("1");
         System.out.println(user);
     }
}
