/**
 * index.js
 * @date 2018/10/24
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

// 带数据源的 Table

import React from 'react';
import { Table } from 'hzero-ui';
import { forEach, isEmpty, isFunction, map, omit, set, split } from 'lodash';
import { Bind } from 'lodash-decorators';
import { withRouter } from 'dva/router';
import querystring from 'querystring';

import intl from 'utils/intl';
import { createPagination, getResponse, parseParameters } from 'utils/utils';
import notification from 'utils/notification';
import request from 'utils/request';
import { operatorRender } from 'utils/renderer';

import { actionCode, openPageModalBodyStyle, openTypeCode, paramTypeCode } from './utils';
import {
  dealObjectProps,
  getContextValue,
  getDisplayValue,
  getWidthFromWord,
  preProcessComponentProps,
  get,
} from '../utils';

import { dynamicTableOmitProps, fieldLabelProp, fieldNameProp, subEventSep } from '../config';

async function fetchGet({ url, query, body }) {
  return request(url, {
    method: 'GET',
    query,
    body,
  });
}

async function fetchDelete({ url, query, body }) {
  return request(url, {
    method: 'DELETE',
    query,
    body,
  });
}
const linkButtonStyle = { marginRight: 8 };

@withRouter
export default class DynamicTable extends React.Component {
  static defaultProps = {};

  static propTypes = {};

  pageModalRef;

  @Bind()
  handlePageModalRef(pageModalRef) {
    this.pageModalRef = pageModalRef;
  }

  constructor(props) {
    super(props);

    // 初始化state
    this.state = {
      queryParams: {},
      pagingDataSource: {},
      ds: !!props.queryUrl, // 是不是自带数据源的Table(queryUrl)
      pageModalProps: {}, // 页面模态框
    };
  }

  static getDerivedStateFromProps(nextProps /* , prevState */) {
    // 根据属性来判断是不是 queryUrl(自带数据源的Table), 或者由父组件控制的 Table
    const { queryUrl } = nextProps;
    const nextState = {};
    nextState.ds = !!queryUrl;
    return nextState;
  }

  render() {
    const {
      batchRemoveUrl, // 是否有批量删除的URL
      rowKey,
      rowSelection: userRowSelection,
      pagination: { showTotal = true, showSizeChanger = true, pageSizeOptions } = {},
      pagination: userPagination,
      scroll = {},
      dataSource: userDataSource = {},
      loading = false,
    } = this.props;
    const {
      selectedRowKeys = [],
      pagingDataSource = {},
      ds,
      pageModalProps,
      queryLoading,
      lineRemoveLoading,
    } = this.state;
    const tableLoading = queryLoading || loading || lineRemoveLoading;
    const otherProps = omit(this.props, dynamicTableOmitProps);
    const rowSelection = batchRemoveUrl
      ? {
          ...userRowSelection,
          selectedRowKeys,
          onChange: this.handleRowSelectionChange,
        }
      : undefined;
    let pagination;
    let dataSource;
    if (ds) {
      dataSource = (pagingDataSource && pagingDataSource.content) || [];
      pagination = userPagination === false ? false : createPagination(pagingDataSource) || false;
      if (pagination) {
        if (!showTotal) {
          delete pagination.showTotal;
        }
        if (showSizeChanger) {
          if (!isEmpty(pageSizeOptions)) {
            pagination.pageSizeOptions = pageSizeOptions;
          }
        } else {
          delete pagination.showSizeChanger;
        }
      }
    } else {
      dataSource = (userDataSource && userDataSource.content) || [];
      pagination = userPagination === false ? false : createPagination(userDataSource) || false;
      if (pagination) {
        if (!showTotal) {
          delete pagination.showTotal;
        }
        if (showSizeChanger) {
          if (!isEmpty(pageSizeOptions)) {
            pagination.pageSizeOptions = pageSizeOptions;
          }
        } else {
          delete pagination.showSizeChanger;
        }
      }
    }

    const { columns, x } = this.getColumnsAndScrollX();
    scroll.x = x;
    const DynamicModal = get('DynamicModal');
    return (
      <>
        <Table
          key="dataSetTable"
          {...otherProps}
          rowSelection={rowSelection}
          pagination={pagination}
          dataSource={dataSource}
          onChange={this.handleTableChange}
          columns={columns}
          scroll={scroll}
          rowKey={rowKey}
          loading={tableLoading}
          bordered
        />
        <DynamicModal {...pageModalProps} key="pageModal" onRef={this.handlePageModalRef} />
      </>
    );
  }

  componentDidMount() {
    const { onRef, defaultPageSize = 10 } = this.props;
    if (isFunction(onRef)) {
      onRef(this);
    }
    this.query({ page: 0, size: defaultPageSize });
  }

  @Bind()
  showLoading(loading) {
    this.setState({
      [loading]: true,
    });
  }

  @Bind()
  hiddenLoading(loading) {
    this.setState({
      [loading]: true,
    });
  }

  @Bind()
  handleAddBtnClick() {
    const { onAdd } = this.props;
    if (isFunction(onAdd)) {
      onAdd();
    }
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows = []) {
    const { rowKey, rowSelection: { onChange } = {} } = this.props;
    const selectedRowKeys = map(selectedRows, record => record[rowKey]);
    this.setState({
      selectedRowKeys,
      selectedRows,
    });
    if (isFunction(onChange)) {
      onChange(selectedRowKeys, selectedRows);
    }
  }

  @Bind()
  handleTableChange(pagination, filter, sorter) {
    const { ds } = this.state;
    const paginationParams = { page: pagination, sort: sorter };
    if (ds) {
      return this.query(paginationParams);
    }
    return Promise.reject();
  }

  @Bind()
  reload() {
    const { queryParams, ds } = this.state;
    if (ds) {
      return this.query(queryParams);
    }
    return Promise.reject();
  }

  /**
   * @export 给页面其他组件使用
   * @param {object} queryParams - 分页信息
   * @return Promise
   */
  @Bind()
  query(queryParams = {}) {
    this.setState({
      queryParams,
    });
    const { ds } = this.state;
    const { context, queryForm } = this.props;
    const form = getContextValue(context, queryForm);
    if (form && isFunction(form.validateFields)) {
      // 有查询表单
      return new Promise((resolve, reject) => {
        form.validateFields((err, fieldsValue) => {
          if (err) {
            reject(err);
          } else {
            // 处理查询参数
            const parsedParameters = parseParameters(queryParams);
            // 相当于常量查询条件
            const nowParams = { ...parsedParameters, ...fieldsValue };
            if (ds) {
              return this.fetch(nowParams).then(resolve, reject);
            }
            return resolve();
          }
        });
      });
    }

    // 处理查询参数
    const parsedParameters = parseParameters(queryParams);
    if (ds) {
      return this.fetch(parsedParameters);
    }
    return Promise.resolve();
  }

  /**
   * 调用 queryUrl 查询数据
   * @param {Object} queryParams
   */
  @Bind()
  fetch(queryParams = {}) {
    const { defaultPageSize = 10 } = this.props;
    this.setState({
      queryLoading: true,
    });
    const { queryUrl } = this.props;
    return fetchGet({ url: queryUrl, query: { page: 0, size: defaultPageSize, ...queryParams } })
      .then(res => {
        const data = getResponse(res);
        if (data) {
          this.setState({
            pagingDataSource: data,
          });
        }
        return data;
      })
      .finally(() => {
        this.setState({
          queryLoading: false,
        });
      });
  }

  /**
   * @param {object} removeRecord - 要删除的记录
   */
  @Bind()
  handleLineRemove(removeRecord) {
    //
    const { removeUrl, batchRemoveUrl } = this.props;
    this.showLoading('lineRemoveLoading');
    if (removeUrl) {
      if (removeRecord) {
        return fetchDelete({
          url: removeUrl,
          // body: [removeRecord],
          body: removeRecord,
        })
          .then(response => {
            const res = getResponse(response);
            if (res) {
              this.query();
            }
            return res;
          })
          .finally(() => {
            this.hiddenLoading('lineRemoveLoading');
          });
      }
      notification.warning({
        message: intl
          .get('hzero.common.components.dynamicTable.notification.illegal')
          .d('非法的调用, 请联系管理员'),
      });
      return Promise.reject(
        new Error(
          intl
            .get('hzero.common.components.dynamicTable.notification.illegal')
            .d('非法的调用, 请联系管理员')
        )
      );
    }
    if (batchRemoveUrl) {
      if (removeRecord) {
        return fetchDelete({
          url: batchRemoveUrl,
          body: [removeRecord],
        })
          .then(response => {
            const res = getResponse(response);
            if (res) {
              this.query();
            } else {
              // todo 删除失败 需要 reject
              throw new Error('删除失败');
            }
            return res;
          })
          .finally(() => {
            this.hiddenLoading('lineRemoveLoading');
          });
      }
      notification.warning({
        message: intl
          .get('hzero.common.components.dynamicTable.notification.illegal')
          .d('非法的调用, 请联系管理员'),
      });
      return Promise.reject(
        new Error(
          intl
            .get('hzero.common.components.dynamicTable.notification.illegal')
            .d('非法的调用, 请联系管理员')
        )
      );
    }
    notification.warning({
      message: intl
        .get('hzero.common.components.dynamicTable.notification.url')
        .d('表格没有提供删除URL'),
    });
    this.hiddenLoading('lineRemoveLoading');
    return Promise.reject(
      new Error(
        intl
          .get('hzero.common.components.dynamicTable.notification.urlOrMore')
          .d('表格没有提供 删除URL 或 批量删除URL')
      )
    );
  }

  @Bind()
  remove() {
    const { removeUrl } = this.props;
    this.showLoading('removeLoading');
    if (removeUrl) {
      const selectedRows = this.getRows();
      if (selectedRows && selectedRows.length > 0) {
        return fetchDelete({
          url: removeUrl,
          body: selectedRows,
        }).then(deleteResponse => {
          const res = getResponse(deleteResponse);
          if (res) {
            this.query();
          } else {
            // todo 删除失败要报错
            throw new Error('删除失败');
          }
          return res;
        });
      }
      notification.warning({
        message: intl
          .get('hzero.common.components.dynamicTable.notification.Selected')
          .d('没有选中要删除的数据'),
      });
      return Promise.reject(
        new Error(
          intl
            .get('hzero.common.components.dynamicTable.notification.Selected')
            .d('没有选中要删除的数据')
        )
      );
    }
    this.hiddenLoading('removeLoading');
    return Promise.reject(
      new Error(
        intl
          .get('hzero.common.components.dynamicTable.notification.urls')
          .d('表格没有提供批量删除URL')
      )
    );
  }

  getColumnsAndScrollX() {
    const { context, fields } = this.props;
    let columnsWidth = 0;
    let hasAutoWidth = false;
    const columns = map(fields, field => {
      const dealFieldProps = dealObjectProps(field, context);
      const componentProps = preProcessComponentProps({ field, context });
      const isAutoWidth = +dealFieldProps.width === 0; // autoWidth field
      const columnWidth =
        +dealFieldProps.width || getWidthFromWord({ word: field[fieldLabelProp] });
      const column = {
        dataIndex: field[fieldNameProp],
        title: field[fieldLabelProp],
        align: dealFieldProps.align,
        render: (_, record) => getDisplayValue({ field, dataSource: record, componentProps }),
      };
      if (isAutoWidth) {
        hasAutoWidth = true;
        columnsWidth += 120;
      } else {
        column.width = columnWidth;
        columnsWidth += columnWidth;
      }
      switch (field.componentType) {
        case 'LinkButton':
          column.render = (_, record) => {
            const btns = {};
            forEach(componentProps, (prop, propKey) => {
              set(btns, propKey, prop);
            });
            // todo 按钮按钮的顺序
            const linkBtns = [];
            forEach(btns, btnProps => {
              const { btnKey } = btnProps;
              const modalProps = {};
              let onBtnClick;
              switch (btnProps.action) {
                case actionCode.page:
                  // 打开 Modal 或 页面
                  switch (btnProps.openType) {
                    case openTypeCode.inner:
                      // 跳转
                      break;
                    case openTypeCode.modal:
                      // 打开 Modal
                      modalProps.type = openTypeCode.modal;
                      modalProps.bodyStyle =
                        openPageModalBodyStyle[btnProps.openPageTypeModal].bodyStyle;
                      modalProps.width = openPageModalBodyStyle[btnProps.openPageTypeModal].width;
                      modalProps.modalBtns = btnProps.modalBtns; // modal 按钮
                      // 订阅事件
                      if (btnProps.subEvents) {
                        forEach(btnProps.subEvents, subEvent => {
                          const [subEventListenStr, subEventActionStr] = split(
                            subEvent,
                            subEventSep
                          );
                          const subEventAction = getContextValue(context, subEventActionStr);
                          if (isFunction(subEventAction)) {
                            modalProps[subEventListenStr] = subEventAction;
                          }
                        });
                      }
                      break;
                    case openTypeCode.drawer:
                      // 打开 侧滑Modal
                      modalProps.type = openTypeCode.drawer;
                      modalProps.width = btnProps.openPageTypeDrawer;
                      modalProps.modalBtns = btnProps.modalBtns; // modal 按钮
                      // 订阅事件
                      if (btnProps.subEvents) {
                        forEach(btnProps.subEvents, subEvent => {
                          const [subEventListenStr, subEventActionStr] = split(
                            subEvent,
                            subEventSep
                          );
                          const subEventAction = getContextValue(context, subEventActionStr);
                          if (isFunction(subEventAction)) {
                            modalProps[subEventListenStr] = subEventAction;
                          }
                        });
                      }
                      break;
                    default:
                      break;
                  }
                  onBtnClick = e => {
                    e.preventDefault();
                    const params = {};
                    const {
                      history: { search },
                    } = this.props;
                    const urlParams = querystring.parse(search);
                    const paramStream = (btnProps.params || '').split(',');
                    for (let i = 1; i <= paramStream.length; i++) {
                      if (i % 3 === 0) {
                        switch (paramStream[i - 2]) {
                          case paramTypeCode.fixParam:
                            params[paramStream[i - 3]] = paramStream[i - 1];
                            break;
                          case paramTypeCode.urlParam:
                            params[paramStream[i - 1]] = urlParams[paramStream[i - 3]];
                            break;
                          case paramTypeCode.columnParam:
                            params[paramStream[i - 1]] = record[paramStream[i - 3]];
                            break;
                          default:
                            break;
                        }
                      }
                    }
                    this.setState({
                      pageModalProps: {
                        pageCode: btnProps.pageCode,
                        ...modalProps,
                      },
                    });
                    if (this.pageModalRef) {
                      this.pageModalRef.init(params);
                      this.pageModalRef.show();
                    }
                  };
                  break;
                case actionCode.action:
                  // 自定义动作
                  onBtnClick = () => {
                    if (btnProps.actionEvent === internalFuncsInfo.lineRemove.code) {
                      this.handleLineRemove(record).catch((/* e */) => {
                        // console.error(e);
                      });
                    }
                  };
                  // Table 的动作, 删除行 ...
                  // 其他事件
                  break;
                default:
                  break;
              }
              linkBtns[btnProps.orderSeq] = {
                key: btnKey,
                ele: (
                  <a onClick={onBtnClick} style={linkButtonStyle}>
                    {btnProps.title}
                  </a>
                ),
                len: (btnProps.title && btnProps.title.length) || 4,
                title: btnProps.title || null,
              };
            });
            return operatorRender(linkBtns.filter(Boolean));
          };
          break;
        default:
          break;
      }
      return column;
    });
    if (hasAutoWidth) {
      return { columns };
    }
    return {
      scroll: { x: columnsWidth },
      columns,
    };
  }

  getRows() {
    const { selectedRows = [] } = this.state;
    return selectedRows;
  }

  getRowKeys() {
    const { selectedRowKeys = [] } = this.state;
    return selectedRowKeys;
  }
}

