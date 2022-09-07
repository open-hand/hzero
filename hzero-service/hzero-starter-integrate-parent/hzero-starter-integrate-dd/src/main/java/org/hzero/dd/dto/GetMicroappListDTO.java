package org.hzero.dd.dto;

import java.util.*;

public class GetMicroappListDTO extends  DefaultResultDTO {
    /**
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "appList": [
     *         {
     *             "appIcon": "http://xxx.dingtalk.com/media/xxx_200_200.png",
     *             "agentId": 111,
     *             "appDesc": "应用描述",
     *             "isSelf": true,
     *             "name": "应用名称",
     *             "homepageLink": "http://oa.dingtalk.com/?h5",
     *             "pcHomepageLink": "http://oa.dingtalk.com/?pc",
     *             "appStatus": 1,
     *             "ompLink": "http://oa.dingtalk.com/index"
     *         }]
     */

    private List<App> appList;

    public List<App> getAppList() {
        return appList;
    }

    public void setAppList(List<App> appList) {
        this.appList = appList;
    }
}
