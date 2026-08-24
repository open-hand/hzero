/**
 * model 平台级日历定义
 * @date: 2018-7-10
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  searchCalendar,
  addCalendar,
  updateCalendar,
  searchHolidays,
  searchWeekdays,
  searchCalendarDetail,
  updateWeekday,
  addHoliday,
  deleteHoliday,
  updateHoliday,
} from '../services/calendarService';

export default {
  namespace: 'calendar',
  state: {
    calendarList: [], // 日历数据列表
    pagination: {}, // 分页器
    calendarDetail: {},
    holidays: {}, // 公共假期
    weekdays: [], // 工作日
    holidayType: [], // 公休假期类型
  },
  effects: {
    *searchHolidayType(_, { call, put }) {
      const result = yield call(queryIdpValue, 'HPFM.HOLIDAY_TYPE');
      const data = getResponse(result);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            holidayType: result,
          },
        });
      }
    },
    // 获取日历列表
    *searchCalendar({ payload }, { call, put }) {
      const result = getResponse(yield call(searchCalendar, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            pagination: createPagination(result),
            calendarList: result.content,
          },
        });
      }
    },
    // 获取日历详情
    *searchCalendarDetail({ payload }, { call, put }) {
      const result = getResponse(yield call(searchCalendarDetail, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            calendarDetail: result,
          },
        });
      }
    },
    // 获取公共假期信息
    *searchHolidays({ payload }, { call, put }) {
      const result = getResponse(yield call(searchHolidays, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            holidays: result,
          },
        });
      }
    },
    // 获取工作日信息
    *searchWeekdays({ payload }, { call, put }) {
      const result = getResponse(yield call(searchWeekdays, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            weekdays: result,
          },
        });
      }
    },
    // 新增日历信息
    *addCalendar({ payload }, { call }) {
      const result = yield call(addCalendar, payload);
      return getResponse(result);
    },
    // 更新日历信息
    *updateCalendar({ payload }, { call }) {
      const result = yield call(updateCalendar, payload);
      return getResponse(result);
    },
    // 新增公休假期
    *addHoliday({ payload }, { call }) {
      const result = yield call(addHoliday, payload);
      return getResponse(result);
    },
    // 更新公休假期
    *updateHoliday({ payload }, { call }) {
      const result = yield call(updateHoliday, payload);
      return getResponse(result);
    },
    // 更新工作日
    *updateWeekday({ payload }, { call }) {
      const result = yield call(updateWeekday, payload);
      return getResponse(result);
    },
    // 批量删除公休假期
    *deleteHolidays({ payload }, { call }) {
      const result = yield call(deleteHoliday, payload);
      return getResponse(result);
    },
  },
  reducers: {
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
