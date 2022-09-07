/**
 * AuthorityManagement - 租户级权限维护
 * @date: 2018-7-31
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Tabs, Spin, Card } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import { connect } from 'dva';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';

import { loadDimensionAsync, hasDimension } from '@/customize/dimensions';
import AuthDetail from '@/components/AuthorityManagement/AuthDetail';
import DimensionTab from '@/components/AuthorityManagement';
import Company from './Detail/Company';
import AuthorityCopy from './Detail/AuthorityCopy';
import styles from './index.less';

const { TabPane } = Tabs;

/**
 * 权限复制弹出框
 * @extends {Component} - React.Component
 * @reactProps {Object} roleId - 角色id
 * @reactProps {Object} copyModalVisible - 控制modal显示/隐藏属性
 * @reactProps {Function} refresh - 刷新数据
 * @reactProps {Function} authorityCopy - 控制modal显示隐藏方法
 * @reactProps {Object} organizationId - 组织编号
 * @return React.element
 */
// const AuthorityCopyModal = props => {
//   const { copyModalVisible, authorityCopy, roleId, organizationId, refresh } = props;
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
//         authorityCopy={authorityCopy}
//         roleId={roleId}
//         organizationId={organizationId}
//         refresh={refresh}
//       />
//     </Modal>
//   );
// };

// /**
//  * 权限交换弹出框
//  * @extends {Component} - React.Component
//  * @reactProps {Object} changeModalVisible - 控制modal显示/隐藏属性
//  * @reactProps {Function} changeAuthority - 交换后触发方法
//  * @reactProps {Function} authorityChange - 控制modal显示隐藏方法
//  * @reactProps {Object} organizationId - 组织编号
//  * @return React.element
//  */
// const AuthorityChangeModal = Form.create({ fieldNameProp: null })(props => {
//   const {
//     changeModalVisible,
//     authorityChange,
//     changeAuthority,
//     organizationId,
//     form,
//     userId,
//   } = props;
//   return (
//     <Modal
//       title={intl
//         .get('hiam.authority.model.authorityManagement.authorityChange')
//         .d('权限交换')}
//       visible={changeModalVisible}
//       onOk={() => {
//         form.validateFields((err, fieldsValue) => {
//           if (!err) {
//             changeAuthority(fieldsValue, form);
//           }
//         });
//       }}
//       onCancel={() => {
//         authorityChange(false);
//       }}
//       width={500}
//     >
//       <div>
//         {intl
//           .get('hiam.authority.view.message.title.authorityChange')
//           .d('权限交换操作会将当前用户与所选用户权限值进行互换，请谨慎操作!')}
//       </div>
//       <React.Fragment>
//         <FormItem>
//           {form.getFieldDecorator('authorityChangeId')(
//             <Lov code="HIAM.USER_AUTHORITY_USER" queryParams={{ organizationId, userId }} />
//           )}
//         </FormItem>
//       </React.Fragment>
//     </Modal>
//   );
// });

