package org.hzero.file.infra.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.hzero.core.base.BaseConstants;

import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.SaveFormat;

import io.choerodon.core.exception.CommonException;

/**
 * @author lyq on 2018/12/27.
 */
public class TextUtils {

    private TextUtils() {
    }

    public static void wordToPdf(InputStream sourceStream, OutputStream outputStream, Map<String, String> vars) {
        try {
            com.aspose.words.Document doc = new com.aspose.words.Document(sourceStream);
            // 接受修订
            doc.acceptAllRevisions();
            // 去掉评论
            NodeCollection comments = doc.getChildNodes(NodeType.COMMENT, true);
            comments.clear();
            doc.save(outputStream, SaveFormat.PDF);
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }
}
