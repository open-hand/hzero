/**
 * index.js
 * @date 2018/9/30
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';

import Preview from './Preview';

export default class PagePreview extends React.Component {
  ref = {};

  state = {
    pageCode: '',
  };

  constructor(props) {
    super(props);
    this.handleRefresh = this.handleRefresh.bind(this);
    this.state = {
      pageCode: this.getPageCode(),
    };
  }

  render() {
    const { pageCode } = this.state;
    return (
      <React.Fragment>
        <Preview pageCode={pageCode} onRefresh={this.handleRefresh} />
      </React.Fragment>
    );
  }

  getPageCode() {
    const {
      match: {
        params: { pageCode },
      },
    } = this.props;
    return pageCode;
  }

  /**
   * reset pageCode from router
   */
  handleRefresh() {
    this.setState({
      pageCode: this.getPageCode(),
    });
  }
}
