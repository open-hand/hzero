/**
 * ButtonEditModal.js
 * @date 2018-12-12
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import ButtonEdit from './ButtonEdit';

const modalBodyStyle = {
  minHeight: 500,
  overflowY: 'auto',
};

export default class ButtonEditModal extends React.Component {
  buttonEditRef;

  render() {
    const { buttonEditProps, ...modalProps } = this.props;
    return (
      <Modal
        title="按钮动作"
        width={800}
        bodyStyle={modalBodyStyle}
        {...modalProps}
        onOk={this.handleModalOk}
        onCancel={this.handleModalCancel}
        destroyOnClose
      >
        <ButtonEdit {...buttonEditProps} onRef={this.refButtonEdit} />
      </Modal>
    );
  }

  @Bind()
  refButtonEdit(buttonEdit) {
    this.buttonEditRef = buttonEdit;
  }

  @Bind()
  handleModalOk() {
    if (this.buttonEditRef) {
      this.buttonEditRef
        .getValidateData()
        .then(validateData => {
          const { onOk } = this.props;
          if (onOk) {
            onOk(validateData);
          }
        })
        .catch(() => {
          // 校验出错
        });
    }
  }

  @Bind()
  handleModalCancel() {
    const { onCancel } = this.props;
    if (onCancel) {
      onCancel();
    }
  }
}
