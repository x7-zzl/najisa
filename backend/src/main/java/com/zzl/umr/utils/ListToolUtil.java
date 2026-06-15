package com.zzl.umr.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangzl
 * @description 常用集合工具类方法
 * @date 2026/02/04 09:53:20
 */
public class ListToolUtil {


    /**
     *  使用Stream.concat合并两个List
     * @param list1 列表1
     * @param list2 列表2
     * @return 合并后的列表
     */
    public static List<String> mergeListsWithStream(List<String> list1, List<String> list2) {
        return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList());
    }


    /**
     * 使用Stream API：通过分组统计
     * 找出出现次数大于1的元素
     * @param list
     * @return
     */
    public static Set<String> findDuplicatesWithGrouping(List<String> list) {
        return list.stream()
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

}
