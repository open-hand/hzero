/**
 * @since 2019-12-03
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, Lov } from 'choerodon-ui/pro';
import { Tabs } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { WithCustomizeC7N as withCustomize } from 'components/Customize';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import { searchDepartmentDS } from '@/stores/departmentDS';
import { searchCompanyAndDepartmentDS } from '@/stores/positionDS';

import Department from './Department';
import Company from './Company';
import Position from './Position';
import styles from './index.less';

const { TabPane } = Tabs;

@withCustomize({
  unitCode: ['HPFM.ORG_LIST.TABS'],
})
@formatterCollections({
  code: ['hpfm.organization'],
})
export default class SearchConfig extends React.Component {
  constructor(props) {
    super(props);
    this.departmentRef = React.createRef();
    this.positionRef = React.createRef();
    this.searchDepartmentDS = new DataSet(searchDepartmentDS());
    this.searchCompanyAndDepartmentDS = new DataSet(searchCompanyAndDepartmentDS());
    this.state = {
      searchNode: [
        <Lov
          dataSet={this.searchDepartmentDS}
          placeholder={intl
            .get('hpfm.organization.view.message.inputCompanyName')
            .d('请输入公司名称')}
          name="parentUnitIdLov"
          onChange={this.handleChangeCompanyData}
          style={{ marginTop: 10 }}
        />,
      ],
    };
  }

  @Bind()
  handleChangeCompanyData(record) {
    const that = this.departmentRef.current;
    const { unitId } = record || { unitId: '' };
    that.treeDS.setQueryParameter('unitCompanyId', unitId);
    that.treeDS.setQueryParameter('keyWord', null);
    that.setState({ searchValue: null, optionValue: undefined });
    that.treeDS.query();
  }

  @Bind()
  handleSearchPositionData(record) {
    const that = this.positionRef.current;
    if (!record) {
      const { unitCompanyId } = this.searchCompanyAndDepartmentDS.current.toData();
      that.treeDS.setQueryParameter('unitCompanyId', unitCompanyId);
      that.treeDS.setQueryParameter('unitId', null);
      that.searchAllFormation();
    } else {
      const { unitCompanyId, unitId } = record || { unitCompanyId: '', unitDepartmentId: '' };
      that.treeDS.setQueryParameter('unitCompanyId', unitCompanyId);
      that.treeDS.setQueryParameter('unitId', unitId);
      that.searchAllFormation();
    }
    that.setState({ searchValue: null, optionValue: undefined });
  }

  @Bind()
  handleClearDepartmentLov(record) {
    this.searchCompanyAndDepartmentDS.current.set('departmentLov', {});
    const that = this.positionRef.current;
    const { unitId } = record || { unitId: '' };
    that.treeDS.setQueryParameter('unitCompanyId', unitId);
    that.treeDS.setQueryParameter('keyWord', null);
    that.treeDS.setQueryParameter('unitId', null);
    that.setState({ searchValue: null, optionValue: undefined });
    that.treeDS.query();
  }

  @Bind()
  handleChange(value) {
    let { searchNode } = this.state;
    if (value === 'department') {
      searchNode = [
        <Lov
          dataSet={this.searchDepartmentDS}
          placeholder={intl
            .get('hpfm.organization.view.message.inputCompanyName')
            .d('请输入公司名称')}
          name="parentUnitIdLov"
          onChange={this.handleChangeCompanyData}
          style={{ marginTop: 10 }}
        />,
      ];
    } else if (value === 'company') {
      searchNode = [];
    } else if (value === 'job') {
      searchNode = [
        <div
          style={{
            display: 'flex',
            flexDirection: 'row',
            justifyContent: 'flex-start',
            marginTop: 10,
          }}
        >
          <Lov
            dataSet={this.searchCompanyAndDepartmentDS}
            placeholder={intl
              .get('hpfm.organization.view.message.inputCompanyName')
              .d('请输入公司名称')}
            name="companyLov"
            style={{ marginRight: 10 }}
            onChange={this.handleClearDepartmentLov}
          />
          <Lov
            dataSet={this.searchCompanyAndDepartmentDS}
            placeholder={intl
              .get('hpfm.organization.view.message.inputDepartmentName')
              .d('请输入部门名称')}
            name="departmentLov"
            onChange={this.handleSearchPositionData}
          />
        </div>,
      ];
    }
    this.setState({ searchNode });
  }

  render() {
    const {
      match: { path },
      customizeTabPane,
    } = this.props;
    const { searchNode } = this.state;
    return (
      <>
        <Header
          title={intl.get('hpfm.organization.view.title.enterpriseDirectory').d('企业通讯录')}
        />
        <Content>
          {customizeTabPane(
            {
              code: 'HPFM.ORG_LIST.TABS',
            },
            <Tabs
              className={styles['tabs-style']}
              defaultActiveKey="department"
              animated={false}
              tabBarExtraContent={searchNode}
              onChange={this.handleChange}
            >
              <TabPane
                tab={intl.get('hpfm.organization.view.title.companyTab').d('公司')}
                key="company"
              >
                <Company />
              </TabPane>
              <TabPane
                tab={intl.get('hpfm.organization.view.title.departmentTab').d('部门')}
                key="department"
              >
                <Department
                  ref={this.departmentRef}
                  searchDepartmentDS={this.searchDepartmentDS}
                  path={path}
                />
              </TabPane>
              <TabPane
                tab={intl.get('hpfm.organization.view.title.positionTab').d('岗位')}
                key="job"
              >
                <Position
                  ref={this.positionRef}
                  searchCompanyAndDepartmentDS={this.searchCompanyAndDepartmentDS}
                  path={path}
                />
              </TabPane>
            </Tabs>
          )}
        </Content>
      </>
    );
  }
}
