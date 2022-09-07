import React from 'react';
import { Cascader, Form, Input, Modal, Select, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Checkbox from 'components/Checkbox';
import Switch from 'components/Switch';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 16,
  },
};

@Form.create({ fieldNameProp: null })
export default class StorageDrawer extends React.PureComponent {
  @Bind()
  handleOk() {
    const { form, initData = {}, type, onOk = (e) => e } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        onOk({ ...initData, ...values, storageType: type });
      }
    });
  }

  render() {
    const {
      form,
      title,
      modalVisible,
      loading,
      type,
      onCancel = (e) => e,
      initLoading = false,
      initData = {},
      serverProviderList = [],
      prefixStrategyList = [],
      microsoftEndpointList = [],
    } = this.props;
    const {
      storageCode,
      domain,
      accessKeyId,
      accessKeySecret,
      appId,
      region,
      bucketPrefix,
      accessControl,
      prefixStrategy,
      endPoint = '',
      defaultFlag = 0,
      createBucketFlag = 1,
    } = initData;
    const { getFieldDecorator = (e) => e } = form;

    const initEndPoint = endPoint.match(/^(http|https)?:\/\/([\S]+)$/);

    const endPointBefore = getFieldDecorator('endPointBefore', {
      initialValue: `${!initEndPoint ? 'http' : initEndPoint && initEndPoint[1]}://`,
    })(
      <Select style={{ width: 90 }}>
        <Select.Option value="http://">http://</Select.Option>
        <Select.Option value="https://">https://</Select.Option>
      </Select>
    );
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        width={820}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Spin spinning={initLoading}>
          <Form>
            <Form.Item
              label={intl.get('hfile.storage.model.storage.storageCode').d('存储编码')}
              {...formItemLayout}
            >
              {getFieldDecorator('storageCode', {
                initialValue: storageCode,
                rules: [
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(<Input typeCase="upper" inputChinese={false} disabled={!!endPoint} />)}
            </Form.Item>
            <Form.Item
              label={intl.get('hfile.storage.model.storage.prefixStrategy').d('文件名前缀策略')}
              {...formItemLayout}
            >
              {getFieldDecorator('prefixStrategy', {
                initialValue: prefixStrategy,
              })(
                <Select allowClear>
                  {prefixStrategyList.map((item) => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
            {type !== '11' && (
              <Form.Item
                label={
                  type === '6' || type === '2' || type === '3' || type === '4'
                    ? intl.get('hfile.storage.model.storage.Domain2').d('代理地址')
                    : intl.get('hfile.storage.model.storage.Domain').d('域名(Domain)')
                }
                {...formItemLayout}
              >
                {getFieldDecorator('domain', {
                  initialValue: domain,
                  rules: [
                    {
                      required: type !== '2' && type !== '3' && type !== '4',
                      message: intl.get('hzero.common.validation.notNull', {
                        name:
                          type === '6' || type === '2' || type === '3' || type === '4'
                            ? intl.get('hfile.storage.model.storage.Domain2').d('代理地址')
                            : intl.get('hfile.storage.model.storage.Domain').d('域名(Domain)'),
                      }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            )}
            <Form.Item
              label={
                // eslint-disable-next-line no-nested-ternary
                type === '6'
                  ? intl.get('hfile.storage.model.storage.storageRoute').d('存储路径')
                  : type === '11'
                  ? intl.get('hfile.storage.model.storage.endPointMeaning').d('区域')
                  : intl.get('hfile.storage.model.storage.EndPoint').d('EndPoint')
              }
              {...formItemLayout}
            >
              {getFieldDecorator('endPoint', {
                initialValue:
                  !initEndPoint || type !== '3' ? endPoint : initEndPoint && initEndPoint[2],
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name:
                        type === '6'
                          ? intl.get('hfile.storage.model.storage.storageRoute').d('存储路径')
                          : intl.get('hfile.storage.model.storage.EndPoint').d('EndPoint'),
                    }),
                  },
                ],
              })(
                type === '11' ? (
                  <Select allowClear>
                    {microsoftEndpointList.map((item) => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                ) : (
                  <Input addonBefore={type === '3' ? endPointBefore : null} />
                )
              )}
            </Form.Item>
            {type !== '6' && (
              <>
                <Form.Item
                  label={intl.get('hfile.storage.model.storage.AccessKeyId').d('AccessKeyId')}
                  {...formItemLayout}
                >
                  {getFieldDecorator('accessKeyId', {
                    initialValue: accessKeyId,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hfile.storage.model.storage.AccessKeyId')
                            .d('AccessKeyId'),
                        }),
                      },
                    ],
                  })(<Input />)}
                </Form.Item>
                <Form.Item
                  label={intl
                    .get('hfile.storage.model.storage.accessKeySecret')
                    .d('AccessKeySecret')}
                  {...formItemLayout}
                >
                  {getFieldDecorator('accessKeySecret', {
                    initialValue: accessKeySecret,
                    rules: [
                      {
                        type: 'string',
                        required: false,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hfile.storage.model.storage.accessKeySecret')
                            .d('AccessKeySecret'),
                        }),
                      },
                    ],
                  })(
                    <Input
                      type="password"
                      autocomplete="new-password"
                      placeholder={intl.get('hfile.storage.view.validation.notChange').d('未更改')}
                    />
                  )}
                </Form.Item>
              </>
            )}
            {type === '4' && (
              <>
                <Form.Item
                  label={intl.get('hfile.storage.model.storage.AppId').d('AppId')}
                  {...formItemLayout}
                >
                  {getFieldDecorator('appId', {
                    initialValue: appId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hfile.storage.model.storage.AppId').d('AppId'),
                        }),
                      },
                    ],
                  })(<Input />)}
                </Form.Item>
              </>
            )}
            {(type === '2' || type === '4' || type === '7' || type === '8' || type === '12') && (
              <Form.Item
                label={intl.get('hfile.storage.model.storage.region').d('Bucket所属地区')}
                {...formItemLayout}
              >
                {getFieldDecorator('region', {
                  initialValue: region,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hfile.storage.model.storage.region').d('Bucket所属地区'),
                      }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            )}
            {type !== '6' && (
              <>
                <Form.Item
                  label={intl.get('hfile.storage.model.storage.accessControl').d('bucket权限控制')}
                  {...formItemLayout}
                >
                  {getFieldDecorator('accessControl', {
                    initialValue: accessControl ? [`${type}`, accessControl] : [],
                    rules: [
                      {
                        type: 'array',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hfile.storage.model.storage.accessControl')
                            .d('bucket权限控制'),
                        }),
                      },
                    ],
                  })(
                    <Cascader
                      placeholder=""
                      expandTrigger="hover"
                      allowClear={false}
                      options={serverProviderList.filter((item) => {
                        if (type) {
                          return item.value === type;
                        } else {
                          return item;
                        }
                      })}
                      fieldNames={{ label: 'meaning', value: 'value', children: 'children' }}
                    />
                  )}
                </Form.Item>
                <Form.Item
                  label={intl.get('hfile.storage.model.storage.prefix').d('bucket前缀')}
                  {...formItemLayout}
                >
                  {getFieldDecorator('bucketPrefix', {
                    initialValue: bucketPrefix,
                    rules: [
                      type !== '3' && {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hfile.storage.model.storage.prefix').d('bucket前缀'),
                        }),
                      },
                    ].filter(Boolean),
                  })(<Input inputChinese={false} typeCase="lower" />)}
                </Form.Item>
              </>
            )}
            {type !== '6' && (
              <Form.Item
                {...formItemLayout}
                label={intl.get('hfile.storage.model.storage.createBucketFlag').d('自动创建桶')}
              >
                {form.getFieldDecorator('createBucketFlag', {
                  initialValue: createBucketFlag,
                })(<Switch />)}
              </Form.Item>
            )}
            <Form.Item
              label={intl.get('hfile.storage.model.storage.defaultFlag').d('默认')}
              {...formItemLayout}
            >
              {getFieldDecorator('defaultFlag', {
                initialValue: defaultFlag,
              })(<Checkbox />)}
            </Form.Item>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
