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
  Select,
  Output,
} from 'choerodon-ui/pro';
import {
  Row,
  Col,
  Input,
  Tabs,
  Icon,
  Dropdown,
  Menu,
  Badge,
  Collapse,
  Select as CSelect,
} from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { yesOrNoRender, enableRender } from 'utils/renderer';

import { WithCustomizeC7N as withCustomize } from 'components/Customize';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';

import { withRouter } from 'dva/router';
import {
  formDS,
  treeDS,
  employeeTableDS,
  positionTableDS,
  departmentTableDS,
  baseInfoDs,
} from '@/stores/companyDS';
import { formDS as departmentFormDS } from '@/stores/departmentDS';
import {
  updateDepartmentInformation,
  disableDepartment,
  enableDepartment,
} from '@/services/newOrganizationService';

import styles from './index.less';

const { Search, Group } = Input;
const { TabPane } = Tabs;
const { Option } = Select;
const { Panel } = Collapse;
const statusMap = ['error', 'success'];

@withRouter
@withCustomize({
  unitCode: [
    'HPFM.ORG_LIST.COMPANY.TABS',
    'HPFM.ORG_LIST.COMPANY.BASIC',
    'HPFM.ORG_LIST.COMPANY.DEPARTMENT.EDIT',
    'HPFM.ORG_LIST.COMPANY.DEPARTMENT.LINE',
    'HPFM.ORG_LIST.COMPANY.EMPOLYEE.LINE',
    'HPFM.ORG_LIST.COMPANY.POSITION.LINE',
    'HPFM.ORG_LIST.COMPANY.BASIC.EDIT',
  ],
})
export default class Company extends React.Component {
  constructor(props) {
    super(props);
    this.treeDS = new DataSet(treeDS());
    this.formDS = new DataSet(formDS());
    this.baseInfoDs = new DataSet(baseInfoDs());
    this.departmentFormDS = new DataSet(departmentFormDS());
    this.employeeTableDS = new DataSet(employeeTableDS());
    this.positionTableDS = new DataSet(positionTableDS());
    this.departmentTableDS = new DataSet(departmentTableDS());
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
        'HPFM.ORG_LIST.COMPANY.EMPOLYEE.LINE'
      );
      this.employeeTableDS.query();
      this.positionTableDS.setQueryParameter('unitId', unitId);
      this.positionTableDS.setQueryParameter(
        'customizeUnitCode',
        'HPFM.ORG_LIST.COMPANY.POSITION.LINE'
      );
      this.positionTableDS.query();
      this.departmentTableDS.setQueryParameter('unitId', unitId);
      this.departmentTableDS.setQueryParameter(
        'customizeUnitCode',
        'HPFM.ORG_LIST.COMPANY.DEPARTMENT.LINE'
      );
      this.departmentTableDS.query();
    } else {
      this.employeeTableDS.setQueryParameter('unitId', 0);
      this.employeeTableDS.setQueryParameter(
        'customizeUnitCode',
        'HPFM.ORG_LIST.COMPANY.EMPOLYEE.LINE'
      );
      this.employeeTableDS.query();
      this.positionTableDS.setQueryParameter('unitId', 0);
      this.positionTableDS.setQueryParameter(
        'customizeUnitCode',
        'HPFM.ORG_LIST.COMPANY.POSITION.LINE'
      );
      this.positionTableDS.query();
      this.departmentTableDS.setQueryParameter('unitId', 0);
      this.departmentTableDS.setQueryParameter(
        'customizeUnitCode',
        'HPFM.ORG_LIST.COMPANY.DEPARTMENT.LINE'
      );
      this.departmentTableDS.query();
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
        width: 126,
      },
      {
        name: 'email',
        width: 210,
      },
      {
        name: 'unitName',
        width: 132,
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
              textFiled = intl.get('hpfm.organization.model.company.onPosition').d('在职');
              break;
            case 'TRIAL':
              textFiled = intl.get('hpfm.organization.model.company.trial').d('试用');
              break;
            case 'INTERNSHIP':
              textFiled = intl.get('hpfm.organization.model.company.practice').d('实习');
              break;
            case 'LEAVE':
              textFiled = intl.get('hpfm.organization.model.company.leave').d('离职');
              break;
            default:
              return;
          }
          return <Badge status={statusMap[tempFlag]} text={textFiled} />;
        },
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

  get departmentColumn() {
    return [
      {
        name: 'unitCode',
      },
      {
        name: 'unitName',
      },
      {
        name: 'quickIndex',
      },
      {
        name: 'phoneticize',
      },
      {
        name: 'orderSeq',
        align: 'left',
      },
      {
        name: 'supervisorFlag',
        align: 'left',
        renderer: ({ value }) => yesOrNoRender(value),
      },
    ];
  }

  // 公司查询
  @Bind()
  searchAllFormation(record) {
    this.setState({ optionValue: undefined });
    this.treeDS.setQueryParameter('keyWord', record);
    this.treeDS.query();
  }

  // 新建或者更新公司信息
  @Bind()
  handelCreateCompany({ type, record = {} }) {
    switch (type) {
      case 'create':
        this.openModal(
          record,
          intl.get('hpfm.organization.view.message.createCompany').d('新建公司'),
          type
        );
        break;
      case 'createLevelCompany':
        this.openModal(
          record.toData(),
          intl.get('hpfm.organization.view.message.createLevelCompany').d('新建平级公司'),
          type
        );
        break;
      case 'createChildrenCompany':
        this.openModal(
          record.toData(),
          intl.get('hpfm.organization.view.message.createChildrenCompany').d('新建子公司'),
          type
        );
        break;
      case 'edit':
        this.openModal(
          this.state.basicInformation,
          intl.get('hpfm.organization.view.message.editCompany').d('编辑公司'),
          type
        );
        break;
      default:
    }
  }

  // 禁用公司
  @Bind()
  async handelDisabledCompany({ record = {} }) {
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

  // 启用公司
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
      const res = await this.formDS.submit();
      if (res && res.success) {
        this.treeDS.query();
      }
      return true;
    } else {
      return false;
    }
  }

  @Bind()
  async handleCreateLevelCompany() {
    const validate = await this.formDS.validate();
    if (validate) {
      const res = await this.formDS.submit();
      if (res && res.success) {
        this.treeDS.query();
      }
      return true;
    } else {
      return false;
    }
  }

  @Bind()
  async handleCreateChildrenCompany() {
    const validate = await this.formDS.validate();
    if (validate) {
      const res = await this.formDS.submit();
      if (res && res.success) {
        this.treeDS.query();
      }
      return true;
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
      const res = await updateDepartmentInformation({
        ...other,
        customizeUnitCode: 'HPFM.ORG_LIST.COMPANY.BASIC.EDIT',
      });
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
      unitName,
      parentUnitId = '',
      parentUnitName = '',
      unitCompanyId,
      companyId = '',
      companyName = '',
    } = record;
    debugger;
    switch (type) {
      case 'create':
        this.formDS.create({});
        break;
      case 'createLevelCompany':
        this.formDS.create({ parentUnitId, parentUnitName, unitCompanyId, companyId, companyName });
        break;
      case 'createChildrenCompany':
        this.formDS.getField('parentUnitIdLov').setLovPara('unitId', record.unitId);
        this.formDS.create({
          parentUnitIdLov: {
            parentUnitId: unitId,
            parentUnitName: unitName,
          },
          parentUnitId: unitId,
          parentUnitName: unitName,
          unitCompanyId,
          companyId,
          companyName,
        });
        break;
      case 'edit':
        this.formDS.create(record);
        break;
      default:
    }
    Modal.open({
      title,
      drawer: true,
      width: 520,
      children: customizeForm(
        {
          code: 'HPFM.ORG_LIST.COMPANY.BASIC.EDIT',
        },
        <Form dataSet={this.formDS}>
          <TextField name="unitCode" />
          <IntlField name="unitName" />
          <Select name="unitTypeCode">
            <Option value="C">
              {intl.get('hpfm.organization.view.unitTypeCode.company').d('公司')}
            </Option>
            <Option value="G">
              {intl.get('hpfm.organization.view.unitTypeCode.group').d('集团')}
            </Option>
          </Select>
          <Lov name="companyLov" />
          <Lov
            name="parentUnitIdLov"
            disabled={type === 'createChildrenCompany' || type === 'createLevelCompany'}
          />
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
          case 'createLevelCompany':
            isClose = this.handleCreateLevelCompany();
            break;
          case 'createChildrenCompany':
            isClose = this.handleCreateChildrenCompany();
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

  @Bind()
  async handlePositionCreate() {
    let flag = true;
    const validate = await this.departmentFormDS.validate();
    if (validate) {
      const res = await this.departmentFormDS.submit();
      if (res) {
        this.treeDS.query();
      }
    } else {
      flag = false;
    }
    return flag;
  }

  /**
   * 打开创建部门模态框
   */
  @Bind()
  handleOpenPositionModal() {
    const { customizeForm } = this.props;
    const { basicInformation } = this.state;
    const { unitId = '', unitName = '' } = basicInformation;
    this.departmentFormDS.create({
      unitCompanyId: unitId,
      parentUnitId: unitId,
      parentUnitName: unitName,
    });
    Modal.open({
      title: intl.get('hpfm.organization.view.title.createDepartment').d('新建部门'),
      drawer: true,
      width: 520,
      children: customizeForm(
        {
          code: 'HPFM.ORG_LIST.COMPANY.DEPARTMENT.EDIT',
        },
        <Form dataSet={this.departmentFormDS}>
          <TextField name="unitCode" />
          <IntlField name="unitName" />
          <Lov name="parentUnitIdLov" disabled />
          <NumberField step={1} name="orderSeq" />
          <Switch name="supervisorFlag" />
        </Form>
      ),
      onOk: async () => this.handlePositionCreate(),
      onCancel: () => true,
      afterClose: () => this.departmentFormDS.reset(),
    });
  }

  // 选中某一个节点
  @Bind()
  handleSelect(record) {
    const tempRecord = record.toData();
    const { unitId } = tempRecord;
    this.setState({ basicInformation: tempRecord, optionValue: undefined });
    this.employeeTableDS.setQueryParameter('unitId', unitId);
    this.employeeTableDS.query();
    this.positionTableDS.setQueryParameter('unitId', unitId);
    this.positionTableDS.query();
    this.departmentTableDS.setQueryParameter('unitId', unitId);
    this.departmentTableDS.query();
  }

  // 树节点渲染方式
  @Bind()
  nodeRenderer({ record }) {
    const {
      match: { path },
    } = this.props;
    const { unitCode, unitName, enabledFlag } = record.toData();
    const menu = (
      <Menu style={{ marginTop: 16 }}>
        <Menu.Item key="createLevelCompany">
          <ButtonPermission
            type="text"
            permissionList={[
              {
                code: `${path}/createLevelCompany`,
                type: 'button',
                meaning: '企业通讯录-新建平级公司',
              },
            ]}
            onClick={() => {
              this.handelCreateCompany({ type: 'createLevelCompany', record });
            }}
          >
            {intl.get('hpfm.organization.view.button.createLevelCompany').d('新建平级公司')}
          </ButtonPermission>
        </Menu.Item>
        <Menu.Item key="createChildrenCompany">
          <ButtonPermission
            type="text"
            permissionList={[
              {
                code: `${path}/createChildrenCompany`,
                type: 'button',
                meaning: '企业通讯录-新建子公司',
              },
            ]}
            onClick={() => {
              this.handelCreateCompany({ type: 'createChildrenCompany', record });
            }}
          >
            {intl.get('hpfm.organization.view.button.createChildrenCompany').d('新建子公司')}
          </ButtonPermission>
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="disabledCompany">
          {enabledFlag ? (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}/disabledCompany`,
                  type: 'button',
                  meaning: '企业通讯录-禁用公司',
                },
              ]}
              onClick={() => {
                this.handelDisabledCompany({ record });
              }}
            >
              {intl.get('hpfm.organization.view.button.disabledCompany').d('禁用公司')}
            </ButtonPermission>
          ) : (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}/enableCompany`,
                  type: 'button',
                  meaning: '企业通讯录-启用公司',
                },
              ]}
              onClick={() => {
                this.handelEnableDepartment({ record });
              }}
            >
              {intl.get('hpfm.organization.view.button.enableCompany').d('启用公司')}
            </ButtonPermission>
          )}
        </Menu.Item>
      </Menu>
    );
    return (
      <Row type="flex" justify="space-between" style={{ width: '20vw' }}>
        <Col span={20} onClick={() => this.handleSelect(record)}>
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
      case 'departmentInformation':
        this.setState({
          currentTabs: key,
          placeHolder: intl
            .get('hpfm.organization.view.button.departmentNameOrCode')
            .d('部门编码/部门名称'),
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
      } else if (currentTabs === 'positionInformation') {
        this.positionTableDS.setQueryParameter('unitId', unitId);
        this.positionTableDS.setQueryParameter('keyWord', value);
        this.positionTableDS.query();
      } else {
        this.departmentTableDS.setQueryParameter('unitId', unitId);
        this.departmentTableDS.setQueryParameter('keyWord', value);
        this.departmentTableDS.query();
      }
    }
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
    const {
      match: { path },
      customizeTable,
      customizeTabPane,
      customizeForm,
    } = this.props;
    const { basicInformation, placeHolder, panelKey, optionValue, searchFlag } = this.state;
    const panelKeyFlag = panelKey === 'information';
    this.baseInfoDs.create(basicInformation);
    return (
      <>
        <Row gutter={45}>
          <Col span={8}>
            <Search
              enterButton
              placeholder={intl
                .get('hpfm.organization.view.button.companyCodeOrName')
                .d('公司代码/公司名称')}
              // enterButton={intl.get('hpfm.organization.view.button.search').d('搜索')}
              size="large"
              style={{ marginBottom: 10 }}
              onSearch={this.searchAllFormation}
            />
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}/add`,
                  type: 'button',
                  meaning: '企业通讯录-新建',
                },
              ]}
              color="primary"
              icon="add"
              style={{ width: '100%' }}
              onClick={() => {
                this.handelCreateCompany({ record: basicInformation, type: 'create' });
              }} // 新建公司
            >
              {intl.get('hpfm.organization.view.button.createCompany').d('新建公司')}
            </ButtonPermission>
            <div style={{ height: 520, overflow: 'auto', marginTop: 16 }}>
              <Tree
                disableCheckbox
                dataSet={this.treeDS}
                renderer={this.nodeRenderer}
                height={520}
              />
            </div>
          </Col>
          <Col span={16}>
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
                          onClick={() => this.handelCreateCompany({ type: 'edit' })}
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
                    code: 'HPFM.ORG_LIST.COMPANY.BASIC',
                  },
                  <Form dataSet={this.baseInfoDs} columns={3} className={styles['base-info-form']}>
                    <Output name="unitCode" />
                    <Output name="unitName" />
                    <Output name="unitTypeMeaning" />
                    <Output
                      name="companyName"
                      renderer={({ text }) =>
                        text || intl.get('hpfm.organization.view.position.nothing').d('无')
                      }
                    />
                    <Output name="supervisorFlag" renderer={({ value }) => yesOrNoRender(value)} />
                    <Output name="enabledFlag" renderer={({ value }) => enableRender(value)} />
                  </Form>
                )}
              </Panel>
            </Collapse>
            {customizeTabPane(
              {
                code: 'HPFM.ORG_LIST.COMPANY.TABS',
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
                      <CSelect
                        className={styles['select-style']}
                        allowClear
                        placeholder={intl
                          .get('hpfm.organization.view.placeHolder.isPrimaryPosition')
                          .d('是否主岗')}
                        style={{ width: 110, marginRight: 10 }}
                        onChange={this.handleSelected}
                        value={optionValue}
                      >
                        <CSelect.Option value="1">
                          {intl.get('hzero.common.status.yes').d('是')}
                        </CSelect.Option>
                        <CSelect.Option value="0">
                          {intl.get('hzero.common.status.no').d('否')}
                        </CSelect.Option>
                      </CSelect>
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
                  {customizeTable(
                    {
                      code: 'HPFM.ORG_LIST.COMPANY.EMPOLYEE.LINE',
                    },
                    <Table
                      style={{ height: 300 }}
                      dataSet={this.employeeTableDS}
                      columns={this.employeeColumn}
                    />
                  )}
                </TabPane>
                <TabPane
                  tab={intl
                    .get('hpfm.organization.view.title.positionInformationTab')
                    .d('岗位信息')}
                  key="positionInformation"
                >
                  {customizeTable(
                    {
                      code: 'HPFM.ORG_LIST.COMPANY.POSITION.LINE',
                    },
                    <Table
                      style={{ height: 300 }}
                      dataSet={this.positionTableDS}
                      columns={this.positionColumn}
                    />
                  )}
                </TabPane>
                <TabPane
                  tab={intl
                    .get('hpfm.organization.view.title.departmentInformationTab')
                    .d('部门信息')}
                  key="departmentInformation"
                >
                  <Row type="flex" justify="end">
                    <Col>
                      <ButtonPermission
                        type="c7n-pro"
                        permissionList={[
                          {
                            code: `${path}/addDepartment`,
                            type: 'button',
                            meaning: '企业通讯录-添加部门',
                          },
                        ]}
                        color="primary"
                        style={{ marginBottom: 18 }}
                        onClick={this.handleOpenPositionModal}
                      >
                        {intl.get('hpfm.organization.view.button.addDepartment').d('添加部门')}
                      </ButtonPermission>
                    </Col>
                  </Row>
                  {customizeTable(
                    {
                      code: 'HPFM.ORG_LIST.COMPANY.DEPARTMENT.LINE',
                    },
                    <Table
                      style={{ height: 300 }}
                      dataSet={this.departmentTableDS}
                      columns={this.departmentColumn}
                    />
                  )}
                </TabPane>
              </Tabs>
            )}
          </Col>
        </Row>
      </>
    );
  }
}
