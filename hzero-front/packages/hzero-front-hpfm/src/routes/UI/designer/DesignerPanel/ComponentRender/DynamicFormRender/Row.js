/**
 * Row
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/10
 */
import React from 'react';
import { map, isNumber, forEach } from 'lodash';
import { Row as HzeroRow } from 'hzero-ui';

import Col from './Col';

export default class Row extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      willRemovePositionY: -1,
    };
    this.handleUpdateWillRemovePositionY = this.handleUpdateWillRemovePositionY.bind(this);
  }

  render() {
    const {
      cols = [],
      rowCount,
      component,
      onRemoveField,
      wrapperFieldComponent,
      colLayout,
      fieldLabelWidth,
      rowIndex,
      currentEditField,
      onUpdateComponent,
      willRemovePositionX,
      positionY,
      onUpdateWillRemovePositionX,
      onRefreshTemplate,
    } = this.props;
    const { willRemovePositionY } = this.state;
    let colCount = 0;
    forEach(cols, c => {
      if (c.colspan && isNumber(c.colspan)) {
        colCount += c.colspan;
      } else {
        colCount += 1;
      }
    });

    let positionX = 0;
    const colElements = map(cols, (c, colIndex) => {
      let positionRightX = positionX;
      if (c.colspan && isNumber(c.colspan)) {
        positionRightX += c.colspan;
      } else {
        positionRightX += 1;
      }
      const colElement = (
        <Col
          key={colIndex}
          rowIndex={rowIndex}
          colIndex={colIndex}
          rowCount={rowCount}
          colCount={colCount}
          component={c}
          pComponent={component}
          onRemoveField={onRemoveField}
          wrapperFieldComponent={wrapperFieldComponent}
          colLayout={colLayout}
          fieldLabelWidth={fieldLabelWidth}
          currentEditField={currentEditField}
          onUpdateComponent={onUpdateComponent}
          // 删除
          willRemovePositionY={willRemovePositionY}
          willRemovePositionX={willRemovePositionX}
          positionY={positionY}
          positionX={positionX}
          positionRightX={positionRightX}
          onUpdateWillRemovePositionX={onUpdateWillRemovePositionX}
          onUpdateWillRemovePositionY={this.handleUpdateWillRemovePositionY}
          onRefreshTemplate={onRefreshTemplate}
        />
      );
      positionX = positionRightX;
      return colElement;
    });

    return <HzeroRow type="flex">{colElements}</HzeroRow>;
  }

  handleUpdateWillRemovePositionY(willRemovePositionY = -1) {
    this.setState({
      willRemovePositionY,
    });
  }
}

if (process.env.NODE_ENV !== 'production') {
  Row.displayName = 'DynamicForm(Row)';
}
