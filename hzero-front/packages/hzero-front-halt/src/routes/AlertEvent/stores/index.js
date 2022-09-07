import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { AlertEventDS, LogDS } from '@/stores/AlertEventDS';

const Store = createContext();

export default Store;

export const StoreProvider = (props) => {
  const { children } = props;
  const alertEventDS = useMemo(() => new DataSet(AlertEventDS()), []);
  const logDS = useMemo(() => new DataSet(LogDS()), []);
  const value = {
    ...props,
    alertEventDS,
    logDS,
  };
  return <Store.Provider value={value}>{children}</Store.Provider>;
};
