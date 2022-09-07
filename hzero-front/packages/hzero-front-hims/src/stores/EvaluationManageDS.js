/*
 * EvaluateManageDS 评价管理DS
 * @date: 2020-09-17
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';
import { HZERO_IM } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = `${HZERO_IM}/v1/${organizationId}`;
// 表格ds
const tableDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'evaluationType',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.evaluationType').d('评价类别'),
      lookupCode: 'HIMS.EVALUATION_TYPE',
    },
    {
      name: 'csUserLov',
      type: 'object',
      lovCode: 'HIAM.USER.ORG',
      lovPara: { tenantId: organizationId },
      label: intl.get('hims.evaluation.model.evaluation.csUserName').d('客服名称'),
      ignore: 'always',
    },
    {
      name: 'csUserId',
      type: 'string',
      bind: 'csUserLov.id',
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.remark').d('评价内容'),
    },
    {
      name: 'score',
      type: 'number',
      max: 5,
      min: 0,
      step: 1,
      label: intl.get('hims.evaluation.model.evaluation.score').d('评价分数'),
    },
    {
      name: 'startDate',
      type: 'dateTime',
      label: intl.get('hims.evaluation.model.evaluation.startDate').d('评价时间从'),
      max: 'endDate',
    },
    {
      name: 'endDate',
      type: 'dateTime',
      label: intl.get('hims.evaluation.model.evaluation.endDate').d('评价时间至'),
      min: 'startDate',
    },
  ],
  fields: [
    {
      name: 'evaluationTypeMeaning',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.evaluationType').d('评价类别'),
    },
    {
      name: 'csUserName',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.csUserName').d('客服名称'),
    },
    {
      name: 'userName',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.userName').d('用户名称'),
    },
    {
      name: 'score',
      type: 'number',
      label: intl.get('hims.evaluation.model.evaluation.score').d('评价分数'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.remark').d('评价内容'),
    },
    {
      name: 'createAt',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.createAt').d('评价时间'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/evaluation/all`,
      method: 'GET',
    }),
  },
});

// 新建或编辑时的DS
const drawerDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'evaluationTypeMeaning',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.evaluationType').d('评价类别'),
    },
    {
      name: 'csUserName',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.csUserName').d('客服名称'),
    },
    {
      name: 'userName',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.userName').d('用户名称'),
    },
    {
      name: 'score',
      type: 'number',
      label: intl.get('hims.evaluation.model.evaluation.score').d('评价分数'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.remark').d('评价内容'),
    },
    {
      name: 'createAt',
      type: 'string',
      label: intl.get('hims.evaluation.model.evaluation.createAt').d('评价时间'),
    },
  ],
  transport: {
    read: ({ data }) => {
      const { id } = data;
      return {
        url: `${apiPrefix}/evaluation/detail/${id}`,
        method: 'GET',
        data: {},
        params: {},
      };
    },
  },
});

export { tableDS, drawerDS };
