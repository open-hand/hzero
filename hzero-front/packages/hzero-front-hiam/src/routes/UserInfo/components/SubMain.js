/**
 * SubMain.js
 * @date 2018/11/23
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';

import styles from '../index.less';

export default class SubMain extends React.Component {
  render() {
    const { children, className, title, ...other } = this.props;
    let subMainClassName = styles['sub-main'];
    if (className) {
      subMainClassName += ` ${className}`;
    }
    return (
      <div className={subMainClassName} {...other}>
        <h4>{title}</h4>
        {children}
      </div>
    );
  }
}
