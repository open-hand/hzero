import React, { Component } from 'react';
import { Form, Input, Row, Col, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { FORM_COL_3_LAYOUT, EDIT_FORM_ITEM_LAYOUT, EDIT_FORM_ROW_LAYOUT } from 'utils/constants';

import TLEditor from 'components/TLEditor';
import Switch from 'components/Switch';
import { CODE_UPPER } from 'utils/regExp';

const { Option } = Select;

const promptCode = 'hpfm.dashboardClause';

/**
 * 条目配置详情表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class DetailForm extends Component {
  constructor(props) {
    super(props);
    const { onRef } = props;
    if (onRef) onRef(this);
    this.state = {};
  }

  @Bind()
  handleChangeIdType(formObj) {
    const {
      form: { setFieldsValue },
    } = this.props;
    setFieldsValue(formObj);
  }

  render() {
    const { form, headInfo = {}, flags, isEdit } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get(`${promptCode}.model.dashboard.clauseCode`).d('条目代码')}
            >
              {getFieldDecorator('clauseCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.model.dashboard.clauseCode`).d('条目代码'),
                    }),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
                initialValue: headInfo.clauseCode,
              })(
                isEdit ? (
                  <Col>{headInfo.clauseCode}</Col>
                ) : (
                  <Input disabled={isEdit} trim typeCase="upper" inputChinese={false} />
                )
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get(`${promptCode}.model.dashboard.clauseName`).d('条目名称')}
            >
              {getFieldDecorator('clauseName', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.model.dashboard.clauseName`).d('条目名称'),
                    }),
                  },
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
                initialValue: headInfo.clauseName,
              })(
                <TLEditor
                  label={intl.get(`${promptCode}.model.dashboard.clauseName`).d('条目名称')}
                  field="clauseName"
                  token={headInfo ? headInfo._token : null}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get(`${promptCode}.model.dashboard.menuCode`).d('功能代码')}
            >
              {getFieldDecorator('menuCode', {
                rules: [
                  {
                    max: 128,
                    message: intl.get('hzero.common.validation.max', {
                      max: 128,
                    }),
                  },
                ],
                initialValue: headInfo.menuCode,
              })(<Input inputChinese={false} />)}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get(`${promptCode}.model.dashboard.route`).d('路由')}
            >
              {getFieldDecorator('route', {
                rules: [
                  {
                    max: 128,
                    message: intl.get('hzero.common.validation.max', {
                      max: 128,
                    }),
                  },
                ],
                initialValue: headInfo.route,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get(`${promptCode}.model.dashboard.statsExpression`).d('数据匹配表达式')}
            >
              {getFieldDecorator('statsExpression', {
                rules: [
                  {
                    max: 360,
                    message: intl.get('hzero.common.validation.max', {
                      max: 360,
                    }),
                  },
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get(`${promptCode}.model.dashboard.statsExpression`)
                        .d('数据匹配表达式'),
                    }),
                  },
                ],
                initialValue: headInfo.statsExpression,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get(`${promptCode}.model.dashboard.docRemarkExpression`)
                .d('单据标题表达式')}
            >
              {getFieldDecorator('docRemarkExpression', {
                rules: [
                  {
                    max: 360,
                    message: intl.get('hzero.common.validation.max', {
                      max: 360,
                    }),
                  },
                ],
                initialValue: headInfo.docRemarkExpression,
              })(<Input />)}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT} className="last-form-item">
          {!isTenantRoleLevel() && (
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...EDIT_FORM_ITEM_LAYOUT}
                label={intl.get(`${promptCode}.model.dashboard.dataTenantLevel`).d('层级')}
              >
                {getFieldDecorator('dataTenantLevel', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get(`${promptCode}.model.dashboard.dataTenantLevel`).d('层级'),
                      }),
                    },
                  ],
                  initialValue: headInfo.dataTenantLevel,
                })(
                  <Select>
                    {flags.map((item) => (
                      <Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                    {/* HPFM.DATA_TENANT_LEVEL */}
                  </Select>
                )}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get(`${promptCode}.model.dashboard.remark`).d('备注')}
            >
              {getFieldDecorator('remark', {
                initialValue: headInfo.remark,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item {...EDIT_FORM_ITEM_LAYOUT} label={intl.get(`hzero.common.status`).d('状态')}>
              {getFieldDecorator('enabledFlag', {
                initialValue: headInfo.enabledFlag === 0 ? 0 : 1,
              })(<Switch />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
