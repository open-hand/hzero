/**
 * 个性化页面-列表 查询表单
 * @date 2018/11/20
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';
import {Button, Col, Form, Input, Row} from 'hzero-ui';
import {Bind} from 'lodash-decorators';
import PropTypes from 'prop-types';

import cacheComponent from 'components/CacheComponent';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hpfm/ui/page-org/list' })
export default class SearchForm extends React.Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
    onRefForm: PropTypes.func.isRequired,
  };

  componentDidMount() {
    const { onRefForm } = this.props;
    onRefForm(this.props.form);
  }

  @Bind()
  handleSearchBtnClick(e) {
    e.preventDefault();
    const { onSearch } = this.props;
    onSearch();
  }

  @Bind()
  handleResetBtnClick() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { form } = this.props;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.ui.model.page.pageCode').d('页面编码')}
            >
              {form.getFieldDecorator('pageCode')(
                <Input trim typeCase="upper" inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.ui.model.page.description').d('页面描述')}
            >
              {form.getFieldDecorator('description')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearchBtnClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
