import intl from 'hzero-front/lib/utils/intl';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import {
  GROUP_SERVER_TYPE,
  CHARGE_SERVER,
  CHARGE_INTERFACE,
  GROUP_SERVER_TYPE_FIELDS,
} from '@/constants/CodeConstants';
import DataSet from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 组合计费设置行表 DataSet
 */

export default () => ({
  primaryKey: 'groupLineId',
  autoQuery: true,
  pageSize: 10,
  name: DataSet.ChargeGroupLineDS,
  selection: false,
  fields: [
    {
      name: 'groupLineId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'groupHeaderId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'typeCode',
      type: 'string',
      required: true,
      lookupCode: GROUP_SERVER_TYPE,
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.typeCode').d('类型'),
    },
    {
      name: 'serverObject',
      type: 'object',
      required: true,
      lovCode: CHARGE_SERVER,
      textField: 'serverCode',
      valueField: 'interfaceServerId',
      ignore: 'always',
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.serverCode').d('服务代码'),
    },
    {
      name: 'interfaceServerId',
      type: 'string',
      required: true,
      bind: 'serverObject.interfaceServerId',
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.interfaceServerId').d('服务ID'),
    },
    {
      name: 'serverCode',
      type: 'string',
      bind: 'serverObject.serverCode',
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.serverCode').d('服务代码'),
    },
    {
      name: 'serverName',
      type: 'string',
      bind: 'serverObject.serverName',
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.serverName').d('服务名称'),
    },
    {
      name: 'interfaceObject',
      type: 'object',
      lovCode: CHARGE_INTERFACE,
      textField: 'interfaceCode',
      valueField: 'interfaceId',
      ignore: 'always',
      dynamicProps: ({ record }) => {
        let required = true;
        let readOnly = false;
        let lovPara = {};
        // 计费类型下拉框 控制interfaceId(typeCode='INTERFACE'，必输)
        if (GROUP_SERVER_TYPE_FIELDS.INTERFACE === record.get('typeCode')) {
          required = true;
          readOnly = false;
        } else {
          required = false;
          readOnly = true;
        }
        // 服务代码Lov选择框 控制interfaceCode
        if (record.get('serverCode')) {
          lovPara = {
            serverCode: record.get('serverCode'),
          };
        }
        return {
          required,
          readOnly,
          lovPara,
        };
      },
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.interfaceCode').d('接口代码'),
    },
    {
      name: 'interfaceId',
      type: 'string',
      required: true,
      bind: 'interfaceObject.interfaceId',
      dynamicProps: ({ record }) => {
        let required = true;
        // 计费类型下拉框 控制interfaceId(typeCode='INTERFACE'，必输)
        if (GROUP_SERVER_TYPE_FIELDS.INTERFACE === record.get('typeCode')) {
          required = true;
        } else {
          required = false;
        }
        return {
          required,
        };
      },
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.interfaceId').d('接口ID'),
    },
    {
      name: 'interfaceCode',
      type: 'string',
      bind: 'interfaceObject.interfaceCode',
      dynamicProps: ({ record }) => {
        let required = true;
        // 计费类型下拉框 控制interfaceCode(typeCode='INTERFACE'，必输)
        if (GROUP_SERVER_TYPE_FIELDS.INTERFACE === record.get('typeCode')) {
          required = true;
        } else {
          required = false;
        }
        return {
          required,
        };
      },
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.interfaceCode').d('接口代码'),
    },
    {
      name: 'interfaceName',
      type: 'string',
      bind: 'interfaceObject.interfaceName',
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.interfaceName').d('接口名称'),
    },
    {
      name: 'tenantId',
      type: 'number',
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.tenantId').d('租户ID'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hitf.chargeGroup.model.chargeGroupLine.remark').d('备注'),
    },
  ],
  queryFields: [],
  transport: {
    read: function read({ data }) {
      const { groupHeaderId } = data;
      const url = isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers/${groupHeaderId}/line`
        : `${HZERO_HITF}/v1/charge-group-headers/${groupHeaderId}/line`;
      return {
        data: null,
        params: null,
        url,
        method: 'GET',
      };
    },
    destroy: ({ data, params }) => ({
      url: isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers/line`
        : `${HZERO_HITF}/v1/charge-group-headers/line`,
      data: {
        groupLineId: data[0].groupLineId,
        _token: data[0]._token,
      },
      params,
      method: 'DELETE',
    }),
  },
  feedback: {
    loadSuccess: (res) => {
      if (res) {
        res.content.map((item) => {
          // 设置服务代码Lov/接口代码Lov的值
          let _item = item;
          _item = {
            ...item,
            serverObject: {
              serverCode: item.serverCode,
              interfaceServerId: item.interfaceServerId,
              serverName: item.serverName,
            },
            interfaceObject: {
              interfaceCode: item.interfaceCode,
              interfaceId: item.interfaceId,
              interfaceName: item.interfaceName,
            },
          };
          return _item;
        });
      }
    },
  },
});
