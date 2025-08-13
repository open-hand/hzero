import React from 'react';
import { Col, Form, Input, InputNumber, Modal, Row, Select, Spin, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import Lov from 'components/Lov';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { FORM_FIELD_CLASSNAME } from 'utils/constants';
import { CODE_UPPER } from 'utils/regExp';
import { operatorRender } from 'utils/renderer';

import ItemForm from './ItemForm';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 15 },
};
@Form.create({ fieldNameProp: null })
export default class EmailForm extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      itemFormData: {},
    };
  }

  /**
   * @function showItemModal - 显示新增配置属性模态框
   * @param {object} record - 行数据
   */
  @Bind()
  showItemModal(record = {}) {
    this.setState({
      modalVisible: true,
      itemFormData: record,
    });
  }

  /**
   * @function handleDeleteItem - 删除服务器属性配置
   * @param {object} record - 行数据
   */
  @Bind()
  handleDeleteItem(record) {
    const { deleteItem } = this.props;
    deleteItem(record);
  }

  /**
   * @function hideItemModal - 隐藏新增配置属性模态框
   */
  @Bind()
  hideItemModal() {
    this.setState({
      modalVisible: false,
    });
  }

  /**
   * @function handleAddItem - 新增一条邮箱服务器配置项
   * @param {object} params - 新增参数
   * @param {string} params.propertyCode - 服务器配置项 - 属性名称
   * @param {string} params.propertyName - 服务器配置项 - 属性值
   */
  @Bind()
  handleAddItem(itemValue) {
    const { addItem } = this.props;
    const { itemFormData } = this.state;
    addItem(itemValue, itemFormData);
    this.hideItemModal();
  }

  @Bind()
  handleEmailOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  /**
   * 设置SSL默认值
   * @memberof EmailForm
   */
  @Bind()
  setDefaultSSL() {
    const { form, addItem } = this.props;
    const { itemFormData } = this.state;
    const arr = [
      {
        propertyId: uuid(),
        isCreate: true,
        propertyCode: 'mail.smtp.socketFactory.class',
        propertyValue: 'javax.net.ssl.SSLSocketFactory',
      },
      {
        propertyId: uuid(),
        isCreate: true,
        propertyCode: 'mail.smtp.socketFactory.port',
        propertyValue: '465',
      },
      {
        propertyId: uuid(),
        isCreate: true,
        propertyCode: 'mail.smtp.ssl.enable',
        propertyValue: 'true',
      },
    ];
    form.setFieldsValue({ port: 465 });
    addItem(arr, itemFormData, true);
  }

  render() {
    const {
      form,
      initData,
      path,
      itemList,
      title,
      modalVisible,
      loading,
      onCancel,
      isCopy,
      tenantRoleLevel,
      detailLoading = false,
      filterStrategyList = [],
      enums: { 'HMSG.EMAIL_PROTOCOL': protocolList = [] } = {},
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      tenantId,
      tenantName,
      serverCode,
      serverName,
      username,
      passwordEncrypted,
      host,
      port,
      sender,
      protocol,
      tryTimes,
      enabledFlag,
      filterStrategy,
      _token,
    } = initData;
    const columns = [
      {
        title: intl.get('hmsg.email.model.email.propertyCode').d('属性编码'),
        dataIndex: 'propertyCode',
        width: 150,
      },
      {
        title: intl.get('hmsg.email.model.email.propertyValue').d('属性值'),
        dataIndex: 'propertyValue',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        dataIndex: 'edit',
        render: (text, record) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.editServer`,
                      type: 'button',
                      meaning: '邮箱账户-编辑服务器配置',
                    },
                  ]}
                  onClick={() => this.showItemModal(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.deleteServer`,
                      type: 'button',
                      meaning: '邮箱账户-删除服务器配置',
                    },
                  ]}
                  onClick={() => this.handleDeleteItem(record)}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators);
        },
      },
    ];
    return (
      <Modal
        destroyOnClose
        title={isCopy ? intl.get('hzero.common.button.copy').d('复制') : title}
        visible={modalVisible}
        width="1000px"
        confirmLoading={loading}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={onCancel}
        onOk={this.handleEmailOk}
      >
        <Spin spinning={detailLoading}>
          <Row type="flex">
            {!tenantRoleLevel && (
              <Col span={12}>
                <FormItem {...formLayout} label={intl.get('entity.tenant.tag').d('租户')}>
                  {getFieldDecorator('tenantId', {
                    initialValue: tenantId === 0 ? 0 : tenantId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('entity.tenant.tag').d('租户'),
                        }),
                      },
                    ],
                  })(<Lov disabled={!!tenantName} textValue={tenantName} code="HPFM.TENANT" />)}
                </FormItem>
              </Col>
            )}
            <Col span={12}>
              <FormItem
                {...formLayout}
                label={intl.get('hmsg.common.view.accountCode').d('账户代码')}
              >
                {getFieldDecorator('serverCode', {
                  initialValue: serverCode,
                  rules: [
                    {
                      type: 'string',
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.common.view.accountCode').d('账户代码'),
                      }),
                    },
                    {
                      pattern: CODE_UPPER,
                      message: intl
                        .get('hzero.common.validation.codeUpper')
                        .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                    },
                  ],
                })(
                  <Input
                    typeCase="upper"
                    trim
                    inputChinese={false}
                    disabled={!!tenantName && !isCopy}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...formLayout}
                label={intl.get('hmsg.common.view.accountName').d('账户名称')}
              >
                {getFieldDecorator('serverName', {
                  initialValue: serverName,
                  rules: [
                    {
                      type: 'string',
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.common.view.accountName').d('账户名称'),
                      }),
                    },
                  ],
                })(
                  <TLEditor
                    label={intl.get('hmsg.common.view.accountName').d('账户名称')}
                    field="serverName"
                    token={_token}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...formLayout}
                label={intl.get('hmsg.email.model.email.userName').d('用户名')}
              >
                {getFieldDecorator('username', {
                  initialValue: isCopy ? undefined : username,
                  rules: [
                    {
                      type: 'string',
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.email.model.email.userName').d('用户名'),
                      }),
                    },
                  ],
                })(<Input trim />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...formLayout}
                label={intl.get('hmsg.email.model.email.password').d('密码')}
              >
                {getFieldDecorator('passwordEncrypted', {
                  initialValue: isCopy ? undefined : passwordEncrypted,
                  rules: [
                    {
                      type: 'string',
                      required: false,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.email.model.email.password').d('密码'),
                      }),
                    },
                    {
                      max: 110,
                      message: intl.get('hzero.common.validation.max', {
                        max: 110,
                      }),
                    },
                  ],
                })(
                  <Input
                    type="password"
                    placeholder={
                      tenantName ? intl.get('hzero.common.validation.notChange').d('未更改') : ''
                    }
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...formLayout}
                label={intl.get('hmsg.email.model.email.host').d('邮件服务器')}
              >
                {getFieldDecorator('host', {
                  initialValue: host,
                  rules: [
                    {
                      type: 'string',
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.email.model.email.host').d('邮件服务器'),
                      }),
                    },
                  ],
                })(<Input />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formLayout} label={intl.get('hmsg.email.model.email.port').d('端口')}>
                {getFieldDecorator('port', {
                  initialValue: port,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.email.model.email.port').d('端口'),
                      }),
                    },
                  ],
                })(<InputNumber precision={0} min={1} max={65535} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...formLayout}
                label={intl.get('hmsg.email.model.email.protocol').d('协议')}
              >
                {getFieldDecorator('protocol', {
                  initialValue: protocol,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.email.model.email.protocol').d('协议'),
                      }),
                    },
                  ],
                })(
                  <Select className={FORM_FIELD_CLASSNAME}>
                    {protocolList.map((item) => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...formLayout}
                label={intl.get('hmsg.email.model.email.tryTimes').d('重试次数')}
              >
                {getFieldDecorator('tryTimes', {
                  initialValue: tryTimes,
                  rules: [
                    {
                      type: 'number',
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.email.model.email.tryTimes').d('重试次数'),
                      }),
                    },
                  ],
                })(<InputNumber min={0} max={5} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...formLayout}
                label={intl.get('hmsg.email.model.email.sender').d('发送人')}
              >
                {getFieldDecorator('sender', {
                  initialValue: isCopy ? undefined : sender,
                  rules: [
                    {
                      type: 'string',
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.email.model.email.sender').d('发送人'),
                      }),
                    },
                  ],
                })(<Input />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
                {getFieldDecorator('enabledFlag', {
                  initialValue: enabledFlag === undefined ? 1 : enabledFlag,
                })(<Switch />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...formLayout}
                label={intl.get('hmsg.email.model.email.filterStrategy').d('安全策略')}
              >
                {getFieldDecorator('filterStrategy', {
                  initialValue: filterStrategy,
                })(
                  <Select allowClear className={FORM_FIELD_CLASSNAME}>
                    {filterStrategyList.map((item) => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row type="flex" justify="end" style={{ marginTop: '8px' }}>
            <Col span={23}>
              <div className="table-operator">
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.createServer`,
                      type: 'button',
                      meaning: '邮箱账户-新建服务器配置项',
                    },
                  ]}
                  onClick={this.showItemModal}
                >
                  {intl
                    .get('hmsg.email.view.message.title.modal.server.create')
                    .d('新建服务器配置项')}
                </ButtonPermission>
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.setSSLDefault`,
                      type: 'button',
                      meaning: '邮箱账户-设置SSL属性',
                    },
                  ]}
                  onClick={this.setDefaultSSL}
                >
                  {intl.get('hmsg.email.view.button.setDefaultSSL').d('设置SSL属性')}
                </ButtonPermission>
              </div>
              <Table
                bordered
                rowKey="propertyId"
                dataSource={itemList}
                columns={columns}
                pagination={false}
              />
            </Col>
          </Row>
          <ItemForm
            title={`${
              this.state.itemFormData.propertyId
                ? intl.get('hmsg.email.view.message.title.modal.server.edit').d('编辑服务器配置项')
                : intl
                    .get('hmsg.email.view.message.title.modal.server.create')
                    .d('新建服务器配置项')
            }`}
            modalVisible={this.state.modalVisible}
            initData={this.state.itemFormData}
            onCancel={this.hideItemModal}
            onOk={this.handleAddItem}
          />
        </Spin>
      </Modal>
    );
  }
}
