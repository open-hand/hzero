import React from 'react';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { SEARCH_FORM_ITEM_LAYOUT, SEARCH_FORM_ROW_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class QueryForm extends React.Component {
  /**
   * 重置查询表单
   */
  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 查询表单-查询
   */
  @Bind()
  handleSearch() {
    const { form, onSearch } = this.props;
    if (onSearch) {
      form.validateFields((err) => {
        if (!err) {
          onSearch();
        }
      });
    }
  }

  render() {
    const {
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Form layout="inline">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col span={14}>
            <Form.Item
              label={intl.get('hiam.subAccount.model.subAccount.secGrpName').d('安全组名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('secGrpName')(<Input />)}
            </Form.Item>
          </Col>
          <Col span={10}>
            <Form.Item>
              <Button onClick={this.handleReset} style={{ marginRight: '8px' }}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
