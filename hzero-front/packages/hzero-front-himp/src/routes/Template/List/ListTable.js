/**
 * template - 模板model
 * @since 2019-1-29
 * @author wangjiacheng <jiacheng.wang@hand-china.com>
 * @version 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Table, Popconfirm } from 'hzero-ui';

import { createPagination, isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { HZERO_IMP } from 'utils/config';
import notification from 'utils/notification';
import queryString from 'querystring';

import { downloadFile } from 'hzero-front/lib/services/api';
import { openTab } from 'utils/menuTab';

// 监听导出错误时 postMessage 事件
window.addEventListener('message', (e) => {
  const {
    data: { type, message },
  } = e;
  if (type && type === 'templateExportError') {
    notification.error({
      message: intl.get('hzero.common.notification.export.error').d('导出异常'),
      description: message,
    });
  }
});

/**
 * 消息模板数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */
export default class ListTable extends PureComponent {
  /**
   * 编辑
   * @param {object} record - 消息模板对象
   */
  changeEdit(record) {
    this.props.editContent(record);
  }

  /**
   * 删除
   * @param {object} record - 消息模板对象
   */
  delHeader(record) {
    this.props.delContent(record);
  }

  /**
   * 导出
   * @param {object} record - 消息模板对象
   */
  exportExcel(record) {
    const { organizationId } = this.props;
    const requestUrl = `${HZERO_IMP}/v1/${
      isTenantRoleLevel() ? `${organizationId}/` : ''
    }template/${record.templateCode}/excel`;
    if (record) {
      downloadFile({
        requestUrl,
        queryParams: [{ name: 'tenantId', value: record.tenantId }],
      });
    }
  }

  exportCsv(record) {
    const { organizationId } = this.props;
    const requestUrl = `${HZERO_IMP}/v1/${
      isTenantRoleLevel() ? `${organizationId}/` : ''
    }template/${record.templateCode}/csv`;
    if (record) {
      downloadFile({
        requestUrl,
        queryParams: [{ name: 'tenantId', value: record.tenantId }],
      });
    }
  }

  /**
   * 导入
   * @param {*} record
   */
  changeImport(record) {
    // 跳转到导入界面
    openTab({
      key: `/himp/commentImport/${record.templateCode}`,
      title: 'hzero.common.title.templateImport',
      search: queryString.stringify({
        title: 'hzero.common.title.templateImport',
        action: 'himp.commentImport.view.button.templateImport',
        tenantId: record.tenantId,
        prefixPatch: record.prefixPatch,
        templateType: record.templateType,
      }),
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource, pagination, onChange } = this.props;
    const paginations = {
      ...createPagination(dataSource),
      ...pagination,
    };

    const columns = [
      !isTenantRoleLevel() && {
        title: intl.get(`himp.template.model.template.tenantName`).d('租户'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get(`himp.template.model.template.templateName`).d('模板名称'),
        dataIndex: 'templateName',
      },
      {
        title: intl.get(`himp.template.model.template.templateCode`).d('模板代码'),
        dataIndex: 'templateCode',
        width: 150,
      },
      {
        title: intl.get(`himp.template.model.template.templateType`).d('模板类型'),
        dataIndex: 'templateTypeMeaning',
        width: 100,
      },
      {
        title: intl.get(`himp.template.model.template.templateFileName`).d('自定义模板名称'),
        dataIndex: 'templateFileName',
        width: 150,
      },
      {
        title: intl.get(`himp.template.model.template.description`).d('描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get(`hzero.common.status`).d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'action',
        width: 210,
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a
                  onClick={() => {
                    this.changeEdit(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get(`himp.template.view.message.title.confirmDelete`).d('确定删除？')}
                  onConfirm={() => this.delHeader(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
            {
              key: 'import',
              ele: (
                <a onClick={() => this.changeImport(record)}>
                  {intl.get('hzero.common.button.import').d('导入')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.import').d('导入'),
            },
            {
              key: 'exportExcel',
              ele: (
                <a onClick={() => this.exportExcel(record)}>
                  {intl.get('himp.template.view.message.title.EXCEL').d('EXCEL')}
                </a>
              ),
              len: 3,
              title: intl.get('himp.template.view.message.title.EXCEL').d('EXCEL'),
            },
            {
              key: 'exportCsv',
              ele: (
                <a onClick={() => this.exportCsv(record)}>
                  {intl.get('himp.template.view.message.title.CSV').d('CSV')}
                </a>
              ),
              len: 2,
              title: intl.get('himp.template.view.message.title.CSV').d('CSV'),
            },
          ];
          return operatorRender(operators, record, {
            limit: 4,
            label: intl.get('hzero.common.button.export').d('导出'),
          });
        },
      },
    ].filter(Boolean);

    return (
      <Table
        bordered
        loading={loading}
        rowKey="id"
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={paginations}
        onChange={onChange}
      />
    );
  }
}