@formatterCollections({
  code: ['hiam.authority', 'entity.company', 'entity.customer', 'entity.supplier'],
})
@Form.create({ fieldNameProp: null })
@connect(
  ({
    trRoleDataAuthorityValueList,
    trRoleDataAuthorityPurorg,
    trRoleDataAuthorityPuragent,
    trRoleDataAuthorityLovView,
    trRoleDataAuthorityDataSource,
    trRoleDataAuthorityDataGroup,
    loading,
  }) => ({
    trRoleDataAuthorityValueList,
    addValueListLoading: loading.effects['trRoleDataAuthorityValueList/addAuthorityValueList'],
    fetchValueListLoading: loading.effects['trRoleDataAuthorityValueList/fetchAuthorityValueList'],
    trRoleDataAuthorityPurorg,
    addPurorgLoading: loading.effects['trRoleDataAuthorityPurorg/addAuthorityPurorg'],
    fetchPurorgLoading: loading.effects['trRoleDataAuthorityPurorg/fetchAuthorityPurorg'],
    trRoleDataAuthorityPuragent,
    addPuragentLoading: loading.effects['trRoleDataAuthorityPuragent/addAuthorityPuragent'],
    fetchPuragentLoading: loading.effects['trRoleDataAuthorityPuragent/fetchAuthorityPuragent'],
    trRoleDataAuthorityLovView,
    addLovViewLoading: loading.effects['trRoleDataAuthorityLovView/addAuthorityLovView'],
    fetchLovViewLoading: loading.effects['trRoleDataAuthorityLovView/fetchAuthorityLovView'],
    trRoleDataAuthorityDataSource,
    addDataSourceLoading: loading.effects['trRoleDataAuthorityDataSource/addAuthorityDataSource'],
    fetchDataSourceLoading:
      loading.effects['trRoleDataAuthorityDataSource/fetchAuthorityDataSource'],
    trRoleDataAuthorityDataGroup,
    addDataGroupLoading: loading.effects['trRoleDataAuthorityDataGroup/addAuthorityDataGroup'],
    fetchDataGroupLoading: loading.effects['trRoleDataAuthorityDataGroup/fetchAuthorityDataGroup'],
  })
)
export default class AuthorityManagement extends React.Component {
  constructor(props) {
    super(props);
    const organizationId = getCurrentOrganizationId();
    this.state = {
      userId: '',
      organizationId,
      defaultList: [],
      copyModalVisible: false,
      // changeModalVisible: false,
      // tabList: ['company', 'purorg', 'puragent', 'lov', 'lovView', 'datasource', 'dataGroup'],
      tabList: [],
    };
  }

