/**
 * ChargeGroup - 组合计费设置
 * @date: 2020-2-18
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';
import React, { PureComponent } from 'react';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import withProps from 'utils/withProps';

import { Header, Content } from 'components/Page';
import { TagRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import axios from 'axios';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { CHARGE_GROUP_STATUS_TAGS, CHARGE_GROUP_STATUS_FIELDS } from '@/constants/CodeConstants';

import ChargeGroupHeaderDS from '../../../stores/ChargeGroup/ChargeGroupHeaderDS';

import Rule from '../Rule';
import Server from '../Server';
import PurchaseList from '../../ChargeSet/PurchaseList';

const organizationId = getCurrentOrganizationId();

/**
 * 组合计费设置
 * @extends {Component} - PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@withProps(
  () => {
    // 组合计费设置头 数据源
    const tableDS = new DataSet({ ...ChargeGroupHeaderDS() });
    return { tableDS };
  },
  { cacheState: true }
)
@formatterCollections({ code: ['hitf.chargeGroup'] })
export default class ChargeGroup extends PureComponent {
  constructor(props) {
    super(props);

    this.state = {
      buttonFlag: true, // 发布/撤销按钮是否可点击
    };
  }

  componentDidMount() {
    const { tableDS } = this.props;
    tableDS.query();
    // 增加选择记录的事件监听
    tableDS.addEventListener('select', () => {
      // 设置撤销/发布按钮可用
      this.setState({
        buttonFlag: false,
      });
    });
    // 增加撤销选择记录的事件监听
    tableDS.addEventListener('unSelect', () => {
      // 设置撤销/发布按钮不可用
      this.setState({
        buttonFlag: true,
      });
    });
    // 增加全选记录的事件监听
    tableDS.addEventListener('selectAll', () => {
      // 设置撤销/发布按钮可用
      this.setState({
        buttonFlag: false,
      });
    });
    // 增加撤销全选记录的事件监听
    tableDS.addEventListener('unSelectAll', () => {
      // 设置撤销/发布按钮不可用
      this.setState({
        buttonFlag: true,
      });
    });
  }

  /**
   * 新建
   */
  @Bind()
  add() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hitf/charge-group/create`,
      })
    );
  }

  /**
   * 撤销
   */
  @Bind()
  cancel() {
    const { tableDS } = this.props;
    // 先判断是否勾选了数据
    if (tableDS.selected) {
      Modal.confirm({
        title: intl.get('hitf.chargeGroup.view.meaasge.confirm.cancel').d('确定撤销？'),
        onOk: async () => {
          // 请求参数
          const data = tableDS.selected.map((item) => ({
            groupHeaderId: item.get('groupHeaderId'),
            _token: item.get('_token'),
          }));

          const url = isTenantRoleLevel()
            ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers/cancel`
            : `${HZERO_HITF}/v1/charge-group-headers/cancel`;
          try {
            const res = await axios.put(url, data);
            if (res && res.failed) {
              // intl.get(`hitf.chargeGroup.view.meaasge.cancel.failed`).d('撤销失败')
              notification.error({
                message: res.message,
              });
            } else {
              // 撤销成功 刷新数据
              tableDS.query();
              notification.success({
                message: intl.get('hitf.chargeGroup.view.meaasge.cancel.success').d('撤销成功'),
              });
            }
          } catch (err) {
            notification.error({
              message: intl
                .get('hitf.chargeSet.view.meaasge.cancel.wait')
                .d('撤销失败，请稍后再试。'),
            });
          }
        },
      });
    } else {
      notification.error({
        message: intl.get('hzero.common.message.confirm.selected.atLeast').d('请至少选择一行数据'),
      });
    }
  }

  /**
   * 发布
   */
  @Bind()
  publish() {
    const { tableDS } = this.props;
    // 先判断是否勾选了数据
    if (tableDS.selected) {
      Modal.confirm({
        title: intl.get('hitf.chargeGroup.view.meaasge.confirm.publish').d('确定发布？'),
        onOk: async () => {
          // 请求参数
          const data = tableDS.selected.map((item) => ({
            groupHeaderId: item.get('groupHeaderId'),
            _token: item.get('_token'),
          }));

          const url = isTenantRoleLevel()
            ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers/publish`
            : `${HZERO_HITF}/v1/charge-group-headers/publish`;
          try {
            const res = await axios.put(url, data);
            if (res && res.failed) {
              // intl.get(`hitf.chargeSet.view.meaasge.publish.failed`).d('发布失败')
              notification.error({
                message: res.message,
              });
            } else {
              // 发布成功 刷新数据
              tableDS.query();
              notification.success({
                message: intl.get('hitf.chargeSet.view.meaasge.publish.success').d('发布成功'),
              });
            }
          } catch (err) {
            notification.error({
              message: intl
                .get('hitf.chargeSet.view.meaasge.publish.wait')
                .d('发布失败，请稍后再试。'),
            });
          }
        },
      });
    } else {
      notification.error({
        message: intl.get('hzero.common.message.confirm.selected.atLeast').d('请至少选择一行数据'),
      });
    }
  }

  /**
   * 跳转组合计费明细页
   * @param {number} groupHeaderId - 头id
   */
  @Bind()
  redirectToDetail(groupHeaderId) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hitf/charge-group/line/${groupHeaderId}`,
      })
    );
  }

  /**
   * 规则列表模态框
   * @param record
   */
  @Bind()
  ruleDetailModal(record) {
    // 组件参数
    const ruleDetailProps = {
      groupHeaderId: record.get('groupHeaderId'),
    };
    Modal.open({
      drawer: false,
      key: 'ruleDetail',
      style: { width: '60%' },
      destroyOnClose: true,
      closable: true,
      okCancel: false,
      title: intl.get('hitf.chargeGroup.model.chargeGroupHeader.chargeGroupRule').d('计费规则'),
      children: <Rule {...ruleDetailProps} />,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  }

  /**
   * 服务列表模态框
   * @param record
   */
  @Bind()
  serverDetailModal(record) {
    // 组件参数
    const serverDetailProps = {
      groupHeaderId: record.get('groupHeaderId'),
    };
    Modal.open({
      drawer: false,
      key: 'serverDetail',
      style: { width: '60%' },
      // destroyOnClose: true,
      closable: true,
      okCancel: false,
      title: intl.get('hitf.chargeGroup.model.chargeGroupHeader.serverList').d('服务列表'),
      children: <Server {...serverDetailProps} />,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  }

  /**
   * 购买详情模态框
   * @param record
   */
  @Bind()
  purchaseListModal(record) {
    // 组件参数
    const purchaseListProps = {
      id: record.get('groupHeaderId'),
      typeCode: 'GROUP',
    };
    Modal.open({
      drawer: false,
      key: 'purchaseList-chargeGroup',
      style: { width: '60%' },
      // destroyOnClose: true,
      closable: true,
      okCancel: false,
      title: intl.get('hitf.purchase.view.message.title.purchaseList').d('购买列表'),
      children: <PurchaseList {...purchaseListProps} />,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  }

  render() {
    const { buttonFlag } = this.state;
    const {
      tableDS,
      match: { path },
    } = this.props;
    return (
      <>
        <Header
          title={intl
            .get('hitf.chargeGroup.view.message.title.chargeGroupHeader')
            .d('组合计费设置')}
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.cancel`,
                type: 'button',
                meaning: '组合计费设置-撤销',
              },
            ]}
            icon="close"
            type="default"
            onClick={this.cancel}
            disabled={buttonFlag}
          >
            {intl.get('hzero.common.status.revoke').d('撤销')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.publish`,
                type: 'button',
                meaning: '组合计费设置-发布',
              },
            ]}
            icon="check"
            type="default"
            onClick={this.publish}
            disabled={buttonFlag}
          >
            {intl.get('hzero.common.button.release').d('发布')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '组合计费设置-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.add}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table
            dataSet={tableDS}
            columns={[
              {
                header: intl.get('hitf.chargeGroup.model.chargeGroupHeader.sort').d('序号'),
                lock: 'left',
                width: 70,
                align: 'center',
                renderer: ({ record }) =>
                  (tableDS.currentPage - 1) * tableDS.pageSize + record.index + 1,
              },
              {
                name: 'groupCode',
                width: 240,
              },
              {
                name: 'groupName',
                width: 240,
                align: 'center',
                renderer: ({ value, record }) => (
                  <a onClick={() => this.redirectToDetail(record.get('groupHeaderId'))}>{value}</a>
                ),
              },
              {
                name: 'statusCode',
                align: 'center',
                renderer: ({ value }) => TagRender(value, CHARGE_GROUP_STATUS_TAGS),
              },
              {
                header: intl
                  .get('hitf.chargeGroup.model.chargeGroupHeader.chargeGroupRule')
                  .d('计费规则'),
                align: 'center',
                renderer: ({ record }) => (
                  <span className="action-link">
                    <a onClick={() => this.ruleDetailModal(record)}>
                      {intl.get('hitf.chargeGroup.model.chargeGroupHeader.ruleList').d('规则列表')}
                    </a>
                  </span>
                ),
              },
              {
                header: intl
                  .get('hitf.chargeGroup.model.chargeGroupHeader.serverList')
                  .d('服务列表'),
                align: 'center',
                renderer: ({ record }) => (
                  <span className="action-link">
                    <a onClick={() => this.serverDetailModal(record)}>
                      {intl
                        .get('hitf.chargeGroup.model.chargeGroupHeader.serverList')
                        .d('服务列表')}
                    </a>
                  </span>
                ),
              },
              {
                header: intl
                  .get('hitf.chargeGroup.model.chargeGroupHeader.purchaseList')
                  .d('订购列表'),
                align: 'center',
                renderer: ({ record }) => (
                  <span className="action-link">
                    <a onClick={() => this.purchaseListModal(record)}>
                      {intl
                        .get('hitf.chargeGroup.model.chargeGroupHeader.purchaseList')
                        .d('订购列表')}
                    </a>
                  </span>
                ),
              },
              {
                header: intl.get('hzero.common.button.action').d('操作'),
                lock: 'right',
                align: 'center',
                width: 100,
                renderer: ({ record }) => {
                  const actions = [
                    {
                      ele: (
                        <ButtonPermission
                          type="text"
                          permissionList={[
                            {
                              code: `${path}.button.delete`,
                              type: 'button',
                              meaning: '组合计费设置-删除',
                            },
                          ]}
                          onClick={() => tableDS.delete(record)}
                        >
                          {intl.get('hzero.common.button.delete').d('删除')}
                        </ButtonPermission>
                      ),
                      key: 'delete',
                      len: 2,
                      title: intl.get('hzero.common.button.delete').d('删除'),
                    },
                  ];
                  // 新建显示删除按钮，其他状态不显示
                  const tempActions = actions.filter((item) =>
                    record.get('statusCode') === CHARGE_GROUP_STATUS_FIELDS.NEW
                      ? ['delete'].includes(item.key)
                      : ''
                  );
                  return operatorRender(tempActions);
                },
              },
            ]}
            queryFieldsLimit={3}
          />
        </Content>
      </>
    );
  }
}
