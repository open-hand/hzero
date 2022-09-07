/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/2/19
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import withProps from 'utils/withProps';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { operatorRender, enableRender } from 'utils/renderer';
import { isUndefined } from 'lodash';
import formatterCollections from 'utils/intl/formatterCollections';
import { accountBalanceTableDS } from '@/stores/AccountBalance/AccountBalanceDS';

@withProps(
  () => {
    const tableDS = new DataSet(accountBalanceTableDS);
    return { tableDS };
  },
  { cacheState: true }
)
@formatterCollections({ code: ['hchg.accountBalance'] })
export default class AccountBalance extends React.Component {
  componentDidMount() {
    this.props.tableDS.query();
  }

  /**
   * 跳转到明细
   */
  @Bind()
  handleGotoDetail(id) {
    const { history } = this.props;
    history.push(`/hchg/account-balance/detail/${id}`);
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
   * 跳转到创建账户页面
   */
  @Bind()
  handleCreateAccount() {
    const { history } = this.props;
    history.push(`/hchg/account-balance/create`);
  }

  /**
   * 启用/禁用
   */
  @Bind()
  async handleEnable(record, type) {
    record.set('_status', 'update');
    record.set('_type', type);
    await this.props.tableDS.submit();
    await this.props.tableDS.query();
  }

  /**
   * 取消充值
   */
  handleCancel(id) {
    console.log(id);
  }

  get accountBalanceColumns() {
    const {
      tableDS,
      match: { path },
    } = this.props;
    return [
      {
        name: 'sequence',
        width: 70,
        align: 'center',
        renderer: ({ record }) => (tableDS.currentPage - 1) * tableDS.pageSize + record.index + 1,
      },
      {
        name: 'accountNum',
        width: 280,
        renderer: ({ record }) => (
          <a onClick={() => this.handleGotoDetail(record.get('balanceId'))}>
            {record.get('accountNum')}
          </a>
        ),
      },
      {
        name: 'accountType',
        width: 100,
      },
      {
        name: 'accountName',
        width: 200,
      },
      {
        name: 'balanceAmount',
        width: 150,
      },
      {
        name: 'enabledFlag',
        width: 100,
        align: 'center',
        renderer: ({ value }) => enableRender(value),
      },
      {
        name: 'remark',
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 110,
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
                      code: `${path}.button.recharge`,
                      type: 'button',
                      meaning: '账户余额汇总-充值',
                    },
                  ]}
                  onClick={() => this.handleGotoRecharge(record.get('balanceId'))}
                >
                  {intl.get('hchg.accountBalance.view.button.recharge').d('充值')}
                </ButtonPermission>
              ),
              key: 'recharge',
              len: 2,
              title: intl.get('hchg.accountBalance.view.button.recharge').d('充值'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.disable`,
                      type: 'button',
                      meaning: '账户余额汇总-禁用',
                    },
                  ]}
                  onClick={() => this.handleEnable(record, 'disabled')}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              key: 'disable',
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.enable`,
                      type: 'button',
                      meaning: '账户余额汇总-启用',
                    },
                  ]}
                  onClick={() => this.handleEnable(record, 'enabled')}
                >
                  {intl.get('hzero.common.button.enable').d('启用')}
                </ButtonPermission>
              ),
              key: 'enable',
              len: 2,
              title: intl.get('hzero.common.button.enable').d('启用'),
            },
            // {
            //   ele: (
            //     <ButtonPermission
            //       type="text"
            //       permissionList={[
            //         {
            //           code: `${path}/cancel`,
            //           type: 'button',
            //           meaning: '账户余额汇总-取消',
            //         },
            //       ]}
            //       onClick={() => this.handleCancel(record.get('balanceId'))}
            //     >
            //       {intl.get('hzero.common.button.cancel').d('取消')}
            //     </ButtonPermission>
            //   ),
            //   key: 'cancel',
            //   len: 2,
            //   title: intl.get('hzero.common.button.cancel').d('取消'),
            // },
          ];
          // 新建/取消显示编辑和发布按钮，已发布显示取消按钮
          const tempActions = actions.filter((item) =>
            record.get('enabledFlag')
              ? ['recharge', 'disable'].includes(item.key)
              : ['enable', 'cancel'].includes(item.key)
          );
          return operatorRender(tempActions);
        },
      },
    ];
  }

  render() {
    const {
      tableDS,
      match: { path },
    } = this.props;
    return (
      <>
        <Header
          title={intl.get('hchg.accountBalance.view.message.title.accountBalance').d('账户余额')}
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
            onClick={() => this.handleGotoRecharge()}
          >
            {intl.get('hchg.accountBalance.view.button.recharge').d('充值')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '账户余额汇总-创建账户',
              },
            ]}
            icon="plus"
            onClick={() => this.handleCreateAccount()}
          >
            {intl.get('hchg.accountBalance.view.button.create').d('创建账户')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table border dataSet={tableDS} columns={this.accountBalanceColumns} />
        </Content>
      </>
    );
  }
}