  componentDidMount() {
    const { dispatch, roleId } = this.props;
    this.renderDefaultTabs();
    dispatch({
      type: 'trRoleDataAuthorityManagement/fetchTabList',
      payload: { roleId },
    }).then((res) => {
      if (!isEmpty(res)) {
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
      type: 'trRoleDataAuthorityManagement/updateState',
      payload: { authorList: [] },
    });
    dispatch({
      type: 'trRoleDataAuthorityCompany/updateState',
      payload: {
        data: [],
        checkList: [],
        originList: [],
        expandedRowKeys: [],
      },
    });
    dispatch({
      type: 'trRoleDataAuthorityPuragent/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        puragentDataSource: [],
        puragentPagination: {},
      },
    });
    dispatch({
      type: 'trRoleDataAuthorityPurorg/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        purorgDataSource: [],
        purorgPagination: {},
      },
    });
    dispatch({
      type: 'trRoleDataAuthorityDataSource/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        createDataSource: [],
        createPagination: {},
      },
    });
    dispatch({
      type: 'trRoleDataAuthorityDataGroup/updateState',
      payload: {
        head: {}, // 头部数据
        list: [], // 请求查询到的数据
        pagination: {}, // 分页信息
        createDataSource: [],
        createPagination: {},
      },
    });
    dispatch({
      type: 'trRoleDataAuthorityValueList/updateState',
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
    return Promise.all(dimensions.map((item) => this.importDimension(item.valueSource)));
  }

  /**
   * 将 卡片 加载成 layout
   */
  @Bind()
  loadDimensions(layouts = []) {
    const { roleId, tenantId } = this.props;
    const layout = layouts;
    const arr = [];
    let dimensions = [];
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
                  queryParams={{ roleId }}
                  tenantId={tenantId}
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
          defaultList: defaultList.filter((item) => !arr.includes(item)),
        });
      });
  }

  @Bind()
  renderDimensions() {
    const { dimensions = [], defaultList, tabList } = this.state;
    return dimensions
      .map((item) => {
        if (!defaultList.includes(item.code)) {
          return (
            <TabPane tab={item.name} key={item.name}>
              {item.component}
            </TabPane>
          );
        } else {
          return (
            <TabPane tab={item.name} key={item.name}>
              {tabList.find((temp) => temp.code === item.code).component}
            </TabPane>
          );
        }
      })
      .filter((item) => !!item);
  }

  @Bind()
  renderDefaultTabs() {
    const { dispatch = (e) => e, path, roleId } = this.props;
    const { organizationId } = this.state;
    const companyProps = {
      path,
      dispatch,
      organizationId,
      roleId,
    };

    // 采购组织
    const purorgProps = {
      code: 'purorg',
      name: intl.get('hiam.authority.view.message.tab.purorg').d('采购组织'),
      path,
      dispatch,
      organizationId,
      roleId,
    };

    const puragentProps = {
      code: 'puragent',
      name: intl.get('hiam.authority.view.message.tab.puragent').d('采购员'),
      path,
      dispatch,
      organizationId,
      roleId,
    };

    const valueListProps = {
      code: 'valueList',
      name: intl.get('hiam.authority.view.message.tab.valueList').d('值集'),
      path,
      dispatch,
      organizationId,
      roleId,
      // roleDataAuthority: trRoleDataAuthorityValueList,
      // addLoading: addValueListLoading,
      // fetchLoading: fetchValueListLoading,
    };

    const lovViewProps = {
      code: 'lovView',
      name: intl.get('hiam.authority.view.message.tab.lovView').d('值集视图'),
      path,
      dispatch,
      organizationId,
      roleId,
    };
    // 数据源
    const dataSourceProps = {
      code: 'dataSource',
      name: intl.get('hiam.authority.view.message.tab.dataSource').d('数据源'),
      path,
      dispatch,
      organizationId,
      roleId,
    };
    // 数据组
    const dataGroupProps = {
      code: 'dataGroup',
      name: intl.get('hiam.authority.view.message.tab.dataGroup').d('数据组'),
      path,
      dispatch,
      organizationId,
      roleId,
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

  /**
   *刷新数据
   *
   */
  @Bind()
  refresh() {
    const { dispatch, roleId } = this.props;
    dispatch({
      type: 'trRoleDataAuthorityCompany/fetchAuthorityCompany',
      payload: { roleId },
    });
    // this.setState({
    //   tabList: ['customer', 'purorg', 'puragent', 'lov', 'lovView', 'datasource', 'dataGroup'],
    // });
  }

  /**
   *权限交换modal显示隐藏标记
   *
   * @param {*Boolean} flag 隐藏/显示标记
   */
  // @Bind()
  // authorityChange(flag) {
  // this.setState({
  //   changeModalVisible: !!flag,
  // });
  // }

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
        // this.authorityChange(false);
        this.refresh();
      }
    });
  }

  render() {
    const {
      authorityManagement: { authorList = [] },
      path,
      roleId,
      dispatch,
      fetchTabListLoading,
    } = this.props;
    const { copyModalVisible } = this.state;

    return (
      <>
        <div style={{ marginBottom: 16, textAlign: 'right' }}>
          <ButtonPermission
            icon="copy"
            type="primary"
            style={{ marginRight: 6 }}
            onClick={() => this.authorityCopy(true)}
            permissionList={[
              {
                code: `${path}.button.copy`,
                type: 'button',
                meaning: '角色管理-角色复制',
              },
            ]}
          >
            {intl.get('hiam.authority.view.button.copy').d('权限复制')}
          </ButtonPermission>
          {/* <Button icon="swap" onClick={() => this.authorityChange(true)}>
            {intl.get(
              'hiam.authority.model.authorityManagement.authorityChange').
              d('权限交换')}
          </Button> */}
        </div>
        <div style={{ minHeight: '100px' }}>
          <Spin spinning={fetchTabListLoading}>
            {authorList.length > 0 && (
              <Tabs
                defaultActiveKey="company"
                animated={false}
                // onChange={this.tabChange}
                tabPosition="left"
                // style={{ width: '150px',
                //   backgroundColor: '#f5f5f5' }}
                className={styles['sub-accout-tabs']}
                loading={fetchTabListLoading}
              >
                {this.renderDimensions()}
              </Tabs>
            )}
            {isEmpty(authorList) && !fetchTabListLoading && (
              <h3 style={{ color: 'gray', marginTop: '10%', textAlign: 'center' }}>
                {intl
                  .get('hiam.authority.model.authorityManagement.noAuthority')
                  .d('当前暂无可维护的数据权限')}
              </h3>
            )}
          </Spin>
        </div>
        <AuthorityCopy
          dispatch={dispatch}
          visible={copyModalVisible}
          onCancel={this.authorityCopy}
          roleId={roleId}
          refresh={this.refresh}
          path={path}
        />
        {/* TODO: 待开发 */}
        {/* <AuthorityChangeModal
         changeAuthority={this.changeAuthority}
         changeModalVisible={changeModalVisible}
         authorityChange={this.authorityChange}
         roleId={roleId}
         organizationId={organizationId}
        /> */}
      </>
    );
  }
}
