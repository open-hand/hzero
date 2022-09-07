/**
 * 服务器集群管理
 * @date: 2019-7-9
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { CODE_UPPER } from 'utils/regExp';

import Lov from 'components/Lov';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';

import styles from './styles.less';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class FilterForm extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {};
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
    const { form, isSiteFlag } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form className={`${styles['message-search-form']} more-fields-search-form`}>
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          {isSiteFlag && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('entity.tenant.tag').d('租户')}
              >
                {getFieldDecorator('tenantId', {})(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.serverCluster.model.serverCluster.clusterCode').d('集群编码')}
            >
              {getFieldDecorator('clusterCode', {
                rules: [
                  {
                    max: 180,
                    message: intl.get('hzero.common.validation.max', {
                      max: 180,
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(<Input inputChinese={false} trim typeCase="upper" />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.serverCluster.model.serverCluster.clusterName').d('集群名称')}
            >
              {getFieldDecorator('clusterName', {
                rules: [
                  {
                    max: 180,
                    message: intl.get('hzero.common.validation.max', {
                      max: 180,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.handleReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
