/**
 * AuthorityManagement - 租户级权限维护
 * @date: 2018-7-31
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { withRouter } from 'dva/router';
import { Form, Modal, Tabs, Spin, Card } from 'hzero-ui';
import qs from 'querystring';
import lodash from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';
import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';

import { loadDimensionAsync, hasDimension } from '@/customize/dimensions';
import AuthDetail from '@/components/AuthorityManagement/AuthDetail';
import Company from './Detail/Company';
// import Customer from './Detail/Customer';
// import Supplier from './Detail/Supplier';
// import Purcat from './Detail/Purcat';
import AuthorityCopy from './Detail/AuthorityCopy';
import DimensionTab from '../../../../components/AuthorityManagement';
import styles from './index.less';

const FormItem = Form.Item;

/**
 * 使用 Tabs.TabPane 组件
 */
const { TabPane } = Tabs;

/**
 * 权限复制弹出框
 * @extends {Component} - React.Component
 * @reactProps {Object} userId - 用户id
 * @reactProps {Object} copyModalVisible - 控制modal显示/隐藏属性
 * @reactProps {Function} refresh - 刷新数据
 * @reactProps {Function} authorityCopy - 控制modal显示隐藏方法
 * @reactProps {Object} organizationId - 组织编号
 * @return React.element
 */
// const AuthorityCopyModal = props => {
//   const { copyModalVisible, authorityCopy, userId, organizationId, refresh, path } = props;
//   return (
//     <Modal
//       title={intl.get('hiam.authority.view.button.copy').d('权限复制')}
//       visible={copyModalVisible}
//       onCancel={() => {
//         authorityCopy(false);
//       }}
//       width={600}
//       footer={null}
//     >
//       <AuthorityCopy
//         path={path}
//         authorityCopy={authorityCopy}
//         userId={userId}
//         organizationId={organizationId}
//         refresh={refresh}
//       />
//     </Modal>
//   );
// };

/**
 * 权限交换弹出框
 * @extends {Component} - React.Component
 * @reactProps {Object} changeModalVisible - 控制modal显示/隐藏属性
 * @reactProps {Function} changeAuthority - 交换后触发方法
 * @reactProps {Function} authorityChange - 控制modal显示隐藏方法
 * @reactProps {Object} organizationId - 组织编号
 * @return React.element
 */
