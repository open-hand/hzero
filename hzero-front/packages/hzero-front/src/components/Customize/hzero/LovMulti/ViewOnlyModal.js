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
  Col,
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

const { HZERO_PLATFORM } = getEnvConfig('config');
const FormItem = Form.Item;
const formItemLayout = {
  labelCol: {
    sm: { span: 8 },
  },
  wrapperCol: {
    sm: { span: 14 },
  },
};

const defaultRowKey = 'lovId';
const pageSizeOptions = ['10', '20', '50', '100'];
@Form.create({ fieldNameProp: null })
export default class LovMultiModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedKeys: [],
      filterRows: [],
      filtered: false,
      selectedPagination: {
        pageSizeOptions,
        pageSize: 10,
        showSizeChanger: true,
        showTotal: (total, range) => totalRender(total, range),
      },
      loading: false,
      selectAllLoading: false,
      translateLoading: false,
      tempSize: 0,
      currentRecords: [],
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
      this.translate(values.slice(0, 10), this.setCurrentRecords);
    }
    this.mounted = true;
  }

  componentDidUpdate() {
    const { lov } = this.props;
    if (!this.firstLoadFinshed && !isEmpty(lov)) {
      const { tableFields = [] } = lov;
      if (tableFields.length > 0) {
        const scrollX = tableFields.reduce((p, n) => p + n.width || 0, 0);
        tableFields[tableFields.length - 1].width = undefined;
        // eslint-disable-next-line react/no-did-update-set-state
        scrollX > 600 && this.setState({ scrollX });
      }
      this.firstLoadFinshed = true;
    }
  }

  componentWillUnmount() {
    this.mounted = false;
  }

  @Bind()
  processQueryParams(pagination) {
    const filter = this.props.form.getFieldsValue();
    const { queryUrl, pageSize, lovCode, lovTypeCode, queryFields = [] } = this.props.lov;
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

    const formatFilter = { ...filter };
    queryFields.forEach((item) => {
      if (item.dataType === 'DATE' || item.dataType === 'DATETIME') {
        if (filter[item.field]) {
          formatFilter[item.field] = moment(filter[item.field]).format(
            item.dataType === 'DATETIME' ? DEFAULT_DATETIME_FORMAT : DEFAULT_DATE_FORMAT
          );
        }
      }
    });
    const sourceParams = {
      ...formatFilter,
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

  @Bind()
  formReset() {
    this.props.form.resetFields();
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
  splitGroupSelect({ resolve, onError, onFinish }) {
    const { params, url } = this.processQueryParams({ current: 1, pageSize: 500 }, 'right');
    const _this = this;
    const { queryUsePost } = this.props.lov;
    const query = queryUsePost ? this.queryLovDataByPost : queryLovData;
    function select(queryParams) {
      query(url, queryParams)
        .then((res) => {
          if (getResponse(res)) {
            const data = processTreeData(res.content || []);
            // eslint-disable-next-line no-unused-expressions
            resolve && resolve(data);
            if (res.number < res.totalPages) {
              select({ ...queryParams, page: res.number + 1 });
            } else {
              _this.setState(
                {
                  selectAllLoading: false,
                },
                onFinish
              );
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
  onReset() {
    this.formReset();
    this.setState({ filterRows: [], filtered: false });
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
  onPagination(pagination = {}) {
    const { current, pageSize } = pagination;
    const { selectedKeys, filterRows } = this.state;
    if (filterRows.length === 0) {
      const start = (current - 1) * pageSize;
      const end = start + pageSize;
      this.translate(selectedKeys.slice(start, end), this.setCurrentRecords);
      this.setState({ selectedPagination: pagination });
    } else {
      this.setState({
        selectedPagination: pagination,
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
  setCurrentRecords(res) {
    this.setState({ currentRecords: res });
  }

  render() {
    const {
      lov,
      form,
      width,
      ldpData = {},
      lovLoadLoading,
      queryInputProps = {},
      isDbc2Sbc = true,
    } = this.props;
    if (lovLoadLoading) {
      return <Spin spinning />;
    }
    const {
      loading,
      selectedPagination,
      selectedKeys,
      currentRecords,
      filterRows,
      selectAllLoading,
      translateLoading,
      scrollX,
      tempSize,
      filtered,
    } = this.state;
    const disableTranslate = filterRows.length > 0 || filtered;
    const { tableFields = [], queryFields = [], valueField: rowkey = defaultRowKey } = lov;
    const { getFieldDecorator } = form;

    const rightPagination = {
      ...selectedPagination,
      pageSize: tempSize + selectedPagination.pageSize,
    };
    if (!disableTranslate) {
      rightPagination.total = selectedKeys.length;
    }
    const rightScroll = { x: scrollX, y: rightPagination.pageSize > 10 ? 365 : undefined };
    const selectedTableProps = {
      rowKey: (record) => this.parseField(record, rowkey),
      loading: selectAllLoading || translateLoading,
      columns: tableFields,
      dataSource: disableTranslate ? filterRows : currentRecords,
      scroll: rightScroll,
      bordered: true,
      pagination: rightPagination,
      onChange: this.onPagination,
    };
    // 查询条件表单
    const span = queryFields.length <= 1 || width <= 400 ? 24 : 12;
    const queryInput = queryFields.map((queryItem = {}) => {
      const valueListData = ldpData[queryItem.sourceCode] || [];
      switch (queryItem.dataType) {
        case 'INT':
          return (
            <Col span={span} key={queryItem.field}>
              <FormItem {...formItemLayout} label={queryItem.label}>
                {getFieldDecorator(queryItem.field)(
                  <InputNumber style={{ width: '100%' }} onPressEnter={this.onSearchSelected} />
                )}
              </FormItem>
            </Col>
          );
        case 'DATE':
          return (
            <Col span={span} key={queryItem.field}>
              <FormItem {...formItemLayout} label={queryItem.label}>
                {getFieldDecorator(queryItem.field)(
                  <DatePicker style={{ width: '100%' }} placeholder="" format={getDateFormat()} />
                )}
              </FormItem>
            </Col>
          );
        case 'DATETIME':
          return (
            <Col span={span} key={queryItem.field}>
              <FormItem {...formItemLayout} label={queryItem.label}>
                {getFieldDecorator(queryItem.field)(
                  <DatePicker
                    style={{ width: '100%' }}
                    placeholder=""
                    showTime={{ format: DEFAULT_DATETIME_FORMAT }}
                    format={getDateTimeFormat()}
                  />
                )}
              </FormItem>
            </Col>
          );
        case 'SELECT':
          return (
            <Col span={span} key={queryItem.field}>
              <FormItem {...formItemLayout} label={queryItem.label}>
                {getFieldDecorator(queryItem.field)(
                  <Select allowClear style={{ width: '100%' }}>
                    {valueListData.map((item) => (
                      <Select.Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
          );
        default:
          return (
            <Col span={span} key={queryItem.field}>
              <FormItem {...formItemLayout} label={queryItem.label}>
                {getFieldDecorator(queryItem.field)(
                  <Input dbc2sbc={isDbc2Sbc} {...queryInputProps} />
                )}
              </FormItem>
            </Col>
          );
      }
    });

    return (
      <div className={styles['lovmulti-wrapper']}>
        {queryFields.length > 0 ? (
          <div style={{ display: 'flex', marginBottom: '10px', alignItems: 'flex-start' }}>
            <Row style={{ flex: 'auto' }} className="query-fields">
              {queryInput}
            </Row>
            <div className="lov-modal-btn-container">
              <Button onClick={this.onReset} style={{ marginRight: 8 }}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                type="primary"
                htmlType="submit"
                onClick={this.onSearchSelected}
                loading={loading}
                style={{ marginRight: 8 }}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </div>
          </div>
        ) : null}
        <Table {...selectedTableProps} />
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
