/*
 * @Descripttion:事件定义 - 查询表单
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-03-26 15:04:34
 * @Copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import {
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';

import Lov from 'components/Lov';
import intl from 'utils/intl';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class FilterForm extends React.PureComponent {
  constructor(props) {
    super(props);
    // 调用父组件 props onRef 方法
    props.onRef(this);
  }

  /**
   * 查询操作
   */
  @Bind()
  handleSearch() {
    const { form, onSearch } = this.props;
    onSearch(form);
  }

  /**
   * 重置操作
   */
  @Bind()
  handleReset() {
    const { form, onReset } = this.props;
    onReset(form);
  }

  @Bind()
  handleOnChange(value, record) {
    if (value) {
      const { form, onSearch, dispatch } = this.props;
      dispatch({
        type: 'event/updateState',
        payload: { categoryName: record.categoryName },
      });
      onSearch(form);
    }
  }

  render() {
    const { form, queryData, categoryName } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form layout="inline" className="more-fields-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hevt.common.model.categoryCode').d('事件编码')}
            >
              {getFieldDecorator('eventCode', {
                initialValue: queryData.eventCode,
              })(<Input inputChinese={false} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hevt.common.model.eventName').d('事件名称')}
            >
              {getFieldDecorator('eventName', {
                initialValue: queryData.eventName,
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hevt.event.model.event.categoryName').d('事件类型')}
            >
              {getFieldDecorator('categoryId', {
                initialValue: queryData.categoryId,
              })(
                <Lov
                  code="HEVT.EVENT_CATEGORY"
                  textValue={categoryName}
                  onChange={this.handleOnChange}
                />
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
