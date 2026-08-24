import React, { createContext, useMemo } from 'react';
import { map, isString, isUndefined, isArray } from 'lodash';
import { DataSet } from 'choerodon-ui/pro';
import SortDataSet from './SortDataSet';
import QueryDataSet from './QueryDataSet';
import ConditionDataSet from './ConditionDataSet';
import SearchDataSet from './SearchDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = props => {
  const { children, tableDataSet, fields, ignoreFields = [], searchCode } = props;
  let fieldArr = [];
  let fieldsValue = [];

  if (fields && isArray(fields)) {
    fieldsValue = fields;
  } else {
    fieldsValue = Object.values(map(tableDataSet.fields.toJSON()));
  }
  fieldArr = map(fieldsValue, field => {
    const {
      type,
      name,
      lookupUrl,
      lovCode,
      lookupCode,
      label,
      options,
      valueField,
      textField,
      defaultValue,
    } = field.props || field;
    let optType = '';
    if (!isUndefined(name) && !ignoreFields.includes(name)) {
      if (lookupCode || isString(lookupUrl) || (type !== 'object' && (lovCode || options))) {
        optType = 'lookup';
      }

      if (lovCode) optType = 'lov';

      return {
        value: name,
        meaning: label || name,
        type: optType || type,
        lovCode,
        valueField,
        textField,
        defaultValue,
      };
    }
    return undefined;
  }).filter(item => !isUndefined(item));

  const sortDataSet = useMemo(() => new DataSet(SortDataSet(fieldArr)), []);
  const queryDataSet = useMemo(() => new DataSet(QueryDataSet(fieldArr)), []);
  const conditionDataSet = useMemo(() => new DataSet(ConditionDataSet(tableDataSet, fieldArr)), []);

  const searchDataSet = useMemo(
    () =>
      new DataSet({
        ...SearchDataSet({ searchCode }),
        children: {
          orderList: sortDataSet,
          queryList: queryDataSet,
          conditionList: conditionDataSet,
        },
      }),
    []
  );

  const value = {
    ...props,
    sortDataSet,
    queryDataSet,
    searchDataSet,
    conditionDataSet,
    fieldArr,
  };
  return <Store.Provider value={value}>{children}</Store.Provider>;
};
