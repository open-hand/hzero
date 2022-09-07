/**
 *
 * @date: 2020-03-24
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import intl from 'utils/intl';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { CODE } from 'utils/regExp';

const organizationId = getCurrentOrganizationId();
const apiPrefix = isTenantRoleLevel()
  ? `${HZERO_PLATFORM}/v1/${organizationId}`
  : `${HZERO_PLATFORM}/v1`;

const initDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'ganttCode',
      type: 'string',
      label: intl.get('hgat.ganttConfig.model.ganttConfig.ganttCode').d('甘特图编码'),
    },
    {
      name: 'ganttName',
      type: 'string',
      label: intl.get('hgat.ganttConfig.model.ganttConfig.ganttName').d('甘特图名称'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hgat.ganttConfig.model.ganttConfig.remark').d('备注说明'),
    },
  ],
  fields: [
    {
      name: 'ganttCode',
      type: 'string',
      label: intl.get('hgat.ganttConfig.model.ganttConfig.ganttCode').d('甘特图编码'),
    },
    {
      name: 'ganttName',
      type: 'string',
      label: intl.get('hgat.ganttConfig.model.ganttConfig.ganttName').d('甘特图名称'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hgat.ganttConfig.model.ganttConfig.enabledFlag').d('状态'),
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hgat.ganttConfig.model.ganttConfig.remark').d('备注说明'),
    },
    {
      name: 'action',
      label: intl.get('hzero.common.button.action').d('操作'),
    },
  ],
  transport: {
    read: ({ params, data }) => ({
      url: `${apiPrefix}/gantts`,
      method: 'GET',
      params: { ...params, ...data },
    }),
  },
});

// 模态框ds
const drawerDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'ganttCode',
      type: 'string',
      pattern: CODE,
      required: true,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.code')
          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
      label: intl.get('hgat.ganttConfig.model.ganttConfig.ganttCode').d('甘特图编码'),
    },
    {
      name: 'ganttName',
      required: true,
      type: 'intl',
      label: intl.get('hgat.ganttConfig.model.ganttConfig.ganttName').d('甘特图名称'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hgat.ganttConfig.model.ganttConfig.enabledFlag').d('状态'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hgat.ganttConfig.model.ganttConfig.remark').d('备注说明'),
    },
  ],
  transport: {
    read: ({ data }) => {
      const { id } = data;
      return {
        url: `${apiPrefix}/gantts/${id}`,
        method: 'GET',
        data: {},
        params: {},
      };
    },
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/gantts`,
        method: 'POST',
        data: { ...other, tenantId: organizationId },
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/gantts`,
        method: 'PUT',
        data: other,
      };
    },
  },
});

const detailFormDS = () => ({
  fields: [
    {
      name: 'DOUBLE_MODE',
      type: 'string',
      trueValue: 'true',
      falseValue: 'false',
      defaultValue: 'true',
      label: intl.get('hgat.ganttConfig.model.searchConfig.editMode').d('双击模式'),
    },
    {
      name: 'TIME_DISPLAY_MODE',
      type: 'string',
      lookupCode: 'HPFM.GANTT.TIME_DISPLAY_MODE',
      defaultValue: 'day',
      label: intl.get('hgat.ganttConfig.model.inquiryConfig.timeDisplayMode').d('时间显示模式'),
    },
    {
      name: 'GANTT_LAYOUT',
      type: 'string',
      lookupCode: 'HPFM.GANTT.LAYOUT',
      defaultValue: 'right_and_left',
      label: intl.get('hgat.ganttConfig.model.searchConfig.ganttLayout').d('甘特图布局'),
    },
    {
      name: 'READONLY',
      type: 'string',
      trueValue: 'true',
      falseValue: 'false',
      defaultValue: 'false',
      label: intl.get('hgat.ganttConfig.model.searchConfig.readonly').d('只读'),
    },
    // {
    //   name: 'SKIN_STYLE',
    //   type: 'string',
    //   lookupCode: 'HPFM.GANTT.SKIN_STYLE',
    //   defaultValue: 'teracce',
    //   label: intl.get('hgat.ganttConfig.model.searchConfig.skinStyle').d('皮肤'),
    // },
    {
      name: 'EXPEND_TREE',
      type: 'string',
      trueValue: 'true',
      falseValue: 'false',
      defaultValue: 'true',
      label: intl.get('hgat.ganttConfig.model.searchConfig.expendTree').d('展开树'),
    },
    // {
    //   name: 'CREATE_TASK_START_DATE',
    //   type: 'date',
    //   format: 'YYYY-MM-DD',
    //   label: intl.get('hgat.ganttConfig.model.searchConfig.startDate').d('开始日期'),
    // },
    // {
    //   name: 'CREATE_TASK_END_DATE',
    //   type: 'date',
    //   format: 'YYYY-MM-DD',
    //   label: intl.get('hgat.ganttConfig.model.searchConfig.endDate').d('结束日期'),
    // },
    {
      name: 'FIT_TASKS',
      type: 'string',
      trueValue: 'true',
      falseValue: 'false',
      defaultValue: 'true',
      label: intl.get('hgat.ganttConfig.model.searchConfig.fitTasks').d('开启任务自动延时'),
    },
    {
      name: 'ENABLE_AUTO_SCROLL',
      type: 'string',
      trueValue: 'true',
      falseValue: 'false',
      defaultValue: 'true',
      label: intl.get('hgat.ganttConfig.model.searchConfig.autoScroll').d('开启任务居中展示'),
    },
    {
      name: 'ROUND_DND_DATES',
      type: 'string',
      trueValue: 'true',
      falseValue: 'false',
      defaultValue: 'true',
      label: intl.get('hgat.ganttConfig.model.searchConfig.roundDNDDates').d('日期四舍五入'),
    },
    {
      name: 'SHOW_TASKS_OUTSIDE_TIMESCALE',
      type: 'string',
      trueValue: 'true',
      falseValue: 'false',
      defaultValue: 'false',
      label: intl.get('hgat.ganttConfig.model.searchConfig.showTaskOT').d('显示超时任务'),
    },
  ],
  transport: {
    read: (config) => {
      const {
        data: { id },
      } = config;
      return {
        url: `${apiPrefix}/gantt-configs/${id}`,
        method: 'GET',
        data: {},
        params: {},
      };
    },
  },
});

export { initDS, drawerDS, detailFormDS };
