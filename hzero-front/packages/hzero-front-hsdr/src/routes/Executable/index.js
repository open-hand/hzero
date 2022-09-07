/**
 * Concurrent - 并发管理器/可执行定义
 * @date: 2018-9-7
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Popconfirm, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';
import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { enableRender, operatorRender } from 'utils/renderer';
import { getCurrentOrganizationId, tableScrollWidth } from 'utils/utils';

import Drawer from './Drawer';
import SearchForm from './SearchForm';

@connect(({ loading, executable }) => ({
  executable,
  saving:
    loading.effects['executable/updateExecutable'] ||
    loading.effects['executable/createExecutable'],
  fetching: loading.effects['executable/fetchExecutable'],
  detailLoading: loading.effects['executable/fetchExecutableDetail'],
  configLoading: loading.effects['executable/executorConfig'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hsdr.executable', 'entity.tenant'] })
export default class Executable extends React.PureComponent {
  Drawer;

  form; // SearchForm 中的 表单

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'executable/init',
    });
    this.handleSearch();
  }

  /**
   *
   * @param ref
   */
  @Bind()
  searchFormRef(ref) {
    this.form = ref;
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const { form } = (this.form || {}).props || {};
    if (form) {
      form.validateFields((err, values) => {
        const fieldValues = values || {};
        if (!err) {
          dispatch({
            type: 'executable/fetchExecutable',
            payload: {
              page: isEmpty(fields) ? {} : fields,
              ...fieldValues,
            },
          });
        }
      });
    } else {
      dispatch({
        type: 'executable/fetchExecutable',
        payload: {
          page: isEmpty(fields) ? {} : fields,
        },
      });
    }
  }

  /**
   * @function handleModalVisible - 控制modal显示与隐藏
   * @param {boolean} flag 是否显示modal
   */
  @Bind()
  handleModalVisible(flag) {
    const { dispatch } = this.props;
    dispatch({
      type: 'executable/updateState',
      payload: {
        modalVisible: !!flag,
      },
    });
    // 清空执行器配置列表
    if (!flag) {
      dispatch({
        type: 'executable/updateState',
        payload: {
          executorConfigList: [],
        },
      });
    }
  }

  /**
   * 获取执行器列表
   * @param {object} data 选择的执行器策略数据
   */
  @Bind()
  handleConfig(data) {
    const { dispatch } = this.props;
    if (data) {
      dispatch({
        type: 'executable/executorConfigByExecutable',
        payload: {
          executableId: data.executableId,
          executorId: data.executorId,
        },
      }).then(res => {
        if (res) {
          const arr = [];
          Object.keys(res).forEach(item => {
            arr.push({
              address: item,
              weight: res[item],
            });
          });
          dispatch({
            type: 'executable/updateState',
            payload: {
              executorConfigList: arr,
            },
          });
        }
      });
    } else {
      dispatch({
        type: 'executable/updateState',
        payload: { executorConfigList: [] },
      });
    }
  }

  /**
   * @function showCreateModal - 显示新增modal
   */
  @Bind()
  showCreateModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'executable/updateState',
      payload: {
        executableDetail: {},
      },
    });
    this.handleModalVisible(true);
  }

  /**
   * @function handleAdd - 新增或编辑可执行数据
   * @param {Object} fieldsValue - 编辑的数据
   */
  @Bind()
  handleAdd(fieldsValue) {
    const {
      dispatch,
      executable: { pagination, executableDetail },
      tenantId,
    } = this.props;
    const { executableId, _token, objectVersionNumber } = executableDetail;
    const { strategyParam, ...others } = fieldsValue;
    dispatch({
      type: `executable/${executableId ? 'updateExecutable' : 'createExecutable'}`,
      payload: {
        _token,
        objectVersionNumber,
        strategyParam: JSON.stringify(strategyParam),
        ...others,
        executableId,
        tenantId,
      },
    }).then(res => {
      if (res) {
        notification.success();
        this.handleModalVisible(false);
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * @function handleUpdateEmail - 编辑
   * @param {object} record - 行数据
   */
  @Bind()
  handleUpdate(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'executable/fetchExecutableDetail',
      payload: {
        executableId: record.executableId,
      },
    }).then(res => {
      if (res && res.executorStrategy === 'JOB_WEIGHT') {
        this.handleConfig(res);
      }
    });
    this.handleModalVisible(true);
  }

  /**
   * 数据列表，删除
   * @param {obejct} record - 操作对象
   */
  @Bind()
  handleDeleteContent(record) {
    const {
      dispatch,
      executable: { pagination },
    } = this.props;
    dispatch({
      type: 'executable/deleteHeader',
      payload: { ...record },
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  render() {
    const {
      tenantId,
      match: { path },
      executable,
      saving,
      configLoading,
      fetching,
      detailLoading = false,
    } = this.props;
    const {
      pagination,
      modalVisible,
      executableDetail,
      executableList = [],
      executorConfigList = [],
    } = executable;
    const columns = [
      {
        title: intl.get('hsdr.executable.model.executable.executableCode').d('可执行编码'),
        width: 150,
        dataIndex: 'executableCode',
      },
      {
        title: intl.get('hsdr.executable.model.executable.executableName').d('可执行名称'),
        dataIndex: 'executableName',
      },
      {
        title: intl.get('hsdr.executable.model.executable.exeTypeCode').d('可执行类型'),
        width: 200,
        dataIndex: 'exeTypeMeaning',
      },
      {
        title: 'JobHandler',
        width: 150,
        dataIndex: 'jobHandler',
      },
      {
        title: intl.get('hsdr.executable.model.executable.failStrategy').d('失败处理策略'),
        width: 120,
        dataIndex: 'failStrategyMeaning',
      },
      {
        title: intl.get('hsdr.executable.model.executable.groupId').d('执行器'),
        width: 200,
        dataIndex: 'executorName',
      },
      {
        title: intl.get('hsdr.executable.model.executable.executorStrategy').d('执行器策略'),
        width: 100,
        dataIndex: 'executorStrategyMeaning',
      },
      {
        title: intl.get('hsdr.executable.model.executable.executableDesc').d('可执行描述'),
        dataIndex: 'executableDesc',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
        fixed: 'right',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 110,
        dataIndex: 'edit',
        fixed: 'right',
        render: (text, record) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '可执行定义-编辑',
                    },
                  ]}
                  onClick={() => this.handleUpdate(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  placement="topRight"
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => this.handleDeleteContent(record)}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.delete`,
                        type: 'button',
                        meaning: '可执行定义-删除',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ];
    const title = executableDetail.executableId
      ? intl.get('hzero.common.button.edit').d('编辑')
      : intl.get('hzero.common.button.create').d('新建');

    const drawerProps = {
      tenantId,
      title,
      executable,
      configLoading,
      detailLoading,
      executorConfigList,
      loading: saving,
      visible: modalVisible,
      initData: executableDetail,
      onConfig: this.handleConfig,
      onCancel: () => this.handleModalVisible(false),
      onOk: this.handleAdd,
    };
    return (
      <>
        <Header title={intl.get('hsdr.executable.view.message.title.list').d('可执行定义')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '可执行定义-新建',
              },
            ]}
            onClick={this.showCreateModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <SearchForm onSearch={this.handleSearch} onRef={this.searchFormRef} />
          <Table
            bordered
            rowKey="executableId"
            loading={fetching}
            dataSource={executableList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handleSearch}
          />
          <Drawer {...drawerProps} />
        </Content>
      </>
    );
  }
}
