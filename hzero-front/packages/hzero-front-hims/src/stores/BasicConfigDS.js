import intl from 'utils/intl';
import { HZERO_IM } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = `${HZERO_IM}/v1/${organizationId}`;
const state = {
  MESSAGE_RETRACT_TIME: {
    configCode: 'MESSAGE_RETRACT_TIME',
    configValue: '0',
    tenantId: organizationId,
  },
  MAX_CS_SERVICE_USERS: {
    configCode: 'MAX_CS_SERVICE_USERS',
    configValue: '1',
    tenantId: organizationId,
  },
  OPEN_BREAK_CHECK: {
    configCode: 'OPEN_BREAK_CHECK',
    configValue: 'true',
    tenantId: organizationId,
  },
  OPEN_WECHAT_SEND: {
    configCode: 'OPEN_WECHAT_SEND',
    configValue: 'true',
    tenantId: organizationId,
  },
  OPEN_TOKEN_UNIQUE: {
    configCode: 'OPEN_TOKEN_UNIQUE',
    configValue: 'true',
    tenantId: organizationId,
  },
};

const initDS = () => ({
  autoQuery: true,
  fields: [
    {
      name: 'retractTime',
      type: 'number',
      required: true,
      label: intl.get('hims.basicConfig.model.basicConfig.retractTime').d('最大撤回消息时间间隔'),
      maxLength: 240,
    },
    {
      name: 'maxUser',
      type: 'number',
      required: true,
      label: intl.get('hims.basicConfig.model.basicConfig.maxUser').d('最大客服可接待用户数'),
      maxLength: 240,
      defaultValue: 1,
    },
    {
      name: 'avoidAudit',
      type: 'boolean',
      required: true,
      label: intl.get('hims.basicConfig.model.basicConfig.avoidAudit').d('是否开启知识库免审核'),
      trueValue: 'true',
      falseValue: 'false',
      defaultValue: 'true',
    },
    {
      name: 'WeChatPush',
      type: 'boolean',
      required: true,
      label: intl.get('hims.basicConfig.model.basicConfig.WeChatPush').d('是否开启微信消息推送'),
      trueValue: 'true',
      falseValue: 'false',
      defaultValue: 'true',
    },
    {
      name: 'tokenUnique',
      type: 'boolean',
      required: true,
      label: intl.get('hims.basicConfig.model.basicConfig.tokenUnique').d('是否开启客服连接唯一'),
      trueValue: 'true',
      falseValue: 'false',
      defaultValue: 'true',
    },
  ].filter(Boolean),
  transport: {
    read: () => ({
      url: `${apiPrefix}/hims-configs`,
      method: 'GET',
      params: {},
      transformResponse: (res) => {
        let data;
        try {
          data = JSON.parse(res);
        } catch (e) {
          return e;
        }
        const newData = {};
        if (Array.isArray(data)) {
          data.forEach((item) => {
            switch (item.configCode) {
              case 'MESSAGE_RETRACT_TIME':
                state.MESSAGE_RETRACT_TIME = item;
                newData.retractTime = Number(item.configValue);
                break;
              case 'MAX_CS_SERVICE_USERS':
                state.MAX_CS_SERVICE_USERS = item;
                newData.maxUser = Number(item.configValue);
                break;
              case 'OPEN_BREAK_CHECK':
                state.OPEN_BREAK_CHECK = item;
                newData.avoidAudit = item.configValue;
                break;
              case 'OPEN_WECHAT_SEND':
                state.OPEN_WECHAT_SEND = item;
                newData.WeChatPush = item.configValue;
                break;
              case 'OPEN_TOKEN_UNIQUE':
                state.OPEN_TOKEN_UNIQUE = item;
                newData.tokenUnique = item.configValue;
                break;
              default:
                return '';
            }
          });
        }
        return newData;
      },
    }),
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      const { avoidAudit, maxUser, WeChatPush, tokenUnique, retractTime } = other;
      const res = [
        {
          configCode: 'MESSAGE_RETRACT_TIME',
          configValue: retractTime,
          tenantId: organizationId,
        },
        {
          configCode: 'MAX_CS_SERVICE_USERS',
          configValue: maxUser,
          tenantId: organizationId,
        },
        {
          configCode: 'OPEN_BREAK_CHECK',
          configValue: avoidAudit,
          tenantId: organizationId,
        },
        {
          configCode: 'OPEN_WECHAT_SEND',
          configValue: WeChatPush,
          tenantId: organizationId,
        },
        {
          configCode: 'OPEN_TOKEN_UNIQUE',
          configValue: tokenUnique,
          tenantId: organizationId,
        },
      ];
      return {
        url: `${apiPrefix}/hims-configs`,
        method: 'PUT',
        data: res,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      const { avoidAudit, maxUser, WeChatPush, tokenUnique, retractTime } = other;
      const list = [];
      for (const prop in state) {
        if (Object.prototype.hasOwnProperty.call(state, prop)) {
          list.push(state[prop]);
        }
      }
      const newData = list.map((item) => {
        switch (item.configCode) {
          case 'MESSAGE_RETRACT_TIME':
            return {
              ...item,
              configValue: retractTime,
            };
          case 'OPEN_BREAK_CHECK':
            return {
              ...item,
              configValue: avoidAudit,
            };
          case 'MAX_CS_SERVICE_USERS':
            return {
              ...item,
              configValue: maxUser,
            };
          case 'OPEN_WECHAT_SEND':
            return {
              ...item,
              configValue: WeChatPush,
            };
          case 'OPEN_TOKEN_UNIQUE':
            return {
              ...item,
              configValue: tokenUnique,
            };
          default:
            return item;
        }
      });
      return {
        url: `${apiPrefix}/hims-configs`,
        method: 'PUT',
        data: newData,
      };
    },
  },
});

export { initDS };
