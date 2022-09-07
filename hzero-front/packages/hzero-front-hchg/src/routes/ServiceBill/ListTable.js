/**
 * ListTable - 服务账单-列表页
 * @date: 2019-8-22
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Table, Modal, Button, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { dateRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

const { TextArea } = Input;

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Function} onChange - 分页查询
 * @return React.element
 */
export default class ListTable extends Component {
  state = {
    remark: '', // 扣款失败的备注
    isShowRemark: false, // 弹窗是否显示
  };

  /**
   * 显示备注模态框
   */
  @Bind()
  handleOpenModal(text) {
    this.setState({
      isShowRemark: true,
      remark: text,
    });
  }

  /**
   * 关闭备注模态框
   */
  @Bind()
  handleCloseModal() {
    this.setState({
      isShowRemark: false,
    });
  }

  render() {
    const {
      loading,
      dataSource,
      pagination,
      onChange,
      isTenant,
      onRedirect = () => {},
    } = this.props;
    const { isShowRemark, remark } = this.state;
    const columns = [
      {
        title: intl.get('hchg.serviceBill.model.serviceBill.client').d('客户端'),
        width: 150,
        dataIndex: 'clientId',
      },
      !isTenant && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hchg.serviceBill.model.serviceBill.service').d('服务'),
        width: 200,
        dataIndex: 'serviceName',
      },
      {
        title: intl.get('hchg.serviceBill.model.serviceBill.freeTimes').d('不收费服务次数'),
        width: 200,
        dataIndex: 'freeTimes',
      },
      {
        title: intl.get('hchg.serviceBill.model.serviceBill.chargeTimes').d('收费服务次数'),
        width: 200,
        dataIndex: 'chargeTimes',
      },
      {
        title: intl.get('hchg.serviceBill.model.serviceBill.chargeAmount').d('原价（元）'),
        width: 200,
        dataIndex: 'chargeAmount',
      },
      {
        title: intl.get('hchg.serviceBill.model.serviceBill.discountAmount').d('优惠（元）'),
        width: 200,
        dataIndex: 'discountAmount',
      },
      {
        title: intl.get('hchg.serviceBill.model.serviceBill.from').d('发生日期从'),
        width: 200,
        dataIndex: 'chargeDateFrom',
        render: dateRender,
      },
      {
        title: intl.get('hchg.serviceBill.model.serviceBill.to').d('发生日期至'),
        width: 200,
        dataIndex: 'chargeDateTo',
        render: dateRender,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'statusMeaning',
        width: 120,
      },
      {
        title: intl.get('hchg.serviceBill.model.serviceBill.paidTime').d('实际扣款时间'),
        width: 200,
        dataIndex: 'paidTime',
      },
      {
        title: intl.get('hchg.serviceBill.model.serviceBill.remark').d('备注'),
        width: 200,
        dataIndex: 'remark',
        render: (text, record) =>
          record.status === 'CLEAR_FAILED' ? (
            <a onClick={() => this.handleOpenModal(text)}>{text}</a>
          ) : (
            text
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 130,
        fixed: 'right',
        render: (_, record) => (
          <span className="action-link">
            <a onClick={() => onRedirect(record.chargeOrderId)}>
              {intl.get('hchg.serviceBill.view.button.orderDetail').d('订购详单')}
            </a>
          </span>
        ),
      },
    ].filter(Boolean);
    return (
      <>
        <Table
          bordered
          rowKey="chargeBillId"
          loading={loading}
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={page => onChange(page)}
        />
        <Modal
          visible={isShowRemark}
          destroyOnClose
          maskClosable
          title={intl.get('hchg.serviceBill.model.serviceBill.remark').d('备注')}
          onCancel={this.handleCloseModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <TextArea row={4} style={{ height: '260px' }} readOnly>
            {remark}
          </TextArea>
        </Modal>
      </>
    );
  }
}
