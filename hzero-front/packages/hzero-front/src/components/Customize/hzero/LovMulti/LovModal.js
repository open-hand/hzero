/* eslint-disable no-unused-expressions */
/* eslint-disable func-names */
import React from 'react';
import {
  Button,
  Form,
  Input,
  Row,
  Spin,
  Table,
  InputNumber,
  DatePicker,
  Select,
  // Tabs,
  Icon,
  Tooltip,
} from 'hzero-ui';
import moment from 'moment';
import { isArray, isEmpty, isFunction, isNil, omit } from 'lodash';
import qs from 'querystring';
import { Bind } from 'lodash-decorators';

import {
  createPagination,
  getCurrentOrganizationId,
  getResponse,
  getDateFormat,
  getDateTimeFormat,
} from 'utils/utils';
import intl from 'utils/intl';
import { DEFAULT_DATETIME_FORMAT, DEFAULT_DATE_FORMAT } from 'utils/constants';
import { totalRender } from 'utils/renderer';
import { getEnvConfig } from 'utils/iocUtils';
import notification from 'utils/notification';
import request from 'utils/request';
import { queryLovData } from 'services/api';
import styles from './index.less';

const FormItem = Form.Item;

const { HZERO_PLATFORM } = getEnvConfig('config');
const defaultRowKey = 'lovId';
const pageSizeOptions = ['10', '20', '50', '100'];
@Form.create({ fieldNameProp: null })
export default class LovMultiModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      leftSelectedKeys: [],
      leftChangeKeys: [],
      rightSelectedKeys: [],
      selectedKeys: [],
      filterRows: [],
      filtered: false,
      list: [],
      treeKeys: [],
      pagination: {},
      selectedPagination: {
        current: 1,
        pageSizeOptions,
        pageSize: 10,
        showSizeChanger: true,
        showTotal: (total, range) => totalRender(total, range),
      },
      loading: false,
      selectAllLoading: false,
      translateLoading: false,
      letfTempSize: 0,
      rightTempSize: 0,
      currentRightRecords: [],
      queryFields: {},
    };
    this.firstLoadFinshed = false;
  }

  setSateData(state) {
    if (this.mounted) {
      this.setState(state);
    }
  }

  componentDidMount() {
    const { currentValue } = this.props;
    const { selectedPagination } = this.state;
    const values =
      isNil(currentValue) || currentValue === '' ? [] : String(currentValue).split(',');
    if (values.length > 0) {
      this.setState({
        selectedKeys: values,
        selectedPagination: { ...selectedPagination, total: values.length },
      });
      this.translate(values.slice(0, 10), this.setCurrentRightRecords);
    }
    this.mounted = true;
  }

  componentDidUpdate() {
    const { lov } = this.props;
    if (!this.firstLoadFinshed && !isEmpty(lov)) {
      const { tableFields = [], queryFields = [] } = lov;
      if (tableFields.length > 0) {
        const scrollX = tableFields.reduce((p, n) => p + n.width || 0, 0);
        tableFields[tableFields.length - 1].width = undefined;
        // eslint-disable-next-line react/no-did-update-set-state
        scrollX > 600 && this.setState({ scrollX });
      }
      const queryFieldsObj = {};
      queryFields.forEach((item) => {
        queryFieldsObj[item.field] = item;
      });
      // eslint-disable-next-line react/no-did-update-set-state
      this.setState({ queryFields: queryFieldsObj });
      this.loadOnFirstVisible();
      this.firstLoadFinshed = true;
    }
  }

  @Bind()
  loadOnFirstVisible() {
    const { delayLoadFlag } = this.props.lov;
    if (this.mounted && !delayLoadFlag) {
      this.queryData();
    }
  }

  componentWillUnmount() {
    this.mounted = false;
  }

  @Bind()
  onLeftSelectChange(keys) {
    const { leftSelectedKeys } = this.state;
    this.setState({
      leftSelectedKeys: keys,
      leftChangeKeys: keys.filter((i) => !leftSelectedKeys.includes(i)),
    });
  }

  @Bind()
  onRightSelectChange(keys) {
    this.setState({
      rightSelectedKeys: keys,
    });
  }

  @Bind()
  processQueryParams(pagination, prefix = 'left') {
    const { form, lov } = this.props;
    const { queryFields } = this.state;
    const queryField = form.getFieldValue(`${prefix === 'left' ? 'l' : 'r'}Query`);
    const queryValue = form.getFieldValue(`${prefix}${queryField}`);
    const { field, dataType } = queryFields[queryField] || {};

    const filter = {};
    if (field) {
      filter[field] =
        dataType === 'DATE' || dataType === 'DATETIME'
          ? moment(queryValue).format(
              dataType === 'DATETIME' ? DEFAULT_DATETIME_FORMAT : DEFAULT_DATE_FORMAT
            )
          : queryValue;
    }
    const { queryUrl, pageSize, lovCode, lovTypeCode } = lov;
    const { queryParams = {} } = this.props;
    let nowQueryParams = queryParams || {};
    if (isFunction(nowQueryParams)) {
      nowQueryParams = nowQueryParams();
    }
    const queryIndex = queryUrl.indexOf('?');
    let sourceQueryParams = {};
    if (queryIndex !== -1) {
      sourceQueryParams = qs.parse(queryUrl.substr(queryIndex + 1));
    }

    const sourceParams = {
      ...filter,
      page: pagination.current - 1 || 0,
      size: pagination.pageSize || pageSize,
      ...sourceQueryParams,
      ...nowQueryParams,
    };
    const params =
      lovTypeCode !== 'URL'
        ? Object.assign(sourceParams, {
            lovCode,
          })
        : sourceParams;
    const url = getUrl(queryUrl, queryParams, queryIndex);
    return { params, url };
  }

  queryLovDataByPost(url, params) {
    const { page, size, ...others } = params;
    return getResponse(
      request(url, {
        method: 'POST',
        body: others,
        query: { page, size },
      })
    );
  }

  @Bind()
  queryData(pagination = {}) {
    const { letfTempSize } = this.state;
    const { queryUsePost } = this.props.lov;
    if (letfTempSize) {
      // eslint-disable-next-line no-param-reassign
      pagination.pageSize -= letfTempSize;
    }
    const { params, url } = this.processQueryParams(pagination);
    this.setState(
      {
        loading: true,
      },
      () => {
        (queryUsePost ? this.queryLovDataByPost(url, params) : queryLovData(url, params))
          .then((res) => {
            if (getResponse(res)) {
              this.dataFilter(res);
            }
          })
          .finally(() => {
            this.setState({
              loading: false,
              letfTempSize: 0,
            });
          });
      }
    );
  }

  /**
   * 树 child 属性更改
   * @param {Array} list 原树结构数据
   * @param {String} childName 要替换的 childName
   */
  @Bind()
  setChildren = (data, childName) =>
    childName
      ? data.map((n) => {
          const item = n;
          if (!isEmpty(n[childName])) {
            this.defineProperty(item, 'children', [{ ...n[childName] }]);
          }
          if (!isEmpty(item.children)) {
            item.children = this.setChildren(item.children);
          }
          return item;
        })
      : data;

  /**
   * 处理返回列表数据
   * @param {Object|Array} data - 返回的列表数据
   */
  @Bind()
  dataFilter(data) {
    const {
      lov: { valueField: rowkey = defaultRowKey, childrenFieldName },
    } = this.props;
    const isTree = isArray(data);
    const hasParams = !isEmpty(
      Object.values(this.props.form.getFieldsValue()).filter((e) => e !== undefined && e !== '')
    );
    const list = isTree ? this.setChildren(data, childrenFieldName) : data.content;
    const pagination = !isTree && createPagination(data);

    const treeKeys = []; // 树状 key 列表
    if (isTree && hasParams) {
      /**
       * 遍历生成树列表
       * @param {*} treeList - 树列表数据
       */
      const flatKeys = (treeList) => {
        if (isArray(treeList.children) && !isEmpty(treeList.children)) {
          treeKeys.push(treeList[rowkey]);
          treeList.children.forEach((item) => flatKeys(item));
        } else {
          treeKeys.push(treeList[rowkey]);
        }
      };

      list.forEach((item) => flatKeys(item)); // 遍历生成 key 列表
    }

    this.setSateData({
      list,
      treeKeys,
      pagination,
    });
  }

  @Bind()
  defineProperty(obj, property, value) {
    Object.defineProperty(obj, property, {
      value,
      writable: true,
      enumerable: false,
      configurable: true,
    });
  }

  /**
   * 访问对象由字符串指定的多层属性
   * @param {Object} obj 访问的对象
   * @param {String} str 属性字符串，如 'a.b.c.d'
   */
  @Bind()
  parseField(obj, str) {
    if (/[.]/g.test(str)) {
      const arr = str.split('.');
      const newObj = obj[arr[0]];
      const newStr = arr.slice(1).join('.');
      return this.parseField(newObj, newStr);
    }
    return String(obj[str]);
  }

  @Bind()
  async getSelectedData() {
    const { selectedKeys, filterRows, filtered } = this.state;
    const { valueField = defaultRowKey, displayField } = this.props.lov;
    const meaningMap = {};
    if (filterRows.length > 0 || filtered) {
      const tempValues = selectedKeys.slice(0, 5);
      let meaningLength = 0;
      for (let i = 0; meaningLength <= 5 && i < filterRows.length; i++) {
        const record = filterRows[i];
        const value = this.parseField(record, valueField);
        if (tempValues.includes(value)) {
          meaningMap[value] = this.parseField(record, displayField);
          meaningLength += 1;
        }
      }
    } else {
      const records = await this.translate(selectedKeys.slice(0, 5));
      records.forEach((item) => {
        const value = this.parseField(item, valueField);
        const meaning = this.parseField(item, displayField);
        meaningMap[value] = meaning;
      });
    }
    return { values: selectedKeys, meaningMap };
  }

  /**
   * 已选择页签-清除已选中的数据
   */
  @Bind()
  clearSelected() {
    this.setState({
      rightSelectedKeys: [],
      filterRows: [],
      selectedKeys: [],
      currentRightRecords: [],
    });
  }

  @Bind()
  selectToRight() {
    const {
      leftSelectedKeys,
      currentRightRecords,
      selectedKeys,
      list = [],
      filterRows,
    } = this.state;
    const { valueField = defaultRowKey } = this.props.lov;
    const newLeftKeys = leftSelectedKeys.filter((i) => !selectedKeys.includes(i));
    const addLeftRows = list.filter((i) => newLeftKeys.includes(this.parseField(i, valueField)));
    if (newLeftKeys.length > 0) {
      const disableTranslate = filterRows.length > 0;
      const { current, pageSize } = this.state.selectedPagination;
      const insertIndex = (current - 1) * pageSize;
      const tempSelectedKeys = [...selectedKeys];
      Array.prototype.splice.apply(tempSelectedKeys, [insertIndex, 0, ...newLeftKeys]);
      if (disableTranslate) {
        const tempFilter = [...filterRows];
        Array.prototype.splice.apply(tempFilter, [insertIndex, 0, ...addLeftRows]);
        this.setState({
          leftChangeKeys: [],
          leftSelectedKeys: [],
          selectedKeys: tempSelectedKeys,
          currentRightRecords: [],
          filterRows: tempFilter,
          rightTempSize: pageSize < selectedKeys.length ? newLeftKeys.length : 0,
        });
      } else {
        this.setState({
          leftChangeKeys: [],
          leftSelectedKeys: [],
          selectedKeys: tempSelectedKeys,
          currentRightRecords: [...addLeftRows, ...currentRightRecords],
          filterRows: [],
          rightTempSize:
            pageSize < currentRightRecords.length + newLeftKeys.length ? newLeftKeys.length : 0,
        });
      }
    }
  }

  /**
   * 已选择页签-移除勾选的数据
   */
  @Bind()
  unSelectRight() {
    const {
      rightSelectedKeys,
      currentRightRecords,
      list,
      selectedKeys,
      filterRows,
      letfTempSize: stateLeftTempSize,
      rightTempSize: stateRightTempSize,
      filtered,
    } = this.state;
    const disableTranslate = filterRows.length > 0 || filtered;
    const { valueField = defaultRowKey } = this.props.lov;
    const opRows = disableTranslate ? filterRows : currentRightRecords;
    const removeData = [];
    const newLeftData = [];
    const newSelectedRows = [];
    opRows.forEach((i) => {
      const value = this.parseField(i, valueField);
      if (rightSelectedKeys.includes(value)) {
        removeData.push(i);
      } else {
        newSelectedRows.push(i);
      }
    });
    const leftKeys = list.map((i) => this.parseField(i, valueField));
    removeData.forEach((i) => {
      const value = this.parseField(i, valueField);
      if (!leftKeys.includes(value)) {
        newLeftData.push(i);
      }
    });
    if (rightSelectedKeys.length > 0) {
      const newSelectedKeys = selectedKeys.filter((i) => !rightSelectedKeys.includes(i));
      if (disableTranslate) {
        this.setState({
          selectedKeys: newSelectedKeys,
          rightSelectedKeys: [],
          list: [...list, ...newLeftData],
          filterRows: newSelectedRows,
          letfTempSize: newLeftData.length + stateLeftTempSize,
        });
      } else {
        this.setState({
          selectedKeys: newSelectedKeys,
          rightSelectedKeys: [],
          currentRightRecords: newSelectedRows,
          list: [...list, ...newLeftData],
          letfTempSize: newLeftData.length + stateLeftTempSize,
          rightTempSize: stateRightTempSize - newLeftData.length,
        });
      }
    }
  }

  @Bind()
  selectAllByQuery() {
    const { selectedKeys, filterRows, rightTempSize } = this.state;
    const { valueField = defaultRowKey } = this.props.lov;
    const oldKeysMap = {};
    selectedKeys.forEach((key) => {
      oldKeysMap[key] = 1;
    });
    this.setState({
      filterRows: [],
      selectedKeys: [],
      rightTempSize: 0,
    });
    this.splitGroupSelect({
      resolve: (data) => {
        const { selectedKeys: keys, filterRows: rows } = this.state;
        const currentKeys = [];
        const currentData = [];
        data.forEach((item) => {
          const key = this.parseField(item, valueField);
          if (oldKeysMap[key] === 1) {
            delete oldKeysMap[key];
          }
          currentKeys.push(key);
          currentData.push(item);
        });
        this.setState({
          selectedKeys: [...keys, ...currentKeys],
          filterRows: [...rows, ...currentData],
        });
      },
      onError: () => {
        this.setState({
          selectedKeys,
          filterRows,
          rightTempSize,
        });
      },
      onFinish: async (cb) => {
        const { selectedKeys: keys, filterRows: rows } = this.state;
        const remainKeys = Object.keys(oldKeysMap);
        const remainData = await this.translate(remainKeys);
        this.setState({
          selectedKeys: [...keys, ...remainKeys],
          filterRows: [...rows, ...remainData],
        });
        cb();
      },
      query: 'left',
    });
  }

  @Bind()
  splitGroupSelect({ resolve, onError, onFinish, query = 'right' }) {
    const { params, url } = this.processQueryParams({ current: 1, pageSize: 500 }, query);
    const _this = this;
    const { queryUsePost } = this.props.lov;
    const queryFun = queryUsePost ? this.queryLovDataByPost : queryLovData;
    function select(queryParams) {
      queryFun(url, queryParams)
        .then((res) => {
          if (getResponse(res)) {
            const data = processTreeData(res.content || []);
            // eslint-disable-next-line no-unused-expressions
            resolve && resolve(data);
            if (res.number < res.totalPages) {
              select({ ...queryParams, page: res.number + 1 });
            } else if (typeof onFinish === 'function') {
              onFinish(() => _this.setState({ selectAllLoading: false }));
            } else {
              _this.setState({
                selectAllLoading: false,
              });
            }
          }
        })
        .catch(() => {
          notification.error();
          // eslint-disable-next-line no-unused-expressions
          onError && onError();
          this.setState({
            selectAllLoading: false,
          });
        });
    }
    this.setState({
      selectAllLoading: true,
    });
    select(params);
  }

  @Bind()
  onSearch() {
    this.queryData();
  }

  @Bind()
  onResetRight() {
    const { selectedKeys } = this.state;
    const keys = Object.keys(this.props.form.getFieldsValue());
    const resetFields = [];
    keys.forEach((i) => {
      if (i.startsWith('right')) {
        resetFields.push(i);
      }
    });
    this.props.form.resetFields(resetFields);
    this.setState({ filterRows: [], filtered: false });
    this.translate(selectedKeys.slice(0, 10), this.setCurrentRightRecords);
  }

  @Bind()
  onResetLeft() {
    const keys = Object.keys(this.props.form.getFieldsValue());
    const resetFields = [];
    keys.forEach((i) => {
      if (i.startsWith('left')) {
        resetFields.push(i);
      }
    });
    this.props.form.resetFields(resetFields);
  }

  @Bind()
  onSearchSelected() {
    const { valueField = defaultRowKey } = this.props.lov;
    this.setState({
      filterRows: [],
      filtered: true,
    });
    this.splitGroupSelect({
      resolve: (data) => {
        const { filterRows, selectedKeys } = this.state;
        const newRows = data.filter((item) =>
          selectedKeys.includes(this.parseField(item, valueField))
        );
        this.setState({
          filterRows: [...filterRows, ...newRows],
        });
      },
    });
  }

  @Bind()
  onRightPagination(pagination = {}) {
    const { current, pageSize } = pagination;
    const { selectedKeys, filterRows, rightTempSize } = this.state;
    const newPageSize = pageSizeOptions.includes(String(pageSize))
      ? pageSize
      : pageSize - rightTempSize;
    if (filterRows.length === 0) {
      const start = (current - 1) * newPageSize;
      const end = start + newPageSize;
      this.translate(selectedKeys.slice(start, end), this.setCurrentRightRecords);
      this.setState({
        selectedPagination: { ...pagination, pageSize: newPageSize },
        rightTempSize: 0,
      });
    } else {
      this.setState({
        selectedPagination: { ...pagination, pageSize: newPageSize },
        rightTempSize: 0,
      });
    }
  }

  @Bind()
  async translate(values, onSuccess) {
    const { code } = this.props;
    this.setState({ translateLoading: true });
    return request(
      `${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/ui-customize/translateLov?lovViewCode=${code}`,
      {
        body: values,
        method: 'POST',
      }
    )
      .then((res) => {
        if (getResponse(res)) {
          const resArr = values.map((i) => omit(res[i], 'children'));
          onSuccess && onSuccess(resArr);
          return resArr;
        }
      })
      .finally(() => {
        this.setState({ translateLoading: false });
      });
  }

  @Bind()
  setCurrentRightRecords(res) {
    this.setState({ currentRightRecords: res });
  }

  render() {
    const {
      lov,
      form,
      ldpData = {},
      lovLoadLoading,
      queryInputProps = {},
      isDbc2Sbc = true,
    } = this.props;
    if (lovLoadLoading) {
      return <Spin spinning />;
    }
    const {
      list = [],
      loading,
      pagination,
      selectedPagination,
      treeKeys,
      leftSelectedKeys,
      selectedKeys,
      currentRightRecords,
      filterRows,
      filtered,
      rightSelectedKeys,
      selectAllLoading,
      translateLoading,
      leftChangeKeys,
      letfTempSize,
      scrollX,
      rightTempSize,
      queryFields,
    } = this.state;
    const disableTranslate = filterRows.length > 0 || filtered;
    const {
      tableFields = [],
      queryFields: queryFieldsList = [],
      valueField: rowkey = defaultRowKey,
    } = lov;
    const { getFieldDecorator, getFieldValue } = form;
    const isTree = isArray(list);
    const rowSelection = {
      type: 'checkbox',
      selectedRowKeys: leftSelectedKeys,
      getCheckboxProps: (record) => {
        const isSelected = selectedKeys.includes(this.parseField(record, rowkey));
        const newProps = {};
        if (isSelected) {
          newProps.checked = true;
          newProps.disabled = true;
        }
        return newProps;
      },
      onChange: this.onLeftSelectChange,
    };
    const leftPagination = { ...pagination, pageSize: letfTempSize + pagination.pageSize };
    const leftScroll = {
      x: scrollX,
      y: leftPagination.pageSize > 10 ? 365 : undefined,
    };

    const tableProps = {
      rowKey: (record) => this.parseField(record, rowkey),
      loading,
      rowSelection,
      pagination: leftPagination,
      bordered: true,
      dataSource: list,
      columns: tableFields,
      onChange: this.queryData,
      scroll: leftScroll,
    };
    const rightPagination = {
      ...selectedPagination,
      pageSize: rightTempSize + selectedPagination.pageSize,
    };
    if (!disableTranslate) {
      rightPagination.total = selectedKeys.length;
      rightPagination.pageSize =
        currentRightRecords.length < selectedPagination.pageSize
          ? selectedPagination.pageSize
          : rightPagination.pageSize;
    } else {
      rightPagination.total = filterRows.length;
    }
    const rightScroll = {
      x: scrollX,
      y: rightPagination.pageSize > 10 && selectedKeys.length > 10 ? 365 : undefined,
    };
    const selectedTableProps = {
      rowKey: (record) => this.parseField(record, rowkey),
      loading: selectAllLoading || translateLoading,
      columns: tableFields,
      dataSource: disableTranslate ? filterRows : currentRightRecords,
      scroll: rightScroll,
      bordered: true,
      pagination: rightPagination,
      rowSelection: {
        type: 'checkbox',
        selectedRowKeys: rightSelectedKeys,
        onChange: this.onRightSelectChange,
      },
      onChange: this.onRightPagination,
    };
    const treeProps = isTree
      ? {
          uncontrolled: true,
          expandedRowKeys: treeKeys,
        }
      : {};

    return (
      <div className={styles['lovmulti-wrapper']}>
        <div className="transfer-wrapper">
          <div className="left-unselect" style={{ width: 600 }}>
            {queryFieldsList.length > 0 ? (
              <Row className="header">
                <div className="inputs">
                  <FormItem>
                    {getFieldDecorator('lQuery', {
                      initialValue:
                        queryFieldsList.length > 0 ? queryFieldsList[0].field : undefined,
                    })(
                      <Select allowClear style={{ width: '100%' }}>
                        {queryFieldsList.map((item) => (
                          <Select.Option value={item.field} key={item.field}>
                            {item.label}
                          </Select.Option>
                        ))}
                      </Select>
                    )}
                  </FormItem>
                  {getQueryComponent(queryFields[getFieldValue('lQuery')], {
                    form,
                    ldpData,
                    isDbc2Sbc,
                    queryInputProps,
                    prefix: 'left',
                  })}
                </div>
                <div className="buttons">
                  <Button onClick={this.onResetLeft} style={{ marginRight: 8 }}>
                    {intl.get('hzero.common.button.reset').d('重置')}
                  </Button>
                  <Button
                    htmlType="submit"
                    type="primary"
                    onClick={this.onSearch}
                    loading={loading}
                  >
                    {intl.get('hzero.common.button.search').d('查询')}
                  </Button>
                </div>
              </Row>
            ) : null}
            <Table {...tableProps} {...treeProps} />
          </div>
          <div className="control">
            <Tooltip
              title={intl.get('hzero.common.button.selectAllByQuery').d('选中符合条件的所有数据')}
            >
              <Button onClick={this.selectAllByQuery} loading={selectAllLoading}>
                <Icon type="double-right" />
              </Button>
            </Tooltip>
            <Button onClick={this.selectToRight} disabled={leftChangeKeys.length === 0}>
              <Icon type="right" />
            </Button>
            <Button onClick={this.unSelectRight} disabled={rightSelectedKeys.length === 0}>
              <Icon type="left" />
            </Button>
            <Tooltip title={intl.get('hzero.common.button.clearSelected').d('清空已选数据')}>
              <Button onClick={this.clearSelected}>
                <Icon type="minus-circle-o" />
              </Button>
            </Tooltip>
          </div>
          <div className="right-selected" style={{ width: 600 }}>
            {queryFieldsList.length > 0 ? (
              <Row className="header">
                <div className="inputs">
                  <FormItem>
                    {getFieldDecorator('rQuery', {
                      initialValue:
                        queryFieldsList.length > 0 ? queryFieldsList[0].field : undefined,
                    })(
                      <Select
                        allowClear
                        style={{ width: '100%' }}
                        defaultValue={
                          queryFieldsList.length > 0 ? queryFieldsList[0].field : undefined
                        }
                      >
                        {queryFieldsList.map((item) => (
                          <Select.Option value={item.field} key={item.field}>
                            {item.label}
                          </Select.Option>
                        ))}
                      </Select>
                    )}
                  </FormItem>
                  {getQueryComponent(queryFields[getFieldValue('rQuery')], {
                    form,
                    ldpData,
                    isDbc2Sbc,
                    queryInputProps,
                    prefix: 'right',
                  })}
                </div>
                <div className="buttons">
                  <Button onClick={this.onResetRight} style={{ marginRight: 8 }}>
                    {intl.get('hzero.common.button.reset').d('重置')}
                  </Button>
                  <Button
                    type="primary"
                    htmlType="submit"
                    onClick={this.onSearchSelected}
                    loading={selectAllLoading}
                  >
                    {intl.get('hzero.common.button.search').d('查询')}
                  </Button>
                </div>
              </Row>
            ) : null}
            <Table {...selectedTableProps} />
          </div>
        </div>
      </div>
    );
  }
}
/**
 * 替换查询 Url 中的变量
 * @param {String} url
 * @param {Object} data
 */
