package com.zzl.umr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.umr.model.AiUserEmotion;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI用户情绪表数据库访问层
 */
@Mapper
public interface AiUserEmotionMapper extends BaseMapper<AiUserEmotion> {

}
