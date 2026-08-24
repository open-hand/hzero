package org.hzero.wechat.enterprise.dto;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class FileMessageDTO {


    /**
     * touser : UserID1|UserID2|UserID3
     * toparty : PartyID1|PartyID2
     * totag : TagID1 | TagID2
     * msgtype : file
     * agentid : 1
     * file : {"media_id":"1Yv-zXfHjSjU-7LH-GwtYqDGS-zz6w22KmWAT5COgP7o"}
     * safe : 0
     */

    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private long agentid;
    private FileBean file;
    private int safe;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getTotag() {
        return totag;
    }

    public void setTotag(String totag) {
        this.totag = totag;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public FileBean getFile() {
        return file;
    }

    public void setFile(FileBean file) {
        this.file = file;
    }

    public int getSafe() {
        return safe;
    }

    public void setSafe(int safe) {
        this.safe = safe;
    }

    public static class FileBean {
        /**
         * media_id : 1Yv-zXfHjSjU-7LH-GwtYqDGS-zz6w22KmWAT5COgP7o
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
