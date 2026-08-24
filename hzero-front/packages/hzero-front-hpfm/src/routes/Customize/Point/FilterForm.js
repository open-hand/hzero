import React, { useState } from 'react';
import { Form, Button, Input, Row, Col } from 'hzero-ui';

import intl from 'utils/intl';
import { SEARCH_FORM_ITEM_LAYOUT } from 'hzero-front/lib/utils/constants';

function FilterForm(props) {
  const [expandForm, setExpandForm] = useState(false);

  const handleSearch = () => {
    const { form, onSearch } = props;
    onSearch(form);
  };

  const handleReset = () => {
    const { form } = props;
    form.resetFields();
  };

  // 查询条件展开/收起
  const toggleForm = () => {
    setExpandForm(!expandForm);
  };

  const { form } = props;
  const { getFieldDecorator } = form;
  return (
    <Form className="more-fields-search-form">
      <Row type="flex" gutter={24} align="bottom">
        <Col span={6}>
          <Form.Item
            {...SEARCH_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.customize.model.customize.point.serviceName').d('服务名')}
          >
            {getFieldDecorator('serviceName')(<Input />)}
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item
            {...SEARCH_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.customize.model.customize.point.packageName').d('包名')}
          >
            {getFieldDecorator('packageName')(<Input />)}
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item
            {...SEARCH_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.customize.model.customize.point.className').d('类名')}
          >
            {getFieldDecorator('className')(<Input />)}
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item>
            <Button style={{ marginRight: 8 }} onClick={toggleForm}>
              {expandForm
                ? intl.get('hzero.common.button.collected').d('收起查询')
                : intl.get(`hzero.common.button.viewMore`).d('更多查询')}
            </Button>
            <Button style={{ marginRight: 8 }} onClick={handleReset}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button type="primary" htmlType="submit" onClick={handleSearch}>
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item
            {...SEARCH_FORM_ITEM_LAYOUT}
            style={{ display: expandForm ? '' : 'none' }}
            label={intl.get('hpfm.customize.model.customize.point.methodName').d('方法名')}
          >
            {getFieldDecorator('methodName')(<Input />)}
          </Form.Item>
        </Col>
      </Row>
    </Form>
  );
}

export default Form.create({ fieldNameProp: null })(FilterForm);
