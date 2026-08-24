import React, { PureComponent } from 'react';
import { Bind } from 'lodash-decorators';
import { Button, Col, Form, Input, Row } from 'hzero-ui';

import cacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import {
  FORM_COL_4_LAYOUT,
  FORM_FIELD_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';

import styles from './styles.less';

/**
 * 查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 表单查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hpfm/lov-view/lov-view-list/search-form' })
export default class FilterForm extends PureComponent {
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

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields((err) => {
        if (!err) {
          onSearch();
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

  @Bind()
  handleToggleForm() {
    const { expandForm = false } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { form } = this.props;
    const { expandForm } = this.state;
    return (
      <Form className={`${styles['lov-setting-search-form']} more-fields-search-form`}>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.lov.model.lov.viewCode').d('视图代码')}
            >
              {form.getFieldDecorator('viewCode')(
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
              label={intl.get('hpfm.lov.model.lov.viewName').d('视图名称')}
            >
              {form.getFieldDecorator('viewName')(<Input className={FORM_FIELD_CLASSNAME} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.lov.model.lov.sourceCode').d('值集编码')}
            >
              {form.getFieldDecorator('lovCode')(
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  className={FORM_FIELD_CLASSNAME}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              {!isTenantRoleLevel() && (
                <Button onClick={this.handleToggleForm}>
                  {expandForm
                    ? intl.get('hzero.common.button.collected').d('收起查询')
                    : intl.get('hzero.common.button.viewMore').d('更多查询')}
                </Button>
              )}
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row
          {...SEARCH_FORM_ROW_LAYOUT}
          style={{ display: !isTenantRoleLevel() && expandForm ? '' : 'none' }}
        >
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('entity.tenant.name').d('租户名称')}
            >
              {form.getFieldDecorator('tenantId')(
                <Lov code="HPFM.TENANT" className={FORM_FIELD_CLASSNAME} />
              )}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
