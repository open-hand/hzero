/**
 * DataGroup- 数据组管理-详情表单
 * @date: 2019/7/15
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Row, Col, Input } from 'hzero-ui';
import Switch from 'components/Switch';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import { EDIT_FORM_ROW_LAYOUT, FORM_COL_3_LAYOUT, EDIT_FORM_ITEM_LAYOUT } from 'utils/constants';

/**
 * 数据组管理-详情表单
 * @extends {Component} - React.Component
 * @reactProps {object} dataGroupHeadInfo - 数据源
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class DetailForm extends Component {
  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) {
      onRef(this);
    }
  }

  render() {
    const {
      form: { getFieldDecorator = e => e },
      dataGroupHeadInfo = {},
    } = this.props;
    const { groupCode, groupName, remark, tenantName, tenantId, enabledFlag } = dataGroupHeadInfo;
    return (
      <Form style={{ marginBottom: '30px' }}>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hpfm.dataGroup.model.dataGroup.code').d('代码')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('groupCode', {
                initialValue: groupCode,
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hpfm.dataGroup.model.dataGroup.name').d('名称')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('groupName', {
                initialValue: groupName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.dataGroup.model.dataGroup.name').d('名称'),
                    }),
                  },
                  {
                    max: 360,
                    message: intl.get('hzero.common.validation.max', {
                      max: 360,
                    }),
                  },
                ],
                validateFirst: true,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hpfm.dataGroup.model.dataGroup.remark').d('说明')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('remark', {
                initialValue: remark,
              })(<Input />)}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item label={intl.get('entity.tenant.tag').d('租户')} {...EDIT_FORM_ITEM_LAYOUT}>
              {getFieldDecorator('tenantId', {
                initialValue: tenantId,
              })(<Lov code="HPFM.TENANT" textValue={tenantName} disabled />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hpfm.dataGroup.model.dataGroup.isEnabled').d('是否启用')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('enabledFlag', {
                initialValue: enabledFlag,
              })(<Switch />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
