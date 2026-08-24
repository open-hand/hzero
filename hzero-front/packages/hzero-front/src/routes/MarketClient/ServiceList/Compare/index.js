import React, { useState, useEffect } from 'react';
import { Radio, Spin, Icon } from 'hzero-ui';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { Content, Header } from 'components/Page';
import ServiceItemList from '../components/ServiceItemList';
import ComponentsCompare from './components/ComponentsCompare';
import ApiCompare from './components/ApiCompare';
import ConfigCompare from './components/ConfigCompare';
import {
  queryServiceCompareResult,
  queryApiCompareResult,
  queryConfigCompareResult,
  queryServiceDetail,
} from '../services';
import styles from './index.less';

const BACK_TO_SERVICE_LIST_URL = '/market-client/services-list';
const TYPE_COMPONENT = 'component';
const TYPE_API = 'api';
const TYPE_CONFIG = 'config';

function ServerList({ match }) {
  const { serviceId, version: versionAndArtifactId } = match.params;
  const [loading, setLoading] = useState(false);
  const [compareServiceResult, setCompareServiceResult] = useState({});
  const [compareApiResult, setCompareApiResult] = useState({});
  const [compareConfigResult, setCompareConfigResult] = useState({});
  const [serviceList, setServiceList] = useState({});
  const [compareType, setCompareType] = useState(TYPE_COMPONENT);
  const [compareServiceItem, setCompareServiceItem] = useState({});
  const [version, artifactId] = versionAndArtifactId.split('__');

  // 初始化
  useEffect(() => {
    init();
  }, [serviceId, version]);

  // 监听 Tab 变化
  useEffect(() => {
    if (!Object.keys(compareApiResult).length && compareType === TYPE_API) {
      setLoading(true);
      queryApiCompareResult(serviceId, {
        artifactId: compareServiceItem.artifactId,
        groupId: compareServiceItem.groupId,
        version,
      }).then((res) => {
        if (res && res.failed !== true) {
          setCompareApiResult(handleCompareApiResult(res));
        }
        setLoading(false);
      });
    }
    if (!Object.keys(compareConfigResult).length && compareType === TYPE_CONFIG) {
      setLoading(true);
      queryConfigCompareResult(serviceId, {
        artifactId: compareServiceItem.artifactId,
        groupId: compareServiceItem.groupId,
        version,
      }).then((res) => {
        if (res && res.failed !== true) {
          setCompareConfigResult(res);
        }
        setLoading(false);
      });
    }
  }, [compareType]);

  const tabType = {
    [TYPE_COMPONENT]: <ComponentsCompare compareResult={compareServiceResult} />,
    [TYPE_API]: <ApiCompare compareResult={compareApiResult} />,
    [TYPE_CONFIG]: <ConfigCompare compareResult={compareConfigResult} />,
  };

  // 初始化函数
  const init = async () => {
    if (!serviceId || !version) return;

    if (!compareServiceResult.length) {
      setLoading(true);
      // 查询组件详情
      queryServiceDetail(serviceId).then((detailRes) => {
        if (!detailRes) return;
        const { servers = [] } = detailRes;
        setServiceList(servers);
        const serviceItem = getCompareItem(servers, artifactId);
        setCompareServiceItem(serviceItem);

        // 然后查询组件对比结果
        queryServiceCompareResult(serviceId, {
          artifactId: serviceItem.artifactId,
          groupId: serviceItem.groupId,
          version,
        }).then((serviceRes) => {
          setLoading(false);
          setCompareServiceResult(serviceRes);
        });
      });
    }
  };

  /**
   * 根据 artifactId 获取对应的数据
   */
  const getCompareItem = (serversList = [], targetArtifactId) => {
    if (Array.isArray(serversList)) {
      return serversList.find((item) => item.artifactId === targetArtifactId) || {};
    }
    return {};
  };

  // 打平接口中 变更的属性：changedEndpoints
  const handleCompareApiResult = (data) => {
    const { changedEndpoints = [], ...res } = data;
    const tempChangedEndpoints = [];
    changedEndpoints.forEach((o) => {
      const { changedOperations = {}, ...resChangedEndpointsProps } = o;

      // 处理接口的行为
      Object.keys(changedOperations).forEach((method) => {
        tempChangedEndpoints.push({
          ...resChangedEndpointsProps,
          ...changedOperations[method],
          method,
        });
      });
    });

    res.changedEndpoints = tempChangedEndpoints;
    return res;
  };

  return (
    <>
      <Header
        title={intl
          .get('hadm.marketclient.view.service.component.interface.contrast')
          .d('组件/接口对比')}
        backPath={BACK_TO_SERVICE_LIST_URL}
      >
        <div>
          <Icon type="question-circle" theme="filled" />
          &nbsp;
          <span>
            {intl
              .get('hadm.marketclient.view.service.contrast.tips')
              .d('以下内容仅供参考，版本对比信息会因为服务使用的插件不同而有所变化。')}
          </span>
        </div>
      </Header>
      <Content>
        <Radio.Group onChange={(e) => setCompareType(e.target.value)} value={compareType}>
          <Radio.Button value={TYPE_COMPONENT}>
            {intl.get('hadm.marketclient.view.component.contrast').d('组件对比')}
          </Radio.Button>
          <Radio.Button value={TYPE_API}>
            {intl.get('hadm.marketclient.view.interface.contrast').d('接口对比')}
          </Radio.Button>
          <Radio.Button value={TYPE_CONFIG}>
            {intl.get('hadm.marketclient.view.config.contrast').d('配置对比')}
          </Radio.Button>
        </Radio.Group>
        <div className={styles['content-wrap']}>
          <div className={styles['service-item-wrap']}>
            {serviceList.length > 1 ? (
              <div className={styles['service-info-component']}>
                <ServiceItemList serviceId={serviceId} serviceList={serviceList} />
              </div>
            ) : (
              <div className={styles['service-info']}>
                <h3 className={styles['list-header']}>{serviceId}</h3>
                <span>
                  {intl.get('hadm.marketclient.view.home.currentVersion').d('当前版本')}：v
                  {compareServiceItem.version}
                </span>
                <span>
                  {' '}
                  <a>VS</a>{' '}
                </span>
                {intl.get('hadm.marketclient.view.version.contrast').d('对比版本')}：v{version}
              </div>
            )}
          </div>
          <Spin spinning={loading}>{tabType[compareType]}</Spin>
        </div>
      </Content>
    </>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient'],
})(ServerList);
