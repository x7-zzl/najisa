package com.zzl.umr.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.config.exception.CommonServiceException;
import com.zzl.umr.enums.GuInsectGenreEnum;
import com.zzl.umr.enums.GuInsectLevelEnum;
import com.zzl.umr.enums.GuTypeEnum;
import com.zzl.umr.mapper.BasicGuInsectMapper;
import com.zzl.umr.mapper.RelUserGuInsectMapper;
import com.zzl.umr.model.BasicGuInsect;
import com.zzl.umr.model.RelUserGuInsect;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.dto.SysMessageDTO;
import com.zzl.umr.service.BasicFileService;
import com.zzl.umr.service.BasicGuInsectService;
import com.zzl.umr.service.MessageProducerService;
import com.zzl.umr.utils.EnumUtil;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.zzl.umr.constants.MessageConstant.ADD_STRING;
import static com.zzl.umr.constants.MessageConstant.DELETE_STRING;
import static com.zzl.umr.constants.MessageConstant.ENUM_GET_CODE_METHOD_NAME;
import static com.zzl.umr.constants.MessageConstant.ENUM_GET_NAME_METHOD_NAME;

/**
 * @author zhangzl
 * @description 蛊虫信息表(BasicGuInsect)表服务实现类
 * @date 2026-01-22 17:04:02
 */
@Service("basicGuInsectService")
@Slf4j
public class BasicGuInsectServiceImpl extends ServiceImpl<BasicGuInsectMapper, BasicGuInsect> implements BasicGuInsectService {
    @Resource
    private BasicGuInsectMapper basicGuInsectMapper;

    @Resource
    private RelUserGuInsectMapper relUserGuInsectMapper;

    @Resource
    private MessageProducerService messageProducerService;

    @Resource
    private BasicFileService basicFileService;
    /**
     * 通过ID查询单条数据
     *
     * @param basicGuInsectId 主键
     * @return 实例对象
     */
    @Override
    public BasicGuInsect queryById(String basicGuInsectId) {
        BasicGuInsect insect = basicGuInsectMapper.selectById(basicGuInsectId);
        if (insect != null) {
            this.dealDataList(java.util.Collections.singletonList(insect));
        }
        return insect;
    }

    /**
     * 查询数据列表
     *
     * @param basicGuInsect 查询参数
     * @return 实例对象集合
     */
    @Override
    public List<BasicGuInsect> queryList(BasicGuInsect basicGuInsect) {
        // 处理查询条件
        LambdaQueryWrapper<BasicGuInsect> queryWrapper = new LambdaQueryWrapper<>();
        this.dealWrapper(queryWrapper, basicGuInsect);

        List<BasicGuInsect> basicGuInsectList = basicGuInsectMapper.selectList(queryWrapper);

        // 处理数据
        this.dealDataList(basicGuInsectList);

        return basicGuInsectList;
    }

