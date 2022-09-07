/**
 * Content.js
 * @date 2018/11/23
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';

import styles from '../index.less';

export default class Content extends React.Component {
  render() {
    const { children, className, ...other } = this.props;
    let contentClassName = styles.content;
    if (className) {
      contentClassName += ` ${className}`;
    }
    return (
      <div className={contentClassName} {...other}>
        {children}
      </div>
    );
  }
}
