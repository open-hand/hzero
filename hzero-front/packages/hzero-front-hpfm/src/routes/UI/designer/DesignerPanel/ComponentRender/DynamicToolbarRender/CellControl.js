/**
 * CellControl.js
 * @author WY
 * @date 2018/10/9
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { isFunction } from 'lodash';
import { Popconfirm } from 'hzero-ui';

import intl from 'utils/intl';

import { emptyFieldType } from '../../../config';
import styles from '../../index.less';

export default class CellControl extends React.Component {
  constructor(props) {
    super(props);
    this.handleRemoveTrigger = this.handleRemoveTrigger.bind(this);
  }

  render() {
    const { isOverAndCanDrop, component, currentEditField, children } = this.props;

    const cellControlClassName = [styles['cell-control']];
    const isNoEmptyCell = component.componentType !== emptyFieldType;
    const isCurrentEditField = currentEditField === component;

    if (!isNoEmptyCell) {
      cellControlClassName.push(styles['cell-control-placeholder']);
    }

    if (isOverAndCanDrop) {
      cellControlClassName.push(styles['cell-control-over']);
    }

    if (isCurrentEditField) {
      cellControlClassName.push(styles['cell-control-active']);
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

  /**
   * remove self
   */
  handleRemoveTrigger() {
    const { onRemoveField, pComponent, component, fieldOptions } = this.props;
    if (isFunction(onRemoveField)) {
      onRemoveField(pComponent, component, fieldOptions);
    }
  }
}
