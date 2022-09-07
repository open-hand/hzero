/**
 * DetailForm - 服务计费配置 - 详情表单
 * @date: 2019/8/28
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Row, Col, Input, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import {
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT,
  DETAIL_EDIT_FORM_CLASSNAME,
} from 'utils/constants';

const FormItem = Form.Item;

/**
 * 服务计费配置 - 详情表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} dataSource - 数据源
 * @reactProps {Boolean} isCreate - 是否为新建计费组
 * @reactProps {Boolean} isPublished - 计费组状态是否为已发布
 * @reactProps {Boolean} isTenant - 是否是租户级
 * @reactProps {Boolean} cycleTypes - 账单周期值集
 * @return React.element
 */

@Form.create({ fieldNameProp: null })
export default class DetailForm extends PureComponent {
  state = {
    currentChargeServiceMeaning: '', // 服务说明
  };

  /**
   * 修改服务代码
   * @param {string} value - 选中的值
   * @param {object} record - 选中的行
   */
  @Bind()
  changeChargeService(value, record) {
    this.setState({
      currentChargeServiceMeaning: record.meaning,
    });
  }

  render() {
    const {
      dataSource,
      form: { getFieldDecorator },
      cycleTypes = [],
      isCreate,
      isTenant,
      isPublished,
    } = this.props;
    const {
      groupCode,
      groupName,
      tenantName,
      tenantId,
      chargeService,
      chargeServiceMeaning,
      billCycle,
      remark,
      statusMeaning,
      billCycleMeaning,
    } = dataSource;
    const { currentChargeServiceMeaning } = this.state;
    return (
      <Form className={DETAIL_EDIT_FORM_CLASSNAME}>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            {isPublished ? (
              <FormItem
                label={intl.get('hchg.serviceCharge.model.serviceCharge.groupCode').d('计费组代码')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {groupCode}
              </FormItem>
            ) : (
              <FormItem
                label={intl.get('hchg.serviceCharge.model.serviceCharge.groupCode').d('计费组代码')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('groupCode', {
                  initialValue: groupCode,
                  validateFirst: true,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hchg.serviceCharge.model.serviceCharge.groupCode')
                          .d('计费组代码'),
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
                })(<Input trim typeCase="upper" inputChinese={false} disabled={!isCreate} />)}
              </FormItem>
            )}
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            {isPublished ? (
              <FormItem
                label={intl.get('hchg.serviceCharge.model.serviceCharge.groupName').d('计费组名称')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {groupName}
              </FormItem>
            ) : (
              <FormItem
                label={intl.get('hchg.serviceCharge.model.serviceCharge.groupName').d('计费组名称')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('groupName', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hchg.serviceCharge.model.serviceCharge.groupName')
                          .d('计费组名称'),
                      }),
                    },
                    {
                      max: 255,
                      message: intl.get('hzero.common.validation.max', {
                        max: 255,
                      }),
                    },
                  ],
                  initialValue: groupName,
                })(<Input typeCase="upper" />)}
              </FormItem>
            )}
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            {isPublished ? (
              <FormItem
                label={intl.get('hzero.common.model.common.tenantId').d('租户')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {tenantName}
              </FormItem>
            ) : isTenant ? null : (
              <FormItem
                label={intl.get('hzero.common.model.common.tenantId').d('租户')}
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
              </FormItem>
            )}
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            {isPublished ? (
              <FormItem
                label={intl
                  .get('hchg.serviceCharge.model.serviceCharge.chargeService')
                  .d('服务代码')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {chargeService}
              </FormItem>
            ) : (
              <FormItem
                label={intl
                  .get('hchg.serviceCharge.model.serviceCharge.chargeService')
                  .d('服务代码')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('chargeService', {
                  initialValue: chargeService,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hchg.serviceCharge.model.serviceCharge.chargeService')
                          .d('服务代码'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HCHG.CHARGE_SERVICE"
                    textValue={chargeService}
                    onChange={this.changeChargeService}
                    disabled={!isCreate}
                  />
                )}
              </FormItem>
            )}
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            {isCreate ? (
              <FormItem
                label={intl
                  .get('hchg.purchaseDetail.model.serviceCharge.serviceName')
                  .d('服务说明')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('chargeServiceMeaning', {
                  initialValue: currentChargeServiceMeaning,
                })(<Input disabled />)}
              </FormItem>
            ) : (
              <FormItem
                label={intl
                  .get('hchg.purchaseDetail.model.serviceCharge.serviceName')
                  .d('服务说明')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {currentChargeServiceMeaning || chargeServiceMeaning}
              </FormItem>
            )}
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            {isPublished ? (
              <FormItem
                label={intl.get('hchg.serviceCharge.model.serviceCharge.remark').d('说明')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {remark}
              </FormItem>
            ) : (
              <FormItem
                label={intl.get('hchg.serviceCharge.model.serviceCharge.remark').d('说明')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('remark', {
                  initialValue: remark,
                })(<Input />)}
              </FormItem>
            )}
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            {isPublished ? (
              <FormItem
                label={intl.get('hchg.serviceCharge.model.serviceCharge.billCycle').d('账单周期')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {billCycleMeaning}
              </FormItem>
            ) : (
              <FormItem
                label={intl.get('hchg.serviceCharge.model.serviceCharge.billCycle').d('账单周期')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('billCycle', {
                  initialValue: billCycle,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hchg.serviceCharge.model.serviceCharge.billCycle')
                          .d('账单周期'),
                      }),
                    },
                  ],
                })(
                  <Select allowClear>
                    {cycleTypes.length &&
                      cycleTypes.map(item => (
                        <Select.Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                  </Select>
                )}
              </FormItem>
            )}
          </Col>
          {!isCreate && (
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hchg.serviceCharge.model.serviceCharge.status').d('状态')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {statusMeaning}
              </FormItem>
            </Col>
          )}
        </Row>
      </Form>
    );
  }
}
