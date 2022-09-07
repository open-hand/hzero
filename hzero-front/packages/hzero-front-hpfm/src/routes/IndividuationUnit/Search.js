import React, { Component } from 'react';
import { Form, Input, Button, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import { SEARCH_COL_CLASSNAME, SEARCH_FORM_ROW_LAYOUT, FORM_COL_4_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';
import { filterNullValueObject } from 'utils/utils';
import { getSingleTenantValueCode } from '@/utils/constConfig';

const FormItem = Form.Item;
const formsLayouts = {
  labelCol: { span: 7 },
  wrapperCol: { span: 16 },
};

@Form.create({ fieldNameProp: null })
export default class Search extends Component {
  /**
   * reset - 重置按钮事件
   */
  @Bind()
  reset() {
    const {
      form: { resetFields = () => {} },
      resetFilterFromValues = () => {},
    } = this.props;
    resetFields();
    resetFilterFromValues();
  }

  /**
   * search - 查询按钮事件
   */
  @Bind()
  search() {
    const { handleSearch = () => {}, form = {} } = this.props;
    const { getFieldsValue = () => {} } = form;
    const filterParams = filterNullValueObject(getFieldsValue());
    handleSearch(filterParams);
  }

  render() {
    const {
      filterFromValues = {},
      form: { getFieldDecorator = () => {} },
    } = this.props;
    const { unitCode = '', unitName = '', modelId = '', modelName = '' } = filterFromValues;
    return (
      <Form className="more-fields-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get('hpfm.individuationUnit.model.individuationUnit.unitCode')
                .d('单元编码')}
              {...formsLayouts}
            >
              {getFieldDecorator('unitCode', {
                initialValue: unitCode,
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get('hpfm.individuationUnit.model.individuationUnit.unitName')
                .d('单元名称')}
              {...formsLayouts}
            >
              {getFieldDecorator('unitName', {
                initialValue: unitName,
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get('hpfm.individuationUnit.model.individuationUnit.modelName')
                .d('关联模型')}
              {...formsLayouts}
            >
              {getFieldDecorator('modelId', {
                initialValue: modelId,
              })(
                <Lov
                  code={getSingleTenantValueCode('HPFM.CUST.MODEL_VIEW')}
                  textField="modelName"
                  textValue={modelName}
                />
              )}

              {getFieldDecorator('modelName', {
                initialValue: modelName,
              })(<Input style={{ display: 'none' }} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button data-code="reset" onClick={this.reset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.search}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
