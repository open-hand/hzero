import React, { useState, useEffect, useRef } from 'react';
import ReactDOM from 'react-dom';
import { PerformanceTable } from 'choerodon-ui/pro';
import { Checkbox, Icon } from 'choerodon-ui';
import { slice, remove, isUndefined, isEmpty, cloneDeep } from 'lodash';

import styles from './index.less';

export default function VirtualTable(props) {
  const tableRef = useRef();
  const {
    columns,
    expandedRowKeys = [],
    onExpandChange = (e) => e,
    rowKey = 'id',
    data,
    isTree,
    height,
    minHeight = 100,
    childrenColumnName = 'children',
    rowSelection = {},
    shouldUpdateScroll = true,
    rowHeight = 36,
    autoInitHeight,
    parentClassName,
    isIndeterminate = false,
  } = props;
  const [trueDataSource, setTrueDataSource] = useState(data);
  const [expandKeys, setExpandKeys] = useState(expandedRowKeys);
  const [selectKeys, setSelectKeys] = useState(rowSelection.selectedRowKeys);
  const [selectRows, setSelectRows] = useState(rowSelection.selectedRowKeys);
  const [selectAllKeys, setSelectAllKeys] = useState([]);
  const [height2, setHeight] = useState(height);
  const [pathMap, setPathMap] = useState({});
  const [scrollToTopFlag, setScrollToTopFlag] = useState(false);

  const mergedColumns = columns.map((column, index) => {
    if (index === 0 && isTree) {
      const tempColumn = cloneDeep(column);
      const tempRender = column.render;
      if (column.render) {
        tempColumn.render = (columnData) => {
          const { rowData: record, rowIndex } = columnData;
          return (
            <span
              style={{
                paddingLeft: record[childrenColumnName]
                  ? (record.INDENT_INDEX || 0) * 12 + 8
                  : (record.INDENT_INDEX || 0) * 12 + 34,
              }}
            >
              {/* eslint-disable-next-line no-nested-ternary */}
              {record[childrenColumnName] ? (
                expandKeys.includes(record[rowKey]) ? (
                  <Icon
                    type="indeterminate_check_box-o"
                    className={styles['expand-icon']}
                    onClick={() => {
                      handleExpand(false, record, rowIndex);
                    }}
                  />
                ) : (
                  <Icon
                    className={styles['expand-icon']}
                    type="add_box-o"
                    onClick={() => {
                      handleExpand(true, record, rowIndex);
                    }}
                  />
                )
              ) : null}
              <span />
              {tempRender(columnData)}
            </span>
          );
        };
      } else {
        tempColumn.render = ({ rowData: record, dataIndex, rowIndex }) => {
          const val = record[dataIndex];
          return (
            <span
              style={{
                paddingLeft: record[childrenColumnName]
                  ? (record.INDENT_INDEX || 0) * 12 + 8
                  : (record.INDENT_INDEX || 0) * 12 + 34,
              }}
            >
              {/* eslint-disable-next-line no-nested-ternary */}
              {record[childrenColumnName] ? (
                expandKeys.includes(record[rowKey]) ? (
                  <Icon
                    className={styles['expand-icon']}
                    type="indeterminate_check_box-o"
                    onClick={() => {
                      handleExpand(false, record, rowIndex);
                    }}
                  />
                ) : (
                  <Icon
                    className={styles['expand-icon']}
                    type="add_box-o"
                    onClick={() => {
                      handleExpand(true, record, rowIndex);
                    }}
                  />
                )
              ) : null}
              <span />
              {val}
            </span>
          );
        };
      }
      return tempColumn;
    }
    return column;
  });

  useEffect(() => {
    if (autoInitHeight) {
      // eslint-disable-next-line
      const childrenRefWrapperDom = ReactDOM.findDOMNode(tableRef.current);
      const childrenRefDom = childrenRefWrapperDom;
      const { top: offsetTop } = childrenRefDom.getBoundingClientRect();
      if (parentClassName) {
        const parentRefDom = document.querySelector(`.${parentClassName}`);
        const { top } = parentRefDom.getBoundingClientRect();
        setHeight(
          minHeight > parentRefDom.offsetHeight - (offsetTop - top) - 10
            ? minHeight
            : parentRefDom.offsetHeight - (offsetTop - top) - 10
        );
      } else {
        setHeight(
          minHeight > window.innerHeight - offsetTop - 30
            ? minHeight
            : window.innerHeight - offsetTop - 30
        );
      }
    }
  }, []);

  useEffect(() => {
    if (isUndefined(expandedRowKeys) && !isTree) {
      setTrueDataSource(data);
    } else {
      const { rowKeys } = flatTree(data, [], expandedRowKeys);
      setTrueDataSource(rowKeys);
    }
    if (!isUndefined(rowSelection.selectedRowKeys)) {
      const { rowKeys } = selectAllTree(data, []);
      setSelectAllKeys(rowKeys);
    }
    if (tableRef.current && shouldUpdateScroll) {
      tableRef.current.scrollTop(0);
    }
  }, [data]);

  useEffect(() => {
    setScrollToTopFlag(
      Math.abs(expandedRowKeys.length - expandKeys.length) > 1 && tableRef.current
    );
    setExpandKeys(expandedRowKeys);
  }, [expandedRowKeys]);

  useEffect(() => {
    const { rowKeys } = flatTree(data, [], expandedRowKeys);
    setTrueDataSource(rowKeys, (prev) => {
      if (scrollToTopFlag) {
        tableRef.current.scrollTop(0);
      }
      if (
        tableRef.current &&
        -tableRef.current.minScrollY - (prev.length - rowKeys.length) * rowHeight <
          -tableRef.current.scrollY - height
      ) {
        tableRef.current.scrollTop(0);
      }
      setScrollToTopFlag(false);
    });
  }, [expandKeys]);

  useEffect(() => {
    setSelectKeys(rowSelection.selectedRowKeys);
  }, [rowSelection.selectedRowKeys]);

  useEffect(() => {
    const { pathMap2, rowKeys } = selectTree(data, [], selectKeys);
    if (isIndeterminate) {
      setPathMap(pathMap2);
    }
    setSelectRows(rowKeys);
  }, [selectKeys]);

  const SelectColumn = !isEmpty(rowSelection) && {
    title: (
      <Checkbox
        checked={selectAllKeys.length !== 0 && selectKeys.length === selectAllKeys.length}
        onChange={(e) => {
          if (e.target.checked) {
            rowSelection.onChange(
              selectAllKeys.map((item) => item[rowKey]),
              selectAllKeys
            );
          } else {
            rowSelection.onChange([], []);
          }
        }}
        indeterminate={!isEmpty(selectKeys) && selectKeys.length !== selectAllKeys.length}
      />
    ),
    dataIndex: '',
    width: 60,
    align: 'center',
    render: ({ rowData: record }) => {
      const val = record[rowKey];
      return (
        <Checkbox
          checked={selectKeys.includes(val)}
          indeterminate={
            isIndeterminate &&
            selectKeys.includes(val) &&
            !(pathMap[val] || []).every((item) => selectKeys.includes(item))
          }
          onChange={(e) => {
            if (e.target.checked) {
              rowSelection.onChange([...selectKeys, val], [...selectRows, record]);
              rowSelection.onSelect(record, e.target.checked, [...selectRows, record], e);
            } else {
              rowSelection.onChange(
                selectKeys.filter((item) => item !== val),
                selectRows.filter((item) => item[rowKey] !== val)
              );
              rowSelection.onSelect(
                record,
                e.target.checked,
                selectRows.filter((item) => item[rowKey] !== val),
                e
              );
            }
          }}
        />
      );
    },
  };

  const handleExpand = (flag, record, rowIndex) => {
    onExpandChange(flag, record);
    if (isUndefined(expandedRowKeys) && record[childrenColumnName]) {
      if (flag) {
        setExpandKeys(Array.from(new Set(expandKeys.concat(record[rowKey]))));
        setTrueDataSource(
          slice(trueDataSource, 0, rowIndex + 1)
            .concat(
              record[childrenColumnName].map((item) => {
                return { ...item, INDENT_INDEX: (record.INDENT_INDEX || 0) + 1 };
              })
            )
            .concat(slice(trueDataSource, rowIndex + 1, trueDataSource.length))
        );
      } else {
        const { rowKeys, i } = literateTree([record], [], expandKeys);
        let length = 0;
        i.forEach((v) => {
          length += v;
        });
        setExpandKeys(
          Array.from(
            new Set(
              remove(expandKeys, (item) => {
                return !rowKeys.includes(item);
              })
            )
          )
        );
        setTrueDataSource(
          slice(trueDataSource, 0, rowIndex + 1).concat(
            slice(trueDataSource, rowIndex + 1 + length, trueDataSource.length)
          )
        );
      }
    }
  };

  const literateTree = (collections = [], rowKeys = [], expandArr = [], i = []) => {
    const arr = rowKeys;
    const j = i;
    const renderTree = collections.map((item) => {
      const temp = item;
      if (temp[childrenColumnName] && expandArr.includes(temp[rowKey])) {
        arr.push(temp[rowKey]);
        j.push(temp[childrenColumnName].length);
        temp[childrenColumnName] = [
          ...literateTree(temp[childrenColumnName] || [], arr, expandArr, j).renderTree,
        ];
      }
      return temp;
    });
    return {
      renderTree,
      rowKeys,
      expandArr,
      i,
    };
  };

  const flatTree = (collections = [], rowKeys = [], expandArr = [], INDENT_INDEX = -1) => {
    const arr = rowKeys;
    const renderTree = collections.map((item) => {
      const temp = item;
      arr.push({ ...temp, INDENT_INDEX: (INDENT_INDEX || 0) + 1 });
      if (temp[childrenColumnName] && expandArr.includes(temp[rowKey])) {
        temp[childrenColumnName] = [
          ...flatTree(temp[childrenColumnName] || [], arr, expandArr, (INDENT_INDEX || 0) + 1)
            .renderTree,
        ];
      }
      return temp;
    });
    return {
      renderTree,
      rowKeys,
      expandArr,
      INDENT_INDEX,
    };
  };

  const selectTree = (collections = [], rowKeys = [], selectArr = [], levelPath = {}) => {
    const arr = rowKeys;
    const pathMap2 = levelPath;
    const renderTree = collections.map((item) => {
      const temp = item;
      pathMap2[temp[rowKey]] = [
        temp[rowKey],
        ...(temp[childrenColumnName] || []).map((res) => res[rowKey]),
      ];
      if (selectArr.includes(temp[rowKey])) {
        arr.push(temp);
      }
      if (temp[childrenColumnName]) {
        temp[childrenColumnName] = [
          ...selectTree(temp[childrenColumnName] || [], arr, selectArr, pathMap2).renderTree,
        ];
      }
      return temp;
    });
    return {
      renderTree,
      rowKeys,
      selectArr,
      pathMap2,
    };
  };

  const selectAllTree = (collections = [], rowKeys = []) => {
    const arr = rowKeys;
    const renderTree = collections.map((item) => {
      const temp = item;
      arr.push(temp);
      if (temp[childrenColumnName]) {
        temp[childrenColumnName] = [
          ...selectAllTree(temp[childrenColumnName] || [], arr).renderTree,
        ];
      }
      return temp;
    });
    return {
      renderTree,
      rowKeys,
    };
  };

  return (
    <PerformanceTable
      className="virtual-table"
      {...props}
      ref={tableRef}
      isTree={false}
      virtualized
      data={trueDataSource}
      columns={isEmpty(rowSelection) ? mergedColumns : [SelectColumn, ...mergedColumns]}
      pagination={false}
      shouldUpdateScroll={false}
      height={height2}
    />
  );
}
