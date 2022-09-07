import intl from 'hzero-front/lib/utils/intl';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { dateRender } from 'utils/renderer';
import { CODE_UPPER } from 'utils/regExp';
import {
  CHARGE_TYPE,
  CHARGE_SET_STATUS,
  CHARGE_SET_STATUS_FIELDS,
  CHARGE_SERVER,
  CHARGE_INTERFACE,
  CHARGE_TYPE_FIELDS,
} from '@/constants/CodeConstants';
import DataSet from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 接口计费设置头表 DataSet
 */

export default () => ({
  primaryKey: 'setHeaderId',
  autoQuery: false,
  pageSize: 10,
  name: DataSet.ChargeSetHeaderDS,
  selection: 'multiple',
  fields: [
    {
      name: 'setHeaderId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'setCode',
      type: 'string',
      required: true,
      pattern: CODE_UPPER,
      format: 'uppercase',
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.codeUpper')
          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.setCode').d('计费代码'),
    },
    {
      name: 'setName',
      type: 'string',
      required: true,
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.setName').d('计费名称'),
    },
    {
      name: 'typeCode',
      type: 'string',
      required: true,
      lookupCode: CHARGE_TYPE,
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.typeCode').d('计费类型'),
    },
    {
      name: 'statusCode',
      type: 'string',
      defaultValue: CHARGE_SET_STATUS_FIELDS.NEW,
      required: true,
      lookupCode: CHARGE_SET_STATUS,
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.statusCode').d('状态'),
    },
    {
      name: 'serverObject',
      type: 'object',
      required: true,
      lovCode: CHARGE_SERVER,
      textField: 'serverCode',
      valueField: 'interfaceServerId',
      ignore: 'always',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.serverCode').d('服务代码'),
    },
    {
      name: 'interfaceServerId',
      type: 'string',
      required: true,
      bind: 'serverObject.interfaceServerId',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.interfaceServerId').d('服务ID'),
    },
    {
      name: 'serverCode',
      type: 'string',
      bind: 'serverObject.serverCode',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.serverCode').d('服务代码'),
    },
    {
      name: 'serverName',
      type: 'string',
      bind: 'serverObject.serverName',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.serverName').d('服务名称'),
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
        if (CHARGE_TYPE_FIELDS.INTERFACE === record.get('typeCode')) {
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
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.interfaceCode').d('接口代码'),
    },
    {
      name: 'interfaceId',
      type: 'string',
      required: true,
      bind: 'interfaceObject.interfaceId',
      dynamicProps: ({ record }) => {
        let required = true;
        // 计费类型下拉框 控制interfaceId(typeCode='INTERFACE'，必输)
        if (CHARGE_TYPE_FIELDS.INTERFACE === record.get('typeCode')) {
          required = true;
        } else {
          required = false;
        }
        return {
          required,
        };
      },
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.interfaceId').d('接口ID'),
    },
    {
      name: 'interfaceCode',
      type: 'string',
      bind: 'interfaceObject.interfaceCode',
      dynamicProps: ({ record }) => {
        let required = true;
        // 计费类型下拉框 控制interfaceCode(typeCode='INTERFACE'，必输)
        if (CHARGE_TYPE_FIELDS.INTERFACE === record.get('typeCode')) {
          required = true;
        } else {
          required = false;
        }
        return {
          required,
        };
      },
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.interfaceCode').d('接口代码'),
    },
    {
      name: 'interfaceName',
      type: 'string',
      bind: 'interfaceObject.interfaceName',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.interfaceName').d('接口名称'),
    },
    {
      name: 'startDate',
      type: 'date',
      format: 'YYYY-MM-DD',
      transformRequest: (value) => dateRender(value),
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.startDate').d('生效日期'),
    },
    {
      name: 'tenantId',
      type: 'number',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.tenantId').d('租户ID'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.remark').d('备注'),
    },
  ],
  queryFields: [
    {
      name: 'serverCode',
      type: 'string',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.serverCode').d('服务代码'),
    },
    {
      name: 'interfaceCode',
      type: 'string',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.interfaceCode').d('接口代码'),
    },
    {
      name: 'typeCode',
      type: 'string',
      lookupCode: CHARGE_TYPE,
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.typeCode').d('计费类型'),
    },
    // {
    //   name: 'statusCode',
    //   type: 'string',
    //   lookupCode: CHARGE_SET_STATUS,
    //   label: intl.get('hitf.chargeSet.model.chargeSetHeader.statusCode').d('状态'),
    // },
    {
      name: 'serverName',
      type: 'string',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.serverName').d('服务名称'),
    },
    {
      name: 'interfaceName',
      type: 'string',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.interfaceName').d('接口名称'),
    },
    {
      name: 'startDateFrom',
      type: 'date',
      max: 'startDateTo',
      format: 'YYYY-MM-DD',
      transformRequest: (value) => dateRender(value),
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.startDateFrom').d('生效日期从'),
    },
    {
      name: 'startDateTo',
      type: 'date',
      min: 'startDateFrom',
      format: 'YYYY-MM-DD',
      transformRequest: (value) => dateRender(value),
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.startDateTo').d('生效日期至'),
    },
  ],
  transport: {
    read: function read({ data, params }) {
      const url = isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/charge-set-headers`
        : `${HZERO_HITF}/v1/charge-set-headers`;
      return {
        data,
        params,
        url: getUrl(url, data.setHeaderId),
        method: 'GET',
      };
    },
    submit: ({ data, params }) => {
      const _data = data.map((item) => {
        let _chargeSetLineList = [];
        if (item.chargeSetLineList) {
          _chargeSetLineList = item.chargeSetLineList.map((line) => ({
            ...line,
            setHeaderId: item.setHeaderId ? item.setHeaderId : 0,
            tenantId: line.tenantId ? line.tenantId : organizationId,
          }));
        }
        return {
          ...item,
          chargeSetLineList: _chargeSetLineList,
          tenantId: item.tenantId ? item.tenantId : organizationId,
        };
      });
      return {
        url: isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/charge-set-headers`
          : `${HZERO_HITF}/v1/charge-set-headers`,
        data: _data[0],
        params,
        method: 'POST',
      };
    },
    update: ({ data, params }) => {
      const _data = data.map((item) => {
        let _chargeSetLineList = [];
        if (item.chargeSetLineList) {
          _chargeSetLineList = item.chargeSetLineList.map((line) => ({
            ...line,
            setHeaderId: item.setHeaderId,
            tenantId: line.tenantId ? line.tenantId : organizationId,
          }));
        }
        return {
          ...item,
          chargeSetLineList: _chargeSetLineList,
          tenantId: item.tenantId ? item.tenantId : organizationId,
        };
      });
      return {
        url: isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/charge-set-headers`
          : `${HZERO_HITF}/v1/charge-set-headers`,
        data: _data[0],
        params,
        method: 'PUT',
      };
    },
    destroy: ({ data, params }) => ({
      url: isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/charge-set-headers`
        : `${HZERO_HITF}/v1/charge-set-headers`,
      data: {
        setHeaderId: data[0].setHeaderId,
        _token: data[0]._token,
      },
      params,
      method: 'DELETE',
    }),
  },
  feedback: {
    loadSuccess: (res) => {
      if (res) {
        if (!res.content) {
          // 明细页面 设置服务代码Lov/接口代码Lov的值
          res.serverObject = {
            serverCode: res.serverCode,
            interfaceServerId: res.interfaceServerId,
            serverName: res.serverName,
          };
          res.interfaceObject = {
            interfaceCode: res.interfaceCode,
            interfaceId: res.interfaceId,
            interfaceName: res.interfaceName,
          };
        }
      }
    },
  },
});

function getUrl(url, id) {
  return id ? `${url}/${id}` : `${url}`;
}
