import React from 'react';
import { Form, Input, Modal, Spin, Icon, Tooltip, Button, Card, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import Upload from 'components/Upload/UploadButton';
import TLEditor from 'components/TLEditor';
import Switch from 'components/Switch';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { CODE } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { BKT_PUBLIC } from 'utils/config';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hadm.common'] })
export default class serviceManageForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: [],
      uuid: 0,
    };
  }

  /**
   * 关闭编辑的模态框
   */
  @Bind()
  handleCancel() {
    const { onCancel = (e) => e } = this.props;
    this.setState({
      key: [],
      uuid: 0,
    });
    onCancel();
  }

  /**
   * 更新服务数据
   */
  @Bind()
  handleSaveService() {
    const { form, onOk, initData } = this.props;
    const status = isEmpty(initData) ? 'create' : 'edit';
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue, status);
      }
    });
  }

  @Bind()
  handleRefresh() {
    const { currentServiceCode, onRefresh } = this.props;
    onRefresh(currentServiceCode);
  }

  /**
   * @function onUploadSuccess - 图片上传成功的回调函数
   * @param {object} file - 上传的文件对象
   */
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.registerField('serviceLogo');
      form.setFieldsValue({
        serviceLogo: file.response,
      });
    }
  }

  // 删除文件成功
  @Bind()
  onCancelSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        serviceLogo: '',
      });
    }
  }

  @Bind()
  handleAdd() {
    const { key, uuid } = this.state;
    const nextKeys = key.concat(uuid);
    this.setState({
      key: nextKeys,
      uuid: uuid + 1,
    });
  }

  /**
   * 移除接收者键，值
   * @param {*}
   */
  @Bind()
  handleDelete(k) {
    const { key } = this.state;
    this.setState({
      key: key.filter((r) => r !== k),
    });
  }

  @Bind()
  handleDeleteInit(record) {
    const { onDelete } = this.props;
    onDelete(record);
  }

  @Bind()
  handleSave(k) {
    const { form, onOk, currentServiceCode } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (fieldsValue[`name${k}`] !== undefined && fieldsValue[`path${k}`]) {
        const data = {
          name: fieldsValue[`name${k}`],
          path: fieldsValue[`path${k}`],
          sensitiveHeaders: fieldsValue[`sensitiveHeaders${k}`],
          stripPrefix: fieldsValue[`stripPrefix${k}`],
          url: fieldsValue[`url${k}`],
          serviceCode: currentServiceCode,
        };
        onOk(data, 'create');
        this.setState({
          key: [],
          uuid: 0,
        });
      }
    });
  }

  @Bind()
  handleSaveInit(record) {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (
        fieldsValue[`name${record.serviceRouteId}${record.serviceCode}`] !== undefined &&
        fieldsValue[`path${record.serviceRouteId}${record.serviceCode}`]
      ) {
        const data = {
          ...record,
          name: fieldsValue[`name${record.serviceRouteId}${record.serviceCode}`],
          path: fieldsValue[`path${record.serviceRouteId}${record.serviceCode}`],
          sensitiveHeaders:
            fieldsValue[`sensitiveHeaders${record.serviceRouteId}${record.serviceCode}`],
          stripPrefix: fieldsValue[`stripPrefix${record.serviceRouteId}${record.serviceCode}`],
          url: fieldsValue[`url${record.serviceRouteId}${record.serviceCode}`],
        };
        onOk(data, 'update');
      }
    });
  }

  render() {
    const {
      form,
      initData,
      title,
      modalVisible,
      loading,
      actionType,
      initLoading = false,
    } = this.props;
    const { getFieldDecorator } = form;
    const { key = [] } = this.state;
    const formItems =
      key &&
      key.map((k) => {
        return (
          <Card
            title={intl.get('hadm.common.title.serviceRoute').d('服务路由')}
            extra={
              <>
                <Tooltip title={intl.get('hzero.common.button.save').d('保存')}>
                  <Icon
                    style={{ fontSize: 20, marginRight: 6, cursor: 'pointer' }}
                    type="save"
                    onClick={() => this.handleSave(k)}
                  />
                </Tooltip>
                <Tooltip title={intl.get('hzero.common.button.delete').d('删除')}>
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    onConfirm={() => this.handleDelete(k)}
                  >
                    <Icon style={{ fontSize: 20, cursor: 'pointer' }} type="close" />
                  </Popconfirm>
                </Tooltip>
              </>
            }
            key={k}
            style={{ marginBottom: 20 }}
          >
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              labelCol={{
                span: 7,
              }}
              label={intl.get('hadm.serviceRoute.model.serviceRoute.name').d('路由标识')}
            >
              {getFieldDecorator(`name${k}`, {
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
              labelCol={{
                span: 7,
              }}
              label={
                <span>
                  {intl.get('hadm.serviceRoute.model.serviceRoute.matchPath').d('匹配路径')}
                  &nbsp;
                  <Tooltip
                    title={intl
                      .get('hadm.serviceRoute.view.message.matchPathMsg')
                      .d('匹配该路径规则的请求将会被路由到当前服务')}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {getFieldDecorator(`path${k}`, {
                initialValue: '',
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
              })(<Input trim inputChinese={false} />)}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              labelCol={{
                span: 7,
              }}
              label={
                <span>
                  {intl.get('hadm.serviceRoute.model.serviceRoute.url').d('物理路径')}
                  &nbsp;
                  <Tooltip
                    title={intl
                      .get('hadm.serviceRoute.view.message.urlMsg')
                      .d(
                        '路由到的物理路径。需要注意，该配置优先于根据服务名进行请求转发，例如：同时配置服务名称、物理路径时，请求将不会被转发到指定服务，而是被转发到该物理路径。'
                      )}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {getFieldDecorator(`url${key}`, {
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
              labelCol={{
                span: 7,
              }}
              label={
                <span>
                  {intl.get('hadm.serviceRoute.model.serviceRoute.stripPrefix').d('去掉前缀')}
                  &nbsp;
                  <Tooltip
                    title={intl
                      .get('hadm.serviceRoute.view.message.stripPrefixMsg')
                      .d(
                        '请求经过路由转发后，是否去掉路径前缀，例如：/hdemo/v1/test经过路由转发后，到后端服务的请求路径将会变成/v1/test'
                      )}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {getFieldDecorator(`stripPrefix${k}`, {})(<Switch />)}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              labelCol={{
                span: 7,
              }}
              label={
                <span>
                  {intl
                    .get('hadm.serviceRoute.model.serviceRoute.delHttpHeaderList')
                    .d('去除Http头列表')}
                  &nbsp;
                  <Tooltip
                    title={intl
                      .get('hadm.serviceRoute.view.message.delHttpHeaderMsg')
                      .d(
                        '请求经过路由转发后，移除http头列表，一般是为了避免头信息传递给下层服务。可配置多个请求头，用“,”隔开。例如：Cookie,Set-Cookie'
                      )}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {getFieldDecorator(`sensitiveHeaders${k}`, {
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
          </Card>
        );
      });
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        width={550}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={this.handleCancel}
        onOk={this.handleSaveService}
        footer={
          <>
            <Button key="cancel" onClick={this.handleCancel}>
              {intl.get('hzero.common.button.cancel').d('取消')}
            </Button>
            {actionType === 'route' && Array.isArray(initData) && initData.length > 0 && (
              <Button key="cancel" type="primary" onClick={this.handleRefresh}>
                {intl.get('hzero.common.button.refreshRouter').d('刷新路由')}
              </Button>
            )}
            {actionType === 'manage' && (
              <Button key="cancel" onClick={this.handleSaveService} type="primary">
                {isEmpty(initData)
                  ? intl.get('hadm.common.view.button.setUp').d('创建')
                  : intl.get('hzero.common.button.ok').d('确定')}
              </Button>
            )}
          </>
        }
      >
        <Spin spinning={initLoading}>
          <Form>
            {actionType === 'manage' && (
              <>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hadm.common.model.common.serviceCode').d('服务编码')}
                >
                  {getFieldDecorator('serviceCode', {
                    initialValue: initData && initData.serviceCode,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hadm.common.model.common.serviceCode').d('服务编码'),
                        }),
                      },
                      {
                        max: 30,
                        message: intl.get('hzero.common.validation.max', {
                          max: 30,
                        }),
                      },
                      {
                        pattern: CODE,
                        message: intl
                          .get('hzero.common.validation.code')
                          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                      },
                    ],
                  })(<Input trim inputChinese={false} disabled={!isEmpty(initData)} />)}
                </FormItem>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hadm.common.model.common.serviceName').d('服务名称')}
                >
                  {getFieldDecorator('serviceName', {
                    initialValue: initData && initData.serviceName,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hadm.common.model.common.serviceName').d('服务名称'),
                        }),
                      },
                      {
                        max: 90,
                        message: intl.get('hzero.common.validation.max', {
                          max: 90,
                        }),
                      },
                    ],
                  })(
                    <TLEditor
                      label={intl.get('hadm.common.model.common.serviceName').d('服务名称')}
                      field="serviceName"
                      token={initData && initData._token}
                    />
                  )}
                </FormItem>
                <FormItem
                  label={intl.get('hadm.common.model.serviceCollect.serviceLogo').d('服务图片')}
                  extra={intl.get('hadm.common.model.upload.support').d('上传格式：*.png;*.jpeg')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  <Upload
                    accept=".jpeg,.png"
                    single
                    fileList={
                      initData &&
                      initData.serviceLogo && [
                        {
                          uid: '-1',
                          name: initData && initData.serviceName,
                          status: 'done',
                          url: initData && initData.serviceLogo,
                        },
                      ]
                    }
                    bucketName={BKT_PUBLIC}
                    bucketDirectory="hadm01"
                    onUploadSuccess={this.onUploadSuccess}
                    onRemove={this.onCancelSuccess}
                  />
                </FormItem>
                <FormItem wrapperCol={{ span: 15, offset: 6 }}>
                  {getFieldDecorator('serviceLogo', {
                    initialValue: initData && initData.serviceLogo,
                  })(<div />)}
                </FormItem>
              </>
            )}
            {actionType === 'route' &&
              initData &&
              initData.map((item) => {
                return (
                  <>
                    <Card
                      title={intl.get('hadm.common.model.common.serviceRoute').d('服务路由')}
                      extra={
                        <>
                          <Tooltip title={intl.get('hzero.common.button.save').d('保存')}>
                            <Icon
                              style={{ fontSize: 20, marginRight: 6, cursor: 'pointer' }}
                              type="save"
                              onClick={() => this.handleSaveInit(item)}
                            />
                          </Tooltip>
                          <Tooltip title={intl.get('hzero.common.button.delete').d('删除')}>
                            <Popconfirm
                              title={intl
                                .get('hzero.common.message.confirm.delete')
                                .d('是否删除此条记录？')}
                              onConfirm={() => this.handleDeleteInit(item)}
                            >
                              <Icon style={{ fontSize: 20, cursor: 'pointer' }} type="close" />
                            </Popconfirm>
                          </Tooltip>
                        </>
                      }
                      style={{ marginBottom: 20 }}
                    >
                      <FormItem
                        {...MODAL_FORM_ITEM_LAYOUT}
                        labelCol={{
                          span: 7,
                        }}
                        label={intl.get('hadm.serviceRoute.model.serviceRoute.name').d('路由标识')}
                      >
                        {getFieldDecorator(`name${item.serviceRouteId}${item.serviceCode}`, {
                          initialValue: item.name,
                          rules: [
                            {
                              required: true,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hadm.serviceRoute.model.serviceRoute.name')
                                  .d('路由标识'),
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
                        labelCol={{
                          span: 7,
                        }}
                        label={
                          <span>
                            {intl
                              .get('hadm.serviceRoute.model.serviceRoute.matchPath')
                              .d('匹配路径')}
                            &nbsp;
                            <Tooltip
                              title={intl
                                .get('hadm.serviceRoute.view.message.matchPathMsg')
                                .d('匹配该路径规则的请求将会被路由到当前服务')}
                            >
                              <Icon type="question-circle-o" />
                            </Tooltip>
                          </span>
                        }
                      >
                        {getFieldDecorator(`path${item.serviceRouteId}${item.serviceCode}`, {
                          initialValue: item.path,
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
                        })(<Input trim disabled={!!item.path} inputChinese={false} />)}
                      </FormItem>
                      <FormItem
                        {...MODAL_FORM_ITEM_LAYOUT}
                        labelCol={{
                          span: 7,
                        }}
                        label={
                          <span>
                            {intl.get('hadm.serviceRoute.model.serviceRoute.url').d('物理路径')}
                            &nbsp;
                            <Tooltip
                              title={intl
                                .get('hadm.serviceRoute.view.message.urlMsg')
                                .d(
                                  '路由到的物理路径。需要注意，该配置优先于根据服务名进行请求转发，例如：同时配置服务名称、物理路径时，请求将不会被转发到指定服务，而是被转发到该物理路径。'
                                )}
                            >
                              <Icon type="question-circle-o" />
                            </Tooltip>
                          </span>
                        }
                      >
                        {getFieldDecorator(`url${item.serviceRouteId}${item.serviceCode}`, {
                          initialValue: item.url,
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
                        labelCol={{
                          span: 7,
                        }}
                        label={
                          <span>
                            {intl
                              .get('hadm.serviceRoute.model.serviceRoute.stripPrefix')
                              .d('去掉前缀')}
                            &nbsp;
                            <Tooltip
                              title={intl
                                .get('hadm.serviceRoute.view.message.stripPrefixMsg')
                                .d(
                                  '请求经过路由转发后，是否去掉路径前缀，例如：/hdemo/v1/test经过路由转发后，到后端服务的请求路径将会变成/v1/test'
                                )}
                            >
                              <Icon type="question-circle-o" />
                            </Tooltip>
                          </span>
                        }
                      >
                        {getFieldDecorator(`stripPrefix${item.serviceRouteId}${item.serviceCode}`, {
                          initialValue: item.stripPrefix,
                        })(<Switch />)}
                      </FormItem>
                      <FormItem
                        {...MODAL_FORM_ITEM_LAYOUT}
                        labelCol={{
                          span: 7,
                        }}
                        label={
                          <span>
                            {intl
                              .get('hadm.serviceRoute.model.serviceRoute.delHttpHeaderList')
                              .d('去除Http头列表')}
                            &nbsp;
                            <Tooltip
                              title={intl
                                .get('hadm.serviceRoute.view.message.delHttpHeaderMsg')
                                .d(
                                  '请求经过路由转发后，移除http头列表，一般是为了避免头信息传递给下层服务。可配置多个请求头，用“,”隔开。例如：Cookie,Set-Cookie'
                                )}
                            >
                              <Icon type="question-circle-o" />
                            </Tooltip>
                          </span>
                        }
                      >
                        {getFieldDecorator(
                          `sensitiveHeaders${item.serviceRouteId}${item.serviceCode}`,
                          {
                            initialValue: item.sensitiveHeaders,
                            rules: [
                              {
                                max: 240,
                                message: intl.get('hzero.common.validation.max', {
                                  max: 240,
                                }),
                              },
                            ],
                          }
                        )(<Input inputChinese={false} />)}
                      </FormItem>
                    </Card>
                  </>
                );
              })}
            {actionType === 'route' && (
              <>
                {formItems}
                <Card style={{ marginBottom: 20, cursor: 'pointer' }} onClick={this.handleAdd}>
                  <Icon type="plus" style={{ fontSize: 150, marginLeft: '30%', color: 'grey' }} />
                </Card>
              </>
            )}
          </Form>
        </Spin>
      </Modal>
    );
  }
}
