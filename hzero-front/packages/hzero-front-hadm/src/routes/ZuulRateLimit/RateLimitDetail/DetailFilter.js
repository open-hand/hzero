/*
 * DetailFilter - Zuul限流配置表单
 * @date: 2018/08/07 14:57:58
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Button, Form } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

/**
 * Zuul限流配置表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} handleSearch  搜索
 * @reactProps {Function} handleFormReset  重置表单
 * @reactProps {Function} toggleForm  展开查询条件
 * @reactProps {Function} renderAdvancedForm 渲染所有查询条件
 * @reactProps {Function} renderSimpleForm 渲染缩略查询条件
 * @return React.element
 */

@Form.create({ fieldNameProp: null })
export default class DetailFilter extends PureComponent {
  @Bind()
  handleCreate() {
    const { onCreate } = this.props;
    onCreate();
  }

  render() {
    const { handleDelete } = this.props;
    return (
      <div className="table-list-operator">
        <Button type="primary" onClick={this.handleCreate}>
          {intl.get('hzero.common.button.add').d('新增')}
        </Button>
        <Button onClick={handleDelete}>{intl.get('hzero.common.button.delete').d('删除')}</Button>
      </div>
    );
  }
}
