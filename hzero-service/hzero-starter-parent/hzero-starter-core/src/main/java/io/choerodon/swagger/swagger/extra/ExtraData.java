package io.choerodon.swagger.swagger.extra;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuguokai
 */
public class ExtraData {

    public static final String EXTRA_DATA_KEY = "extraData";

    public static final String ZUUL_ROUTE_DATA = "choerodon_route";

    public static final String URL_MODULAR_MAP_KEY = "url_modular_map";

    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void put(String key, Object o) {
        if (this.data == null) {
            this.data = new HashMap<>(8);
        }
        this.data.put(key, o);
    }

    public Object get(String key){
        if (this.data == null){
            return null;
        }
        return this.data.get(key);
    }

    public Object remove(String key){
        if (this.data == null){
            return null;
        }
        return this.data.remove(key);
    }

    @Override
    public String toString() {
        return "ExtraData{" +
                "data=" + data +
                '}';
    }
}
