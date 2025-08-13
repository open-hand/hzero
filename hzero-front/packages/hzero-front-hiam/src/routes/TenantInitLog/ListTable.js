/**
 * ListTable - 租户初始化处理日志-列表页
 * @date: 2019-6-18
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Table, Modal, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import ProcessorsImg from '../TenantInitConfig/ProcessorsImg';

const modelPrompt = 'hiam.tenantConfig.model.tenantConfig';
const viewTitle = 'hiam.tenantLog.view.title';

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Function} onEdit - 跳转详情页
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @return React.element
 */

export default class ListTable extends Component {
  state = {
    isShowError: false, // 是否显示错误消息
    errorText: '',
  };

  /**
   * 显示错误信息弹窗
   * @param {string} record - 表格行数据
   */
  @Bind()
  handleOpenErrorModal(record) {
    this.setState({
      errorText: record.processMessage || '',
      isShowError: true,
    });
  }

  /**
   * 关闭错误消息模态框
   */
  @Bind()
  handleCloseErrorModal() {
    this.setState({
      isShowError: false,
      errorText: '',
    });
  }

  render() {
    const {
      loading,
      dataSource,
      pagination,
      onChange,
      picProps,
      onOpenPic,
      onClosePic,
      isShowImg,
    } = this.props;
    const { errorText, isShowError } = this.state;
    const columns = [
      {
        title: intl.get(`${modelPrompt}.instanceKeyNum`).d('实例编号'),
        dataIndex: 'instanceKey',
        width: 300,
        render: (text, record) => <a onClick={() => onOpenPic(record)}>{text}</a>,
      },
      {
        title: intl.get(`${modelPrompt}.tenantNum`).d('租户编码'),
        dataIndex: 'tenantNum',
        width: 120,
      },
      {
        title: intl.get(`${modelPrompt}.serviceName`).d('服务名称'),
        dataIndex: 'serviceName',
        width: 200,
      },
      {
        title: intl.get(`${modelPrompt}.processorCode`).d('处理器代码'),
        dataIndex: 'processorCode',
        width: 150,
      },
      {
        title: intl.get(`${modelPrompt}.processorName`).d('处理器名称'),
        dataIndex: 'processorName',
        width: 150,
      },
      {
        title: intl.get(`${modelPrompt}.processorType`).d('处理器类型'),
        dataIndex: 'processorTypeMeaning',
        width: 150,
      },
      {
        title: intl.get(`${modelPrompt}.initType`).d('初始化类型'),
        dataIndex: 'initTypeMeaning',
        width: 120,
      },
      {
        title: intl.get(`${modelPrompt}.order`).d('排序'),
        dataIndex: 'orderSeq',
        width: 90,
      },
      {
        title: intl.get(`${modelPrompt}.processStatus`).d('处理状态'),
        dataIndex: 'processStatusMeaning',
        width: 90,
        render: (text, record) => {
          if (record.processStatus === 'E') {
            return <a onClick={() => this.handleOpenErrorModal(record)}>{text}</a>;
          } else {
            return text;
          }
        },
      },
      {
        title: intl.get(`${modelPrompt}.processTime`).d('处理时间'),
        dataIndex: 'processTime',
        width: 180,
      },
      {
        title: intl.get(`${modelPrompt}.remark`).d('说明'),
        dataIndex: 'remark',
        width: 150,
      },
    ];

    return (
      <>
        <Table
          bordered
          rowKey="tenantInitLogId"
          loading={loading}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          pagination={pagination}
          onChange={page => onChange(page)}
        />
        <Modal
          visible={isShowError}
          destroyOnClose
          maskClosable
          title={intl.get(`${viewTitle}.error.msg`).d('错误消息')}
          onCancel={this.handleCloseErrorModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseErrorModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <div style={{ maxWidth: 472, maxHeight: 400, overflowY: 'scroll' }}>{errorText}</div>
        </Modal>
        <Modal
          visible={isShowImg}
          width={560}
          destroyOnClose
          maskClosable
          title={
            picProps.type === 'create'
              ? intl.get('hiam.tenantConfig.view.title.tenant.create').d('租户创建')
              : intl.get('hiam.tenantConfig.view.title.tenant.update').d('租户更新')
          }
          onCancel={onClosePic}
          footer={[
            <Button key="cancel" onClick={onClosePic}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <ProcessorsImg {...picProps} />
        </Modal>
      </>
    );
  }
}
