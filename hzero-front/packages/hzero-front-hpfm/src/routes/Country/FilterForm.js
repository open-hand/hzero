import React from 'react';
import { Form, Button, Input, Col, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { FORM_COL_3_LAYOUT, SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

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

  render() {
    const { form } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form>
        <Row>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.country.model.country.condition').d('国家代码/国家名称')}
            >
              {getFieldDecorator('condition')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT} className="search-btn-more">
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
