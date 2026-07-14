package com.zzl.umr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.umr.model.AiUserMemory;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI长期记忆表数据库访问层
 */
@Mapper
public interface AiUserMemoryMapper extends BaseMapper<AiUserMemory> {

}
