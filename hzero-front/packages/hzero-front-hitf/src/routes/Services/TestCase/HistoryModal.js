/**
 * HistoryModal - 测试历史弹窗
 * @date: 2019/6/10
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Button, Modal, Popconfirm, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import jsonFormat from '../../../components/JsonFormat';

/**
 * 测试历史弹窗
 * @extends {Component} - PureComponent
 * @reactProps {!boolean} visible - 是否可见
 * @reactProps {!boolean} loading - 加载标志
 * @reactProps {!Object} testCaseHistory - 测试历史数据源及分页
 * @reactProps {Function} onDelete - 删除历史
 * @reactProps {Function} onChange - 切换列表页码
 * @reactProps {Function} onCancel - 关闭测试历史弹窗
 * @return React.element
 */
export default class HistoryModal extends PureComponent {
  state = {
    isShowMessage: false,
    type: '',
  };

  /**
   * 显示消息内容模态框
   */
  @Bind()
  handleOpenMessageModal(text, type) {
    const obj = this.handleTransObj(text);
    const jsonText = obj === null ? '' : jsonFormat(obj);
    this.setState({
      isShowMessage: true,
      message: jsonText,
      type,
    });
  }

  /**
   * 关闭消息内容模态框
   */
  @Bind()
  handleCloseMessageModal() {
    this.setState({
      isShowMessage: false,
    });
  }

  /**
   * 将JSON字符串转换为格式化JSON
   * @param {string} - str JSON字符串
   */
  @Bind()
  handleTransObj(str) {
    let result = null;
    try {
      result = JSON.parse(str);
    } catch (err) {
      return null;
    }
    return result;
  }

  render() {
    const {
      visible,
      loading,
      testCaseHistory: { list = [], pagination = {} },
      onDelete,
      onChange,
      onCancel,
    } = this.props;
    const { isShowMessage, message, type } = this.state;
    const columns = [
      {
        title: intl.get('hitf.services.model.services.test.time').d('测试时间'),
        dataIndex: 'creationDate',
        width: 160,
      },
      {
        title: intl.get('hitf.services.model.services.requestUrl').d('请求地址'),
        dataIndex: 'interfaceUrl',
        width: 200,
      },
      {
        title: intl.get('hitf.services.model.services.responseStatus').d('返回状态'),
        dataIndex: 'interfaceResponseStatus',
        width: 100,
      },
      {
        title: intl.get('hitf.services.model.services.responseTime').d('耗时'),
        dataIndex: 'interfaceResponseTime',
        render: (text) => {
          if (text) {
            return `${text / 1000}s`;
          }
        },
      },
      {
        title: intl.get('hitf.services.model.services.resDetail').d('请求详情'),
        dataIndex: 'requestDetail',
        width: 150,
        render: (text) => <a onClick={() => this.handleOpenMessageModal(text, 'REQ')}>{text}</a>,
      },
      {
        title: intl.get('hitf.services.model.services.respDetail').d('响应详情'),
        dataIndex: 'responseDetail',
        width: 150,
        render: (text) => <a onClick={() => this.handleOpenMessageModal(text, 'RESP')}>{text}</a>,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'action',
        fixed: 'right',
        width: 90,
        render: (_, record) => {
          const operators = [
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  placement="topRight"
                  onConfirm={() => onDelete(record.interfaceLogId)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <Modal
        width={700}
        visible={visible}
        maskClosable
        destroyOnClose
        title={intl.get('hitf.document.view.title.test.history').d('测试历史')}
        onCancel={() => onCancel()}
        footer={[
          <Button key="cancel" onClick={() => onCancel()}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>,
        ]}
      >
        <Table
          rowKey="interfaceLogId"
          bordered
          scroll={{ x: tableScrollWidth(columns) }}
          loading={loading}
          columns={columns}
          dataSource={list}
          pagination={pagination}
          onChange={(page) => onChange(page)}
        />
        <Modal
          width={700}
          visible={isShowMessage}
          destroyOnClose
          maskClosable
          title={
            type === 'REQ'
              ? intl.get('hitf.services.model.services.resDetail').d('请求详情')
              : intl.get('hitf.services.model.services.respDetail').d('响应详情')
          }
          onCancel={this.handleCloseMessageModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseMessageModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <pre style={{ maxHeight: '380px' }}>
            <code>{message}</code>
          </pre>
        </Modal>
      </Modal>
    );
  }
}
