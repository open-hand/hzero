package org.hzero.dd.dto;

public class FileFormat {

    /**
     * msgtype : file
     * file : {"media_id":"MEDIA_ID"}
     */

    private String msgtype;
    private FileBean file;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public FileBean getFile() {
        return file;
    }

    public void setFile(FileBean file) {
        this.file = file;
    }

    public static class FileBean {
        /**
         * media_id : MEDIA_ID
         */

        private String media_id;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }
    }
}
