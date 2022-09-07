/**
 * Bill - 账单
 * @date: 2020-2-14
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import notification from 'utils/notification';
import React, { PureComponent } from 'react';
import withProps from 'utils/withProps';
import { TagRender, operatorRender } from 'utils/renderer';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_CHG } from 'hzero-front/lib/utils/config';
import {
  getCurrentOrganizationId,
  isTenantRoleLevel,
  tableScrollWidth,
} from 'hzero-front/lib/utils/utils';
import { isEmpty } from 'lodash';
import { BILL_STATUS_FIELDS, BILL_STATUS_TAGS, PAYMENT_STATUS_TAGS } from '@/constants/constants';
import { axios } from '../../../components';

import BillHeaderDS from '../../../stores/bill/BillHeaderDS';

const organizationId = getCurrentOrganizationId();

/**
 * 购买详单
 * @extends {Component} - PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@withProps(
  () => {
    const tableDS = new DataSet({ ...BillHeaderDS() });
    return { tableDS };
  },
  { cacheState: true }
)
@formatterCollections({ code: ['hchg.bill'] })
export default class Bill extends PureComponent {
  constructor(props) {
    super(props);

    this.state = {
      selectedRows: this.props.tableDS.selected, // 选中行集合
    };
  }

  componentDidMount() {
    const { tableDS } = this.props;
    tableDS.query();
    // 增加选中记录的事件监听
    tableDS.addEventListener('select', () => {
      // 更新选中记录
      this.setState({
        selectedRows: tableDS.selected, // 选中行集合
      });
    });
    // 增加撤销选择记录的事件监听
    tableDS.addEventListener('unSelect', () => {
      // 更新选中记录
      this.setState({
        selectedRows: tableDS.selected, // 选中行集合
      });
    });
    // 增加全选记录的事件监听
    tableDS.addEventListener('selectAll', () => {
      // 更新选中记录
      this.setState({
        selectedRows: tableDS.selected, // 选中行集合
      });
    });
    // 增加撤销全选记录的事件监听
    tableDS.addEventListener('unSelectAll', () => {
      // 更新选中记录
      this.setState({
        selectedRows: tableDS.selected, // 选中行集合
      });
    });
  }

  /**
   * 账单回调
   */
  @Bind()
  callback(rows) {
    const { tableDS } = this.props;
    if (rows) {
      Modal.confirm({
        title: intl.get(`hchg.bill.meaasge.confirm.callback`).d('确定回调？'),
        onOk: async () => {
          const headerIds = rows.map((item) => item.get('headerId'));

          const url = isTenantRoleLevel()
            ? `${HZERO_CHG}/v1/${organizationId}/bill-headers/callback`
            : `${HZERO_CHG}/v1/bill-headers/callback`;
          try {
            const res = await axios.put(url, headerIds);
            if (res.failed) {
              notification.error({
                message: res.message,
              });
            } else {
              notification.success({
                message: intl.get(`hchg.bill.meaasge.pay.callback`).d('回调成功'),
              });
            }
            tableDS.query();
          } catch (err) {
            notification.error({
              message: intl.get(`hchg.bill.meaasge.pay.callbackWait`).d('回调失败，请稍后再试。'),
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
   * 支付
   */
  @Bind()
  pay(rows) {
    const { tableDS } = this.props;
    if (rows) {
      Modal.confirm({
        title: intl.get(`hchg.bill.meaasge.confirm.pay`).d('确定支付？'),
        onOk: async () => {
          const headerIds = rows.map((item) => item.get('headerId'));

          const url = isTenantRoleLevel()
            ? `${HZERO_CHG}/v1/${organizationId}/bill-headers/pay`
            : `${HZERO_CHG}/v1/bill-headers/pay`;
          try {
            const res = await axios.put(url, headerIds);
            if (res.failed) {
              // intl.get(`hchg.bill.meaasge.pay.failed`).d('支付失败')
              notification.error({
                message: res.message,
              });
            } else {
              notification.success({
                message: intl.get(`hchg.bill.meaasge.pay.success`).d('支付成功'),
              });
            }
            tableDS.query();
          } catch (err) {
            notification.error({
              message: intl.get(`hchg.bill.meaasge.pay.wait`).d('支付失败，请稍后再试。'),
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
   * 跳转明细页
   */
  @Bind()
  redirectToDetail(record) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hchg/bill/line/${record.get('billNum')}`,
      })
    );
  }

  render() {
    const {
      tableDS,
      match: { path },
    } = this.props;
    const columns = [
      {
        header: intl.get('hchg.bill.model.billHeader.sort').d('序号'),
        align: 'center',
        lock: 'left',
        width: 70,
        renderer: ({ record }) => (tableDS.currentPage - 1) * tableDS.pageSize + record.index + 1,
      },
      {
        name: 'billName',
      },
      {
        name: 'billNum',
        align: 'center',
        width: 240,
        renderer: ({ value, record }) => (
          <a onClick={() => this.redirectToDetail(record)}>{value}</a>
        ),
      },
      {
        name: 'billDate',
        align: 'center',
        width: 130,
      },
      {
        name: 'amount',
        align: 'right',
      },
      {
        name: 'discountTotal',
        align: 'right',
      },
      {
        name: 'statusCode',
        align: 'center',
        width: 110,
        renderer: ({ value }) => TagRender(value, BILL_STATUS_TAGS),
      },
      {
        name: 'sourceSystemName',
        width: 120,
      },
      {
        name: 'sourceBillNum',
        width: 230,
      },
      {
        name: 'paymentStatus',
        align: 'center',
        width: 120,
        renderer: ({ value }) => TagRender(value, PAYMENT_STATUS_TAGS),
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 100,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.callback`,
                      type: 'button',
                      meaning: '账单-账单回调',
                    },
                  ]}
                  onClick={() => this.callback([record])}
                >
                  {intl.get('hchg.bill.view.button.callback').d('账单回调')}
                </ButtonPermission>
              ),
              key: 'callback',
              len: 4,
              title: intl.get('hchg.bill.view.button.callback').d('账单回调'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.pay`,
                      type: 'button',
                      meaning: '账单-支付',
                    },
                  ]}
                  onClick={() => this.pay([record])}
                >
                  {intl.get('hchg.bill.view.button.pay').d('支付')}
                </ButtonPermission>
              ),
              key: 'pay',
              len: 2,
              title: intl.get('hchg.bill.view.button.pay').d('支付'),
            },
          ];
          // 状态=已结账，显示账单回调，状态=已出账，显示显示支付
          const tempActions = actions.filter((item) =>
            record.get('statusCode') === BILL_STATUS_FIELDS.SETTLED
              ? ['callback'].includes(item.key)
              : record.get('statusCode') === BILL_STATUS_FIELDS.BILLED
              ? ['pay'].includes(item.key)
              : ''
          );
          return operatorRender(tempActions);
        },
      },
    ];
    return (
      <>
        <Header title={intl.get('hchg.bill.view.title.bill').d('账单')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.pay.batch`,
                type: 'button',
                meaning: '账单-批量支付',
              },
            ]}
            icon="check-circle" // payment attach_money
            type="default"
            disabled={isEmpty(this.state.selectedRows)}
            onClick={() => this.pay(this.state.selectedRows)}
          >
            {intl.get('hchg.bill.view.button.pay.batch').d('批量支付')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.callback.batch`,
                type: 'button',
                meaning: '账单-批量回调',
              },
            ]}
            icon="sync"
            type="default"
            disabled={isEmpty(this.state.selectedRows)}
            onClick={() => this.callback(this.state.selectedRows)}
          >
            {intl.get('hchg.bill.view.button.callback.batch').d('批量回调')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table
            dataSet={tableDS}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            queryFieldsLimit={3}
          />
        </Content>
      </>
    );
  }
}
