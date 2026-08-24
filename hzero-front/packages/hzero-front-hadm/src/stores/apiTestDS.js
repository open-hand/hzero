/**
 * @since 2019-12-03
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import { HZERO_ADM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();

const treeDS = {
  autoQuery: false,
  pageSize: -1,
  selection: 'single',
  idField: 'key',
  expandField: 'expand',
  parentField: 'parentKey',

  transport: {
    read: () => {
      return {
        url: isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/swagger/tree`
          : `${HZERO_ADM}/v1/swagger/tree`,
        method: 'get',
        transformResponse: (res) => {
          let parsedData = {};
          try {
            parsedData = JSON.parse(res);
          } catch (e) {
            // do nothing, use default error deal
          }
          return parsedData.service;
        },
      };
    },
  },
};

const detailDS = {
  autoCreate: false,
  autoQuery: false,
  autoQueryAfterSubmit: false,
  paging: false,
  fields: [],
  transport: {
    read: ({ dataSet, params }) => {
      const { refController, servicePrefix } = dataSet;
      return {
        url: isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/swagger/${servicePrefix}/controllers/${refController}/paths`
          : `${HZERO_ADM}/v1/swagger/${servicePrefix}/controllers/${refController}/paths`,
        method: 'GET',
        params,
      };
    },
  },
};

const detailParamDS = () => {
  return {
    autoCreate: false,
    autoQuery: false,
    autoQueryAfterSubmit: false,
    paging: false,
    selection: false,
    fields: [
      {
        name: 'name',
        type: 'string',
        label: intl.get('hadm.apiTest.model.apiTest.name').d('参数名称'),
      },
      {
        name: 'inDefault',
        // type: 'string',
        label: intl.get('hadm.apiTest.model.apiTest.request.data').d('请求数据'),
        dynamicProps: {
          required: ({ record }) => {
            const required = record.get('type') === 'file' ? false : record.get('required');
            return record.get('type') ? required : true;
          },
          lookupCode: ({ record }) => {
            return record.get('type') === 'boolean' ? 'HPFM.FLAG' : null;
          },
        },
      },
      {
        name: 'type',
        type: 'string',
        label: intl.get('hadm.apiTest.model.apiTest.request.data.type').d('请求数据类型'),
      },
      {
        name: 'description',
        type: 'string',
        label: intl.get('hadm.apiTest.model.apiTest.param.desc').d('参数描述'),
      },
      {
        name: 'in',
        type: 'string',
        label: intl.get('hadm.apiTest.model.apiTest.param.type').d('参数类型'),
      },
    ],
  };
};

export { treeDS, detailDS, detailParamDS };