const exportFuncs = ['reload', 'query', 'remove'];
const exportFuncsInfo = {
  reload: {
    descriptionIntlCode: 'hpfm.ui.tpl.table.reload',
    descriptionIntlDefault: intl.get('hzero.common.button.reload').d('重新加载'),
    code: 'reload',
  },
  query: {
    descriptionIntlCode: 'hpfm.ui.tpl.table.query',
    descriptionIntlDefault: intl.get('hzero.common.button.search').d('查询'),
    code: 'query',
  },
  remove: {
    descriptionIntlCode: 'hpfm.ui.tpl.table.remove',
    descriptionIntlDefault: intl.get('hzero.common.button.delete').d('删除'),
    code: 'remove',
  },
};
const internalFuncs = ['lineRemove'];
const internalFuncsInfo = {
  lineRemove: {
    descriptionIntlCode: 'hpfm.ui.tpl.table.internal.lineRemove',
    descriptionIntlDefault: intl.get('hzero.common.button.deleteLine').d('删除行'),
    code: 'lineRemove',
  },
};

// 暴露出去的方法
DynamicTable.exportFuncs = exportFuncs;
DynamicTable.exportFuncsInfo = exportFuncsInfo;

// 行内按钮事件
DynamicTable.internalFuncs = internalFuncs;
DynamicTable.internalFuncsInfo = internalFuncsInfo;
