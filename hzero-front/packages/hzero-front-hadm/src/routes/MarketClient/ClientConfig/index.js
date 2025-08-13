import React, { useEffect, useState } from 'react';
import { Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { Row, Col, Switch, Input, Button, Icon } from 'hzero-ui';
import notification from 'utils/notification';
import AppStoreIcon from '../../../assets/market/appstore.svg';
import PlanIcon from '../../../assets/market/plan.svg';
import FeedbackIcon from '../../../assets/market/feedback.png';
import UserTermsModal from './components/UserTermsModal';
import { queryMarketConfig, saveMarketConfig } from './services';
import styles from './index.less';

const KEY_MARKET_ENTER = 'KEY_MARKET_ENTER';
const KEY_PLAN = 'KEY_PLAN';
const KEY_FEEDBACK = 'KEY_FEEDBACK';

function ClientConfig() {
  const [marketConfig, setMarketConfig] = useState({});
  const [switchLoading, setSwitchLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [companyName, setCompanyName] = useState('');
  useEffect(() => {
    queryMarketConfig().then((res) => {
      if (res) {
        setMarketConfig(res);
        setCompanyName(res.companyName);
      }
    });
  }, []);

  // 处理市场按钮变更
  const handleMarketEnterChange = (val) => {
    saveConfig({ ...marketConfig, iconFlag: val }, KEY_MARKET_ENTER);
  };

  // 处理是否同意计划变更
  const handleRuleChange = (val) => {
    saveConfig({ ...marketConfig, joinFlag: val }, KEY_PLAN);
  };

  // 处理是否开启问题反馈
  const handleFeedbackChange = (val) => {
    saveConfig({ ...marketConfig, feedbackFlag: val }, KEY_FEEDBACK);
  };

  // 保存认证企业名称
  const handleSaveCompanyName = () => {
    saveConfig({ ...marketConfig, companyName });
    setIsEditing(false);
  };

  // 处理清空按钮
  const handleCancelButton = () => {
    setIsEditing(false);
    setCompanyName(marketConfig.companyName);
  };

  // 保存客户端配置数据
  const saveConfig = (data, loadingKey, cb) => {
    setSwitchLoading(loadingKey);
    saveMarketConfig(data).then((res) => {
      if (res) {
        setMarketConfig(res);
        setSwitchLoading(false);
        notification.success({
          message: intl.get('hzero.common.notification.success').d('操作成功'),
        });
        if (typeof cb === 'function') cb(data);
      }
    });
  };

  // 渲染按钮
  const renderEnterEnable = ({
    key,
    initVal,
    handleChange,
    openText = intl.get('hadm.marketclient.view.config.on').d('开启'),
    closeText = intl.get('hadm.marketclient.view.config.close').d('关闭'),
  }) => {
    return (
      <>
        <h3 className={styles['switch-text']}>{initVal ? openText : closeText}</h3>
        <Switch
          loading={switchLoading === key}
          checked={initVal}
          onChange={(val) => handleChange && handleChange(val)}
        />
      </>
    );
  };

  // 渲染顶部公司信息维护按钮
  const renderCompanyNameUI = () => {
    return (
      <div className={styles['company-wrap']}>
        <h3 className={styles['header-text']}>
          {intl.get('hadm.marketclient.view.config.company.auth').d('企业认证')}：
        </h3>
        {isEditing ? (
          <div className={styles['edit-wrap']}>
            <Input
              onChange={(val) => setCompanyName(val.target.value)}
              value={companyName}
              style={{ width: '300px', marginRight: '12px' }}
              placeholder={intl
                .get('hzero.c7nProUI.Validator.value_missing', {
                  label: intl.get('hadm.marketclient.view.config.company.name').d('认证企业名称'),
                })
                .d('请输入{label}')}
            />
            <Button style={{ marginRight: '12px' }} onClick={handleCancelButton}>
              {intl.get('hzero.common.button.cancel').d('取消')}
            </Button>
            <Button type="primary" onClick={handleSaveCompanyName}>
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          </div>
        ) : (
          <>
            {marketConfig?.companyName ? (
              <a>{marketConfig.companyName}</a>
            ) : (
              <span onClick={() => setIsEditing(true)} style={{ color: '#999' }}>
                {intl.get('hadm.marketclient.view.config.company.maintain').d('维护认证企业名称')}
              </span>
            )}
            <Icon
              style={{ color: '#999', marginLeft: '7px', fontSize: '14px' }}
              onClick={() => setIsEditing(true)}
              type="edit"
            />
          </>
        )}
      </div>
    );
  };

  const renderData = [
    [
      KEY_MARKET_ENTER,
      AppStoreIcon,
      [
        [
          <h3>{intl.get('hadm.marketclient.view.config.market.entry').d('应用市场入口')}</h3>,
          renderEnterEnable({
            initVal: marketConfig.iconFlag,
            handleChange: handleMarketEnterChange,
            openText: intl.get('hadm.marketclient.view.config.on').d('开启'),
            closeText: intl.get('hzero.common.button.disable').d('禁用'),
            key: KEY_MARKET_ENTER,
          }),
        ],
        [
          <span style={{ color: '#5A6677' }}>
            {intl
              .get('hadm.marketclient.view.config.market.description')
              .d('快速浏览产品、服务、组件，支持版本对比')}
          </span>,
          <span style={{ color: '#5A6677' }}>
            {marketConfig.iconFlag
              ? intl
                  .get('hadm.marketclient.view.config.market.description.off')
                  .d('关闭后，应用市场入口将不会在顶部导航中展示')
              : intl
                  .get('hadm.marketclient.view.config.market.description.on')
                  .d('打开后，系统管理员可通过顶部应用市场图标进入应用市场')}
          </span>,
        ],
      ],
    ],
    [
      KEY_PLAN,
      PlanIcon,
      [
        [
          <h3 onClick={() => setVisible(!visible)}>
            <a>
              《{intl.get('hadm.marketclient.view.home.description3').d('Hzero产品体验改进计划')}》
            </a>
          </h3>,
          renderEnterEnable({
            initVal: marketConfig.joinFlag,
            handleChange: handleRuleChange,
            openText: intl.get('hadm.marketclient.button.clause.agree').d('同意'),
            closeText: intl.get('hadm.marketclient.button.clause.disagree').d('不同意'),
            key: KEY_PLAN,
          }),
        ],
        [
          <span style={{ color: '#5A6677' }}>
            {intl
              .get('hadm.marketclient.view.config.hzero.description')
              .d('我们持续优化Hzero产品，为您提供更好的服务')}
          </span>,
          <span style={{ color: '#5A6677' }}>
            {marketConfig.joinFlag
              ? intl
                  .get('hadm.marketclient.view.config.hzero.description.off')
                  .d(
                    '若您不同意，我们将不会收集您的本地环境运行信息，同时也无法为您提供版本对比服务'
                  )
              : intl
                  .get('hadm.marketclient.view.config.hzero.description.on')
                  .d('若您同意，我们将为您提供本地服务版本对比服务')}
          </span>,
        ],
      ],
    ],
    [
      KEY_FEEDBACK,
      FeedbackIcon,
      [
        [
          <h3>{intl.get('hadm.marketclient.view.config.feedback.entry').d('问题反馈入口')}</h3>,
          renderEnterEnable({
            initVal: marketConfig.feedbackFlag,
            handleChange: handleFeedbackChange,
            openText: intl.get('hadm.marketclient.view.config.on').d('开启'),
            closeText: intl.get('hzero.common.button.disable').d('禁用'),
            key: KEY_FEEDBACK,
          }),
        ],
        [
          <>
            <div style={{ color: '#5A6677' }}>
              {intl
                .get('hadm.marketclient.view.config.feedback.description1')
                .d('提交工单至Hzero产品运维团队，高效快速处理您遇到的问题')}
            </div>
            <div>
              <a
                href="https://open.hand-china.com/personal-center/self-fd"
                target="_blank"
                rel="noopener noreferrer"
              >
                {intl
                  .get('hadm.marketclient.view.config.feedback.description2')
                  .d('查看我提交的工单')}
              </a>
            </div>
          </>,
          <span style={{ color: '#5A6677' }}>
            {marketConfig.feedbackFlag
              ? intl
                  .get('hadm.marketclient.view.config.feedback.description.off')
                  .d('关闭后，问题反馈按钮将不会在页面右下角显示')
              : intl
                  .get('hadm.marketclient.view.config.feedback.description.on')
                  .d('打开后，问题反馈按钮将会在页面右下角显示')}
          </span>,
        ],
      ],
    ],
  ];

  // 渲染配置列表
  const renderConfigItem = (item) => {
    const [key, icon, children = []] = item;
    return (
      <Row key={key} type="flex" className={styles['config-item']}>
        <Col>
          <img src={icon} alt="" />
        </Col>
        <Col className={styles['config-item-right']}>
          {children.map((o) => {
            const [left, right] = o;
            return (
              <Row type="flex">
                <Col className={styles.left}>{left}</Col>
                <Col className={styles.right}>{right}</Col>
              </Row>
            );
          })}
        </Col>
      </Row>
    );
  };

  return (
    <Content>
      {renderCompanyNameUI()}
      <div className={styles['config-wrap']}>{renderData.map((o) => renderConfigItem(o))}</div>
      <UserTermsModal visible={visible} editAble={false} handleCancel={() => setVisible(false)} />
    </Content>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient', 'hzero.c7nProUI'],
})(ClientConfig);
