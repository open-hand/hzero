/**
 * ListTable - 数据核对-列表页
 * @date: 2019-7-28
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Table, Modal, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isObject } from 'lodash';

import intl from 'utils/intl';
import { TagRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth, isTenantRoleLevel } from 'utils/utils';

import jsonFormat from '../../../components/JsonFormat';

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
    isShowMessage: false,
    message: '', // 处理信息
  };

  /**
   * 显示处理消息内容模态框
   * @param {string} text - 处理信息
   */
  @Bind()
  handleOpenMsgModal(text) {
    const obj = this.handleTransObj(text);
    const content = isObject(obj) ? jsonFormat(obj) : text;
    this.setState({
      isShowMessage: true,
      message: content,
    });
  }

  /**
   * JSON类型处理
   * @param {string} - JSON字符串
   */
  @Bind()
  handleTransObj(str) {
    let result = str;
    try {
      result = JSON.parse(str);
    } catch (err) {
      return null;
    }
    return result;
  }

  /**
   * 关闭处理消息内容模态框
   */
  @Bind()
  handleCloseMsgModal() {
    this.setState({
      isShowMessage: false,
    });
  }

  render() {
    const { loading, dataSource, pagination, onChange, onView } = this.props;
    const { isShowMessage, message } = this.state;
    const isTenant = isTenantRoleLevel();
    const columns = [
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.batchNum').d('核对批次'),
        width: 150,
        dataIndex: 'batchNum',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.chkLevel').d('核对层级'),
        dataIndex: 'chkLevelMeaning',
        width: 150,
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.sourceService').d('生产服务'),
        dataIndex: 'sourceService',
        width: 200,
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.sourceDs').d('生产数据源'),
        width: 200,
        dataIndex: 'sourceDs',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.sourceDb').d('生产DB'),
        width: 200,
        dataIndex: 'sourceDb',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.sourceTable').d('生产表名'),
        width: 200,
        dataIndex: 'sourceTable',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.targetService').d('消费服务'),
        width: 200,
        dataIndex: 'targetService',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.targetDs').d('消费数据源'),
        width: 200,
        dataIndex: 'targetDs',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.targetDb').d('消费DB'),
        width: 200,
        dataIndex: 'targetDb',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.targetTable').d('消费表名'),
        width: 200,
        dataIndex: 'targetTable',
      },
      !isTenant && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        width: 150,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 130,
        dataIndex: 'processStatus',
        render: (text) => {
          const statusList = [
            {
              status: 'R',
              color: 'blue',
              text: intl.get('hdtt.dataCheck.model.dataCheck.handling').d('处理中'),
            },
            {
              status: 'S',
              color: 'green',
              text: intl.get('hdtt.dataCheck.model.dataCheck.success').d('完成'),
            },
            {
              status: 'E',
              color: 'red',
              text: intl.get('hdtt.dataCheck.model.dataCheck.failed').d('失败'),
            },
            {
              status: 'P',
              color: 'orange',
              text: intl.get('hdtt.dataCheck.model.dataCheck.waiting').d('待处理'),
            },
          ];
          return TagRender(text, statusList);
        },
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.processTime').d('处理时间'),
        width: 200,
        dataIndex: 'processTime',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.processMsg').d('处理消息'),
        width: 150,
        dataIndex: 'processMsg',
        render: (text) => <a onClick={() => this.handleOpenMsgModal(text)}>{text}</a>,
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.remark').d('备注'),
        width: 100,
        dataIndex: 'remark',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        key: 'edit',
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'detail',
              ele: (
                <a onClick={() => onView(record)}>
                  {intl.get('hzero.common.button.detail').d('详情')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.detail').d('详情'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    return (
      <>
        <Table
          bordered
          rowKey="dataChkBatchLineId"
          loading={loading}
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={(page) => onChange(page)}
        />
        <Modal
          visible={isShowMessage}
          destroyOnClose
          maskClosable
          title={intl.get('hdtt.dataCheck.model.dataCheck.processMsg').d('处理消息')}
          onCancel={this.handleCloseMsgModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseMsgModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <pre style={{ maxHeight: '350px' }}>
            <code>{message}</code>
          </pre>
        </Modal>
      </>
    );
  }
}
