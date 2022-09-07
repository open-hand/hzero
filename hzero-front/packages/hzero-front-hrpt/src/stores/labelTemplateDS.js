/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/29
 * @copyright 2019 ® HAND
 */

import intl from 'utils/intl';
import { HZERO_RPT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const isTenant = isTenantRoleLevel();
const organizationId = getCurrentOrganizationId();

export const labelTemplateListDS = () => ({
  rowKey: 'labelTemplateId',
  autoQuery: true,
  selection: false,
  queryFields: [
    {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('entity.tenant.tag').d('租户'),
      lovCode: 'HPFM.TENANT', // 平台/租户 统一使用这个 sql lov
      ignore: 'always',
    },
    {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'enabledFlag',
      label: intl.get('hzero.common.status').d('状态'),
      type: 'single',
      lookupCode: 'HPFM.ENABLED_FLAG',
    },
    {
      name: 'templateCode',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateCode').d('模板代码'),
      type: 'string',
    },
    {
      name: 'templateName',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateName').d('模板名称'),
      type: 'string',
    },
    {
      name: 'datasetName',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.datasetName').d('数据集名称'),
      type: 'string',
    },
  ],
  fields: [
    {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('entity.tenant.tag').d('租户'),
      lovCode: 'HPFM.TENANT', // 平台/租户 统一使用这个 sql lov
      ignore: 'always',
      required: true,
    },
    {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'tenantName',
      type: 'string',
      bind: 'tenantLov.tenantName',
    },
    {
      name: 'templateCode',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateCode').d('模板代码'),
      type: 'string',
      required: true,
    },
    {
      name: 'templateName',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateName').d('模板名称'),
      type: 'string',
      required: true,
    },
    {
      name: 'datasetLov',
      type: 'object',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.dataset').d('数据集'),
      lovCode: 'HRPT.DATASET', // 数据集的Lov
      ignore: 'always',
      required: true,
      cascadeMap: { tenantId: 'tenantId' },
    },
    {
      name: 'datasetId',
      bind: 'datasetLov.datasetId',
      type: 'string',
    },
    {
      name: 'datasetName',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.datasetName').d('数据集名称'),
      bind: 'datasetLov.datasetName',
    },
    {
      name: 'enabledFlag',
      label: intl.get('hzero.common.status').d('状态'),
      type: 'single',
      lookupCode: 'HPFM.ENABLED_FLAG',
      defaultValue: '1',
      required: true,
      transformResponse: (value) => {
        // default is enabld, so
        if (value === 0) {
          return '0';
        }
        return '1';
      },
    },
    {
      name: 'templateWidth',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateWidth').d('模板宽度'),
      type: 'number',
      required: true,
    },
    {
      name: 'templateHigh',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateHigh').d('模板高度'),
      type: 'number',
      required: true,
    },
  ],
  transport: {
    read({ data, config }) {
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-templates`
          : `${HZERO_RPT}/v1/label-templates`,
        method: 'GET',
        query: data,
      };
    },
    create({ data, dataSet }) {
      const {
        queryParameter: { copy },
      } = dataSet;
      return {
        // eslint-disable-next-line no-nested-ternary
        url: copy
          ? isTenant
            ? `${HZERO_RPT}/v1/${organizationId}/label-templates/copy`
            : `${HZERO_RPT}/v1/label-templates/copy`
          : isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-templates`
          : `${HZERO_RPT}/v1/label-templates`,
        method: 'POST',
        data: data[0],
        params: {
          tenantId: organizationId,
        },
      };
    },
    destroy({ data, config }) {
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-templates`
          : `${HZERO_RPT}/v1/label-templates`,
        method: 'DELETE',
        data: data[0],
      };
    },
  },
});

/**
 *
 * 编辑的表单 DS
 * @param {number} labelTemplateId - 当前编辑的标签id
 * @param labelParameterListDS
 */
export const labelTemplateFormDS = (labelTemplateId, labelParameterListDS) => ({
  rowKey: 'labelTemplateId',
  autoQuery: true,
  paging: false,
  fields: [
    {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('entity.tenant.tag').d('租户'),
      lovCode: 'HPFM.TENANT', // 平台/租户 统一使用这个 sql lov
      ignore: 'always',
      required: true,
    },
    {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'tenantName',
      type: 'string',
      bind: 'tenantLov.tenantName',
    },
    {
      name: 'templateCode',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateCode').d('模板代码'),
      type: 'string',
      required: true,
    },
    {
      name: 'templateName',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateName').d('模板名称'),
      type: 'intl',
      required: true,
    },
    {
      name: 'datasetLov',
      type: 'object',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.dataset').d('数据集'),
      lovCode: 'HRPT.DATASET', // 数据集的Lov
      ignore: 'always',
      required: true,
      cascadeMap: { tenantId: 'tenantId' },
    },
    {
      name: 'datasetId',
      bind: 'datasetLov.datasetId',
      type: 'string',
    },
    {
      name: 'datasetName',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.datasetName').d('数据集名称'),
      bind: 'datasetLov.datasetName',
    },
    {
      name: 'enabledFlag',
      label: intl.get('hzero.common.status').d('状态'),
      type: 'single',
      lookupCode: 'HPFM.ENABLED_FLAG',
      required: true,
      defaultValue: '1',
      transformResponse: (value) => {
        // default is enabld, so
        if (value === 0) {
          return '0';
        }
        return '1';
      },
    },
    {
      name: 'templateWidth',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateWidth').d('模板宽度'),
      type: 'number',
      required: true,
      min: 1,
    },
    {
      name: 'templateHigh',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateHigh').d('模板高度'),
      type: 'number',
      required: true,
      min: 1,
    },
    {
      name: 'templateContent',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateContent').d('模板内容'),
      type: 'string',
    },
  ],
  transport: {
    read({ config }) {
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-templates/${labelTemplateId}`
          : `${HZERO_RPT}/v1/label-templates/${labelTemplateId}`,
        method: 'GET',
        query: {},
      };
    },
    update({ data, config }) {
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-templates`
          : `${HZERO_RPT}/v1/label-templates`,
        method: 'PUT',
        data: data[0],
      };
    },
  },
  children: {
    labelParameterList: labelParameterListDS,
  },
});

// 参数的 DS labelParameterList
export const labelTemplateParameterDS = () => ({
  rowKey: 'labelParameterId',
  autoQuery: false,
  paging: false,
  selection: false,
  fields: [
    {
      name: 'parameterCode',
      label: intl.get('hrpt.labelTemplate.model.parameter.parameterCode').d('参数代码'),
      type: 'string',
      required: true,
    },
    {
      name: 'parameterName',
      label: intl.get('hrpt.labelTemplate.model.parameter.parameterName').d('参数名称'),
      type: 'string',
      required: true,
    },
    {
      name: 'paramTypeCode',
      label: intl.get('hrpt.labelTemplate.model.parameter.parameterType').d('参数类型'),
      type: 'single',
      required: true,
      lookupCode: 'HRPT.LABEL_PARAM_TYPE',
    },
    {
      name: 'textLength',
      label: intl.get('hrpt.labelTemplate.model.parameter.textLength').d('文字长度'),
      type: 'number',
    },
    {
      name: 'maxRows',
      label: intl.get('hrpt.labelTemplate.model.parameter.maxRows').d('最大行数'),
      type: 'number',
    },
    {
      name: 'barCodeType',
      label: intl.get('hrpt.labelTemplate.model.parameter.barCodeType').d('条码类型'),
      type: 'single',
      lookupCode: 'HRPT.LABEL_BARCODE_TYPE',
    },
    {
      name: 'characterEncoding',
      label: intl.get('hrpt.labelTemplate.model.parameter.characterEncoding').d('字符编码'),
      type: 'string',
      dynamicProps: {
        required({ record }) {
          return record.get('paramTypeCode') === 'BARCODE';
        },
      },
      lookupCode: 'HRPT.LABEL.CHARACTER_ENCODE',
    },
  ],
  transport: {
    destroy({ data, config }) {
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-parameters`
          : `${HZERO_RPT}/v1/label-parameters`,
        method: 'DELETE',
        data: data[0],
      };
    },
  },
});

