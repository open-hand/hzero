/**
 * 卡片管理查询表单
 * @date 2019-01-23
 * @author WY yang.wang06@hand-china.com
 * @copyright © HAND 2019
 */

import React from 'react';
import { Form, Input, Select, Button, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import {
  SEARCH_COL_CLASSNAME,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import classnames from 'classnames';

// Select 等 组件需要指定宽度
// const fdLevelSelectStyle = {
//   width: '200px',
// };

/**
 * 卡片管理查询表单
 * @ReactProps {!Function} onRef - 拿到该组件的this
 * @ReactProps {!Function} onSearch - 触发查询方法
 * @ReactProps {Array<{value: string, meaning: string}>} fdLevel - 层级的值集
 */
@Form.create({ fieldNameProp: null })
export default class SearchForm extends React.Component {
  constructor(props) {
    super(props);
    const { onRef } = this.props;
    onRef(this);
  }

  render() {
    const { form, fdLevel } = this.props;
    return (
      <Form className={classnames('more-fields-form')}>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.card.model.card.code').d('卡片代码')}
            >
              {form.getFieldDecorator('code')(<Input trim inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.card.model.card.name').d('卡片名称')}
            >
              {form.getFieldDecorator('name')(<Input />)}
            </Form.Item>
          </Col>
          {isTenantRoleLevel() || (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.card.model.card.fdLevel').d('层级')}
              >
                {form.getFieldDecorator('level')(
                  <Select>
                    {(fdLevel || []).map(item => (
                      <Select.Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item {...SEARCH_FORM_ITEM_LAYOUT}>
              <Button key="reset" onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                key="search"
                type="primary"
                htmlType="submit"
                onClick={this.handleSearchBtnClick}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

  @Bind()
  handleSearchBtnClick() {
    const { onSearch } = this.props;
    onSearch();
  }

  @Bind()
  handleResetBtnClick() {
    const { form } = this.props;
    form.resetFields();
  }
}
