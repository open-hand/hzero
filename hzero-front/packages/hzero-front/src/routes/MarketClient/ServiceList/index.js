import React, { useEffect, useState, useRef } from 'react';
import { connect } from 'dva';
import { getResponse } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { Link, routerRedux } from 'dva/router';
import { Table, Row, Col, Icon, notification, Tooltip, Button } from 'hzero-ui';
import { Content, Header } from 'components/Page';

import CompareButton from './components/CompareButton';
import VersionModal from './components/VersionModal';
import LoginModal from '../components/LoginModal';
import DownloadModal from './components/download-modal';
import {
  fetchServiceList,
  queryHzeroVersion,
  queryServiceVersionList,
  testConnect,
  marketUserLogin,
  refreshCache,
} from './services';
import styles from './index.less';

const MARKET_USER_INFO_KEY = '__market_user_info_';
const BACK_TO_HOME_URL = '/market-client/home';

function ServerList({ dispatch, global: { hzeroUILocale } }) {
  const [serviceList, setServiceList] = useState([]);
  const [serviceListLoading, setHzeroVersionLoading] = useState(false);
  const [loginModalVisible, setLoginModalVisible] = useState(false);
  const [hzeroVersion, setHzeroVersion] = useState([]);
  const [menuList, setMenuList] = useState([]);
  const [versionLoadingId, setVersionLoadingId] = useState('');
  const [menuVisibleId, setMenuVisibleId] = useState('');
  const [compareModalData, setCompareModalData] = useState(null);
  const [reachAble, setReachAble] = useState(false);
  const [refreshLoading, setRefreshLoading] = useState(false);
  const [downloadRecord, setDownloadRecord] = useState(null);
  const beforeLoginModalTag = useRef(null); // 点击登陆完成后，弹出版本选择按钮
  useEffect(() => {
    init();
    return () => {
      document.body.onclick = null;
    };
  }, []);

  // 初始化方法
  const init = async () => {
    setHzeroVersionLoading(true);
    // 查询服务列表
    fetchServiceList().then((res) => {
      setHzeroVersionLoading(false);
      if (Array.isArray(res)) {
        setServiceList(handleServerList(res));
      }
    });

    // 查询 Hzero 版本
    queryHzeroVersion().then((res) => {
      if (res) {
        setHzeroVersion(res);
      }
    });

    // 查询对比的后端是否是通的
    testConnect().then((connectRes) => {
      if (connectRes && connectRes.flag === true) {
        setReachAble(true);
      } else {
        // 网络链接不通，不能用服务对比
        setReachAble(false);
      }
    });
  };

  // 跳转离线页面
  // TODO 跳转空页面，下个迭代做，这个迭代不能用，置灰即可
  // eslint-disable-next-line
  const goToOfflinePage = () => {
    dispatch(
      routerRedux.push({
        pathname: '/market-client/offline',
      })
    );
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

  // 打开登录模态框
  const openLoginModal = () => {
    setLoginModalVisible(true);
  };

  // 清空页面全局点击事件
  const clearMenuEvent = () => {
    setMenuVisibleId('');
    setVersionLoadingId('');
    setMenuList([]);
    document.body.onclick = null;
  };

  // 处理数据服务列表数据接口返回结果
  const handleServerList = (res) => {
    return res.map((item) => {
      const { components, servers } = item;
      const itemRes = { ...item };
      if (Array.isArray(servers) && servers.length > 1) {
        // 由多个服务, 需要展示子元素
        itemRes.children = servers.map((i) => ({ ...i, expendChilde: true }));
      } else {
        // 只有一个服务，任意从 components, servers 中拿出一个版本
        const tempVersionItem = servers[0] || components[0] || {};
        Object.assign(itemRes, {
          currentVersion: tempVersionItem?.version,
          groupId: tempVersionItem?.groupId,
          artifactId: tempVersionItem?.artifactId,
        });
      }
      return itemRes;
    });
  };

  // 处理版本按钮显示/隐藏
  const handleVisibleChange = (visible, record) => {
    if (!reachAble) {
      // goToOfflinePage();
      return;
    }
    if (!checkLoginStatus()) {
      beforeLoginModalTag.current = { ...record };
      openLoginModal();
      return;
    }

    if (visible) {
      document.body.onclick = clearMenuEvent;
      setVersionLoadingId(record.serviceId);
      const { artifactId, groupId } = record;

      // 查询版本号
      queryServiceVersionList({ artifactId, groupId }).then((res) => {
        setVersionLoadingId('');
        if (getResponse(res)) {
          setMenuVisibleId(record.serviceId);
          setMenuList(res); // 把查询回来的版本回写到页面中
        }
      });
    }
  };

  // 处理离线下载包
  const handleOfflineDownload = (record) => {
    const { serviceId, servers = [] } = record;
    if (Array.isArray(servers) && servers.length === 1) {
      // 只有一个节点，直接弹出下载确认框
      setDownloadRecord(Object.assign({}, servers[0] || {}, { serviceId }));
    } else if (Array.isArray(servers) && servers.length > 1) {
      // 服务合并，打开服务合并下载狂
      setCompareModalData({ ...record, type: 'download' }); // 打开版本对比框，下载模式
    } else {
      notification.warning({
        message: intl
          .get('hadm.marketclient.view.service.download.error')
          .d('数据异常，暂时无法下载'),
      });
    }
  };

  // 表格列公共渲染函数
  const columCommonRender = (text, record) => {
    const obj = {
      children: text || '-',
      props: {},
    };
    if (record.expendChilde) {
      obj.props.colSpan = 0;
    }
    return obj;
  };

  // 服务名称表格列渲染函数
  const serviceNameColumnRender = (text, record) => {
    const obj = {
      children: text,
      props: {},
    };
    if (record.expendChilde) {
      // 渲染展开行
      obj.props.colSpan = 5;
      const { groupId, artifactId, version } = record;
      obj.children = (
        <div className={styles['service-name-render']}>
          <Row className={styles['service-name-item']} type="flex">
            <Col>
              <span>groupId：{groupId}</span>
              <br />
              <span>artifactId：{artifactId}</span>
            </Col>
            <Col>
              {intl.get('hadm.marketclient.view.home.currentVersion').d('当前版本')}：{version}
            </Col>
          </Row>
        </div>
      );
    }
    return obj;
  };

  // 多服务版本对比
  const handleMultipleCompare = (record) => {
    if (!reachAble) {
      // goToOfflinePage();
      return;
    }
    if (!checkLoginStatus()) {
      openLoginModal();
      return;
    }

    if (!record || !Array.isArray(record.servers) || !record.servers.length) {
      notification.warning({
        message: intl.get('hadm.marketclient.view.service.versionContrast.empty').d('暂无对比版本'),
      });
      return;
    }
    setCompareModalData(record);
  };

  // 对版本对比文字按钮入口
  const multipleCompareEnter = (record) => {
    if (!reachAble) {
      return (
        <Tooltip title="无网络连接，在线对比功能不可用">
          <span style={{ color: '#999', cursor: 'not-allowed' }}>
            {intl.get('hadm.marketclient.view.service.versionContrast').d('版本对比')}{' '}
            <Icon type="down" />
          </span>
        </Tooltip>
      );
    }

    return (
      <a onClick={() => handleMultipleCompare(record)}>
        {intl.get('hadm.marketclient.view.service.versionContrast').d('版本对比')}
      </a>
    );
  };

  // 表格操作列
  const operationRender = (text, record) => {
    const child = (hasChildren) => {
      // 下载离线版本
      const downloadElement = (
        <Tooltip
          title={intl
            .get('hadm.marketclient.view.service.download.tips')
            .d('离线包可以用于上传应用市场做离线对比')}
        >
          <a onClick={() => handleOfflineDownload(record)}>
            {intl.get('hadm.marketclient.view.service.download').d('下载离线包')}
          </a>
        </Tooltip>
      );
      const singleChile =
        versionLoadingId === record.serviceId ? (
          <span style={{ marginLeft: '16px' }}>
            <Icon type="loading" />
          </span>
        ) : (
          <CompareButton
            record={record}
            reachAble={reachAble}
            menuList={menuList}
            menuVisibleId={menuVisibleId}
            handleVisibleChange={handleVisibleChange}
          />
        );
      const OptionsList = hasChildren ? multipleCompareEnter(record) : singleChile;

      return (
        <>
          <Link to={`/market-client/services-detail/${record.serviceId}`}>
            {intl.get('hadm.marketclient.view.component.list').d('组件列表')}
          </Link>
          &nbsp; &nbsp;
          {downloadElement}
          &nbsp; &nbsp;
          {OptionsList}
        </>
      );
    };

    const obj = {
      children: child(record.children && record.children.length),
      props: {},
    };
    if (record.expendChilde) {
      obj.props.colSpan = 0;
    }
    return obj;
  };

  const handleRefresh = () => {
    setRefreshLoading(true);
    refreshCache().then(() => {
      setRefreshLoading(false);
      init();
    });
  };

  // 处理登陆成功回调
  const handleLoginSuccessCallBack = () => {
    if (beforeLoginModalTag.current) {
      // 点击登陆完成后，弹出版本选择按钮
      handleVisibleChange(true, beforeLoginModalTag.current);
      beforeLoginModalTag.current = null;
    }
  };

  const columns = [
    {
      title: intl.get('hadm.common.model.common.serviceName').d('服务名称'),
      dataIndex: 'serviceName',
      width: 250,
      render: serviceNameColumnRender,
    },
    {
      title: intl.get('hadm.common.model.common.serviceCode').d('服务编码'),
      dataIndex: 'serviceId',
      render: columCommonRender,
    },
    {
      title: intl.get('hadm.marketclient.view.home.currentVersion').d('当前版本'),
      width: 200,
      dataIndex: 'currentVersion',
      render: columCommonRender,
    },
    {
      title: intl.get('hzero.common.button.action').d('操作'),
      width: hzeroUILocale.locale === 'zh_CN' ? 250 : 310,
      dataIndex: 'operation',
      render: operationRender,
    },
  ];
  const tableProps = {
    indentSize: 0,
    bordered: true,
    rowKey: 'serviceId',
    dataSource: serviceList,
    loading: serviceListLoading,
    pagination: false,
    columns,
  };

  return (
    <>
      <Header
        title={intl.get('hadm.marketclient.view.service.list').d('服务列表')}
        backPath={BACK_TO_HOME_URL}
      />
      <Content>
        <h3 className={styles['header-text']}>
          {intl.get('hadm.marketclient.view.service.description1').d('当前您使用的是')}&nbsp;
          <a style={{ cursor: 'text' }}>
            {intl.get('hadm.marketclient.view.hzero.platform').d('HZERO微服务技术平台 ')}
            {hzeroVersion.length ? ` v${hzeroVersion.join(', v')}` : null}
          </a>{' '}
          {intl.get('hadm.marketclient.view.service.description2').d(' ，您使用的服务有： ')}
          <Button style={{ float: 'right' }} loading={refreshLoading} onClick={handleRefresh}>
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </Button>
        </h3>
        <Table {...tableProps} />
        <VersionModal
          handleCancel={() => setCompareModalData(null)}
          serviceData={compareModalData}
        />
        <LoginModal
          marketUserLogin={marketUserLogin}
          loginModalVisible={loginModalVisible}
          onCancel={() => setLoginModalVisible(false)}
          onOk={handleLoginSuccessCallBack}
        />
        <DownloadModal
          downloadRecord={downloadRecord}
          handleCancel={() => setDownloadRecord(null)}
        />
      </Content>
    </>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient', 'hadm.common'],
})(
  connect(({ global = {} }) => ({
    global,
  }))(ServerList)
);
