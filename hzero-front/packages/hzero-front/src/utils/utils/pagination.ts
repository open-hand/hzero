import { isArray, isEmpty, isNumber, isUndefined, pull, sortBy, uniq } from 'lodash';

import { PAGE_SIZE_OPTIONS } from '../constants';
import { totalRender } from '../renderer';

/**
 * 根据传入的数据列表对象，生成页面分页参数对象
 * @param {object} data - 数据列表对象
 * @returns {object} pagination- 分页对象
 */
export function createPagination(data, list) {
  if (data) {
    return {
      showSizeChanger: true,
      pageSizeOptions: isArray(list) && !isEmpty(list) ? list : PAGE_SIZE_OPTIONS,
      // showQuickJumper: true,
      current: (isNumber(data.number) ? data.number : data.start) + 1,
      pageSize: data.size, // 每页大小
      total: isNumber(data.totalElements) ? data.totalElements : data.total,
      showTotal: totalRender,
    };
  }
}

/**
 * 表格批量新增操作, 添加数据行，更新分页信息
 * @author WH <heng.wei@hand-china.com>
 * @param {number} length - 数据列表长度
 * @param {object} pagination - 原始分页对象
 * @returns {object} pagination - 分页对象
 */
export function addItemToPagination(length, pagination: any = {}) {
  const {
    total = 0,
    pageSize = 10,
    current = 1,
    sourceSize,
    pageSizeOptions = PAGE_SIZE_OPTIONS,
  } = pagination;
  const size = length === pageSize ? pageSize + 1 : pageSize;
  const source = isUndefined(sourceSize) ? pageSize : sourceSize;
  const pages = uniq([...pull([...pageSizeOptions], `${pageSize}`), `${size}`]);
  return {
    ...pagination,
    // 变更每页显示条数
    pageSizeOptions: sortBy(pages, item => +item),
    // 更新数据总量
    total: total + 1,
    // 记录pageSize的原始值
    sourceSize: source,
    // 根据 数据列表的长度与每页大小的情况，更新分页大小
    pageSize: size,
    // 变更showTotal信息
    showTotal: () =>
      totalRender(total + 1, [
        source * (current - 1) + 1,
        source * (current - 1) + size < total + 1 ? source * (current - 1) + size : total + 1,
      ]),
  };
}

/**
 * 表格批量新增操作, 移除数据行，更新分页信息
 * @author WH <heng.wei@hand-china.com>
 * @param {number} length - 数据列表长度
 * @param {object} pagination - 原始分页对象
 * @returns {object} pagination - 分页对象
 */
export function delItemToPagination(length, pagination: any = {}) {
  const {
    total = 1,
    pageSize = 10,
    current = 1,
    sourceSize,
    pageSizeOptions = PAGE_SIZE_OPTIONS,
  } = pagination;
  const size = length === pageSize ? pageSize - 1 : pageSize;
  const pages = uniq([...pull([...pageSizeOptions], `${pageSize}`), `${size}`]);
  return {
    ...pagination,
    // 更新数据总量
    total: total - 1,
    // 变更每页显示条数
    pageSizeOptions: sortBy(pages, item => +item),
    // 根据 数据列表的长度与每页大小的情况，更新分页大小
    pageSize: size,
    // 变更showTotal信息
    showTotal: () =>
      totalRender(total - 1, [
        sourceSize * (current - 1) + 1,
        sourceSize * (current - 1) + size < total - 1
          ? sourceSize * (current - 1) + size
          : total - 1,
      ]),
  };
}

/**
 * 表格批量新增操作, 添加数据行，更新分页信息
 * @author WH <heng.wei@hand-china.com> WY <yang.wang06@hand-china.com> (继承修改)
 * @param {number} addItemsLength - 新增数据长度
 * @param {number} currentLength - 当前数据长度
 * @param {object} pagination - 原始分页对象
 * @returns {object} pagination - 分页对象
 */
export function addItemsToPagination(addItemsLength, currentLength = 0, pagination: any = {}) {
  const {
    total = 0,
    pageSize = 10,
    current = 1,
    sourceSize,
    pageSizeOptions = PAGE_SIZE_OPTIONS,
  } = pagination;
  const nextDataLength = currentLength + addItemsLength;
  const nextTotal = total + addItemsLength;
  const size = nextDataLength >= pageSize ? nextDataLength : pageSize;
  const source = isUndefined(sourceSize) ? pageSize : sourceSize;
  const pages = uniq([...pull([...pageSizeOptions], `${pageSize}`), `${size}`]);
  return {
    ...pagination,
    // 变更每页显示条数
    pageSizeOptions: sortBy(pages, item => +item),
    // 更新数据总量
    total: nextTotal,
    // 记录pageSize的原始值
    sourceSize: source,
    // 根据 数据列表的长度与每页大小的情况，更新分页大小
    pageSize: size,
    // 变更showTotal信息
    showTotal: () =>
      totalRender(nextTotal, [
        source * (current - 1) + 1,
        source * (current - 1) + size < nextTotal ? source * (current - 1) + size : nextTotal,
      ]),
  };
}

/**
 * 表格批量新增操作, 移除数据行，更新分页信息
 * @author WH <heng.wei@hand-china.com> WY <yang.wang06@hand-china.com> (继承修改)
 * @param {number} delItemsLength - 新增数据长度
 * @param {number} currentLength - 当前数据长度
 * @param {object} pagination - 原始分页对象
 * @returns {object} pagination - 分页对象
 */
export function delItemsToPagination(delItemsLength, currentLength = 0, pagination: any = {}) {
  const {
    total = 0,
    pageSize = 10,
    current = 1,
    sourceSize = pageSize,
    pageSizeOptions = PAGE_SIZE_OPTIONS,
  } = pagination;
  const nextDataLength = currentLength - delItemsLength;
  const nextTotal = total - delItemsLength;
  const size = Math.max(nextDataLength <= pageSize ? nextDataLength : pageSize, sourceSize);
  const pages = uniq([...pull([...pageSizeOptions], `${pageSize}`), `${size}`]);
  return {
    ...pagination,
    // 更新数据总量
    total: nextTotal,
    // 变更每页显示条数
    pageSizeOptions: sortBy(pages, item => +item),
    // 根据 数据列表的长度与每页大小的情况，更新分页大小
    pageSize: size,
    // 变更showTotal信息
    showTotal: () =>
      totalRender(nextTotal, [
        sourceSize * (current - 1) + 1,
        sourceSize * (current - 1) + size < nextTotal
          ? sourceSize * (current - 1) + size
          : nextTotal,
      ]),
  };
}
