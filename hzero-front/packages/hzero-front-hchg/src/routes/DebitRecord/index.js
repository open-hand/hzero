/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/2/20
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet, Modal, Button } from 'choerodon-ui/pro';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender, TagRender } from 'utils/renderer';
import { Bind } from 'lodash-decorators';
import tableDS from '@/stores/DebitRecord/DebitRecordDS';
import { STATUS_LIST } from '@/constants/constants';

const viewModalKey = Modal.key();
let modal;

@formatterCollections({ code: ['hchg.debitRecord'] })
export default class DebitRecord extends React.Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet({
      ...tableDS(),
    });
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
                  onClick={() => this.handleProcessMessage(value)}
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

  render() {
    return (
      <>
        <Header title={intl.get('hchg.debitRecord.view.title.debitRecord').d('扣款记录')} />
        <Content>
          <Table border dataSet={this.tableDS} columns={this.debitRecordTableColumns} />
        </Content>
      </>
    );
  }
}
