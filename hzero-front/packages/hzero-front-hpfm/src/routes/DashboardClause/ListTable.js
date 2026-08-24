/*
 * ListTable - 条目配置列表
 * @date: 2019/01/28 14:49:19
 * @author: YKK <kaikai.yang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */

import React, { Component, Fragment } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';
import uuid from 'uuid/v4'; // 用于生成每次打开分配模态框的key

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth, isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';

import TenantEditModal from './TenantEditModal';
/**
 * 条目配置列表
 * @extends {Component} - React.Component
 * @reactProps {Function} editRow 修改行
 * @reactProps {Function} handleSave 保存行
 * @reactProps {Function} deleteRow 删除行
 * @reactProps {Function} cancelRow 取消行
 * @reactProps {Function} 取消行handleLovOnChange Lov发生改变的回调
 * @reactProps {Object} form 表单
 * @return React.element
 */
const promptCode = 'hpfm.dashboardClause';

@connect(({ loading, dashboardClause }) => {
  return {
    dashboardClause,
    cardTenantFetchLoading: loading.effects['dashboardClause/cardTenantFetch'],
    cardTenantAddLoading: loading.effects['dashboardClause/cardTenantAdd'],
    cardTenantRemoveLoading: loading.effects['dashboardClause/cardTenantRemove'],
  };
})
export default class ListTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      currentRecord: {},
      // 卡片编辑模态框的属性
      cardTenantEditModalProps: {
        modalProps: { visible: false },
        // cardId: undefined, // 卡片的id
      },
    };
  }

  /**
   * 打开编辑弹窗
   * @param {Object} record
   */
  @Bind()
  showEditModal(record) {
    if (this.props.showEditModal) {
      this.props.showEditModal(record);
    }
  }

  /**
   * 分配租户
   * a 标签的事件 阻止默认事件
   */
  @Bind()
  handleLineAssignCardBtnClick(record) {
    this.setState({
      cardTenantEditModalProps: {
        key: uuid(),
        modalProps: { visible: true },
        clauseId: record.clauseId,
      },
      currentRecord: record,
    });
  }

  @Bind()
  hiddenCardTenantEditModalAndReload() {
    this.hiddenCardTenantEditModal();
    this.reloadList();
  }

  @Bind()
  hiddenCardTenantEditModal() {
    const { cardTenantEditModalProps } = this.state;
    this.setState({
      cardTenantEditModalProps: {
        ...cardTenantEditModalProps,
        clauseId: undefined, // 去掉 cardId
        modalProps: { visible: false }, // 模态框隐藏
      },
    });
  }

  @Bind()
  handleFetchCardTends(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'dashboardClause/cardTenantFetch',
      payload,
    });
  }

  @Bind()
  handleRemoveCardTenants(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'dashboardClause/cardTenantRemove',
      payload,
    });
  }

  @Bind()
  handleCardTenantEditModalOk(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'dashboardClause/cardTenantAdd',
      payload,
    }).then(res => {
      if (res) {
        // 成功 关闭模态框 不需要刷新页面
        this.hiddenCardTenantEditModal();
        notification.success();
      }
    });
  }

  @Bind()
  handleCardTenantEditModalCancel() {
    this.hiddenCardTenantEditModal();
  }

  /**
   * @function handlePagination - 分页操作
   */
  @Bind()
  handlePagination(pagination) {
    const { onChange = e => e } = this.props;
    onChange({ page: pagination });
  }

  render() {
    const {
      match,
      loading,
      dataSource,
      pagination,
      cardTenantFetchLoading,
      cardTenantAddLoading,
      cardTenantRemoveLoading,
    } = this.props;
    const { cardTenantEditModalProps, currentRecord } = this.state;
    const cardTenantEditModalTableLoading = cardTenantFetchLoading;
    const cardTenantEditModalLoading = cardTenantAddLoading || cardTenantRemoveLoading;
    const columns = [
      {
        title: intl.get(`${promptCode}.model.dashboard.clauseCode`).d('条目代码'),
        dataIndex: 'clauseCode',
      },
      {
        title: intl.get(`${promptCode}.model.dashboard.clauseName`).d('条目名称'),
        dataIndex: 'clauseName',
      },
      !isTenantRoleLevel() && {
        title: intl.get(`${promptCode}.model.dashboard.dataTenantLevel`).d('层级'),
        width: 100,
        dataIndex: 'dataTenantLevelMeaning',
      },
      {
        title: intl.get(`${promptCode}.model.dashboard.menuCode`).d('功能代码'),
        width: 100,
        dataIndex: 'menuCode',
      },
      {
        title: intl.get(`${promptCode}.model.dashboard.route`).d('路由'),
        width: 100,
        dataIndex: 'route',
      },
      {
        title: intl.get(`${promptCode}.model.dashboard.statsExpression`).d('数据匹配表达式'),
        width: 150,
        dataIndex: 'statsExpression',
      },
      {
        title: intl.get(`${promptCode}.model.dashboard.docRemarkExpression`).d('单据标题表达式'),
        width: 150,
        dataIndex: 'docRemarkExpression',
      },
      {
        title: intl.get(`hzero.common.status`).d('状态'),
        dataIndex: 'enabledFlag',
        width: 90,
        render: enableRender,
      },
      {
        title: intl.get(`${promptCode}.model.dashboard.remark`).d('备注'),
        width: 100,
        dataIndex: 'remark',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        fixed: 'right',
        width: 140,
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  key="edit"
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '条目配置-编辑',
                    },
                  ]}
                  onClick={() => this.showEditModal(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (!isTenantRoleLevel() && record.dataTenantLevel === 'TENANT') {
            operators.push({
              key: 'assignCard',
              ele: (
                <ButtonPermission
                  key="assignCard"
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.tenantAssign`,
                      type: 'button',
                      meaning: '条目配置-分配租户',
                    },
                  ]}
                  onClick={() => this.handleLineAssignCardBtnClick(record)}
                >
                  {intl.get(`${promptCode}.view.message.title.tenantAssign`).d('分配租户')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hpfm.card.view.button.assignCard').d('分配卡片'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ].filter(Boolean);
    return (
      <Fragment>
        <Table
          bordered
          loading={loading}
          rowKey="clauseId"
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          scroll={{ x: tableScrollWidth(columns, 400) }}
          onChange={this.handlePagination}
        />
        <TenantEditModal
          {...cardTenantEditModalProps}
          match={match}
          currentRecord={currentRecord}
          confirmLoading={cardTenantEditModalLoading}
          onOk={this.handleCardTenantEditModalOk}
          onCancel={this.handleCardTenantEditModalCancel}
          onFetchCardTenants={this.handleFetchCardTends}
          onRemoveCardTenants={this.handleRemoveCardTenants}
          fetchCardTenantsLoading={cardTenantEditModalTableLoading}
        />
      </Fragment>
    );
  }
}
