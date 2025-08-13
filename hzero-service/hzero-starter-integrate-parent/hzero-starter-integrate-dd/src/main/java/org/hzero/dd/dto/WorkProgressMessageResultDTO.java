package org.hzero.dd.dto;

public class WorkProgressMessageResultDTO {
    /**
     * errcode : 0
     * errmsg : ok
     * progress : {"progress_in_percent":100,"status":2}
     */

    private int errcode;
    private String errmsg;
    private ProgressBean progress;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public ProgressBean getProgress() {
        return progress;
    }

    public void setProgress(ProgressBean progress) {
        this.progress = progress;
    }

    public static class ProgressBean {
        /**
         * progress_in_percent : 100
         * status : 2
         */

        private int progress_in_percent;
        private int status;

        public int getProgress_in_percent() {
            return progress_in_percent;
        }

        public void setProgress_in_percent(int progress_in_percent) {
            this.progress_in_percent = progress_in_percent;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

}