    /**
     * 新增数据
     *
     * @param basicGuInsect 实例对象
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean add(BasicGuInsect basicGuInsect) {
        basicGuInsect.setId(SnowflakeIdWorker.newId());
        // 凡蛊数量默认无穷大
        if (GuTypeEnum.COMMON.getCode().equals(basicGuInsect.getGuType())) {
            basicGuInsect.setInventory(Integer.MAX_VALUE);
        }

        //仙蛊唯一
        if (GuTypeEnum.IMMORTAL.getCode().equals(basicGuInsect.getGuType())) {
            basicGuInsect.setInventory(1);
        }

        log.info("插入一条数据:{}", basicGuInsect);
        boolean b = basicGuInsectMapper.insert(basicGuInsect) > 0;

        if (b) {
            SysMessageDTO message = new SysMessageDTO();
            // 把basicGuInsect复制到 message
            message.setBusinessId(basicGuInsect.getId());
            // 蛊虫名称
            message.setName(basicGuInsect.getName());
            // 操作类型
            message.setOperationType(ADD_STRING);
            message.setTimestamp(System.currentTimeMillis());

            // 蛊虫流派
            if (basicGuInsect.getGenre() != null) {
                message.setGenreName(
                        EnumUtil.getEnumNameByCode(GuInsectGenreEnum.class, basicGuInsect.getGenre(), ENUM_GET_CODE_METHOD_NAME, ENUM_GET_NAME_METHOD_NAME)
                );
            }

            // 发送消息
            messageProducerService.sendInsectMessage(message);
        }
        return b;
    }

    /**
     * 批量新增数据
     *
     * @param basicGuInsectList 数据列表
     * @return 新增数量
     */
    @Override
    public Integer batchAdd(List<BasicGuInsect> basicGuInsectList) {
        if (CollectionUtil.isNotEmpty(basicGuInsectList)) {
            log.info("批量插入{}条数据", basicGuInsectList.size());

            for (BasicGuInsect basicGuInsect : basicGuInsectList) {
                basicGuInsect.setId(SnowflakeIdWorker.newId());
            }

            boolean saveBatch = this.saveBatch(basicGuInsectList);
            return saveBatch ? basicGuInsectList.size() : 0;
        }
        return 0;
    }

    /**
     * 修改数据
     *
     * @param basicGuInsect 实例对象
     * @return 是否成功
     */
    @Override
    public boolean updateById(BasicGuInsect basicGuInsect) {
        log.info("修改一条数据:{}", basicGuInsect);
        if (basicGuInsect == null || StringUtils.isBlank(basicGuInsect.getId())) {
            return false;
        }
        BasicGuInsect old = basicGuInsectMapper.selectById(basicGuInsect.getId());
        boolean updated = basicGuInsectMapper.updateById(basicGuInsect) > 0;
        if (updated && old != null) {
            String oldOne = old.getGuInsectAvatarOne();
            String oldTwo = old.getGuInsectAvatarTwo();
            String oldThree = old.getGuInsectAvatarThree();

            String newOne = basicGuInsect.getGuInsectAvatarOne();
            String newTwo = basicGuInsect.getGuInsectAvatarTwo();
            String newThree = basicGuInsect.getGuInsectAvatarThree();

            if (newOne != null && StringUtils.isNotBlank(oldOne) && !StringUtils.equals(oldOne, newOne)) {
                basicFileService.deleteLocalByUrl(oldOne);
            }
            if (newTwo != null && StringUtils.isNotBlank(oldTwo) && !StringUtils.equals(oldTwo, newTwo)) {
                basicFileService.deleteLocalByUrl(oldTwo);
            }
            if (newThree != null && StringUtils.isNotBlank(oldThree) && !StringUtils.equals(oldThree, newThree)) {
                basicFileService.deleteLocalByUrl(oldThree);
            }
        }
        return updated;
    }

    /**
     * 通过主键删除数据
     *
     * @param basicGuInsectId 主键
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean deleteById(String basicGuInsectId) {
        log.info("删除一条数据，Id:{}", basicGuInsectId);

        boolean b = basicGuInsectMapper.deleteById(basicGuInsectId) > 0;
        if (b) {

            try {
                // 删除关联信息表
                LambdaUpdateWrapper<RelUserGuInsect> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(RelUserGuInsect::getGuInsectId, basicGuInsectId);
                int i = relUserGuInsectMapper.deleteById(updateWrapper);
                if (i > 0) {
                    log.info("因天道更迭，蛊虫id为:{}的蛊虫永久消亡，删除用户-蛊虫关联信息表{}条数据", basicGuInsectId, i);
                }
            } catch (CommonServiceException ignored) {
            } finally {
                //查询数据
                BasicGuInsect queryInsect = this.queryById(basicGuInsectId);
                if (queryInsect != null) {
                    SysMessageDTO message = new SysMessageDTO();
                    // 把basicGuInsect复制到 message
                    message.setBusinessId(basicGuInsectId);

                    // 蛊虫名称
                    message.setName(queryInsect.getName());
                    // 操作类型
                    message.setOperationType(DELETE_STRING);
                    message.setTimestamp(System.currentTimeMillis());

                    // 蛊虫流派
                    if (queryInsect.getGenre() != null) {
                        message.setGenreName(
                                EnumUtil.getEnumNameByCode(GuInsectGenreEnum.class, queryInsect.getGenre(), ENUM_GET_CODE_METHOD_NAME, ENUM_GET_NAME_METHOD_NAME)
                        );
                    }

                    // 发送消息
                    messageProducerService.sendInsectMessage(message);
                }
            }


        }
        return b;
    }

    /**
     * 批量删除数据
     *
     * @param idList 主键列表
     * @return 删除数量
     */
    @Override
    public Integer batchDelete(List<String> idList) {
        if (CollectionUtil.isNotEmpty(idList)) {
            log.info("批量删除{}条数据", idList.size());
            return basicGuInsectMapper.deleteBatchIds(idList);
        }
        return 0;
    }


