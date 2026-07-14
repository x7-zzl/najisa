package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.AiCharacter;

import java.util.List;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI角色表服务接口
 */
public interface AiCharacterService extends IService<AiCharacter> {

    /**
     * 通过ID查询角色
     *
     * @param id 角色ID
     * @return 角色
     */
    AiCharacter queryById(String id);

    /**
     * 获取所有启用的角色列表
     *
     * @return 角色列表
     */
    List<AiCharacter> queryEnabledList();

    /**
     * 获取默认角色，zhang-yu-han
     *
     * @return 角色
     */
    AiCharacter getDefaultCharacter();
}
