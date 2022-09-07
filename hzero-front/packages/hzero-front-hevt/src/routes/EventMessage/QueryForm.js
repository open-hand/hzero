/*
 * @Descripttion: 事件查询 - 事件查询表单
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-04-09 14:26:40
 * @Copyright: Copyright (c) 2020, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Button, Input, Row, Col, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import {
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';

const { Option } = Select;

const FormItem = Form.Item;
@Form.create({ fieldNameProp: null })
export default class QueryForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  /**
   * 查询操作
   */
  @Bind()
  handleSearch() {
    const { onSearch } = this.props;
    onSearch();
  }

  /**
   * 重置操作
   */
  @Bind()
  handleReset() {
    const { form, onReset } = this.props;
    onReset(form);
  }

  render() {
    const { form, mapValues } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form layout="inline" className="more-fields-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hevt.eventMessage.model.eventMessage.processType').d('处理类型')}
            >
              {getFieldDecorator('processType', {
                initialValue: 'PRODUCE',
              })(
                <Select>
                  {mapValues.processTypes?.map((e) => {
                    return <Option value={e.value}>{e.meaning}</Option>;
                  })}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hevt.eventMessage.model.eventMessage.eventName').d('事件')}
            >
              {getFieldDecorator('eventName')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hevt.eventMessage.model.eventMessage.processStatus').d('状态')}
            >
              {getFieldDecorator('processStatus')(
                <Select allowClear>
                  {mapValues.processStatus?.map((e) => {
                    return <Option value={e.value}>{e.meaning}</Option>;
                  })}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button style={{ marginRight: 8 }} onClick={this.handleReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
