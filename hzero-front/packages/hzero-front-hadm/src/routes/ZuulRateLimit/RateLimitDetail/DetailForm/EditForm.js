/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/19
 * @copyright 2019 ® HAND
 */

import React from 'react';
import {
  Col,
  Form,
  Input,
  InputNumber,
  Popconfirm,
  Row,
  Select,
  Table,
  Icon,
  Tooltip,
} from 'hzero-ui';
import { difference } from 'lodash';

import Lov from 'components/Lov';
import Switch from 'components/Switch';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { operatorRender } from 'utils/renderer';

import { getDimensionMeaningDataIndex } from './utils';

const EditFrom = ({
  form,
  initData,
  dimensionTypes,
  match,
  onRecordAdd,
  onRecordEdit,
  onRecordDelete,
  onTableChange,
  dimensionConfigsDataSource,
  dimensionConfigsPagination,
  dimensionAllowChange,
  interactedRef,
  isCreate,
  deleteDimensionConfigsLoading = false,
}) => {
  const rateLimitDimension = form.getFieldValue('rateLimitDimension') || [];
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
  /**
   * 维度规则表格列
   * @type {unknown}
   */
  const columns = React.useMemo(() => {
    if (isCreate) {
      return [];
    } else {
      return rateLimitDimension
        .map((dimensionValue) => {
          const dimensionItem = dimensionTypes.find((item) => item.value === dimensionValue);
          return (
            dimensionItem && {
              // 仅做显示
              title: dimensionItem.meaning,
              dataIndex: getDimensionMeaningDataIndex(dimensionItem.value),
            }
          );
        })
        .filter(Boolean)
        .concat([
          // 操作列
          {
            title: intl.get('hzero.common.button.action').d('操作'),
            key: 'operator',
            fixed: 'right',
            width: 120,
            render: (_, record) => {
              const actions = [];
              actions.push({
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '限流规则-维度-编辑限流规则(表格)',
                      },
                    ]}
                    key="action-edit"
                    onClick={() => {
                      onRecordEdit(form.getFieldValue('rateLimitDimension') || [], record);
                    }}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              });
              actions.push({
                ele: (
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    key="action-edit"
                    onConfirm={() => {
                      onRecordDelete(record);
                    }}
                  >
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.delete`,
                          type: 'button',
                          meaning: '限流规则-维度-删除限流规则(表格)',
                        },
                      ]}
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              });
              return operatorRender(actions);
            },
          },
        ]);
    }
  }, [dimensionTypes, isCreate, [...rateLimitDimension].sort().join(',')]);
  /**
   * 新增按钮点击
   * @type {Function}
   */
  const handleAdd = React.useCallback(() => {
    onRecordAdd(form.getFieldValue('rateLimitDimension'));
  }, [onRecordAdd, form]);

  // 如果可以修改维度, 那么修改维度后就不能修改维度规则
  const dimensionAllowChangeDuringUpdate =
    !dimensionAllowChange ||
    (dimensionAllowChange &&
      // dimensionAllowChange true -> 维度有值
      // 当前没有更新过 url/维度值
      difference(form.getFieldValue('rateLimitDimension'), initData.rateLimitDimension).length ===
        0 &&
      // 不存在 url 模板
      ((!form.getFieldValue('data-extra__rateLimitDimension_url') &&
        !initData['data-extra__rateLimitDimension_url']) ||
        // 存在url模板
        initData['data-extra__rateLimitDimension_url'] ===
          form.getFieldValue('data-extra__rateLimitDimension_url')));

  return (
    <Form>
      <Row type="flex">
        <Col {...FORM_COL_2_LAYOUT}>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`hadm.common.model.common.serviceRoute`).d('服务路由')}
          >
            {form.getFieldDecorator('serviceRouteId', {
              initialValue: initData.serviceRouteId,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get(`hadm.common.model.common.serviceRoute`).d('服务路由'),
                  }),
                },
              ],
            })(<Lov code="HADM.SERVICE_ROUTE" textValue={initData.path} />)}
          </Form.Item>
        </Col>
        <Col {...FORM_COL_2_LAYOUT}>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl
              .get(`hadm.zuulRateLimit.model.zuulRateLimit.replenishRate`)
              .d('每秒流量限制值')}
          >
            {form.getFieldDecorator('replenishRate', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get(`hadm.zuulRateLimit.model.zuulRateLimit.replenishRate`)
                      .d('每秒流量限制值'),
                  }),
                },
              ],
              initialValue: initData.replenishRate,
            })(<InputNumber min={1} />)}
          </Form.Item>
        </Col>
        <Col {...FORM_COL_2_LAYOUT}>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl
              .get('hadm.zuulRateLimit.model.zuulRateLimit.rateLimitDimension')
              .d('限流维度')}
          >
            {form.getFieldDecorator('rateLimitDimension', {
              initialValue: isCreate ? [] : initData.rateLimitDimension,
              rules: [
                {
                  type: 'array',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hadm.zuulRateLimit.model.zuulRateLimit.rateLimitDimension')
                      .d('限流维度'),
                  }),
                },
              ],
            })(
              <Select allowClear disabled={!dimensionAllowChange} mode="multiple">
                {dimensionTypes.map((item) => (
                  <Select.Option value={item.value} key={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
        </Col>
        <Col {...FORM_COL_2_LAYOUT}>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl
              .get(`hadm.zuulRateLimit.model.zuulRateLimit.burstCapacity`)
              .d('突发流量限制值')}
          >
            {form.getFieldDecorator('burstCapacity', {
              initialValue: initData.burstCapacity,
            })(<InputNumber min={1} />)}
          </Form.Item>
        </Col>
        <Col {...FORM_COL_2_LAYOUT}>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`hzero.common.status.enable`).d('启用')}
          >
            {form.getFieldDecorator('enabledFlag', {
              initialValue: initData.enabledFlag === 0 ? 0 : 1,
            })(<Switch />)}
          </Form.Item>
        </Col>
      </Row>
      <Row>
        {rateLimitDimension.includes('url') && (
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={
                <span>
                  {intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.dataExtraUrl`).d('URL维度值')}
                  <Tooltip
                    title={intl
                      .get('hadm.zuulRateLimit.view.message.url')
                      .d(
                        'URL特殊字符需使用encode后的值,变量使用{数字}占位, 且不能包含空白和特殊字符?(){}&= 示例: /hiam/v1/{1}/{2}?tenantId={3}&roleId={4}'
                      )}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {form.getFieldDecorator('data-extra__rateLimitDimension_url', {
                initialValue: initData['data-extra__rateLimitDimension_url'],
                rules: [
                  {
                    pattern: /^(([^?(){}&=\s]|(\{\d+\}))*)+(\?([^?(){}&=\s]|(\{\d+\}))*=([^?(){}&=\s]|(\{\d+\}))*(&([^?(){}&=\s]|(\{\d+\}))*=([^?(){}&=\s]|(\{\d+\}))*)*)?$/,
                    message: intl
                      .get('hadm.zuul.RateLimit.view.validation.dimension.url')
                      .d('URL必须符合规则'),
                  },
                ],
              })(<Input disabled={!dimensionAllowChange} />)}
            </Form.Item>
          </Col>
        )}
      </Row>
      <Row>
        <Col>
          <Form.Item
            label={intl.get('hadm.zuulRateLimit.model.zuulRateLimit.dimensionLine').d('限流规则')}
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            {!dimensionAllowChangeDuringUpdate ? (
              intl
                .get('hadm.zuulRateLimit.view.message.notLimitDuringCreate')
                .d('保存限流路由后再编辑限流规则')
            ) : (
              <ButtonPermission
                permissionList={[
                  {
                    code: `${match.path}.button.valueAdd`,
                    type: 'button',
                    meaning: '限流规则-维度-新增限流规则',
                  },
                ]}
                onClick={handleAdd}
                htmlType="button"
                disabled={(form.getFieldValue('rateLimitDimension') || []).length === 0}
              >
                {intl.get('hadm.zuulRateLimit.view.form.create').d('新增限流规则')}
              </ButtonPermission>
            )}
          </Form.Item>
        </Col>
      </Row>
      {dimensionAllowChangeDuringUpdate && (
        <Row type="flex">
          <Col span={3} />
          <Col span={21}>
            <Table
              bordered
              loading={deleteDimensionConfigsLoading}
              rowKey="rateLimitDimId"
              dataSource={dimensionConfigsDataSource}
              pagination={dimensionConfigsPagination}
              onChange={onTableChange}
              columns={columns}
            />
          </Col>
        </Row>
      )}
    </Form>
  );
};

export default Form.create({ fieldNameProp: null })(EditFrom);
