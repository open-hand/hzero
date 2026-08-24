package org.hzero.file.infra.init;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.file.infra.config.FileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aspose.words.FileFontSource;
import com.aspose.words.FolderFontSource;
import com.aspose.words.FontSettings;
import com.aspose.words.FontSourceBase;

/**
 * <p>
 * 本地化文档字体设置
 * <p>
 * 字体应用规则，采用Aspose的默认设置，
 * 详见：Aspose.words.resource.MsOfficeFallbackSetting.xml
 * <p>
 * 此类的作用是，当从系统中扫描没有这三个基本字体时
 * 会自动将jar中的字体文件copy到～/.fonts目录下，并将此目录
 * 设置为Aspose的字体扫描路径，这样直接引用此jar
 * 进行文档处理，打印，转换时就不会存在字体找不到
 * 而导致的内容缺失
 * <p>
 * 之所以将jar中的资源字体复制到外部路径中是因为在jar
 * 时只能其添加为内存字体，这样后期如果字体过多过大会导致
 * 严重的内存占用问题
 *
 * @author huifei.liu@hand-chian.com
 * Date: 2019-05-09
 * Description: 本地化字体设置
 */
@Component
public class AsposeFonts {

    private static final Logger log = LoggerFactory.getLogger(AsposeFonts.class);

    private final FileConfig fileConfig;
    private final FontSettings fontSettings;


    public AsposeFonts(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
        fontSettings = FontSettings.getDefaultInstance();
    }


    /**
     * 扩充默认字体集合，此修改Aspose全局生效
     */
    public void extraAsposeFonts() {
        List<FontSourceBase> list = getDiskFontSource();
        if(CollectionUtils.isNotEmpty(list)){
            List<FontSourceBase> fontSources = Arrays.stream(FontSettings.getDefaultInstance().getFontsSources()).collect(Collectors.toList());
            // 扩充Aspose的字体扫描路径
            fontSources.addAll(list);
            fontSettings.setFontsSources(fontSources.toArray(new FontSourceBase[0]));
        }
    }

    /**
     * 从磁盘读取字体资源
     *
     * @return 字体资源集合
     */
    private List<FontSourceBase> getDiskFontSource() {
        if (fileConfig.getAsposeFonts() == null) {
            return new ArrayList<>();
        }
        return fileConfig.getAsposeFonts()
                .stream()
                .map(File::new)
                .filter(file -> {
                    boolean exist = file.exists();
                    if (!exist) {
                        log.warn("font source: [{}] not found", file.getPath());
                    }
                    return exist;
                })
                .map(fontFile -> {
                    FontSourceBase base;
                    if (fontFile.isDirectory()) {
                        base = new FolderFontSource(fontFile.getAbsolutePath(), true);
                    } else {
                        base = new FileFontSource(fontFile.getPath());
                    }
                    return base;
                })
                .collect(Collectors.toList());
    }
}
