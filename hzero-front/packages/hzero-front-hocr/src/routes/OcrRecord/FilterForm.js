import React from 'react';
import { Form, Button, Row, Col, DatePicker, Select, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { getDateTimeFormat } from 'utils/utils';
import moment from 'moment';
import intl from 'utils/intl';
import {
  DEFAULT_TIME_FORMAT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;
const dateTimeFormat = getDateTimeFormat();

@Form.create({ fieldNameProp: null })
export default class FilterForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isExpendSearch: false,
    };
  }

  @Bind()
  handleExpendSearch() {
    const { isExpendSearch } = this.state;
    this.setState({
      isExpendSearch: !isExpendSearch,
    });
  }

  /**
   * 重置查询表单
   */
  @Bind()
  handleResetBtnClick() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 查询表单-查询
   */
  @Bind()
  handleSearchBtnClick() {
    const { onSearch } = this.props;
    onSearch();
  }

  render() {
    const { form, ocrTypeList = [] } = this.props;
    const { isExpendSearch } = this.state;
    const { getFieldDecorator } = form;
    return (
      <Form>
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hocr.ocrRecord.model.ocrRecord.recognizeType').d('识别类型')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('recognizeType')(
                <Select allowClear>
                  {ocrTypeList.map((item) => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hocr.ocrRecord.model.ocrRecord.recognizeDateFrom').d('识别时间从')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('recognizeDateFrom')(
                <DatePicker
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  placeholder=""
                  format={dateTimeFormat}
                  disabledDate={(currentDate) =>
                    form.getFieldValue('recognizeDateTo') &&
                    moment(form.getFieldValue('recognizeDateTo')).isBefore(currentDate, 'day')
                  }
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hocr.ocrRecord.model.ocrRecord.recognizeDateTo').d('识别时间到')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('recognizeDateTo')(
                <DatePicker
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  placeholder=""
                  format={dateTimeFormat}
                  disabledDate={(currentDate) =>
                    form.getFieldValue('recognizeDateFrom') &&
                    moment(form.getFieldValue('recognizeDateFrom')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <FormItem>
              <Button onClick={this.handleExpendSearch}>
                {isExpendSearch
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearchBtnClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
        <Row
          {...SEARCH_FORM_ROW_LAYOUT}
          style={{ display: isExpendSearch ? '' : 'none' }}
          type="flex"
          gutter={24}
          align="bottom"
        >
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hocr.ocrRecord.model.ocrRecord.realName').d('识别人')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('realName')(<Input />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
