/**
 * @date 2019-11-27
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 * 描述组件展示只读字段
 */
import React from 'react';

import styles from './description.less';

export default class Description extends React.PureComponent {
  render() {
    const { label, children, mode = 'str' } = this.props;
    return (
      <div className={styles['description-container']}>
        <span className={styles['description-title']}>{label}</span>
        {mode === 'str' ? <p className={styles['description-content']}>{children}</p> : children}
      </div>
    );
  }
}
