/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/2/20
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet, Button, Modal } from 'choerodon-ui/pro';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender, TagRender } from 'utils/renderer';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import tableDS from '@/stores/RechargeRecord/RechargeRecordDS';
import { STATUS_LIST } from '@/constants/constants';

const viewModalKey = Modal.key();
let modal;

@formatterCollections({ code: ['hchg.rechargeRecord'] })
export default class ChargeRecord extends React.Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet({
      ...tableDS(),
    });
  }

  /**
   * 跳转到充值页面
   */
  @Bind()
  handleGotoRecharge(id) {
    const { history } = this.props;
    const url = isUndefined(id) ? '/select' : `/${id}`;
    history.push(`/hchg/account-balance/recharge${url}`);
  }

  /**
   * 关闭消息弹窗
   */
  @Bind()
  closeModal() {
    modal.close();
  }

  /**
   * 处理日志
   */
  @Bind()
  handleProcessMessage(value) {
    modal = Modal.open({
      title: intl.get('hchg.accountBalance.view.title.processMessage').d('处理日志'),
      closable: true,
      key: viewModalKey,
      children: <div style={{ maxWidth: 472, maxHeight: 400, overflowY: 'scroll' }}>{value}</div>,
      footer: (
        <Button onClick={this.closeModal}>{intl.get('hzero.common.button.close').d('关闭')}</Button>
      ),
    });
  }

  /**
   * 充值记录columns
   */
  get rechargeRecordTableColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'sequence',
        width: 70,
        align: 'center',
        renderer: ({ record }) =>
          (this.tableDS.currentPage - 1) * this.tableDS.pageSize + record.index + 1,
      },
      {
        name: 'accountName',
        width: 120,
      },
      {
        name: 'accountNum',
        width: 200,
      },
      {
        name: 'requestTime',
        width: 180,
        align: 'center',
      },
      {
        name: 'rechargeChannel',
        width: 100,
      },
      {
        name: 'rechargeAmount',
        width: 120,
      },
      {
        name: 'rechargedTime',
        width: 180,
        align: 'center',
      },
      {
        name: 'transactionSerial',
        width: 240,
      },
      {
        name: 'balanceAmount',
        width: 150,
      },
      {
        name: 'rechargeStatus',
        width: 130,
        align: 'center',
        renderer: ({ value }) => TagRender(value, STATUS_LIST),
      },
      {
        name: 'remark',
      },
      {
        name: 'processMessage',
        width: 100,
        renderer: ({ value }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.processMessage`,
                      type: 'button',
                      meaning: '充值记录-处理日志',
                    },
                  ]}
                  onClick={() => this.handleProcessMessage(value)}
                >
                  {intl.get('hchg.rechargeRecord.view.button.processMessage').d('处理日志')}
                </ButtonPermission>
              ),
              key: 'processMessage',
              len: 4,
              title: intl.get('hchg.rechargeRecord.view.button.processMessage').d('处理日志'),
            },
          ];
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'center',
      },
    ];
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hchg.rechargeRecord.view.title.rechargeRecord').d('充值记录')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.recharge`,
                type: 'button',
                meaning: '账户余额汇总-充值',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={() => this.handleGotoRecharge()}
          >
            {intl.get('hchg.accountBalance.view.button.recharge').d('充值')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table border dataSet={this.tableDS} columns={this.rechargeRecordTableColumns} />
        </Content>
      </>
    );
  }
}
