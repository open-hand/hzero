/**
 * ServiceListModal - 服务列表弹窗
 * // TODO: 删除功能可能会改成批量的 所以注释了一部分代码
 * @date: 2019/8/30
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Button, Modal, Spin, Form, Row, Col, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isNumber } from 'lodash';

import Lov from 'components/Lov';
import notification from 'utils/notification';
import intl from 'utils/intl';

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};

/**
 * 服务列表弹窗
 * @extends {Component} - Component
 * @reactProps {boolean} visible - 是否显示服务列表弹窗
 * @reactProps {string} lovCode - 创建服务范围查询code
 * @reactProps {boolean} isPublished - 计费组状态是否为已发布
 * @reactProps {number} chargeGroupId - 计费组ID
 * @reactProps {array} chargeScopeList - 服务范围列表
 * @reactProps {boolean} visible - 是否可见
 * @reactProps {object} currentServiceRule - 当前规则行
 * @reactProps {Function} onCreate - 创建服务范围
 * @reactProps {Function} onDelete - 删除服务范围
 * @reactProps {Function} onFetch - 查询服务范围
 * @reactProps {Function} onCancel - 关闭服务列表弹窗
 * @reactProps {Boolean} loading - 服务列表加载标志
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class ServiceListModal extends Component {
  // state = {
  //   selectedRowKeys: [],
  //   selectedRows: [],
  // }

  getSnapshotBeforeUpdate(prevProps) {
    const { visible, currentServiceRule } = this.props;
    const { chargeRuleId } = currentServiceRule;
    return (
      visible &&
      isNumber(chargeRuleId) &&
      chargeRuleId !== (prevProps.currentServiceRule || {}).chargeRuleId
    );
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.handleFetch({});
    }
  }

  /**
   * 查询服务列表
   * @param {object} page - 分页
   */
  @Bind()
  handleFetch(page = {}) {
    const { onFetch = () => {}, currentServiceRule = {} } = this.props;
    const { chargeRuleId } = currentServiceRule;
    onFetch(page, chargeRuleId);
  }

  /**
   * 创建服务范围
   * @param {number} serviceId - lov选中行的服务id
   * @param {string} serviceName - lov选中行的服务名称
   */
  @Bind()
  handleCreateScope(_, { serviceId, serviceName }) {
    const { onCreate = () => {}, currentServiceRule = {}, chargeGroupId } = this.props;
    const payload = [
      {
        chargeRuleId: currentServiceRule.chargeRuleId,
        chargeGroupId,
        serviceId,
        serviceName,
      },
    ];
    onCreate(payload).then(res => {
      if (res) {
        notification.success();
        this.handleFetch({});
      }
    });
  }

  /**
   * 删除服务范围
   * @param {object} record - 选中行
   */
  @Bind()
  handleDeleteScope(record) {
    const { onDelete = () => {} } = this.props;
    // const { selectedRows } = this.state;
    const that = this;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk() {
        onDelete(record).then(res => {
          if (res) {
            notification.success();
            that.handleFetch({});
            // that.setState({
            //   selectedRowKeys: [],
            //   selectedRows: [],
            // });
          }
        });
      },
    });
  }

  /**
   * 获取选中行
   * @param {array} selectedRowKeys 选中行的key值集合
   * @param {object} selectedRows 选中行集合
   */
  // @Bind()
  // handleRowSelectChange(selectedRowKeys, selectedRows) {
  //   this.setState({
  //     selectedRowKeys,
  //     selectedRows,
  //   });
  // }

  /**
   * 关闭弹窗
   */
  @Bind()
  handleCancel() {
    const { onCancel = () => {} } = this.props;
    // this.setState({
    //   selectedRowKeys: [],
    //   selectedRows: {},
    // });
    onCancel();
  }

  render() {
    const {
      isPublished,
      visible,
      lovCode = '',
      chargeScopeList,
      currentServiceRule = {},
      loading,
    } = this.props;
    const { dataSource, pagination } = chargeScopeList;
    const { priority, ruleName } = currentServiceRule;
    const columns = [
      {
        title: intl.get('hchg.serviceCharge.model.serviceCharge.serviceName').d('服务名称'),
        dataIndex: 'serviceName',
      },
      !isPublished && {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 160,
        key: 'edit',
        fixed: 'right',
        render: (_, record) => (
          <span className="action-link">
            <a onClick={() => this.handleDeleteScope(record)}>
              {intl.get('hzero.common.button.delete').d('删除')}
            </a>
          </span>
        ),
      },
    ].filter(Boolean);
    // const rowSelection = {
    //   selectedRowKeys,
    //   onChange: this.handleRowSelectChange,
    // };
    return (
      <Modal
        visible={visible}
        destroyOnClose
        maskClosable
        title={intl.get('hchg.serviceCharge.view.title.modal.serviceList').d('服务列表')}
        onCancel={this.handleCancel}
        footer={[
          <Button key="cancel" onClick={this.handleCancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>,
        ]}
      >
        <Spin spinning={false}>
          <Form>
            <Row type="flex">
              <Col span={12}>
                <Form.Item
                  label={intl.get('hchg.serviceCharge.model.serviceCharge.priority').d('优先级')}
                  {...formLayout}
                >
                  {/* {getFieldDecorator('priority', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hchg.serviceCharge.model.serviceCharge.priority').d('优先级'),
                        }),
                      },
                    ],
                  })(<InputNumber style={{ width: '100%' }} />)} */}
                  {priority}
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  label={intl.get('hchg.serviceCharge.model.serviceCharge.ruleName').d('说明')}
                  {...formLayout}
                >
                  {/* {getFieldDecorator('ruleName', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hchg.serviceCharge.model.serviceCharge.ruleName').d('说明'),
                        }),
                      },
                    ],
                  })(<Input />)} */}
                  {ruleName}
                </Form.Item>
              </Col>
            </Row>
          </Form>
          <div
            className="table-list-operator"
            style={{
              textAlign: 'right',
              marginTop: '15px',
              display: isPublished ? 'none' : 'block',
            }}
          >
            <Lov isButton code={lovCode} onChange={this.handleCreateScope} type="primary">
              {intl.get('hzero.common.button.create').d('新建')}
            </Lov>
          </div>
          <Table
            loading={loading}
            // rowSelection={rowSelection}
            dataSource={dataSource}
            columns={columns}
            bordered
            pagination={pagination}
            rowKey="interfaceUsecaseParamId"
            onChange={page => this.handleFetch(page)}
          />
        </Spin>
      </Modal>
    );
  }
}
