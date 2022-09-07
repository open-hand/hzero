/**
 * @file index.js
 * @description 将 activeField swapField addField(Field) 给 wrapperFieldComponent 调用
 * removeField 由 CellControl 调用
 * @author WY
 * @date 2018/10/11
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { forEach, isNaN, map, isFunction } from 'lodash';
import { Pane, SortablePane } from 'react-sortable-pane';
import { Pagination } from 'hzero-ui';

import intl from 'utils/intl';
import { totalRender } from 'utils/renderer';

import { autoSizeWidth } from '../../../config';
import DrawDragComponent from '../../Drag/DrawDragComponent';
import DropField from '../../Drop/DropField';
import Col from './Col';

import styles from '../../index.less';

// 一次最大能增加的长度
const maxFieldWidth = 500;
const fieldHeight = 46;
const resizeLayoutHeight = fieldHeight + 2;
const noResizable = {
  resizable: { x: false, y: false, xy: false },
};

const defaultPagination = {
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100'],
  total: 100,
  showTotal: totalRender,
};

const dynamicTableStyleWithNoPagination = { height: 166 };
const dynamicTableStyleWithPagination = { height: 200 };

// TableRender 可以直接拖进 新增的字段

export default class DynamicTableRender extends React.Component {
  state = {
    originKeyMapIndex: {}, // 字段原始的顺序 key 对应的 index
    // prevRenderKey: '', // 原来的 render key
    orderKeys: [], // 字段的顺序
    originWidth: 0, // resize field 开始时的宽度
    // resizingWidth: 0, // field resizing 时的宽度
    paginationProps: { ...defaultPagination }, // 分页的信息
  };

  constructor(props) {
    super(props);
    this.wrapperFieldComponent = this.wrapperFieldComponent.bind(this);

    this.orderChange = this.orderChange.bind(this);
    this.changeViewWidthStart = this.changeViewWidthStart.bind(this);
    this.changeViewWidth = this.changeViewWidth.bind(this);
    this.changeViewWidthStop = this.changeViewWidthStop.bind(this);

    this.getSortableResizeConfig = this.getSortableResizeConfig.bind(this);
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { component } = nextProps;
    const { prevRenderKey } = prevState;
    if (component && prevRenderKey !== component.renderKey) {
      const originKeyMapIndex = {};
      const orderKeys = map(component.fields, (field, index) => {
        originKeyMapIndex[field.fieldId] = index;
        return `${field.fieldId}`;
      });
      return {
        prevRenderKey: component.renderKey,
        orderKeys,
        originKeyMapIndex,
      };
    }
    return null;
  }

  sortableResize = {
    grid: [5],
    resizable: {
      x: true,
    },
    minWidth: 60,
    maxWidth: 200,
  };

  render() {
    const {
      component,
      onRemoveField,
      onAddField,
      onAddComponent,
      onActiveComponent,
      onSwapComponent,
      currentEditComponent,
      currentEditField,
    } = this.props;
    const { orderKeys, originKeyMapIndex } = this.state;
    const panes = [];
    const fieldWidths = new Array(orderKeys.length);
    let totalWidth = 0;
    // field-width
    forEach(orderKeys, key => {
      const field = component.fields[originKeyMapIndex[key]];
      const isAutoSize = +field.width === 0;
      const width = isAutoSize ? autoSizeWidth : +field.width; // field is autoSize when width is 0/'0'
      totalWidth += isAutoSize ? autoSizeWidth : +field.width;
      fieldWidths.push(
        <td
          style={{
            width,
            minWidth: width,
          }}
          align="right"
          className={styles['dynamic-table-content-field-width-item']}
          key={field.fieldId}
        >
          <span>{isAutoSize ? 'auto' : width}</span>
        </td>
      );
    });
    // field
    forEach(component.fields, field => {
      const isAutoSize = +field.width === 0;
      panes.push(
        <Pane
          {...this.sortableResize}
          {...(isAutoSize ? noResizable : {})}
          size={{
            width: isAutoSize ? autoSizeWidth : +field.width,
            height: fieldHeight,
          }}
          key={field.fieldId}
          maxWidth={maxFieldWidth}
          className={styles['dynamic-table-content-field-item']}
        >
          <Col
            onRemoveField={onRemoveField}
            pComponent={component}
            component={field}
            wrapperFieldComponent={this.wrapperFieldComponent}
            currentEditField={currentEditField}
          />
        </Pane>
      );
    });
    // pagination
    const noPagination = component.pagination === false;
    const { paginationProps } = this.state;
    const currentPaginationProps = paginationProps;
    if (component.pagination) {
      // custom
      paginationProps.pageSize = component.defaultPageSize;
    }
    return (
      <div
        className={styles['dynamic-table']}
        style={noPagination ? dynamicTableStyleWithNoPagination : dynamicTableStyleWithPagination}
      >
        <DrawDragComponent
          component={component}
          onAddField={onAddField}
          onAddComponent={onAddComponent}
          onActiveComponent={onActiveComponent}
          onSwapComponent={onSwapComponent}
          currentEditComponent={currentEditComponent}
          currentEditField={currentEditField}
          wrapperFieldComponent={this.wrapperFieldComponent}
        >
          <div className={styles['dynamic-table-content']}>
            <table className={styles['dynamic-table-content-field-width']}>
              <tbody>
                <tr className={styles['dynamic-table-content-field-width-wrap']}>
                  {fieldWidths}
                  <td key="placeholder" />
                </tr>
                <tr className={styles['dynamic-table-content-field-width-rule-bg']} />
              </tbody>
            </table>
            <SortablePane
              {...this.getSortableResizeConfig()}
              order={orderKeys}
              className={styles['dynamic-table-content-field']}
              style={{
                width: totalWidth,
                height: resizeLayoutHeight,
                minWidth: '100%',
              }}
            >
              {panes}
            </SortablePane>
          </div>
          <div className={styles['dynamic-table-data']}>
            {intl.get('hpfm.ui.dynamicTable.data').d('数据区域')}
          </div>
          {noPagination ? null : (
            <div className={styles['dynamic-table-pagination']}>
              <Pagination {...currentPaginationProps} />
            </div>
          )}
        </DrawDragComponent>
      </div>
    );
  }

  componentDidMount() {
    const { refRender, component } = this.props;
    if (isFunction(refRender)) {
      refRender(component.renderKey, this);
    }
  }

  orderChange(orderKeys) {
    this.setState({
      orderKeys,
    });
  }

  changeViewWidthStart(event, key) {
    const { component } = this.props;
    const { originKeyMapIndex } = this.state;
    const { width } = component.fields[originKeyMapIndex[key]];
    this.setState({
      originWidth: +width,
    });
  }

  changeViewWidth(event, key, dir, elementRef, delta) {
    const { component } = this.props;
    const { originKeyMapIndex } = this.state;
    const { originWidth } = this.state;
    const { width: moveWidth = 0 } = delta;
    component.fields[originKeyMapIndex[key]].width =
      originWidth + (isNaN(moveWidth) ? 0 : moveWidth);
    // 需要更新组件
    // todo remove this after complete
    this.forceUpdate();
  }

  changeViewWidthStop() {
    this.setState({
      originWidth: 0,
    });
  }

  getSortableResizeConfig() {
    if (!this.sortableResizeConfig) {
      this.sortableResizeConfig = {
        onResizeStart: this.changeViewWidthStart,
        onResize: this.changeViewWidth,
        onResizeStop: this.changeViewWidthStop,
        onOrderChange: this.orderChange,
        style: {
          height: resizeLayoutHeight,
        },
      };
    }
    return this.sortableResizeConfig;
  }

  wrapperFieldComponent(fieldElement, field, fieldOptions) {
    const { component, onAddField, onActiveField, onSwapField, currentEditField } = this.props;
    return (
      <DropField
        pComponent={component}
        component={field}
        onSwapField={onSwapField}
        onAddField={onAddField}
        onActiveField={onActiveField}
        fieldOptions={fieldOptions}
        currentEditField={currentEditField}
      >
        {fieldElement}
      </DropField>
    );
  }
}
