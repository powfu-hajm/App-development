package org.example.emotionbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.emotionbackend.entity.TestRecord;

@Mapper
public interface TestRecordMapper extends BaseMapper<TestRecord> {
}
