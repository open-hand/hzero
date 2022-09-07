import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

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

const tenantRoleLevel = isTenantRoleLevel();

@Form.create({ fieldNameProp: null })
export default class Search extends PureComponent {
  constructor(props) {
    super(props);
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

  @Bind()
  onClick() {
    const {
      handleQueryList = (e) => e,
      form: { getFieldsValue = (e) => e },
    } = this.props;
    const data = getFieldsValue() || {};
    handleQueryList({
      ...data,
    });
  }

  onReset() {
    const {
      form: { resetFields = (e) => e },
    } = this.props;
    resetFields();
  }

  render() {
    const {
      code = {}, // 角色来源
      form: { getFieldDecorator = (e) => e },
    } = this.props;
    const { expandForm } = this.state;

    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hitf.clientRole.model.clientRole.name').d('角色名称')}
            >
              {getFieldDecorator('name')(<Input className={FORM_FIELD_CLASSNAME} />)}
            </FormItem>
          </Col>
          {/* <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`${modelPrompt}.parentRole`).d('父级角色')}
            >
              {getFieldDecorator('adminRoleId')(
                <Lov code="HIAM.ROLE.ADMIN_ROLE" className={FORM_FIELD_CLASSNAME} />
              )}
            </FormItem>
          </Col> */}
          {!VERSION_IS_OP && !tenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hitf.clientRole.model.clientRole.level').d('角色层级')}
              >
                {getFieldDecorator('level')(
                  <Select allowClear className={FORM_FIELD_CLASSNAME}>
                    {(code['HIAM.RESOURCE_LEVEL'] || [])
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
          )}
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              {!VERSION_IS_OP && !tenantRoleLevel && (
                <Button onClick={this.toggleForm}>
                  {expandForm
                    ? intl.get('hzero.common.button.collected').d('收起查询')
                    : intl.get('hzero.common.button.viewMore').d('更多查询')}
                </Button>
              )}
              <Button onClick={this.onReset.bind(this)}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.onClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
        {!VERSION_IS_OP && !tenantRoleLevel && (
          <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hitf.clientRole.model.clientRole.roleSource').d('角色来源')}
              >
                {getFieldDecorator('roleSource')(
                  <Select allowClear className={FORM_FIELD_CLASSNAME}>
                    {(code['HIAM.ROLE_SOURCE'] || []).map((n) => (
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
                label={intl.get('hitf.clientRole.model.clientRole.tenant').d('所属租户')}
              >
                {getFieldDecorator('tenantId')(
                  <Lov
                    className={FORM_FIELD_CLASSNAME}
                    code={tenantRoleLevel ? 'HPFM.TENANT.ORG' : 'HPFM.TENANT'}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
        )}
      </Form>
    );
  }
}
