package io.choerodon.swagger.swagger;

import io.choerodon.core.swagger.LabelData;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LabelRegistry {

    /**
     * key: /example@GET
     */
    private final Map<String, LabelData> labelDataMap = new ConcurrentHashMap<>();

    public void register(Map<String, LabelData> permissionDataMap){
        this.labelDataMap.putAll(permissionDataMap);
    }

    public void register(String path, LabelData permissionData){
        this.labelDataMap.put(path, permissionData);
    }

    public void deregister(String path){
        this.labelDataMap.remove(path);
    }

    public LabelData read(String path){
        return labelDataMap.get(path);
    }

    public Map<String, LabelData> readMap(){
        return new HashMap<>(labelDataMap);
    }

}