/**
 * 富文本编辑器 标签插入表单
 */
export const labelTemplateLabelInsertConfig = () =>
  // 圆形 和 方形 公用宽高 以 设置椭圆
  // 方形/圆形 才显示 类型
  ({
    constants: {
      shape: {
        // 圆形
        circle: '1',
        // 方形
        rectangle: '2',
        // 横线
        horizontalLine: '4',
        // 竖线
        verticalLine: '8',
      },
      type: {
        // 文本
        text: '1',
        // 图片
        img: '2',
        // 条码
        bar: '4',
        // 二维码
        qr: '8',
      },
      sourceType: {
        // 手动输入
        text: '1',
        // 参数
        param: '2',
        // 图片上传
        upload: '4',
      },
    },
    options: {
      shape: [
        {
          value: '1',
          meaning: intl.get('hrpt.labelTemplate.view.option.cke.p.label.shape.circle').d('圆'),
        },
        {
          value: '2',
          meaning: intl.get('hrpt.labelTemplate.view.option.cke.p.label.shape.rectangle').d('方形'),
        },
        {
          value: '4',
          // 水平线
          meaning: intl.get('hrpt.labelTemplate.view.option.cke.p.label.type.hL').d('横线'),
        },
        {
          value: '8',
          // 垂直线
          meaning: intl
            .get('hrpt.labelTemplate.view.option.cke.p.label.type.verticalLine')
            .d('竖线'),
        },
      ],
      type: [
        {
          value: '1',
          meaning: intl.get('hrpt.labelTemplate.view.option.cke.p.label.type.text').d('文本'),
        },
        {
          value: '2',
          meaning: intl.get('hrpt.labelTemplate.view.option.cke.p.label.type.img').d('图片'),
        },
        {
          value: '4',
          meaning: intl.get('hrpt.labelTemplate.view.option.cke.p.label.type.bar').d('条码'),
        },
        {
          value: '8',
          meaning: intl.get('hrpt.labelTemplate.view.option.cke.p.label.type.qr').d('二维码'),
        },
      ],
      sourceType: [
        {
          value: '1',
          meaning: intl
            .get('hrpt.labelTemplate.view.option.cke.p.label.sourceType.text')
            .d('手动输入'),
        },
        {
          value: '2',
          meaning: intl
            .get('hrpt.labelTemplate.view.option.cke.p.label.sourceType.param')
            .d('表达式'),
        },
        {
          value: '4',
          meaning: intl
            .get('hrpt.labelTemplate.view.option.cke.p.label.sourceType.upload')
            .d('上传图片'),
        },
      ],
    },
    ds: {
      fields: [
        {
          name: 'shape',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.shape').d('形状'),
          required: true,
        },
        {
          name: 'code',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.code').d('参数'),
          required: true,
        },
        // 图片, 条码, 文本
        {
          name: 'type',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.type').d('类型'),
          required: true,
        },
        // 输入源: 参数 手动输入 上传图片(图片)
        {
          name: 'sourceType',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.sourceType').d('输入源'),
          required: true,
          defaultValue: '1',
        },
        // 方形/横线/竖线
        {
          name: 'width',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.width').d('宽'),
          type: 'number',
          required: true,
        },
        {
          name: 'height',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.height').d('高'),
          type: 'number',
          required: true,
        },
        // 文本
        {
          name: 'text',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.text').d('内容'),
          type: 'string',
          required: true,
        },
        // 参数, now call expression, all is expression;
        {
          name: 'param',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.param').d('表达式'),
          type: 'string',
          required: true,
        },
        // 图片
        {
          name: 'src',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.src').d('图片URL'),
          type: 'string',
          required: true,
        },
        // 图片
        {
          name: 'borderWidth',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.borderWidth').d('边框宽度'),
          type: 'string',
          required: true,
        },
        // 图片
        {
          name: 'borderColor',
          label: intl.get('hrpt.labelTemplate.model.cke.p.label.borderColor').d('边框颜色'),
          type: 'string',
          required: true,
        },
      ],
    },
  });

