package org.hzero.export.util;

import org.hzero.core.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * 分片工具类
 *
 * @author XCXCXCXCX
 * @date 2019/8/7
 */
public class ShardUtils {

    public static Pair<Integer, Integer> shard(int size, int shardSize){
        int lastNum = size % shardSize;
        int shardNum;
        if(lastNum == 0){
            shardNum = size / shardSize;
            lastNum = shardSize;
        }else{
            shardNum = size / shardSize + 1;
        }
        return new Pair<>(shardNum, lastNum);
    }

    public static List<List<?>> prepareShardData(List<?> data, int shardNum, int shardSize, int lastNum) {
        List<List<?>> shardData = new ArrayList<>();
        List<?> shard = null;
        for (int i = 0; i < shardNum; i++){
            if(i == shardNum - 1){
                shard = data.subList(i * shardSize, i * shardSize + lastNum);
            }else{
                shard = data.subList(i * shardSize, (i + 1) * shardSize);
            }
            shardData.add(shard);
        }

        return shardData;
    }
}
