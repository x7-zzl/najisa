package com.zzl.umr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.AiCharacterMapper;
import com.zzl.umr.model.AiCharacter;
import com.zzl.umr.service.AiCharacterService;
import com.zzl.umr.enums.CommonEnabledEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI角色表服务实现类
 */
@Slf4j
@Service("aiCharacterService")
public class AiCharacterServiceImpl extends ServiceImpl<AiCharacterMapper, AiCharacter> implements AiCharacterService {

    @Resource
    private AiCharacterMapper aiCharacterMapper;

    /**
     * 通过ID查询角色
     *
     * @param id 角色ID
     * @return 角色
     */
    @Override
    public AiCharacter queryById(String id) {
        return aiCharacterMapper.selectById(id);
    }

    /**
     * 获取所有启用的角色列表
     *
     * @return 角色列表
     */
    @Override
    public List<AiCharacter> queryEnabledList() {
        LambdaQueryWrapper<AiCharacter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiCharacter::getIsEnabled, CommonEnabledEnum.ENABLED.getCode());
        wrapper.orderByAsc(AiCharacter::getName);
        return aiCharacterMapper.selectList(wrapper);
    }

    /**
     * 获取默认角色，zhang-yu-han
     *
     * @return 角色
     */
    @Override
    public AiCharacter getDefaultCharacter() {
        List<AiCharacter> list = queryEnabledList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}
