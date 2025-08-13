/**
 * SecGrpListPage - 安全组维护
 * @date: 2019-10-24
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { DataSet, Table, Modal, TextField, Form, Switch, IntlField, Lov } from 'choerodon-ui/pro';
import { Tabs } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import { enableRender } from 'utils/renderer';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { getCurrentOrganizationId, getResponse, getCurrentRole } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { copyPermissionSets } from '@/services/securityGroupService';

import { secGrpDS, roleLovDS } from '@/stores/SecurityGroupDS';
import openAssignModal from './AssignDrawer';
import QuickCreateModal from './QuickCreateModal';

const dataModalKey = Modal.key();
const createModalKey = Modal.key();

const { TabPane } = Tabs;
const currentRole = getCurrentRole();

@formatterCollections({
  code: ['hiam.roleManagement', 'hiam.securityGroup'],
})
export default class SecGrpListPage extends Component {
  constructor(props) {
    super(props);
    this.secGrpDS = new DataSet({
      ...secGrpDS(),
      queryParameter: {
        secGrpSource: 'self',
        roleId: currentRole.id,
      },
      autoQuery: false,
    });
    this.roleLovDs = new DataSet(roleLovDS());
    const {
      queryParameter: { secGrpSource = 'self' },
    } = this.secGrpDS;

    this.state = {
      activeKey: secGrpSource,
      currentRoleId: currentRole.id, // 当前角色id
      currentRoleName: currentRole.name, // 当前角色名称
      secGrpSource, // 安全组来源
    };
  }

  stepModal;

  componentDidMount() {
    const { id, name } = currentRole;
    this.roleLovDs.current.set('roleId', id);
    this.roleLovDs.current.set('roleName', name);
    const {
      location: { state: { _back, roleId, roleName, secGrpSource } = {} },
    } = this.props;
    if (_back === -1) {
      this.roleLovDs.current.set('roleId', roleId);
      this.roleLovDs.current.set('roleName', roleName);
      this.secGrpDS.setQueryParameter('secGrpSource', secGrpSource);
      this.setState({
        currentRoleId: roleId,
        currentRoleName: roleName,
        activeKey: secGrpSource,
        secGrpSource,
      });
      this.secGrpDS.setQueryParameter('roleId', roleId);
    }
    this.secGrpDS.query();
  }

  // 权限维度表格列
  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      { name: 'secGrpCode' },
      { name: 'secGrpName' },
      { name: 'secGrpLevelMeaning', width: 100 },
      { name: 'remark' },
      { name: 'createRoleName' },
      { name: 'enabledFlag', width: 120, renderer: ({ value }) => enableRender(value) },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        command: ({ record }) => [
          <span className="action-link">
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.detail`,
                  type: 'button',
                  meaning: '安全组-查看详情',
                },
              ]}
              onClick={() => this.redirectToEdit(record)}
            >
              {record.get('secGrpSource') === 'self'
                ? intl.get('hzero.common.button.edit').d('编辑')
                : intl.get('hiam.securityGroup.view.button.securityGroup.detail').d('查看详情')}
            </ButtonPermission>
            {record.get('secGrpSource') !== 'children' && (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.copy`,
                    type: 'button',
                    meaning: '安全组-复制',
                  },
                ]}
                onClick={() => this.handleCreate('copy', record)}
              >
                {intl.get('hzero.common.button.copy').d('复制')}
              </ButtonPermission>
            )}
            {record.get('secGrpSource') === 'self' && (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.assign`,
                    type: 'button',
                    meaning: '安全组-分配角色',
                  },
                ]}
                onClick={() => this.handleAssignRole(record)}
              >
                {intl.get('hiam.securityGroup.view.title.securityGroup.assign').d('分配角色')}
              </ButtonPermission>
            )}
          </span>,
        ],
        lock: 'right',
      },
    ];
  }

  /**
   * 跳转至详情页
   * @param {object} record - 行数据
   */
  @Bind()
  redirectToEdit(record) {
    const { secGrpId, roleId } = record.toData();
    const { history } = this.props;
    const { secGrpSource, currentRoleId, currentRoleName } = this.state;
    history.push({
      pathname: `/hiam/security-group/detail/${secGrpId}/${roleId}`,
      search: `?source=${secGrpSource}&roleId=${currentRoleId}&name=${currentRoleName}`,
    });
  }

  /**
   * 新建、复制安全组
   * @param type
   * @param record
   */
  @Bind()
  handleCreate(type, record) {
    const isCreate = type === 'create';
    this.secGrpDS.create({ tenantId: getCurrentOrganizationId() });
    Modal.open({
      title: isCreate
        ? intl.get('hiam.securityGroup.view.title.securityGroup.create').d('新建安全组')
        : intl.get('hiam.securityGroup.view.title.securityGroup.copy').d('复制安全组'),
      drawer: true,
      closable: true,
      key: dataModalKey,
      children: (
        <Form record={this.secGrpDS.current}>
          <TextField name="secGrpCode" />
          <IntlField name="secGrpName" />
          <IntlField name="remark" />
          <Switch name="enabledFlag" />
        </Form>
      ),
      destroyOnClose: true,
      onCancel: () => this.secGrpDS.reset(),
      onClose: () => this.secGrpDS.reset(),
      onOk: async () => {
        const { currentRoleId } = this.state;
        let res;
        let copyRes;
        if (isCreate) {
          res = await this.secGrpDS.submit();
        } else {
          const validateRes = await this.secGrpDS.validate();
          const newData = {
            ...this.secGrpDS.current.toJSONData(),
            roleId: currentRoleId,
          };
          copyRes =
            validateRes && getResponse(await copyPermissionSets(record.get('secGrpId'), newData));
        }
        if (!isEmpty(res) && res.failed && res.message) {
          return false;
        }
        if ((!isEmpty(res) && res.success) || copyRes) {
          this.secGrpDS.query();
        } else if (res === false) {
          return false;
        } else {
          return false;
        }
      },
    });
  }

  @Bind()
  handleAssignRole(record) {
    openAssignModal(record);
  }

  /**
   * 关闭快速创建弹窗
   */
  @Bind()
  closeStepModal(current) {
    this.stepModal.close();
    if (current === 2) {
      this.secGrpDS.query();
    }
  }

  /**
   * 打开快速创建弹窗
   */
  @Bind()
  handleStepCreate(secGrpSource) {
    const { currentRoleId } = this.state;
    this.stepModal = Modal.open({
      title: intl.get('hiam.securityGroup.model.securityGroup.create').d('快速创建'),
      closable: false,
      key: createModalKey,
      style: { width: '8.2rem' },
      children: (
        <QuickCreateModal
          onCancel={this.closeStepModal}
          roleId={currentRoleId}
          secGrpSource={secGrpSource}
        />
      ),
      footer: null,
      destroyOnClose: true,
    });
  }

  /**
   * 切换安全组角色tab
   * @param {string} activeKey - 选中的tab页
   */
  @Bind()
  handleChangeType(activeKey) {
    const { currentRoleId } = this.state;
    this.secGrpDS.setQueryParameter('roleId', currentRoleId);
    this.secGrpDS.setQueryParameter('secGrpSource', activeKey);
    this.secGrpDS.queryDataSet.current.reset();
    this.secGrpDS.query();
    this.setState({
      activeKey,
      secGrpSource: activeKey,
    });
  }

  /**
   * 切换当前角色
   * @param {object} value
   * @memberof SecGrpListPage
   */
  @Bind()
  handleRoleChange(value) {
    if (value) {
      this.setState({
        currentRoleId: value.id,
        currentRoleName: value.name,
      });
      this.secGrpDS.setQueryParameter('roleId', value.id);
      this.secGrpDS.query();
    }
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const { activeKey, secGrpSource } = this.state;
    return (
      <>
        <Header title={intl.get('hiam.securityGroup.view.title.securityGroup').d('安全组维护')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '安全组-新建',
              },
            ]}
            icon="add"
            onClick={this.handleCreate.bind(this, 'create')}
            color="primary"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.fastCreate`,
                type: 'button',
                meaning: '安全组-快速创建',
              },
            ]}
            icon="add"
            onClick={() => this.handleStepCreate(secGrpSource)}
          >
            {intl.get('hiam.securityGroup.model.securityGroup.create').d('快速创建')}
          </ButtonPermission>
          <div>
            <span>
              {intl.get('hiam.securityGroup.model.securityGroup.roleLov').d('当前角色')}：
            </span>
            <Lov
              dataSet={this.roleLovDs}
              name="roleLov"
              noCache
              clearButton={false}
              onChange={this.handleRoleChange}
            />
          </div>
        </Header>
        <Content>
          <Tabs activeKey={activeKey} animated={false} onChange={this.handleChangeType}>
            <TabPane
              tab={intl.get('hiam.securityGroup.view.title.tab.secGrp.roleSelf').d('角色安全组')}
              key="self"
            >
              <Table dataSet={this.secGrpDS} queryFieldsLimit={2} columns={this.columns} />
            </TabPane>
            <TabPane
              tab={intl
                .get('hiam.securityGroup.view.title.tab.secGrp.parent')
                .d('上级分配的安全组')}
              key="parent"
            >
              <Table dataSet={this.secGrpDS} queryFieldsLimit={2} columns={this.columns} />
            </TabPane>
            <TabPane
              tab={intl.get('hiam.securityGroup.view.title.tab.secGrp.child').d('下级创建的安全组')}
              key="children"
            >
              <Table dataSet={this.secGrpDS} queryFieldsLimit={2} columns={this.columns} />
            </TabPane>
          </Tabs>
        </Content>
      </>
    );
  }
}
