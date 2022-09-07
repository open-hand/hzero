package org.hzero.seata.rm.init.datasource.locator;

import io.seata.common.Constants;
import io.seata.common.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author XCXCXCXCX
 * @date 2020/4/17 5:47 下午
 */
public abstract class FileSqlLoader implements SqlLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSqlLoader.class);

    private String directory;

    private String fileName;

    private ClassLoader classLoader;
    
    public FileSqlLoader(String directory, String fileName, ClassLoader classLoader) {
        this.directory = directory;
        this.fileName = fileName;
        this.classLoader = classLoader;
    }

    @Override
    public String load() throws IOException {
        String fullFileName = directory + this.fileName;
        Enumeration<URL> urls;
        if (classLoader != null) {
            urls = classLoader.getResources(fullFileName);
        } else {
            urls = ClassLoader.getSystemResources(fullFileName);
        }

        StringBuilder sql = null;
        if (urls != null) {
            while (urls.hasMoreElements()) {
                sql = new StringBuilder();
                java.net.URL url = urls.nextElement();
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(url.openStream(), Constants.DEFAULT_CHARSET));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        final int ci = line.indexOf('#');
                        if (ci >= 0) {
                            line = line.substring(0, ci);
                        }
                        line = line.trim();
                        if (line.length() > 0) {
                            //读取文件中一行
                            sql.append(line + "\n");
                        }
                    }
                } catch (Throwable e) {
                    LOGGER.warn(e.getMessage());
                } finally {
                    IOUtil.close(reader);
                }
            }
        }
        return sql == null ? null : sql.toString();
    }

    @Override
    public abstract String getSqlType();
}
