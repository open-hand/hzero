/**
 * Col.js
 * @author WY
 * @date 2018/10/10
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Button } from 'hzero-ui';

import { fieldLabelProp } from '../../../config';

import CellControl from './CellControl';

import styles from '../../index.less';

const componentMap = {
  Button,
};

export default class Col extends React.Component {
  render() {
    const {
      component,
      pComponent,
      currentEditField,
      wrapperFieldComponent,
      colOptions,
      onRemoveField,
    } = this.props;
    const ComponentType = componentMap[component.componentType] || Button;
    const props = {
      type: component.type,
    };
    return (
      <div className={styles['dynamic-toolbar-item']} style={component.style || {}}>
        {wrapperFieldComponent(
          <CellControl
            component={component}
            pComponent={pComponent}
            currentEditField={currentEditField}
            fieldOptions={colOptions}
            onRemoveField={onRemoveField}
          >
            {React.createElement(ComponentType, props, component[fieldLabelProp])}
          </CellControl>,
          component,
          colOptions
        )}
      </div>
    );
  }
}
