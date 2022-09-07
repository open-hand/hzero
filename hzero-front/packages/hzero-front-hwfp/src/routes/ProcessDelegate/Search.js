import React, { Component } from 'react';
import { Form, Input, Row, Col, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class Search extends Component {
  constructor(props) {
    super(props);
    props.handleRef(this);
  }

  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  @Bind()
  handleSearch() {
    this.props.onSearch();
  }

  render() {
    const {
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hwfp.common.view.message.handler').d('当前处理人')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('assignee')(
                <Lov
                  code="HWFP.EMPLOYEE"
                  queryParams={{ tenantId: getCurrentOrganizationId() }}
                  lovOptions={{
                    displayField: 'name',
                  }}
                  onChange={this.handleSelect}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hwfp.common.model.process.name').d('流程名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('processName')(<Input dbc2sbc={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hwfp.common.model.process.description').d('流程描述')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('processDescription')(<Input dbc2sbc={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item>
              <Button data-code="reset" style={{ marginRight: 8 }} onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                data-code="search"
                type="primary"
                htmlType="submit"
                onClick={this.handleSearch}
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