const AuthorityChangeModal = Form.create({ fieldNameProp: null })((props) => {
  const {
    changeModalVisible,
    authorityChange,
    changeAuthority,
    organizationId,
    form,
    userId,
  } = props;
  return (
    <Modal
      title={intl.get('hiam.authority.model.authorityManagement.authorityChange').d('权限交换')}
      visible={changeModalVisible}
      onOk={() => {
        form.validateFields((err, fieldsValue) => {
          if (!err) {
            changeAuthority(fieldsValue, form);
          }
        });
      }}
      onCancel={() => {
        authorityChange(false);
      }}
      width={500}
    >
      <div>
        {intl
          .get('hiam.authority.view.message.title.authorityChange')
          .d('权限交换操作会将当前用户与所选用户权限值进行互换，请谨慎操作!')}
      </div>
      <FormItem>
        {form.getFieldDecorator('authorityChangeId', {
          rules: [
            {
              required: true,
              message: intl.get('hiam.authority.view.message.validate.tenant').d('请选择一个用户'),
            },
          ],
        })(<Lov code="HIAM.USER_AUTHORITY_USER" queryParams={{ organizationId, userId }} />)}
      </FormItem>
    </Modal>
  );
});
/**
 * 租户级权限管理
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} authorityManagement，authorityCompany，authoritySupplier，authorityPurorg，authorityPurcat，authorityCustomer - 数据源
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({
  code: ['hiam.authority', 'entity.company', 'entity.customer', 'entity.supplier'],
})
@Form.create({ fieldNameProp: null })
@connect(
  ({
    authorityDimension,
    authorityCompany,
    authorityCustomer,
    authoritySupplier,
    authorityPurorg,
    authorityPurcat,
    authorityManagement,
    authorityValueList,
    authorityLovView,
    authorityDataSource,
    authorityDataGroup,
    loading,
  }) => ({
    authorityDimension,
    authorityCompany,
    authorityCustomer,
    authoritySupplier,
    authorityPurorg,
    authorityPurcat,
    authorityManagement,
    authorityValueList,
    authorityLovView,
    authorityDataSource,
    authorityDataGroup,
    fetchTabListLoading: loading.effects['authorityManagement/fetchUserDimension'],
  })
)
@withRouter
export default class AuthorityManagement extends React.Component {
  /**
   *Creates an instance of AuthorityManagement.
   * @param {Object} props 属性
   */
  constructor(props) {
    super(props);
    const routerParam = qs.parse(props.history.location.search.substr(1));
    const organizationId = getCurrentOrganizationId();
    this.state = {
      dimensions: [],
      userId: routerParam.userId,
      organizationId,
      copyModalVisible: false,
      changeModalVisible: false,
      defaultList: [],
      tabList: [],
    };
  }

  /**
   *组件挂载后执行方法
   */
  componentDidMount() {
    const tabList = this.renderDefaultTabs();
    this.setState({ tabList });
    const { dispatch } = this.props;
    const { userId } = this.state;
    // dispatch({
    //   type: 'authorityManagement/fetchUserDimension',
    //   payload: { userId },
    // });
    dispatch({
      type: 'authorityManagement/fetchUserInfo',
      payload: { userId },
    });
    dispatch({
      type: 'authorityManagement/fetchUserDimension',
      payload: { userId },
    }).then((res) => {
      if (!lodash.isEmpty(res)) {
        const defaultList = [
          'PURORG',
          'LOV',
          'DATASOURCE',
          'LOV_VIEW',
          'PURAGENT',
          'COMPANY',
          'DATA_GROUP',
        ];
        const arr = res
          .filter(
            (item) => defaultList.includes(item.valueSource) && item.valueSourceType === 'LOCAL'
          )
          .filter((item) => !hasDimension(item))
          .map((item) => item.valueSource);
        this.setState({ defaultList: arr });
        this.loadDimensions(res);
      }
    });
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    // TODO: 为了 清除缓存 需要在这里将所有model还原到初始化
    dispatch({
      type: 'authorityManagement/updateState',
      payload: { authorList: [] },
    });
    dispatch({
      type: 'authorityCompany/updateState',
      payload: {
        data: [],
        checkList: [],
        originList: [],
        expandedRowKeys: [],
      },
    });
    // dispatch({
    //   type: 'authorityCustomer/updateState',
    //   payload: {
    //     head: {},
    //     list: [],
    //     pagination: {},
    //     customerDataSource: [],
    //     customerPagination: {},
    //   }
    // });
    // dispatch({
    //   type: 'authorityManagement/updateState',
    //   payload: {
    //     data: {
    //       list: [],
    //     },
    //   }
    // });
    dispatch({
      type: 'authorityPuragent/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        puragentDataSource: [],
        puragentPagination: {},
      },
    });
    // dispatch({
    //   type: 'authorityPurcat/updateState',
    //   payload: {
    //     head: {},
    //     list: [],
    //     pagination: {},
    //     purcatDataSource: [],
    //     purcatPagination: {},
    //   }
    // });
    // dispatch({
    //   type: 'authorityPuritem/updateState',
    //   payload: {
    //     head: {},
    //     data: {
    //       list: [],
    //     },
    //   }
    // });
    dispatch({
      type: 'authorityPurorg/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        purorgDataSource: [],
        purorgPagination: {},
      },
    });
    // dispatch({
    //   type: 'authoritySalitem/updateState',
    //   payload: {
    //     head: {},
    //     data: {
    //       list: [],
    //     },
    //   }
    // });
    // dispatch({
    //   type: 'authoritySupplier/updateState',
    //   payload: {
    //     head: {}, // 头部数据
    //     list: [], // 请求查询到的数据
    //     pagination: {}, // 分页信息
    //     supplierDataSource: [],
    //     supplierPagination: {},
    //   }
    // });
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
    return Promise.all(dimensions.map((item) => this.importDimension(item.valueSource)));
  }

  /**
   * 将 卡片 加载成 layout
   */
  @Bind()
  loadDimensions(layouts = []) {
    const { userId } = this.state;
    const layout = layouts;
    let dimensions = [];
    const arr = [];
    this.importDimensions(...layout)
      .then((cmps) => {
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
                  queryParams={{ userId }}
                />
              ),
            };
          }
          if (cmp) {
            arr.push(item.valueSource);
            if (cmp.__esModule) {
              const HzeroDimension = cmp.default;
              return {
                code: item.valueSource,
                name: item.dimensionName,
                component: <HzeroDimension dimensionParams={item} queryParams={{ userId }} />,
              };
            }
            const HzeroDimension = cmp;
            return {
              code: item.valueSource,
              name: item.dimensionName,
              component: <HzeroDimension dimensionParams={item} queryParams={{ userId }} />,
            };
          }
          return {
            code: item.valueSource,
            name: item.dimensionName,
            component: <Card loading />,
          };
        });
      })
      .finally(() => {
        const { defaultList } = this.state;
        this.setState({
          dimensions,
          defaultList: defaultList.filter((item) => !arr.includes(item)),
        });
      });
  }

  @Bind()
  renderDimensions() {
    const { dimensions = [], defaultList, tabList } = this.state;
    return dimensions.map((item) => {
      if (!defaultList.includes(item.code)) {
        return (
          <TabPane tab={item.name} key={item.name}>
            {item.component}
          </TabPane>
        );
      }
      return (
        <TabPane tab={item.name} key={item.name}>
          {tabList.find((temp) => temp.code === item.code).component}
        </TabPane>
      );
    });
  }

  @Bind()
  renderDefaultTabs() {
    const {
      match: { path },
    } = this.props;
    const { userId, organizationId } = this.state;
    const tabList = [
      {
        code: 'COMPANY',
        component: <Company organizationId={organizationId} userId={userId} path={path} />,
      },
      {
        code: 'PURORG',
        component: (
          <AuthDetail
            code="purorg"
            name={intl.get('hiam.authority.view.message.tab.purorg').d('采购组织')}
            organizationId={organizationId}
            userId={userId}
            path={path}
          />
        ),
      },
      {
        code: 'PURAGENT',
        component: (
          <AuthDetail
            code="puragent"
            name={intl.get('hiam.authority.view.message.tab.puragent').d('采购员')}
            organizationId={organizationId}
            userId={userId}
            path={path}
          />
        ),
      },
      {
        code: 'LOV',
        component: (
          <AuthDetail
            code="valueList"
            name={intl.get('hiam.authority.view.message.tab.valueList').d('值集')}
            organizationId={organizationId}
            userId={userId}
            path={path}
          />
        ),
      },
      {
        code: 'LOV_VIEW',
        component: (
          <AuthDetail
            code="lovView"
            name={intl.get('hiam.authority.view.message.tab.lovView').d('值集视图')}
            organizationId={organizationId}
            userId={userId}
            path={path}
          />
        ),
      },
      {
        code: 'DATASOURCE',
        component: (
          <AuthDetail
            code="dataSource"
            name={intl.get('hiam.authority.view.message.tab.dataSource').d('数据源')}
            organizationId={organizationId}
            userId={userId}
            path={path}
          />
        ),
      },
      {
        code: 'DATA_GROUP',
        component: (
          <AuthDetail
            code="dataGroup"
            name={intl.get('hiam.authority.view.message.tab.dataGroup').d('数据组')}
            organizationId={organizationId}
            userId={userId}
            path={path}
          />
        ),
      },
    ];
    return tabList;
  }

  /**
   * tab切换后查询数据
   *
   * @param {Object} name tab名称
   */
  @Bind()
  fetchData(name) {
    const { dispatch } = this.props;
    const { userId } = this.state;
    const staticData = {
      userId,
      page: 0,
      size: 10,
      authorityTypeCode:
        name === 'lovView'
          ? 'LOV_VIEW'
          : name === 'dataGroup'
          ? 'DATA_GROUP'
          : lodash.upperCase(name),
    };
    dispatch({
      type: `authority${lodash.upperFirst(name)}/fetchAuthority${lodash.upperFirst(name)}`,
      payload: staticData,
    });
    if (name === 'lov') {
      dispatch({
        type: `authorityValueList/fetchAuthorityValueList`,
        payload: staticData,
      });
    }
  }

  /**
   *刷新数据
   *
   */
  @Bind()
  refresh() {
    const { dispatch } = this.props;
    const { userId } = this.state;
    dispatch({
      type: 'authorityCompany/fetchAuthorityCompany',
      payload: { userId },
    });
  }

  /**
   *权限交换modal显示隐藏标记
   *
   * @param {*Boolean} flag 隐藏/显示标记
   */
  @Bind()
  authorityChange(flag) {
    this.setState({
      changeModalVisible: !!flag,
    });
  }

  /**
   *权限复制modal显示隐藏方法
   *
   * @param {Boolean} flag 隐藏/显示标记
   */
  @Bind()
  authorityCopy(flag) {
    this.setState({
      copyModalVisible: !!flag,
    });
  }

  /**
   *权限更改触发方法
   *
   * @param {Object} values form数据
   * @param {Object} form form表单
   */
  @Bind()
  changeAuthority(values, form) {
    const { dispatch } = this.props;
    const { userId } = this.state;
    dispatch({
      type: 'authorityManagement/changeAuthority',
      payload: {
        userId,
        exchanageUserId: values.authorityChangeId,
      },
    }).then((response) => {
      if (response) {
        notification.success();
        form.resetFields();
        this.authorityChange(false);
        this.refresh();
      }
    });
  }

  /**
   *渲染事件
   *
   * @returns
   */
  render() {
    const {
      authorityManagement: { data = {} },
      authorityManagement: { authorList = [] },
      fetchTabListLoading = false,
      match: { path },
      location: { search },
    } = this.props;
    const { organizationId, userId, copyModalVisible, changeModalVisible } = this.state;
    const { access_token: accessToken } = qs.parse(search.substring(1));
    return (
      <>
        <Header
          title={intl.get('hiam.authority.view.message.title').d('权限维护')}
          backPath={
            path.indexOf('/private') === 0
              ? `/private/hiam/sub-account-org/users?access_token=${accessToken}`
              : '/hiam/sub-account-org/users'
          }
        >
          <>
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.copy`,
                  type: 'button',
                  meaning: '权限维护-权限复制',
                },
              ]}
              icon="copy"
              type="primary"
              onClick={() => this.authorityCopy(true)}
            >
              {intl.get('hiam.authority.view.button.copy').d('权限复制')}
            </ButtonPermission>
            {/* <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.authorityChange`,
                    type: 'button',
                    meaning: '权限维护-权限交换',
                  },
                ]}
                icon="swap"
                onClick={() => this.authorityChange(true)}
              >
                {intl.get('hiam.authority.model.authorityManagement.authorityChange').d('权限交换')}
              </ButtonPermission> */}
          </>
        </Header>
        <Content>
          <div style={{ margin: '0 0 24px 0px', lineHeight: '20px' }}>
            <span style={{ color: '#666' }}>
              {intl.get('hiam.authority.model.authorityManagement.userAccount').d('账号')}:
            </span>
            <span style={{ marginRight: '32px' }}>{data.loginName}</span>
            <span style={{ color: '#666' }}>
              {intl.get('hiam.authority.model.authorityManagement.userName').d('描述')}:
            </span>
            <span>{data.realName}</span>
          </div>
          <Spin spinning={fetchTabListLoading}>
            {authorList.length > 0 && (
              <Tabs
                defaultActiveKey="company"
                animated={false}
                // onChange={this.tabChange}
                tabPosition="left"
                className={styles['sub-accout-tabs']}
              >
                {/* {defaultList.some(item => item === 'COMPANY') && (
                  <TabPane tab={intl.get('entity.company.tag').d('公司')} key="company">
                    <Company organizationId={organizationId} userId={userId} path={path} />
                  </TabPane>
                )} */}
                {/* <TabPane tab={intl.get('entity.customer.tag').d('客户')} key="customer"> */}
                {/*  <Customer organizationId={organizationId} userId={userId} /> */}
                {/* </TabPane> */}
                {/* <TabPane tab={intl.get('entity.supplier.tag').d('供应商')} key="supplier"> */}
                {/*  <Supplier organizationId={organizationId} userId={userId} /> */}
                {/* </TabPane> */}
                {/* <TabPane */}
                {/*  tab={intl.get('hiam.authority.view.message.tab.purcat').d('采购品类')} */}
                {/*  key="purcat" */}
                {/* > */}
                {/*  <Purcat organizationId={organizationId} userId={userId} /> */}
                {/* </TabPane> */}
                {/* <TabPane */}
                {/*  tab={intl.get('hiam.authority.view.message.tab.puritem').d('采购物料')} */}
                {/*  key="puritem" */}
                {/* > */}
                {/*  /!* <Customer organizationId={organizationId} userId={userId} /> *!/ */}
                {/* </TabPane> */}
                {/* <TabPane */}
                {/*  tab={intl.get('hiam.authority.view.message.tab.salitem').d('销售产品')} */}
                {/*  key="salitem" */}
                {/* > */}
                {/*  /!* <Customer organizationId={organizationId} userId={userId} /> *!/ */}
                {/* </TabPane> */}
                {this.renderDimensions()}
              </Tabs>
            )}
            {lodash.isEmpty(authorList) && !fetchTabListLoading && (
              <h3 style={{ color: 'gray', marginTop: '10%', textAlign: 'center' }}>
                {intl
                  .get('hiam.authority.model.authorityManagement.noAuthority')
                  .d('当前暂无可维护的数据权限')}
              </h3>
            )}
          </Spin>
          <AuthorityCopy
            path={path}
            organizationId={organizationId}
            copyModalVisible={copyModalVisible}
            authorityCopy={this.authorityCopy}
            userId={userId}
            refresh={this.refresh}
          />
          <AuthorityChangeModal
            changeAuthority={this.changeAuthority}
            changeModalVisible={changeModalVisible}
            authorityChange={this.authorityChange}
            userId={userId}
            organizationId={organizationId}
          />
        </Content>
      </>
    );
  }
}
