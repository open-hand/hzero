import React from 'react';
import { Dropdown, Form, Icon, Menu, Modal, notification, Tree } from 'hzero-ui';
import { isFunction, isEmpty } from 'lodash';
import { Bind, Debounce } from 'lodash-decorators';

import { Button } from 'components/Permission';

import intl from 'utils/intl';
import { getResponse, listenDownloadError } from 'utils/utils';

import {
  downloadFileByAxios,
  initiateAsyncExport,
  queryColumn,
  queryIdpValue,
} from '../../services/api';
import ExportPage from './ExportPage';
import HistoryData from './HistoryData';

// 监听导出错误时 postMessage 事件
listenDownloadError(
  'downloadError',
  intl.get('hzero.common.notification.export.error').d('导出异常')
);

// 监听导出错误时 postMessage 事件
listenDownloadError(
  'asyncRequestSuccess',
  intl.get('hzero.common.notification.export.async').d('异步导出任务已提交'),
  'success'
);

const { TreeNode } = Tree;

/**
 * 导出Excel ExcelExport
 *
 * @author wangjiacheng <jiacheng.wang@hand-china.com>
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {!string} [requestUrl=''] - 导出请求的url
 * @reactProps {?boolean} [defaultSelectAll=false] - 默认勾选所有列
 * @reactProps {?string} [method='GET'] - 导出请求的类型，默认GET请求
 * @reactProps {!object || function} [queryParams={}] - 表单查询参数，查询之后将会导出查询的数据, 可传递函数
 * @reactProps {?object} [queryFormItem={}] - 导出条件
 * @reactProps {?object} [otherButtonProps={}] - 导出按钮props
 * @reactProps {?array} [code = []] - 权限控制
 * @reactProps {?boolean} [exportAsync=false] - 是否有异步导出(租户层才会生效)
 * @returns React.element
 * @example
 * import ExcelExport from 'components/ExcelExport';
 *
 *   queryFormItem() {
 *    return (
 *      <React.Fragment>
 *        <FormItem label='事件编码'>
 *          {this.props.form.getFieldDecorator('eventCode', {
 *            initialValue: '',
 *          })(<Input style={{ width: '150px' }} />)}
 *        </FormItem>
 *        <FormItem label='事件描述'>
 *          {this.props.form.getFieldDecorator('eventDescription', {
 *            initialValue: '',
 *          })(<Input style={{ width: '150px' }} />)}
 *        </FormItem>
 *      </React.Fragment>
 *    );
 *   }
 *
 * <ExcelExport
 *  requestUrl={`${HZERO_PLATFORM} /v1/events/export`}
 *  queryParams={this.props.form.getFieldsValue()}
 *  queryFormItem={this.queryFormItem()}
 * />
 */
