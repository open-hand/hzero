/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/3/4
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet, Modal, Button } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import { operatorRender, TagRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import tableDS from '@/stores/BillCallbackRecord/BillCallbackRecordDS';
import { STATUS_LIST } from '@/constants/constants';

const viewModalKey = Modal.key();
let modal;

@formatterCollections({ code: ['hchg.billCbRecord'] })
export default class AccountBalance extends React.Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet({ ...tableDS() });
    this.state = {
      selectedRowKeys: [],
    };
  }

  componentDidMount() {
    // 增加选中记录的事件监听
    this.tableDS.addEventListener('select', this.handleSelectRowKeys);
    // 增加撤销选择记录的事件监听
    this.tableDS.addEventListener('unSelect', this.handleSelectRowKeys);
    // 增加全选记录的事件监听
    this.tableDS.addEventListener('selectAll', this.handleSelectRowKeys);
    // 增加撤销全选记录的事件监听
    this.tableDS.addEventListener('unSelectAll', this.handleSelectRowKeys);
  }

  componentWillUnmount() {
    // 移除选中记录的事件监听
    this.tableDS.removeEventListener('select', this.handleSelectRowKeys);
    // 移除撤销选择记录的事件监听
    this.tableDS.removeEventListener('unSelect', this.handleSelectRowKeys);
    // 移除全选记录的事件监听
    this.tableDS.removeEventListener('selectAll', this.handleSelectRowKeys);
    // 移除撤销全选记录的事件监听
    this.tableDS.removeEventListener('unSelectAll', this.handleSelectRowKeys);
  }

  /**
   * 选中行的key
   */
  @Bind()
  handleSelectRowKeys() {
    this.setState({
      selectedRowKeys: this.tableDS.selected.map((record) => record.get('callbackLogId')),
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
   * 回调消息
   */
  @Bind()
  handleCallbackMessage(value) {
    modal = Modal.open({
      title: intl.get('hchg.billCbRecord.view.title.callback').d('回调消息'),
      closable: true,
      key: viewModalKey,
      children: <div style={{ maxWidth: 472, maxHeight: 400, overflowY: 'scroll' }}>{value}</div>,
      footer: (
        <Button onClick={this.closeModal}>{intl.get('hzero.common.button.close').d('关闭')}</Button>
      ),
    });
  }

  /**
   * 跳转到账单
   */
  @Bind()
  handleGotoBill(billNum) {
    const { history } = this.props;
    history.push(`/hchg/bill/line/${billNum}`);
  }

  /**
   * 重新回调/再次回调
   */
  @Bind()
  async handleCallback(record) {
    record.set('status', 'update');
    await this.tableDS.submit();
    await this.tableDS.query();
  }

  /**
   * 批量回调
   */
  @Bind()
  async handleBachCallback() {
    const { selectedRowKeys } = this.state;
    for (let i = 0; i < this.tableDS.records.length; i++) {
      const record = this.tableDS.records[i];
      if (
        selectedRowKeys.includes(record.get('callbackLogId')) &&
        record.get('callbackStatus') === 'SUCCESS'
      ) {
        return notification.error({
          message: intl
            .get('hchg.billCbRecord.view.message.bach')
            .d('批量回调只能选择回调失败的记录'),
        });
      }
    }
    this.tableDS.records.forEach((record) =>
      selectedRowKeys.includes(record.get('callbackLogId'))
        ? record.set('status', 'update')
        : record
    );
    await this.tableDS.submit();
    await this.tableDS.query();
  }

  get callbackColumns() {
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
        name: 'systemNum',
        width: 160,
      },
      {
        name: 'billNum',
        width: 250,
        renderer: ({ record }) => (
          <a onClick={() => this.handleGotoBill(record.get('billNum'))}>{record.get('billNum')}</a>
        ),
      },
      {
        name: 'callbackUrl',
        width: 300,
      },
      {
        name: 'callbackStatus',
        width: 100,
        align: 'center',
        renderer: ({ value, record }) =>
          TagRender(value, STATUS_LIST, record.get('callbackStatusMeaning')),
      },
      {
        name: 'callbackCount',
        width: 100,
      },
      {
        name: 'callbackTime',
        width: 180,
      },
      {
        name: 'callbackMessage',
        width: 100,
        renderer: ({ value }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.callback`,
                      type: 'button',
                      meaning: '账单回调记录汇总-回调消息',
                    },
                  ]}
                  onClick={() => this.handleCallbackMessage(value)}
                >
                  {intl.get('hchg.billCbRecord.view.button.callback').d('回调消息')}
                </ButtonPermission>
              ),
              key: 'callback',
              len: 4,
              title: intl.get('hchg.billCbRecord.view.button.callback').d('回调消息'),
            },
          ];
          return operatorRender(actions);
        },
        align: 'center',
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
                      code: `${path}.button.reCallBack`,
                      type: 'button',
                      meaning: '账单回调记录汇总-重新回调',
                    },
                  ]}
                  onClick={() => this.handleCallback(record)}
                >
                  {intl.get('hchg.billCbRecord.view.button.reCallBack').d('重新回调')}
                </ButtonPermission>
              ),
              key: 'reCallBack',
              len: 4,
              title: intl.get('hchg.billCbRecord.view.button.reCallBack').d('重新回调'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.callBackAgain`,
                      type: 'button',
                      meaning: '账单回调记录汇总-再次回调',
                    },
                  ]}
                  onClick={() => this.handleCallback(record)}
                >
                  {intl.get('hchg.billCbRecord.view.button.callBackAgain').d('再次回调')}
                </ButtonPermission>
              ),
              key: 'callBackAgain',
              len: 4,
              title: intl.get('hchg.billCbRecord.view.button.callBackAgain').d('再次回调'),
            },
          ];
          // 新建/取消显示编辑和发布按钮，已发布显示取消按钮
          const tempActions = actions.filter((item) =>
            record.get('callbackStatus') === 'SUCCESS'
              ? item.key === 'reCallBack'
              : item.key === 'callBackAgain'
          );
          return operatorRender(tempActions);
        },
      },
    ];
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const { selectedRowKeys } = this.state;
    return (
      <>
        <Header
          title={intl
            .get('hchg.billCbRecord.view.message.title.billCallbackRecord')
            .d('账单回调记录')}
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.batchCallBack`,
                type: 'button',
                meaning: '账单回调记录汇总-批量回调',
              },
            ]}
            icon="plus"
            type="primary"
            disabled={isEmpty(selectedRowKeys)}
            onClick={() => this.handleBachCallback()}
          >
            {intl.get('hchg.billCbRecord.view.button.batchCallBack').d('批量回调')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table border dataSet={this.tableDS} columns={this.callbackColumns} />
        </Content>
      </>
    );
  }
}
