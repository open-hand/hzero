/**
 * TypeApplication - 应用类型定义-详情表单
 * @date: 2019/8/22
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Row, Col, Input, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { EDIT_FORM_ROW_LAYOUT, FORM_COL_3_LAYOUT, EDIT_FORM_ITEM_LAYOUT } from 'utils/constants';

/**
 * 应用类型定义-详情表单
 * @extends {Component} - React.Component
 * @reactProps {object} instanceHeadInfo - 详情头部
 * @reactProps {array} composePolicyTypes - 编排策略
 * @reactProps {boolean} isCreate - 是否为新建
 * @reactProps {Function} onRef - 绑定元素
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class DetailForm extends Component {
  /**
   * 切换应用大类
   * @param {string} value - lov选中值
   */
  @Bind()
  handleChangeMajorCategory(value) {
    const {
      form: { setFieldsValue = () => {}, getFieldValue = () => {} },
      fetchMinorCategory = () => {},
    } = this.props;
    if (getFieldValue('majorCategory') !== value) {
      setFieldsValue({ minorCategory: undefined });
      if (value) {
        fetchMinorCategory({ parentValue: value });
      }
    }
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e, getFieldValue = () => {} },
      instanceHeadInfo = {},
      composePolicyTypes,
      isCreate = false,
      isTenant,
      minorCategoryList,
    } = this.props;
    const {
      applicationCode,
      applicationName,
      majorCategory,
      majorCategoryMeaning,
      minorCategory,
      interfaceId,
      interfaceName,
      composePolicy,
      enabledFlag,
      remark,
      tenantId,
      tenantName,
      fastFailFlag,
    } = instanceHeadInfo;
    return (
      <Form style={{ marginBottom: '30px' }}>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hitf.typeDefinition.model.typeDefinition.applicationCode')
                .d('应用代码')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('applicationCode', {
                initialValue: applicationCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hitf.typeDefinition.model.typeDefinition.applicationCode')
                        .d('应用代码'),
                    }),
                  },
                  {
                    max: 80,
                    message: intl.get('hzero.common.validation.max', {
                      max: 80,
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
                validateFirst: true,
              })(<Input typeCase="upper" disabled={!isCreate} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hitf.typeDefinition.model.typeDefinition.name').d('应用名称')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('applicationName', {
                initialValue: applicationName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.typeDefinition.model.typeDefinition.name').d('应用名称'),
                    }),
                  },
                  {
                    max: 255,
                    message: intl.get('hzero.common.validation.max', {
                      max: 255,
                    }),
                  },
                ],
                validateFirst: true,
              })(<Input />)}
            </Form.Item>
          </Col>
          {!isTenant && (
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl.get('hzero.common.model.tenantName').d('租户')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('tenantId', {
                  initialValue: tenantId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hzero.common.model.tenantName').d('租户'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HPFM.TENANT"
                    textValue={tenantName}
                    textField="tenantName"
                    disabled={!isCreate}
                  />
                )}
              </Form.Item>
            </Col>
          )}
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hitf.typeDefinition.model.typeDefinition.majorCategory')
                .d('应用大类')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('majorCategory', {
                initialValue: majorCategory,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hitf.typeDefinition.model.typeDefinition.majorCategory')
                        .d('应用大类'),
                    }),
                  },
                ],
              })(
                <Lov
                  code="HITF.APP_MAJOR_CATEGORY"
                  onChange={this.handleChangeMajorCategory}
                  textValue={majorCategoryMeaning}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hitf.typeDefinition.model.typeDefinition.minorCategory')
                .d('应用小类')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('minorCategory', {
                initialValue: minorCategory,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hitf.typeDefinition.model.typeDefinition.minorCategory')
                        .d('应用小类'),
                    }),
                  },
                ],
              })(
                <Select
                  allowClear
                  showSearch
                  disabled={!getFieldValue('majorCategory')}
                  optionFilterProp="children"
                >
                  {minorCategoryList.map((item) => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hitf.typeDefinition.model.typeDefinition.interfaceId').d('开放接口')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('interfaceId', {
                initialValue: interfaceId,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hitf.typeDefinition.model.typeDefinition.interfaceId')
                        .d('开放接口'),
                    }),
                  },
                ],
              })(
                <Lov
                  code="HITF.COMPOSE_ENTRY_INTERFACE"
                  textValue={interfaceName}
                  disabled={!isCreate}
                />
              )}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hitf.typeDefinition.model.typeDefinition.composePolicy')
                .d('编排策略')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('composePolicy', {
                initialValue: composePolicy,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hitf.typeDefinition.model.typeDefinition.composePolicy')
                        .d('编排策略'),
                    }),
                  },
                ],
              })(
                <Select allowClear disabled={!isCreate}>
                  {composePolicyTypes.map((item) => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hzero.common.status.enable').d('启用')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('enabledFlag', {
                initialValue: isCreate ? 1 : enabledFlag,
              })(<Switch />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hitf.typeDefinition.model.typeDefinition.remark').d('说明')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('remark', {
                initialValue: remark,
              })(<Input />)}
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hitf.typeDefinition.model.typeDefinition.fastFail').d('快速失败')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('fastFailFlag', {
                initialValue: isCreate ? 1 : fastFailFlag,
              })(<Switch />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
