/**
 * InventoryOrg -库存组织页面 -查询条件
 * @date: 2018-11-5
 * @author dengtingmin <tingmin.deng@hand-china.com>
 * @version: 0.0.3
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import {
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const SEARCH_FORM_COL_LAYOUT = {
  span: 6,
};

@Form.create({ fieldNameProp: null })
export default class FilterForm extends Component {
  componentDidMount() {
    this.props.onHandleBindRef(this);
  }

  @Bind()
  queryByconditon() {
    const { form, onFetchOrg } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (isEmpty(err)) {
        onFetchOrg(fieldsValue);
      }
    });
  }

  @Bind()
  resetCondition() {
    this.props.form.resetFields();
  }

  render() {
    const {
      form: { getFieldDecorator },
      getOrganizationId,
    } = this.props;
    return (
      <Form className={SEARCH_FORM_CLASSNAME}>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...SEARCH_FORM_COL_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.inventoryOrg.model.inventoryOrg.headerTitle').d('库存组织编码')}
            >
              {getFieldDecorator('organizationCode')(
                <Input trim typeCase="upper" inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col {...SEARCH_FORM_COL_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl
                .get('hpfm.inventoryOrg.model.inventoryOrg.organizationName')
                .d('库存组织名称')}
            >
              {getFieldDecorator('organizationName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...SEARCH_FORM_COL_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.inventoryOrg.model.inventoryOrg.ouId').d('业务实体')}
            >
              {getFieldDecorator('ouId')(
                <Lov
                  code="HPFM.OU"
                  queryParams={{
                    organizationId: getOrganizationId,
                    enabledFlag: 1,
                  }}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...SEARCH_FORM_COL_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.resetCondition}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                type="primary"
                htmlType="submit"
                style={{ marginLeft: 8 }}
                onClick={this.queryByconditon}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
