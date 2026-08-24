/**
 * 检查用户是否没有修改过密码, 绑定过手机号 邮箱
 * 如果不满足 则 在第一次登录 提示用户去个人中心修改
 */
import React from 'react';
import { Modal, Button } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { getAsset } from '@hzero-front-ui/cfg';

import intl from 'utils/intl';
import { getSession, setSession, getResponse } from 'utils/utils';

import { queryStaticText } from '../../../services/api';
import toastImg from '../../../assets/illustrate-toast.svg';

import styles from './index.less';

class DefaultCheckUserSafe extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      // needShowModal: false,
    };
  }

  componentDidMount() {
    const {
      currentUser,
      currentUser: { changePasswordFlag },
    } = this.props;
    const infoCheckFlag = getSession('infoCheckFlag');
    if ((!infoCheckFlag && currentUser) || changePasswordFlag === 1) {
      const { emailCheckFlag, phoneCheckFlag, passwordResetFlag } = currentUser;
      if (
        (emailCheckFlag === 0 ||
          phoneCheckFlag === 0 ||
          passwordResetFlag === 0 ||
          changePasswordFlag === 1) &&
        !infoCheckFlag
      ) {
        const nextState = {
          selfModalVisible: true,
          // needShowModal: true,
          passwordResetFlag,
          emailCheckFlag,
          phoneCheckFlag,
        };
        queryStaticText('USER_PROMPT')
          .then(res => {
            const staticRes = getResponse(res);
            // info 如果 没有模版， 那么不需要弹出提示
            if (staticRes && staticRes.text) {
              nextState.selfModalDangerouslySetInnerHTML = {
                __html: staticRes.text,
              };
            }
          })
          .finally(() => {
            this.setState(nextState);
            setSession('infoCheckFlag', true);
          });
      }
    } else {
      // const { needShowModal } = this.state;
      // if(needShowModal) {
      //   this.setState({needShowModal: false});
      // }
    }
  }

  renderCheckModal() {
    const {
      selfModalDangerouslySetInnerHTML,
      selfModalVisible,
      phoneCheckFlag,
      emailCheckFlag,
      passwordResetFlag,
    } = this.state;
    const {
      gotoTab,
      currentUser: { popoutReminderFlag = 1, changePasswordFlag },
    } = this.props;
    return (
      <Modal
        visible={popoutReminderFlag && selfModalVisible}
        wrapClassName={styles['self-modal']}
        footer={null}
        width={468}
        onCancel={() => this.setState({ selfModalVisible: false })}
      >
        <div className="self-modal-header">
          <img
            src={getAsset('remind') || toastImg}
            alt="toast-img"
            style={{ width: '100%', height: 'auto' }}
          />
        </div>
        <div className="self-modal-content">
          {changePasswordFlag ? (
            <div>
              <p>{intl.get('hzero.common.basicLayout.greetMessage').d('尊敬的用户您好')}，</p>
              <p>
                {intl
                  .get('hzero.common.basicLayout.passwordExpireMsg')
                  .d(
                    '您的密码即将到期，为保证消息的正常接收及您的账户安全，和后续的正常使用，请前往'
                  )}
                <span className="user-info">
                  {intl.get('hzero.common.basicLayout.userInfo').d('个人中心')}
                </span>
                {intl.get('hzero.common.basicLayout.safeMessage2').d('进行修改。')}
              </p>
            </div>
          ) : selfModalDangerouslySetInnerHTML ? (
            // eslint-disable-next-line react/no-danger
            <div dangerouslySetInnerHTML={selfModalDangerouslySetInnerHTML} />
          ) : (
            <div>
              <p>{intl.get('hzero.common.basicLayout.greetMessage').d('尊敬的用户您好')}，</p>
              <p>
                {intl.get('hzero.common.basicLayout.accountNoBind').d('系统检测到您的账号尚未绑定')}
                {phoneCheckFlag === 0 && intl.get('hzero.common.phone').d('手机')}
                {emailCheckFlag === 0 &&
                  `${phoneCheckFlag === 0 ? '、' : ''}${intl.get('hzero.common.email').d('邮箱')}`}
                {passwordResetFlag === 0 &&
                  intl.get('hzero.common.basicLayout.passwordReset').d('、系统密码为初始密码，')}
                {intl
                  .get('hzero.common.basicLayout.safeMessage1')
                  .d('为保证消息的正常接收及您的账户安全，请前往')}
                <span className="user-info">
                  {intl.get('hzero.common.basicLayout.userInfo').d('个人中心')}
                </span>
                {intl.get('hzero.common.basicLayout.safeMessage2').d('进行修改。')}
              </p>
            </div>
          )}
        </div>
        <div className="self-modal-footer">
          <Button
            type="primary"
            className="go-info"
            onClick={() => {
              gotoTab({ pathname: '/hiam/user/info' });
              this.setState({ selfModalVisible: false });
            }}
          >
            {intl.get('hzero.common.basicLayout.userInfo').d('个人中心')}
          </Button>
        </div>
      </Modal>
    );
  }

  render() {
    // const { needShowModal } = this.state;
    // if (needShowModal) {
    return this.renderCheckModal();
    // } else {
    // return null;
    // }
  }
}

export default connect(
  ({ user = {} }) => ({
    currentUser: user.currentUser,
  }),
  dispatch => ({
    gotoTab: (location, state) => dispatch(routerRedux.push(location, state)),
  })
)(DefaultCheckUserSafe);
