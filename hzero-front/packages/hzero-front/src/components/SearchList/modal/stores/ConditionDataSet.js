import { DataSet, Stores } from 'choerodon-ui/pro';
import { map, omit, isObject } from 'lodash';
import { OPERATOR_TYPE } from '../../constant';

async function findLovParam(fieldArr, name, value) {
  const { lovCode, valueField } = fieldArr.find(field => field.value === name);
  if (lovCode) {
    if (lovCode) {
      if (!valueField) {
        const config = await Stores.LovCodeStore.fetchConfig(lovCode);
        return value[config.valueField];
      } else {
        return value[valueField];
      }
    }
  }
}

/**
 * 字段改变事件
 * @param record
 * @param name
 * @param value
 * @param fieldArr
 */
async function handleFieldChange({ record, name, value }, fieldArr) {
  const { defaultValue, lovCode } =
    fieldArr.find(field => field.value === record.get('fieldName')) || {};

  if (name === 'fieldName') {
    record.set('comparator', '');
    record.set('value', undefined);
    map(fieldArr, field => {
      if (value === field.value) {
        record.getField('comparator').set(
          'options',
          new DataSet({
            selection: 'single',
            data: OPERATOR_TYPE[field.type.toUpperCase()],
          })
        );
      }
    });
  }
  if (name === 'comparator') {
    if (value && value.indexOf('NULL') !== -1) {
      record.set('value', undefined);
    } else if (defaultValue) {
      record.set('value', defaultValue);
    }
  }
  if (name === 'value' && isObject(value) && lovCode) {
    const lovValue = await findLovParam(fieldArr, record.get('fieldName'), value);
    record.set('value', lovValue);
  }
}

function getDynamicProps(dataSet, record) {
  if (record.get('fieldName')) {
    const tableField = dataSet.getField(record.get('fieldName'));
    return tableField
      ? omit(tableField.props, [
          'name',
          'label',
          'required',
          'bind',
          'ignore',
          'validator',
          'readOnly',
          'cascadeMap',
        ])
      : {};
  }
  return {};
}

export default (dataSet, fieldArr) => ({
  paging: false,
  fields: [
    {
      name: 'fieldName',
      type: 'string',
      label: '字段',
      unique: true,
      options: new DataSet({
        selection: 'single',
        paging: false,
        data: fieldArr,
      }),
      required: true,
    },
    {
      name: 'comparator',
      type: 'string',
      label: '比较符',
      options: new DataSet({
        selection: 'single',
        paging: false,
        data: OPERATOR_TYPE.ALL,
      }),
      required: true,
    },
    {
      name: 'value',
      label: '比较值',
      dynamicProps: ({ record }) => getDynamicProps(dataSet, record),
    },
    {
      name: 'searchConditionId',
      type: 'number',
    },
  ],
  events: {
    update: props => handleFieldChange(props, fieldArr),
  },
});
