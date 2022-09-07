/**
 * @since 2019-12-03
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import {
  DataSet,
  Table,
  Button,
  Modal,
  Tree,
  Form,
  TextField,
  IntlField,
  Lov,
  Switch,
  NumberField,
  Output,
} from 'choerodon-ui/pro';
import { Row, Col, Input, Tabs, Icon, Dropdown, Menu, Badge, Collapse, Select } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { yesOrNoRender, operatorRender, enableRender } from 'utils/renderer';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';

import {
  treeDS,
  formDS,
  createFormDS,
  employeeTableDS,
  employeeFormDS,
  employeePositionFormDS,
  positionTableDS,
  baseInfoDs,
} from '@/stores/departmentDS';
import { formDS as positionFormDS } from '@/stores/positionDS';
import {
  updateDepartmentInformation,
  disableDepartment,
  enableDepartment,
  addEmployeeDetail,
  updateEmployeeDetail,
} from '@/services/newOrganizationService';

import { WithCustomizeC7N as withCustomize } from 'components/Customize';

import CreateEmployeeDrawer from './CreateEmployeeDrawer';
import Detail from './Detail';
import styles from './index.less';

const { Search, Group } = Input;
const { TabPane } = Tabs;
const { Panel } = Collapse;
const { Option } = Select;

const statusMap = ['error', 'success'];

@withCustomize({
  unitCode: [
    'HPFM.ORG_LIST.DEPARTMENT.BASIC',
    'HPFM.ORG_LIST.DEPARTMENT.EMPLOYEE.FILTER',
    'HPFM.ORG_LIST.DEPARTMENT.BASIC.EDIT',
    'HPFM.ORG_LIST.DEPARTMENT.POSITION.EDIT',
    'HPFM.ORG_LIST.DEPARTMENT.EMPLOYEE.DETAIL',
    'HPFM.ORG_LIST.DEPARTMENT.EMPOLYEE.LINE',
    'HPFM.ORG_LIST.COMPANY.POSITION.LINE',
    'HPFM.ORG_LIST.DEPARTMENT.TABS',
  ],
})
export default class Department extends React.Component {
  constructor(props) {
    super(props);
    this.treeDS = new DataSet(treeDS());
    this.formDS = new DataSet(formDS());
    this.baseInfoDs = new DataSet(baseInfoDs());
    this.createFormDS = new DataSet(createFormDS());
    this.positionFormDS = new DataSet(positionFormDS());
    this.employeeTableDS = new DataSet(employeeTableDS());
    this.employeeFormDS = new DataSet(employeeFormDS());
    this.employeePositionFormDS = new DataSet(employeePositionFormDS());
    this.positionTableDS = new DataSet(positionTableDS());
    this.state = {
      basicInformation: {
        unitCode: '',
        unitName: '',
        enabledFlag: '',
      },
      currentTabs: 'employeeInformation', // 初始化当前tabs
      placeHolder: intl
        .get('hpfm.organization.view.button.employeeHolderPlace')
        .d('员工编码/员工姓名'),
      panelKey: 'information',
      employeeId: null,
      detailFlag: true,
      searchValue: null,
      searchFlag: true,
      optionValue: undefined,
    };
  }

  componentDidMount() {
    this.treeDS.addEventListener('load', this.loadData);
  }

  componentWillUnmount() {
    this.treeDS.addEventListener('load', this.loadData);
  }

  @Bind()
  loadData() {
    const initData = this.treeDS.toData()[0] || { unitId: '', unitCode: '' };
    const { unitId = '' } = initData;
    this.setState({ basicInformation: initData });
    if (unitId !== '') {
      this.employeeTableDS.setQueryParameter('unitId', unitId);
      this.employeeTableDS.setQueryParameter(
        'customizeUnitCode',
        'HPFM.ORG_LIST.DEPARTMENT.EMPOLYEE.LINE'
      );
      this.employeeTableDS.query();
      this.positionTableDS.setQueryParameter('unitId', unitId);
      this.positionTableDS.setQueryParameter(
        'customizeUnitCode',
        'HPFM.ORG_LIST.DEPARTMENT.POSITION.LINE'
      );
      this.positionTableDS.query();
    } else {
      this.employeeTableDS.setQueryParameter('unitId', 0);
      this.employeeTableDS.setQueryParameter(
        'customizeUnitCode',
        'HPFM.ORG_LIST.DEPARTMENT.EMPOLYEE.LINE'
      );
      this.employeeTableDS.query();
      this.positionTableDS.setQueryParameter('unitId', 0);
      this.positionTableDS.setQueryParameter(
        'customizeUnitCode',
        'HPFM.ORG_LIST.DEPARTMENT.POSITION.LINE'
      );
      this.positionTableDS.query();
    }
  }

  get employeeColumn() {
    return [
      {
        name: 'employeeCode',
      },
      {
        name: 'name',
      },
      {
        name: 'gender',
        align: 'left',
        width: 80,
      },
      {
        name: 'mobile',
        width: 130,
      },
      {
        name: 'email',
        width: 210,
      },
      {
        name: 'unitName',
        width: 130,
      },
      {
        name: 'positionName',
      },
      {
        name: 'primaryPositionFlag',
        align: 'left',
        width: 100,
        renderer: ({ value }) => yesOrNoRender(value),
      },
      {
        name: 'status',
        renderer: ({ value }) => {
          const tempFlag = value === 'LEAVE' ? 0 : 1;
          let textFiled;
          switch (value) {
            case 'ON':
              textFiled = intl.get('hpfm.organization.model.department.onPosition').d('在职');
              break;
            case 'TRIAL':
              textFiled = intl.get('hpfm.organization.model.department.trial').d('试用');
              break;
            case 'INTERNSHIP':
              textFiled = intl.get('hpfm.organization.model.department.practice').d('实习');
              break;
            case 'LEAVE':
              textFiled = intl.get('hpfm.organization.model.department.leave').d('离职');
              break;
            default:
              return;
          }
          return <Badge status={statusMap[tempFlag]} text={textFiled} />;
        },
      },
      {
        name: 'operation',
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 112,
        renderer: ({ record }) => {
          const { path } = this.props;
          const { employeeId } = record.toData();
          let actions = [];
          actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/edit`,
                      type: 'button',
                      meaning: '企业通讯录-编辑',
                    },
                  ]}
                  onClick={() => this.handleOpenEmployeeModal({ type: 'edit', employeeId })}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/detail`,
                      type: 'button',
                      meaning: '企业通讯录-详情',
                    },
                  ]}
                  onClick={() => this.handleDetail(record)}
                >
                  {intl.get('hzero.common.button.detail').d('详情')}
                </ButtonPermission>
              ),
              key: 'detail',
              len: 2,
              title: intl.get('hzero.common.button.detail').d('详情'),
            },
          ];
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'left',
      },
    ];
  }

  get positionColumn() {
    return [
      {
        name: 'positionName',
      },
      {
        name: 'unitName',
      },
      {
        name: 'positionCode',
      },
      // {
      //   name: 'description',
      // },
    ];
  }

  // 部门查询
  @Bind()
  searchAllFormation(record) {
    const { searchDepartmentDS } = this.props;
    let unitCompanyId = '';
    this.setState({ optionValue: undefined });

    if (
      searchDepartmentDS.current &&
      searchDepartmentDS.current.toData().parentUnitIdLov !== null
    ) {
      const {
        parentUnitIdLov: { unitId = '' },
      } = searchDepartmentDS.current.toData();
      unitCompanyId = unitId;
    }
    this.treeDS.setQueryParameter('unitCompanyId', unitCompanyId);
    this.treeDS.setQueryParameter('keyWord', record);
    this.treeDS.query();
  }

  /**
   * 打开模态框
   */
  @Bind()
  handelCreateSingleDepartment() {
    this.createFormDS.create({});
    Modal.open({
      title: intl.get('hpfm.organization.view.button.createDepartment').d('新建部门'),
      drawer: true,
      width: 520,
      children: (
        <Form dataSet={this.createFormDS}>
          <TextField name="unitCode" />
          <IntlField name="unitName" />
          <Lov name="parentUnitIdLov" />
          <NumberField step={1} name="orderSeq" />
          <Switch name="supervisorFlag" />
        </Form>
      ),
      onOk: async () => {
        if (await this.createFormDS.validate()) {
          await this.createFormDS.submit().then((res) => {
            if (res) {
              this.treeDS.query();
              return true;
            }
          });
        } else {
          return false;
        }
      },
      onCancel: () => true,
      afterClose: () => this.createFormDS.reset(),
    });
  }

  // 新建或者更新部门信息
  @Bind()
  handelCreateDepartment({ type, record = {} }) {
    switch (type) {
      case 'createLevelDepartment':
        this.openModal(
          record.toData(),
          intl.get('hpfm.organization.view.message.createLevelDepartment').d('新建平级部门'),
          type
        );
        break;
      case 'createChildrenDepartment':
        this.openModal(
          record.toData(),
          intl.get('hpfm.organization.view.message.createChildrenDepartment').d('新建子部门'),
          type
        );
        break;
      case 'edit':
        this.openModal(
          this.state.basicInformation,
          intl.get('hpfm.organization.view.message.editDepartment').d('编辑部门'),
          type
        );
        break;
      default:
    }
  }

  // 禁用部门
  @Bind()
  async handelDisabledDepartment({ record = {} }) {
    const res = await disableDepartment(record.toData());
    if (res && res.failed) {
      notification.error({
        message: res.message,
      });
    } else {
      this.treeDS.query();
      notification.success({
        message: intl.get('hpfm.organization.view.message.operationSuccess').d('操作成功！'),
      });
    }
  }

  // 启用部门
  @Bind()
  async handelEnableDepartment({ record = {} }) {
    const res = await enableDepartment(record.toData());
    if (res && res.failed) {
      notification.error({
        message: res.message,
      });
    } else {
      this.treeDS.query();
      notification.success({
        message: intl.get('hpfm.organization.view.message.operationSuccess').d('操作成功！'),
      });
    }
  }

  @Bind()
  async handleCreate() {
    const validate = await this.formDS.validate();
    if (validate) {
      const res = this.formDS.submit();
      if (res && res.success) {
        this.treeDS.query();
      }
      return res;
    } else {
      return false;
    }
  }

  @Bind()
  async handleCreateLevelDepartment() {
    const validate = await this.formDS.validate();
    if (validate) {
      const res = this.formDS.submit();
      if (res && res.success) {
        this.treeDS.query();
      }
      return res;
    } else {
      return false;
    }
  }

  @Bind()
  async handleCreateChildrenDepartment() {
    const validate = await this.formDS.validate();
    if (validate) {
      const res = await this.formDS.submit();
      if (res && res.success) {
        this.treeDS.query();
      }
      return res;
    } else {
      return false;
    }
  }

  @Bind()
  async handelEdit() {
    const validate = await this.formDS.validate();
    if (validate) {
      const updateData = this.formDS.current.toData();
      const { parentUnitIdLov, ...other } = updateData;
      const res = await updateDepartmentInformation(other);
      if (res && res.failed) {
        notification.error({
          message: res.message,
        });
      } else {
        this.treeDS.query();
        notification.success({
          message: intl
            .get('hpfm.organization.view.message.editInformationSuccess')
            .d('编辑信息成功！'),
        });
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * 打开模态框
   */
  @Bind()
  openModal(record, title, type) {
    const { customizeForm } = this.props;
    const {
      unitId = '',
      unitName = '',
      parentUnitId = '',
      parentUnitName = '',
      unitCompanyId = '',
    } = record;
    let parentUnitIdLovDisabledFlag = false;
    let unitCodeFlag = false;
    switch (type) {
      case 'createLevelDepartment':
        this.formDS.create({ parentUnitId, parentUnitName, unitCompanyId });
        parentUnitIdLovDisabledFlag = true;
        break;
      case 'createChildrenDepartment':
        this.formDS.create({ parentUnitId: unitId, parentUnitName: unitName, unitCompanyId });
        parentUnitIdLovDisabledFlag = true;
        break;
      case 'edit':
        this.formDS.create(record);
        this.formDS.getField('parentUnitIdLov').setLovPara('unitId', record.unitId);
        this.formDS.getField('parentUnitIdLov').setLovPara('unitCompanyId', record.unitCompanyId);
        unitCodeFlag = true;
        break;
      default:
    }
    Modal.open({
      title,
      drawer: true,
      width: 520,
      children: customizeForm(
        {
          code: 'HPFM.ORG_LIST.DEPARTMENT.BASIC.EDIT',
        },
        <Form dataSet={this.formDS}>
          <TextField name="unitCode" disabled={unitCodeFlag} />
          <IntlField name="unitName" />
          <Lov name="parentUnitIdLov" disabled={parentUnitIdLovDisabledFlag} />
          <NumberField step={1} name="orderSeq" />
          <Switch name="supervisorFlag" />
          <Switch name="enabledFlag" />
        </Form>
      ),
      onOk: async () => {
        let isClose = false;
        switch (type) {
          case 'create':
            isClose = this.handleCreate();
            break;
          case 'createLevelDepartment':
            isClose = this.handleCreateLevelDepartment();
            break;
          case 'createChildrenDepartment':
            isClose = this.handleCreateChildrenDepartment();
            break;
          case 'edit':
            isClose = await this.handelEdit();
            break;
          default:
        }
        return isClose;
      },
      onCancel: () => true,
      afterClose: () => this.formDS.reset(),
    });
  }

  // 选中某一个节点
  @Bind()
  handleSelect(record) {
    const tempRecord = record.toData();
    const { unitId } = tempRecord;
    this.setState({ basicInformation: tempRecord, detailFlag: true, optionValue: undefined });
    this.employeeTableDS.setQueryParameter('unitId', unitId);
    this.employeeTableDS.query();
    this.positionTableDS.setQueryParameter('unitId', unitId);
    this.positionTableDS.query();
  }

  // 树节点渲染方式
  @Bind()
  nodeRenderer({ record }) {
    const { path } = this.props;
    const { unitCode, unitName, enabledFlag } = record.toData();
    const menu = (
      <Menu style={{ marginTop: 16 }}>
        <Menu.Item key="createLevelDepartment">
          <ButtonPermission
            type="text"
            permissionList={[
              {
                code: `${path}/createLevelDepartment`,
                type: 'button',
                meaning: '企业通讯录-新建平级部门',
              },
            ]}
            onClick={() => {
              this.handelCreateDepartment({ type: 'createLevelDepartment', record });
            }}
          >
            {intl.get('hpfm.organization.view.button.createLevelDepartment').d('新建平级部门')}
          </ButtonPermission>
        </Menu.Item>
        <Menu.Item key="createChildrenDepartment">
          <ButtonPermission
            type="text"
            permissionList={[
              {
                code: `${path}/createChildrenDepartment`,
                type: 'button',
                meaning: '企业通讯录-新建子部门',
              },
            ]}
            onClick={() => {
              this.handelCreateDepartment({ type: 'createChildrenDepartment', record });
            }}
          >
            {intl.get('hpfm.organization.view.button.createChildrenDepartment').d('新建子部门')}
          </ButtonPermission>
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="disabledDepartment">
          {enabledFlag ? (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}/disabledDepartment`,
                  type: 'button',
                  meaning: '企业通讯录-禁用部门',
                },
              ]}
              onClick={() => {
                this.handelDisabledDepartment({ record });
              }}
            >
              {intl.get('hpfm.organization.view.button.disabledDepartment').d('禁用部门')}
            </ButtonPermission>
          ) : (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}/enableDepartment`,
                  type: 'button',
                  meaning: '企业通讯录-启用部门',
                },
              ]}
              onClick={() => {
                this.handelEnableDepartment({ record });
              }}
            >
              {intl.get('hpfm.organization.view.button.enableDepartment').d('启用部门')}
            </ButtonPermission>
          )}
        </Menu.Item>
      </Menu>
    );
    return (
      <Row type="flex" justify="space-between" style={{ width: '20vw' }}>
        <Col
          span={20}
          onClick={() => this.handleSelect(record)}
          style={{ textOverflow: 'ellipsis' }}
        >
          <span>
            {unitCode}-{unitName}
          </span>
        </Col>
        <Col span={2}>
          <Dropdown overlay={menu} trigger="click">
            <Icon type="more_horiz" />
          </Dropdown>
        </Col>
      </Row>
    );
  }

  // Tabs切换
  @Bind()
  handleChange(key) {
    switch (key) {
      case 'employeeInformation':
        this.setState({
          currentTabs: key,
          placeHolder: intl
            .get('hpfm.organization.view.button.employeeHolderPlace')
            .d('员工编码/员工姓名'),
          searchFlag: true,
        });
        break;
      case 'positionInformation':
        this.setState({
          currentTabs: key,
          placeHolder: intl
            .get('hpfm.organization.view.button.jobCodeOrName')
            .d('岗位编码/岗位名称'),
          searchFlag: false,
        });
        break;
      default:
    }
  }

  // 查询员工或者岗位信息
  @Bind()
  handleSearchInformation(value) {
    this.setState({ optionValue: undefined });
    const { currentTabs, basicInformation } = this.state;
    const { unitId = '' } = basicInformation;
    if (unitId !== '') {
      if (currentTabs === 'employeeInformation') {
        this.employeeTableDS.setQueryParameter('unitId', unitId);
        this.employeeTableDS.setQueryParameter('keyWord', value);
        this.employeeTableDS.query();
      } else {
        this.positionTableDS.setQueryParameter('unitId', unitId);
        this.positionTableDS.setQueryParameter('keyWord', value);
        this.positionTableDS.query();
      }
    }
  }

  @Bind()
  async handlePositionCreate() {
    const validate = await this.positionFormDS.validate();
    if (validate) {
      const res = await this.positionFormDS.submit();
      if (res && res.success) {
        this.treeDS.query();
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * 打开创建岗位模态框
   */
  @Bind()
  handleOpenPositionModal() {
    const { customizeForm } = this.props;
    const { basicInformation } = this.state;
    const { unitId = '', unitName = '', unitCompanyId } = basicInformation;
    this.positionFormDS.create({ unitId, unitName, unitCompanyId });
    this.positionFormDS.setQueryParameter('unitCompanyId', unitCompanyId);
    Modal.open({
      title: intl.get('hpfm.organization.view.message.create').d('新建岗位'),
      drawer: true,
      width: 520,
      children: customizeForm(
        {
          code: 'HPFM.ORG_LIST.DEPARTMENT.POSITION.EDIT',
        },
        <Form dataSet={this.positionFormDS}>
          <TextField name="positionCode" />
          <IntlField name="positionName" />
          <Lov name="departmentLov" disabled />
          <NumberField step={1} name="orderSeq" />
          <Switch name="supervisorFlag" />
        </Form>
      ),
      onOk: async () => this.handlePositionCreate(),
      onCancel: () => true,
      afterClose: () => this.positionFormDS.reset(),
    });
  }

  @Bind()
  async handleEmployeeCreate({ type }) {
    if ((await this.employeeFormDS.validate()) && (await this.employeePositionFormDS)) {
      const employeeData = this.employeeFormDS.current.toJSONData();
      const employeePositionData = this.employeePositionFormDS.toData();
      const { unitId = '', positionId = '', primaryPositionFlag = 1, ...other } = employeeData;
      const list = employeePositionData.map((record) => ({
        unitId: record.unitId || '',
        positionId: record.partTimePositionLov.positionId || '',
        primaryPositionFlag: 0,
      }));
      list.push({ unitId, positionId, primaryPositionFlag });
      const resultData = { ...other, list };
      let res = true;
      if (type === 'create') {
        res = await addEmployeeDetail({
          ...resultData,
          customizeUnitCode: 'HPFM.ORG_LIST.DEPARTMENT.EMPLOYEE.EDIT',
        });
      } else {
        res = await updateEmployeeDetail({
          ...resultData,
          customizeUnitCode: 'HPFM.ORG_LIST.DEPARTMENT.EMPLOYEE.EDIT',
        });
      }
      if (res && res.failed) {
        notification.error({
          message: res.message,
        });
      } else {
        this.employeeTableDS.query();
        notification.success({
          message: intl.get('hpfm.organization.view.message.operationSuccess').d('操作成功！'),
        });
      }
      return res;
    }
    return false;
  }

  /**
   * 跳转详情页
   */
  @Bind()
  handleDetail(record) {
    const { employeeId } = record.toData();
    this.setState({ employeeId, detailFlag: false });
  }

  @Bind()
  handleBack() {
    this.setState({ detailFlag: true });
  }

  /**
   * 打开创建员工模态框
   */
  @Bind()
  handleOpenEmployeeModal({ type, employeeId = '' }) {
    Modal.open({
      title: employeeId
        ? intl.get('hpfm.organization.view.title.editEmployee').d('编辑员工')
        : intl.get('hpfm.organization.view.title.addNewEmployee').d('新建员工'),
      drawer: true,
      width: 520,
      children: (
        <CreateEmployeeDrawer
          employeeFormDS={this.employeeFormDS}
          employeePositionFormDS={this.employeePositionFormDS}
          type={type}
          employeeId={employeeId}
        />
      ),
      onOk: async () => this.handleEmployeeCreate({ type }),
      onCancel: () => true,
      afterClose: () => {
        this.employeeFormDS.loadData([]);
        this.employeePositionFormDS.loadData([]);
      },
    });
  }

  @Bind()
  changePanel() {
    const { panelKey } = this.state;
    if (panelKey === '') {
      this.setState({ panelKey: 'information' });
    } else {
      this.setState({ panelKey: '' });
    }
  }

  @Bind()
  handleChangeData(e) {
    this.setState({ searchValue: e.target.value });
  }

  // 是否主岗切换
  @Bind()
  handleSelected(value) {
    this.setState({ optionValue: value });
    const { basicInformation } = this.state;
    const { unitId = '' } = basicInformation;
    if (unitId !== '') {
      this.employeeTableDS.setQueryParameter('unitId', unitId);
      this.employeeTableDS.setQueryParameter('primaryPositionFlag', value);
      this.employeeTableDS.query();
      this.employeeTableDS.setQueryParameter('primaryPositionFlag', null);
    }
  }

  render() {
    const { path, customizeTable, customizeTabPane, customizeForm } = this.props;
    const {
      basicInformation,
      placeHolder,
      panelKey,
      employeeId,
      detailFlag,
      searchValue,
      searchFlag,
      optionValue,
    } = this.state;
    const panelKeyFlag = panelKey === 'information';
    this.baseInfoDs.create(basicInformation);
    return (
      <>
        <Row gutter={45}>
          <Col span={8}>
            <Search
              enterButton
              placeholder={intl
                .get('hpfm.organization.view.button.departmentNameOrCode')
                .d('部门编码/部门名称')}
              size="large"
              style={{ marginBottom: 10 }}
              value={searchValue}
              onChange={this.handleChangeData}
              onSearch={this.searchAllFormation}
            />
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}/createDepartment`,
                  type: 'button',
                  meaning: '企业通讯录-新建部门',
                },
              ]}
              color="primary"
              icon="add"
              style={{ width: '100%' }}
              onClick={this.handelCreateSingleDepartment}
            >
              {intl.get('hpfm.organization.view.button.createDepartment').d('新建部门')}
            </ButtonPermission>
            <div style={{ height: 420, overflow: 'auto', marginTop: 16 }}>
              <Tree
                disableCheckbox
                dataSet={this.treeDS}
                renderer={this.nodeRenderer}
                height={420}
              />
            </div>
          </Col>
          {detailFlag ? (
            <Col span={16} style={{ height: 'calc(100% - 130px)' }}>
              <Collapse activeKey={panelKey}>
                <Panel
                  showArrow={false}
                  disabled
                  header={
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <div style={{ lineHeight: '28px' }}>
                        {intl.get('hpfm.organization.view.title.baseMessage').d('基本信息')}
                      </div>
                      <div>
                        {panelKeyFlag ? (
                          <ButtonPermission
                            type="c7n-pro"
                            permissionList={[
                              {
                                code: `${path}/edit`,
                                type: 'button',
                                meaning: '企业通讯录-编辑',
                              },
                            ]}
                            color="primary"
                            onClick={() => this.handelCreateDepartment({ type: 'edit' })}
                          >
                            {intl.get('hzero.common.button.edit').d('编辑')}
                          </ButtonPermission>
                        ) : null}
                        <Button
                          color="primary"
                          funcType="flat"
                          shape="circle"
                          onClick={this.changePanel}
                          icon={panelKeyFlag ? 'expand_less' : 'expand_more'}
                        />
                      </div>
                    </div>
                  }
                  key="information"
                >
                  {customizeForm(
                    {
                      code: 'HPFM.ORG_LIST.DEPARTMENT.BASIC',
                    },
                    <Form
                      dataSet={this.baseInfoDs}
                      columns={3}
                      className={styles['base-info-form']}
                    >
                      <Output name="unitCode" />
                      <Output name="unitName" />
                      <Output name="enabledFlag" renderer={({ value }) => enableRender(value)} />
                      <Output name="parentUnitName" />
                      <Output name="unitCompanyName" />
                    </Form>
                  )}
                </Panel>
              </Collapse>
              {customizeTabPane(
                {
                  code: 'HPFM.ORG_LIST.DEPARTMENT.TABS',
                },
                <Tabs
                  className={styles['tabs-style']}
                  defaultActiveKey="employeeInformation"
                  animated={false}
                  tabBarExtraContent={
                    searchFlag ? (
                      <Group
                        style={{
                          marginTop: 4,
                          display: 'flex',
                          flexDirection: 'row',
                          justifyContent: 'space-between',
                        }}
                      >
                        <Select
                          className={styles['select-style']}
                          allowClear
                          placeholder={intl
                            .get('hpfm.organization.view.placeHolder.isPrimaryPosition')
                            .d('是否主岗')}
                          style={{ width: 110, marginRight: 10 }}
                          onChange={this.handleSelected}
                          value={optionValue}
                        >
                          <Option value="1">{intl.get('hzero.common.status.yes').d('是')}</Option>
                          <Option value="0">{intl.get('hzero.common.status.no').d('否')}</Option>
                        </Select>
                        <Search
                          enterButton
                          placeholder={placeHolder}
                          size="small"
                          onSearch={this.handleSearchInformation}
                        />
                      </Group>
                    ) : (
                      <Search
                        enterButton
                        placeholder={placeHolder}
                        size="small"
                        style={{ marginTop: 4 }}
                        onSearch={this.handleSearchInformation}
                      />
                    )
                  }
                  onChange={this.handleChange}
                >
                  <TabPane
                    tab={intl
                      .get('hpfm.organization.view.title.employeeInformationTab')
                      .d('员工信息')}
                    key="employeeInformation"
                  >
                    <Row type="flex" justify="end">
                      <Col>
                        <ButtonPermission
                          type="c7n-pro"
                          permissionList={[
                            {
                              code: `${path}/addEmployee`,
                              type: 'button',
                              meaning: '企业通讯录-添加员工',
                            },
                          ]}
                          color="primary"
                          style={{ marginBottom: 18 }}
                          onClick={() => {
                            this.handleOpenEmployeeModal({ type: 'create' });
                          }}
                        >
                          {intl.get('hpfm.organization.view.button.addEmployee').d('添加员工')}
                        </ButtonPermission>
                      </Col>
                    </Row>
                    {customizeTable(
                      {
                        code: 'HPFM.ORG_LIST.DEPARTMENT.EMPOLYEE.LINE',
                        filterCode: 'HPFM.ORG_LIST.DEPARTMENT.EMPLOYEE.FILTER',
                      },
                      <Table dataSet={this.employeeTableDS} columns={this.employeeColumn} />
                    )}
                  </TabPane>
                  <TabPane
                    tab={intl
                      .get('hpfm.organization.view.title.positionInformationTab')
                      .d('岗位信息')}
                    key="positionInformation"
                  >
                    <Row type="flex" justify="end">
                      <Col>
                        <ButtonPermission
                          type="c7n-pro"
                          permissionList={[
                            {
                              code: `${path}/addPosition`,
                              type: 'button',
                              meaning: '企业通讯录-添加岗位',
                            },
                          ]}
                          color="primary"
                          style={{ marginBottom: 18 }}
                          onClick={this.handleOpenPositionModal}
                        >
                          {intl.get('hpfm.organization.view.button.addPosition').d('添加岗位')}
                        </ButtonPermission>
                      </Col>
                    </Row>
                    {customizeTable(
                      {
                        code: 'HPFM.ORG_LIST.DEPARTMENT.POSITION.LINE',
                      },
                      <Table dataSet={this.positionTableDS} columns={this.positionColumn} />
                    )}
                  </TabPane>
                </Tabs>
              )}
            </Col>
          ) : (
            <Col span={16}>
              <Detail
                employeeId={employeeId}
                handleBack={this.handleBack}
                customizeForm={customizeForm}
              />
            </Col>
          )}
        </Row>
      </>
    );
  }
}
