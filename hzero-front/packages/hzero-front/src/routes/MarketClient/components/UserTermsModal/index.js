import React from 'react';
import { Modal, Button } from 'hzero-ui';
import intl from 'utils/intl';
import { connect } from 'dva';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_USER_TERMS } from '../../utils/internationalization';

/**
 * 用户条款模态框
 * @param {boolean} visible - 是否显示弹框
 * @param {boolean} editAble - 是否可以编辑
 * @param {function} handleAgree - 处理同意按钮回调
 * @param {function} handleDisagree - 处理不同意按钮回调
 * @param {function} handleDisagree - 处理取消按钮回调
 */
function UserTermsModal({
  visible,
  editAble = false,
  handleAgree,
  handleDisagree,
  handleCancel,
  global: { hzeroUILocale },
}) {
  const _handleAgree = () => {
    if (handleAgree) {
      handleAgree();
    }
  };
  const _handleDisagree = () => {
    if (handleDisagree) {
      handleDisagree();
    }
  };
  const _handleCancel = () => {
    if (handleCancel) {
      handleCancel();
    }
  };
  const _getFooterArr = () => {
    if (editAble) {
      return [
        <Button type="default" onClick={_handleDisagree}>
          {intl.get('hadm.marketclient.button.clause.disagree').d('不同意')}
        </Button>,
        <Button type="primary" onClick={_handleAgree}>
          {intl.get('hadm.marketclient.button.clause.agree').d('同意')}
        </Button>,
      ];
    }
    return null;
  };

  return (
    <Modal
      width={807}
      maskClosable={false}
      title={intl.get('hadm.marketclient.view.clause.user').d('用户条款')}
      visible={visible}
      onCancel={_handleCancel}
      footer={_getFooterArr()}
      closable={!_getFooterArr()}
      bodyStyle={{ height: '600px', overflowY: 'scroll' }}
    >
      {HZERO_USER_TERMS[hzeroUILocale.locale === 'zh_CN' ? 'CN' : 'EN']}
    </Modal>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient'],
})(
  connect(({ global = {} }) => ({
    global,
  }))(UserTermsModal)
);
