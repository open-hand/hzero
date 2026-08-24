import React, { Fragment, PureComponent } from 'react';
import { Button, Form, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { FORM_COL_4_LAYOUT, SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

import intl from 'utils/intl';

const btnStyle = {
  marginRight: 8,
};

/**
 * 库位查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 表单查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
  componentDidMount() {
    this.props.onRef(this);
  }

  /**
   * 表单查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields((err, values) => {
        if (!err) {
          // 如果验证成功,则执行onSearch
          onSearch(values);
        }
      });
    }
  }

  /**
   * 表单重置
   */
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Fragment>
        <Form className="more-fields-search-form">
          <Row>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('entity.lang.code').d('语言编码')}
              >
                {getFieldDecorator('code')(<Input inputChinese={false} />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
              <Form.Item {...FORM_COL_4_LAYOUT}>
                <Button onClick={this.handleFormReset} style={btnStyle}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Fragment>
    );
  }
}
