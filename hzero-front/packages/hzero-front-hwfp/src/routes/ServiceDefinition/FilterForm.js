import React from 'react';
import { Form, Button, Input, Row, Col, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { getCurrentOrganizationId } from 'utils/utils';
import cacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  FORM_FIELD_CLASSNAME,
} from 'utils/constants';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hwfp/service-definition/list' })
export default class FilterForm extends React.Component {
  constructor(props) {
    super(props);
    const { onRef } = props;
    if (onRef) {
      onRef(this);
    }
    this.state = {
      expandForm: false,
    };
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
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch = (e) => e } = this.props;
    onSearch();
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
    const { form, serviceTypeList = [], isSiteFlag } = this.props;
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
                        .get('hwfp.serviceDefinition.model.service.categoryId')
                        .d('流程分类')}
                    >
                      {getFieldDecorator('categoryId')(<Lov code="HWFP.PROCESS_CATEGORY" />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.serviceCode')
                        .d('服务编码')}
                    >
                      {getFieldDecorator('serviceCode')(
                        <Input
                          trim
                          typeCase="upper"
                          inputChinese={false}
                          className={FORM_FIELD_CLASSNAME}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
                    <Form.Item>
                      <Button
                        style={{ display: expandForm ? 'none' : 'inline-block' }}
                        onClick={this.toggleForm}
                      >
                        {intl.get(`hzero.common.button.viewMore`).d('更多查询')}
                      </Button>
                      <Button
                        style={{ display: expandForm ? 'inline-block' : 'none' }}
                        onClick={this.toggleForm}
                      >
                        {intl.get('hzero.common.button.collected').d('收起查询')}
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
                <Row
                  style={{ display: expandForm ? '' : 'none' }}
                  {...SEARCH_FORM_ROW_LAYOUT}
                  type="flex"
                  gutter={24}
                  align="bottom"
                >
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.documentId')
                        .d('流程单据')}
                    >
                      {getFieldDecorator('documentId')(
                        <Lov
                          allowClear
                          queryParams={isSiteFlag ? {} : { tenantId: organizationId }}
                          code="HWFP.PROCESS_DOCUMENT"
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.serviceType')
                        .d('服务类别')}
                    >
                      {getFieldDecorator('serviceType')(
                        <Select className={FORM_FIELD_CLASSNAME} allowClear>
                          {serviceTypeList.map((m) => (
                            <Select.Option key={m.value} value={m.value}>
                              {m.meaning}
                            </Select.Option>
                          ))}
                        </Select>
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.description')
                        .d('服务描述')}
                    >
                      {getFieldDecorator('description')(
                        <Input trim className={FORM_FIELD_CLASSNAME} />
                      )}
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
                        .get('hwfp.serviceDefinition.model.service.categoryId')
                        .d('流程分类')}
                    >
                      {getFieldDecorator('categoryId')(
                        <Lov
                          code="HWFP.PROCESS_CATEGORY"
                          queryParams={{ tenantId: organizationId }}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.serviceCode')
                        .d('服务编码')}
                    >
                      {getFieldDecorator('serviceCode')(
                        <Input
                          trim
                          typeCase="upper"
                          inputChinese={false}
                          className={FORM_FIELD_CLASSNAME}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.documentId')
                        .d('流程单据')}
                    >
                      {getFieldDecorator('documentId')(
                        <Lov
                          allowClear
                          queryParams={isSiteFlag ? {} : { tenantId: organizationId }}
                          code="HWFP.PROCESS_DOCUMENT"
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
                    <Form.Item>
                      <Button
                        style={{ display: expandForm ? 'none' : 'inline-block' }}
                        onClick={this.toggleForm}
                      >
                        {intl.get(`hzero.common.button.viewMore`).d('更多查询')}
                      </Button>
                      <Button
                        style={{ display: expandForm ? 'inline-block' : 'none' }}
                        onClick={this.toggleForm}
                      >
                        {intl.get('hzero.common.button.collected').d('收起查询')}
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
                <Row
                  style={{ display: expandForm ? '' : 'none' }}
                  {...SEARCH_FORM_ROW_LAYOUT}
                  type="flex"
                  gutter={24}
                  align="bottom"
                >
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.serviceType')
                        .d('服务类别')}
                    >
                      {getFieldDecorator('serviceType')(
                        <Select className={FORM_FIELD_CLASSNAME} allowClear>
                          {serviceTypeList.map((m) => (
                            <Select.Option key={m.value} value={m.value}>
                              {m.meaning}
                            </Select.Option>
                          ))}
                        </Select>
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.description')
                        .d('服务描述')}
                    >
                      {getFieldDecorator('description')(
                        <Input trim className={FORM_FIELD_CLASSNAME} />
                      )}
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
