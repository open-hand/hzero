/**
 * LineItem.js
 * @date 2018/11/26
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { isUndefined } from 'lodash';

import styles from '../index.less';

export default class LineItem extends React.Component {
  render() {
    const { label, itemLabel, content, btns, className, ...other } = this.props;
    let itemClassName = styles['line-item'];
    if (className) {
      itemClassName += ` ${className}`;
    }
    let labelElement = null;
    if (isUndefined(itemLabel)) {
      labelElement = <div className={styles['line-item-label']}>{label}</div>;
    } else {
      labelElement = itemLabel;
    }

    return (
      <div className={itemClassName} {...other}>
        {labelElement}
        <div className={styles['line-item-content']}>{content}</div>
        <div className={styles['line-item-btns']}>{btns}</div>
      </div>
    );
  }
}