function getUrl(url, data, queryIndex) {
  let ret = url;
  const organizationRe = /\{organizationId\}|\{tenantId\}/g;
  Object.keys(data).map((key) => {
    const re = new RegExp(`{${key}}`, 'g');
    ret = ret.replace(re, data[key]);
    return ret;
  });
  if (organizationRe.test(ret)) {
    ret = ret.replace(organizationRe, getCurrentOrganizationId());
  }
  const index = ret.indexOf('?'); // 查找是否有查询条件
  if (queryIndex !== -1) {
    ret = ret.substr(0, index);
  }
  return ret;
}

function processTreeData(data) {
  let res = [];
  data.forEach((item) => {
    res.push(omit(item, ['children']));
    if (item.children) {
      res = res.concat(processTreeData(item.children));
    }
  });
  return res;
}

function getQueryComponent(queryItem = {}, others) {
  const { form, ldpData, isDbc2Sbc, queryInputProps, prefix = '' } = others;
  const { getFieldDecorator } = form;
  const valueListData = ldpData[queryItem.sourceCode] || [];
  if (isNil(queryItem.field)) {
    return null;
  }
  switch (queryItem.dataType) {
    case 'INT':
      return (
        <FormItem>
          {getFieldDecorator(`${prefix}${queryItem.field}`)(
            <InputNumber style={{ width: '100%' }} />
          )}
        </FormItem>
      );
    case 'DATE':
      return (
        <FormItem>
          {getFieldDecorator(`${prefix}${queryItem.field}`)(
            <DatePicker style={{ width: '100%' }} placeholder="" format={getDateFormat()} />
          )}
        </FormItem>
      );
    case 'DATETIME':
      return (
        <FormItem>
          {getFieldDecorator(`${prefix}${queryItem.field}`)(
            <DatePicker
              style={{ width: '100%' }}
              placeholder=""
              showTime={{ format: DEFAULT_DATETIME_FORMAT }}
              format={getDateTimeFormat()}
            />
          )}
        </FormItem>
      );
    case 'SELECT':
      return (
        <FormItem>
          {getFieldDecorator(`${prefix}${queryItem.field}`)(
            <Select allowClear style={{ width: '100%' }}>
              {valueListData.map((item) => (
                <Select.Option value={item.value} key={item.value}>
                  {item.meaning}
                </Select.Option>
              ))}
            </Select>
          )}
        </FormItem>
      );
    default:
      return (
        <FormItem>
          {getFieldDecorator(`${prefix}${queryItem.field}`)(
            <Input dbc2sbc={isDbc2Sbc} {...queryInputProps} />
          )}
        </FormItem>
      );
  }
}
