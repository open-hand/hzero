import React from 'react';
import { Button, Col, Form, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class Filter extends React.Component {
  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch = e => e, form } = this.props;
    onSearch(form.getFieldsValue());
  }

  /**
   * 重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { form, channelCodeList = [] } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form>
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpay.payConfig.model.payConfig.channelCode').d('支付渠道')}
            >
              {getFieldDecorator('channelCode')(
                <Select allowClear>
                  {channelCodeList.map(m => (
                    <Select.Option key={m.value} value={m.value}>
                      {m.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
