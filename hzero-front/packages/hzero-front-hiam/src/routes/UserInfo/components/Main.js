/**
 * Main.js
 * @date 2018/11/23
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';

import styles from '../index.less';

export default class Main extends React.Component {
  render() {
    const { children, className, title, ...other } = this.props;
    let mainClassName = styles.main;
    if (className) {
      mainClassName += ` ${className}`;
    }
    return (
      <div className={mainClassName} {...other}>
        <h3 className={styles['main-title']}>{title}</h3>
        {children}
      </div>
    );
  }
}
