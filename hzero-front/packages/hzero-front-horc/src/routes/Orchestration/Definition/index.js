/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/6/10
 * @copyright HAND ® 2020
 */
import React from 'react';
import { connect } from 'dva';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet, Modal } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { operatorRender, TagRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { routerRedux } from 'dva/router';
import { isUndefined } from 'lodash';
import { getResponse } from 'utils/utils';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import { tableDS } from '@/stores/Orchestration/definitionDS';
import {
  ORCH_DEF_STATUS,
  ORCH_DEF_STATUS_OFFLINE,
  ORCH_DEF_STATUS_ONLINE,
} from '@/constants/constants';
import { duplicate } from '@/services/orchestrationService';
import ExecuteModal from './ExecuteModal';

@connect(({ orchestration }) => ({
  orchestration,
}))
@formatterCollections({ code: ['horc.orchestration'] })
export default class List extends React.Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet(tableDS());
    this.state = {
      execLoading: false,
    };
  }

  componentDidMount() {
    this.props.dispatch({ type: 'orchestration/init' });
  }

  /**
   * 调整到新建/明细页面
   * @param {*} id
   */
  @Bind()
  handleGotoDetail(id) {
    const { dispatch = () => {} } = this.props;
    const path = isUndefined(id) ? '/create' : `/detail/${id}`;
    dispatch(
      routerRedux.push({
        pathname: `/horc/orchestration-definition${path}`,
      })
    );
  }

  /**
   * 打开运行弹窗
   */
  openExecuteModal(record) {
    const { match, orchestration } = this.props;
    const { failureStrategyList } = orchestration;
    const { execLoading } = this.state;
    const { path } = match.params;
    const modalProps = {
      failureStrategyList,
      executeRecord: record,
      onRef: (ref) => {
        this.executeFormDS = ref.executeFormDS;
      },
    };
    this.executeModal = Modal.open({
      title: ORCHESTRATION_LANG.EXECUTE_TITLE,
      closable: true,
      destroyOnClose: true,
      style: { width: 600 },
      children: <ExecuteModal {...modalProps} />,
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: ORCHESTRATION_LANG.EXECUTE,
              },
            ]}
            type="c7n-pro"
            color="primary"
            wait={1000}
            loading={execLoading}
            onClick={this.handleExecute}
          >
            {ORCHESTRATION_LANG.EXECUTE}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 执行
   */
  @Bind()
  async handleExecute() {
    const validate = await this.executeFormDS.validate();
    if (validate) {
      this.setState({ execLoading: true });
      const response = await this.executeFormDS.submit();
      this.setState({ execLoading: false });
      if (getResponse(response)) {
        await this.tableDS.query();
        this.executeModal.close();
      }
    }
  }

  /**
   * 上线/下线
   */
  @Bind()
  async handleToggle(record, requestType) {
    record.set('_requestType', requestType);
    const confirm = await Modal.confirm({
      children: (
        <p>
          {requestType === 'online'
            ? ORCHESTRATION_LANG.ONLINE_CONFIRM
            : ORCHESTRATION_LANG.OFFLINE_CONFIRM}
        </p>
      ),
    });
    if (confirm === 'ok') {
      const res = await this.tableDS.submit();
      if (getResponse(res)) {
        await this.tableDS.query();
      }
    }
  }

  /**
   * 处理克隆
   * @returns {Promise<void>}
   */
  @Bind()
  async handDuplicate(record) {
    const res = await duplicate(record.get('definitionId'));
    if (getResponse(res)) {
      await this.tableDS.query();
    }
  }

  get serviceChoreographyColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'seqNumber',
        width: 80,
        align: 'center',
        renderer: ({ record }) =>
          (this.tableDS.currentPage - 1) * this.tableDS.pageSize + record.index + 1,
      },
      {
        name: 'definitionName',
        renderer: ({ value, record }) => (
          <a onClick={() => this.handleGotoDetail(record.get('definitionId'))}>{value}</a>
        ),
      },
      {
        name: 'statusCode',
        width: 120,
        align: 'center',
        renderer: ({ value }) => TagRender(value, ORCH_DEF_STATUS),
      },
      {
        name: 'remark',
      },
      {
        header: ORCHESTRATION_LANG.OPERATOR,
        width: 400,
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
                      code: `${path}.button.execute`,
                      type: 'button',
                      meaning: '编排定义-运行',
                    },
                  ]}
                  disabled={record.get('statusCode') === 'OFFLINE'}
                  onClick={() => this.openExecuteModal(record)}
                >
                  {ORCHESTRATION_LANG.EXECUTE}
                </ButtonPermission>
              ),
              key: 'execute',
              len: 2,
              title: ORCHESTRATION_LANG.EXECUTE,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.duplicate`,
                      type: 'button',
                      meaning: '编排实例-克隆',
                    },
                  ]}
                  onClick={() => this.handDuplicate(record)}
                >
                  {ORCHESTRATION_LANG.DUPLICATE}
                </ButtonPermission>
              ),
              key: 'rerun',
              len: 2,
              title: ORCHESTRATION_LANG.DUPLICATE,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.scheduler`,
                      type: 'button',
                      meaning: '编排实例-调度任务',
                    },
                  ]}
                  disabled
                >
                  {ORCHESTRATION_LANG.SCHEDULER}
                </ButtonPermission>
              ),
              key: 'scheduler',
              len: 4,
              title: ORCHESTRATION_LANG.SCHEDULER,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '编排定义-编辑',
                    },
                  ]}
                  disabled={record.get('statusCode') === 'ONLINE'}
                  onClick={() => this.handleGotoDetail(record.get('definitionId'))}
                >
                  {ORCHESTRATION_LANG.EDIT}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: ORCHESTRATION_LANG.EDIT,
            },
            ORCH_DEF_STATUS_OFFLINE === record.get('statusCode') && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.online`,
                      type: 'button',
                      meaning: '编排定义-上线',
                    },
                  ]}
                  onClick={() => this.handleToggle(record, 'online')}
                >
                  {ORCHESTRATION_LANG.ONLINE}
                </ButtonPermission>
              ),
              key: 'online',
              len: 2,
              title: ORCHESTRATION_LANG.ONLINE,
            },
            ORCH_DEF_STATUS_ONLINE === record.get('statusCode') && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.offline`,
                      type: 'button',
                      meaning: '编排定义-下线',
                    },
                  ]}
                  onClick={() => this.handleToggle(record, 'offline')}
                >
                  {ORCHESTRATION_LANG.OFFLINE}
                </ButtonPermission>
              ),
              key: 'offline',
              len: 2,
              title: ORCHESTRATION_LANG.OFFLINE,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '编排定义-删除',
                    },
                  ]}
                  disabled={record.get('statusCode') === 'ONLINE'}
                  onClick={() => this.tableDS.delete(record)}
                >
                  {ORCHESTRATION_LANG.DELETE}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: ORCHESTRATION_LANG.DELETE,
            },
          ];
          return operatorRender(actions, record, { limit: 8 });
        },
      },
    ];
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={ORCHESTRATION_LANG.HEADER}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '服务编排-新建',
              },
            ]}
            icon="add"
            type="c7n-pro"
            color="primary"
            onClick={() => this.handleGotoDetail()}
          >
            {ORCHESTRATION_LANG.CREATE}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.tableDS} columns={this.serviceChoreographyColumns} />
        </Content>
      </>
    );
  }
}
