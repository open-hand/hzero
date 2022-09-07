/**
 * ConsumerDrawer - 数据消费生产消费配置详情页-消费配置-租户配置表格
 * @date: 2019/4/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Icon, Modal, Button, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import EditTable from 'components/EditTable';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { operatorRender } from 'utils/renderer';

const promptCode = 'hdtt.producerConfig.model.producerConfig';
const FormItem = Form.Item;
const { TextArea } = Input;
/**
 * 数据生产配置详情
 * @extends {Component} - React.Component
 * @reactProps {boolean} loading - 表格是否在加载中
 * @reactProps {array[Object]} dataSource - 表格数据源
 * @reactProps {Object} rowSelection - 表格行选择
 * @reactProps {Object} consumerDbConfig - DB维度表格当前行数据
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Function} onChange - 表格变化时的回调
 * @reactProps {Function} onTenantInit - 初始化租户消费配置
 * @return React.element
 */

export default class ConsumerDrawerTable extends Component {
  state = {
    currentConsTenantConfigId: '',
    isShowTenantMsg: false,
    tenantInitMsg: '',
  };

  /**
   * 消费者初始化
   * @param {Object} record - 消费租户表格行数据
   */
  @Bind()
  handleInit(record) {
    this.setState(
      {
        currentConsTenantConfigId: record.consTenantConfigId,
      },
      () => {
        this.props.onTenantInit(record);
      }
    );
  }

  /**
   * 渲染初始化按钮
   *  @param {Object} record - 租户消费配置行数据
   */
  @Bind()
  renderInitBtn(record) {
    const { dataSource, isTenantIniting } = this.props;
    const isEditing = dataSource.find(item => '_status' in item);
    const { currentConsTenantConfigId } = this.state;
    let btn = null;
    if (['create'].includes(record._status)) {
      return null;
    } else if (isTenantIniting && currentConsTenantConfigId === record.consTenantConfigId) {
      btn = (
        <a>
          <Icon type="loading" />
        </a>
      );
    } else if (isEditing || record.processStatus === 'R') {
      btn = (
        <a style={{ pointerEvents: 'none', color: 'rgba(0, 0, 0, 0.25)' }}>
          {intl.get(`${promptCode}.init`).d('初始化')}
        </a>
      );
    } else {
      btn = (
        <a onClick={() => this.handleInit(record)}>{intl.get(`${promptCode}.init`).d('初始化')}</a>
      );
    }
    return btn;
  }

  /**
   * 显示初始化消息弹窗
   * @param {string} text - 初始化消息
   */
  @Bind()
  handleOpenMsgModal(text) {
    this.setState({
      isShowTenantMsg: true,
      tenantInitMsg: text,
    });
  }

  /**
   * 关闭初始化消息弹窗
   */
  @Bind()
  handleCloseMsgModal() {
    this.setState({
      isShowTenantMsg: false,
      tenantInitMsg: '',
    });
  }

  render() {
    const {
      loading,
      dataSource,
      pagination,
      rowSelection,
      onChange,
      consumerDbConfig,
      initDbCode,
      initDsId,
      isEdit,
    } = this.props;
    const { isShowTenantMsg, tenantInitMsg } = this.state;
    const columns = [
      {
        title: intl.get(`${promptCode}.tenant`).d('租户'),
        dataIndex: 'tenantName',
        width: 150,
        render: (val, record) =>
          ['create'].includes(record._status) ? (
            <FormItem>
              {record.$form.getFieldDecorator('tenantId', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.tenant`).d('租户'),
                    }),
                  },
                ],
                initialValue: record.tenantId,
              })(
                <Lov
                  disabled={isEdit ? false : !(initDsId && initDbCode)}
                  code="HDTT.DATABASE_TENANT"
                  textField="tenantName"
                  queryParams={{
                    datasourceId: isEdit ? consumerDbConfig.consumerDsId : initDsId,
                    databaseCode: isEdit ? consumerDbConfig.consumerDb : initDbCode,
                  }}
                />
              )}
            </FormItem>
          ) : (
            val
          ),
      },
      {
        title: intl.get(`${promptCode}.consumerOffset`).d('初始偏移'),
        dataIndex: 'consumerOffset',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.initStatus`).d('初始化状态'),
        dataIndex: 'processStatusMeaning',
        width: 120,
        align: 'center',
      },
      {
        title: intl.get(`${promptCode}.initTime`).d('初始化时间'),
        dataIndex: 'processTime',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.initMsg`).d('初始化消息'),
        dataIndex: 'processMsg',
        width: 150,
        render: text => <a onClick={() => this.handleOpenMsgModal(text)}>{text}</a>,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'init',
              ele: this.renderInitBtn(record),
              len: 3,
              title: intl.get(`${promptCode}.init`).d('初始化'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    const editTableProps = {
      loading,
      columns,
      dataSource,
      pagination,
      bordered: true,
      rowSelection,
      rowKey: 'consTenantConfigId',
      scroll: { x: tableScrollWidth(columns) },
      onChange: page => onChange(page),
    };
    return (
      <>
        <EditTable {...editTableProps} />
        <Modal
          visible={isShowTenantMsg}
          destroyOnClose
          maskClosable
          title={intl.get(`${promptCode}.initMsg`).d('初始化消息')}
          onCancel={this.handleCloseMsgModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseMsgModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <TextArea row={4} style={{ height: '260px' }} readOnly>
            {tenantInitMsg}
          </TextArea>
        </Modal>
      </>
    );
  }
}
