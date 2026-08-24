/**
 * UserGroupManagement 用户组管理
 * @date: 2019-1-14
 * @author: guochaochao <chaochao.guo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 10 },
  wrapperCol: { span: 14 },
};
@Form.create({ fieldNameProp: null })
export default class FilterForm extends React.PureComponent {
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

  /**
   * 查询表单
   */
  @Bind()
  handleSearch() {
    const { form, search } = this.props;
    search(form);
  }

  /**
   * 重置表单
   */
  @Bind()
  handleReset() {
    const { form, reset } = this.props;
    reset(form);
  }

  render() {
    const { form } = this.props;
    const { expandForm } = this.state;
    const { getFieldDecorator } = form;
    return (
      <Form className="more-fields-form">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          {!isTenantRoleLevel() && (
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.database.model.database.tenantName').d('租户名称')}
                {...formLayout}
              >
                {getFieldDecorator('tenantId')(<Lov allowClear={false} code="HPFM.TENANT" />)}
              </FormItem>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.userGroupManagement.model.userGroup.groupCode').d('用户组编码')}
              {...formLayout}
            >
              {getFieldDecorator('groupCode')(
                <Input trim typeCase="upper" inputChinese={false} dbc2sbc={false} />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.userGroupManagement.model.userGroup.groupName').d('用户组名称')}
              {...formLayout}
            >
              {getFieldDecorator('groupName')(<Input dbc2sbc={false} />)}
            </FormItem>
          </Col>
          {isTenantRoleLevel() && (
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`hiam.userGroupManagement.model.userGroup.user`).d('用户')}
              >
                {getFieldDecorator('userId')(
                  <Lov code={isTenantRoleLevel() ? 'HIAM.USER.ORG' : 'HIAM.USER'} />
                )}
              </FormItem>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              {!isTenantRoleLevel() && (
                <Button onClick={this.toggleForm}>
                  {expandForm
                    ? intl.get('hzero.common.button.collected').d('收起查询')
                    : intl.get(`hzero.common.button.viewMore`).d('更多查询')}
                </Button>
              )}
              <Button onClick={this.handleReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
        {!isTenantRoleLevel() && (
          <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`hiam.userGroupManagement.model.userGroup.user`).d('用户')}
              >
                {getFieldDecorator('userId')(
                  <Lov code={isTenantRoleLevel() ? 'HIAM.USER.ORG' : 'HIAM.USER'} />
                )}
              </FormItem>
            </Col>
          </Row>
        )}
      </Form>
    );
  }
}
