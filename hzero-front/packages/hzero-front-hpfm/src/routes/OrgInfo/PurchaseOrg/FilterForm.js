import React, { PureComponent } from 'react';
import { Button, Form, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
  /**
   * handleSearchOrg - 搜索采购组织
   * @param {object} e - 事件对象
   */
  @Bind()
  handleSearchOrg(e) {
    e.preventDefault();
    const { form } = this.props;
    this.props.onSearch(form.getFieldsValue());
  }

  /**
   * handleResetSearch - 重置搜索表单
   * @param {object} e - 事件对象
   */
  @Bind()
  handleResetSearch() {
    const { form } = this.props;
    this.props.onReset(form);
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form>
        <Row type="flex" align="bottom" gutter={24}>
          <Col span={8}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.purchaseOrg.model.org.organizationCode').d('采购组织编码')}
            >
              {getFieldDecorator('organizationCode')(
                <Input trim typeCase="upper" inputChinese={false} />
              )}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.purchaseOrg.model.org.organizationName').d('采购组织名称')}
            >
              {getFieldDecorator('organizationName')(<Input />)}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem>
              <Button onClick={this.handleResetSearch}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                type="primary"
                htmlType="submit"
                style={{ marginLeft: 8 }}
                onClick={this.handleSearchOrg}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
