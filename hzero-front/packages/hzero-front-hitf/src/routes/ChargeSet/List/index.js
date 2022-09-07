/**
 * ChargeSet - 接口/服务计费设置
 * @date: 2020-2-17
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import notification from 'utils/notification';
import React, { PureComponent } from 'react';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import withProps from 'utils/withProps';
import axios from 'axios';
import { Header, Content } from 'components/Page';
import { TagRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import {
  CHARGE_TYPE_TAGS,
  CHARGE_SET_STATUS_TAGS,
  CHARGE_SET_STATUS_FIELDS,
} from '@/constants/CodeConstants';

import ChargeSetHeaderDS from '../../../stores/ChargeSet/ChargeSetHeaderDS';

import PurchaseList from '../PurchaseList';

const organizationId = getCurrentOrganizationId();

/**
 * 接口/服务计费设置
 * @extends {Component} - PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@withProps(
  () => {
    // 接口/服务计费设置头 数据源
    const tableDS = new DataSet({ ...ChargeSetHeaderDS() });
    return { tableDS };
  },
  { cacheState: true }
)
@formatterCollections({ code: ['hitf.chargeSet'] })
export default class ChargeSet extends PureComponent {
  constructor(props) {
    super(props);

    this.state = {
      buttonFlag: true, // 撤销/发布按钮是否可点击
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
        pathname: `/hitf/charge-set/create`,
      })
    );
  }

  /**
   * 撤销
   */
  @Bind()
  cancel() {
    const { tableDS } = this.props;
    // 判断是否勾选了数据
    if (tableDS.selected) {
      Modal.confirm({
        title: intl.get('hitf.chargeSet.view.meaasge.confirm.cancel').d('确定撤销？'),
        onOk: async () => {
          // 请求参数
          const data = tableDS.selected.map((item) => ({
            setHeaderId: item.get('setHeaderId'),
            _token: item.get('_token'),
          }));

          const url = isTenantRoleLevel()
            ? `${HZERO_HITF}/v1/${organizationId}/charge-set-headers/cancel`
            : `${HZERO_HITF}/v1/charge-set-headers/cancel`;
          try {
            const res = await axios.put(url, data);
            if (res && res.failed) {
              // intl.get('hitf.chargeSet.view.meaasge.cancel.failed').d('撤销失败')
              notification.error({
                message: res.message,
              });
            } else {
              // 撤销成功 刷新数据
              tableDS.query();
              notification.success({
                message: intl.get('hitf.chargeSet.view.meaasge.cancel.success').d('撤销成功'),
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
    // 判断是否勾选了数据
    if (tableDS.selected) {
      Modal.confirm({
        title: intl.get('hitf.chargeSet.view.meaasge.confirm.publish').d('确定发布？'),
        onOk: async () => {
          // 请求参数
          const data = tableDS.selected.map((item) => ({
            setHeaderId: item.get('setHeaderId'),
            _token: item.get('_token'),
          }));

          const url = isTenantRoleLevel()
            ? `${HZERO_HITF}/v1/${organizationId}/charge-set-headers/publish`
            : `${HZERO_HITF}/v1/charge-set-headers/publish`;
          try {
            const res = await axios.put(url, data);
            if (res && res.failed) {
              // intl.get('hitf.chargeSet.view.meaasge.publish.failed').d('发布失败');
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
   * 跳转计费明细页
   * @param {number} headerId - 头id
   */
  @Bind()
  redirectToDetail(setHeaderId) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hitf/charge-set/line/${setHeaderId}`,
      })
    );
  }

  /**
   * 删除
   * @param {number} record - 行记录
   */
  @Bind()
  delete(record) {
    const { tableDS } = this.props;
    tableDS.delete(record);
  }

  /**
   * 购买详情模态框
   * @param record
   */
  @Bind()
  purchaseListModal(record) {
    // 组件参数
    const purchaseListProps = {
      id: record.get('setHeaderId'),
      typeCode: record.get('typeCode'),
    };
    Modal.open({
      drawer: false,
      key: 'purchaseList-chargeSet',
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
          title={intl.get('hitf.chargeSet.view.message.title.chargeSet').d('接口服务计费设置')}
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.cancel`,
                type: 'button',
                meaning: '接口计费设置-撤销',
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
                meaning: '接口计费设置-发布',
              },
            ]}
            icon="check"
            type="default"
            onClick={this.publish}
            disabled={buttonFlag}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '接口计费设置-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.add}
          >
            {intl.get('hitf.chargeSet.view.button.add').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table
            dataSet={tableDS}
            columns={[
              {
                header: intl.get('hitf.chargeSet.model.chargeSetHeader.sort').d('序号'),
                lock: 'left',
                width: 70,
                align: 'center',
                renderer: ({ record }) =>
                  (tableDS.currentPage - 1) * tableDS.pageSize + record.index + 1,
              },
              {
                name: 'setCode',
                width: 240,
              },
              {
                name: 'setName',
                align: 'center',
                width: 240,
                renderer: ({ value, record }) => (
                  <a onClick={() => this.redirectToDetail(record.get('setHeaderId'))}>{value}</a>
                ),
              },
              {
                name: 'typeCode',
                align: 'center',
                renderer: ({ value }) => TagRender(value, CHARGE_TYPE_TAGS),
              },
              {
                name: 'serverCode',
                width: 240,
              },
              {
                name: 'serverName',
                width: 240,
              },
              {
                name: 'interfaceCode',
                width: 240,
              },
              {
                name: 'interfaceName',
                width: 240,
              },
              {
                name: 'startDate',
                align: 'center',
                width: 130,
              },
              {
                name: 'statusCode',
                align: 'center',
                renderer: ({ value }) => TagRender(value, CHARGE_SET_STATUS_TAGS),
              },
              {
                header: intl.get('hzero.common.button.action').d('操作'),
                lock: 'right',
                align: 'center',
                width: 200,
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
                              meaning: '接口服务计费设置-删除',
                            },
                          ]}
                          onClick={() => this.delete(record)}
                        >
                          {intl.get('hzero.common.button.delete').d('删除')}
                        </ButtonPermission>
                      ),
                      key: 'delete',
                      len: 2,
                      title: intl.get('hzero.common.button.delete').d('删除'),
                    },
                    {
                      ele: (
                        <ButtonPermission
                          type="text"
                          permissionList={[
                            {
                              code: `${path}.button.buyDetail`,
                              type: 'button',
                              meaning: '接口服务计费设置-购买详情',
                            },
                          ]}
                          onClick={() => this.purchaseListModal(record)}
                        >
                          {intl.get('hitf.chargeSet.view.button.buyDetail').d('购买详情')}
                        </ButtonPermission>
                      ),
                      key: 'buyDetail',
                      len: 4,
                      title: intl.get('hitf.chargeSet.view.button.buyDetail').d('购买详情'),
                    },
                  ];
                  // 新建显示删除和购买详情按钮，其他状态只显示购买详情按钮
                  const tempActions = actions.filter((item) =>
                    record.get('statusCode') === CHARGE_SET_STATUS_FIELDS.NEW
                      ? ['delete', 'buyDetail'].includes(item.key)
                      : ['buyDetail'].includes(item.key)
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
