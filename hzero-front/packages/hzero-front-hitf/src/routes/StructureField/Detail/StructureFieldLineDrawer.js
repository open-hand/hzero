/**
 * StructureFieldLine - 结构字段明细
 * @date: 2020-4-9
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { Form, TextField, Select, NumberField, TextArea, Switch } from 'choerodon-ui/pro';
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';

import { Content } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { STRUCTURE_FIELD_TYPE_FIELDS } from '../../../constants/CodeConstants';

/**
 * 结构字段明细
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hitf.structureField'] })
export default class StructureFieldLine extends Component {
  constructor(props) {
    super(props);

    this.state = {
      defaultValueDisabled: !!(
        STRUCTURE_FIELD_TYPE_FIELDS.OBJECT === props.formDS.current.get('fieldType') ||
        STRUCTURE_FIELD_TYPE_FIELDS.ARRAY === props.formDS.current.get('fieldType')
      ),
    };
  }

  /**
   * 字段类型 change事件
   */
  @Bind()
  async fieldTypeChange(value) {
    const { formDS } = this.props;
    formDS.current.set('defaultValue', null);
    if (
      STRUCTURE_FIELD_TYPE_FIELDS.OBJECT === value ||
      STRUCTURE_FIELD_TYPE_FIELDS.ARRAY === value
    ) {
      this.setState({
        defaultValueDisabled: true,
      });
    } else {
      this.setState({
        defaultValueDisabled: false,
      });
    }
  }

  render() {
    const { openType, formDS } = this.props;
    const { defaultValueDisabled } = this.state;
    // 字段类型=【字符】 可输入任意字符；字段类型=【布尔】 限定只输入true/false；字段类型=【数字】 只输入数字
    let defaultValueRestrict = '';
    if (STRUCTURE_FIELD_TYPE_FIELDS.BOOL === formDS.current.get('fieldType')) {
      defaultValueRestrict = 'truefalse';
    } else if (STRUCTURE_FIELD_TYPE_FIELDS.DIGITAL === formDS.current.get('fieldType')) {
      defaultValueRestrict = '0-9';
    }
    return (
      <div>
        <Content>
          <Form dataSet={formDS} columns={1}>
            <TextField name="fieldName" restrict="a-zA-Z" disabled={openType === 'EDIT'} required />
            {formDS.current.get('parentId') && formDS.current.get('parentId') !== -1 && (
              <TextField name="parentName" disabled />
            )}
            <TextField name="fieldDesc" />
            <Select name="fieldType" required onChange={this.fieldTypeChange} />
            <NumberField name="seqNum" required />
            <TextField name="formatMask" />
            <TextField
              name="defaultValue"
              placeholder={
                STRUCTURE_FIELD_TYPE_FIELDS.BOOL === formDS.current.get('fieldType')
                  ? 'true/false'
                  : ''
              }
              disabled={defaultValueDisabled}
              restrict={defaultValueRestrict}
            />
            <TextArea name="remark" />
            <Switch name="enabledFlag" required />
          </Form>
        </Content>
      </div>
    );
  }
}
