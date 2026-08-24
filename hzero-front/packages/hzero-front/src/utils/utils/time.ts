/**
 * 时间相关
 * @date: 2019-12-25
 * @author: wjc <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import moment from 'moment';

import { DEFAULT_DATE_FORMAT, DEFAULT_DATETIME_FORMAT, DEFAULT_TIME_FORMAT } from '../constants';
import { newArray } from './common';
import { getCurrentUser } from './user';

/**
 * 获取日期(date)格式化字符串
 *
 * @export
 * @returns
 */
export function getDateFormat() {
  const { dateFormat = DEFAULT_DATE_FORMAT } = getCurrentUser();
  return dateFormat;
}

/**
 * 获取日期(dateTime)格式化字符串
 * @export
 * @returns
 */
export function getDateTimeFormat() {
  const { dateTimeFormat = DEFAULT_DATETIME_FORMAT } = getCurrentUser();
  return dateTimeFormat;
}

/**
 * 获取时间(time)格式化字符串
 * @export
 * @returns
 */
export function getTimeFormat() {
  const { timeFormat = DEFAULT_TIME_FORMAT } = getCurrentUser();
  return timeFormat;
}

/**
 * 获取当前设置的时区
 */
export function getTimeZone() {
  const { timeZone } = getCurrentUser();
  return timeZone;
}

/**
 * 生成 antd 的时间禁用支持函数
 * @param {String|Moment} data - 时间日期字符串
 * @param {String} type - 类型，可选 start, end
 */
export function disabledTime(data, type = 'start') {
  const DATE_FORMAT = 'YYYY-MM-DD';
  const timepoint = moment(data);
  const hour = timepoint.hour();
  const minute = timepoint.minute();
  const second = timepoint.second();

  return curDate => {
    const curHour = curDate && curDate.hour();
    const curMinute = curDate && curDate.minute();

    if (curDate && curDate.format(DATE_FORMAT) === timepoint.format(DATE_FORMAT)) {
      return {
        disabledHours() {
          if (type === 'end') {
            if (minute === 0 && second === 0) {
              return newArray(hour, 24);
            }
            return newArray(hour + 1, 24);
          }
          if (minute === 59 && second === 59) {
            return newArray(0, hour + 1);
          }
          return newArray(0, hour);
        },
        disabledMinutes() {
          if (curHour === hour) {
            if (type === 'end') {
              if (second === 0) {
                return newArray(minute, 60);
              }
              return newArray(minute + 1, 60);
            }
            if (second === 59) {
              return newArray(0, minute + 1);
            }
            return newArray(0, minute);
          }
          return [];
        },
        disabledSeconds() {
          if (curHour === hour && curMinute === minute) {
            if (type === 'end') {
              return newArray(second, 60);
            }
            return newArray(0, second + 1);
          }
          return [];
        },
      };
    }
  };
}
