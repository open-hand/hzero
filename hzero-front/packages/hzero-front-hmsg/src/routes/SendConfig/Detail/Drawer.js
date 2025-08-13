import React, { PureComponent } from 'react';
import { Modal, Form, Input, Select, InputNumber, Table } from 'hzero-ui';
import { Lov as C7nLov } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import PropTypes from 'prop-types';
import { isEmpty, isNumber } from 'lodash';

import Switch from 'components/Switch';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';

import styles from './index.less';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const FormItem = Form.Item;
const { Option } = Select;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};
/**
 * 发送配置-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 操作对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  /**
   * state初始化
   */
  state = {
    selectedRowKeys: [],
    selectedRows: [],
  };

  /**
   * 组件属性定义
   */
  static propTypes = {
    anchor: PropTypes.oneOf(['left', 'right', 'top', 'bottom']),
    title: PropTypes.string,
    visible: PropTypes.bool,
    onOk: PropTypes.func,
    onCancel: PropTypes.func,
  };

  /**
   * 组件属性默认值设置
   */
  static defaultProps = {
    anchor: 'right',
    title: '',
    visible: false,
    onOk: (e) => e,
    onCancel: (e) => e,
  };

  componentDidMount() {
    const { onRef } = this.props;
    onRef(this.handleClearSelection);
  }

  @Bind()
  handleTableChange({ current = 1, pageSize = 10 } = {}) {
    const { onFetch } = this.props;
    onFetch({ page: current - 1, size: pageSize });
  }

  /**
   * 确定操作
   */
  @Bind()
  saveBtn() {
    const { form, onOk, onEditOk, itemData } = this.props;
    const { tempServerLineId } = itemData;
    if (onOk) {
      form.validateFields((err, values) => {
        if (isEmpty(err)) {
          // 校验通过，进行保存操作
          if (isEmpty(itemData)) {
            onOk(values);
          } else {
            onEditOk({ ...values, tempServerLineId });
          }
        }
      });
    }
  }

  /**
   * 改变消息类型，重置服务代码
   *
   * @memberof DetailModal
   */
  @Bind()
  changeTypeCode() {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({ serverId: undefined, serverName: undefined });
  }

  @Bind()
  getServerName(value, record) {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({ serverName: record.serverName, serverCode: record.serverCode });
  }

  @Bind()
  onChangeTemplate(value, record) {
    const { setFieldsValue, registerField } = this.props.form;
    registerField('templateName');
    setFieldsValue({ templateName: record.templateName });
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows) {
    this.setState({
      selectedRowKeys: selectedRows.map((r) => r.tempServerWhId),
      selectedRows,
    });
  }

  @Bind()
  handleClearSelection() {
    this.setState({
      selectedRowKeys: [],
      selectedRows: [],
    });
  }

  /**
   * 取消保存
   */
  @Bind()
  handleDeleteWebHook() {
    const { selectedRows = [] } = this.state;
    Modal.confirm({
      title: intl.get(`hzero.common.message.confirm.title`).d('提示'),
      content: intl.get(`hmsg.sendConfig.view.message.title.content`).d('确定删除吗？'),
      onOk: () => {
        const { onDelete = (e) => e } = this.props;
        onDelete(selectedRows);
      },
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      anchor,
      visible,
      title,
      form,
      itemData,
      onCancel,
      messageType = [],
      tenantId,
      fetchLoading,
      deleteLoading,
      webHook: { pagination = {}, webHookData = [] },
      ds,
      path,
    } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const isEdit = !isEmpty(itemData);
    const { selectedRowKeys } = this.state;
    const columns = [
      {
        title: intl.get('hmsg.common.view.accountCode').d('账户代码'),
        dataIndex: 'serverCode',
        width: 100,
      },
      {
        title: intl.get('hmsg.common.view.accountName').d('账户名称'),
        dataIndex: 'serverName',
      },
      {
        title: intl.get('hmsg.sendConfig.model.sendConfig.accountType').d('账户类型'),
        dataIndex: 'serverTypeMeaning',
        width: 100,
      },
    ];
    return (
      <Modal
        title={title}
        width={520}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.saveBtn}
        okText={intl.get('hzero.common.button.ok').d('确定')}
        onCancel={onCancel}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
        zIndex={999}
        destroyOnClose
      >
        <Form>
          <Form.Item
            label={intl.get('hmsg.sendConfig.model.sendConfig.templateId').d('模板代码')}
            {...formLayout}
          >
            {getFieldDecorator('templateCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hmsg.sendConfig.model.sendConfig.templateCode').d('模板代码'),
                  }),
                },
              ],
              initialValue: itemData.templateCode,
            })(
              <Lov
                code="HMSG.TEMP_SERVER.MESSAGE_TEMP"
                textValue={itemData.templateCode}
                disabled={tenantId === undefined}
                queryParams={{
                  tenantId,
                }}
                onChange={this.onChangeTemplate}
              />
            )}
          </Form.Item>
          <FormItem
            label={intl.get('hmsg.sendConfig.model.sendConfig.typeCode').d('消息类型')}
            {...formLayout}
          >
            {getFieldDecorator('typeCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hmsg.sendConfig.model.sendConfig.typeCode').d('消息类型'),
                  }),
                },
              ],
              initialValue: itemData.typeCode,
            })(
              <Select allowClear disabled={isEdit} onChange={this.changeTypeCode}>
                {messageType &&
                  messageType.map((item) => (
                    <Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
              </Select>
            )}
          </FormItem>
          {itemData.typeCode !== 'WEB_HOOK' && (
            <>
              <Form.Item
                label={intl.get('hmsg.common.view.accountCode').d('账户代码')}
                {...formLayout}
              >
                {getFieldDecorator('serverCode', {
                  rules: [
                    {
                      required:
                        getFieldValue('typeCode') !== 'WEB' &&
                        getFieldValue('typeCode') !== 'WEB_HOOK',
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.common.view.accountCode').d('账户代码'),
                      }),
                    },
                  ],
                  initialValue: itemData.serverCode,
                })(
                  <Lov
                    code="HMSG.SERVER"
                    textValue={itemData.serverCode}
                    disabled={
                      !getFieldValue('typeCode') ||
                      tenantId === undefined ||
                      getFieldValue('typeCode') === 'WEB' ||
                      getFieldValue('typeCode') === 'WEB_HOOK'
                    }
                    queryParams={{
                      typeCode: getFieldValue('typeCode'),
                      tenantId,
                    }}
                    onChange={this.getServerName}
                  />
                )}
              </Form.Item>
              <Form.Item
                label={intl.get('hmsg.common.view.accountName').d('账户名称')}
                {...formLayout}
              >
                {getFieldDecorator('serverName', {
                  rules: [
                    {
                      required: false,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.common.view.accountName').d('账户名称'),
                      }),
                    },
                  ],
                  initialValue: itemData.serverName,
                })(<Input disabled />)}
              </Form.Item>
            </>
          )}
          <Form.Item
            {...formLayout}
            label={intl.get('hmsg.sendConfig.model.sendConfig.tryTimes').d('重试次数')}
          >
            {getFieldDecorator('tryTimes', {
              initialValue: itemData.tryTimes,
            })(
              <InputNumber step={1} min={0} disabled={form.getFieldValue('typeCode') !== 'EMAIL'} />
            )}
          </Form.Item>
          <Form.Item label={intl.get('hzero.common.remark').d('备注')} {...formLayout}>
            {getFieldDecorator('remark', {
              rules: [
                {
                  required: false,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hzero.common.remark').d('备注'),
                  }),
                },
              ],
              initialValue: itemData.remark,
            })(<Input />)}
          </Form.Item>
          <Form.Item label={intl.get('hzero.common.status').d('状态')} {...formLayout}>
            {getFieldDecorator('enabledFlag', {
              initialValue: itemData.enabledFlag !== undefined ? itemData.enabledFlag : 1,
            })(<Switch />)}
          </Form.Item>

          {isEdit && itemData.typeCode === 'WEB_HOOK' && isNumber(itemData.objectVersionNumber) && (
            <>
              <div style={{ textAlign: 'right', marginBottom: '12px' }}>
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.deleteUser`,
                      type: 'button',
                      meaning: '消息发送配置-删除',
                    },
                  ]}
                  style={{ marginRight: 8 }}
                  onClick={this.handleDeleteWebHook}
                  disabled={selectedRowKeys.length === 0 || deleteLoading}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
                {/* <Lov
                  isButton
                  code='HMSG.TEMP_SERVER_WEBHOOK'
                  onChange={this.handleCreateWebHook}
                  queryParams={{tenantId, tempServerLineId: itemData.tempServerLineId}}
                  type="primary"
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </Lov> */}
                <C7nLov
                  name="code"
                  mode="button"
                  color="primary"
                  placeholder={intl.get('hzero.common.button.create').d('新建')}
                  dataSet={ds}
                  clearButton={false}
                  modalProps={{ zIndex: 1000 }}
                  className={styles['lov-button']}
                />
              </div>
              <Table
                bordered
                rowKey="tempServerWhId"
                loading={fetchLoading}
                columns={columns}
                pagination={pagination}
                dataSource={webHookData}
                style={{ marginTop: 14 }}
                scroll={{ x: tableScrollWidth(columns) }}
                rowSelection={{
                  selectedRowKeys,
                  onChange: this.handleRowSelectionChange,
                }}
                onChange={this.handleTableChange}
              />
            </>
          )}
          {/* <Form.Item>
            {getFieldDecorator('serverCode', {
              initialValue: itemData.serverCode,
            })(<div />)}
          </Form.Item> */}
        </Form>
      </Modal>
    );
  }
}
