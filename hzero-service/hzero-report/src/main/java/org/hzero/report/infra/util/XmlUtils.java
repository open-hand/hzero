package org.hzero.report.infra.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XmlUtil,封装了一些以DOM方式获得Xml对象的方法 。<br/>
 * <b>仅mulThreadParse方法是线程安全的</b>
 *
 * @author xianzhi.chen@hand-china.com 2018年11月28日下午3:04:21
 */
public class XmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    private static DocumentBuilder builder;
    private static final DocumentBuilderFactory FACTORY;

    private XmlUtils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    static {
        FACTORY = DocumentBuilderFactory.newInstance();
        try {
            builder = FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("error", e);
        }
    }

    /**
     * 通过一个InputSource来创建Xml文档对象
     *
     * @param inputSource 包含Xml文本信息的InputSource
     * @return 文档对象
     */
    public static Document parse(InputSource inputSource) throws SAXException, IOException {
        return builder.parse(inputSource);
    }

    /**
     * 通过一个InputStream来创建Xml文档对象
     *
     * @param inputStream 包含Xml文本信息的InputStream
     * @return Xml文档对象
     */
    public static Document parse(InputStream inputStream) throws SAXException, IOException {
        return builder.parse(inputStream);
    }

    /**
     * 通过一个File来创建Xml文档对象
     *
     * @param file 包含Xml文本信息的File对象
     * @return Xml文档对象
     */
    public static Document parse(File file) throws SAXException, IOException {
        return builder.parse(file);
    }

    /**
     * 通过一个url来创建Xml文档对象<br/>
     * <b style="color:red;">注意此方法不是解析String类型Xml文本的方法,若想解析,请将String对象转化为InputStream</b>
     *
     * @param url 获得Xml文本的URL
     * @return Xml文档对象
     */
    public static Document parse(String url) throws SAXException, IOException {
        return builder.parse(url.replace(" ", "%20"));
    }

    /**
     * 可用于线程安全的解析函数<br/>
     * <i>参数类型说明参考其他parse函数</i>
     *
     * @param param 参数，必须是String/File/InputStream/InputSource中的一种
     * @return Xml文档对象
     */
    public static Document mulThreadParse(Object param)
            throws SAXException, IOException, ParserConfigurationException {
        if (param instanceof String) {
            return FACTORY.newDocumentBuilder().parse((String) param);
        }
        if (param instanceof File) {
            return FACTORY.newDocumentBuilder().parse((File) param);
        }
        if (param instanceof InputStream) {
            return FACTORY.newDocumentBuilder().parse((InputStream) param);
        }
        if (param instanceof InputSource) {
            return FACTORY.newDocumentBuilder().parse((InputSource) param);
        }
        throw new IllegalArgumentException("不接受此类型的参数");
    }
}
