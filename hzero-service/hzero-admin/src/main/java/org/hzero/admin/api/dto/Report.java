package org.hzero.admin.api.dto;

import org.hzero.core.message.MessageAccessor;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 2:45 下午
 */
public class Report {

    private Boolean success = true;

    private Map<String, String> instanceErrorMessages = new HashMap<>();

    private String message;

    public Report() {
    }

    public Report(Map<String, String> instanceErrorMessages) {
        if (!CollectionUtils.isEmpty(instanceErrorMessages)) {
            this.success = false;
            this.instanceErrorMessages = instanceErrorMessages;
        }
    }

    private String merge(Map<String, String> instanceErrorMessages) {
        StringBuilder builder = new StringBuilder();
        int sum = instanceErrorMessages.size();
        builder.append(MessageAccessor.getMessage("hadm.error.maintain_rule_push_failed_sum", new Object[]{sum}).desc());
        builder.append("\n");
        builder.append(MessageAccessor.getMessage("hadm.error.maintain_rule_push_failed_tips").desc());
        builder.append("\n");
        for (Map.Entry<String, String> entry : instanceErrorMessages.entrySet()) {
            builder
                    .append(" - ")
                    .append(MessageAccessor.getMessage("hadm.error.maintain_rule_push_failed", new Object[]{entry.getKey()}).desc())
                    .append("\n ");
        }
        return builder.toString();
    }

    public Boolean getSuccess() {
        return success;
    }

    public Map<String, String> getInstanceErrorMessages() {
        return instanceErrorMessages;
    }

    public String getMessage() {
        return merge(instanceErrorMessages);
    }

}
