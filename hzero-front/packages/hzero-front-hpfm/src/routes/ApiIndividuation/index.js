/**
 * apiIndividuation API个性化
 * @date: 2020-7-14
 * @author: jmy <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import {
  Table,
  Modal,
  ModalContainer,
  DataSet,
  Form,
  Output,
  CodeArea,
  Spin,
} from 'choerodon-ui/pro';
import { Badge } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';

import { initDS, applyDS, unApplyDS, disableDS, enableDS } from '@/stores/apiIndividuationDS';
import HistoryDrawer from './HistoryDrawer';
import LogDrawer from './LogDrawer';

@formatterCollections({ code: ['hpfm.apiIndividuation'] })
export default class apiIndividuation extends React.Component {
  state = { loading: false };

  initDs = new DataSet(initDS());

  applyDs = new DataSet(applyDS());

  unApplyDs = new DataSet(unApplyDS());

  disableDs = new DataSet(disableDS());

  enableDs = new DataSet(enableDS());

  componentDidMount() {
    this.initDs.query();
  }

  /**
   * 新建
   * @memberof apiIndividuation
   */
  @Bind()
  handleCreate() {
    const { history } = this.props;
    history.push(`/hpfm/api-customize/create/create`);
  }

  /**
   * 编辑
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  @Bind()
  handleUpdate(record) {
    const { history } = this.props;
    history.push(`/hpfm/api-customize/edit/${record.get('customizeId')}`);
  }

  /**
   * 详情
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  @Bind()
  handleDetail(record) {
    const { history } = this.props;
    history.push(`/hpfm/api-customize/detail/${record.get('customizeId')}`);
  }

  /**
   * 删除
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  @Bind()
  async handleDelete(record) {
    await this.initDs.delete(record);
    this.initDs.query();
  }

  /**
   * 应用
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  @Bind()
  handleApply(record) {
    this.setState({ loading: true });
    this.applyDs.setQueryParameter('customizeIds', [record.get('customizeId')]);
    this.applyDs
      .query()
      .then(() => {
        this.setState({ loading: false });
        this.initDs.query();
        Modal.open({
          closable: true,
          key: 'historyDetail',
          title: intl.get('hpfm.apiIndividuation.view.title.logDetail').d('日志详情'),
          drawer: true,
          style: {
            width: 800,
          },
          children: (
            <Form dataSet={this.applyDs}>
              <Output
                name="applyStatus"
                renderer={({ value, text }) => {
                  return (
                    <Badge status={value === 'APPLY_SUCCESS' ? 'success' : 'error'} text={text} />
                  );
                }}
              />
              <CodeArea
                disabled
                name="logContent"
                options={{ lineWrapping: true }}
                style={{ height: '100%' }}
              />
            </Form>
          ),
          footer: null,
        });
      })
      .catch(() => {
        this.setState({ loading: false });
      });
  }

  /**
   * 取消应用
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  @Bind()
  async handleUnApply(record) {
    this.setState({ loading: true });
    this.unApplyDs.setQueryParameter('customizeIds', [record.get('customizeId')]);
    await this.unApplyDs
      .query()
      .then(() => {
        this.setState({ loading: false });
        this.initDs.query();
        Modal.open({
          closable: true,
          key: 'historyDetail',
          title: intl.get('hpfm.apiIndividuation.view.title.logDetail').d('日志详情'),
          drawer: true,
          style: {
            width: 800,
          },
          children: (
            <Form dataSet={this.unApplyDs}>
              <Output
                name="applyStatus"
                renderer={({ value, text }) => {
                  return (
                    <Badge
                      status={value === 'CANCEL_APPLY_SUCCESS' ? 'success' : 'error'}
                      text={text}
                    />
                  );
                }}
              />
              <CodeArea
                disabled
                name="logContent"
                options={{ lineWrapping: true }}
                style={{ height: '100%' }}
              />
            </Form>
          ),
          footer: null,
        });
      })
      .catch(() => {
        this.setState({ loading: false });
      });
    this.initDs.query();
  }

  /**
   * 启用/禁用
   * @param {boolean} flag
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  @Bind()
  async handleEnable(flag, record) {
    if (flag) {
      this.enableDs.setQueryParameter('customizeIds', [record.get('customizeId')]);
      await this.enableDs.query();
    } else {
      this.disableDs.setQueryParameter('customizeIds', [record.get('customizeId')]);
      await this.disableDs.query();
    }
    this.initDs.query();
  }

  /**
   * 打开历史记录
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  @Bind()
  handleOpenHistory(record = {}) {
    const id = record.get('customizeId');
    Modal.open({
      closable: true,
      key: 'history',
      title: intl.get('hpfm.apiIndividuation.view.title.history').d('历史版本'),
      drawer: false,
      style: {
        width: 800,
      },
      children: <HistoryDrawer id={id} path={this.props.match.path} />,
      footer: null,
    });
  }

  /**
   * 打开应用日志
   * @param {object} [record={}]
   * @memberof apiIndividuation
   */
  @Bind()
  handleOpenLog(record = {}) {
    const id = record.get('customizeId');
    Modal.open({
      closable: true,
      key: 'history',
      title: intl.get('hpfm.apiIndividuation.view.title.log').d('应用日志'),
      drawer: false,
      style: {
        width: 800,
      },
      children: <LogDrawer id={id} path={this.props.match.path} />,
      footer: null,
    });
  }

  get columns() {
    return [
      {
        name: 'customizeCode',
      },
      {
        name: 'customizeName',
        width: 250,
      },
      {
        name: 'serviceName',
        width: 150,
      },
      {
        name: 'customizePosition',
        width: 100,
      },
      {
        name: 'unApplied',
        width: 100,
        renderer: ({ value }) =>
          !value
            ? intl.get('hzero.common.status.apply').d('应用')
            : intl.get('hzero.common.status.disapply').d('未应用'),
      },
      {
        name: 'syncFlag',
        width: 100,
        // renderer: ({ value }) => asyncRender(Number(value)),
      },
      {
        name: 'action',
        width: 240,
        lock: 'right',
        renderer: ({ record }) => {
          const operators = [];
          if (record.get('unApplied')) {
            operators.push({
              key: 'apply',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${this.props.match.path}.button.apply`,
                      type: 'button',
                      meaning: 'API个性化-应用',
                    },
                  ]}
                  onClick={() => this.handleApply(record)}
                >
                  {intl.get('hzero.common.button.apply').d('应用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.apply').d('应用'),
            });
          } else {
            operators.push({
              key: 'unApply',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${this.props.match.path}.button.unApply`,
                      type: 'button',
                      meaning: 'API个性化-应用',
                    },
                  ]}
                  onClick={() => this.handleUnApply(record)}
                >
                  {intl.get('hzero.common.button.unApply').d('取消应用')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hzero.common.button.unApply').d('取消应用'),
            });
          }
          operators.push({
            key: 'detail',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${this.props.match.path}.button.detail`,
                    type: 'button',
                    meaning: 'API个性化-详情',
                  },
                ]}
                onClick={() => this.handleDetail(record)}
              >
                {intl.get('hzero.common.button.detail').d('详情')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.detail').d('详情'),
          });
          if (record.get('unApplied')) {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${this.props.match.path}.button.edit`,
                      type: 'button',
                      meaning: 'API个性化-编辑',
                    },
                  ]}
                  onClick={() => this.handleUpdate(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          // if (record.get('enabledFlag')) {
          //   operators.push({
          //     key: 'edit',
          //     ele: (
          //       <ButtonPermission
          //         type="text"
          //         permissionList={[
          //           {
          //             code: `${this.props.match.path}.button.disable`,
          //             type: 'button',
          //             meaning: 'API个性化-禁用',
          //           },
          //         ]}
          //         onClick={() => this.handleEnable(false, record)}
          //       >
          //         {intl.get('hzero.common.button.disable').d('禁用')}
          //       </ButtonPermission>
          //     ),
          //     len: 2,
          //     title: intl.get('hzero.common.button.disable').d('禁用'),
          //   });
          // } else {
          //   operators.push({
          //     key: 'enable',
          //     ele: (
          //       <ButtonPermission
          //         type="text"
          //         permissionList={[
          //           {
          //             code: `${this.props.match.path}.button.enable`,
          //             type: 'button',
          //             meaning: 'API个性化-启用',
          //           },
          //         ]}
          //         onClick={() => this.handleEnable(true, record)}
          //       >
          //         {intl.get('hzero.common.button.enable').d('启用')}
          //       </ButtonPermission>
          //     ),
          //     len: 2,
          //     title: intl.get('hzero.common.button.enable').d('启用'),
          //   });
          // }
          // if (record.get('unApplied')) {
          //   operators.push({
          //     key: '删除',
          //     ele: (
          //       <ButtonPermission
          //         type="text"
          //         permissionList={[
          //           {
          //             code: `${this.props.match.path}.button.delete`,
          //             type: 'button',
          //             meaning: 'API个性化-删除',
          //           },
          //         ]}
          //         onClick={() => this.handleDelete(record)}
          //       >
          //         {intl.get('hzero.common.button.delete').d('删除')}
          //       </ButtonPermission>
          //     ),
          //     len: 2,
          //     title: intl.get('hzero.common.button.delete').d('删除'),
          //   });
          // }
          operators.push({
            key: 'history',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${this.props.match.path}.button.history`,
                    type: 'button',
                    meaning: 'API个性化-历史版本',
                  },
                ]}
                onClick={() => this.handleOpenHistory(record)}
              >
                {intl.get('hpfm.apiIndividuation.button.history').d('历史版本')}
              </ButtonPermission>
            ),
            len: 4,
            title: intl.get('hpfm.apiIndividuation.button.history').d('历史版本'),
          });
          operators.push({
            key: 'applyLog',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${this.props.match.path}.button.applyLog`,
                    type: 'button',
                    meaning: 'API个性化-应用日志',
                  },
                ]}
                onClick={() => this.handleOpenLog(record)}
              >
                {intl.get('hpfm.apiIndividuation.button.applyLog').d('应用日志')}
              </ButtonPermission>
            ),
            len: 4,
            title: intl.get('hpfm.apiIndividuation.button.applyLog').d('应用日志'),
          });
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ];
  }

  render() {
    const { location, match } = this.props;
    return (
      <>
        <Header title={intl.get('hpfm.apiIndividuation.view.message.title').d('API个性化')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: 'API个性化-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={this.handleCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Spin spinning={this.state.loading}>
            <Table
              // spin={{spinning: false}}
              dataSet={this.initDs}
              highLightRow={false}
              columns={this.columns}
              queryFieldsLimit={3}
            />
          </Spin>
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
