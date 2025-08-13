/**
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
// import PropTypes from 'prop-types';
import { Button, Modal, Spin } from 'hzero-ui';
import { isEmpty, isFunction, map, noop, omit } from 'lodash';
import { Bind } from 'lodash-decorators';
// import { queryConfigsByPageCode } from 'hzero-front-hpfm/lib/services/uiPageService';
import { getCurrentOrganizationId, getResponse, isPromise } from 'utils/utils';
import request from 'utils/request';

import { dealObjectProps, getContextValue, get } from '../utils';
import { dynamicModalOmitProps, modalBtnSep } from '../config';

function queryConfigsByPageCode(organizationId, pageCode) {
  return request(`hpfm/v1/${organizationId}/ui-pages/common/${pageCode}`, {
    method: 'GET',
  });
}

export default class DynamicModal extends React.Component {
  ref = {};

  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      loading: false,
      // modalBtnProps: {}, // {footer: []} 按钮组
    };
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const nextState = {};
    if (nextProps.params && nextProps.params !== prevState.params) {
      nextState.params = nextProps.params;
    }
    if (nextProps.fields && nextProps.fields !== prevState.fields) {
      nextState.fields = nextProps.fields;
    }
    if (nextProps.pageCode && nextProps.pageCode !== prevState.pageCode) {
      nextState.pageCode = nextProps.pageCode;
      nextState.loading = true;
    }
    if (isEmpty(nextState)) {
      return null;
    }
    return nextState;
  }

  render() {
    const { type = 'drawer', destroyOnClose = true } = this.props;
    const { pageModalProps = {}, modalBtnProps = {} } = this.state;
    const typeProps = type === 'modal' ? modalProps : drawerProps;
    const dealProps = dealObjectProps(omit(this.props, dynamicModalOmitProps), this);
    this.dealProps = dealProps;
    const { visible, loading } = this.state;
    return (
      <Modal
        {...pageModalProps}
        {...typeProps}
        {...dealProps}
        {...modalBtnProps}
        visible={visible}
        onOk={this.handleOkBtnClick}
        onCancel={this.handleCancelBtnClick}
        destroyOnClose={destroyOnClose}
      >
        <Spin spinning={loading}>{this.renderDynamicComponents()}</Spin>
      </Modal>
    );
  }

  componentDidMount() {
    const { onRef } = this.props;
    const { pageCode } = this.state;
    if (isFunction(onRef)) {
      onRef(this);
    }
    if (pageCode) {
      this.updateModalPage();
    }
  }

  componentDidUpdate(prevProps, prevState) {
    // 根据 pageCode 更新 fields
    const { pageCode } = this.state;
    if (pageCode && pageCode !== prevState.pageCode) {
      this.updateModalPage();
    }
    if (this.props.modalBtns && this.props.modalBtns !== prevProps.modalBtns) {
      this.updateModalBtns();
    }
  }

  @Bind()
  updateModalPage() {
    const { pageCode } = this.state;
    const { organizationId = getCurrentOrganizationId() } = this.props;
    queryConfigsByPageCode(organizationId, pageCode)
      .then(res => {
        const pageConfig = getResponse(res) || {};
        if (pageConfig.script) {
          eval(pageConfig.script)(this); // eslint-disable-line
        }
        this.setState({
          fields: pageConfig.fields,
          pageModalProps: {
            title: pageConfig.description, // 页面描述
          },
        });
      })
      .then(() => {
        this.setState({ loading: false });
      });
  }

  @Bind()
  updateModalBtns() {
    const { modalBtns } = this.props;
    const that = this;
    const footerBtns = map(modalBtns, (modalBtn, index) => {
      const [keyAndDescription, type, contextPath] = (modalBtn || '').split(modalBtnSep);
      const key = keyAndDescription || index;

      async function modalBtnClick() {
        const func = getContextValue(that, contextPath);
        if (isFunction(func)) {
          try {
            await func();
            that.hide();
          } catch (e) {
            // 调用出错什么都不做
            // console.debug(e);
          }
        } else {
          // 没有绑定到方法就直接关闭
          that.hide();
        }
      }

      return (
        <Button key={key} type={type} onClick={modalBtnClick}>
          {keyAndDescription}
        </Button>
      );
    });
    this.setState({
      modalBtnProps: {
        footer: footerBtns,
      },
    });
  }

  renderDynamicComponents() {
    const { params = {}, fields = [] } = this.state;
    const DynamicComponent = get('DynamicComponent');
    return map(fields, field => (
      <DynamicComponent
        key={field.templateCode}
        context={this}
        onRef={this.handleRef(field.templateCode)}
        template={field}
        params={params}
      />
    ));
  }

  @Bind()
  handleRef(dynamicRef) {
    return dynamicComponent => {
      this.ref[dynamicRef] = dynamicComponent;
    };
  }

  @Bind()
  show() {
    const { beforeShow, afterShow } = this.dealProps;
    let afterShowCallback = afterShow;
    if (!isFunction(afterShow)) {
      afterShowCallback = noop;
    }
    if (isFunction(beforeShow)) {
      const beforeShowReturn = beforeShow();
      if (beforeShowReturn === false) {
        return false;
      }
      if (isPromise(beforeShowReturn)) {
        beforeShowReturn.then(
          () => {
            this.setState({ visible: true });
            afterShowCallback();
          },
          () => {}
        );
      } else {
        this.setState({ visible: true });
        afterShowCallback();
      }
    } else {
      this.setState({ visible: true });
      afterShowCallback();
    }
  }

  @Bind()
  hide() {
    const { beforeHide, afterHide } = this.dealProps;
    let afterHideCallback = afterHide;
    if (!isFunction(afterHide)) {
      afterHideCallback = noop;
    }
    if (isFunction(beforeHide)) {
      const beforeHideReturn = beforeHide();
      if (beforeHideReturn === false) {
        return false;
      }
      if (isPromise(beforeHideReturn)) {
        beforeHideReturn.then(() => {
          this.setState({ visible: false });
          afterHideCallback();
        });
      } else {
        this.setState({ visible: false });
        afterHideCallback();
      }
    } else {
      this.setState({ visible: false });
      afterHideCallback();
    }
  }

  @Bind()
  handleOkBtnClick() {
    const { onOk, afterOk } = this.dealProps;
    let afterOkCallback = afterOk;
    if (!isFunction(afterOk)) {
      afterOkCallback = noop;
    }
    if (isFunction(onOk)) {
      const onOkReturn = onOk();
      if (onOkReturn === false) {
        return false;
      }
      if (isPromise(onOkReturn)) {
        onOkReturn.then(
          () => {
            this.setState({ visible: false });
            afterOkCallback();
          },
          () => {
            this.setState({ visible: true });
          }
        );
      } else {
        this.setState({ visible: false });
        afterOkCallback();
      }
    } else {
      this.setState({ visible: false });
      afterOkCallback();
    }
  }

  @Bind()
  handleCancelBtnClick() {
    this.hide();
  }

  @Bind()
  init(params) {
    this.setState({ params });
  }
}

const modalProps = {};

const drawerProps = {
  wrapClassName: 'ant-modal-sidebar-right',
  transitionName: 'move-right',
};

// 暴露出去的方法
DynamicModal.exportFuncs = [];
DynamicModal.exportFuncsInfo = {};
