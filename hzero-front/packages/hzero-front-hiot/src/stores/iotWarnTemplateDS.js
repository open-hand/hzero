/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019/10/18 5:39 下午
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: iot告警模版
 */
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import { API_PREFIX, CODE_UPPER_REG } from '@/utils/constants';

const organizationId = getCurrentOrganizationId();

const iotWarnTemplateDS = () => ({
  transport: {
    /**
     * 查询告警模板列表数据
     * @param config
     * @param data 查询参数(分页信息)
     * @returns {{method: string, url: string}}
     */
    read: ({ config, data }) => {
      const { messageTemplateId } = data;
      return {
        ...config,
        url: messageTemplateId
          ? `${API_PREFIX}/v1/${organizationId}/alert-models/${messageTemplateId}`
          : `${API_PREFIX}/v1/${organizationId}/alert-models`,
        method: 'get',
        data: messageTemplateId ? {} : data,
      };
    },
    /**
     * 创建新的告警模板
     * @param data
     * @returns {{data: (boolean|*), url: string}}
     */
    create: ({ data }) => ({
      url: `${API_PREFIX}/v1/${organizationId}/alert-models`,
      data: data.length > 0 && data[0],
    }),
    /**
     * 更新告警模板
     * @param config
     * @param data 修改的数据
     * @returns {{url: string}}
     */
    update: ({ data }) =>
      // const {  }
      ({
        url: `${API_PREFIX}/v1/${organizationId}/alert-models`,
        method: 'put',
        data: data.length > 0 && data[0],
      }),
    /**
     * 删除告警模版数据
     * @param data
     * @returns {{method: string, data: *, url: string}}
     */
    destroy: ({ data }) => ({
      url: `${API_PREFIX}/v1/${organizationId}/alert-models`,
      method: 'delete',
      data: data.map(({ alertModelId }) => alertModelId),
    }),
  },
  queryFields: [
    {
      name: 'alertModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'alertModelName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'alertLevel',
      type: 'single',
      lookupCode: 'HIOT.ALERT_LEVEL',
      label: intl.get('hiot.iotTemplate.model.iotTemplate.warnLevel').d('告警级别'),
    },
    {
      name: 'isReferred',
      type: 'single',
      label: intl.get('hiot.common.isReferred').d('是否被引用'),
      lookupCode: 'HIOT.REFERRED_STATUS',
    },
  ],
  fields: [
    {
      name: 'alertModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
      required: true,
      pattern: CODE_UPPER_REG,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hiot.common.view.validation.codeMsg')
          .d('全大写及数字，必须以字母、数字开头，可包含“_”'),
      },
    },
    {
      name: 'alertModelName',
      type: 'intl',
      label: intl.get('hiot.common.name').d('名称'),
      required: true,
    },
    {
      name: 'alertLevelMeaning',
      type: 'string',
      label: intl.get('hiot.iotTemplate.model.iotTemplate.warnLevel').d('告警级别'),
    },
    {
      name: 'alertLevel',
      type: 'single',
      lookupCode: 'HIOT.ALERT_LEVEL',
      label: intl.get('hiot.iotTemplate.model.iotTemplate.warnLevel').d('告警级别'),
      required: true,
    },
    {
      name: 'enabled',
      type: 'boolean',
      label: intl.get('hiot.iotTemplate.model.iotTemplate.runStatus').d('启用状态'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'messageTemplate',
      type: 'object',
      lovCode: 'HIOT.LOV.MESSAGE_TEMPLATE',
      lovPara: { tenantId: organizationId },
      label: intl.get('hiot.iotTemplate.view.title.messageTemplate').d('消息模板'),
      required: true,
      ignore: 'always',
    },
    {
      name: 'messageTemplateId',
      type: 'string',
      bind: 'messageTemplate.messageTemplateId',
    },
    {
      name: 'templateName',
      type: 'string',
      label: intl.get('hiot.iotTemplate.view.title.messageTemplate').d('消息模板'),
    },
    {
      name: 'operation',
      type: 'string',
      label: intl.get(`hzero.common.button.action`).d('操作'),
    },
    {
      name: 'dummyTime',
      type: 'number',
      label: intl.get('hiot.iotTemplate.model.iotTemplate.dumbFireTime').d('哑火时间'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hiot.iotTemplate.model.iotTemplate.ruleDescription').d('规则说明'),
      maxLength: 100,
    },
    {
      name: 'templateContent',
      type: 'string',
      label: intl.get('hiot.iotTemplate.model.iotTemplate.templateContent').d('模版内容'),
    },
    {
      name: 'receiverName',
      type: 'object',
      lovCode: 'HIOT.LOV.ALERT_RECEIVER',
      lovPara: { tenantId: organizationId },
      multiple: true,
      label: intl.get('hiot.iotTemplate.model.iotTemplate.receiver').d('接收人员'),
      ignore: 'always',
    },
    {
      name: 'isReferred',
      type: 'single',
      label: intl.get('hiot.common.isReferred').d('是否被引用'),
      lookupCode: 'HIOT.REFERRED_STATUS',
    },
  ],
  events: {
    update: ({ name, value, dataSet }) => {
      if (name === 'receiverName') {
        const idArray = value.map(({ id }) => id);
        dataSet.get(0).set('receiver', idArray.join(';'));
      }
    },
  },
});

const messageTemplateDS = () => ({
  transport: {
    /**
     * 查询告警模板详情列表数据
     * @param config 查询参数(分页信息)
     * @returns {{method: string, url: 'http://hzeronb.saas.hand-china.com${HZERO_HIOT}/v1/${getCurrentOrganizationId()}/property-models'}}
     */
    read: ({ config }) => ({
      ...config,
      url: `http://localhost:5000/iotWarn`,
      method: 'get',
    }),
  },
  fields: [
    {
      name: 'messageTemplateId',
      type: 'single',
      label: intl.get('hiot.iotTemplate.model.iotMsgTemplate.noticeTemplate').d('通知模板'),
      required: true,
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hiot.iotTemplate.model.iotMsgTemplate.ruleDescription').d('规则说明'),
      maxLength: 100,
    },
    {
      name: 'content',
      type: 'string',
      label: intl.get('hiot.iotTemplate.model.iotMsgTemplate.templateContent').d('模版内容'),
      bind: 'description',
    },
    {
      name: 'receiver',
      type: 'string',
      label: intl.get('hiot.iotTemplate.model.iotMsgTemplate.receiver').d('接收人'),
    },
  ],
});

export { iotWarnTemplateDS, messageTemplateDS };
