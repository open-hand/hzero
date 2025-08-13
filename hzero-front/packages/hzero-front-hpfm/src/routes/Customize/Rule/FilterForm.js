import React from 'react';
import { Form, Button, Input, Row, Col } from 'hzero-ui';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

function FilterForm(props) {
  const handleSearch = () => {
    const { form, onSearch } = props;
    onSearch(form);
  };

  const handleReset = () => {
    const { form } = props;
    form.resetFields();
  };

  const { form, isSiteFlag } = props;
  const { getFieldDecorator } = form;
  return (
    <Form>
      <Row type="flex" gutter={24} align="bottom">
        <Col span={6}>
          {isSiteFlag && (
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.model.tenantName').d('租户')}
            >
              {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
            </Form.Item>
          )}
        </Col>
        <Col span={6}>
          <Form.Item
            {...SEARCH_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.customize.model.customize.rule.ruleCode').d('规则编码')}
          >
            {getFieldDecorator('ruleCode')(<Input typeCase="upper" />)}
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item
            {...SEARCH_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.customize.model.customize.rule.ruleName').d('规则名称')}
          >
            {getFieldDecorator('ruleName')(<Input />)}
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item>
            <Button style={{ marginRight: 8 }} onClick={handleReset}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button type="primary" htmlType="submit" onClick={handleSearch}>
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
          </Form.Item>
        </Col>
      </Row>
    </Form>
  );
}

export default Form.create({ fieldNameProp: null })(FilterForm);
