package org.hzero.file.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * description
 *
 * @author fanghan.liu 2020/05/26 13:54
 */
public class OnlyOfficeCallbackDTO {

    /**
     * 新的文件url
     */
    private String url;

    private String key;

    private String changesurl;

    private List<String> users;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 未更新
     */
    private Boolean notmodified;

    /**
     * 修改历史
     */
    private History history;

    public static class History {

        private History(){}

        private String serverVersion;

        private List<Change> changes;

        public static class Change {

            private Change(){}

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
            private LocalDate created;

            private User user;

            public static class User {

                private User(){}

                private String id;

                private String name;

                public String getName() {
                    return name;
                }

                public User setName(String name) {
                    this.name = name;
                    return this;
                }

                public String getId() {
                    return id;
                }

                public User setId(String id) {
                    this.id = id;
                    return this;
                }
            }

            public LocalDate getCreated() {
                return created;
            }

            public Change setCreated(LocalDate created) {
                this.created = created;
                return this;
            }

            public User getUser() {
                return user;
            }

            public Change setUser(User user) {
                this.user = user;
                return this;
            }
        }

        public String getServerVersion() {
            return serverVersion;
        }

        public History setServerVersion(String serverVersion) {
            this.serverVersion = serverVersion;
            return this;
        }

        public List<Change> getChanges() {
            return changes;
        }

        public History setChanges(List<Change> changes) {
            this.changes = changes;
            return this;
        }
    }

    public String getUrl() {
        return url;
    }

    public OnlyOfficeCallbackDTO setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getKey() {
        return key;
    }

    public OnlyOfficeCallbackDTO setKey(String key) {
        this.key = key;
        return this;
    }

    public String getChangesurl() {
        return changesurl;
    }

    public OnlyOfficeCallbackDTO setChangesurl(String changesurl) {
        this.changesurl = changesurl;
        return this;
    }

    public List<String> getUsers() {
        return users;
    }

    public OnlyOfficeCallbackDTO setUsers(List<String> users) {
        this.users = users;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public OnlyOfficeCallbackDTO setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Boolean getNotmodified() {
        return notmodified;
    }

    public OnlyOfficeCallbackDTO setNotmodified(Boolean notmodified) {
        this.notmodified = notmodified;
        return this;
    }

    public History getHistory() {
        return history;
    }

    public OnlyOfficeCallbackDTO setHistory(History history) {
        this.history = history;
        return this;
    }

    @Override
    public String toString() {
        return "OnlyOfficeCallbackDTO{" +
                "url='" + url + '\'' +
                ", key='" + key + '\'' +
                ", changesurl='" + changesurl + '\'' +
                ", users=" + users +
                ", status=" + status +
                ", notmodified=" + notmodified +
                '}';
    }

}
