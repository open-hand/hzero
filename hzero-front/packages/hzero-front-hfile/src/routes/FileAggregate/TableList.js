import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isNil } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import {
  createPagination,
  isTenantRoleLevel,
  tableScrollWidth,
  getCurrentOrganizationId,
  listenDownloadError,
  getAccessToken,
} from 'utils/utils';
import { dateTimeRender, operatorRender } from 'utils/renderer';
import { HZERO_FILE, VERSION_IS_OP } from 'utils/config';

import { downloadFile } from 'services/api';

listenDownloadError(
  'templateExportError',
  intl.get('hzero.common.notification.download.error').d('下载异常')
);

export default class TableList extends PureComponent {
  @Bind()
  handleStandardTableChange(pagination) {
    const { formValues, onSearch } = this.props;
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      ...formValues,
    };
    onSearch(params);
  }

  @Bind()
  handleWordEditClick(record) {
    const { onRowWordEdit } = this.props;
    onRowWordEdit(record);
  }

  /**
   * 下载
   * @param {object} record - 流程对象
   */
  @Bind()
  downloadLogFile(record) {
    const organizationId = getCurrentOrganizationId();
    const api =
      record.sourceType === '10'
        ? `${HZERO_FILE}/v1/${organizationId}/server-file/download`
        : `${HZERO_FILE}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}files/download`;
    const queryParams = [{ name: 'url', value: encodeURIComponent(record.fileUrl) }];
    if (record.sourceType === '10') {
      queryParams.push({ name: 'serverCode', value: record.serverCode });
    } else {
      queryParams.push({ name: 'bucketName', value: record.bucketName });
      if (!isNil(record.directory)) {
        queryParams.push({ name: 'directory', value: record.directory });
      }
      if (!isNil(record.storageCode)) {
        queryParams.push({ name: 'storageCode', value: record.storageCode });
      }
    }
    downloadFile({
      requestUrl: api,
      queryParams,
    });
  }

  @Bind()
  reviewFile(record) {
    window.open(
      `${HZERO_FILE}/v1/${record.tenantId}/file-preview/by-url?url=${encodeURIComponent(
        record.fileUrl
      )}&bucketName=${record.bucketName}${
        record.storageCode ? `&storageCode=${record.storageCode}` : ''
      }&access_token=${getAccessToken()}`
    );
  }

  render() {
    const {
      fileData,
      loading,
      match: { path },
    } = this.props;
    const columns = [
      !isTenantRoleLevel() && {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.tenantId').d('租户'),
        dataIndex: 'tenantId',
        width: 150,
        render: (val, record) => <span>{record.tenantName}</span>,
      },
      {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.bucketName').d('分组'),
        dataIndex: 'bucketName',
        width: 120,
      },
      {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.directory').d('上传目录'),
        dataIndex: 'directory',
        width: 120,
      },
      (!isTenantRoleLevel() || VERSION_IS_OP) && {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.tableName').d('关联表'),
        dataIndex: 'tableName',
        width: 120,
      },
      {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.fileType').d('文件类型'),
        dataIndex: 'fileType',
        width: 200,
      },
      {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.fileName').d('文件名'),
        dataIndex: 'fileName',
      },
      {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.hrealName').d('上传人'),
        dataIndex: 'realName',
        width: 150,
      },
      {
        title: intl.get('hzero.common.date.creation').d('创建时间'),
        dataIndex: 'creationDate',
        width: 150,
        render: dateTimeRender,
      },
      {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.lastUpdateDate').d('更新时间'),
        dataIndex: 'lastUpdateDate',
        width: 150,
        render: dateTimeRender,
      },
      {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.fileSize').d('文件大小'),
        dataIndex: 'fileSize',
        width: 150,
        render: (val) => <span>{`${val}B`}</span>,
      },
      {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.attachmentUuid').d('批号'),
        dataIndex: 'attachmentUuid',
        width: 250,
      },
      {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.sourceTypeMeaning').d('来源类型'),
        dataIndex: 'sourceTypeMeaning',
        width: 120,
      },
      {
        title: intl.get('hfile.fileAggregate.model.fileAggregate.serverCode').d('服务器编码'),
        dataIndex: 'serverCode',
        width: 120,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'option',
        width: 150,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          if (record.fileUrl) {
            operators.push({
              key: 'download',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.download`,
                      type: 'button',
                      meaning: '文件汇总查询-下载',
                    },
                  ]}
                  onClick={() => this.downloadLogFile(record)}
                >
                  {intl.get('hzero.common.button.download').d('下载')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.download').d('下载'),
            });
            if (record.sourceType !== '10') {
              operators.push({
                key: 'review',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.review`,
                        type: 'button',
                        meaning: '文件汇总查询-预览',
                      },
                    ]}
                    onClick={() => this.reviewFile(record)}
                  >
                    {intl.get('hzero.common.button.review').d('预览')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.review').d('预览'),
              });
            }
            if (
              record.fileUrl.endsWith('.doc') ||
              record.fileUrl.endsWith('docx') ||
              record.fileUrl.endsWith('xls') ||
              record.fileUrl.endsWith('xlsx') ||
              record.fileUrl.endsWith('csv') ||
              record.fileUrl.endsWith('ppt') ||
              record.fileUrl.endsWith('pptx')
            ) {
              operators.push({
                key: 'word-edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.edit`,
                        type: 'button',
                        meaning: '文件汇总查询-编辑',
                      },
                    ]}
                    onClick={() => {
                      this.handleWordEditClick(record);
                    }}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              });
            }
          }
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter(Boolean);
    return (
      <Table
        bordered
        scroll={{ x: tableScrollWidth(columns) }}
        columns={columns}
        rowKey="fileId"
        dataSource={fileData.content || []}
        pagination={createPagination(fileData)}
        loading={loading}
        onChange={this.handleStandardTableChange}
      />
    );
  }
}
