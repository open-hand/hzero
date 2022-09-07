/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/2/19
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import {
  Table,
  DataSet,
  Form,
  TextField,
  Tabs,
  Select,
  Currency,
  Modal,
  Button,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender, TagRender } from 'utils/renderer';
import { Bind } from 'lodash-decorators';
import {
  detailFormDS,
  rechargeRecordTableDS,
  debitRecordTableDS,
} from '@/stores/AccountBalance/AccountBalanceDS';
import { STATUS_LIST } from '@/constants/constants';

const viewModalKey = Modal.key();
let modal;

@formatterCollections({ code: ['hchg.accountBalance'] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.detailFormDS = new DataSet(detailFormDS);
    this.rechargeRecordTableDS = new DataSet(rechargeRecordTableDS);
    this.debitRecordTableDS = new DataSet(debitRecordTableDS);
  }

  async componentDidMount() {
    const {
      match: { params },
    } = this.props;
    const { id } = params;
    this.detailFormDS.setQueryParameter('balanceId', id);
    this.rechargeRecordTableDS.setQueryParameter('balanceId', id);
    this.debitRecordTableDS.setQueryParameter('balanceId', id);
    await this.detailFormDS.query();
    await this.rechargeRecordTableDS.query();
    await this.debitRecordTableDS.query();
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
  handleProcessMessage(title, value) {
    modal = Modal.open({
      title,
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
          (this.rechargeRecordTableDS.currentPage - 1) * this.rechargeRecordTableDS.pageSize +
          record.index +
          1,
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
                      code: `${path}.button.processRecord`,
                      type: 'button',
                      meaning: '充值记录-处理日志',
                    },
                  ]}
                  onClick={() =>
                    this.handleProcessMessage(
                      intl.get('hchg.accountBalance.view.button.processRecord').d('处理日志'),
                      value
                    )}
                >
                  {intl.get('hchg.accountBalance.view.button.processRecord').d('处理日志')}
                </ButtonPermission>
              ),
              key: 'processRecord',
              len: 4,
              title: intl.get('hchg.accountBalance.view.button.processRecord').d('处理日志'),
            },
          ];
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'center',
      },
    ];
  }

  /**
   * 扣款记录columns
   */
  get debitRecordTableColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'sequence',
        width: 70,
        align: 'center',
        renderer: ({ record }) =>
          (this.debitRecordTableDS.currentPage - 1) * this.debitRecordTableDS.pageSize +
          record.index +
          1,
      },
      {
        name: 'debitTime',
        width: 180,
        align: 'center',
      },
      {
        name: 'costName',
        width: 150,
      },
      {
        name: 'transactionSerial',
        width: 210,
      },
      {
        name: 'debitAmount',
        width: 120,
      },
      {
        name: 'debitStatus',
        width: 100,
        align: 'center',
        renderer: ({ value, record }) =>
          TagRender(value, STATUS_LIST, record.get('debitStatusMeaning')),
      },
      {
        name: 'balanceAmount',
        width: 150,
      },
      {
        name: 'businessCode',
        width: 140,
      },
      {
        name: 'businessKey',
        width: 280,
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
                      meaning: '充值记录-处理消息',
                    },
                  ]}
                  onClick={() =>
                    this.handleProcessMessage(
                      intl.get('hchg.accountBalance.view.button.processMessage').d('处理消息'),
                      value
                    )}
                >
                  {intl.get('hchg.accountBalance.view.button.processMessage').d('处理消息')}
                </ButtonPermission>
              ),
              key: 'processMessage',
              len: 4,
              title: intl.get('hchg.accountBalance.view.button.processMessage').d('处理消息'),
            },
          ];
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'center',
      },
    ];
  }

  /**
   * 跳转到充值页面
   */
  @Bind()
  handleGotoRecharge() {
    const {
      history,
      match: { params },
    } = this.props;
    history.push(`/hchg/account-balance/recharge/${params.id}`);
  }

  render() {
    const { match } = this.props;
    const { path } = match;
    return (
      <>
        <Header
          title={intl.get('hchg.accountBalance.view.title.accountBalanceDetail').d('账户余额详情')}
          backPath="/hchg/account-balance/list"
        >
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
            onClick={this.handleGotoRecharge}
          >
            {intl.get('hchg.accountBalance.view.button.recharge').d('充值')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>{intl.get('hchg.accountBalance.view.title.basicInformation').d('基本信息')}</h3>
            }
          >
            <Form labelLayout="horizontal" dataSet={this.detailFormDS} columns={3}>
              <TextField name="accountNum" disabled />
              <Select name="accountType" disabled />
              <TextField name="accountName" disabled />
              <Select name="enabledFlag" disabled />
              <Currency name="balanceAmount" disabled />
              <TextField name="remark" disabled colSpan={2} />
            </Form>
          </Card>
          <Tabs animated={false}>
            <Tabs.TabPane
              tab={intl.get('hchg.accountBalance.view.title.rechargeRecord').d('充值记录')}
              key="rechargeRecord"
            >
              <Table
                border
                dataSet={this.rechargeRecordTableDS}
                columns={this.rechargeRecordTableColumns}
              />
            </Tabs.TabPane>
            <Tabs.TabPane
              tab={intl.get('hchg.accountBalance.view.title.debitRecord').d('扣款记录')}
              key="debitRecord"
            >
              <Table
                border
                dataSet={this.debitRecordTableDS}
                columns={this.debitRecordTableColumns}
              />
            </Tabs.TabPane>
          </Tabs>
        </Content>
      </>
    );
  }
}
