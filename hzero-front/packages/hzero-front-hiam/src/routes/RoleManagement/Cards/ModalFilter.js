import React from 'react';
import PropTypes from 'prop-types';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import {
  FORM_COL_3_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';

import styles from './styles.less';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class ModalFilter extends React.Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
  };

  /**
   * 重置查询表单
   * @param {*} e
   */
  @Bind()
  handleResetBtnClick(e) {
    e.preventDefault();
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 查询表单-查询
   * @param {*} e
   */
  @Bind()
  handleSearchBtnClick(e) {
    e.preventDefault();
    const { onSearch } = this.props;
    onSearch();
  }

  render() {
    const { form } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form className={`${styles['message-search-form']} more-fields-search-form`}>
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.roleManagement.model.roleManagement.cardCode').d('卡片代码')}
            >
              {getFieldDecorator('code')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.roleManagement.model.roleManagement.cardName').d('卡片名称')}
            >
              {getFieldDecorator('name')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearchBtnClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
