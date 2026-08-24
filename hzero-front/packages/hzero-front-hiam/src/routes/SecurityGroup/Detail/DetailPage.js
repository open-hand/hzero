/**
 * SecGrpDetailPage - 安全组维护详情页
 * @date: 2019-10-25
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { DataSet, Form, TextField, Switch, IntlField } from 'choerodon-ui/pro';
import { Card, Tabs } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isNull } from 'lodash';
import queryString from 'query-string';

import { Header, Content } from 'components/Page';
import { getCurrentRole } from 'utils/utils';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';

import { secGrpDetailDS } from '@/stores/SecurityGroupDS';
import VisitPermissionTab from './VisitPermissionTab';
import DimensionTab from './DimensionTab';
import FieldDimensionTab from './FieldPermissionTab/ListPage';
import CardTab from './CardTab';
import DataPermissionTab from './DataPermissionTab';

const { TabPane } = Tabs;

@formatterCollections({
  code: ['hiam.roleManagement', 'hiam.securityGroup', 'hiam.authority'],
})
export default class SecGrpDetailPage extends Component {
  constructor(props) {
    super(props);
    const { id: roleId } = getCurrentRole();
    this.roleId = roleId;
    const { id } = props.match.params;
    this.state = {
      activeKey: null, // 选中的tab页
      isSelf: false, // 是否为详情页,
    };
    this.secGrpDetailDS = new DataSet({
      ...secGrpDetailDS(id),
      feedback: {
        loadSuccess: (res) => {
          if (res) {
            const { secGrpSource } = res;
            this.setState({
              isSelf: secGrpSource === 'self',
              activeKey: this.state.activeKey || 'visit',
            });
          }
        },
      },
    });
  }

  componentDidMount() {
    const { location } = this.props;
    const { search = {} } = location;
    const { roleId } = queryString.parse(search);
    this.secGrpDetailDS.setQueryParameter('roleId', roleId);
    if (roleId) {
      this.roleId = roleId;
    }
  }

  // 更新或新建后提交
  @Bind()
  async handleSave() {
    const res = await this.secGrpDetailDS.submit();
    if (!isEmpty(res) && res.failed && res.message) {
      return false;
    }
    if (!isEmpty(res) && res.success) {
      this.secGrpDetailDS.query();
      return false;
    }
    if (res === false) {
      notification.error({
        message: intl.get('hiam.securityGroup.view.message.required').d('存在必输字段未填写'),
      });
      return false;
    }
    notification.warning({
      message: intl.get('hiam.securityGroup.view.message.form.noChange').d('表单未做修改'),
    });
    return false;
  }

  /**
   * 切换选项卡
   * @param {string} activeKey - 选中的tabkey
   */
  @Bind()
  handleChangePermissionType(activeKey) {
    this.setState({ activeKey });
  }

  /**
   * 返回上一页时触发
   */
  @Bind()
  onBack() {
    const { history, location } = this.props;
    const { search = {} } = location;
    const { name, source } = queryString.parse(search);
    history.push({
      state: {
        _back: -1,
        roleId: this.roleId,
        roleName: name,
        secGrpSource: source,
      },
    });
  }

  render() {
    const {
      match: { path },
      location,
    } = this.props;
    const { search = {} } = location;
    const { source } = queryString.parse(search);
    const { id } = this.props.match.params;
    const { activeKey, isSelf } = this.state;
    const commonProps = {
      secGrpId: id,
      isSelf,
      roleId: this.roleId,
      secGrpSource: source,
    };
    return (
      <>
        <Header
          title={intl.get('hiam.securityGroup.view.title.securityGroup.config').d('安全组权限配置')}
          backPath="/hiam/security-group/list"
          onBack={this.onBack}
        >
          {isSelf && (
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}.button.save`,
                  type: 'button',
                  meaning: '安全组详情-保存',
                },
              ]}
              color="primary"
              icon="save"
              onClick={() => this.handleSave()}
            >
              {intl.get(`hzero.common.button.save`).d('保存')}
            </ButtonPermission>
          )}
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>{intl.get('hiam.securityGroup.view.title.basicInformation').d('基本信息')}</h3>
            }
            loading={false}
          >
            <Form dataSet={this.secGrpDetailDS} columns={3}>
              <TextField name="secGrpCode" disabled />
              <IntlField name="secGrpName" disabled={!isSelf} />
              <IntlField name="remark" disabled={!isSelf} />
              <TextField name="secGrpLevelMeaning" disabled />
              <TextField name="createRoleName" disabled />
              <Switch name="enabledFlag" />
            </Form>
          </Card>
          {!isNull(activeKey) && (
            <Tabs animated={false} onChange={this.handleChangePermissionType}>
              <TabPane
                tab={intl.get('hiam.securityGroup.view.title.tab.visit.permission').d('访问权限')}
                key="visit"
              >
                {activeKey === 'visit' && <VisitPermissionTab {...commonProps} />}
              </TabPane>
              <TabPane
                tab={intl.get('hiam.securityGroup.view.title.tab.field.permission').d('字段权限')}
                key="field"
              >
                {activeKey === 'field' && <FieldDimensionTab {...commonProps} />}
              </TabPane>
              <TabPane
                tab={intl.get('hiam.securityGroup.view.title.tab.workplace').d('工作台配置')}
                key="workplace"
              >
                {activeKey === 'workplace' && <CardTab {...commonProps} />}
              </TabPane>
              <TabPane
                tab={intl.get('hiam.securityGroup.view.title.tab.dimension').d('数据权限维度')}
                key="dimension"
              >
                {activeKey === 'dimension' && <DimensionTab {...commonProps} />}
              </TabPane>
              <TabPane
                tab={intl.get('hiam.securityGroup.view.title.tab.data.permission').d('数据权限')}
                key="data"
              >
                {activeKey === 'data' && <DataPermissionTab {...commonProps} />}
              </TabPane>
            </Tabs>
          )}
        </Content>
      </>
    );
  }
}
