package org.hzero.dd.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author J
 * @Date 2019/9/3
 */
public class LogUtils {
    public static void info(Logger logger, File file,String message) {
        logger.info(message);
        try {
            FileUtils.writeStringToFile(file, "info: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ":" + message+"\n","utf8",true);
        } catch (IOException e) {
            logger.error("error.write.log.file");
        }
    }

    public static void error(Logger logger, File file, String message){
        logger.error(message);
        try {
            FileUtils.writeStringToFile(file, "error: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ":" + message+"\n","utf8",true);
        } catch (IOException e) {
            logger.error("error.write.log.file");
        }
    }

}
