/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019/10/18 3:27 下午
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 数据点模版DS
 */
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import {
  API_PREFIX,
  CONTROL_BOOL_TYPE,
  CONTROL_INT_TYPE,
  DATA_TYPE,
  POINT_TYPE,
  STATUS_TYPE,
  CODE_UPPER_REG,
} from '@/utils/constants';
import notification from 'utils/notification';

const organizationId = getCurrentOrganizationId();

const noticeMessage = intl
  .get('hiot.dataPointTemplate.model.dpt.minmax-valid')
  .d('最大值需大于最小值');
/**
 * 判断当前记录的状态是否在dataTypeArray中
 * @param record 当前记录
 * @param dataTypeArray 数据点类型
 * @returns {boolean}
 */
function dynamicPropsRequired(record, dataTypeArray = []) {
  const { propertyType: { category, dataType } = {} } = record.toData();
  let dataPointType;
  if ([POINT_TYPE, STATUS_TYPE].includes(category)) {
    dataPointType = category;
  } else if (dataType === DATA_TYPE.NUMBER) {
    dataPointType = CONTROL_INT_TYPE;
  } else if ([DATA_TYPE.BOOL, DATA_TYPE.ENUM].includes(dataType)) {
    dataPointType = CONTROL_BOOL_TYPE;
  } else {
    dataPointType = null;
  }
  return dataTypeArray.includes(dataPointType);
}

