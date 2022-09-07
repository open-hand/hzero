/**
 * DetailTable - 数据核对详情列表
 * @date: 2019-7-29
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Table, Modal, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import jsonFormat from '../../../components/JsonFormat';

/**
 * 详情列表
 * @extends {Component} - React.Component
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Function} onChange - 分页查询
 */
export default class DetailTable extends Component {
  state = {
    isShowMessage: false,
    message: '',
    type: '',
  };

  /**
   * 显示模态框
   * @param {string} text - 处理信息
   */
  @Bind()
  handleOpenMsgModal(text, type) {
    const obj = this.handleTransObj(text);
    const content = jsonFormat(obj);
    this.setState({
      isShowMessage: true,
      message: content,
      type,
    });
  }

  /**
   * 将JSON字符串转换为格式化JSON
   * @param {string} - JSON字符串
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

  /**
   * 关闭模态框
   */
  @Bind()
  handleCloseMsgModal() {
    this.setState({
      isShowMessage: false,
    });
  }

  /**
   * 渲染弹窗标题
   */
  @Bind()
  renderModalTitle() {
    const { type } = this.state;
    let title = '';
    switch (type) {
      case 'source':
        title = intl.get('hdtt.dataCheck.model.dataCheck.sourceLine').d('来源行');
        break;
      case 'target':
        title = intl.get('hdtt.dataCheck.model.dataCheck.targetLine').d('消费行');
        break;
      case 'diff':
        title = intl.get('hdtt.dataCheck.model.dataCheck.diffContent').d('差异内容');
        break;
      default:
        break;
    }
    return title;
  }

  render() {
    const { loading, dataSource, pagination, onChange } = this.props;
    const { isShowMessage, message } = this.state;
    const columns = [
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.batchNum').d('核对批次'),
        width: 150,
        dataIndex: 'batchNum',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.sourceLine').d('来源行'),
        width: 150,
        dataIndex: 'sourceLine',
        render: text => <a onClick={() => this.handleOpenMsgModal(text, 'source')}>{text}</a>,
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.targetLine').d('消费行'),
        width: 150,
        dataIndex: 'targetLine',
        render: text => <a onClick={() => this.handleOpenMsgModal(text, 'target')}>{text}</a>,
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.diffType').d('差异类型'),
        width: 150,
        dataIndex: 'diffTypeMeaning',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.diffContent').d('差异内容'),
        width: 150,
        dataIndex: 'diffContent',
        render: text => <a onClick={() => this.handleOpenMsgModal(text, 'diff')}>{text}</a>,
      },
    ];

    return (
      <>
        <Table
          bordered
          rowKey="dataChkDtlId"
          loading={loading}
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          onChange={page => onChange(page)}
        />
        <Modal
          width={550}
          visible={isShowMessage}
          destroyOnClose
          maskClosable
          title={this.renderModalTitle()}
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
