/**
 * TestCase - 测试用例
 * @date: 2019/6/6
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import React, { Component } from 'react';
import { Button, Popconfirm, Icon, Form, Input, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { groupBy, isEmpty } from 'lodash';
import { connect } from 'dva';
import uuidv4 from 'uuid/v4';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { getEditTableData, tableScrollWidth } from 'utils/utils';
import { operatorRender } from 'utils/renderer';
import HistoryModal from './HistoryModal';
import ViewCodeModal from './ViewCodeModal';
import EditModal from './Detail/EditModal';
import styles from './index.less';

/**
 * 测试用例
 * @extends {Component} - React.Component
 * @reactProps {string} interfaceId - 接口ID
 * @reactProps {object} services - 数据源
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @reactProps {Function} onSearch - 列表查询
 * @return React.element
 */
@connect(({ services, loading }) => ({
  services,
  listLoading:
    loading.effects['services/queryTestCase'] ||
    loading.effects['services/deleteTestCase'] ||
    loading.effects['services/createTestCase'],
  detailLoading:
    loading.effects['services/queryTestCaseDetail'] ||
    loading.effects['services/deleteTestCaseParams'] ||
    loading.effects['services/updateTestCaseParams'],
  createLoading: loading.effects['services/createTestCase'],
  historyLoading:
    loading.effects['services/queryTestCaseHistory'] ||
    loading.effects['services/deleteTestHistory'],
  viewCodeLoading: loading.effects['services/queryViewCode'],
  updateParamsLoading:
    loading.effects['services/queryTestCaseDetail'] ||
    loading.effects['services/updateTestCaseParams'],
}))
export default class TestCase extends Component {
  state = {
    currentUsecaseId: '',
    isDetailVisible: false,
    isHistoryVisible: false,
    isViewVisible: false,
    isExecuting: false, // 测试用例执行标志
  };

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'services/updateState',
      payload: {
        testCaseList: {}, // 测试用例列表数据
        testCaseDetail: {}, // 测试用例详情
        testCaseParams: [],
        testCaseHistory: {}, // 测试历史
        paramsWithValues: {},
      },
    });
  }

  /**
   * 创建测试用例
   */
  @Bind()
  handleCreateTestCase() {
    const { dispatch, interfaceId, onSearch } = this.props;
    dispatch({
      type: 'services/createTestCase',
      payload: interfaceId,
    }).then((res) => {
      if (res) {
        notification.success();
        onSearch();
      }
    });
  }

  /**
   * 开启测试历史弹窗
   * @param {number} usecaseId - 测试用例ID
   */
  @Bind()
  handleOpenHistoryModal(usecaseId) {
    this.setState(
      {
        isHistoryVisible: true,
        currentUsecaseId: usecaseId,
      },
      () => {
        this.viewTestCaseHistory();
      }
    );
  }

  /**
   * 关闭测试历史弹窗
   */
  @Bind()
  handleCloseHistoryModal() {
    const { dispatch } = this.props;
    this.setState({ isHistoryVisible: false });
    dispatch({
      type: 'services/updateState',
      payload: {
        testCaseHistory: {}, // 测试历史
      },
    });
  }

  /**
   * 查询测试历史
   * @param {number} usecaseId - 测试用例ID
   */
  @Bind()
  viewTestCaseHistory(params = {}) {
    const { currentUsecaseId } = this.state;
    const { dispatch, interfaceId } = this.props;
    dispatch({
      type: 'services/queryTestCaseHistory',
      payload: {
        page: params,
        interfaceId,
        usecaseId: currentUsecaseId,
      },
    });
  }

  /**
   * 删除测试历史
   * @param {number} interfaceLogId 测试历史数据ID
   */
  @Bind()
  handleDeleteHistory(interfaceLogId) {
    const { dispatch, interfaceId } = this.props;
    dispatch({
      type: 'services/deleteTestHistory',
      payload: { interfaceId, interfaceLogId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.viewTestCaseHistory();
      }
    });
  }

  /**
   * 测试历史分页变换时
   * @param {object} page - 分页信息
   */
  @Bind()
  handleHistoryPageChange(page) {
    this.viewTestCaseHistory(page);
  }

  /**
   * 执行测试用例
   * @param {number} usecaseId - 测试用例ID
   */
  @Bind()
  handleExecuteTestCase(usecaseId) {
    const { dispatch, interfaceId } = this.props;
    this.setState({
      isExecuting: true,
      currentUsecaseId: usecaseId,
    });
    dispatch({
      type: 'services/executeTestCase',
      payload: {
        interfaceId,
        usecaseId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
      }
      this.setState({ isExecuting: false });
    });
  }

  /**
   * 开启测试用例详情弹窗
   * @param {number} usecaseId - 测试用例ID
   */
  @Bind()
  handleOpenDetailModal(usecaseId) {
    this.queryParamsAndAlternative();
    this.setState(
      {
        isDetailVisible: true,
        currentUsecaseId: usecaseId,
      },
      () => {
        this.viewDetail();
      }
    );
  }

  /**
   * 查询文档的所有参数信息及参数备选值
   */
  @Bind()
  queryParamsAndAlternative() {
    const {
      dispatch,
      services: { documentData },
    } = this.props;
    dispatch({
      type: 'services/queryParamsAndAlternative',
      payload: documentData.documentId,
    });
  }

  /**
   * 查看用例详情
   */
  @Bind()
  viewDetail() {
    const { dispatch, interfaceId } = this.props;
    const { currentUsecaseId } = this.state;
    dispatch({
      type: 'services/queryTestCaseDetail',
      payload: {
        interfaceId,
        usecaseId: currentUsecaseId,
      },
    });
  }

  /**
   * 新建参数
   * @param {string} type - 参数类型
   */
  @Bind()
  handleCreateParams(type) {
    const {
      dispatch,
      services: { testCaseParams },
    } = this.props;
    let newList = [
      {
        paramType: type,
        parameterName: '',
        parameterValue: '',
        paramValueType: 'STRING',
        interfaceUsecaseParamId: uuidv4(),
        _status: 'create',
      },
    ];
    if (testCaseParams) {
      newList = [...newList, ...testCaseParams];
    }
    dispatch({
      type: 'services/updateState',
      payload: {
        testCaseParams: [...newList],
      },
    });
  }

  /**
   * 清除新增行数据
   * @param {Objec} record - 待清除的数据对象
   */
  @Bind()
  handleCleanLine(record) {
    const {
      dispatch,
      services: { testCaseParams },
    } = this.props;
    const newList = testCaseParams.filter(
      (item) => item.interfaceUsecaseParamId !== record.interfaceUsecaseParamId
    );
    dispatch({
      type: 'services/updateState',
      payload: {
        testCaseParams: [...newList],
      },
    });
  }

  /**
   * 编辑参数
   * @param {Object} record - 行数据
   * @param {Boolean} flag - 编辑/取消标记
   */
  @Bind()
  handleEditLine(record, flag) {
    const {
      dispatch,
      services: { testCaseParams },
    } = this.props;
    const newList = testCaseParams.map((item) => {
      let tempItem = { ...item };
      if (item.interfaceUsecaseParamId === record.interfaceUsecaseParamId) {
        tempItem = { ...item, _status: flag ? 'update' : '' };
      } else if (item.children && item.children.length) {
        // eslint-disable-next-line array-callback-return
        tempItem.children.map((child) => {
          if (child.interfaceUsecaseParamId === record.interfaceUsecaseParamId) {
            // eslint-disable-next-line no-param-reassign
            child._status = flag ? 'update' : '';
          }
        });
      }
      return tempItem;
    });
    dispatch({
      type: 'services/updateState',
      payload: {
        testCaseParams: [...newList],
      },
    });
  }

  /**
   * 保存参数
   */
  @Bind()
  handleSaveParams(formValues) {
    if (isEmpty(formValues)) {
      return;
    }
    const { currentUsecaseId } = this.state;
    const {
      dispatch,
      services: { testCaseParams, testCaseDetail },
      interfaceId,
      onSearch,
    } = this.props;
    let testCaseParamsList = getEditTableData(testCaseParams, ['_status']);
    if (
      !isEmpty(formValues) ||
      (Array.isArray(testCaseParamsList) && testCaseParamsList.length !== 0)
    ) {
      if (!isEmpty(testCaseParamsList)) {
        testCaseParamsList = testCaseParamsList.map((item) => {
          const tempItem = { ...item };
          if (tempItem.parameterName.includes('_@_')) {
            // eslint-disable-next-line prefer-destructuring
            tempItem.parameterName = item.parameterName.split('_@_')[1];
          }
          if ('converter' in tempItem) {
            delete tempItem.converter;
          }
          if (!('paramNodeId' in tempItem)) {
            delete tempItem.interfaceUsecaseParamId;
          }
          return { ...tempItem, paramValueType: 'STRING' };
        });
      }
      let rawColumn = {};
      if (testCaseDetail.reqRawFlag === 1 && this.editModal) {
        rawColumn = this.editModal.getRawValue();
      }
      if (testCaseDetail.reqRawFlag === 1 && !isEmpty(rawColumn)) {
        testCaseParamsList.push(rawColumn);
      }
      dispatch({
        type: 'services/updateTestCaseParams',
        payload: {
          ...formValues,
          interfaceUsecaseParamList: testCaseParamsList,
          interfaceId,
          interfaceUsecaseId: currentUsecaseId,
          objectVersionNumber: testCaseDetail.objectVersionNumber,
          _token: testCaseDetail._token,
        },
      }).then((res) => {
        if (res) {
          notification.success();
          this.viewDetail();
          onSearch();
        }
      });
    }
  }

  /**
   * 关闭测试用例详情弹窗
   */
  @Bind()
  handleCloseDetailModal() {
    this.setState({ isDetailVisible: false });
  }

  /**
   * 删除用例参数
   */
  @Bind()
  handleDeleteParams(record) {
    const { currentUsecaseId } = this.state;
    const { dispatch, interfaceId } = this.props;
    dispatch({
      type: 'services/deleteTestCaseParams',
      payload: {
        interfaceId,
        usecaseId: currentUsecaseId,
        paramId: record.interfaceUsecaseParamId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.viewDetail();
      }
    });
  }

  /**
   * 删除测试用例
   * @param {number} usecaseId - 测试用例ID
   */
  @Bind()
  handleDeleteTestCase(interfaceUsecaseId) {
    const { dispatch, onSearch } = this.props;
    dispatch({
      type: 'services/deleteTestCase',
      payload: interfaceUsecaseId,
    }).then((res) => {
      if (res) {
        notification.success();
        onSearch();
      }
    });
  }

  /**
   * 打开代码预览模态框
   * @param {number} usecaseId 测试用例ID
   */
  @Bind()
  openViewModal(usecaseId) {
    this.setState(
      {
        currentUsecaseId: usecaseId,
        isViewVisible: true,
      },
      () => {
        this.viewCode();
      }
    );
  }

  /**
   * 关闭代码预览模态框
   */
  @Bind()
  closeViewModal() {
    const { dispatch } = this.props;
    this.setState(
      {
        isViewVisible: false,
      },
      () => {
        dispatch({
          type: 'services/updateState',
          payload: {
            viewCode: '',
          },
        });
      }
    );
  }

  /**
   * 查询预览代码
   * @param {number} usecaseId 测试用例ID
   */
  @Bind()
  viewCode(type = 'JAVA') {
    const { dispatch, interfaceId } = this.props;
    const { currentUsecaseId } = this.state;
    dispatch({
      type: 'services/queryViewCode',
      payload: { interfaceId, usecaseId: currentUsecaseId, type },
    });
  }

  render() {
    const {
      currentUsecaseId,
      isHistoryVisible,
      isExecuting,
      isDetailVisible,
      isViewVisible,
    } = this.state;
    const {
      services: {
        documentData,
        testCaseList,
        testCaseHistory,
        viewCode,
        testCaseParams = {},
        testCaseDetail = {},
        paramsWithValues = {},
        enumMap: { codeTypes, mimeTypes, usecaseTypes },
      },
      interfaceId,
      listLoading,
      createLoading,
      historyLoading,
      detailLoading = false,
      viewCodeLoading = false,
      updateParamsLoading,
      onSearch,
    } = this.props;
    let paramsData = {};
    if (!isEmpty(testCaseParams)) {
      paramsData = groupBy(testCaseParams, 'paramType');
    }
    const columns = [
      {
        title: intl.get('hitf.services.model.services.usecaseName').d('用例名称'),
        dataIndex: 'usecaseName',
      },
      {
        title: intl.get('hitf.services.model.services.usecaseType').d('用例类型'),
        dataIndex: 'usecaseTypeMeaning',
        width: 200,
      },
      {
        title: intl.get('hitf.services.model.services.remark').d('说明'),
        dataIndex: 'remark',
        width: 250,
        render: (val, record) =>
          record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('remark', {
                initialValue: val,
              })(<Input />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'action',
        width: 330,
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.handleOpenDetailModal(record.interfaceUsecaseId)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (currentUsecaseId === record.interfaceUsecaseId && isExecuting) {
            operators.push({
              key: 'loading',
              ele: <Icon type="loading" />,
              len: 2,
            });
          } else {
            operators.push({
              key: 'execute',
              ele: (
                <a onClick={() => this.handleExecuteTestCase(record.interfaceUsecaseId)}>
                  {intl.get('hitf.services.view.button.execute').d('执行')}
                </a>
              ),
              len: 2,
              title: intl.get('hitf.services.view.button.execute').d('执行'),
            });
          }
          operators.push(
            {
              key: 'history',
              ele: (
                <a onClick={() => this.handleOpenHistoryModal(record.interfaceUsecaseId)}>
                  {intl.get('hitf.services.view.button.test.history').d('查看测试历史')}
                </a>
              ),
              len: 6,
              title: intl.get('hitf.services.view.button.test.history').d('查看测试历史'),
            },
            {
              key: 'code',
              ele: (
                <a onClick={() => this.openViewModal(record.interfaceUsecaseId)}>
                  {intl.get('hitf.services.view.button.view.code').d('代码预览')}
                </a>
              ),
              len: 4,
              title: intl.get('hitf.services.view.button.view.code').d('代码预览'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  placement="topRight"
                  onConfirm={() => this.handleDeleteTestCase(record.interfaceUsecaseId)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators, record, { limit: 5 });
        },
      },
    ];

    const historyProps = {
      visible: isHistoryVisible,
      loading: historyLoading,
      testCaseHistory,
      onChange: this.handleHistoryPageChange,
      onDelete: this.handleDeleteHistory,
      onCancel: this.handleCloseHistoryModal,
    };
    const viewCodeProps = {
      visible: isViewVisible,
      loading: viewCodeLoading,
      codeTypes,
      code: viewCode,
      onCancel: this.closeViewModal,
      onChangeType: this.viewCode,
    };
    const editProps = {
      visible: isDetailVisible,
      loading: detailLoading,
      confirmLoading: updateParamsLoading,
      paramsWithValues,
      testCaseDetail,
      testCaseParams: paramsData,
      interfaceId,
      mimeTypes,
      usecaseTypes,
      documentId: documentData && documentData.documentId,
      onCancel: this.handleCloseDetailModal,
      onDelete: this.handleDeleteParams,
      onEditLine: this.handleEditLine,
      onSave: this.handleSaveParams,
      onCreate: this.handleCreateParams,
      onCleanLine: this.handleCleanLine,
      ref: (node) => {
        this.editModal = node;
      },
    };
    return (
      <>
        <div className="table-list-search" style={{ textAlign: 'right' }}>
          <Button
            type="primary"
            style={{ marginTop: '16px' }}
            onClick={this.handleCreateTestCase}
            loading={createLoading}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </div>
        <div className={styles['hitf-testcase-table']}>
          <Table
            rowKey="interfaceUsecaseId"
            bordered
            loading={listLoading}
            columns={columns}
            dataSource={testCaseList.list}
            pagination={testCaseList.pagination}
            scroll={{ x: tableScrollWidth(columns) }}
            onChange={(page) => onSearch(page)}
          />
        </div>
        <HistoryModal {...historyProps} />
        <ViewCodeModal {...viewCodeProps} />
        <EditModal {...editProps} />
      </>
    );
  }
}
