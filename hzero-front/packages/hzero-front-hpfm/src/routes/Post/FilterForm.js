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
 *  页面搜索框
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} onSearch - 搜索方法
 * @reactProps {String} unitName - 部门名称
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@CacheComponent({ cacheKey: '/hpfm/hr/org/post/' })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  /**
   * 编辑保存前校验
   */
  @Bind()
  handleSearch() {
    const { onSearch, form, setCacheFormData } = this.props;
    if (onSearch) {
      form.validateFields((err, value) => {
        if (!err) {
          // 如果验证成功,则执行onSearch
          onSearch();
          setCacheFormData(value);
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
    const { unitName, form } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Fragment>
        <Fragment>
          <p className={classNames(styles['hpfm-hr-title'])}>
            <span />
            {intl
              .get('hpfm.position.view.message.tips', {
                name: unitName,
              })
              .d(`当前正在为「${unitName}」部门，分配岗位`)}
          </p>
        </Fragment>
        <Form className={SEARCH_FORM_CLASSNAME}>
          <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('entity.position.code').d('岗位编码')}
              >
                {getFieldDecorator('positionCode', {})(
                  <Input trim typeCase="upper" inputChinese={false} />
                )}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('entity.position.name').d('岗位名称')}
              >
                {getFieldDecorator('positionName', {})(<Input />)}
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
