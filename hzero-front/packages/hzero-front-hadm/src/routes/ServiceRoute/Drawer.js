/* eslint-disable no-useless-escape */
/**
 * 服务编码字段 需要存储2个值 serviceId, serviceCode
 */

import React from 'react';
import { Form, Input, Modal, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { CODE } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class ServiceRouteForm extends React.PureComponent {
  @Bind()
  handleOK() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        if (fieldsValue.url === '') {
          onOk({ ...fieldsValue, url: null });
        } else {
          onOk(fieldsValue);
        }
      }
    });
  }

  render() {
    const {
      form,
      title,
      modalVisible,
      loading,
      onCancel,
      initData = {},
      initLoading = false,
    } = this.props;
    const { getFieldDecorator, setFieldsValue } = form;
    const {
      serviceId,
      serviceCode,
      serviceName,
      name,
      path,
      url,
      sensitiveHeaders,
      stripPrefix = 1,
    } = initData;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        width={620}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOK}
      >
        <Spin spinning={initLoading}>
          <Form>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hadm.common.model.common.serviceCode').d('服务编码')}
            >
              {getFieldDecorator('serviceCode', {
                initialValue: serviceCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hadm.common.model.common.serviceCode').d('服务编码'),
                    }),
                  },
                ],
              })(
                <Lov
                  code={isTenantRoleLevel() ? 'HADM.SERVICE.ORG' : 'HADM.SERVICE'}
                  lovOptions={{
                    valueField: 'serviceCode',
                    displayField: 'serviceCode',
                  }}
                  textValue={serviceCode}
                  disabled={!!serviceId}
                  onChange={(val, item) => {
                    setFieldsValue({
                      serviceName: item.serviceName,
                    });
                  }}
                />
              )}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hadm.common.model.common.serviceName').d('服务名称')}
            >
              {getFieldDecorator('serviceName', {
                initialValue: serviceName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hadm.common.model.common.serviceName').d('服务名称'),
                    }),
                  },
                ],
              })(<Input disabled />)}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hadm.serviceRoute.model.serviceRoute.name').d('路由标识')}
            >
              {getFieldDecorator('name', {
                initialValue: name,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hadm.serviceRoute.model.serviceRoute.name').d('路由标识'),
                    }),
                  },
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                  {
                    pattern: CODE,
                    message: intl
                      .get('hzero.common.validation.code')
                      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(<Input trim disabled={!!name} inputChinese={false} />)}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hadm.serviceRoute.model.serviceRoute.matchPath').d('匹配路径')}
            >
              {getFieldDecorator('path', {
                initialValue: path,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hadm.serviceRoute.model.serviceRoute.matchPath')
                        .d('匹配路径'),
                    }),
                  },
                  {
                    max: 120,
                    message: intl.get('hzero.common.validation.max', {
                      max: 120,
                    }),
                  },
                ],
              })(<Input trim disabled={!!path} inputChinese={false} />)}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hadm.serviceRoute.model.serviceRoute.url').d('物理路径')}
            >
              {getFieldDecorator('url', {
                initialValue: url,
                rules: [
                  {
                    max: 240,
                    message: intl.get('hzero.common.validation.max', {
                      max: 240,
                    }),
                  },
                  {
                    pattern: '([A-Za-z_]+)://(w+)(.w+)*(:[0-9_]+)?',
                    message: intl
                      .get('hadm.serviceRoute.model.serviceRoute.correctPhysicalPath')
                      .d('请输入正确的物理路径'),
                  },
                ],
              })(<Input inputChinese={false} />)}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hadm.serviceRoute.model.serviceRoute.stripPrefix').d('去掉前缀')}
            >
              {getFieldDecorator('stripPrefix', {
                initialValue: stripPrefix,
              })(<Switch />)}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl
                .get('hadm.serviceRoute.model.serviceRoute.delHttpHeaderList')
                .d('去除Http头列表')}
            >
              {getFieldDecorator('sensitiveHeaders', {
                initialValue: sensitiveHeaders,
                rules: [
                  {
                    max: 240,
                    message: intl.get('hzero.common.validation.max', {
                      max: 240,
                    }),
                  },
                ],
              })(<Input inputChinese={false} />)}
            </FormItem>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
