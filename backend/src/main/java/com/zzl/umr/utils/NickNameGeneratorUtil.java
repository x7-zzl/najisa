package com.zzl.umr.utils;

import java.util.Random;

/**
 * @author zhangzl
 * @description 随机生成用户昵称工具类
 * @date 2026/01/15 19:19:15
 */
public class NickNameGeneratorUtil {

    private static final String[] PREFIXES = {
            "快乐的", "悲伤的", "忧郁的", "开心的", "愉悦的", "激动的", "温柔的", "坚强的", "冷静的", "焦虑的",
            "忧虑的", "宁静的", "疲惫的", "愤怒的", "激烈的", "平静的", "期待的", "紧张的", "满足的", "舒适的",
            "惊讶的", "激奋的", "怀疑的", "孤独的", "信任的", "坚韧的", "温暖的", "满足的", "温馨的", "释然的",
            "高兴的", "兴奋的", "自在的", "迷茫的", "宽慰的", "冷酷的", "疯狂的", "焦急的", "迷恋的", "悲哀的",
            "温和的", "振奋的", "爽朗的", "沉静的", "灿烂的", "惆怅的", "伤感的", "难过的", "忧伤的", "自信的"
    };

    private static final String[] SUFFIXES = {
            "蛊虫", "蛊虫", "蛊虫", "蛊虫", "蛊虫", "蛊虫", "蛊虫", "蛊材", "蛊材", "蛊材", "蛊材",
            "原石", "蛊师", "真人", "蛊材", "真元", "阵法", "蛊尊", "平民", "仙元石", "凡道杀招",
            "仙道杀招", "仙蛊", "仙蛊屋", "梦境", "仙尊", "魔尊", "圣人", "仙人", "传奇", "天命",
            "太古传奇", "道痕", "法则", "天意", "我意", "人意", "画中人",
    };

    private static final Random RANDOM = new Random();

    /**
     * Generates a random nickname by combining a prefix, a suffix, and a random number.
     *
     * @return a randomly generated nickname.
     */
    public static String generateNickname() {
        String prefix = PREFIXES[RANDOM.nextInt(PREFIXES.length)];
        String suffix = SUFFIXES[RANDOM.nextInt(SUFFIXES.length)];

        int randomNumber = RANDOM.nextInt(1000);

        // Combine them to form a nickname
        return prefix + suffix + randomNumber;
    }
}