    @Override
    public Page<BasicGuInsect> queryListPage(BaseQueryCdn queryCdn) {
        Page<BasicGuInsect> page = new Page<>(queryCdn.getPageNum(), queryCdn.getPageSize());

        LambdaQueryWrapper<BasicGuInsect> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(queryCdn.getKeyword())) {
            wrapper.like(BasicGuInsect::getName, queryCdn.getKeyword());
        }
        // 查询类别
        if (queryCdn.getGuType() != null) {
            wrapper.eq(BasicGuInsect::getGuType, queryCdn.getGuType());
        }
        // 查询流派
        if (queryCdn.getGenre() != null) {
            wrapper.eq(BasicGuInsect::getGenre, queryCdn.getGenre());
        }
        // 转数
        if (queryCdn.getGuLevel() != null) {
            wrapper.eq(BasicGuInsect::getGuLevel, queryCdn.getGuLevel());
        }
        // 根据更新时间倒序
        wrapper.orderByDesc(BasicGuInsect::getUpdateTime);
        Page<BasicGuInsect> guInsectPageList = this.page(page, wrapper);
        // 处理数据
        this.dealDataList(guInsectPageList.getRecords());
        return guInsectPageList;
    }

    /**
     * 处理查询条件
     *
     * @param queryWrapper  查询条件
     * @param basicGuInsect 查询参数
     */
    private void dealWrapper(LambdaQueryWrapper<BasicGuInsect> queryWrapper, BasicGuInsect basicGuInsect) {
    }

    /**
     * 处理数据列表
     *
     * @param basicGuInsectList 数据列表
     */
    private void dealDataList(List<BasicGuInsect> basicGuInsectList) {
        if (CollectionUtil.isNotEmpty(basicGuInsectList)) {
            for (BasicGuInsect basicGuInsect : basicGuInsectList) {
                // 转换蛊虫类别
                if (basicGuInsect.getGuType() != null) {
                    basicGuInsect.setGuTypeName(
                            EnumUtil.getEnumNameByCode(GuTypeEnum.class, basicGuInsect.getGuType(), ENUM_GET_CODE_METHOD_NAME, ENUM_GET_NAME_METHOD_NAME)
                    );
                }

                // 转换蛊虫流派
                if (basicGuInsect.getGenre() != null) {
                    basicGuInsect.setGenreName(
                            EnumUtil.getEnumNameByCode(GuInsectGenreEnum.class, basicGuInsect.getGenre(), ENUM_GET_CODE_METHOD_NAME, ENUM_GET_NAME_METHOD_NAME)
                    );
                }

                // 转换蛊虫转数
                if (basicGuInsect.getGuLevel() != null) {
                    basicGuInsect.setGuLevelName(
                            EnumUtil.getEnumNameByCode(GuInsectLevelEnum.class, basicGuInsect.getGuLevel(), ENUM_GET_CODE_METHOD_NAME, ENUM_GET_NAME_METHOD_NAME)
                    );
                }
            }

        }
    }
}
