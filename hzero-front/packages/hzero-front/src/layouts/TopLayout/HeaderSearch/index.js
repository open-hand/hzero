/**
 * 搜索框
 */
import React from 'react';
import { Select } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, uniqBy } from 'lodash';

import intl from 'utils/intl';

import { openMenu } from '../../components/DefaultMenu/utils';
import { loadHistory, storeHistory } from '../../components/DefaultHeaderSearch/utils';

import styles from './styles.less';

function mapHistoryToSearchMenu(history = []) {
  return [...history].map((hs) => ({
    icon: hs.icon,
    name: hs.name,
    path: hs.path,
    type: hs.type,
    search: hs.search,
    id: hs.id,
  }));
}

/**
 * 阻止冒泡事件, 防止搜索框影响到 一级菜单切换
 */
function stopPropagationEvent(e) {
  e.stopPropagation();
}

class DefaultHeaderSearch extends React.Component {
  state = {
    data: [], // 根据输入框过滤出符合的值
    value: undefined, // 搜索框中的值
    history: loadHistory(),
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    const { menuLeafNode: prevMenuLeafNode = [], history } = prevState;
    const { menuLeafNode = [] } = nextProps;
    if (menuLeafNode.length > 0 && prevMenuLeafNode.length === 0) {
      const newHistory = history.filter((i) => {
        return menuLeafNode.find((j) => j.id === i.id);
      });
      storeHistory(newHistory);
      return { history: newHistory, menuLeafNode };
    }
    return null;
  }

  /**
   * 通过搜索历史 跳转页面
   * @param {object} tab - tab 信息
   */
  @Bind()
  handleGotoHistory(tab) {
    openMenu(tab);
    this.addHistory(tab);
  }

  /**
   * 新增一个历史记录
   * @param {object} tab - tab 信息
   */
  addHistory(tab) {
    const { history = [] } = this.state;
    const newHistory = uniqBy([tab, ...history], (t) => t.key);
    if (newHistory.length > 8) {
      newHistory.pop();
    }
    storeHistory(history);
    this.setState({
      history: newHistory,
    });
  }

  componentWillUnmount() {
    const { history = [] } = this.state;
    storeHistory(history);
  }

  /**
   * 文本框变化时回调
   * @param {string} searchValue - 填写的标题
   */
  @Bind()
  handleSearch(searchValue) {
    const { menuLeafNode } = this.props;
    let newData = [];
    const value = searchValue ? searchValue.trim() : '';
    if (!isEmpty(value)) {
      newData = menuLeafNode.filter((item) => {
        if (item.title.toLowerCase().includes(value.toLowerCase())) {
          return true;
        }
        const quickIndex = item && item.quickIndex && item.quickIndex.toLowerCase();
        if (quickIndex) {
          return quickIndex.startsWith(value.toLowerCase());
        }
        return false;
      });
    }
    this.setState({
      data: newData,
    });
  }

  /**
   * 下拉框选择值时变化
   * @param {string} value - 选中菜单的路径
   */
  @Bind()
  handleSelect(value) {
    const { menuLeafNode } = this.props;
    const { history = [] } = this.state;
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
      };
      openMenu(newTab);
      this.addHistory(newTab);
      this.setState({
        data: mapHistoryToSearchMenu(history),
        value: undefined,
      });
    }
  }

  /**
   * 获取焦点
   */
  @Bind()
  handleFocus() {
    const { history = [] } = this.state;
    this.setState({
      focus: true,
      data: mapHistoryToSearchMenu(history),
    });
  }

  /**
   * 失去焦点
   */
  @Bind()
  handleBlur() {
    this.setState({
      focus: false,
      data: [],
      value: undefined,
    });
  }

  render() {
    const { className = '', collapsed } = this.props;
    const { data = [], value, focus } = this.state;
    const options =
      data &&
      // 去除非当前用户/角色/租户 的菜单
      data
        .filter((tab) => (tab.name ? intl.get(tab.name) : ''))
        .map((d) => (
          <Select.Option key={d.id} value={d.id}>
            {intl.get(d.name)}
          </Select.Option>
        ));
    const wrapClassNames = [className, styles.search];
    const iconClassNames = [styles.icon];
    if (collapsed) {
      wrapClassNames.push(styles.collapsed);
    }
    if (focus) {
      wrapClassNames.push(styles.focus);
      iconClassNames.push(styles.active);
    }
    return (
      <div className={wrapClassNames.join(' ')} onKeyDown={stopPropagationEvent}>
        <span className={iconClassNames.join(' ')} />
        <Select
          showSearch
          size="small"
          placeholder={intl.get('hzero.common.basicLayout.menuSelect').d('菜单搜索')}
          value={value}
          showArrow={false}
          className={styles.input}
          filterOption={false}
          onBlur={this.handleBlur}
          onFocus={this.handleFocus}
          onSearch={this.handleSearch}
          onSelect={this.handleSelect}
        >
          {options}
        </Select>
      </div>
    );
  }
}

export default connect(({ global = {} }) => ({
  menuLeafNode: global.menuLeafNode,
}))(DefaultHeaderSearch);
