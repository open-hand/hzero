import React from 'react';
import PropTypes from 'prop-types';
import { Form, Input, Button, Row, Col, DatePicker } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import { getDateTimeFormat } from 'utils/utils';

import intl from 'utils/intl';
import {
  DEFAULT_TIME_FORMAT,
  SEARCH_FORM_CLASSNAME,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';

const dateTimeFormat = getDateTimeFormat();

@Form.create({ fieldNameProp: null })
export default class FilterForm extends React.Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
  };

  @Bind()
  handleResetBtnClick(e) {
    e.preventDefault();
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  handleSearchBtnClick(e) {
    e.preventDefault();
    const { onSearch } = this.props;
    onSearch();
  }

  render() {
    const { form } = this.props;
    return (
      <Form className={SEARCH_FORM_CLASSNAME}>
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmsg.userMessage.model.userMessage.fromDate').d('创建时间从')}
            >
              {form.getFieldDecorator('fromDate')(
                <DatePicker
                  placeholder=""
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  disabledDate={currentDate =>
                    form.getFieldValue('toDate') &&
                    moment(form.getFieldValue('toDate')).isBefore(currentDate, 'day')}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmsg.userMessage.model.userMessage.toDate').d('创建时间至')}
            >
              {form.getFieldDecorator('toDate')(
                <DatePicker
                  placeholder=""
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  disabledDate={currentDate =>
                    form.getFieldValue('fromDate') &&
                    moment(form.getFieldValue('fromDate')).isAfter(currentDate, 'day')}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmsg.userMessage.model.userMessage.subject').d('消息标题')}
            >
              {form.getFieldDecorator('subject')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleSearchBtnClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
