/**
 * EditableRow - 角色管理分配用户明细页面 - 行内编辑行高阶组件
 * @date: 2018-10-26
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form } from 'hzero-ui';
import { omit } from 'lodash';

/**
 * EditableRow - 送货单创建明细页面 - 行内编辑行高阶组件
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {object} contextProvider - Context.Provider
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class EditableRow extends Component {
  /**
   *
   *
   * @memberof EditableRow
   */
  componentDidMount() {
    const { onRef = e => e } = this.props;
    onRef(this);
  }

  render() {
    const { contextProvider, form, ...others } = this.props;
    const WrapperContextProvider = contextProvider;
    return (
      <WrapperContextProvider value={form}>
        <tr {...omit(others, ['onRef'])} />
      </WrapperContextProvider>
    );
  }
}
