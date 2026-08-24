import React, { PureComponent } from 'react';
import { Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import SideBar from './SideBar';

export default class ModalForm extends PureComponent {
  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) onRef(this);
  }

  resetForm() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  onOk() {
    const { form, handleAdd } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      handleAdd(fieldsValue);
    });
  }

  renderForm() {
    return null;
  }

  render() {
    const {
      modalVisible,
      hideModal,
      confirmLoading = false,
      sideBar = false,
      title,
      ...otherProps
    } = this.props;
    const Component = sideBar ? SideBar : Modal;
    return (
      <Component
        title={title}
        visible={modalVisible}
        onCancel={hideModal}
        onOk={this.onOk}
        confirmLoading={confirmLoading}
        {...otherProps}
      >
        {this.renderForm()}
      </Component>
    );
  }
}
