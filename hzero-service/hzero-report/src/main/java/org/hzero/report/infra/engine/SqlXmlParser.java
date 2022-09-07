package org.hzero.report.infra.engine;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.infra.engine.data.SqlXmlLeaf;
import org.hzero.report.infra.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML配置的SQL处理工具
 *
 * @author xianzhi.chen@hand-china.com 2018年11月21日下午2:45:02
 */
public class SqlXmlParser {

    /**
     * XML多级参数匹配表达式，如{{parameter}}
     */
    public static final String PARAMETER_REGEX = "\\{\\{.*?\\}\\}";
    /**
     * XML类型SQL的简单规则字符
     */
    private static final String XML_SQL_REGEX = "<select";
    /**
     * select 查询语句标签
     */
    private static final String SQL_TAG_SELECT = "select";
    /**
     * column 查询列标签
     */
    private static final String SQL_TAG_COLUMN = "column";
    /**
     * from 查询表标签
     */
    private static final String SQL_TAG_FROM = "from";
    /**
     * where 查询条件标签
     */
    private static final String SQL_TAG_WHERE = "where";
    /**
     * 标签属性名称
     */
    private static final String SQL_TAG_ATTR_NAME = "name";

    /**
     * 根节点
     */
    private SqlXmlLeaf root;
    /**
     * 节点池
     */
    private Map<String, SqlXmlLeaf> leafMap = new HashMap<>(BaseConstants.Digital.SIXTEEN);

    /**
     * 判断是否为XML配置的SQL语句
     */
    public static boolean isSqlXml(String sql) throws SAXException, IOException, ParserConfigurationException {
        int cnt = StringUtils.countMatches(sql, XML_SQL_REGEX);
        if (cnt > 0) {
            InputStream in = new BufferedInputStream(new ByteArrayInputStream(sql.getBytes(StandardCharsets.UTF_8)));
            XmlUtils.mulThreadParse(in);
            return true;
        }
        return false;
    }

    /**
     * 添加叶子节点
     */
    private boolean addLeaf(String leafName, String sql, String parentName) {
        if (StringUtils.isBlank(parentName)) {
            // 尝试插入根节点
            if (this.root != null) {
                return false;
            } else {
                this.root = new SqlXmlLeaf(leafName, sql);
                this.leafMap.put(leafName, this.root);
                return true;
            }
        } else {
            // 尝试插入普通节点
            if (StringUtils.isBlank(leafName)) {
                return false;
            } else {
                SqlXmlLeaf parent = this.leafMap.get(parentName);
                if (parent == null) {
                    return false;
                } else {
                    SqlXmlLeaf leaf = new SqlXmlLeaf(leafName, sql);
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(leaf);
                    this.leafMap.put(leafName, leaf);
                    return true;
                }
            }
        }
    }

    /**
     * 初始化SQL XML
     */
    public SqlXmlLeaf getSqlLeaf(String xmlStr) throws SAXException, IOException, ParserConfigurationException {
        InputStream in = new BufferedInputStream(new ByteArrayInputStream(xmlStr.getBytes(StandardCharsets.UTF_8)));
        Document document = XmlUtils.mulThreadParse(in);
        this.initNode(document.getDocumentElement(), null);
        return root;
    }

    /**
     * 递归初始化叶节点
     *
     * @param ele        XML元素节点
     * @param parentName 父节点名称[处理根节点时传null]
     */
    private void initNode(Element ele, String parentName) {
        // 初始化变量
        String thisName = ele.getAttribute(SQL_TAG_ATTR_NAME);
        Element colum = null;
        Element from = null;
        Element where = null;
        List<Element> children = new ArrayList<>();
        // Xml Node分类归集
        NodeList childNodes = ele.getChildNodes();
        for (int i = 0, length = childNodes.getLength(); i < length; i++) {
            if (!(childNodes.item(i) instanceof Element)) {
                continue;
            }
            Element temp = (Element) childNodes.item(i);
            switch (temp.getTagName()) {
                case SQL_TAG_COLUMN:
                    colum = temp;
                    break;
                case SQL_TAG_FROM:
                    from = temp;
                    break;
                case SQL_TAG_WHERE:
                    where = temp;
                    break;
                case SQL_TAG_SELECT:
                    children.add(temp);
                    break;
                default:
                    break;
            }
        }
        // 组装叶节点
        StringBuilder sb = new StringBuilder(SQL_TAG_SELECT);
        sb.append(" ");
        if (colum != null) {
            sb.append(colum.getTextContent());
        }
        sb.append(" ");
        if (from != null) {
            sb.append(SQL_TAG_FROM);
            sb.append(" ");
            sb.append(from.getTextContent());
        }
        sb.append(" ");
        if (where != null) {
            sb.append(SQL_TAG_WHERE);
            sb.append(" ");
            sb.append(where.getTextContent());
        }
        this.addLeaf(thisName, sb.toString(), parentName);
        // 如果有子节点，递归执行初始化
        if (CollectionUtils.isNotEmpty(children)) {
            for (Element child : children) {
                initNode(child, thisName);
            }
        }
    }

}
