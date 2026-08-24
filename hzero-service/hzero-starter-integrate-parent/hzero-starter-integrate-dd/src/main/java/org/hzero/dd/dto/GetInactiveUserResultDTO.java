package org.hzero.dd.dto;

import java.util.List;

/**
 * Created by tx on 2019/11/24 14:25
 */

public class GetInactiveUserResultDTO extends DefaultResultDTO {


    /**
     * result : {"has_more":false,"list":["1075xxxxx","1075xxxxx","000000000"]}
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * has_more : false
         * list : ["1075xxxxx","1075xxxxx","000000000"]
         */

        private boolean has_more;
        private List<String> list;

        public boolean isHas_more() {
            return has_more;
        }

        public void setHas_more(boolean has_more) {
            this.has_more = has_more;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }
}
