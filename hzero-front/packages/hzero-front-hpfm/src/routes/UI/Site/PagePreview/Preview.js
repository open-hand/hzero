/**
 * Preview.js
 * should change key if pageCode hasChange
 * warn update immute after set config for load props's like 'this.refXXX'
 * @date 2018/10/10
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';
import { isFunction } from 'lodash';

import { Button /* , Spin */ } from 'hzero-ui';

// import { getResponse } from 'utils/utils';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import DynamicPage from 'components/DynamicComponent/DynamicPage';
import ValueList from 'components/ValueList';

import styles from './index.less';

let id = 0;

const resolutionMap = {
  '0': {
    style: {
      width: 800,
      height: 600,
      overflow: 'auto',
    },
  },
  '1': {
    style: {
      width: 1024,
      height: 768,
      overflow: 'auto',
    },
  },
  '2': {
    style: {
      width: 1366,
      height: 768,
      overflow: 'auto',
    },
  },
  '3': {
    style: {
      width: 1920,
      height: 1080,
      overflow: 'auto',
    },
  },
};

const resolutionOptions = [
  {
    value: '0',
    meaning: '800x600',
  },
  {
    value: '1',
    meaning: '1024x768',
  },
  {
    value: '2',
    meaning: '1366x768',
  },
  {
    value: '3',
    meaning: '1920x1080',
  },
];

export default class Preview extends React.Component {
  state = {
    // fetching: false,
    dynamicPageId: id,
    resolutionKey: '2',
  };

  constructor(props) {
    super(props);
    this.handleRefreshBtnClick = this.handleRefreshBtnClick.bind(this);
    this.resolutionChange = this.resolutionChange.bind(this);
  }

  render() {
    const {
      resolutionKey,
      dynamicPageId,
      // fetching,
    } = this.state;
    // TODO Spin will destroy layout, so what todo
    return (
      <React.Fragment>
        <Header title={intl.get('hzero.common.button.preview').d('预览')}>
          <Button type="primary" onClick={this.handleRefreshBtnClick} style={{ marginRight: 10 }}>
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </Button>
          <ValueList
            options={resolutionOptions}
            value={resolutionKey}
            onChange={this.resolutionChange}
            style={{ width: 120 }}
          />
        </Header>
        <Content>
          <div className={styles.preview} {...resolutionMap[resolutionKey]}>
            <DynamicPage key={dynamicPageId} pageCode={this.getPageCode()} />
          </div>
        </Content>
      </React.Fragment>
    );
  }

  componentDidUpdate() {
    // const { needResetToLoadDynamicRef } = this.state;
    // if (needResetToLoadDynamicRef) {
    //   this.reRender();
    // }
  }

  componentDidMount() {}

  getPageCode() {
    const { pageCode } = this.props;
    return pageCode;
  }

  handleRefreshBtnClick(e) {
    e.preventDefault();
    const { onRefresh } = this.props;
    if (isFunction(onRefresh)) {
      onRefresh();
    }
    this.reRender();
  }

  resolutionChange(resolutionKey) {
    this.setState({
      resolutionKey,
    });
  }

  /**
   * for load props's like 'this.refXXX'
   */
  reRender() {
    this.setState({
      dynamicPageId: id++,
    });
  }
}
