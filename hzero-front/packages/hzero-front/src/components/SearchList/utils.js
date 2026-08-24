import React, { cloneElement, isValidElement } from 'react';
import { isString, isObject, map, omit } from 'lodash';
import warning from 'choerodon-ui/lib/_util/warning';
import {
  Select,
  Lov,
  CheckBox,
  NumberField,
  Currency,
  ColorPicker,
  DatePicker,
  TextField,
  DateTimePicker,
  TimePicker,
  WeekPicker,
  MonthPicker,
  YearPicker,
  // IntlField,
  EmailField,
  UrlField,
  Stores,
} from 'choerodon-ui/pro';
import { OPERATOR } from './constant';

export function getEditorByField(field) {
  const lookupCode = field.get('lookupCode');
  const lookupUrl = field.get('lookupUrl');
  const lovCode = field.get('lovCode');
  const defaultValue = field.get('defaultValue');
  const { type, name } = field;
  let placeholder = '';
  if (defaultValue && OPERATOR[defaultValue]) {
    placeholder = defaultValue && OPERATOR[defaultValue].meaning;
    field.set('defaultValue', undefined);
  }
  if (lookupCode || isString(lookupUrl) || (type !== 'object' && (lovCode || field.options))) {
    return <Select placeholder={placeholder} />;
  }
  if (lovCode) {
    return <Lov placeholder={placeholder} />;
  }
  switch (type) {
    case 'boolean':
      return <CheckBox placeholder={placeholder} />;
    case 'number':
      return <NumberField placeholder={placeholder} />;
    case 'currency':
      return <Currency placeholder={placeholder} />;
    case 'date':
      return <DatePicker format="YYYY-MM-DD HH:mm:ss" placeholder={placeholder} />;
    case 'dateTime':
      return <DateTimePicker placeholder={placeholder} />;
    case 'time':
      return <TimePicker placeholder={placeholder} />;
    case 'week':
      return <WeekPicker placeholder={placeholder} />;
    case 'month':
      return <MonthPicker placeholder={placeholder} />;
    case 'year':
      return <YearPicker placeholder={placeholder} />;
    case 'intl':
      // return <IntlField placeholder={placeholder} />;
      return <TextField placeholder={placeholder} />;
    case 'email':
      return <EmailField placeholder={placeholder} />;
    case 'url':
      return <UrlField placeholder={placeholder} />;
    case 'color':
      return <ColorPicker placeholder={placeholder} />;
    case 'string':
      return <TextField placeholder={placeholder} />;
    default:
      warning(
        false,
        `Table auto editor: No editor exists on the field<${name}>'s type<${type}>, so use the TextField as default editor`
      );
      return <TextField placeholder={placeholder} />;
  }
}

export function getQueryFields(dataSet) {
  const {
    props: { queryFields },
  } = dataSet;
  const queryDataSet = dataSet.queryDataSet || dataSet.props.queryDataSet;
  const result = [];
  if (queryDataSet) {
    const fields = queryDataSet.fields || queryDataSet.props.fields;
    return [...fields.entries()].reduce((list, [name, field]) => {
      if (!field.get('bind')) {
        const props = {
          key: name,
          name,
          dataSet: queryDataSet,
        };
        const element = queryFields[name];
        list.push(
          isValidElement(element)
            ? cloneElement(element, props)
            : cloneElement(getEditorByField(field), {
                ...props,
                ...(isObject(element) ? element : {}),
              })
        );
      }
      return list;
    }, result);
  }
  return result;
}

export async function setCondition(dataSet, list, reset) {
  map(list, async (param, index) => {
    const field = dataSet.getField(param.fieldName);
    const lovCode = field.get('lovCode');
    const valueField = field.get('valueField');
    let paramName = param.fieldName;
    if (lovCode) {
      if (!valueField) {
        const config = await Stores.LovCodeStore.fetchConfig(lovCode);
        paramName = config.valueField;
      } else {
        paramName = valueField;
      }
    }
    const params = param.comparator.includes('NULL')
      ? `${paramName},${param.comparator}`
      : `${paramName},${param.comparator},${param.value}`;
    dataSet.setQueryParameter(`search.condition.${index}`, reset ? '' : params);
    dataSet.setQueryParameter(paramName, '');
  });
}

export function setOrder(dataSet, list, reset) {
  map(list, async (param, index) => {
    const field = dataSet.getField(param.fieldName);
    const lovCode = field.get('lovCode');
    let paramName = param.fieldName;
    const valueField = field.get('valueField');
    if (lovCode) {
      if (!valueField) {
        const config = await Stores.LovCodeStore.fetchConfig(lovCode);
        paramName = config.valueField;
      } else {
        paramName = valueField;
      }
    }
    dataSet.setQueryParameter(
      `search.order.${index}`,
      reset ? '' : `${paramName},${param.direction}`
    );
    dataSet.setQueryParameter(paramName, '');
  });
}

export function omitData(data) {
  return omit(
    data,
    'creationDate',
    'createdBy',
    'lastUpdateDate',
    'lastUpdatedBy',
    'objectVersionNumber',
    '_token',
    'searchId',
    'searchConditionId',
    'searchQueryId',
    'searchOrderId'
  );
}
