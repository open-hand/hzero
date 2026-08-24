/**
 * DimensionConfigsEditForm - 限流方式 维度编辑表单
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/19
 * @copyright 2019 ® HAND
 */

import React from 'react';
import { Form, Input, InputNumber, Tooltip, Icon, Row, Col } from 'hzero-ui';
import { isUndefined, words } from 'lodash';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { getCurrentOrganizationId } from 'utils/utils';

// import URLDimensionRuleInput from '../../components/URLDimensionRuleInput';

import {
  getDimensionDataIndex,
  getDimensionMeaningDataIndex,
  getDimensionValueDataIndex,
} from './utils';

/**
 * @param form - Form 表单
 * @param {string[]} dimensions - 已经选择的维度
 * @param {boolean} isCreate - 是否是新建
 * @param {object} dimensionConfig - 注意 dimensionConfig 一定会有值, 且只有在重新 编辑/新建 的时候才会更新
 * @param {object[]} dimensionTypes - 维度值集
 * @param {React.RefObject} interactedRef - 将本表单获取数据的方法传递出去
 * @param {object} rateLimitLine - 限流规则行信息
 * @returns {*}
 * @constructor
 */
const DimensionConfigsEditForm = ({
  form,
  dimensions = [],
  isCreate = true,
  dimensionConfig,
  dimensionTypes = [],
  interactedRef,
  rateLimitLine, // 只有在新建规则后才能编辑维度规则
}) => {
  let tenantId = form.getFieldValue(getDimensionValueDataIndex('tenant'));
  if (isUndefined(tenantId)) {
    tenantId = dimensionConfig[getDimensionValueDataIndex('tenant')];
  }
  React.useImperativeHandle(
    interactedRef,
    () => ({
      getValidateData() {
        return new Promise((resolve, reject) => {
          form.validateFields((err, values) => {
            if (err) {
              reject(err);
            } else {
              resolve(values);
            }
          });
        });
      },
    }),
    [interactedRef, form]
  );

  return (
    <Form>
      {dimensions
        .map(dimension => {
          const dimensionItem = dimensionTypes.find(item => item.value === dimension);
          const fieldName = getDimensionValueDataIndex(dimensionItem.value);
          const meaningField = getDimensionMeaningDataIndex(dimensionItem.value);
          const hasTenant = dimensions.includes('tenant');
          let inputEle;
          switch (dimensionItem.value) {
            case 'user':
              inputEle = (
                <Form.Item label={dimensionItem.meaning} {...MODAL_FORM_ITEM_LAYOUT}>
                  {form.getFieldDecorator(fieldName, {
                    initialValue: isCreate ? undefined : dimensionConfig[fieldName],
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: dimensionItem.meaning,
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code="HIAM.TENANT.USER"
                      textField={meaningField}
                      textValue={dimensionConfig[meaningField]}
                      queryParams={{
                        organizationId: hasTenant ? tenantId : getCurrentOrganizationId(),
                      }}
                      disabled={hasTenant && isUndefined(tenantId)}
                    />
                  )}
                </Form.Item>
              );
              break;
            case 'role':
              inputEle = (
                <Form.Item label={dimensionItem.meaning} {...MODAL_FORM_ITEM_LAYOUT}>
                  {form.getFieldDecorator(fieldName, {
                    initialValue: isCreate ? undefined : dimensionConfig[fieldName],
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: dimensionItem.meaning,
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code="HIAM.TENANT_ROLE"
                      textField={meaningField}
                      textValue={dimensionConfig[meaningField]}
                      queryParams={{
                        tenantId: dimensions.includes('tenant')
                          ? tenantId
                          : getCurrentOrganizationId(),
                      }}
                      disabled={hasTenant && isUndefined(tenantId)}
                    />
                  )}
                </Form.Item>
              );
              break;
            case 'tenant':
              inputEle = (
                <Form.Item label={dimensionItem.meaning} {...MODAL_FORM_ITEM_LAYOUT}>
                  {form.getFieldDecorator(fieldName, {
                    initialValue: isCreate ? undefined : dimensionConfig[fieldName],
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: dimensionItem.meaning,
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code="HPFM.TENANT"
                      textField={meaningField}
                      textValue={dimensionConfig[meaningField]}
                    />
                  )}
                </Form.Item>
              );
              break;
            case 'url':
              {
                const args = words(
                  rateLimitLine['data-extra__rateLimitDimension_url'] || '',
                  /\{\d+\}/g
                );
                inputEle = (
                  <>
                    {args.map((arg, argIndex) => {
                      if (argIndex === 0) {
                        return (
                          <Form.Item
                            label={
                              <span>
                                {dimensionItem.meaning}
                                <Tooltip
                                  title={intl
                                    .get('hadm.zuulRateLimit.view.message.url.arg')
                                    .d('URL参数不能包含;')}
                                >
                                  <Icon type="question-circle-o" />
                                </Tooltip>
                              </span>
                            }
                            {...MODAL_FORM_ITEM_LAYOUT}
                          >
                            {form.getFieldDecorator(`${fieldName}[${argIndex}]`, {
                              initialValue: isCreate
                                ? undefined
                                : (dimensionConfig[fieldName] || [])[argIndex],
                              rules: [
                                {
                                  required: true,
                                  message: intl.get('hzero.common.validation.notNull', {
                                    name: dimensionItem.meaning,
                                  }),
                                },
                                {
                                  pattern: /^[^;]+$/,
                                  message: intl
                                    .get('hadm.zuulRateLimit.view.validation.url.argg')
                                    .d('参数不合法'),
                                },
                              ],
                            })(<Input />)}
                          </Form.Item>
                        );
                      } else {
                        return (
                          <Row type="flex">
                            <Col {...MODAL_FORM_ITEM_LAYOUT.labelCol} />
                            <Col {...MODAL_FORM_ITEM_LAYOUT.wrapperCol}>
                              <Form.Item>
                                {form.getFieldDecorator(`${fieldName}[${argIndex}]`, {
                                  initialValue: isCreate
                                    ? undefined
                                    : (dimensionConfig[fieldName] || [])[argIndex],
                                  rules: [
                                    {
                                      required: true,
                                      message: intl.get('hzero.common.validation.notNull', {
                                        name: dimensionItem.meaning,
                                      }),
                                    },
                                    {
                                      pattern: /^[^;]+$/,
                                      message: intl
                                        .get('hadm.zuulRateLimit.view.validation.url.argg')
                                        .d('参数不合法'),
                                    },
                                  ],
                                })(<Input />)}
                              </Form.Item>
                            </Col>
                          </Row>
                        );
                      }
                    })}
                  </>
                );
              }
              break;
            default:
              break;
          }
          return (
            <>
              {/* 维度 */}
              {form.getFieldDecorator(getDimensionDataIndex(dimensionItem.value), {
                initialValue: dimensionItem.value,
              })(<></>)}
              {/* 维度值 */}
              {inputEle || (
                <Form.Item label={dimensionItem.meaning} {...MODAL_FORM_ITEM_LAYOUT}>
                  {form.getFieldDecorator(fieldName, {
                    initialValue: isCreate ? undefined : dimensionConfig[fieldName],
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: dimensionItem.meaning,
                        }),
                      },
                    ],
                  })(<Input />)}
                </Form.Item>
              )}
            </>
          );
        })
        .filter(Boolean)}
      <Form.Item
        {...MODAL_FORM_ITEM_LAYOUT}
        label={intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.replenishRate`).d('每秒流量限制值')}
      >
        {form.getFieldDecorator('replenishRate', {
          rules: [
            {
              required: true,
              message: intl.get('hzero.common.validation.notNull', {
                name: intl
                  .get('hadm.zuulRateLimit.model.zuulRateLimit.replenishRate')
                  .d('每秒流量限制值'),
              }),
            },
          ],
          initialValue: isCreate ? 1 : dimensionConfig.replenishRate,
        })(<InputNumber min={1} />)}
      </Form.Item>
      <Form.Item
        {...MODAL_FORM_ITEM_LAYOUT}
        label={intl.get('hadm.zuulRateLimit.model.zuulRateLimit.burstCapacity').d('突发流量限制值')}
      >
        {form.getFieldDecorator('burstCapacity', {
          initialValue: isCreate ? undefined : dimensionConfig.burstCapacity,
        })(<InputNumber min={1} />)}
      </Form.Item>
    </Form>
  );
};

export default Form.create({ fieldNameProp: null })(DimensionConfigsEditForm);
