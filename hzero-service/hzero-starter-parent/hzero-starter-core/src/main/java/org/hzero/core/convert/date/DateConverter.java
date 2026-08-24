package org.hzero.core.convert.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hzero.core.convert.exception.DateConvertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * <p>
 * 自定义 Date 转换
 * </p>
 *
 * @author qingsheng.chen 2018/8/20 星期一 19:39
 */
public class DateConverter implements Converter<String, Date> {
    private static final Logger logger = LoggerFactory.getLogger(DateConverter.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Date convert(String date) {
        if (!StringUtils.hasText(date)) {
            return null;
        }
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DATE_FORMAT);
        CustomUserDetails details = DetailsHelper.getUserDetails();
        if (details != null && details.getTimeZone() != null) {
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone(details.getTimeZone()));
        }
        try {
            return dateFormatGmt.parse(date);
        } catch (ParseException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new DateConvertException(String.format("Can't convert %s to %s", date, DATE_FORMAT), e);
        }
    }
}
