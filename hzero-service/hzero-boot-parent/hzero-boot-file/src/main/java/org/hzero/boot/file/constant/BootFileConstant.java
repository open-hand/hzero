package org.hzero.boot.file.constant;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/27 10:02
 */
public class BootFileConstant {

    public static class FileIdType {
        private FileIdType() {
        }

        public static final String URL = "U";
        public static final String KEY = "K";
    }

    public static class ErrorCode {

        private ErrorCode() {
        }

        public static final String GENERATE_HTML = "hfle.error.onlyOffice.generateHtml";
        public static final String REQUEST_DOCUMENT_SERVER = "hfle.error.onlyOffice.request.documentServer";
        public static final String BUILD_HEADER = "hfle.error.onlyOffice.request.header";
        public static final String UPDATE_FILE = "hfle.error.onlyOffice.updateFile";
        public static final String DOWNLOAD = "hfle.error.file.download";
        public static final String CONVERSION = "hfle.error.onlyOffice.conversion";
    }

    /**
     * 文件在线编辑类型
     */
    public static final class EditType {
        private EditType() {
        }

        /**
         * onlyOffice
         */
        public static final String ONLY_OFFICE = "onlyOffice";

        /**
         * hoffice
         */
        public static final String HOFFICE = "hoffice";
    }
}
