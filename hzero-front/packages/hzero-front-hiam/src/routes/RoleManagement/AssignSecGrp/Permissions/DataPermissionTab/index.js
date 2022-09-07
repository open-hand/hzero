/**
 * DataDimensionTab - 安全组数据权限
 * @date: 2019-12-24
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Tabs, Spin, Card } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isEqual } from 'lodash';
import { connect } from 'dva';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import { loadDimensionAsync } from '@/customize/dimensions';
import Company from './Detail/Company';
import AuthDetail from '@/components/AuthorityManagement/AuthDetail';
import DimensionTab from '@/components/AuthorityManagement';
import styles from './index.less';

const { TabPane } = Tabs;

@formatterCollections({
  code: ['hiam.authority', 'entity.company', 'entity.customer', 'entity.supplier'],
})
@Form.create({ fieldNameProp: null })
@connect(
  ({
    secGrpAuthorityValueList,
    secGrpAuthorityPurorg,
    secGrpAuthorityPuragent,
    secGrpAuthorityLovView,
    secGrpAuthorityDataSource,
    secGrpAuthorityDataGroup,
    secGrpAuthorityCompany,
    loading,
  }) => ({
    secGrpAuthorityCompany,
    secGrpAuthorityValueList,
    fetchValueListLoading: loading.effects['secGrpAuthorityValueList/fetchAuthorityValueList'],
    secGrpAuthorityPurorg,
    fetchPurorgLoading: loading.effects['secGrpAuthorityPurorg/fetchAuthorityPurorg'],
    secGrpAuthorityPuragent,
    fetchPuragentLoading: loading.effects['secGrpAuthorityPuragent/fetchAuthorityPuragent'],
    secGrpAuthorityLovView,
    fetchLovViewLoading: loading.effects['secGrpAuthorityLovView/fetchAuthorityLovView'],
    secGrpAuthorityDataSource,
    fetchDataSourceLoading: loading.effects['secGrpAuthorityDataSource/fetchAuthorityDataSource'],
    secGrpAuthorityDataGroup,
    fetchDataGroupLoading: loading.effects['secGrpAuthorityDataGroup/fetchAuthorityDataGroup'],
  })
)
export default class DataPermissionTab extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      defaultList: [],
      tabList: [],
    };
  }

  componentDidMount() {
    const { allTabList } = this.props;
    this.renderDefaultTabs();
    const defaultList = [
      'PURORG',
      'LOV',
      'DATASOURCE',
      'LOV_VIEW',
      'PURAGENT',
      'COMPANY',
      'DATA_GROUP',
    ];
    const arr = allTabList
      .filter(item => defaultList.includes(item.dimensionCode) && item.valueSourceType === 'LOCAL')
      .map(item => item.dimensionCode);
    this.setState({ defaultList: arr });
    this.loadDimensions(allTabList);
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    const { allTabList } = this.props;
    this.renderDefaultTabs();
    if (!isEqual(nextProps.allTabList, allTabList) && !isEmpty(nextProps.allTabList)) {
      const defaultList = [
        'PURORG',
        'LOV',
        'DATASOURCE',
        'LOV_VIEW',
        'PURAGENT',
        'COMPANY',
        'DATA_GROUP',
      ];
      const arr = nextProps.allTabList
        .filter(
          item => defaultList.includes(item.dimensionCode) && item.valueSourceType === 'LOCAL'
        )
        .map(item => item.dimensionCode);
      this.setState({ defaultList: arr });
      this.loadDimensions(nextProps.allTabList);
    }
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    // TODO: 为了 清除缓存 需要在这里将所有model还原到初始化
    dispatch({
      type: 'roleDataAuthorityManagement/updateState',
      payload: { authorList: [] },
    });
    dispatch({
      type: 'secGrpAuthorityCompany/updateState',
      payload: {
        data: [],
        checkList: [],
        originList: [],
        expandedRowKeys: [],
      },
    });
    dispatch({
      type: 'secGrpAuthorityPuragent/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        puragentDataSource: [],
        puragentPagination: {},
      },
    });
    dispatch({
      type: 'secGrpAuthorityPurorg/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        purorgDataSource: [],
        purorgPagination: {},
      },
    });
    dispatch({
      type: 'secGrpAuthorityDataSource/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        createDataSource: [],
        createPagination: {},
      },
    });
    dispatch({
      type: 'secGrpAuthorityDataGroup/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        createDataSource: [],
        createPagination: {},
      },
    });
    dispatch({
      type: 'secGrpAuthorityValueList/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        createDataSource: [],
        createPagination: {},
      },
    });
  }

  /**
   * 加载单独的组件, 失败返回 失败的页面
   * @param {string} code - 维度编码
   * @return {React.Component|null}
   */
  async importDimension(code) {
    let loadDimension = null;
    try {
      loadDimension = await loadDimensionAsync(code);
    } catch (e) {
      loadDimension = null;
    }
    return loadDimension;
  }

  /**
   * 加载所有的维度组件
   */
  async importDimensions(...dimensions) {
    return Promise.all(dimensions.map(item => this.importDimension(item.valueSource)));
  }

  /**
   * 将 卡片 加载成 layout
   */
  @Bind()
  loadDimensions(layouts = []) {
    const { roleId, tenantId, secGrpId } = this.props;
    const layout = layouts;
    const arr = [];
    let dimensions = [];
    this.importDimensions(...layout)
      .then(cmps => {
        dimensions = layout.map((item, index) => {
          const cmp = cmps[index];
          if (item.valueSourceType === 'LOV') {
            return {
              code: item.valueSource,
              name: item.dimensionName,
              valueSource: item.valueSource,
              component: (
                <DimensionTab
                  code={item.viewCode}
                  name={item.dimensionName}
                  authorityTypeCode={item.dimensionCode}
                  queryParams={{ roleId, secGrpId }}
                  tenantId={tenantId}
                  isSecGrp
                />
              ),
            };
          } else if (cmp) {
            arr.push(item.valueSource);
            if (cmp.__esModule) {
              const HzeroDimension = cmp.default;
              return {
                code: item.valueSource,
                name: item.dimensionName,
                component: (
                  <HzeroDimension
                    dimensionParams={item}
                    tenantId={tenantId}
                    queryParams={{ roleId }}
                  />
                ),
              };
            } else {
              const HzeroDimension = cmp;
              return {
                code: item.valueSource,
                name: item.dimensionName,
                component: (
                  <HzeroDimension
                    dimensionParams={item}
                    tenantId={tenantId}
                    queryParams={{ roleId }}
                  />
                ),
              };
            }
          } else {
            return {
              code: item.valueSource,
              name: item.dimensionName,
              component: <Card loading />,
            };
          }
        });
      })
      .finally(() => {
        const { defaultList } = this.state;
        this.setState({
          dimensions,
          defaultList: defaultList.filter(item => !arr.includes(item)),
        });
      });
  }

  @Bind()
  renderDimensions() {
    const { dimensions = [], defaultList, tabList } = this.state;
    return dimensions
      .map(item => {
        if (!defaultList.includes(item.code)) {
          return (
            <TabPane tab={item.name} key={item.name}>
              {item.component}
            </TabPane>
          );
        } else {
          return (
            <TabPane tab={item.name} key={item.name}>
              {tabList.find(temp => temp.code === item.code).component}
            </TabPane>
          );
        }
      })
      .filter(item => !!item);
  }

  @Bind()
  renderDefaultTabs() {
    const {
      dispatch = e => e,
      path,
      roleId,
      secGrpId,
      onShield = e => e,
      shieldLoading,
    } = this.props;
    const { organizationId } = this.state;
    const companyProps = {
      path,
      dispatch,
      organizationId,
      secGrpId,
      roleId,
      onShield,
      shieldLoading,
    };

    const commonProps = {
      path,
      dispatch,
      organizationId,
      roleId,
      isSecGrp: true,
      secGrpId,
      onShield,
    };

    // 采购组织
    const purorgProps = {
      ...commonProps,
      code: 'purorg',
      name: intl.get('hiam.authority.view.message.tab.purorg').d('采购组织'),
    };

    const puragentProps = {
      ...commonProps,
      code: 'puragent',
      name: intl.get('hiam.authority.view.message.tab.puragent').d('采购员'),
    };

    const valueListProps = {
      ...commonProps,
      code: 'valueList',
      name: intl.get('hiam.authority.view.message.tab.valueList').d('值集'),
    };

    const lovViewProps = {
      ...commonProps,
      code: 'lovView',
      name: intl.get('hiam.authority.view.message.tab.lovView').d('值集视图'),
    };
    // 数据源
    const dataSourceProps = {
      ...commonProps,
      code: 'dataSource',
      name: intl.get('hiam.authority.view.message.tab.dataSource').d('数据源'),
    };
    // 数据组
    const dataGroupProps = {
      ...commonProps,
      code: 'dataGroup',
      name: intl.get('hiam.authority.view.message.tab.dataGroup').d('数据组'),
    };
    const tabList = [
      {
        code: 'COMPANY',
        component: <Company {...companyProps} />,
      },
      {
        code: 'PURORG',
        component: <AuthDetail {...purorgProps} />,
      },
      {
        code: 'PURAGENT',
        component: <AuthDetail {...puragentProps} />,
      },
      {
        code: 'LOV',
        component: <AuthDetail {...valueListProps} />,
      },
      {
        code: 'LOV_VIEW',
        component: <AuthDetail {...lovViewProps} />,
      },
      {
        code: 'DATASOURCE',
        component: <AuthDetail {...dataSourceProps} />,
      },
      {
        code: 'DATA_GROUP',
        component: <AuthDetail {...dataGroupProps} />,
      },
    ];
    this.setState({ tabList });
  }

  render() {
    const { allTabList, queryDataPermissionTabLoading = false } = this.props;
    return (
      <>
        <div style={{ minHeight: '100px' }}>
          <Spin spinning={queryDataPermissionTabLoading}>
            {!isEmpty(allTabList) && (
              <Tabs
                defaultActiveKey="company"
                animated={false}
                tabPosition="left"
                className={styles['sub-accout-tabs']}
                loading={false}
              >
                {this.renderDimensions()}
              </Tabs>
            )}
            {isEmpty(allTabList) && (
              <h3
                style={{
                  color: 'gray',
                  marginTop: '10%',
                  marginBottom: '10%',
                  textAlign: 'center',
                }}
              >
                {intl
                  .get('hiam.authority.model.authorityManagement.noAuthority')
                  .d('当前暂无可维护的数据权限')}
              </h3>
            )}
          </Spin>
        </div>
      </>
    );
  }
}
