import React, { PureComponent } from 'react';
import { Table, Popconfirm, Tag } from 'hzero-ui';
import { isEmpty, uniqBy } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth, getCurrentOrganizationId } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';
import { Bind } from 'lodash-decorators';

/**
 * 发送配置数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 分页查询
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
   * @param {object} record - 数据对象
   */
  editOption(record) {
    this.props.onEdit(record);
  }

  @Bind()
  handleCopy(record) {
    const { onCopy } = this.props;
    onCopy(record);
  }

  /**
   * 删除
   * @param {object} record - 数据对象
   */
  deleteOption(record) {
    this.props.onDelete(record);
  }

  /**
   * 发送
   *
   * @memberof ListTable
   */
  sendOption(record) {
    this.props.onOpenSendModal(record);
  }

  /**
   * 渲染启用服务
   * @param {*} item
   */
  typeMeaningRender(item) {
    let mean = '';
    switch (item.typeCode) {
      case 'WEB':
        mean = (
          <Tag color="green" key="1">
            {item.typeMeaning}
          </Tag>
        );
        break;
      case 'EMAIL':
        mean = (
          <Tag color="orange" key="2">
            {item.typeMeaning}
          </Tag>
        );
        break;
      case 'SMS':
        mean = (
          <Tag color="blue" key="3">
            {item.typeMeaning}
          </Tag>
        );
        break;
      case 'WC_E':
        mean = (
          <Tag color="red" key="4">
            {item.typeMeaning}
          </Tag>
        );
        break;
      case 'WC_O':
        mean = (
          <Tag color="#9866ff" key="5">
            {item.typeMeaning}
          </Tag>
        );
        break;
      case 'DT':
        mean = (
          <Tag color="pink" key="6">
            {item.typeMeaning}
          </Tag>
        );
        break;
      case 'CALL':
        mean = (
          <Tag color="#00ccff" key="7">
            {item.typeMeaning}
          </Tag>
        );
        break;
      case 'WEB_HOOK':
        mean = (
          <Tag color="rgb(187, 103, 222)" key="7">
            {item.typeMeaning}
          </Tag>
        );
        break;
      default:
        mean = (
          <Tag color="#dcdcdc" key="6">
            {item}
          </Tag>
        );
        break;
    }
    return mean;
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource, pagination, onChange, tenantRoleLevel, path } = this.props;
    const columns = [
      !tenantRoleLevel && {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hmsg.sendConfig.model.sendConfig.messageCode').d('消息代码'),
        dataIndex: 'messageCode',
        width: 200,
      },
      {
        title: intl.get('hmsg.sendConfig.model.sendConfig.messageName').d('消息名称'),
        dataIndex: 'messageName',
      },
      {
        title: intl.get('hmsg.sendConfig.model.sendConfig.typeMeaning').d('启用服务'),
        dataIndex: 'typeMeaning',
        width: 420,
        render: (val, record) => {
          let types = [];
          if (!isEmpty(record.serverList)) {
            const list = record.serverList.map((item) => ({
              typeCode: item.typeCode,
              typeMeaning: item.typeMeaning,
            }));
            types = uniqBy(list, 'typeCode');
          }
          return <span>{types && types.map((item) => this.typeMeaningRender(item))}</span>;
        },
      },
      // {
      //   title: intl.get('hmsg.sendConfig.model.sendConfig.receiveConfigCode').d('接收配置编码'),
      //   dataIndex: 'receiveCode',
      //   width: 120,
      // },
      tenantRoleLevel &&
        !VERSION_IS_OP && {
          title: intl.get('hmsg.common.view.source').d('来源'),
          width: 120,
          dataIndex: 'tenantId',
          render: (_, record) => {
            const tenantId = getCurrentOrganizationId();
            return tenantId.toString() === record.tenantId.toString() ? (
              <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
            ) : (
              <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
            );
          },
        },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 200,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          const tenantId = getCurrentOrganizationId();
          if (!tenantRoleLevel || tenantId.toString() === record.tenantId.toString()) {
            operators.push(
              {
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.edit`,
                        type: 'button',
                        meaning: '消息发送配置-编辑',
                      },
                    ]}
                    onClick={() => this.editOption(record)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'delete',
                ele: (
                  <Popconfirm
                    placement="topRight"
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    onConfirm={() => this.deleteOption(record)}
                  >
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.delete`,
                          type: 'button',
                          meaning: '消息发送配置-删除',
                        },
                      ]}
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              }
            );
            if (record.enabledFlag && record.serverList && record.serverList.length !== 0) {
              operators.push({
                key: 'testSend',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.testSend`,
                        type: 'button',
                        meaning: '消息发送配置-测试发送',
                      },
                    ]}
                    onClick={() => this.sendOption(record)}
                  >
                    {intl.get('hmsg.sendConfig.view.title.testSend').d('测试发送')}
                  </ButtonPermission>
                ),
                len: 4,
                title: intl.get('hmsg.sendConfig.view.title.testSend').d('测试发送'),
              });
            }
          }
          if (
            tenantId.toString() !== record.tenantId.toString() &&
            tenantRoleLevel &&
            !VERSION_IS_OP
          ) {
            operators.push({
              key: 'copy',
              ele: (
                <a onClick={() => this.handleCopy(record)}>
                  {intl.get('hzero.common.button.copy').d('复制')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.copy').d('复制'),
            });
          }
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    return (
      <Table
        bordered
        rowKey="tempServerId"
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={pagination}
        onChange={(page) => onChange(page)}
      />
    );
  }
}
