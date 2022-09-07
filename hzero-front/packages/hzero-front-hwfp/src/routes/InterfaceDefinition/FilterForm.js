import React from 'react';
import { Button, Form, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import cacheComponent from 'components/CacheComponent';

import intl from 'utils/intl';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';

/**
 * 表单管理查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} search - 查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hwfp/interface-definition/list' })
export default class FilterForm extends React.Component {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expandForm: false,
    };
  }

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch = (e) => e } = this.props;
    onSearch();
  }

  // 查询条件展开/收起
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  /**
   * 重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { form, isSiteFlag } = this.props;
    const { expandForm } = this.state;
    const { getFieldDecorator } = form;
    return (
      <Form>
        <Row>
          <Col>
            {isSiteFlag ? (
              <>
                <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      label={intl.get('entity.tenant.tag').d('租户')}
                      {...SEARCH_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.interfaceMap.model.interfaceMap.interfaceCode')
                        .d('接口编码')}
                    >
                      {getFieldDecorator('interfaceCode')(
                        <Input typeCase="upper" trim inputChinese={false} />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.interfaceMap.model.interfaceMap.serviceName')
                        .d('服务名称')}
                    >
                      {getFieldDecorator('serviceName')(<Lov code="HADM.ROUTE.SERVICE_CODE.ORG" />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
                    <Form.Item>
                      <Button onClick={this.toggleForm}>
                        {expandForm
                          ? intl.get('hzero.common.button.collected').d('收起查询')
                          : intl.get(`hzero.common.button.viewMore`).d('更多查询')}
                      </Button>
                      <Button data-code="reset" onClick={this.handleFormReset}>
                        {intl.get('hzero.common.button.reset').d('重置')}
                      </Button>
                      <Button
                        data-code="search"
                        type="primary"
                        htmlType="submit"
                        onClick={this.handleSearch}
                      >
                        {intl.get('hzero.common.button.search').d('查询')}
                      </Button>
                    </Form.Item>
                  </Col>
                </Row>
                <Row
                  style={{ display: expandForm ? '' : 'none' }}
                  {...SEARCH_FORM_ROW_LAYOUT}
                  type="flex"
                  gutter={24}
                  align="bottom"
                >
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hwfp.interfaceMap.model.interfaceMap.description')
                        .d('接口说明')}
                      {...SEARCH_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('description')(<Input />)}
                    </Form.Item>
                  </Col>
                </Row>
              </>
            ) : (
              <>
                <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.interfaceMap.model.interfaceMap.interfaceCode')
                        .d('接口编码')}
                    >
                      {getFieldDecorator('interfaceCode')(
                        <Input typeCase="upper" trim inputChinese={false} />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.interfaceMap.model.interfaceMap.serviceName')
                        .d('服务名称')}
                    >
                      {getFieldDecorator('serviceName')(<Lov code="HADM.ROUTE.SERVICE_CODE.ORG" />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hwfp.interfaceMap.model.interfaceMap.description')
                        .d('接口说明')}
                      {...SEARCH_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('description')(<Input />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
                    <Form.Item>
                      {/* <Button onClick={this.toggleForm}>
                        {expandForm
                          ? intl.get('hzero.common.button.collected').d('收起查询')
                          : intl.get(`hzero.common.button.viewMore`).d('更多查询')}
                      </Button> */}
                      <Button data-code="reset" onClick={this.handleFormReset}>
                        {intl.get('hzero.common.button.reset').d('重置')}
                      </Button>
                      <Button
                        data-code="search"
                        type="primary"
                        htmlType="submit"
                        onClick={this.handleSearch}
                      >
                        {intl.get('hzero.common.button.search').d('查询')}
                      </Button>
                    </Form.Item>
                  </Col>
                </Row>
              </>
            )}
          </Col>
        </Row>
      </Form>
    );
  }
}
