/**
 * CellControl.js
 * @author WY
 * @date 2018/10/11
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Popconfirm } from 'hzero-ui';
import { isFunction } from 'lodash';

import intl from 'utils/intl';

import styles from '../../index.less';

export default class CellControl extends React.Component {
  constructor(props) {
    super(props);
    this.handleRemoveTrigger = this.handleRemoveTrigger.bind(this);
  }

  render() {
    const { currentEditField, isOverAndCanDrop, component, children } = this.props;
    const cellControlClassName = [styles['cell-control']];
    const isCurrentEditField = currentEditField === component;

    if (isCurrentEditField) {
      cellControlClassName.push(styles['cell-control-active']);
    }

    if (isOverAndCanDrop) {
      cellControlClassName.push(styles['cell-control-over']);
    }

    return (
      <div className={cellControlClassName.join(' ')}>
        <div className={styles['cell-control-component']}>{children}</div>
        <i className={styles['cell-control-remove']}>
          <Popconfirm
            onConfirm={this.handleRemoveTrigger}
            title={intl.get('hpfm.ui.message.field.removeTitle').d('是否确认删除')}
          >
            <i className="anticon" />
          </Popconfirm>
        </i>
      </div>
    );
  }

  handleRemoveTrigger(e) {
    e.stopPropagation();
    const { onRemoveField, pComponent, component } = this.props;
    if (isFunction(onRemoveField)) {
      onRemoveField(pComponent, component);
    }
  }
}
