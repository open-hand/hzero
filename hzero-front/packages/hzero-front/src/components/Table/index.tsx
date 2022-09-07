/**
 * 优化C7NPRO Table渲染
 * @date: 2020-05-13
 * @author: Nemo <yingbin.jiang@hand-china.com>
 * @version: 1.0.0
 * @copyright: Copyright (c) 2020, Hand
 */

import React, { Component } from 'react';
import { Table as C7NPROTable } from 'choerodon-ui/pro';
import { isFunction, isObject, isArray } from 'lodash';

/**
 *  深度比较两个对象是否相同, 函数每次都会重新创建(无论是否是同一个函数)，故props比较排除函数对比
 * @param {Object} oldData
 * @param {Object} newData
 */
function compareProps(oldData, newData, n = 0) {
  if (n > 8) {
    return oldData === newData;
  }
  // 类型为基本类型时,如果相同,则返回true
  if (oldData === newData) return true;

  // 如果是react原生属性，则跳过
  if (React.isValidElement(oldData) && React.isValidElement(newData)) {
    return true;
  }

  if (
    isObject(oldData) &&
    isObject(newData) &&
    Object.keys(oldData).length === Object.keys(newData).length
  ) {
    // 类型为对象并且元素个数相同
    // 遍历所有对象中所有属性,判断元素是否相同
    for (const key in oldData) {
      if (Object.prototype.hasOwnProperty.call(oldData, key)) {
        if (!compareProps(oldData[key], newData[key], n + 1)) {
          // 对象中具有不相同属性 返回false
          return false;
        }
      }
    }
  } else if (isArray(oldData) && isArray(oldData) && oldData.length === newData.length) {
    // 类型为数组并且数组长度相同
    // eslint-disable-next-line prefer-destructuring
    for (let i = 0, length = oldData.length; i < length; i++) {
      // 如果数组元素中具有不相同元素,返回false
      if (!compareProps(oldData[i], newData[i], n + 1)) {
        return false;
      }
    }
  } else if (isFunction(oldData) && isFunction(newData)) {
    return true;
  } else {
    // 其它类型,均返回false
    return false;
  }
  // 走到这里,说明数组或者对象中所有元素都相同,返回true
  return true;
}

class Table extends Component<any> {
  shouldComponentUpdate = (nextProps) => {
    if (compareProps(nextProps, this.props)) {
      return false;
    }
    return true;
  };

  ref: any;

  handleRef(r) {
    this.ref = r;
    if (this.props.tableRef) {
      if (typeof this.props.tableRef === 'function') {
        this.props.tableRef(this.ref);
      } else {
        this.props.tableRef.current = this.ref;
      }
    }
  }

  handleResize() {
    if (this.ref) {
      this.ref.handleResize();
    }
  }

  render() {
    const { dataSet, columns, ...otherProps } = this.props;
    return (
      <C7NPROTable
        ref={(r) => this.handleRef(r)}
        dataSet={dataSet}
        columns={columns}
        {...otherProps}
      />
    );
  }
}

export default (Table as any) as C7NPROTable;