/**
 * 权限分配
 * @param {object} labelTemplate - 标签模板数据
 */
export const labelTemplateAssignPermissionConfig = (labelTemplate) => ({
  autoQuery: true,
  selection: false,
  queryFields: [
    {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('entity.tenant.tag').d('租户'),
      lovCode: 'HPFM.TENANT', // 平台/租户 统一使用这个 sql lov
      ignore: 'always',
      disabled: true,
    },
    {
      name: 'tenantId',
      type: 'number',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'enabledFlag',
      label: intl.get('hzero.common.status').d('状态'),
      type: 'single',
      lookupCode: 'HPFM.ENABLED_FLAG',
      disabled: true,
    },
    {
      name: 'templateCode',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateCode').d('模板代码'),
      type: 'string',
      disabled: true,
    },
    {
      name: 'templateName',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.templateName').d('模板名称'),
      type: 'string',
    },
    {
      name: 'datasetName',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.datasetName').d('数据集名称'),
      type: 'string',
    },
    {
      name: 'datasetLov',
      type: 'object',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.dataset').d('数据集'),
      lovCode: 'HRPT.DATASET', // 数据集的Lov
      ignore: 'always',
      required: true,
      cascadeMap: { tenantId: 'tenantId' },
    },
    {
      name: 'datasetId',
      bind: 'datasetLov.datasetId',
      type: 'string',
    },
    {
      name: 'datasetName',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.datasetName').d('数据集名称'),
      bind: 'datasetLov.datasetName',
    },
  ],
  fields: [
    {
      name: 'tenantLov',
      type: 'object',
      lovCode: 'HPFM.TENANT', // 平台/租户 统一使用这个 sql lov
      label: intl.get('entity.tenant.tag').d('租户'),
      ignore: 'always',
      required: true,
    },
    {
      name: 'tenantId',
      type: 'number',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'tenantName',
      type: 'string',
      bind: 'tenantLov.tenantName',
    },
    {
      name: 'roleLov',
      type: 'object',
      lovCode: 'HIAM.TENANT_ROLE', // 平台/租户 统一使用这个 sql lov
      label: intl.get('hrpt.labelTemplate.model.labelPermissions.role').d('角色'),
      cascadeMap: { tenantId: 'tenantId' },
      ignore: 'always',
      required: true,
    },
    {
      name: 'roleId',
      type: 'string',
      bind: 'roleLov.id',
    },
    {
      name: 'roleName',
      type: 'string',
      bind: 'roleLov.name',
    },
    {
      name: 'startDate',
      type: 'date',
      label: intl.get('hrpt.labelTemplate.model.labelPermissions.startDate').d('生效日期'),
      dynamicProps: {
        max: ({ record }) => record.get('endDate'),
      },
    },
    {
      name: 'endDate',
      type: 'date',
      label: intl.get('hrpt.labelTemplate.model.labelPermissions.endDate').d('失效日期'),
      dynamicProps: {
        min: ({ record }) => record.get('startDate'),
      },
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hrpt.labelTemplate.model.labelPermissions.remark').d('描述'),
    },
  ],
  transport: {
    read({ params, config }) {
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-permissions`
          : `${HZERO_RPT}/v1/label-permissions`,
        method: 'GET',
        params: {
          ...params,
          labelTemplateId: labelTemplate.labelTemplateId,
        },
      };
    },
    create({ data, config }) {
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-permissions`
          : `${HZERO_RPT}/v1/label-permissions`,
        method: 'POST',
        data: data[0],
      };
    },
    update({ data, config }) {
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-permissions`
          : `${HZERO_RPT}/v1/label-permissions`,
        method: 'PUT',
        data: data[0],
      };
    },
    destroy({ data, config }) {
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-permissions`
          : `${HZERO_RPT}/v1/label-permissions`,
        method: 'DELETE',
        data: data[0],
      };
    },
  },
});

