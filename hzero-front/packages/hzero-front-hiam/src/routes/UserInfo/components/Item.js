/**
 * Item.js
 * @date 2018/11/23
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';

import styles from '../index.less';

export default class Item extends React.Component {
  render() {
    const { label, children, className, ...other } = this.props;
    let itemClassName = styles.item;
    if (className) {
      itemClassName += ` ${className}`;
    }
    return (
      <div className={itemClassName} {...other}>
        <span className={styles['item-label']}>{label}</span>
        <span className={styles['item-content']}>{children}</span>
      </div>
    );
  }
}
