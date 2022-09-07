import React from 'react';
import { Icon, List, Popover } from 'hzero-ui';
import { isFunction } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import { cleanCache } from 'components/CacheComponent';
import Icons from 'components/Icons';

import intl from 'utils/intl';
import { isPromise } from 'utils/utils';
import {
  closeTab,
  getBeforeMenuTabRemove,
  getTabFromKey,
  menuTabEventManager,
  openTab,
} from 'utils/menuTab';

class DefaultMenuTabs extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      contextMenuVisibleKey: '', // 显示的右键菜单对应的 tab
      contextMenuVisible: false, // 是否显示 右键菜单
    };
  }

  /**
   * 关闭 tab
   */
  remove(targetKey) {
    const onBeforeHandler = getBeforeMenuTabRemove(targetKey);
    if (isFunction(onBeforeHandler)) {
      const isShouldDelete = onBeforeHandler();
      if (isPromise(isShouldDelete)) {
        isShouldDelete.then(
          // 关闭tab
          () => {
            cleanCache(targetKey);
            closeTab(targetKey);
          }
        );
      } else if (isShouldDelete !== false) {
        cleanCache(targetKey);
        closeTab(targetKey);
      }
    } else {
      cleanCache(targetKey);
      closeTab(targetKey);
    }
  }

  /**
   * 清除 contextMenu 的信息
   */
  clearContextMenuVisible() {
    this.setState({
      contextMenuVisibleKey: '',
      contextMenuVisible: false,
    });
  }

  /**
   * 在 render 时 使用了 arrow function, 所以不需要 @Bind()
   * 触发右键菜单的事件
   * @param {Object} pane - tab
   * @param {Boolean} visible - 显示或隐藏
   */
  handleContextMenuTrigger(pane, visible) {
    this.setState({
      contextMenuVisibleKey: pane.key,
      contextMenuVisible: visible,
    });
  }

  /**
   * 关闭除了当前 tab 的 所有可关闭的tab, 如果新的 tabs 不包含 active tab 则 打开当前 tab
   * @param {object} pane - menuTab 数据
   */
  @Bind()
  handleMenuTabCloseOthers(pane, e) {
    e.stopPropagation();
    const { removeOtherMenuTab, tabs = [], activeTabKey } = this.props;
    const removeTabs = tabs.filter((t) => t.key !== pane.key && t.closable);
    this.clearContextMenuVisible();
    removeOtherMenuTab({ tab: pane }).then((nextActiveTabKey) => {
      if (nextActiveTabKey !== activeTabKey) {
        openTab(getTabFromKey(nextActiveTabKey));
      }
      removeTabs.forEach((t) => {
        cleanCache(t.key);
        menuTabEventManager.emit('close', { tabKey: t.key });
      });
    });
  }

  /**
   * 关闭除了当前 tab 的 所有可关闭的tab, 如果新的 tabs 不包含 active tab 则 打开当前 tab
   * @param {object} pane - menuTab 数据
   */
  @Bind()
  handleMenuTabCloseLeft(pane, e) {
    e.stopPropagation();
    const { removeSomeMenuTab, tabs = [], activeTabKey } = this.props;
    const removeTabs = tabs
      .slice(
        0,
        tabs.findIndex((t) => t.key === pane.key)
      )
      .filter((i) => i.closable)
      .map((j) => j.key);
    this.clearContextMenuVisible();
    removeSomeMenuTab({ removeTabs }).then(() => {
      if (pane.key !== activeTabKey) {
        openTab(getTabFromKey(pane.key));
      }
      removeTabs.forEach((t) => {
        cleanCache(t.key);
        menuTabEventManager.emit('close', { tabKey: t.key });
      });
    });
  }

  /**
   * 关闭除了当前 tab 的 所有可关闭的tab, 如果新的 tabs 不包含 active tab 则 打开当前 tab
   * @param {object} pane - menuTab 数据
   */
  @Bind()
  handleMenuTabCloseRight(pane, e) {
    e.stopPropagation();
    const { removeSomeMenuTab, tabs = [], activeTabKey } = this.props;
    const removeTabs = tabs
      .slice(tabs.findIndex((t) => t.key === pane.key) + 1)
      .filter((i) => i.closable)
      .map((j) => j.key);
    this.clearContextMenuVisible();
    removeSomeMenuTab({ removeTabs }).then(() => {
      if (pane.key !== activeTabKey) {
        openTab(getTabFromKey(pane.key));
      }
      removeTabs.forEach((t) => {
        cleanCache(t.key);
        menuTabEventManager.emit('close', { tabKey: t.key });
      });
    });
  }

  /**
   * 关闭所有可关闭的tab, 打开最靠近的 !closable 的tab
   * @param {object} pane - menuTab 数据
   */
  @Bind()
  handleMenuTabCloseAll(pane, e) {
    e.stopPropagation();
    const { removeAllMenuTab, tabs = [], activeTabKey } = this.props;
    const removeTabs = tabs.filter((t) => t.closable);
    this.clearContextMenuVisible();
    removeAllMenuTab({ tab: pane }).then((nextActiveTabKey) => {
      if (nextActiveTabKey !== activeTabKey) {
        openTab(getTabFromKey(nextActiveTabKey));
      }
      removeTabs.forEach((t) => {
        cleanCache(t.key);
        menuTabEventManager.emit('close', { tabKey: t.key });
      });
    });
  }

  /**
   * 刷新当前 tab
   * @param {object} pane - menuTab 数据
   */
  @Bind()
  handleMenuTabRefresh(pane, e) {
    const { refreshMenuTabsKeyMap = new Map(), updateRefreshMenuTabsKeyMap } = this.props;
    e.stopPropagation();
    this.clearContextMenuVisible();
    const refreshKeyMap = refreshMenuTabsKeyMap;
    refreshKeyMap.set(pane.key, uuid());
    updateRefreshMenuTabsKeyMap({
      refreshMenuTabsKeyMap: refreshKeyMap,
    });
    menuTabEventManager.emit('refresh', { tabKey: pane.key });
  }

  /**
   * 全屏当前 tab
   * @param {object} pane - menuTab 数据 page-container
   */
  @Bind()
  handleMenuTabFullScreen(pane, e) {
    e.stopPropagation();
    const { activeTabKey, menuLayout } = this.props;
    this.clearContextMenuVisible();

    if (!document.fullscreenElement) {
      if (menuLayout === 'horizontal') {
        const node = document.querySelector(`.hzero-layout`);
        node.classList.add('hzero-fullscreen');
      } else if (menuLayout === 'side') {
        const node = document.querySelector(`.hzero-side-layout-container`);
        node.classList.add('hzero-fullscreen');
      } else if (menuLayout === 'side-all') {
        const node = document.querySelector(`.hzero-common-layout-container`);
        node.classList.add('hzero-fullscreen');
      } else if (menuLayout === 'inline') {
        const node = document.querySelector(`.hzero-normal-container`);
        node.classList.add('hzero-fullscreen');
      } else {
        const node = document.querySelector(`#root`).firstElementChild;
        node.classList.add('hzero-fullscreen');
      }

      if (document.documentElement.requestFullscreen) {
        document.documentElement.requestFullscreen();
      } else if (document.documentElement.msRequestFullscreen) {
        document.documentElement.msRequestFullscreen();
      } else if (document.documentElement.mozRequestFullScreen) {
        document.documentElement.mozRequestFullScreen();
      } else if (document.documentElement.webkitRequestFullscreen) {
        document.documentElement.webkitRequestFullscreen();
      }
    }

    if (pane.key !== activeTabKey) {
      openTab(getTabFromKey(pane.key));
    }
  }

  render() {
    const {
      activeTabKey, // 当前激活的 Tab 的 key
      language, // 当前的语言
      tabs = [], // 所有打开的 tabs
      index,
      pane,
    } = this.props;

    const { contextMenuVisibleKey, contextMenuVisible = false } = this.state;

    return (
      <Popover
        overlayClassName="default-menu-tabs-context-menu"
        content={
          <List size="small">
            {(tabs.length > 2 || pane.key === '/workplace') && tabs.length !== 1 && (
              <List.Item
                onClick={(e) => {
                  this.handleMenuTabCloseOthers(pane, e);
                }}
                className="default-menu-tabs-context-menu-item"
              >
                <Icons
                  type="close-other"
                  size={14}
                  className="default-menu-tabs-context-menu-item-icon"
                />
                <span className="default-menu-tabs-context-menu-item-text">
                  {intl.get('hzero.common.button.closeOther').d('关闭其他')}
                </span>
              </List.Item>
            )}
            {pane.key !== '/workplace' && index !== 1 && (
              <List.Item
                onClick={(e) => {
                  this.handleMenuTabCloseLeft(pane, e);
                }}
                className="default-menu-tabs-context-menu-item"
              >
                <Icons
                  type="close-all"
                  size={14}
                  className="default-menu-tabs-context-menu-item-icon"
                />
                <span className="default-menu-tabs-context-menu-item-text">
                  {intl.get('hzero.common.button.closeLeft').d('关闭左侧')}
                </span>
              </List.Item>
            )}
            {tabs.length - 1 !== index && (
              <List.Item
                onClick={(e) => {
                  this.handleMenuTabCloseRight(pane, e);
                }}
                className="default-menu-tabs-context-menu-item"
              >
                <Icons
                  type="close-all"
                  size={14}
                  className="default-menu-tabs-context-menu-item-icon"
                />
                <span className="default-menu-tabs-context-menu-item-text">
                  {intl.get('hzero.common.button.closeRight').d('关闭右侧')}
                </span>
              </List.Item>
            )}
            {tabs.length !== 1 && (
              <List.Item
                onClick={(e) => {
                  this.handleMenuTabCloseAll(pane, e);
                }}
                className="default-menu-tabs-context-menu-item"
              >
                <Icons
                  type="close-all"
                  size={14}
                  className="default-menu-tabs-context-menu-item-icon"
                />
                <span className="default-menu-tabs-context-menu-item-text">
                  {intl.get('hzero.common.button.closeAll').d('关闭全部')}
                </span>
              </List.Item>
            )}
            <List.Item
              onClick={(e) => {
                this.handleMenuTabFullScreen(pane, e);
              }}
              className="default-menu-tabs-context-menu-item"
            >
              <Icons
                type="full-screen"
                size={14}
                className="default-menu-tabs-context-menu-item-icon"
                // style={{ lineHeight: 'inherit', fontSize: '14px' }}
              />
              <span className="default-menu-tabs-context-menu-item-text">
                {intl.get('hzero.common.button.fullScreen').d('全屏')}
              </span>
            </List.Item>
            {pane.key === activeTabKey && (
              <List.Item
                onClick={(e) => {
                  this.handleMenuTabRefresh(pane, e);
                }}
                className="default-menu-tabs-context-menu-item"
              >
                <Icons
                  type="refresh"
                  size={14}
                  className="default-menu-tabs-context-menu-item-icon"
                />
                <span className="default-menu-tabs-context-menu-item-text">
                  {intl.get('hzero.common.button.refresh').d('刷新')}
                </span>
              </List.Item>
            )}
          </List>
        }
        title={null}
        trigger="contextMenu"
        visible={pane.key === contextMenuVisibleKey && contextMenuVisible}
        onVisibleChange={(visible) => {
          this.handleContextMenuTrigger(pane, visible);
        }}
      >
        {pane.path === '/workplace' ? (
          <span>
            <Icon type={pane.icon} key="icon" />
            {language ? pane.title && intl.get(pane.title).d(pane.title) : '...'}
          </span>
        ) : (
          <span>{language ? pane.title && intl.get(pane.title).d(pane.title) : '...'}</span>
        )}
      </Popover>
    );
  }
}

export default DefaultMenuTabs;
