import React from 'react';

// import { DEBOUNCE_TIME } from 'utils/constants';

import { loadHistory, storeHistory } from '../../../components/DefaultHeaderSearch/utils';

// import { useDebounceState } from '../../hooks';

const useHistory = () => {
  // const [history, setHistory] = useDebounceState(loadHistory());
  const [history, setHistory] = React.useState(loadHistory());
  const wrapSetHistory = React.useCallback(
    (newHistory) => {
      storeHistory(newHistory);
      setHistory(newHistory);
    },
    [setHistory]
  );
  return [history, wrapSetHistory];
};
const useSearch = (leafMenus) => {
  // const [searchData, setSearchData] = useDebounceState([], DEBOUNCE_TIME, [leafMenus]);
  // const [searchValue, setSearchValue] = useDebounceState(undefined);
  const [searchData, setSearchData] = React.useState([]);
  const [searchValue, setSearchValue] = React.useState();
  const wrapSetSearchValue = React.useCallback(
    (newSearchValue) => {
      const trimValue = newSearchValue ? newSearchValue.trim() : undefined;
      setSearchValue(trimValue);
      if (trimValue) {
        setSearchData(
          leafMenus.filter((item) => {
            if (item.title.toLowerCase().includes(trimValue.toLowerCase())) {
              return true;
            }
            const quickIndex = item && item.quickIndex && item.quickIndex.toLowerCase();
            if (quickIndex) {
              return quickIndex.startsWith(trimValue.toLowerCase());
            }
            return false;
          })
        );
      } else {
        setSearchData([]);
      }
    },
    [leafMenus, setSearchData]
  );
  return [searchData, searchValue, wrapSetSearchValue];
};
export { useHistory, useSearch };
