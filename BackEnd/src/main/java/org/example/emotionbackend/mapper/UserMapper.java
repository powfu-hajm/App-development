package org.example.emotionbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.emotionbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