@Form.create({ fieldNameProp: null })
export default class ExcelExport extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      exportList: [],
      exportPending: false,
      fetchColumnLoading: true,
      enableAsync: false, // 允许异步
      // 树
      expandedKeys: [], // 展开的节点
      checkedKeys: [], // 选择的节点
      exportTypeList: [], // 导出类型值集
      // 异步数据
      historyModalVisible: false, // 异步数据模态框显示
      showAsync: true,
      defaultRequestMode: null,
    };
    if (props.onRef) {
      props.onRef(this);
    }
  }

  /**
   * @function queryColumnData - 查询可以选择导出的列数据
   */
  @Bind()
  queryColumnData(config) {
    const { requestUrl, method = 'GET', defaultSelectAll = false, form } = this.props;
    this.setState({ fetchColumnLoading: true });
    queryColumn({ requestUrl, method })
      .then((res) => {
        const response = getResponse(res);
        const nextState = {};
        if (response && response.children) {
          const defaultList = [];
          if (defaultSelectAll) {
            const exTreeKey = (data) => {
              if (data) {
                defaultList.push(data.id);
                if (Array.isArray(data.children)) {
                  for (let i = 0; i < data.children.length; i++) {
                    exTreeKey(data.children[i]);
                  }
                }
              }
            };
            exTreeKey(res);
          } else {
            const travelTree = (data) => {
              if (data) {
                // 后端返回的是 boolean 值
                if (data.checked) {
                  defaultList.push(data.id);
                }
                if (Array.isArray(data.children)) {
                  for (let i = 0; i < data.children.length; i++) {
                    travelTree(data.children[i]);
                  }
                }
              }
            };
            travelTree(res);
          }
          nextState.exportList = [res];
          nextState.expandedKeys = [`${res.id}`];
          nextState.checkedKeys = defaultList;
        }
        nextState.enableAsync = !!response?.enableAsync; // 不为真 就为假
        nextState.showAsync = isEmpty(response?.defaultRequestMode); // 不为真 就为假
        nextState.defaultRequestMode = response?.defaultRequestMode; // 不为真 就为假
        this.setState(nextState);
        if (!nextState.enableAsync) {
          if (form.getFieldValue('async') === 'true') {
            form.setFieldsValue({ async: 'false' });
          }
        }
      })
      .finally(() => {
        this.setState({ fetchColumnLoading: false });
        const { exportList } = this.state;
        if (!isEmpty(config)) {
          this.traversalTreeNodes(exportList);
          this.handleExport(config);
        }
      });
  }

  /**
   * @function showModal - 控制对话框是否可见
   * @param {boolean} flag - 对话框显示标识
   */
  showModal(flag) {
    const { defaultConfig = {} } = this.props;
    if (!isEmpty(defaultConfig)) {
      const { data, ...others } = defaultConfig;
      if (!isEmpty(data)) {
        this.traversalTreeNodes(defaultConfig.data);
        this.handleExport({ fillerType: 'single-sheet', ...others, async: 'false' });
      } else {
        this.queryColumnData({ fillerType: 'single-sheet', ...others, async: 'false' });
      }
    } else {
      this.setState({
        modalVisible: flag,
        checkedKeys: [],
      });
      if (flag) {
        queryIdpValue('HPFM.EXCEL_EXPORT_TYPE').then((res) => {
          const response = getResponse(res);
          if (response) {
            this.setState({
              exportTypeList: res,
            });
          }
        });
        this.queryColumnData();
      } else {
        this.setState({ checkedKeys: [] });
      }
    }
  }

  /**
   * @function handleExpand - 节点展开
   * @param {array} expandedKeys - 展开的节点组成的数组
   */
  @Bind()
  handleExpand(expandedKeys) {
    this.setState({
      expandedKeys,
    });
  }

  /**
   * @function handleSelect - 选择项变化监控
   * @param {array} checkedKeys - 选中项的 key 数组
   */
  @Bind()
  handleSelect(checkedKeys) {
    this.setState({ checkedKeys });
  }

  /**
   * @function handleExport - 导出，下载文件
   */
  @Bind()
  @Debounce(500)
  handleExport(config = {}) {
    const {
      requestUrl = '',
      queryParams = {},
      form,
      method = 'GET',
      fileName = '',
      allBody = false,
    } = this.props;
    let { queryData = {} } = this.props;
    const { checkedKeys, showAsync, defaultRequestMode } = this.state;
    let newQueryParams = queryParams;
    if (isFunction(queryData)) {
      queryData = queryData();
    }
    if (isFunction(queryParams)) {
      newQueryParams = queryParams();
    }
    if (method !== 'GET' && method !== 'get') {
      queryData = {
        ...queryData,
        ...form.getFieldsValue(),
        ...config,
      };
    } else {
      newQueryParams = {
        ...newQueryParams,
        ...form.getFieldsValue(),
        ...config,
      };
    }

    if (!showAsync) {
      newQueryParams.async = defaultRequestMode === 'ASYNC';
    }
    if (!checkedKeys || (Array.isArray(checkedKeys) && checkedKeys.length === 0)) {
      Modal.warning({
        title: intl.get('hzero.common.message.validation.atLeast').d('请至少选择一条数据'),
      });
    } else {
      let params = [];
      this.setState({ exportPending: true });
      if (method !== 'GET' && method !== 'get') {
        queryData.ids = checkedKeys;
        queryData.exportType = 'DATA';
      } else {
        params = checkedKeys.map((item) => ({ name: 'ids', value: item }));
        params.push({ name: 'exportType', value: 'DATA' });
      }
      if (allBody) {
        let customData;
        try {
          customData = JSON.stringify(newQueryParams);
        } catch (e) {
          console.error(e);
        }
        queryData.customData = customData;
      } else {
        for (const key of Object.keys(newQueryParams)) {
          if (newQueryParams[key] !== undefined) {
            params.push({ name: key, value: newQueryParams[key] });
          }
        }
      }
      // 添加导出Excel参数
      if (newQueryParams.async === 'true') {
        initiateAsyncExport({ requestUrl, queryParams: params, method, queryData }).then((res) => {
          if (res) {
            notification.success({
              message: intl
                .get('hzero.common.notification.export.asyncWithUid', { uuid: res.uuid })
                .d(`异步导出任务已提交${res.uuid}`),
            });
          }
          this.setState({ exportPending: false });
        });
      } else {
        downloadFileByAxios({ requestUrl, queryParams: params, method, queryData }, fileName)
          .catch((err) => {
            if (err && getResponse(err) && err.uuid) {
              notification.success({
                message: intl
                  .get('hzero.common.notification.export.asyncWithUid', { uuid: err.uuid })
                  .d(`异步导出任务已提交${err.uuid}`),
              });
            }
          })
          .then((res) => {
            if (res) {
              if (!isEmpty(config)) {
                this.setState({ checkedKeys: [] });
              }
            }
          })
          .finally(() => {
            this.setState({ exportPending: false });
          });
      }
    }
  }

  /**
   * @function traversalTreeNodes - 遍历树的子节点
   * @param {object} data - 列数据
   */
  @Bind()
  traversalTreeNodes(data = [], arr = []) {
    const { checkedKeys } = this.state;
    const idList = arr;
    data.map((item) => {
      const temp = item;
      checkedKeys.push(temp.id);
      this.setState({ checkedKeys });
      if (temp.children) {
        this.traversalTreeNodes(temp.children, idList);
      }
      return temp;
    });
  }

  /**
   * @function renderTreeNodes - 渲染树的子节点
   * @param {object} data - 列数据
   */
  @Bind()
  renderTreeNodes(data) {
    return data.map((item) => {
      if (item.children) {
        return (
          <TreeNode title={item.title} key={item.id} dataRef={item}>
            {this.renderTreeNodes(item.children)}
          </TreeNode>
        );
      }
      return <TreeNode {...item} key={item.id} />;
    });
  }

  @Bind()
  renderQueryForm() {
    const { queryFormItem } = this.props;
    return <Form layout="inline">{queryFormItem}</Form>;
  }

  @Bind()
  handleMenuClick({ key }) {
    if (key === 'async-data') {
      this.showHistoryModal();
    } else if (key === 'export') {
      this.showModal(true);
    }
  }

  @Bind()
  showSyncExportModal() {
    this.showModal(true);
  }

  @Bind()
  showHistoryModal() {
    this.setState({
      historyModalVisible: true,
    });
  }

  @Bind()
  hideHistoryModal() {
    this.setState({
      historyModalVisible: false,
    });
  }

  render() {
    const {
      code,
      queryFormItem,
      otherButtonProps,
      buttonText = intl.get('hzero.common.button.export').d('导出'),
      title = intl.get(`hzero.common.components.export`).d('导出Excel'),
      form,
      exportAsync = false,
      defaultConfig,
    } = this.props;
    const {
      exportTypeList,
      exportList,
      fetchColumnLoading,
      checkedKeys,
      expandedKeys,
      enableAsync,
      exportPending,
      modalVisible,
      historyModalVisible,
      queryHistoryLoading,
      showAsync,
      defaultRequestMode,
    } = this.state;
    const modalProps = {
      title,
      destroyOnClose: true,
      bodyStyle: { height: '460px', overflowY: 'scroll' },
      width: '600px',
      visible: modalVisible,
      onCancel: this.showModal.bind(this, false),
      onOk: () => {
        this.handleExport();
      },
      confirmLoading: exportPending,
    };
    const buttonProps = {
      code,
      icon: 'export',
      type: 'default',
      ...otherButtonProps,
      onClick: () => {},
    };
    const formItemLayout = {
      labelCol: {
        span: 12,
      },
      wrapperCol: {
        span: 12,
      },
    };
    const historyModalProps = {
      title: intl.get('hzero.common.excelExport.view.title.asyncData').d('异步数据'),
      visible: historyModalVisible,
      destroyOnClose: true,
      width: '1000px',
      onCancel: this.hideHistoryModal,
      onOk: this.hideHistoryModal,
      confirmLoading: queryHistoryLoading,
      wrapClassName: 'ant-modal-sidebar-right',
      transitionName: 'move-right',
    };
    // 导出组件入口: 如果设置了 异步且为租户级, 那么显示异步数据(Dropdown),否则显示导出按钮
    const exportEntryElement =
      exportAsync && isEmpty(defaultConfig) ? (
        <Dropdown
          overlay={
            <Menu onClick={this.handleMenuClick}>
              <Menu.Item key="export">{intl.get('hzero.common.button.export').d('导出')}</Menu.Item>
              <Menu.Item key="async-data">
                {intl.get('hzero.common.excelExport.asyncDataView').d('异步数据查看')}
              </Menu.Item>
            </Menu>
          }
        >
          <Button {...buttonProps}>
            {buttonText}
            <Icon type="down" />
          </Button>
        </Dropdown>
      ) : (
        <Button {...buttonProps} onClick={this.showSyncExportModal} loading={exportPending}>
          {buttonText}
        </Button>
      );
    return (
      <>
        {exportEntryElement}
        <Modal {...modalProps}>
          <ExportPage
            exportTypeList={exportTypeList}
            exportList={exportList}
            fetchColumnLoading={fetchColumnLoading}
            formItemLayout={formItemLayout}
            queryFormItem={queryFormItem}
            form={form}
            checkedKeys={checkedKeys}
            expandedKeys={expandedKeys}
            renderQueryForm={this.renderQueryForm}
            renderTreeNodes={this.renderTreeNodes}
            onExpand={this.handleExpand}
            onSelect={this.handleSelect}
            enableAsync={enableAsync}
            exportAsync={exportAsync}
            showAsync={showAsync}
            defaultRequestMode={defaultRequestMode}
          />
        </Modal>
        <Modal {...historyModalProps}>
          <HistoryData />
        </Modal>
      </>
    );
  }
}
