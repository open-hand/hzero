import React, { useState, useEffect } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, Radio, Icon, Modal, Tooltip, notification, Row, Col } from 'hzero-ui';
import { Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import Spin from '@hzero-front-ui/hzero-ui/lib/Spin';
import HomeLogoText from '../../../assets/market/market-logo-text.svg';
import HomeBackground from '../../../assets/market/market-home-bg.png';
import ImgAvatar from '../../../assets/market/avatar-circle.svg';
import ImgProductList from '../../../assets/market/productlist.svg';
import ImgServiceList from '../../../assets/market/serviceList.svg';
import UserTermsModal from '../components/UserTermsModal';
import MainCardList from './components/MainCardList';
import ProductList from './components/ProductList';
import ServiceList from './components/ServicesList';
import Notification from './components/Notification';
import LoginModal from '../components/LoginModal';
import {
  queryMarketConfig,
  saveMarketConfig,
  getMarketUserInfo,
  marketUserLogout,
} from './services';
import { MARKET_USER_INFO_KEY } from '../utils/constants.js';
import { fetchProductList } from '../ProductList/services';
import { fetchServiceList, marketUserLogin, queryHzeroVersion } from '../ServiceList/services';
import styles from './index.less';

// const PRODUCT_LIST_JUMP_URL = '/market-client/product-list';
// const SERVICE_LIST_JUMP_URL = '/market-client/services-list';

function MarketHome({ dispatch, modelsIsAgrees }) {
  const [loading, setLoading] = useState(true);
  const [marketUserInfo, setMarketUserInfo] = useState({});
  const [marketConfig, setMarketConfig] = useState({});
  const [loginModalVisible, setLoginModalVisible] = useState(false);
  const [isAgree, setIsAgree] = useState(false);
  const [tempIsAgree, setTempIsAgree] = useState(false);
  const [isShowIcon, setIsShowIcon] = useState(false);
  const [ruleModalVisible, setRuleModalVisible] = useState(false);
  const [productList, setProductList] = useState([]); // 产品列表
  const [servicesList, setServicesList] = useState([]); // 服务列表
  const [hzeroVersion, setHzeroVersion] = useState([]);

  useEffect(() => {
    init();
  }, []);

  const isLogin = !!marketUserInfo?.realName;

  const _setIsAgree = (value) => {
    dispatch({
      type: 'market/updateState',
      payload: { isAgree: value },
    });
    setIsAgree(value);
  };

  // 初始化数据
  const init = async () => {
    setLoading(true);
    if (typeof modelsIsAgrees === 'boolean') {
      setLoading(false);
      setIsAgree(modelsIsAgrees);
    } else {
      queryMarketConfig().then((res) => {
        setLoading(false);
        if (res && !res.failed) {
          setMarketConfig(res);

          /**
           * 1.第一次 进来 joinFlag 没有值；直接弹出模态框
           * 2.joinFlag = false 不同意收集，展示操作按钮
           * 3.joinFlag = true 同意收集。不展示操作按钮
           */
          if (typeof res.joinFlag === 'boolean') {
            _setIsAgree(res.joinFlag);
          } else if (!res.joinFlag && res.joinFlag !== false) {
            setRuleModalVisible(true);
            _setIsAgree(true);
          }
          setIsShowIcon(res.joinFlag !== true);
        }
      });
    }

    // 默默获取一次用户信息
    getMarketUserInfo((res) => {
      if (res && !res.failed) {
        setMarketUserInfo(res);
      }
      sessionStorage.setItem(MARKET_USER_INFO_KEY, JSON.stringify(res || ''));
    });

    // 获取产品列表
    fetchProductList().then((res) => {
      if (Array.isArray(res)) {
        setProductList(res.slice(0, 2));
      }
    });

    // 获取服务列表
    fetchServiceList().then((res) => {
      if (Array.isArray(res)) {
        setServicesList(res.slice(0, 4));
      }
    });

    // 获取Hzero版本
    queryHzeroVersion().then((res) => {
      if (res && !res.failed) {
        setHzeroVersion(res);
      }
    });
  };

  // radio 按钮值改变
  const handleRadioChange = () => {
    setRuleModalVisible(true);
  };

  // 模态框的条款值改变
  const handleRuleModalResult = (value) => {
    saveMarketConfig({ ...marketConfig, joinFlag: value }).then((res) => {
      if (res && !res.failed) {
        setTempIsAgree(value);
        setRuleModalVisible(false);
      }
    });
  };

  // 退出确认
  const logoutConfirmModal = () => {
    Modal.confirm({
      title: intl.get('hadm.marketclient.view.modal.log.out').d('确认退出登录？'),
      content: intl
        .get('hadm.marketclient.view.modal.log.out.description')
        .d('退出后，我们将无法帮您获取最新版本产品、服务信息。'),
      style: { marginTop: '13%' },
      onOk() {
        marketUserLogout().then((res) => {
          if (res) {
            sessionStorage.setItem(MARKET_USER_INFO_KEY, '');
            setMarketUserInfo({});
            notification.success({
              message: intl.get('hzero.common.notification.success').d('操作成功'),
            });
          }
        });
      },
      onCancel() {},
    });
  };

  // 判断是否显示 ’我同意‘ 按钮
  const isShowAgreeRule = () => {
    if (marketConfig && typeof marketConfig.joinFlag === 'boolean') {
      return !marketConfig.joinFlag;
    }
    return true;
  };

  const onLogin = () => {
    setLoginModalVisible(true);
  };

  const onLoginOk = () => {
    init();
  };

  const toAll = (path) => {
    dispatch(
      routerRedux.push({
        pathname: path,
      })
    );
  };

  return (
    <div className={styles.home}>
      <Spin spinning={loading}>
        {/* eslint-disable-next-line no-nested-ternary */}
        {!loading ? (
          isAgree ? (
            <div className={styles.wrapper}>
              <Row>
                <Col span={16}>
                  <div className={`${styles['card-left']} ${styles.logo}`}>
                    <img src={HomeLogoText} alt="logo" />
                    <span className={styles.line} />
                    <div className={styles.description}>
                      <div
                        dangerouslySetInnerHTML={{
                          __html: intl
                            .get('hadm.marketclient.view.home.company.info')
                            .d('企业级应用产品一体化采购平台<br />领赢数字化转型之战'),
                        }}
                      />
                    </div>
                  </div>
                  <div className={`${styles['card-left']} ${styles['main-card']}`}>
                    <MainCardList
                      icon={ImgProductList}
                      title={intl.get('hadm.marketclient.view.home.productList').d('产品列表')}
                      toAll={() => toAll('/market-client/product-list')}
                    >
                      {productList.map((product) => (
                        <ProductList productInfo={product} />
                      ))}
                    </MainCardList>
                    <MainCardList
                      icon={ImgServiceList}
                      title={intl.get('hadm.marketclient.view.home.service').d('已有服务')}
                      toAll={() => toAll('/market-client/services-list')}
                    >
                      <div className={styles['service-info']} style={{ marginTop: '15px' }}>
                        {intl
                          .get('hadm.marketclient.view.home.service.description1')
                          .d('当前您使用的是 HZERO微服务技术平台 ')}
                        {hzeroVersion.length ? ` v${hzeroVersion.join(', v')}` : null}
                      </div>
                      <div className={styles['service-info']}>
                        {intl
                          .get('hadm.marketclient.view.home.service.description2')
                          .d(
                            '点击「查看全部」查看全部服务列表并可以进行版本对比、离线包下载等操作'
                          )}
                      </div>
                      <ServiceList serviceList={servicesList} style={{ marginBottom: '12px' }} />
                    </MainCardList>
                  </div>
                </Col>
                <Col span={8}>
                  <div className={`${styles['card-right']} ${styles.login}`}>
                    <div className={styles.sign}>
                      <img
                        src={marketUserInfo.imageUrl || ImgAvatar}
                        alt=""
                        className={styles.avatar}
                      />
                      <div className={styles.content}>
                        <div className={styles.operation}>
                          {isLogin ? (
                            marketUserInfo.realName
                          ) : (
                            <span style={{ cursor: 'pointer' }} onClick={onLogin}>
                              {intl.get('hadm.marketclient.view.home.now.login').d('立即登录')}
                            </span>
                          )}
                        </div>
                        <div className={styles.description}>
                          {' '}
                          {isLogin
                            ? `Hi~${intl
                                .get('hadm.marketclient.view.home.welcome')
                                .d('欢迎来到应用市场')}`
                            : intl
                                .get('hadm.marketclient.view.home.login.description')
                                .d('登录账号享受更多权益')}
                        </div>
                      </div>
                    </div>
                    {isLogin && (
                      <Tooltip title={intl.get('hzero.common.message.loginOut').d('退出登录')}>
                        <span className={styles.out} onClick={logoutConfirmModal} />
                      </Tooltip>
                    )}
                  </div>
                  <div className={`${styles['card-right']} ${styles.notification}`}>
                    <Notification isLogin={isLogin} marketUserInfo={marketUserInfo} />
                  </div>
                </Col>
              </Row>
              <LoginModal
                marketUserLogin={marketUserLogin}
                loginModalVisible={loginModalVisible}
                onCancel={() => setLoginModalVisible(false)}
                onOk={onLoginOk}
              />
            </div>
          ) : (
            <Content style={{ position: 'relative' }}>
              <div className={styles['market-home-page']}>
                <div className={styles['logo-wrap']}>
                  <img src={HomeLogoText} alt="" />
                </div>
                <h1>
                  <a>
                    {marketUserInfo?.realName ? <span>{marketUserInfo.realName}，</span> : null}
                    <span>
                      {intl.get('hadm.marketclient.view.home.welcome').d('欢迎来到应用市场！')}
                    </span>
                  </a>
                  {marketUserInfo?.realName ? (
                    <Tooltip placement="top" title="退出登录">
                      <span
                        style={{ cursor: 'pointer', fontSize: '16px' }}
                        onClick={logoutConfirmModal}
                      >
                        <Icon type="logout" />
                      </span>
                    </Tooltip>
                  ) : null}
                </h1>
                <h2>
                  {intl
                    .get('hadm.marketclient.view.home.title')
                    .d('企业级应用产品一体化采购平台 为企业数字化转型助力')}
                </h2>
                <p>
                  <span>
                    {intl
                      .get('hadm.marketclient.view.home.description1')
                      .d(
                        '为了完善产品，避免缺陷，改善产品的用户体验，我们需要采集部分环境数据（包括本地产品版本、服务版本、崩溃日志等），汇总后统计这些数据以持续不断地提升产品的操作体验、运行性能。'
                      )}
                  </span>
                  <br />
                  <span>
                    {intl.get('hadm.marketclient.view.home.description2').d('请您仔细阅读')}
                  </span>
                  <a onClick={() => setRuleModalVisible(true)}>
                    《
                    {intl
                      .get('hadm.marketclient.view.home.description3')
                      .d('HZERO用户体验改进计划')}
                    》
                  </a>
                  <span>
                    {intl
                      .get('hadm.marketclient.view.home.description4')
                      .d(
                        '的具体内容，如果您不愿意加入该计划，取消选中“我同意加入《HZERO用户体验改进计划》”即可。'
                      )}
                  </span>
                </p>
                {isShowIcon ? (
                  <div>
                    <Radio checked={tempIsAgree} onClick={handleRadioChange} />
                    <a onClick={() => setRuleModalVisible(true)}>
                      {intl
                        .get('hadm.marketclient.view.home.agree.hzero')
                        .d('我同意加入《HZERO用户体验改进计划》')}
                    </a>
                  </div>
                ) : null}
                <div className={styles['btn-wrap']}>
                  <Button
                    type="primary"
                    size="large"
                    disabled={loading}
                    style={{ width: '200px' }}
                    onClick={() => _setIsAgree(true)}
                  >
                    {intl.get('hadm.marketclient.view.home.go').d('立即前往')}
                  </Button>
                </div>
              </div>
              <div className={styles['home-background']}>
                <img src={HomeBackground} alt="" />
              </div>
              <UserTermsModal
                visible={ruleModalVisible}
                editAble={isShowAgreeRule()}
                handleAgree={() => handleRuleModalResult(true)}
                handleDisagree={() => handleRuleModalResult(false)}
                handleCancel={() => setRuleModalVisible(false)}
              />
            </Content>
          )
        ) : null}
      </Spin>
    </div>
  );
}

function mapStateToProps(state) {
  return {
    modelsIsAgrees: state.market.isAgree,
  };
}

function mapDispatchToProps(dispatch) {
  return { dispatch };
}

export default formatterCollections({
  code: ['hadm.marketclient'],
})(connect(mapStateToProps, mapDispatchToProps)(MarketHome));
