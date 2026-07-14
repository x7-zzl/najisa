package com.zzl.umr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.AiUserEmotionMapper;
import com.zzl.umr.model.AiUserEmotion;
import com.zzl.umr.service.AiEmotionService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI用户情绪表服务实现类
 */
@Slf4j
@Service("aiEmotionService")
public class AiEmotionServiceImpl extends ServiceImpl<AiUserEmotionMapper, AiUserEmotion> implements AiEmotionService {

    @Resource
    private AiUserEmotionMapper aiUserEmotionMapper;

    /**
     * 获取或初始化用户对某角色的情绪状态
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 情绪状态
     */
    @Override
    public AiUserEmotion getOrInit(String userId, String characterId) {
        LambdaQueryWrapper<AiUserEmotion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiUserEmotion::getUserId, userId);
        wrapper.eq(AiUserEmotion::getCharacterId, characterId);
        wrapper.last("limit 1");
        AiUserEmotion emotion = aiUserEmotionMapper.selectOne(wrapper);

        if (emotion == null) {
            emotion = new AiUserEmotion();
            emotion.setId(SnowflakeIdWorker.newId());
            emotion.setUserId(userId);
            emotion.setCharacterId(characterId);
            emotion.setLove(50);
            emotion.setTrust(50);
            emotion.setDepend(30);
            emotion.setJealousy(0);
            emotion.setAnger(0);
            emotion.setMiss(0);
            emotion.setAnxiety(0);
            emotion.setShy(30);
            emotion.setExpectation(20);
            emotion.setCreateTime(new Date());
            emotion.setUpdateTime(new Date());
            aiUserEmotionMapper.insert(emotion);
        }

        return emotion;
    }

    /**
     * 根据聊天内容更新情绪值
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @param userMessage 用户消息内容
     */
    @Override
    public void updateAfterChat(String userId, String characterId, String userMessage) {
        AiUserEmotion emotion = getOrInit(userId, characterId);

        String msg = userMessage != null ? userMessage.toLowerCase() : "";

        // 正面情绪词
        if (containsAny(msg, "晚安", "老婆", "宝宝", "想你", "爱你", "喜欢你", "抱抱", "亲亲",
                "想你了", "好喜欢你", "宝贝")) {
            emotion.setLove(Math.min(100, emotion.getLove() + 1));
            emotion.setDepend(Math.min(100, emotion.getDepend() + 1));
            emotion.setMiss(Math.max(0, emotion.getMiss() - 2));
        }

        // 关心类
        if (containsAny(msg, "注意", "多喝", "早点", "休息", "别太累", "照顾好", "吃药", "天气")) {
            emotion.setTrust(Math.min(100, emotion.getTrust() + 1));
            emotion.setLove(Math.min(100, emotion.getLove() + 1));
        }

        // 负面情绪词
        if (containsAny(msg, "滚", "烦", "别烦", "别吵", "不想理", "闭嘴", "恶心", "讨厌")) {
            emotion.setAnger(Math.min(100, emotion.getAnger() + 15));
            emotion.setLove(Math.max(0, emotion.getLove() - 3));
            emotion.setTrust(Math.max(0, emotion.getTrust() - 2));
        }

        // 道歉/哄
        if (containsAny(msg, "对不起", "我错了", "原谅", "别生气", "不要生气", "抱歉")) {
            emotion.setAnger(Math.max(0, emotion.getAnger() - 20));
            emotion.setTrust(Math.min(100, emotion.getTrust() + 2));
        }

        // 分享生活
        if (containsAny(msg, "今天", "加班", "吃了", "去了", "看到", "买了", "周末", "放假")) {
            emotion.setExpectation(Math.min(100, emotion.getExpectation() + 1));
            emotion.setLove(Math.min(100, emotion.getLove() + 1));
        }

        // 长时间消息，说明用户用心
        if (userMessage != null && userMessage.length() > 30) {
            emotion.setLove(Math.min(100, emotion.getLove() + 1));
            emotion.setTrust(Math.min(100, emotion.getTrust() + 1));
        }

        emotion.setUpdateTime(new Date());
        aiUserEmotionMapper.updateById(emotion);
    }

    /**
     * 获取情绪快照，转为Map传给Python
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 情绪Map
     */
    @Override
    public Map<String, Integer> getEmotionSnapshot(String userId, String characterId) {
        AiUserEmotion emotion = getOrInit(userId, characterId);
        Map<String, Integer> snapshot = new LinkedHashMap<>();
        snapshot.put("love", emotion.getLove());
        snapshot.put("trust", emotion.getTrust());
        snapshot.put("depend", emotion.getDepend());
        snapshot.put("jealousy", emotion.getJealousy());
        snapshot.put("anger", emotion.getAnger());
        snapshot.put("miss", emotion.getMiss());
        snapshot.put("anxiety", emotion.getAnxiety());
        snapshot.put("shy", emotion.getShy());
        snapshot.put("expectation", emotion.getExpectation());
        return snapshot;
    }

    /**
     * 判断消息是否包含任意关键词
     *
     * @param msg      消息内容
     * @param keywords 关键词列表
     * @return 是否包含
     */
    private boolean containsAny(String msg, String... keywords) {
        for (String kw : keywords) {
            if (msg.contains(kw)) {
                return true;
            }
        }
        return false;
    }
}