/**
 * 打印设置
 * @param {object} labelTemplate - 标签模板数据
 */
export const labelTemplatePrintSettingsConfig = (labelTemplate) => ({
  autoQuery: false,
  autoCreate: true,
  paging: false,
  fields: [
    // paper setting
    {
      name: 'paperSize',
      label: intl.get('hrpt.labelTemplate.model.print.paperSize').d('纸张尺寸'),
      lookupCode: 'HRPT.LABEL.PAPER',
      type: 'string',
      required: true,
    },
    {
      name: 'paperWidth',
      label: intl.get('hrpt.labelTemplate.model.print.paperWidth').d('宽度(mm)'),
      type: 'number',
      required: true,
      dynamicProps: {
        readOnly({ record }) {
          return record.get('paperSize') !== 'customize';
        },
      },
    },
    {
      name: 'paperHigh',
      label: intl.get('hrpt.labelTemplate.model.print.paperHigh').d('高度(mm)'),
      type: 'number',
      // required: true, // 当选择无限制后 不需要
      dynamicProps: {
        readOnly({ record }) {
          return record.get('paperSize') !== 'customize' || record.get('_paperNoLimit');
        },
        required({ record }) {
          return !record.get('_paperNoLimit');
        },
      },
    },
    {
      name: '_paperNoLimit',
      label: intl.get('hrpt.labelTemplate.model.print.paperNoLimit').d('无限制'),
      type: 'boolean',
      trueValue: true,
      falseValue: false,
      defaultValue: false,
      dynamicProps: {
        readOnly({ record }) {
          return record.get('paperSize') !== 'customize';
        },
      },
    },
    // directory setting
    {
      name: 'printDirection',
      label: intl.get('hrpt.labelTemplate.model.print.printDirection').d('方向'),
      type: 'boolean',
      trueValue: 0, // 横向
      falseValue: 1, // 纵向
      defaultValue: 0,
    },
    // paper padding setting
    {
      name: 'marginTop',
      label: intl.get('hrpt.labelTemplate.model.print.marginTop').d('上(mm)'),
      type: 'number',
      required: true,
      defaultValue: 0,
    },
    {
      name: 'marginRight',
      label: intl.get('hrpt.labelTemplate.model.print.marginRight').d('右(mm)'),
      type: 'number',
      required: true,
      defaultValue: 0,
    },
    {
      name: 'marginBottom',
      label: intl.get('hrpt.labelTemplate.model.print.marginBottom').d('下(mm)'),
      type: 'number',
      // required: true, // 如果无限制, 则下间距 为 undefined
      defaultValue: 0,
      dynamicProps: {
        readOnly({ record }) {
          return record.get('_paperNoLimit');
        },
        required({ record }) {
          return !record.get('_paperNoLimit');
        },
      },
    },
    {
      name: 'marginLeft',
      label: intl.get('hrpt.labelTemplate.model.print.marginLeft').d('左(mm)'),
      type: 'number',
      required: true,
      defaultValue: 0,
    },
    // label count setting
    {
      name: 'wideQty',
      label: intl.get('hrpt.labelTemplate.model.print.wideQty').d('宽(个)'),
      type: 'number',
      required: true,
      defaultValue: 1,
      // 需要校验 wideQty * (templateWidth(printDirection = 0), templateHigh(printDirection = 1)) + marginLeft + marginRight < paperWidth
    },
    {
      name: 'highQty',
      label: intl.get('hrpt.labelTemplate.model.print.highQty').d('高(个)'),
      type: 'number',
      // required: true, // 如果高度无限制, 则高个数 为 undefined
      defaultValue: 1,
      // 当高度没有选择不限制时 需要校验 highQty * (templateHigh(printDirection = 0), templateWidth(printDirection = 1)) + marginTop + marginBottom < paperHigh
      dynamicProps: {
        readOnly({ record }) {
          return record.get('_paperNoLimit');
        },
      },
    },
    {
      name: 'highSpace',
      label: intl.get('hrpt.labelTemplate.model.print.highSpace').d('高间距(mm)'),
      type: 'number',
      // required: true, // 如果高度无限制, 则高间距必输
      defaultValue: 0,
      dynamicProps: {
        required({ record }) {
          return record.get('_paperNoLimit');
        },
      },
    },
  ],
  transport: {
    read({ config }) {
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-prints`
          : `${HZERO_RPT}/v1/label-prints`,
        method: 'GET',
        params: {
          // tenantId: organizationId,
          labelTemplateCode: labelTemplate.templateCode,
        },
      };
    },
    create({ data }) {
      return {
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-prints`
          : `${HZERO_RPT}/v1/label-prints`,
        method: 'POST',
        data: data[0],
      };
    },
    update({ data, dataSet }) {
      const {
        queryParameter: { tenantId },
      } = dataSet;
      return {
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-prints`
          : `${HZERO_RPT}/v1/label-prints`,
        method: 'POST',
        data: {
          ...data[0],
          tenantId,
        },
      };
    },
  },
});

/**
 * 打印设置
 * @param {object} labelTemplate - 标签模板数据
 */
export const PrintDS = () => ({
  autoQuery: true,
  fields: [],
  // transport: {
  //   read: ({ dataSet }) => {
  //     const {
  //       queryParameter: { templateCode },
  //     } = dataSet;
  //     return {
  //       url: isTenant
  //         ? `${HZERO_RPT}/v1/${organizationId}/label-prints/meta/${templateCode}`
  //         : `${HZERO_RPT}/v1/label-prints/meta/${templateCode}`,
  //       method: 'GET',
  //       params: {},
  //     };
  //   },
  // },
});

export const PrintConfigDS = () => ({
  autoQuery: false,
  autoCreate: true,
  fields: [
    {
      name: 'printerName',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.printName').d('打印机名称'),
      required: true,
      type: 'string',
      lookupCode: 'HRPT.PRINTER_LIST',
    },
    {
      name: 'quantity',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.quantity').d('份数'),
      required: true,
      type: 'number',
    },
    {
      name: 'scaling',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.scaling').d('填充'),
      type: 'string',
      lookupCode: 'HRPT.PRINT_SCALING',
    },
    {
      name: 'sides',
      label: intl.get('hrpt.labelTemplate.model.labelTemplate.sides').d('单双面'),
      type: 'string',
      lookupCode: 'HRPT.PRINT_SIDES',
    },
  ],
  transport: {
    read: ({ dataSet, config }) => {
      const {
        queryParameter: { templateCode, printerName, quantity },
      } = dataSet;
      const params = isTenant
        ? {
            printerName,
            quantity,
            labelTemplateCode: templateCode,
          }
        : {
            // organizationId,
            printerName,
            quantity,
            labelTemplateCode: templateCode,
          };
      return {
        ...config,
        url: isTenant
          ? `${HZERO_RPT}/v1/${organizationId}/label-prints/print-by-drive`
          : `${HZERO_RPT}/v1/label-prints/print-by-drive`,
        method: 'GET',
        params,
      };
    },
  },
});
