import intl from 'utils/intl';
import { HZERO_FILE } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_FILE}/v1/${organizationId}` : `${HZERO_FILE}/v1`;

// 表格ds
const tableDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    !isTenant && {
      name: 'tenantLov',
      lovCode: 'HPFM.TENANT',
      type: 'object',
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
      ignore: 'always',
      noCache: true,
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'watermarkCode',
      type: 'object',
      label: intl.get('hfile.waterMark.model.waterMark.watermarkCode').d('编码'),
    },
    {
      name: 'description',
      label: intl.get('hfile.waterMark.model.waterMark.description').d('描述'),
      type: 'string',
    },
    {
      name: 'watermarkType',
      type: 'string',
      label: intl.get('hfile.waterMark.model.waterMark.watermarkType').d('水印类型'),
      lookupCode: 'HFLE.WATERMARK_TYPE',
    },
  ].filter(Boolean),
  fields: [
    !isTenant && {
      name: 'tenantId',
      label: intl.get('hfile.waterMark.model.waterMark.tenantId').d('租户'),
      type: 'string',
    },
    {
      name: 'watermarkCode',
      type: 'object',
      label: intl.get('hfile.waterMark.model.waterMark.watermarkCode').d('编码'),
    },
    {
      name: 'description',
      label: intl.get('hfile.waterMark.model.waterMark.description').d('描述'),
      type: 'string',
    },
    {
      name: 'watermarkTypeMeaning',
      type: 'string',
      label: intl.get('hfile.waterMark.model.waterMark.watermarkType').d('水印类型'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      label: intl.get('hfile.waterMark.model.waterMark.enabledFlag').d('启用标识'),
    },
  ].filter(Boolean),
  transport: {
    read: () => ({
      url: `${apiPrefix}/watermark-configs`,
      method: 'GET',
    }),
  },
});

const drawerDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [
    !isTenant && {
      name: 'tenantLov',
      lovCode: 'HPFM.TENANT',
      required: true,
      type: 'object',
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
      ignore: 'always',
      noCache: true,
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    !isTenant && {
      name: 'tenantName',
      type: 'string',
      bind: 'tenantLov.tenantName',
    },
    {
      name: 'watermarkCode',
      type: 'string',
      required: true,
      pattern: CODE_UPPER,
      label: intl.get('hfile.waterMark.model.waterMark.code').d('编码'),
      maxLength: 30,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.codeUpper')
          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hfile.waterMark.model.waterMark.description').d('描述'),
      maxLength: 240,
    },
    {
      name: 'watermarkType',
      type: 'string',
      required: true,
      lookupCode: 'HFLE.WATERMARK_TYPE',
      label: intl.get('hfile.waterMark.model.waterMark.watermarkType').d('水印类型'),
      defaultValue: 'TEXT',
    },
    {
      name: 'fillOpacity',
      type: 'number',
      required: true,
      label: intl.get('hfile.waterMark.model.waterMark.fillOpacity').d('透明度'),
      defaultValue: 0.5,
    },
    {
      name: 'color',
      type: 'string',
      label: intl.get('hfile.waterMark.model.waterMark.color').d('色彩'),
      defaultValue: '#ff0000',
      dynamicProps: ({ record }) => ({
        required:
          record.get('watermarkType') !== 'IMAGE' && record.get('watermarkType') !== 'TILE_IMAGE',
      }),
    },
    {
      name: 'fontSize',
      type: 'number',
      label: intl.get('hfile.waterMark.model.waterMark.fontSize').d('字体大小'),
      defaultValue: 60,
      dynamicProps: ({ record }) => ({
        required:
          record.get('watermarkType') !== 'IMAGE' && record.get('watermarkType') !== 'TILE_IMAGE',
      }),
    },
    {
      name: 'weight',
      type: 'number',
      label: intl.get('hfile.waterMark.model.waterMark.weight').d('图片宽度'),
      defaultValue: 200,
      dynamicProps: ({ record }) => ({
        required:
          record.get('watermarkType') === 'IMAGE' || record.get('watermarkType') === 'TILE_IMAGE',
      }),
    },
    {
      name: 'height',
      type: 'number',
      label: intl.get('hfile.waterMark.model.waterMark.height').d('图片高度'),
      defaultValue: 200,
      dynamicProps: ({ record }) => ({
        required:
          record.get('watermarkType') === 'IMAGE' || record.get('watermarkType') === 'TILE_IMAGE',
      }),
    },
    {
      name: 'xAxis',
      type: 'number',
      required: true,
      // label: intl.get('hfile.waterMark.model.waterMark.xAxis').d('横坐标'),
      defaultValue: 200,
      dynamicProps: ({ record }) => ({
        label:
          record.get('watermarkType') === 'IMAGE' || record.get('watermarkType') === 'TEXT'
            ? intl.get('hfile.waterMark.model.waterMark.xAxis').d('横坐标')
            : intl.get('hfile.waterMark.model.waterMark.xMargin').d('横向间距'),
      }),
    },
    {
      name: 'yAxis',
      type: 'number',
      required: true,
      label: intl.get('hfile.waterMark.model.waterMark.yAxis').d('纵坐标'),
      defaultValue: 300,
      dynamicProps: ({ record }) => ({
        label:
          record.get('watermarkType') === 'IMAGE' || record.get('watermarkType') === 'TEXT'
            ? intl.get('hfile.waterMark.model.waterMark.yAxis').d('纵坐标')
            : intl.get('hfile.waterMark.model.waterMark.yMargin').d('纵向间距'),
      }),
    },
    {
      name: 'align',
      type: 'number',
      required: true,
      label: intl.get('hfile.waterMark.model.waterMark.align').d('对齐方式'),
      defaultValue: 1,
    },
    {
      name: 'rotation',
      type: 'number',
      required: true,
      label: intl.get('hfile.waterMark.model.waterMark.rotation').d('倾斜角度'),
      defaultValue: 0,
    },
    {
      name: 'detail',
      type: 'string',
      required: true,
      maxLength: 480,
      label: intl.get('hfile.waterMark.model.waterMark.detail').d('水印内容'),
      help: intl
        .get('hfile.waterMark.model.waterMark.requireFont')
        .d('水印内容包含中文时，需要指定字体文件'),
    },
    {
      name: 'fontUrl',
      type: 'string',
      maxLength: 480,
      label: intl.get('hfile.waterMark.model.waterMark.fontUrl').d('字体文件'),
    },
    {
      name: 'filename',
      type: 'string',
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      required: true,
      label: intl.get('hfile.waterMark.model.waterMark.enabledFlag').d('启用标识'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ].filter(Boolean),
  transport: {
    read: ({ data }) => {
      const { watermarkId } = data;
      return {
        url: `${apiPrefix}/watermark-configs/${watermarkId}`,
        method: 'GET',
        data: {},
        params: {},
      };
    },
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/watermark-configs`,
        method: 'POST',
        data: other,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/watermark-configs`,
        method: 'PUT',
        data: other,
      };
    },
  },
});

export { tableDS, drawerDS };
