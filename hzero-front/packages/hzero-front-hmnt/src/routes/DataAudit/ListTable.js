/**
 * ListTable - 数据变更审计-列表页
 * @date: 2019-7-9
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table, Modal, Button } from 'hzero-ui';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import { operatorRender } from 'utils/renderer';
import { tableScrollWidth, isTenantRoleLevel } from 'utils/utils';
import DetailTable from './DetailTable';

const isTenant = isTenantRoleLevel();

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} auditList - 数据源
 * @reactProps {Function} onChange - 分页查询
 * @return React.element
 */

export default class ListTable extends PureComponent {
  state = {
    isShowModal: false, // 是否显示
    currentRecord: {},
  };

  /**
   * 查看详情
   * @param {object} record - 表格行数据
   */
  @Bind()
  handleOpenModal(record) {
    this.setState({
      currentRecord: record,
      isShowModal: true,
    });
  }

  // 关闭弹窗
  @Bind()
  handleCloseModal() {
    this.setState({
      isShowModal: false,
    });
  }

  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 180,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: (e) => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
        setTimeout(() => {
          this.forceUpdate();
        }, 23);
      },
    };
  }

  render() {
    const {
      onChange,
      auditList: { dataSource, pagination },
      loading,
    } = this.props;
    const { isShowModal, currentRecord = {} } = this.state;
    const { auditDataLineList = [] } = currentRecord;
    const columns = [
      !isTenant && {
        title: intl.get('hzero.common.model.tenantName').d('租户'),
        width: 200,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.serviceName').d('服务名'),
        dataIndex: 'serviceName',
        width: 200,
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.entityCode').d('审计实体'),
        dataIndex: 'entityCode',
        width: 400,
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.tableName').d('审计表'),
        dataIndex: 'tableName',
        width: 200,
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.entityId').d('主键ID'),
        dataIndex: 'entityId',
        width: 150,
        onCell: this.onCell.bind(this),
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.auditType').d('操作类型'),
        dataIndex: 'auditTypeMeaning',
        width: 150,
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.menuName').d('菜单名称'),
        dataIndex: 'menuName',
        width: 200,
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.entityVersion').d('版本'),
        dataIndex: 'entityVersion',
        width: 150,
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.processUserName').d('操作用户'),
        dataIndex: 'processUserName',
        width: 150,
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.processTime').d('操作时间'),
        dataIndex: 'processTime',
      },
      {
        title: intl.get('hzero.common.remark').d('备注'),
        dataIndex: 'remark',
        width: 150,
        onCell: this.onCell.bind(this),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 108,
        fixed: 'right',
        key: 'edit',
        render: (_, record) => {
          const operators = [];
          if (record.auditType !== 'DELETE') {
            operators.push({
              key: 'detail',
              ele: (
                <a onClick={() => this.handleOpenModal(record)}>
                  {intl.get('hmnt.dataAudit.view.message.detail').d('详情')}
                </a>
              ),
              len: 2,
              title: intl.get('hmnt.dataAudit.view.message.detail').d('详情'),
            });
          }
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter(Boolean);
    const detailTableProps = {
      dataSource: auditDataLineList,
    };
    return (
      <>
        <Table
          bordered
          loading={loading}
          rowKey="auditDataId"
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          pagination={pagination}
          onChange={(page) => onChange(page)}
        />
        <Modal
          width={800}
          visible={isShowModal}
          destroyOnClose
          maskClosable
          title={intl.get('hmnt.dataAudit.view.message.audit.detail').d('审计详情')}
          onCancel={this.handleCloseModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <DetailTable {...detailTableProps} />
        </Modal>
      </>
    );
  }
}
