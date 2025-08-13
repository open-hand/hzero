/**
 * 系统管理--模板维护
 * @date 2019-6-26
 * @author: XL <liang.xiong@hand-china.com>
 */
import React, { Component } from 'react';
import { Table, Tooltip } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { BKT_PLATFORM } from 'utils/config';
import { enableRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth, getAttachmentUrl } from 'utils/utils';

/**
 * 短信配置数据展示列表
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

export default class ListTable extends Component {
  // 编辑
  @Bind()
  editModal(record) {
    const { onGetRecord } = this.props;
    onGetRecord(record);
  }

  render() {
    const {
      isTenantRoleLevel,
      templateData = {},
      loading,
      pagination,
      onChange,
      match,
      organizationId,
    } = this.props;
    const columns = [
      !isTenantRoleLevel && {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hpfm.portalTemplate.model.portalTemplate.templateCode').d('模板代码'),
        dataIndex: 'templateCode',
        width: 150,
      },
      {
        title: intl.get('hpfm.portalTemplate.model.portalTemplate.templateName').d('模板名称'),
        dataIndex: 'templateName',
        width: 150,
      },
      {
        title: intl.get('hpfm.portalTemplate.model.portalTemplate.templateAvatar').d('模板缩略图'),
        dataIndex: 'templateAvatar',
        width: 300,
        render: (templateAvatar, record) => (
          <Tooltip placement="top" title={record.imageName}>
            <a
              href={getAttachmentUrl(templateAvatar, BKT_PLATFORM, organizationId)}
              target="_blank"
              rel="noopener noreferrer"
            >
              {record.imageName}
            </a>
          </Tooltip>
        ),
      },
      {
        title: intl.get('hpfm.portalTemplate.model.portalTemplate.templatePath').d('模板路径'),
        dataIndex: 'templatePath',
      },
      !isTenantRoleLevel && {
        title: intl.get('hpfm.hpfmTemplate.model.portalTemplate.templateLevel').d('层级'),
        dataIndex: 'templateLevelCodeMeaning',
        width: 120,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 85,
        key: 'error',
        fixed: 'right',
        render: (val, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '模板维护-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.editModal(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    return (
      <Table
        bordered
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        rowKey="templateId"
        dataSource={templateData.content || []}
        pagination={pagination}
        loading={loading}
        onChange={page => onChange(page)}
      />
    );
  }
}
