/**
 * List  - 应用管理 - 首页列表
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Icon, Popconfirm, Table } from 'hzero-ui';
// import { Tooltip } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { enableRender, operatorRender, TagRender } from 'utils/renderer';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import {
  SERVICE_CATEGORY_TAGS,
  SERVICE_STATUS_TAGS,
  SERVICE_TYPE_TAGS,
} from '@/constants/constants';

export default class List extends PureComponent {
  defaultTableRowKey = 'interfaceServerId';

  handleDelete(record) {
    const { deleteRow = (e) => e } = this.props;
    deleteRow(record);
  }

  @Bind()
  handleOnChange(page) {
    const {
      onChange = () => {},
      setCacheQueryParams = () => {},
      cacheQueryParams = {},
    } = this.props;
    onChange({ page });
    setCacheQueryParams({ ...cacheQueryParams, page });
  }

  render() {
    const {
      dataSource = [],
      pagination,
      processing = {},
      openDetail = (e) => e,
      handleOpenInvokeAddrModal = (e) => e,
      currentProcessedRow,
      tenantRoleLevel,
    } = this.props;
    const tableColumns = [
      !tenantRoleLevel && {
        title: intl.get('hitf.services.model.services.tenant').d('所属租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hitf.services.model.services.code').d('服务代码'),
        dataIndex: 'serverCode',
        width: 400,
      },
      {
        title: intl.get('hitf.services.model.services.name').d('服务名称'),
        dataIndex: 'serverName',
        width: 200,
      },
      {
        title: intl.get('hitf.services.model.services.type').d('服务类型'),
        width: 140,
        align: 'center',
        render: (text, record) => {
          return TagRender(record.serviceType, SERVICE_TYPE_TAGS, text);
        },
        dataIndex: 'serviceTypeMeaning',
      },
      {
        title: intl.get('hitf.services.model.services.category').d('服务类别'),
        width: 140,
        render: (text, record) => {
          return TagRender(record.serviceCategory, SERVICE_CATEGORY_TAGS, text);
        },
        align: 'center',
        dataIndex: 'serviceCategoryMeaning',
      },
      {
        title: intl.get('hitf.services.model.services.namespace').d('服务命名空间'),
        dataIndex: 'namespace',
      },
      {
        title: intl.get('hitf.services.model.services.address').d('服务地址'),
        dataIndex: 'domainUrl',
        width: 300,
      },
      {
        title: intl.get('hmsg.common.status').d('状态'),
        dataIndex: 'statusMeaning',
        align: 'center',
        render: (text, record) => {
          return TagRender(record.status, SERVICE_STATUS_TAGS, text);
        },
        width: 120,
      },
      {
        title: intl.get('hmsg.common.view.enabledFlag').d('启用标识'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        align: 'center',
        width: 100,
      },
      {
        title: intl.get('hitf.services.view.message.current.version').d('当前版本'),
        dataIndex: 'formatVersion',
        width: 100,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 180,
        fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'addr',
              ele: (
                <a className="addr" onClick={() => handleOpenInvokeAddrModal(record)}>
                  {intl.get('hitf.services.model.services.invokeAddr').d('透传地址')}
                </a>
              ),
              len: 4,
              title: intl.get('hitf.services.model.services.invokeAddr').d('透传地址'),
            },
            {
              key: 'edit',
              ele: (
                <a className="edit" onClick={() => openDetail(record.interfaceServerId)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (currentProcessedRow === record.interfaceServerId && processing.delete) {
            operators.push({
              key: 'loading',
              ele: <Icon type="loading" />,
              len: 2,
            });
          } else {
            operators.push({
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={this.handleDelete.bind(this, record)}
                >
                  <a className="delete">{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ].filter(Boolean);
    const tableProps = {
      dataSource,
      onChange: this.handleOnChange,
      pagination,
      bordered: true,
      loading: processing.query,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      rowKey: this.defaultTableRowKey,
      autoHeight: { type: 'maxHeight' },
    };
    return (
      <>
        <Table {...tableProps} />
      </>
    );
  }
}
