import React from 'react';
import { forEach, isFunction, map, omit, split } from 'lodash';
import querystring from 'querystring';
import { withRouter } from 'dva/router';
import { Bind } from 'lodash-decorators';

import { dealButtonProps, getBtnType } from './utils';

import {
  ACTION_CODE,
  dynamicToolBarOmitProps,
  fieldLabelProp,
  fieldNameProp,
  PAGE_PARAM,
  subEventSep,
} from '../config';
import DynamicModal from '../DynamicModal';
import { getContextValue } from '../utils';
import { openPageModalBodyStyle, openTypeCode } from '../DynamicTable/utils';

@withRouter
export default class DynamicToolbar extends React.Component {
  state = {};

  pageModalRef;

  @Bind()
  handlePageModalRef(pageModalRef) {
    this.pageModalRef = pageModalRef;
  }

  componentDidMount() {
    const { onRef } = this.props;
    if (isFunction(onRef)) {
      onRef(this);
    }
  }

  render() {
    const otherProps = omit(this.props, dynamicToolBarOmitProps);
    return this.renderComposeToolBar(otherProps);
  }

  renderComposeToolBar({ fields, context, ...toolbarProps }) {
    // toolbarProps 已经在 DynamicComponent 中处理了
    const children = this.renderToolbarFields({ fields, context });
    const { pageModalProps = {} } = this.state;
    // 渲染 Toolbar 组件的属性与render
    return (
      <>
        <div {...toolbarProps}>{children}</div>
        <DynamicModal {...pageModalProps} key="pageModal" onRef={this.handlePageModalRef} />
      </>
    );
  }

  renderToolbarFields({ fields = [], context }) {
    return map(fields, field => {
      // 获取组件不依赖 Context 的属性
      // 获取对应的组件
      const ComponentType = getBtnType({ field });
      const { componentProps, btn: btnProps = {} } = dealButtonProps({ field, context });
      const buttonProps = {};
      switch (btnProps.action) {
        case ACTION_CODE.action:
          buttonProps.onClick = () => {
            const { actionEvent } = btnProps;
            const func = getContextValue(context, actionEvent);
            if (isFunction(func)) {
              func().catch((/* e */) => {
                // todo 失败不需要做事情?
                // console.error(e);
              });
            }
          };
          break;
        case ACTION_CODE.page:
          buttonProps.onClick = () => {
            const modalProps = {};
            const params = {};
            const {
              history: { search },
            } = this.props;
            const urlParams = querystring.parse(search);
            const paramStream = (btnProps.params || '').split(',');
            for (let i = 1; i <= paramStream.length; i++) {
              if (i % 3 === 0) {
                switch (paramStream[i - 2]) {
                  case PAGE_PARAM.fixParam:
                    params[paramStream[i - 3]] = paramStream[i - 1];
                    break;
                  case PAGE_PARAM.urlParam:
                    params[paramStream[i - 1]] = urlParams[paramStream[i - 3]];
                    break;
                  default:
                    break;
                }
              }
            }
            // 打开 Modal 或 页面
            switch (btnProps.openType) {
              case openTypeCode.inner:
                // 跳转
                break;
              case openTypeCode.modal:
                // 打开 Modal
                modalProps.type = openTypeCode.modal;
                modalProps.bodyStyle = openPageModalBodyStyle[btnProps.openPageTypeModal].bodyStyle;
                modalProps.width = openPageModalBodyStyle[btnProps.openPageTypeModal].width;
                modalProps.modalBtns = btnProps.modalBtns; // modal 按钮
                // 订阅事件
                if (btnProps.subEvents) {
                  forEach(btnProps.subEvents, subEvent => {
                    const [subEventListenStr, subEventActionStr] = split(subEvent, subEventSep);
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
                    const [subEventListenStr, subEventActionStr] = split(subEvent, subEventSep);
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
        default:
          break;
      }
      // TODO Button 的显示属性 最后看在哪里处理
      if (!componentProps.children && componentProps.text) {
        componentProps.children = componentProps.text;
      } else {
        // use [fieldLabelProp] as Button's text
        componentProps.children = field[fieldLabelProp];
      }
      return (
        <React.Fragment key={field[fieldNameProp]}>
          {React.createElement(ComponentType, { ...componentProps, ...buttonProps })}
        </React.Fragment>
      );
    });
  }
}

// 暴露出去的方法
DynamicToolbar.exportFuncs = [];
DynamicToolbar.exportFuncsInfo = {};
