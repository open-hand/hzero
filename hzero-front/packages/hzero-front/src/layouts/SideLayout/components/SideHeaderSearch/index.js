/**
 * 菜单搜索框
 */

import React from 'react';
import classNames from 'classnames';
import { connect } from 'dva';
import { Popover, Select } from 'hzero-ui';
import { isEmpty, uniqBy } from 'lodash';

import intl from 'utils/intl';
import { openMenu } from '../../../components/DefaultMenu/utils';
import { defaultGetClassName } from './utils';
import { useHistory, useSearch } from './hooks';
import SideHistory from './History';
// import { DEBOUNCE_TIME } from 'utils/constants';

// import { useDebounceState } from '../../hooks';

const SideHeaderSearch = ({
  getClassName = defaultGetClassName,
  collapsed,
  menuLeafNode = [],
  components = {},
}) => {
  const History = components.History || SideHistory;
  const [searchData, searchValue, setSearchValue] = useSearch(menuLeafNode);
  // const [focus, setFocus] = useDebounceState(false, DEBOUNCE_TIME, []);
  const [focus, setFocus] = React.useState(false);
  const [history, setHistory] = useHistory();
  const ownHistory = React.useMemo(
    () => history.filter((tab) => (tab.name ? intl.get(tab.name) : '')),
    [history]
  );
  React.useEffect(() => {
    if (menuLeafNode.length > 0) {
      const newHistory = history.filter((i) => {
        return menuLeafNode.find((j) => j.id === i.id);
      });
      setHistory(newHistory);
    }
  }, [menuLeafNode]);
  const handleBlur = React.useCallback(() => {
    setSearchValue();
    setFocus(false);
  }, [setSearchValue, setFocus]);
  const handleFocus = React.useCallback(() => {
    setSearchValue();
    setFocus(true);
  }, [setSearchValue, setFocus]);
  const handleSearch = React.useCallback(
    (value) => {
      setSearchValue(value);
    },
    [setSearchValue]
  );
  const handleSelect = React.useCallback(
    (value) => {
      // 必定选中 且只能选中一个
      const selectMenu = menuLeafNode.filter((item) => item.id === value)[0];
      if (!isEmpty(selectMenu)) {
        const newTab = {
          icon: selectMenu.icon,
          name: selectMenu.name,
          path: selectMenu.path,
          type: selectMenu.type,
          search: selectMenu.search,
          id: selectMenu.id,
          key: selectMenu.path,
        };
        openMenu(newTab);
        const newHistory = uniqBy([newTab, ...history], (t) => t.key);
        if (newHistory.length > 8) {
          newHistory.pop();
        }
        setHistory(newHistory);
        setSearchValue();
      }
    },
    [menuLeafNode, history, setHistory, setSearchValue]
  );
  const handleHistoryClearBtnClick = React.useCallback(() => {
    setHistory([]);
  }, [setHistory]);
  const handleHistoryClearItem = React.useCallback(
    (tab) => {
      setHistory(history.filter((t) => t !== tab));
    },
    [history, setHistory]
  );
  const handleGotoHistory = React.useCallback(
    (tab) => {
      openMenu(tab);
      setHistory(uniqBy([tab, ...history], (t) => t.key));
    },
    [history, setHistory]
  );
  const selection = (
    <Select
      showSearch
      size="small"
      placeholder={intl.get('hzero.common.basicLayout.menuSelect').d('菜单搜索')}
      showArrow={false}
      filterOption={false}
      className={getClassName('input')}
      dropdownClassName={getClassName('select-wrap')}
      value={searchValue}
      onBlur={handleBlur}
      onFocus={handleFocus}
      onSearch={handleSearch}
      onSelect={handleSelect}
    >
      {searchData.map((item) => (
        <Select.Option key={item.id} value={item.id}>
          {item.title}
        </Select.Option>
      ))}
    </Select>
  );
  if (collapsed) {
    return (
      <Popover
        trigger="click"
        content={
          <div
            className={classNames(getClassName(), {
              [getClassName('focus')]: focus,
            })}
          >
            <span className={getClassName('icon')} />
            {selection}
          </div>
        }
        placement="right"
        overlayClassName={getClassName('popover')}
      >
        <span className={getClassName('icon')} />
      </Popover>
    );
  } else {
    return (
      <div
        className={classNames(getClassName(), {
          [getClassName('focus')]: focus,
        })}
      >
        <span className={getClassName('icon')} />
        {selection}
        <History
          history={ownHistory}
          onClearBtnClick={handleHistoryClearBtnClick}
          onClearItem={handleHistoryClearItem}
          onGotoHistory={handleGotoHistory}
        />
      </div>
    );
  }
};

export default connect(({ global = {} }) => ({
  menuLeafNode: global.menuLeafNode,
}))(SideHeaderSearch);
