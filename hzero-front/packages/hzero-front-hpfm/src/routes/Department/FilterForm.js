import React, { PureComponent, Fragment } from 'react';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import classNames from 'classnames';
import CacheComponent from 'components/CacheComponent';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_CLASSNAME,
} from 'utils/constants';
import intl from 'utils/intl';

import styles from './index.less';

/**
 * 部门维护-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 表单查询=
 * @reactProps {string} companyName - 公司名称
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */

@Form.create({ fieldNameProp: null })
@CacheComponent({ cacheKey: '/hpfm/hr/org/department' })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  /**
   * 表单查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields(err => {
        if (!err) {
          // 如果验证成功,则执行onSearch
          onSearch();
        }
      });
    }
  }

  /**
   * 表单重置
   */
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { companyName, form } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Fragment>
        <Fragment>
          <p className={classNames(styles['hpfm-hr-title'])}>
            <span />
            {intl
              .get('hpfm.department.view.message.tips', {
                name: companyName,
              })
              .d(`当前正在为「${companyName}」公司，分配部门`)}
          </p>
        </Fragment>
        <Form className={SEARCH_FORM_CLASSNAME}>
          <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('entity.department.code').d('部门编码')}
              >
                {getFieldDecorator('unitCode')(
                  <Input trim typeCase="upper" inputChinese={false} />
                )}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('entity.department.name').d('部门名称')}
              >
                {getFieldDecorator('unitName')(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
              <Form.Item>
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
        </Form>
      </Fragment>
    );
  }
}
