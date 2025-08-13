/**
 * DataPermissionTab - 数据权限tab页
 * @date: 2019-11-1
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Tabs, Form, Card } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, sortBy } from 'lodash';

import intl from 'utils/intl';
// import { getCurrentOrganizationId } from 'utils/utils';

import { loadDimensionAsync } from '@/customize/dimensions';
import { querySecGrpDimension } from '@/services/securityGroupService';
import Company from './Company';
import styles from './index.less';
import AuthDetail from './AuthDetail';

const { TabPane } = Tabs;

@Form.create({ fieldNameProp: null })
export default class DataPermissionTab extends Component {
  constructor(props) {
    super(props);
    this.state = {
      dimensions: [], // 权限数据tab集合
      defaultList: [],
      allDimensions: [],
    };
  }

  @Bind()
  getTabList() {
    const { secGrpId, roleId, isSelf, secGrpSource } = this.props;
    const commonProps = {
      valueSourceType: 'LOCAL',
      secGrpId,
      roleId,
      isSelf,
      secGrpSource,
    };
    const companyProps = {
      authorityTypeCode: 'COMPANY',
      secGrpId,
      isSelf,
      roleId,
      secGrpSource,
    };
    const purorgProps = {
      ...commonProps,
      authorityTypeCode: 'PURORG',
      labels: {
        nameLabel: intl.get('hiam.authority.model.authorityPurorg.dataName').d('采购组织名称'),
        codeLabel: intl.get('hiam.authority.model.authorityPurorg.dataCode').d('采购组织代码'),
        addLabel: intl.get('hiam.authority.view.button.table.create.purorg').d('新建采购组织权限'),
        deleteLabel: intl
          .get('hiam.authority.view.button.table.delete.purorg')
          .d('删除采购组织权限'),
        includeAllLabel: intl
          .get('hiam.authority.view.message.title.tooltip.Purorg')
          .d('“加入全部”即将所有采购组织权限自动添加至当前账户，无需再手工添加。'),
        addTitleLabel: intl.get('hiam.authority.view.message.title.selectPurorg').d('选择采购组织'),
        nameTitleLabel: intl.get('hiam.authority.model.authorityPurorg.dataName').d('采购组织名称'),
        codeTitleLabel: intl.get('hiam.authority.model.authorityPurorg.dataCode').d('采购组织代码'),
      },
    };
    const puragentProps = {
      ...commonProps,
      authorityTypeCode: 'PURAGENT',
      labels: {
        nameLabel: intl.get('hiam.authority.model.authorityPuragent.dataName').d('采购员名称'),
        codeLabel: intl.get('hiam.authority.model.authorityPuragent.dataCode').d('采购员代码'),
        addLabel: intl.get('hiam.authority.view.button.table.create.puragent').d('新建采购员权限'),
        deleteLabel: intl
          .get('hiam.authority.view.button.table.delete.puragent')
          .d('删除采购员权限'),
        includeAllLabel: intl
          .get('hiam.authority.view.message.title.tooltip.pur')
          .d('“加入全部”即将所有采购员权限自动添加至当前账户，无需再手工添加。'),
        addTitleLabel: intl.get('hiam.authority.view.title.modal.puragent').d('选择采购员'),
        nameTitleLabel: intl.get('hiam.authority.model.authorityPuragent.dataName').d('采购员名称'),
        codeTitleLabel: intl.get('hiam.authority.model.authorityPuragent.dataCode').d('采购员代码'),
      },
    };

    const valueListProps = {
      ...commonProps,
      authorityTypeCode: 'LOV',
      labels: {
        nameLabel: intl.get('hiam.authority.model.authorityValueList.dataName').d('值集名称'),
        codeLabel: intl.get('hiam.authority.model.authorityValueList.dataCode').d('值集代码'),
        addLabel: intl.get('hiam.authority.view.button.table.create.valueList').d('新建权限'),
        deleteLabel: intl.get('hiam.authority.view.button.table.delete.valueList').d('删除权限'),
        includeAllLabel: intl
          .get('hiam.authority.view.message.title.tooltip.vl')
          .d('“加入全部”即将所有值集权限自动添加至当前账户，无需再手工添加。'),
        addTitleLabel: intl.get('hiam.authority.view.title.modal.valueList').d('选择值集'),
        nameTitleLabel: intl.get('hiam.authority.model.authorityValueList.lovName').d('值集名称'),
        codeTitleLabel: intl.get('hiam.authority.model.authorityValueList.lovCode').d('值集代码'),
      },
    };
    const lovViewProps = {
      ...commonProps,
      authorityTypeCode: 'LOV_VIEW',
      labels: {
        nameLabel: intl.get('hiam.authority.model.authorityLovView.viewName').d('值集视图名称'),
        codeLabel: intl.get('hiam.authority.model.authorityLovView.viewCode').d('值集视图代码'),
        addLabel: intl.get('hiam.authority.view.button.table.create.lovView').d('新建值集视图权限'),
        deleteLabel: intl
          .get('hiam.authority.view.button.table.delete.lovView')
          .d('删除值集视图权限'),
        includeAllLabel: intl
          .get('hiam.authority.view.message.title.tooltip.lovView')
          .d('“加入全部”即将所有值集视图权限自动添加至当前账户，无需再手工添加。'),
        addTitleLabel: intl.get('hiam.authority.view.title.modal.lovView').d('选择值集视图'),
        nameTitleLabel: intl
          .get('hiam.authority.model.authorityLovView.viewName')
          .d('值集视图名称'),
        codeTitleLabel: intl
          .get('hiam.authority.model.authorityLovView.viewCode')
          .d('值集视图代码'),
      },
    };
    const dataSourceProps = {
      ...commonProps,
      authorityTypeCode: 'DATASOURCE',
      labels: {
        nameLabel: intl.get('hiam.authority.model.authorityDataSource.dataName').d('数据源名称'),
        codeLabel: intl.get('hiam.authority.model.authorityDataSource.dataCode').d('数据源代码'),
        addLabel: intl
          .get('hiam.authority.view.button.table.create.dataSource')
          .d('新建数据源权限'),
        deleteLabel: intl
          .get('hiam.authority.view.button.table.delete.dataSource')
          .d('删除数据源权限'),
        includeAllLabel: intl
          .get('hiam.authority.view.message.title.tooltip.ds')
          .d('“加入全部”即将所有数据源权限自动添加至当前账户，无需再手工添加。'),
        addTitleLabel: intl.get('hiam.authority.view.title.modal.dataSource').d('选择数据源'),
        nameTitleLabel: intl
          .get('hiam.authority.model.authorityDataSource.viewName')
          .d('数据源名称'),
        codeTitleLabel: intl
          .get('hiam.authority.model.authorityDataSource.viewCode')
          .d('数据源代码'),
      },
    };
    const dataGroupProps = {
      ...commonProps,
      authorityTypeCode: 'DATA_GROUP',
      labels: {
        nameLabel: intl.get('hiam.authority.model.dataGroup.name').d('数据组名称'),
        codeLabel: intl.get('hiam.authority.model.dataGroup.code').d('数据组代码'),
        addLabel: intl.get('hiam.authority.view.button.table.create.dataGroup').d('新建数据组权限'),
        deleteLabel: intl
          .get('hiam.authority.view.button.table.delete.dataGroup')
          .d('删除数据组权限'),
        includeAllLabel: intl
          .get('hiam.authority.view.message.title.tooltip.dg')
          .d('“加入全部”即将所有数据组权限自动添加至当前账户，无需再手工添加。'),
        addTitleLabel: intl.get('hiam.authority.view.title.modal.dg').d('选择数据组'),
        nameTitleLabel: intl.get('hiam.authority.model.dataGroup.name').d('数据组名称'),
        codeTitleLabel: intl.get('hiam.authority.model.dataGroup.code').d('数据组代码'),
      },
    };
    return [
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
  }

  async componentDidMount() {
    const { secGrpId } = this.props;
    const dimensionTypes = await querySecGrpDimension(secGrpId);
    if (!isEmpty(dimensionTypes)) {
      const sortedDimensionTypes = sortBy(dimensionTypes, 'orderSeq');
      const defaultList = [
        'PURORG',
        'LOV',
        'DATASOURCE',
        'LOV_VIEW',
        'PURAGENT',
        'COMPANY',
        'DATA_GROUP',
      ];
      const arr = sortedDimensionTypes
        .filter(
          (item) => defaultList.includes(item.dimensionCode) && item.valueSourceType === 'LOCAL'
        )
        .map((item) => item.dimensionCode);
      this.setState({ defaultList: arr, allDimensions: dimensionTypes });
      this.loadDimensions(dimensionTypes);
    }
    // this.setState({
    //   authorityTypeCode: dimensionTypes.length ? dimensionTypes[0] : '',
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

  @Bind()
  loadDimensions(layouts = []) {
    const { secGrpId, roleId, tenantId = 0, isSelf, secGrpSource } = this.props;
    const layout = layouts;
    const arr = [];
    let dimensions = [];
    this.importDimensions(...layout)
      .then((cmps) => {
        dimensions = layout.map((item, index) => {
          const cmp = cmps[index];
          if (item.valueSourceType === 'LOV') {
            const authProps = {
              isSelf,
              authorityTypeCode: item.dimensionCode,
              secGrpId,
              roleId,
              viewCode: item.viewCode,
              valueSourceType: item.valueSourceType,
              secGrpSource,
              labels: {
                nameLabel: intl
                  .get('hiam.securityGroup.model.securityGroup.auth.name', {
                    name: item.dimensionName,
                  })
                  .d(`${item.dimensionName}名称`),
                addLabel: intl
                  .get('hiam.securityGroup.view.button.table.create.auth', {
                    name: item.dimensionName,
                  })
                  .d(`新建${item.dimensionName}权限`),
                deleteLabel: intl
                  .get('hiam.securityGroup.view.button.table.delete.auth', {
                    name: item.dimensionName,
                  })
                  .d(`删除${item.dimensionName}权限`),
                includeAllLabel: intl
                  .get('hiam.securityGroup.view.message.title.tooltip.auth', {
                    name: item.dimensionName,
                  })
                  .d(
                    `“加入全部”即将所有${item.dimensionName}权限自动添加至当前账户，无需再手工添加。`
                  ),
                addTitleLabel: intl
                  .get('hiam.securityGroup.view.title.modal.auth', { name: item.dimensionName })
                  .d(`选择${item.dimensionName}`),
                nameTitleLabel: intl
                  .get('hiam.securityGroup.model.securityGroup.auth.name', {
                    name: item.dimensionName,
                  })
                  .d(`${item.dimensionName}名称`),
                codeTitleLabel: intl
                  .get('hiam.securityGroup.model.securityGroup.auth.code', {
                    name: item.dimensionName,
                  })
                  .d(`${item.dimensionName}代码`),
              },
            };
            return {
              code: item.valueSource,
              name: item.dimensionName,
              valueSource: item.valueSource,
              component: <AuthDetail {...authProps} />,
            };
          }
          if (cmp) {
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
            }
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
  renderTabPanes() {
    const { dimensions = [], defaultList } = this.state;
    const tabList = this.getTabList();
    return dimensions
      .map((item) => {
        if (!defaultList.includes(item.code)) {
          return (
            <TabPane tab={item.name} key={item.name}>
              {item.component}
            </TabPane>
          );
        }
        return (
          <TabPane tab={item.name} key={item.name}>
            {tabList.find((temp) => temp.code === item.code).component || {}}
          </TabPane>
        );
      })
      .filter((item) => !!item);
  }

  render() {
    const { allDimensions } = this.state;
    return (
      <>
        {allDimensions.length > 0 ? (
          <Tabs animated={false} tabPosition="left" className={styles['hiam-permission-data-tabs']}>
            {this.renderTabPanes()}
          </Tabs>
        ) : (
          <h3 style={{ color: 'gray', marginTop: '10%', textAlign: 'center' }}>
            {intl
              .get('hiam.authority.model.authorityManagement.noAuthority')
              .d('当前暂无可维护的数据权限')}
          </h3>
        )}
      </>
    );
  }
}
