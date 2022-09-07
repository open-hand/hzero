/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/25
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 项目管理DS
 */
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import { API_HOST, HZERO_HIOT } from 'utils/config';

const orgId = getCurrentOrganizationId();
const prefix = 'hiot.thingVolumes';

const projectManageDS = () => ({
  autoQuery: false,
  selection: false,
  transport: {
    read() {
      return {
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/thing-groups`,
        method: 'get',
      };
    },
  },
  queryFields: [
    {
      name: 'name',
      type: 'string',
      label: intl.get(`${prefix}.project.name`).d('设备分组名称'),
    },
    {
      name: 'code',
      type: 'string',
      label: intl.get(`${prefix}.project.code`).d('设备分组编码'),
    },
  ],
  fields: [
    {
      name: 'name',
      type: 'intl',
      label: intl.get(`${prefix}.project.name`).d('设备分组名称'),
    },
    {
      name: 'code',
      type: 'string',
      label: intl.get(`${prefix}.project.code`).d('设备分组编码'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hzero.common.status').d('状态'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'subGroupNum',
      type: 'number',
      label: intl.get(`${prefix}.project.subGroupNum`).d('子设备分组个数'),
    },
  ],
});

const detailDS = () => ({
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'name',
      type: 'intl',
      required: true,
      label: intl.get(`${prefix}.project.name`).d('设备分组名称'),
      maxLength: 30,
    },
    {
      name: 'code',
      type: 'string',
      required: true,
      unique: true,
      label: intl.get(`${prefix}.project.code`).d('设备分组编码'),
      maxLength: 30,
    },
    {
      name: 'levelPath',
      type: 'string',
      label: intl.get(`${prefix}.levelPath`).d('层级路径'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hzero.common.status').d('状态'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'description',
      label: intl.get('hzero.common.explain').d('说明'),
      type: 'string',
      maxLength: 200,
    },
    {
      name: 'parentId',
      type: 'string',
    },
    {
      name: 'parentName',
      type: 'string',
      label: intl.get(`hiot.common.model.device.parentDeviceName`).d('所属设备分组'),
    },
  ],
  transport: {
    read({ dataSet }) {
      const { projectId } = dataSet;
      return {
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/thing-groups/${projectId}`,
        method: 'get',
      };
    },
    create({ data }) {
      const subData = data.map(({ editing, ...item }) => item);
      return {
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/thing-groups`,
        method: 'post',
        data: {
          ...subData[0],
        },
      };
    },
    update({ data, dataSet }) {
      const {
        queryParameter: { enabledFlag },
      } = dataSet;
      return {
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/thing-groups`,
        method: 'put',
        data: {
          ...data[0],
          enabledFlag: enabledFlag || data[0].enabledFlag,
        },
      };
    },
  },
});

export { projectManageDS, detailDS };
