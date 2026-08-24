/**
 * 其他相关
 * @date: 2019-12-25
 * @author: wjc <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { notification } from 'hzero-ui';
import { isArray, isEmpty, sortBy, uniq } from 'lodash';
import moment from 'moment';
import uuid from 'uuid/v4';

import { GLOBAL_PAGE_SIZE, PAGE_SIZE_OPTIONS } from '../constants';
import intl from '../intl';
import { getCurrentOrganizationId, isTenantRoleLevel } from './user';

export function isPromise(obj) {
  return (
    !!obj &&
    (typeof obj === 'object' || typeof obj === 'function') &&
    typeof obj.then === 'function'
  );
}

/**
 * tableScrollWidth - 计算滚动表格的宽度
 * 如果 fixWidth 不传或者为0, 会将没有设置宽度的列 宽度假设为 200
 * @param {array} columns - 表格列
 * @param {number} fixWidth - 不固定宽度列需要补充的宽度
 * @return {number} - 返回计算过的 x 值
 */
export function tableScrollWidth(columns: any[] = [], fixWidth = 0) {
  let fillFixWidthCount = 0;
  const total = columns.reduce((prev, current) => {
    if (current.width) {
      return prev + current.width;
    }
    fillFixWidthCount += 1;
    return prev;
  }, 0);
  if (fixWidth) {
    return total + fixWidth + 1;
  }
  return total + fillFixWidthCount * 200 + 1;
}

/**
 * 过滤掉对象值为 undefined 和 空字符串 和 空数组 的属性
 * @author WH <heng.wei@hand-china.com>
 * @param {Object} obj
 * @returns {Object} 过滤后的查询参数
 */
export function filterNullValueObject(obj) {
  const result = {};
  if (obj && Object.keys(obj).length >= 1) {
    Object.keys(obj).forEach((key) => {
      if (key && obj[key] !== undefined && obj[key] !== '' && obj[key] !== null) {
        // 如果查询的条件不为空
        if (isArray(obj[key]) && obj[key].length === 0) {
          return;
        }
        result[key] = obj[key];
      }
    });
  }
  return result; // 返回查询条件
}

/**
 * 解析查询参数
 * @param {Object} params
 */
export function parseParameter(params: any = {}) {
  const { page = { current: 1, pageSize: 10 }, sort = {}, body } = params;

  if (sort.order === 'ascend') {
    sort.order = 'asc';
  }
  if (sort.order === 'descend') {
    sort.order = 'desc';
  }

  const sortObj: any = {};
  if (!isEmpty(sort)) {
    sortObj.sort = `${sort.field},${sort.order}`;
  }

  return {
    page: page.current - 1,
    size: page.pageSize,
    ...body,
    ...sortObj,
  };
}

/**
 * 解析查询参数
 * @author WH <heng.wei@hand-china.com>
 * @param {Object} params
 * @returns {Object} 解析后的查询参数
 */
export function parseParameters(params: any = {}) {
  const { page = {}, sort = {}, ...others } = params;
  const { current = 1, pageSize = GLOBAL_PAGE_SIZE } = page;
  if (sort.order === 'ascend') {
    sort.order = 'asc';
  }
  if (sort.order === 'descend') {
    sort.order = 'desc';
  }
  const sortObj: any = {};
  if (!isEmpty(sort)) {
    sortObj.sort = `${sort.field},${sort.order}`;
  }
  let size = pageSize;
  const sourceSize = [...PAGE_SIZE_OPTIONS];
  if (!sourceSize.includes(`${pageSize}`)) {
    const sizes = sortBy(uniq([...sourceSize, `${pageSize}`]), (i) => +i);
    const index = sizes.findIndex((item) => +item === pageSize);
    size = +sizes[index];
  }
  return {
    size,
    page: current - 1,
    ...others,
    ...sortObj,
  };
}

// todo 调整消息返回
export function getResponse(response, errorCallback?) {
  if (response && response.failed === true) {
    if (errorCallback) {
      errorCallback(response);
    } else {
      const msg = {
        message: intl.get('hzero.common.notification.error').d('操作失败'),
        description: response.message,
      };
      switch (response.type) {
        case 'info':
          notification.info(msg);
          break;
        case 'warn':
          notification.warning(msg);
          break;
        case 'error':
        default:
          notification.error(msg);
          break;
      }
    }
  } else {
    return response;
  }
}

/**
 * 捕获下载文件时的错误信息(表单下载)
 * @param {string} errorCode - 后端返回的错误类型
 * @param {string} msg - 自定义错误提示标题
 * @param {string} noticeType - 报错提示类型
 */
export function listenDownloadError(errorCode = '', msg = '', noticeType = 'error') {
  // 监听表单下载错误时 postMessage 事件
  window.addEventListener('message', (e) => {
    const {
      data: { type, message },
    } = e;
    if (type && type === errorCode) {
      notification[noticeType]({ message: msg, description: message });
    }
  });
}

