import React from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import cacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';

import {
  FORM_COL_4_LAYOUT,
  FORM_FIELD_CLASSNAME,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hpfm/event/list' })
export default class EventSearchForm extends React.Component {
  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch } = this.props;
    if (onSearch) {
      onSearch();
    }
  }

  /**
   * 重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { form, isSiteFlag } = this.props;
    return (
      <Form className={SEARCH_FORM_CLASSNAME}>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          {isSiteFlag && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                label={intl.get('hzero.common.model.common.tenantId').d('租户')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {form.getFieldDecorator('tenantId')(
                  <Lov code="HPFM.TENANT" textField="tenantName" />
                )}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.event.model.event.code').d('事件编码')}
            >
              {form.getFieldDecorator('eventCode')(
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  className={FORM_FIELD_CLASSNAME}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.event.model.event.description').d('事件描述')}
            >
              {form.getFieldDecorator('eventDescription')(
                <Input className={FORM_FIELD_CLASSNAME} />
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
