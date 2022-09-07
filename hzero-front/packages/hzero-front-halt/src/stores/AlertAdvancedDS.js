/*
 * @Descripttion: 告警高级配置
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-03-24 14:13:38
 * @Copyright: Copyright (c) 2020, Hand
 */
import { DataSet } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { getCurrentOrganizationId, getCurrentUser } from 'utils/utils';
import { HZERO_ALT } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const { tenantId } = getCurrentUser();
const apiPrefix = `${HZERO_ALT}/v1/${organizationId}`;

// 路由配置dataSet
const RouterConfigDS = () => ({
  autoQuery: true,
  selection: false,
  transport: {
    read: ({ data, params }) => {
      if (data.alertRouteId) {
        return {
          url: `${apiPrefix}/alert-routes/${data.alertRouteId}`,
          params: { ...params, ...data },
          method: 'get',
        };
      } else {
        return {
          url: `${apiPrefix}/alert-routes`,
          params: { ...params, ...data },
          method: 'get',
        };
      }
    },
    submit: ({ data }) => {
      return {
        url: `${apiPrefix}/alert-routes`,
        data: data[0],
        method: 'post',
      };
    },
    destroy: ({ data }) => {
      return {
        url: `${apiPrefix}/alert-routes`,
        data: { ...data[0], alertMatchList: [] },
        method: 'delete',
      };
    },
  },
  primaryKey: 'alertRouteId',
  idField: 'alertRouteId',
  parentField: 'parentId',
  fields: [
    {
      name: 'tenantId',
      defaultValue: tenantId,
    },
    {
      name: 'alertRouteId',
    },
    { name: 'parentId' },
    {
      name: 'alertRouteCode',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.alertRouteCode').d('路由代码'),
      required: true,
      type: 'string',
      validator: (value) => {
        const pattern = /^[A-Za-z0-9][A-Za-z0-9-_]*$/;
        if (!pattern.test(value)) {
          return intl
            .get('halt.alertAdvanced.validation.message.code.warning')
            .d('请输入字母及数字，只能以字母或数字开头，可包含“-”、“_”');
        }
      },
    },
    {
      name: 'continueFlag',
      label: intl
        .get('halt.alertAdvanced.model.alertAdvanced.continueFlag')
        .d('命中后是否继续匹配'),
      type: 'boolean',
      labelWidth: 150,
      required: true,
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
    {
      name: 'sendConfig',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.sendConfig').d('发送配置'),
      type: 'object',
      lovCode: 'HALT.MSG_SEND_CONFIG',
      lovPara: { tenantId },
      required: true,
      ignore: 'always',
    },
    {
      name: 'sendConfigId',
      type: 'number',
      bind: 'sendConfig.sendConfigId',
    },
    {
      name: 'sendConfigName',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.sendConfig').d('发送配置'),
      type: 'string',
      bind: 'sendConfig.messageName',
      ignore: 'always',
    },
    {
      name: 'receiverGroup',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.receiverGroup').d('接收组'),
      type: 'object',
      lovCode: 'HALT.MSG_RECEIVER_GROUP',
      lovPara: { tenantId },
      required: true,
      ignore: 'always',
    },
    {
      name: 'receiverGroupId',
      type: 'number',
      bind: 'receiverGroup.receiverGroupId',
    },
    {
      name: 'receiverGroupName',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.receiverGroup').d('接收组'),
      type: 'string',
      bind: 'receiverGroup.typeName',
      ignore: 'always',
    },
    {
      name: 'remark',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.remark').d('路由说明'),
      maxLength: 255,
      type: 'string',
    },
    {
      name: 'groupWait',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.groupWait').d('等待分组间隔'),
      type: 'number',
      step: 1,
      min: 0,
      defaultValue: 30,
    },
    {
      name: 'groupInterval',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.groupInterval').d('分组发送间隔'),
      type: 'number',
      step: 1,
      min: 0,
      defaultValue: 5,
    },
    {
      name: 'repeatInterval',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.repeatInterval').d('重复发送间隔'),
      type: 'number',
      step: 1,
      min: 0,
      defaultValue: 4,
    },
    {
      name: 'groupBy',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.groupBy').d('分组'),
      type: 'string',
      multiple: ',',
      validator: (value) => {
        const pattern = /^[A-Za-z0-9][A-Za-z0-9-_.]*$/;
        if (!pattern.test(value)) {
          return intl
            .get('halt.alertAdvanced.validation.message.groupBy.warning')
            .d('请输入字母及数字，只能以字母或数字开头，可包含“-”、“_”、“.”');
        }
      },
    },
  ],
});

// 抑制规则配置dataSet
const InhibitionRulesDS = () => ({
  autoQuery: true,
  pageSize: 10,
  selection: 'multiple',
  transport: {
    read: ({ data, params }) => {
      if (data.alertInhibitId) {
        return {
          url: `${apiPrefix}/alert-inhibits/${data.alertInhibitId}`,
          params: { ...data, ...params },
          method: 'get',
        };
      } else {
        return {
          url: `${apiPrefix}/alert-inhibits`,
          params: { ...data, ...params },
          method: 'get',
        };
      }
    },
    create: ({ data }) => {
      return {
        url: `${apiPrefix}/alert-inhibits`,
        data: { ...data[0] },
        method: 'POST',
      };
    },
    update: ({ data }) => {
      return {
        url: `${apiPrefix}/alert-inhibits`,
        data: { ...data[0] },
        method: 'POST',
      };
    },
    destroy: ({ data }) => {
      return {
        url: `${apiPrefix}/alert-inhibits`,
        data: data.map((e) => ({
          ...e,
          sourceAlertMatchList: [],
          targetAlertMatchList: [],
        })),
        method: 'DELETE',
      };
    },
  },
  fields: [
    {
      name: 'alertInhibitCode',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.rule.code').d('规则编码'),
      type: 'string',
      required: true,
      validator: (value) => {
        const pattern = /^[A-Za-z0-9][A-Za-z0-9-_./]*$/;
        if (!pattern.test(value)) {
          return intl
            .get('halt.alertAdvanced.validation.message.alertInCode.warning')
            .d('请输入字母及数字，只能以字母或数字开头，可包含“-”、“_”、“.”、“/”');
        }
      },
    },
    {
      name: 'alertInhibitName',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.rule.name').d('规则名称'),
      type: 'string',
      required: true,
    },
    {
      name: 'remark',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.remark').d('路由说明'),
      type: 'string',
    },
    {
      name: 'labelMatch',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.labelMatch').d('标签名称匹配'),
      required: true,
      type: 'string',
      multiple: ',',
      validator: (value) => {
        const pattern = /^[a-zA-Z_][a-zA-Z0-9_]*$/;
        if (!pattern.test(value)) {
          return intl
            .get('halt.alertAdvanced.validation.message.labelMatch.warning')
            .d('请输入字母、数字或“_”，只能以字母或“_”开头');
        }
      },
    },
  ],
  queryFields: [
    {
      name: 'alertInhibitCode',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.rule.code').d('规则编码'),
      type: 'string',
    },
    {
      name: 'alertInhibitName',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.rule.name').d('规则名称'),
      type: 'string',
    },
  ],
});

// 静默规则配置dataSet
const SilentRulesDS = () => ({
  autoQuery: true,
  pageSize: 10,
  selection: 'multiple',
  transport: {
    read: ({ data, params }) => {
      if (data.alertSilenceId) {
        return {
          url: `${apiPrefix}/alert-silences/${data.alertSilenceId}`,
          params: { ...data, ...params },
          method: 'get',
        };
      } else {
        return {
          url: `${apiPrefix}/alert-silences`,
          params: { ...data, ...params },
          method: 'get',
        };
      }
    },
    create: ({ data }) => {
      return {
        url: `${apiPrefix}/alert-silences`,
        data: data[0],
        method: 'POST',
      };
    },
    update: ({ data }) => {
      return {
        url: `${apiPrefix}/alert-silences`,
        data: { ...data[0] },
        method: 'POST',
      };
    },
    destroy: ({ data }) => {
      return {
        url: `${apiPrefix}/alert-silences`,
        data: data.map((e) => ({ ...e, targetAlertMatchList: [] })),
        method: 'DELETE',
      };
    },
  },
  fields: [
    {
      name: 'alertSilenceCode',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.rule.code').d('规则编码'),
      type: 'string',
      required: true,
    },
    {
      name: 'alertSilenceName',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.rule.name').d('规则名称'),
      type: 'string',
      required: true,
    },
    {
      name: 'remark',
      label: intl.get('hzero.common.explain').d('说明'),
      type: 'string',
      required: true,
    },
    {
      name: 'startTime',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.startTime').d('开始时间'),
      required: true,
      type: 'time',
    },
    {
      name: 'endTime',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.endTime').d('结束时间'),
      required: true,
      dynamicProps: {
        min: ({ record }) => record.get('startTime'),
      },
      type: 'time',
    },
  ],
  queryFields: [
    {
      name: 'alertSilenceCode',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.rule.code').d('规则编码'),
      type: 'string',
    },
    {
      name: 'alertSilenceName',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.rule.name').d('规则名称'),
      type: 'string',
    },
  ],
});

// 规则匹配
const matchRuleDS = () => ({
  paging: false,
  autoCreate: true,
  selection: 'multiple',
  fields: [
    {
      name: 'matchCode',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.tagName').d('标签名称'),
      required: true,
      type: 'string',
      validator: (value) => {
        const pattern = /^[a-zA-Z_][a-zA-Z0-9_]*$/;
        if (!pattern.test(value)) {
          return intl
            .get('halt.alertAdvanced.validation.message.labelMatch.warning')
            .d('请输入字母、数字或“_”，只能以字母或“_”开头');
        }
      },
    },
    {
      name: 'matchValue',
      required: true,
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.matchValue').d('标签值'),
      type: 'string',
    },
    {
      name: 'matchRegexFlag',
      label: intl.get('halt.alertAdvanced.model.alertAdvanced.matchRegexFlag').d('是否正则匹配'),
      defaultValue: 1,
      options: new DataSet({
        selection: 'single',
        data: [
          { value: 1, meaning: intl.get('hzero.common.status.yes').d('是') },
          { value: 0, meaning: intl.get('hzero.common.status.no').d('否') },
        ],
      }),
    },
  ],
});
export { InhibitionRulesDS, SilentRulesDS, matchRuleDS, RouterConfigDS };
