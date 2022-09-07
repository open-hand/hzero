/**
 * 通用-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-7-8
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

export const PREFIX = 'hfnt.common';

export default {
  PREFIX,
  DW_SCRIPT_TIP: intl
    .get(`${PREFIX}.view.tip.dw`)
    .d('请参考DataWeave Language语法编写映射脚本，官方文档参见'),

  SECOND: intl.get(`${PREFIX}.view.cron.second`).d('秒'),
  MINUTE: intl.get(`${PREFIX}.view.cron.minute`).d('分钟'),
  HOUR: intl.get(`${PREFIX}.view.cron.hour`).d('小时'),
  DAY: intl.get(`${PREFIX}.view.cron.day`).d('日'),
  MONTH: intl.get(`${PREFIX}.view.cron.month`).d('月'),
  WEEK: intl.get(`${PREFIX}.view.cron.week`).d('周'),
  YEAR: intl.get(`${PREFIX}.view.cron.year`).d('年'),

  EVERY_DAY: intl.get(`${PREFIX}.view.cron.everyDay`).d('每日'),
  NOT_SPECIFY: intl.get(`${PREFIX}.view.cron.notSpecify`).d('不指定'),
  SPECIFY: intl.get(`${PREFIX}.view.cron.specify`).d('指定'),
  PERIOD: intl.get(`${PREFIX}.view.cron.period`).d('周期'),
  FROM: intl.get(`${PREFIX}.view.cron.from`).d('从'),
  TO: intl.get(`${PREFIX}.view.cron.to`).d('到'),
  FROM_CERTAIN: intl.get(`${PREFIX}.view.cron.fromCertain`).d('从第'),
  PER_DAY_START: intl.get(`${PREFIX}.view.cron.perDayStart`).d('日开始，每'),
  ONE_TIME_FOR_DAY: intl.get(`${PREFIX}.view.cron.oneTimeForDay`).d('天执行一次'),
  EVERY_MONTH: intl.get(`${PREFIX}.view.cron.everyMonth`).d('每月'),
  LASTEST_WORK_DAY: intl.get(`${PREFIX}.view.cron.lastestWorkDay`).d('日最近的那个工作日'),
  LAST_THE_MONTH: intl.get(`${PREFIX}.view.cron.lastTheMonth`).d('本月最后'),

  EVERY_HOUR: intl.get(`${PREFIX}.view.cron.everyDay`).d('每小时'),
  SINGLE_HOUR: intl.get(`${PREFIX}.view.cron.singleHour`).d('时'),
  PER_FROM_HOUR: intl.get(`${PREFIX}.view.cron.perFromHour`).d('时开始，每'),
  ONE_TIME_FOR_HOUR: intl.get(`${PREFIX}.view.cron.oneTimeForHour`).d('时执行一次'),
  MIN_NUM: intl.get(`${PREFIX}.view.cron.minNum`).d('至少选择一项'),

  EVERY_MINUTE: intl.get(`${PREFIX}.view.cron.everyMinute`).d('每日'),
  SINGLE_MINUTE: intl.get(`${PREFIX}.view.cron.singleMinute`).d('分'),
  PER_FROM_MINUTE: intl.get(`${PREFIX}.view.cron.perFromMinute`).d('分开始，每'),
  ONE_TIME_FOR_MINUTE: intl.get(`${PREFIX}.view.cron.oneTimeForMinute`).d('分执行一次'),

  SINGLE_MONTH: intl.get(`${PREFIX}.view.cron.singleMonth`).d('月'),
  SINGLE_DAY: intl.get(`${PREFIX}.view.cron.singleMonth`).d('天'),
  ONE_TIME_FOR_MONTH: intl.get(`${PREFIX}.view.cron.oneTimeForMonth`).d('月执行一次'),

  EVERY_SECOND: intl.get(`${PREFIX}.view.cron.everySecond`).d('每秒'),
  SINGLE_SECOND: intl.get(`${PREFIX}.view.cron.singleSecond`).d('秒'),
  PER_FROM_SECOND: intl.get(`${PREFIX}.view.cron.perFromSecond`).d('秒开始，每'),
  ONE_TIME_FOR_SECOND: intl.get(`${PREFIX}.view.cron.oneTimeForSecond`).d('秒执行一次'),

  EVERY_WEEK: intl.get(`${PREFIX}.view.cron.everyWeek`).d('每周'),
  SINGLE_WEEK: intl.get(`${PREFIX}.view.cron.singleWeek`).d('周'),
  CERTAIN: intl.get(`${PREFIX}.view.cron.certain`).d('第'),
  THE_WEEK: intl.get(`${PREFIX}.view.cron.theWeek`).d('周的'),
  WEEK_DAY: intl.get(`${PREFIX}.view.cron.weekDay`).d('星期'),

  LAST_ONE_MONTH: intl.get(`${PREFIX}.view.cron.lastTheMonth`).d('本月最后一个'),
  MONDAY: intl.get(`${PREFIX}.view.cron.monday`).d('星期一'),
  TUESDAY: intl.get(`${PREFIX}.view.cron.tuesday`).d('星期二'),
  WEDNESDAY: intl.get(`${PREFIX}.view.cron.wednesday`).d('星期三'),
  THURSDAY: intl.get(`${PREFIX}.view.cron.thursday`).d('星期四'),
  FRIDAY: intl.get(`${PREFIX}.view.cron.friday`).d('星期五'),
  SATURDAY: intl.get(`${PREFIX}.view.cron.saturday`).d('星期六'),
  SUNDAY: intl.get(`${PREFIX}.view.cron.sunday`).d('星期日'),

  EVERY_YEAR: intl.get(`${PREFIX}.view.cron.everyWeek`).d('每年'),
  SINGLE_YEAR: intl.get(`${PREFIX}.view.cron.singleYear`).d('年'),
};
