package org.hzero.admin.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

public class YamlDTO {

    @ApiModelProperty(value = "yaml 内容体")
    private String yaml;

    @ApiModelProperty(value = "yaml 高量显示")
    private List<HighlightMarker> highlightMarkers = new ArrayList<>();

    @ApiModelProperty(value = "总行数")
    private int totalLine;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    public YamlDTO() {
    }

    public YamlDTO(String yaml, int totalLine) {
        this.yaml = yaml;
        this.totalLine = totalLine;
    }

    public String getYaml() {
        return yaml;
    }

    public void setYaml(String yaml) {
        this.yaml = yaml;
    }

    public List<HighlightMarker> getHighlightMarkers() {
        return highlightMarkers;
    }

    public void setHighlightMarkers(List<HighlightMarker> highlightMarkers) {
        this.highlightMarkers = highlightMarkers;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public void setTotalLine(int totalLine) {
        this.totalLine = totalLine;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    static class HighlightMarker {

        @ApiModelProperty("行")
        private int line;
        @ApiModelProperty("开始坐标")
        private int startIndex;
        @ApiModelProperty("结束坐标")
        private int endIndex;
        @ApiModelProperty("开始列")
        private int startColumn;
        @ApiModelProperty("结束列")
        private int endColumn;

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }

        public int getStartColumn() {
            return startColumn;
        }

        public void setStartColumn(int startColumn) {
            this.startColumn = startColumn;
        }

        public int getEndColumn() {
            return endColumn;
        }

        public void setEndColumn(int endColumn) {
            this.endColumn = endColumn;
        }
    }
}