/* eslint-disable react/require-default-props */

/**
 * LowCode
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019/9/16
 * @copyright 2019/9/16 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';

import LowCodeProvider from 'components/LowCodeProvider';

export default class LowCode extends Component {
  static propTypes = {
    code: PropTypes.string,
    type: PropTypes.string,
    params: PropTypes.object,
    id: PropTypes.string,
    onLoad: PropTypes.func,
  };

  render() {
    const { code, type = 'form', params = {}, id, onLoad } = this.props;
    return (
      <LowCodeProvider
        code={code}
        type={type}
        defaultParams={{ ...params, key: id }}
        onLoad={onLoad}
      />
    );
  }
}