/**
 * getCodeMeaning - 在值集中根据value获取对应的meaning
 * @param {any} value - 值集中某对象的value
 * @param {Array<Object>} [code=[]] - 值集集合
 * @returns {string}
 */
export function getCodeMeaning(value, code: any[] = []) {
  let result;
  if (value && !isEmpty(code)) {
    const codeList = code.filter((n) => n.value === value);
    if (!isEmpty(codeList)) {
      result = codeList[0].meaning;
    }
  }
  return result;
}

/**
 * 获取平台版本API
 */
export function getPlatformVersionApi(api) {
  const tenantId = getCurrentOrganizationId();
  const isTenantLevel = isTenantRoleLevel();
  return isTenantLevel ? `${tenantId}/${api}` : `${api}`;
}

/**
 * 根据起始值生成响应的数组
 * @param {Number} start - 开始值
 * @param {Number} end - 结束值
 */
export function newArray(start, end) {
  const result: any = [];
  for (let i = start; i < end; i++) {
    result.push(i);
  }
  return result;
}

/**
 * 通过 ref 获取表单数据
 * this.filterFormRef = React.createRef();
 * <FilterForm wrappedComponentRef={this.filterFormRef} />
 * const data = getRefFormData(this.filterFormRef);
 * @param {React.RefObject<Form>} ref - 表单的 ref
 * @return {object}
 */
export function getRefFormData(ref) {
  if (ref.current) {
    const { form } = ref.current.props;
    return form.getFieldsValue();
  }
  return {};
}

export function getPlainNode(nodeList, parentPath = '') {
  const arr: any = [];
  nodeList.forEach((node) => {
    const item = node;
    item.path = `${parentPath}/${item.path || ''}`.replace(/\/+/g, '/');
    item.exact = true;
    if (item.children && !item.component) {
      arr.push(...getPlainNode(item.children, item.path));
    } else {
      if (item.children && item.component) {
        item.exact = false;
      }
      arr.push(item);
    }
  });
  return arr;
}

/**
 * 获取行内编辑表格中的 form
 * @param {array} dataSource - 表格数据源
 */
export function getEditTableForm(dataSource) {
  const formList = [];
  const fetchForm = (source, list) => {
    if (Array.isArray(source)) {
      source.forEach((item) => {
        if (item.$form) {
          list.push(item.$form);
        }
        if (item.children && Array.isArray(item.children)) {
          fetchForm(item.children, list);
        }
      });
    }
  };
  fetchForm(dataSource, formList);
  return formList;
}

/**
 * 获取行内编辑表格中的 form
 * @param {array} dataSource - 表格数据源
 * @param {array} filterList - 过滤新增操作中的属性字段，例如：['children', 'unitId']，默认过滤 $form
 * @param {object} scrollOptions - 配置form效验报错后的滚动行为，
 * - 默认是基于页面滚动，如果需要基于表格内滚动，
 * - 需要：{ container: document.querySelector('.ant-table-body') }，同时需要设置Y轴滚动
 * @param {string} treeChildrenAlias = 'children' - 指定树形结构行内编辑的子节点名称
 */
export function getEditTableData(
  dataSource = [],
  filterList = [],
  scrollOptions = {},
  treeChildrenAlias = 'children'
) {
  const paramsList: any[] = [];
  const errList: any[] = [];
  const fetchForm = (source, list) => {
    if (Array.isArray(source)) {
      for (let i = 0; i < source.length; i++) {
        if (source[i].$form && source[i]._status) {
          source[i].$form.validateFieldsAndScroll(
            { scroll: { allowHorizontalScroll: true }, ...scrollOptions },
            (err, values) => {
              if (!err) {
                const { $form, ...otherProps } = source[i];
                if (Array.isArray(filterList) && filterList.length > 0) {
                  for (const name of filterList) {
                    // 如果record中存在需要过滤的值，且是新增操作，执行过滤，默认过滤$form
                    // eslint-disable-next-line
                    if (source[i][name] && source[i]._status === 'create') {
                      delete otherProps[name];
                      // eslint-disable-next-line
                      delete values[name];
                    }
                  }
                }
                list.push({ ...otherProps, ...values });
              } else {
                // 捕获表单效验错误
                errList.push(err);
              }
              return err;
            }
          );
        }
        if (source[i][treeChildrenAlias] && Array.isArray(source[i][treeChildrenAlias])) {
          fetchForm(source[i][treeChildrenAlias], list);
        }
      }
    }
  };
  fetchForm(dataSource, paramsList);
  return errList.length > 0 ? [] : paramsList;
}

export function getRequestId() {
  const time = moment().format('x').split('').reverse().join('');
  const time1 = time.substring(0, 3);
  const time2 = time.substring(3, 7);
  const time3 = time.substring(7);
  const id = uuid().replace(/-/g, '');
  const uuid1 = id.substring(0, 12);
  const uuid2 = id.substring(12);

  return time1 + uuid1 + time2 + uuid2 + time3;
}
