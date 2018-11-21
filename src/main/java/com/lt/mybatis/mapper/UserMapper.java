package com.lt.mybatis.mapper;

import com.lt.mybatis.bean.User;

public interface UserMapper {
    /**
     * 根据id查询用户信息
     * @param id 主键
     * @return
     */
    public User getUserById(String id);
}
