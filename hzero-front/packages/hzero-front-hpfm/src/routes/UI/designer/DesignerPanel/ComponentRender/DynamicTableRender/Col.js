/**
 * Col.js
 * @author WY
 * @date 2018/10/11
 * @email yang.wang06@hand-china.com
 */

import React from 'react';

import CellControl from './CellControl';
import { fieldLabelProp } from '../../../config';

// warn use <span>&nbsp;</span> to fill empty text

export default class Col extends React.Component {
  render() {
    const {
      wrapperFieldComponent,
      component,
      pComponent,
      onRemoveField,
      currentEditField,
    } = this.props;
    return wrapperFieldComponent(
      <CellControl
        pComponent={pComponent}
        component={component}
        onRemoveField={onRemoveField}
        currentEditField={currentEditField}
      >
        {component[fieldLabelProp] || <span>&nbsp;</span>}
      </CellControl>,
      component
    );
  }
}
