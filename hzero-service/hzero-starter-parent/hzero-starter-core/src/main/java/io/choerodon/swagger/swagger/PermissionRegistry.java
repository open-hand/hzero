package io.choerodon.swagger.swagger;

import io.choerodon.core.swagger.PermissionData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PermissionRegistry {

    /**
     * key: /example@GET
     */
    private final Map<String, PermissionData> permissionDataMap = new ConcurrentHashMap<>();

    public void register(Map<String, PermissionData> permissionDataMap){
        this.permissionDataMap.putAll(permissionDataMap);
    }

    public void register(String path, PermissionData permissionData){
        this.permissionDataMap.put(path, permissionData);
    }

    public void deregister(String path){
        this.permissionDataMap.remove(path);
    }

    public PermissionData read(String path){
        return permissionDataMap.get(path);
    }


    public Map<String, PermissionData> readMap(){
        return new HashMap<>(permissionDataMap);
    }

}
