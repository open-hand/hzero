import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import Lov from 'components/Lov';
import cacheComponent from 'components/CacheComponent';

import intl from 'utils/intl';
import { VERSION_IS_OP } from 'utils/config';
import {
  FORM_COL_4_LAYOUT,
  FORM_FIELD_CLASSNAME,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import { isTenantRoleLevel } from 'utils/utils';

const FormItem = Form.Item;
const { Option } = Select;

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hiam/role/search-form' })
export default class Search extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      expandForm: true,
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

  @Bind()
  onClick() {
    const {
      handleQueryList = (e) => e,
      form: { getFieldsValue = (e) => e },
    } = this.props;
    const data = getFieldsValue() || {};
    const { labels = [] } = data;
    const param = isEmpty(labels) ? '' : labels.join(',');
    handleQueryList({
      ...data,
      labels: param,
    });
  }

  onReset() {
    const {
      form: { resetFields = (e) => e },
    } = this.props;
    resetFields();
  }

  renderSiteSearchForm() {
    const {
      flagList = [],
      code = [], // 角色来源
      roleLevel = [], // 角色层级
      searchLabels = [], // 标签
      form: { getFieldDecorator = (e) => e },
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hiam.roleManagement.model.roleManagement.name`).d('角色名称')}
            >
              {getFieldDecorator('name')(<Input className={FORM_FIELD_CLASSNAME} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hiam.roleManagement.model.roleManagement.level`).d('角色层级')}
            >
              {getFieldDecorator('level')(
                <Select allowClear className={FORM_FIELD_CLASSNAME}>
                  {roleLevel
                    .filter((item) => item.value !== 'org')
                    .map((n) => (
                      <Option key={n.value} value={n.value}>
                        {n.meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hiam.roleManagement.model.roleManagement.roleSource`).d('角色来源')}
            >
              {getFieldDecorator('roleSource')(
                <Select allowClear className={FORM_FIELD_CLASSNAME}>
                  {code.map((n) => (
                    <Option key={n.value} value={n.value}>
                      {n.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get(`hzero.common.button.viewMore`).d('更多查询')}
              </Button>
              <Button onClick={this.onReset.bind(this)}>
                {intl.get(`hzero.common.button.reset`).d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.onClick}>
                {intl.get(`hzero.common.button.search`).d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hiam.roleManagement.model.roleManagement.tenant`).d('所属租户')}
            >
              {getFieldDecorator('tenantId')(
                <Lov
                  className={FORM_FIELD_CLASSNAME}
                  code={isTenantRoleLevel() ? 'HPFM.TENANT.ORG' : 'HPFM.TENANT'}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hiam.roleManagement.model.roleManagement.labels`).d('标签')}
            >
              {getFieldDecorator('labels')(
                <Select allowClear mode="multiple" className={FORM_FIELD_CLASSNAME}>
                  {searchLabels.map((n) => (
                    <Option key={n.id} value={n.name}>
                      {n.name}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hzero.common.status`).d('状态')}
            >
              {getFieldDecorator('enabled', {
                initialValue: '1',
              })(
                <Select allowClear className={FORM_FIELD_CLASSNAME}>
                  {flagList.map((n) => (
                    <Option key={n.value} value={n.value}>
                      {n.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  renderTenantSearchForm() {
    const {
      form: { getFieldDecorator = (e) => e },
      flagList = [],
      searchLabels = [], // 标签
    } = this.props;
    return (
      <Form>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hiam.roleManagement.model.roleManagement.name`).d('角色名称')}
            >
              {getFieldDecorator('name')(<Input className={FORM_FIELD_CLASSNAME} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hiam.roleManagement.model.roleManagement.labels`).d('标签')}
            >
              {getFieldDecorator('labels')(
                <Select allowClear mode="multiple" className={FORM_FIELD_CLASSNAME}>
                  {searchLabels.map((n) => (
                    <Option key={n.id} value={n.name}>
                      {n.name}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hzero.common.status`).d('状态')}
            >
              {getFieldDecorator('enabled', {
                initialValue: '1',
              })(
                <Select allowClear className={FORM_FIELD_CLASSNAME}>
                  {flagList.map((n) => (
                    <Option key={n.value} value={n.value}>
                      {n.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.onReset.bind(this)}>
                {intl.get(`hzero.common.button.reset`).d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.onClick}>
                {intl.get(`hzero.common.button.search`).d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    if (!VERSION_IS_OP && !isTenantRoleLevel()) {
      return this.renderSiteSearchForm();
    }
    return this.renderTenantSearchForm();
  }
}
