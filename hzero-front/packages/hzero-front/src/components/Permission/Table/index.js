import React, { cloneElement } from 'react';
import { Bind } from 'lodash-decorators';

import { getResponse } from 'utils/utils';
import { checkPermission } from '../../../services/api';

/**
 * 处理表格列，检查权限
 */
export default class TablePermission extends React.Component {
  state = {
    checkColumns: [],
  };

  codeQueue = new Set();

  // eslint-disable-next-line
  componentDidMount() {
    this.check();
  }

  componentWillUnmount() {
    this.codeQueue.clear();
  }

  @Bind()
  check() {
    const { children } = this.props;
    const {
      props: { columns = [] },
    } = children;
    const checkColumns = [];

    // 收集列中的权限集，存放在队列中
    columns.forEach((item) => {
      const { permissionList } = item;
      if (permissionList && Array.isArray(permissionList)) {
        permissionList.forEach((key) => {
          this.codeQueue.add(key.code.replace(/^\//g, '').replace(/\//g, '.').replace(/:/g, '-'));
        });
      }
    });

    if (this.codeQueue.size > 0) {
      checkPermission(Array.from(this.codeQueue)).then((data) => {
        if (getResponse(data)) {
          // 将列中的权限集与鉴权结果做比对，去除鉴权失败的列
          columns.forEach((items) => {
            data.some(({ code, approve }) => {
              if (
                items.permissionList === undefined ||
                // approved=true，则controllerType=disabled则禁用，如果为hidden，则隐藏。非这两个值，则不控制
                (items.permissionList.some(
                  (key) =>
                    key.code.replace(/^\//g, '').replace(/\//g, '.').replace(/:/g, '-') === code
                ) &&
                  approve)
              ) {
                checkColumns.push(items);
              }
              return (
                items.permissionList === undefined ||
                (items.permissionList.some((key) => key.code === code) && approve)
              );
            });
          });
        }
        this.setState({ checkColumns });
      });
    } else {
      this.setState({ checkColumns: columns });
    }
  }

  @Bind()
  extendProps() {
    const { children } = this.props;
    const { checkColumns } = this.state;
    return cloneElement(children, { columns: checkColumns });
  }

  render() {
    return this.extendProps();
  }
}
