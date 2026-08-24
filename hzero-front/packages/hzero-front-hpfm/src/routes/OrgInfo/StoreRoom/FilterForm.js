/*
 * FilterForm - 库房查询表单
 * @date: 2018/08/07 14:48:29
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent, Fragment } from 'react';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isFunction } from 'lodash';

import Lov from 'components/Lov';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

/**
 * 计量单位查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} handleSearch // 搜索
 * @reactProps {Function} handleFormReset // 重置表单
 * @return React.element
 */
const FormItem = Form.Item;
const modelPrompt = 'hpfm.storeRoom.model.storeRoom';

@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) {
      onRef(this);
    }
  }

  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (isFunction(onSearch)) {
      form.validateFields(err => {
        if (!err) {
          onSearch();
        }
      });
    }
  }

  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <div>
        <Fragment>
          <Form>
            <Row type="flex" gutter={24} align="bottom">
              <Col span={6}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get(`${modelPrompt}.inventoryCode`).d('库房编码')}
                >
                  {getFieldDecorator('inventoryCode')(
                    <Input trim typeCase="upper" inputChinese={false} />
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get(`${modelPrompt}.inventoryName`).d('库房名称')}
                >
                  {getFieldDecorator('inventoryName')(<Input />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get(`${modelPrompt}.ouId`).d('业务实体')}
                >
                  {getFieldDecorator('ouId')(
                    <Lov
                      code="HPFM.OU"
                      queryParams={{ organizationId: getCurrentOrganizationId(), enabledFlag: 1 }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem>
                  <Button data-code="reset" onClick={this.handleFormReset}>
                    {intl.get('hzero.common.button.reset').d('重置')}
                  </Button>
                  <Button
                    data-code="search"
                    htmlType="submit"
                    type="primary"
                    style={{ marginLeft: 8 }}
                    onClick={this.handleSearch}
                  >
                    {intl.get('hzero.common.button.search').d('查询')}
                  </Button>
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Fragment>
      </div>
    );
  }
}
