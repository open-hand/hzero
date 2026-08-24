/**
 * SearchForm
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-21
 * @copyright 2019 © HAND
 */
import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import { Form, Button, DatePicker, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import {
  SEARCH_FORM_CLASSNAME,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import { getDateTimeFormat } from 'utils/utils';

class SearchForm extends React.Component {
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

    const dateTimeFormat = getDateTimeFormat();
    return (
      <Form className={SEARCH_FORM_CLASSNAME}>
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl
                .get('himp.commentImport.model.commentImport.creationDateFrom')
                .d('创建日期从')}
            >
              {form.getFieldDecorator('creationDateFrom')(
                <DatePicker
                  showTime
                  placeholder=""
                  format={dateTimeFormat}
                  disabledDate={currentDate =>
                    form.getFieldValue('creationDateTo') &&
                    moment(form.getFieldValue('creationDateTo')).isBefore(currentDate)}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl
                .get('himp.commentImport.model.commentImport.creationDateTo')
                .d('创建日期至')}
            >
              {form.getFieldDecorator('creationDateTo')(
                <DatePicker
                  showTime
                  placeholder=""
                  format={dateTimeFormat}
                  disabledDate={currentDate =>
                    form.getFieldValue('creationDateFrom') &&
                    moment(form.getFieldValue('creationDateFrom')).isAfter(currentDate)}
                />
              )}
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

SearchForm.propTypes = {
  onSearch: PropTypes.func.isRequired,
};

export default Form.create({ fieldNameProp: null })(SearchForm);
