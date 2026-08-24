import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import SearchListDataSet from './SearchListDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = props => {
  const { children, searchCode } = props;
  const listDataSet = useMemo(() => new DataSet(SearchListDataSet({ searchCode })), []);

  const value = {
    ...props,
    listDataSet,
  };
  return <Store.Provider value={value}>{children}</Store.Provider>;
};
