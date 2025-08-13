import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind, Throttle } from 'lodash-decorators';

import Lov from 'components/Lov';
import cacheComponent from 'components/CacheComponent';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import {
  DEBOUNCE_TIME,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const { Option } = Select;

/**
 * 规则引擎列表查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} search - 查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */

const expandFormStyle = {
  display: '',
};
const noExpandFormStyle = {
  display: 'none',
};

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hpfm/rule-engine' })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expandForm: false,
    };
  }

  componentWillUnmount() {
    this.toggleForm.cancel();
  }

  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form, onStoreValues } = this.props;
    if (onSearch) {
      form.validateFields((err, values) => {
        if (isEmpty(err)) {
          // 如果验证成功,则执行search
          onSearch();
          onStoreValues(values);
        }
      });
    }
  }

  /**
   * 重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      scriptTypeCode = [],
      form: { getFieldDecorator },
      categoryList = [],
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.ruleEngine.model.ruleEngine.serverName').d('服务名称')}
            >
              {getFieldDecorator('serverName', {})(
                <Lov
                  code={
                    isTenantRoleLevel() ? 'HADM.ROUTE.SERVICE_CODE.ORG' : 'HADM.ROUTE.SERVICE_CODE'
                  }
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.ruleEngine.model.ruleEngine.scriptTypeCode').d('类型')}
            >
              {getFieldDecorator('scriptTypeCode', {})(
                <Select allowClear>
                  {scriptTypeCode &&
                    scriptTypeCode.map(item => (
                      <Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.status').d('状态')}
            >
              {getFieldDecorator('enabledFlag', {})(
                <Select allowClear>
                  <Option value={1}>{intl.get('hzero.common.status.enable').d('启用')}</Option>
                  <Option value={0}>{intl.get('hzero.common.status.disable').d('禁用')}</Option>
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={expandForm ? expandFormStyle : noExpandFormStyle}>
          {!isTenantRoleLevel() && (
            <Col span={6}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.ruleEngine.model.ruleEngine.tenantId').d('租户')}
              >
                {getFieldDecorator('tenantId', {})(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col span={6}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.ruleEngine.model.ruleEngine.scriptCode').d('脚本编码')}
            >
              {getFieldDecorator('scriptCode', {})(
                <Input typeCase="upper" trim inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.ruleEngine.model.ruleEngine.category').d('脚本分类')}
            >
              {getFieldDecorator('category')(
                <Select allowClear>
                  {categoryList.map(item => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
        </Row>
        <Row style={expandForm ? expandFormStyle : noExpandFormStyle} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col span={6}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.ruleEngine.model.ruleEngine.scriptDescription').d('描述')}
            >
              {getFieldDecorator('scriptDescription', {})(<Input />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
