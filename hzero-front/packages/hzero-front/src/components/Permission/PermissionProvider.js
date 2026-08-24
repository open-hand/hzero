import React from 'react';
import PropTypes from 'prop-types';

import { checkPermission } from 'services/api';

import { getResponse } from 'utils/utils';

import { FAILURE, SUCCESS } from './Status';

const DELAY = 500;

export default class PermissionProvider extends React.Component {
  // 向子组件传递context
  static childContextTypes = {
    permission: PropTypes.object,
  };

  getChildContext() {
    return {
      permission: this,
    };
  }

  // 定时器对象
  delayId = 0;

  // 存放所有需要验证的权限集
  allPermissionCodeList = new Set();

  // 存储权限集
  permissions = new Map();

  // 存储控制类型
  controllerTypes = new Map();

  // 待check的权限队列
  queue = new Set();

  // 存放回调
  handlers = new Set();

  /**
   * 请求接口验证权限集
   */
  fetchPermission() {
    const handlers = Array.from(this.handlers);
    checkPermission(Array.from(this.queue)).then((data) => {
      if (getResponse(data)) {
        data.forEach(({ code, approve, controllerType }) => {
          // 存储权限对象
          if (code) {
            this.permissions.set(code, approve ? SUCCESS : FAILURE);
            this.controllerTypes.set(code, controllerType);
          }
        });
        // 处理权限
        handlers.forEach(([props, handler]) => this.check(props, handler, true));
      }
    });
  }

  /**
   * 开始验证权限，处理延迟
   */
  start() {
    if (this.delayId) {
      clearTimeout(this.delayId);
    }
    this.delayId = setTimeout(() => {
      this.fetchPermission();
      this.queue.clear();
      this.handlers.clear();
    }, DELAY);
  }

  /**
   * 处理权限
   * @param {object} props - 参数
   * @param {function} handler - 回调函数
   * @param {boolean} flag - 标记
   */
  check(props, handler, flag) {
    // 设置了 code 属性才去验证权限，没设置权限将直接返回成功状态
    if (!props.permissionList || props.permissionList.length === 0) {
      handler(SUCCESS);
    } else {
      const { permissionList = [] } = props;
      const codeList = permissionList.map((item) =>
        item.code.replace(/^\//g, '').replace(/\//g, '.').replace(/:/g, '-')
      );
      const queue = new Set();
      // 遍历找到对应的需要验证权限的对象
      const isCheckThrough = codeList.every((item) => {
        if (item) {
          this.allPermissionCodeList.add(item);
          const key = item;
          // 获取对应待验证对象的权限状态
          const status = this.permissions.get(key);
          // 验证成功后执行回调函数，结束验证
          if (status === SUCCESS) {
            const ctlType = this.controllerTypes.get(item);
            handler(status, ctlType);
            return false;
          } else if (status !== FAILURE) {
            // 没有被验证的放入队列中等待下次验证
            this.queue.add(key);
            queue.add(key);
          }
        }
        return true;
      });
      if (isCheckThrough) {
        // 再一次验证剩余的权限
        if (queue.size > 0 && !flag) {
          this.handlers.add([props, handler]);
          this.start();
        } else {
          // 鉴权失败后执行回调函数
          codeList.some((item) => {
            const ctlType = this.controllerTypes.get(item) || undefined;
            handler(FAILURE, ctlType);
            return true;
          });
        }
      }
    }
  }

  render() {
    return this.props.children;
  }
}
