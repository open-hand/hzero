/**
 * StructureField - 结构字段
 * @date: 2020-4-13
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { Form, TextField, Select, TextArea, Switch } from 'choerodon-ui/pro';
import React, { Component } from 'react';

import { Content } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';

/**
 * 结构字段
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hitf.structureField'] })
export default class StructureFieldDrawer extends Component {
  render() {
    const { openType, formDS } = this.props;
    return (
      <div>
        <Content>
          <Form dataSet={formDS} columns={1}>
            <TextField name="structureName" />
            <TextField
              name="structureNum"
              restrict="a-zA-Z"
              disabled={openType === 'EDIT'}
              required
            />
            <Select name="structureCategory" required />
            <Select name="composition" required disabled={openType === 'EDIT'} />
            <Select name="bizUsage" />
            <TextArea name="structureDesc" />
            <Switch name="enabledFlag" required />
          </Form>
        </Content>
      </div>
    );
  }
}
