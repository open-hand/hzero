/* eslint-disable react/require-default-props */
/**
 * MaxLenItem.js
 * @date 2018/11/26
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Icon } from 'hzero-ui';

import styles from '../index.less';

export default class MaxLenItem extends React.Component {
  static defaultProps = {
    comment: '',
    content: '',
  };

  static propTypes = {
    itemIcon: PropTypes.any,
    icon: PropTypes.any,
    iconType: PropTypes.string,
    description: PropTypes.any.isRequired,
    content: PropTypes.any,
    comment: PropTypes.any,
    btns: PropTypes.array.isRequired,
  };

  state = {};

  render() {
    const {
      className,
      content,
      description,
      descriptions,
      comment,
      btns = [],
      itemIcon,
      icon,
      iconType,
      maxLen,
      style,
      ...other
    } = this.props;
    const iconItem = icon || <Icon type={iconType} />;
    let itemClassName = styles['max-len-item'];
    if (className) {
      itemClassName += ` ${className}`;
    }
    const iconElement =
      itemIcon === undefined ? (
        <div className={styles['max-len-item-icon']}>{iconItem}</div>
      ) : (
        itemIcon
      );
    return (
      <div className={itemClassName} {...other}>
        {iconElement}
        <div className={styles['max-len-item-content-wrapper']}>
          <div className={styles['max-len-item-main-content-wrapper']}>
            {descriptions ? (
              <div className={styles['max-len-item-descriptions']}>{descriptions}</div>
            ) : (
              <div className={styles['max-len-item-description']}>{description}</div>
            )}
            <div className={styles['max-len-item-content']}>{content}</div>
          </div>
          <div className={styles['max-len-item-comment']}>{comment}</div>
        </div>
        <div className={styles['max-len-item-btns']}>{btns}</div>
      </div>
    );
  }
}
