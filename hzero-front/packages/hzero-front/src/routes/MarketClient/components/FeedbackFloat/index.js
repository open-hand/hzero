import React, { useState } from 'react';
import { withRouter } from 'dva/router';
import notification from 'utils/notification';
import { openTab } from 'utils/menuTab';
import { connect } from 'dva';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { marketUserLogin, testConnect } from '../../ServiceList/services';
import ImgFeedback from '../../../../assets/market/feedback-new.svg';
import LoginModal from '../LoginModal';
import styles from './index.less';

const MARKET_USER_INFO_KEY = '__market_user_info_';
const FEED_BACK_PATH = '/market-client/feedback';

const FeedbackFloat = ({ menuList, parentMenuObj, feedbackFlag, activeTabKey: fromPathName }) => {
  const [loginModalVisible, setLoginModalVisible] = useState(false); // 是否显示登陆弹框

  const toMarketClientFeedback = () => {
    // history.push('/market-client/feedback');
    let menu = {};
    if (Object.keys(parentMenuObj).includes(fromPathName)) {
      // 查找父级路径对应的中文
      menu = parentMenuObj[fromPathName] || {};
    } else {
      // 查找子级路径对应的中文
      menu = menuList.find(({ path }) => path === fromPathName) || {};
    }
    const search = {};
    if (menu.title) {
      search.fromName = menu.title;
    }
    if (fromPathName) {
      search.fromPath = fromPathName;
    }

    openTab({
      title: intl.get('hadm.marketclient.view.feedback.title').d('问题反馈'),
      key: `${FEED_BACK_PATH}`,
      search,
    });
  };

  // 登录成功
  const onLoginOk = () => {
    toMarketClientFeedback();
  };

  // 判断是否登录
  const checkLoginStatus = () => {
    try {
      const userInfo = JSON.parse(sessionStorage.getItem(MARKET_USER_INFO_KEY)) || {};
      return userInfo.realName;
    } catch (e) {
      return false;
    }
  };

  const onFeedbackClick = async () => {
    // 1.联网检查
    const connectRes = await testConnect();
    if (connectRes && connectRes.flag === true) {
      // 可以连接外网
    } else {
      notification.error({ message: '此功能需连接外网' });
      return;
    }
    // 2.登录检查
    if (!checkLoginStatus()) {
      setLoginModalVisible(true);
      return;
    }
    // 3.跳转到反馈页面
    toMarketClientFeedback();
  };
  if (!feedbackFlag || fromPathName === FEED_BACK_PATH) return null;

  return (
    <div className={styles.feedback} id="__market_client_enter_">
      <img src={ImgFeedback} alt="feedback" onClick={onFeedbackClick} />
      <LoginModal
        marketUserLogin={marketUserLogin}
        loginModalVisible={loginModalVisible}
        onCancel={() => setLoginModalVisible(false)}
        onOk={onLoginOk}
      />
    </div>
  );
};

export default formatterCollections({
  code: ['hadm.marketclient'],
})(
  connect(({ global }) => ({
    parentMenuObj: global.routerData, // 主路由菜单
    menuList: global.menuLeafNode, // 叶子路由菜单
    feedbackFlag: global.feedbackFlag,
    activeTabKey: global.activeTabKey,
  }))(withRouter(FeedbackFloat))
);
