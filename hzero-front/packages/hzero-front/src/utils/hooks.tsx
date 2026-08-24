import { DataSet } from 'choerodon-ui/pro';
import { autorun } from 'mobx';
import { useEffect, useMemo, useState } from 'react';

export type DataSetFactory = () => DataSet;

class TimedMap extends Map {
  // todo 时间缓存
}

const DataSetStore = new TimedMap();

export const useDataSet = (dataSetFactory: DataSetFactory, cacheKey: any = false): DataSet => {
  const ds = useMemo<DataSet>(() => {
    if (cacheKey) {
      let cacheDS = DataSetStore.get(cacheKey);
      if (!cacheDS) {
        cacheDS = dataSetFactory();
        DataSetStore.set(cacheKey, cacheDS);
      }
      return cacheDS;
    }
    return dataSetFactory();
  }, []);
  return ds;
};

export const useDataSetCurrentPage = (ds: DataSet): number => {
  const [currentPage, setCurrentPage] = useState<number>(ds.currentPage);
  useEffect(() => {
    const disposer = autorun(() => {
      setCurrentPage(ds.currentPage);
    });
    return disposer;
  }, []);
  return currentPage;
};

export const useDataSetIsSelected = (ds: DataSet): boolean => {
  const isSelectedFun = (): boolean => {
    return ds.selected.length > 0;
  };
  const [isSelected, setIsSelected] = useState<boolean>(isSelectedFun());
  useEffect(() => {
    const disposer = autorun(() => {
      setIsSelected(isSelectedFun());
    });
    return disposer;
  }, []);
  return isSelected;
};

export const useDataSetEvent = (
  ds: DataSet,
  eventName: string,
  // CallableFunction is define in lib.es5.d.ts
  // eslint-disable-next-line no-undef
  eventListener: CallableFunction
) => {
  useEffect(() => {
    ds.addEventListener(eventName, eventListener);
    return () => {
      ds.removeEventListener(eventName, eventListener);
    };
  }, []);
};