const dataPointTemplateDS = () => ({
  primaryKey: 'templateId',
  autoQueryAfterSubmit: false,
  transport: {
    /**
     * 查询数据点模板信息
     * @param config
     * @param data 查询参数
     * @returns {{method: string, url: string}}
     */
    read: ({ config, data }) => {
      const { propertyModelId, dataTypeValue: { dataType } = {}, ...params } = data;
      return {
        ...config,
        url: propertyModelId
          ? `${API_PREFIX}/v1/${organizationId}/property-models/${propertyModelId}`
          : `${API_PREFIX}/v1/${organizationId}/property-models`,
        method: 'get',
        data: { dataTypeValue: dataType, ...params },
      };
    },
    /**
     * 创建数据点模板
     * @param config 参数
     * @param data 创建记录的信息
     * @returns {{method: string, data: *, url: string}}
     */
    create: ({ config, data }) => {
      const {
        propertyModel: { unitCode = {}, ...params },
        typeNameCode,
        options,
        ...restFields
      } = data[0];
      const { uomCode } = unitCode;
      return {
        ...config,
        url: `${API_PREFIX}/v1/${organizationId}/property-models`,
        method: 'post',
        data: {
          ...params,
          ...typeNameCode,
          options,
          ...restFields,
          unitCode: uomCode,
        },
      };
    },
    /**
     * 删除数据点模板
     * @param config 参数
     * @param data 待删除的数据点模板id集合
     * @returns {{method: string, data: *, url: string}}
     */
    destroy: ({ config, data }) => ({
      ...config,
      url: `${API_PREFIX}/v1/${organizationId}/property-models`,
      method: 'delete',
      data: data.length > 0 && data.map((record) => record.propertyModelId),
    }),
    /**
     * 更新数据点模板
     * @param config 参数
     * @param data 待更新的数据点模板
     * 存在propertyModel中的alertModelId可能是int类型(初始数据) 可能是object(修改过的数据)
     * @returns {{method: stfring, data: *, url: string}}
     */
    update: ({ data }) => {
      const {
        propertyModel: { unitCode, ...params },
        typeNameCode,
      } = data[0];
      const uomCode = typeof unitCode === 'object' ? unitCode.uomCode : unitCode;
      return {
        url: `${API_PREFIX}/v1/${organizationId}/property-models`,
        method: 'put',
        data: {
          ...params,
          ...typeNameCode,
          unitCode: uomCode,
        },
      };
    },
  },
  feedback: {
    submitFailed: (error) => {
      if (error && error.failed) {
        notification.error({
          message: error.message,
        });
      }
    },
  },
  queryFields: [
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'propertyModelName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'dataTypeValue',
      type: 'object',
      label: intl.get('hiot.dataPointTemplate.model.dpt.dataPointType').d('数据点类型'),
      lovCode: 'HIOT.LOV.PROPERTY_TYPE',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
    },
    {
      name: 'typeCode',
      type: 'string',
      bind: 'dataTypeValue.typeCode',
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
      name: 'propertyModel', // 基本信息
      type: 'object',
    },
    {
      name: 'propertyType', // 数据点类型
      type: 'object',
    },
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
      required: true,
      bind: 'propertyModel.propertyModelCode',
      pattern: CODE_UPPER_REG,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hiot.common.view.validation.codeMsg')
          .d('全大写及数字，必须以字母、数字开头，可包含“_”'),
      },
    },
    {
      name: 'propertyModelName',
      type: 'intl',
      label: intl.get('hiot.common.name').d('名称'),
      required: true,
      bind: 'propertyModel.propertyModelName',
    },
    {
      name: 'dataTypeValue',
      type: 'string',
      label: intl.get('hiot.dataPointTemplate.model.dpt.dataPointType').d('数据点类型'),
    },
    {
      name: 'dataPointType',
      type: 'object',
      lovCode: '',
      lovPara: { tenantId: organizationId },
    },
    {
      name: 'typeNameCode',
      type: 'object',
      lovCode: 'HIOT.LOV.PROPERTY_TYPE',
      lovPara: { tenantId: organizationId },
      label: intl.get('hiot.dataPointTemplate.model.dpt.dataPointType').d('数据点类型'),
      dynamicProps: {
        required({ record }) {
          const { _status } = record.toData();
          return _status === 'create';
        },
      },
    },
    {
      name: 'typeNameMeaning',
      type: 'string',
    },
    {
      name: 'minValue',
      type: 'string',
      label: intl.get('hzero.common.min.value').d('合理最小值'),
      bind: 'propertyModel.minValue',
      validator: (value, name, form) => {
        const { propertyModel: { maxValue = 0, minValue = 0 } = {} } = form.toData();
        return Number(maxValue) >= Number(minValue) || noticeMessage;
      },
    },
    {
      name: 'maxValue',
      type: 'string',
      label: intl.get('hzero.common.max.value').d('合理最大值'),
      bind: 'propertyModel.maxValue',
      validator: (value, name, form) => {
        const { propertyModel: { maxValue = 0, minValue = 0 } = {} } = form.toData();
        return Number(maxValue) >= Number(minValue) || noticeMessage;
      },
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.common.explain').d('说明'),
      maxLength: 100,
      bind: 'propertyModel.description',
    },
    {
      name: 'isReferred',
      type: 'string',
      label: intl.get('hiot.common.isReferred').d('是否被引用'),
      lookupCode: 'HIOT.REFERRED_STATUS',
    },
    {
      name: 'operation',
      type: 'string',
      label: intl.get(`hzero.common.button.action`).d('操作'),
    },
    {
      name: 'reportInterval',
      type: 'number',
      label: intl.get('hiot.dataPointTemplate.model.dpt.collectInterval').d('采集间隔'),
      bind: 'propertyModel.reportInterval',
      dynamicProps: {
        required({ record }) {
          return dynamicPropsRequired(record, [STATUS_TYPE, POINT_TYPE]);
        },
      },
    },
    {
      name: 'validNumArea',
      type: 'string',
      label: intl.get('hiot.dataPointTemplate.model.dpt.validNumArea').d('合理值范围'),
    },
    {
      name: 'valuePrecision',
      type: 'number',
      label: intl.get('hiot.dataPointTemplate.model.dpt.accuracyDigit').d('精度位数'),
      bind: 'propertyModel.valuePrecision',
      dynamicProps: {
        required({ record }) {
          return dynamicPropsRequired(record, [CONTROL_INT_TYPE, POINT_TYPE]);
        },
      },
    },
    {
      name: 'unitCode',
      type: 'object',
      label: intl.get('hiot.dataPointTemplate.model.dpt.unit').d('计量单位'),
      bind: 'propertyModel.unitCode',
      valueField: 'uomCode',
      textField: 'uomName',
      lovCode: 'HIOT.UOM',
      lovPara: { tenantId: organizationId },
      dynamicProps: {
        required({ record }) {
          return dynamicPropsRequired(record, [CONTROL_INT_TYPE, POINT_TYPE]);
        },
      },
    },
    {
      name: 'typeCode',
      type: 'string',
      label: intl.get('hiot.dataPointTemplate.model.dpt.dataTypeCode').d('数据点类型编码'),
      bind: 'propertyType.typeCode',
    },
    {
      name: 'typeName',
      type: 'string',
      label: intl.get('hiot.dataPointTemplate.model.dpt.dataTypeName').d('数据点类型名称'),
      bind: 'propertyType.typeName',
    },
    {
      name: 'categoryMeaning',
      type: 'string',
      label: intl.get('hzero.common.category').d('分类'),
      bind: 'propertyType.categoryMeaning',
    },
    {
      name: 'dataTypeMeaning',
      type: 'string',
      label: intl.get('hiot.common.data.type').d('数据类型'),
      bind: 'propertyType.dataTypeMeaning',
    },
  ],
  events: {
    update: ({ dataSet, name }) => {
      if (name === 'maxValue' || name === 'minValue') {
        dataSet.validate();
      }
    },
  },
});

export { dataPointTemplateDS };
