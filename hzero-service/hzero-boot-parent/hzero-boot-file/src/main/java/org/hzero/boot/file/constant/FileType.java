package org.hzero.boot.file.constant;

/**
 * 文件类型
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/14 19:27
 */
public class FileType {

    private FileType() {
    }

    /**
     * 文件
     */
    public static final class Application {
        private Application() {
        }
        public static final String AI = "application/postscript";
        public static final String EPS = "application/postscript";
        public static final String EXE = "application/x-msdownload";
        public static final String DOC = "application/msword";
        public static final String XLS = "application/vnd.ms-excel";
        public static final String PPT = "application/vnd.ms-powerpoint";
        public static final String PPS = "application/vnd.ms-powerpoint";
        public static final String PDF = "application/vnd.ms-excel";
        public static final String SWF = "application/x-shockwave-flash";
        public static final String GZ = "application/x-gzip ";
        public static final String TGZ = "application/x-compressed ";
        public static final String BZ = "application/x-bzip";
        public static final String BZ2 = "application/x-bzip";
        public static final String TBZ = "application/x-bzip-compressed-tar";
        public static final String ZIP = "application/zip";
        public static final String RAR = "application/x-rar";
        public static final String TAR = "application/x-tar";
    }

    /**
     * 音频
     */
    public static final class Audio {
        private Audio() {
        }

        public static final String MP3 = "audio/mpeg";
        public static final String MID = "audio/mid";
        public static final String OGG = "application/ogg";
        public static final String MPGA = "audio/rn-mpeg";
        public static final String WAV = "audio/wav";
        public static final String WMA = "audio/x-ms-wma";
    }

    /**
     * 视频
     */
    public static final class Video {
        private Video() {
        }

        public static final String AVI = "video/avi";
        public static final String DV = "video/dv";
        public static final String MP4 = "video/mp4";
        public static final String MPEG = "video/mpeg";
        public static final String MPG = "video/mpeg";
        public static final String MOV = "video/quicktime";
        public static final String WM = "video/x-ms-wm";
        public static final String FLV = "video/x-flv";
        public static final String MKV = "video/x-matroska";
    }

    /**
     * 图片
     */
    public static final class Image {
        private Image() {
        }

        public static final String BMP = "image/bmp";
        public static final String JPG = "image/jpeg";
        public static final String JPEG = "image/jpeg";
        public static final String GIF = "image/gif";
        public static final String PNG = "image/png";
        public static final String TIF = "image/tiff";
        public static final String TIFF = "image/tiff";
        public static final String TGA = "image/x-tga";
        public static final String PSD = "image/vnd.adobe.photoshop";
    }

    /**
     * 文本
     */
    public static final class Text {
        private Text() {
        }
        public static final String TXT = "text/plain";
        public static final String PHP = "application/x-php";
        public static final String HTML = "text/html";
        public static final String HTM = "text/html";
        public static final String JS = "application/javascript";
        public static final String CSS = "text/css";
        public static final String RTF = "application/rtf";
        public static final String PY = "text/x-python";
        public static final String JAVA = "text/x-java";
        public static final String RB = "application/x-ruby";
        public static final String SH = "application/x-shellscript";
        public static final String PL = "application/x-perl";
        public static final String SQL = "text/x-sql";
        public static final String XML = "text/xml";
    }
}
