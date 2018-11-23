package com.lt.mybatis.sqlSession;

public interface Excutor {
     public <T> T query(String statement,Object parameter);
}
