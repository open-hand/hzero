import React from 'react';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Row, Col, Tree, Input } from 'choerodon-ui';
import {
  Table,
  DataSet,
  Lov,
  Modal,
  Form,
  Select,
  TextArea,
  TextField,
  Upload,
  Pagination,
} from 'choerodon-ui/pro';
import { isEmpty } from 'lodash';
import { operatorRender } from 'utils/renderer';
import { Bind } from 'lodash-decorators';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  tableDS,
  issueDetailDS,
  issueDS,
  editDS,
  treeDS,
} from '@/stores/PreposedMachineManagement/PreposedMachineManagementDS';

import { getCurrentOrganizationId, isTenantRoleLevel, getAccessToken } from 'utils/utils';
import FRONTAL_MANAGEMENT_LANG from '@/langs/frontalManagementLang';

const HZERO_HFNT = '/hfnt';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';
const accessToken = getAccessToken();

const { TreeNode } = Tree;
const { Search } = Input;

@formatterCollections({ code: ['hfnt.frontalManagement'] })
export default class FrontalLogs extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      programCodeLabel: FRONTAL_MANAGEMENT_LANG.PROGRAM_CODE,
      currentPage: 1,
      tenantList: [],
    };
    this.tableDS = new DataSet(tableDS);
    this.issueDetailDS = new DataSet(issueDetailDS);
    this.issueDS = new DataSet(issueDS);
    this.editDS = new DataSet(editDS);
    this.treeDS = new DataSet(treeDS);
    this.getTenantList();
  }

  onSelect = (selectedKeys) => {
    if (selectedKeys[0] === 'all') {
      this.tableDS.setQueryParameter('tenantId', undefined);
    } else {
      this.tableDS.setQueryParameter('tenantId', selectedKeys[0]);
    }
    this.tableDS.query();
  };

  @Bind()
  async searchTenant(e) {
    const { value } = e.target;
    await this.getTenantList();
    if (!isEmpty(value)) {
      const newTenantList = [];
      this.state.tenantList.forEach((item) => {
        if (item.tenantName.toUpperCase().indexOf(value.toUpperCase()) !== -1) {
          newTenantList.push(item);
        }
      });
      this.setState({ tenantList: newTenantList });
    }
  }

  async getTenantList() {
    await this.treeDS.query(this.state.currentPage);
    this.setState({ tenantList: this.treeDS.toData() });
  }

  @Bind()
  async openProgramInfoModal(record) {
    const { programId } = record.toData();
    this.issueDetailDS.setQueryParameter('programId', programId);
    await this.issueDetailDS.query();
    Modal.open({
      key: Modal.key(),
      title: FRONTAL_MANAGEMENT_LANG.ISSUE_DETAIL,
      children: <Table dataSet={this.issueDetailDS} columns={this.issueDetailColumns} />,
      closable: true,
      style: {
        width: 900,
      },
    });
  }

  @Bind()
  async openIssueModal(record) {
    const { programId } = record.toData();
    await this.issueDS.query();
    Modal.open({
      key: Modal.key(),
      title: FRONTAL_MANAGEMENT_LANG.ISSUE_PROGRAM,
      children: <Table dataSet={this.issueDS} columns={this.issueColumns} />,
      closable: true,
      onOk: () => this.handleIssue({ programId }),
      style: {
        width: 900,
      },
    });
  }

  @Bind()
  editForm({ isNew }) {
    const uploadProps = {
      headers: {
        'Access-Control-Allow-Origin': '*',
        Authorization: `bearer ${accessToken}`,
      },
      action: `http://hzeronb.saas.hand-china.com${HZERO_HFNT}/v1${level}/frontal-program/upload-file`,
      multiple: true,
      accept: ['.jar', '.class'],
      name: 'file',
      uploadImmediately: false,
      onUploadSuccess: (response) => {
        this.editDS.current.set('programFileUrl', response);
      },
      disabled: !isNew,
    };
    return (
      <>
        <Form dataSet={this.editDS}>
          {!(isNew && isTenantRoleLevel()) && (
            <Lov
              name="tenantLov"
              placeholder={FRONTAL_MANAGEMENT_LANG.TENANT_NAME}
              disabled={!isNew}
            />
          )}
          <Select
            name="programType"
            placeholder={FRONTAL_MANAGEMENT_LANG.PROGRAM_TYPE}
            disabled={!isNew}
            onChange={(value) => this.handleProgramCodeLabel({ value, isNew })}
          />
          <TextField
            name="programCode"
            label={this.state.programCodeLabel}
            disabled={!isNew}
            placeholder={this.state.programCodeLabel}
            restrict="a-zA-Z0-9.-"
          />
          <TextField name="programName" placeholder={FRONTAL_MANAGEMENT_LANG.PROGRAM_NAME} />
          <Select name="statusCode" placeholder={FRONTAL_MANAGEMENT_LANG.STATUS} disabled />
          <div>
            {/* <Button funcType="flat" color="green">{FRONTAL_MANAGEMENT_LANG.UPLOAD}</Button> */}
            <Upload {...uploadProps} />
          </div>
          <TextArea name="programDesc" rowSpan={2} colSpan={2} style={{ height: 80 }} />
        </Form>
      </>
    );
  }

  _modal;

  @Bind
  async handleProgramCodeLabel({ value, isNew }) {
    if (value === '.class') {
      await this.setState({ programCodeLabel: FRONTAL_MANAGEMENT_LANG.PROGRAM_CODE_CLASS });
    } else {
      await this.setState({ programCodeLabel: FRONTAL_MANAGEMENT_LANG.PROGRAM_CODE_JAR });
    }
    this._modal.update({
      children: <>{this.editForm({ isNew })}</>,
    });
  }

  @Bind()
  async openEditModal(record) {
    const { programId, statusCode } = record;
    if (statusCode === '.class') {
      await this.setState({ programCodeLabel: FRONTAL_MANAGEMENT_LANG.PROGRAM_CODE_CLASS });
    } else {
      await this.setState({ programCodeLabel: FRONTAL_MANAGEMENT_LANG.PROGRAM_CODE_JAR });
    }
    this.editDS.setQueryParameter('programId', programId);
    await this.editDS.query();
    // this.editDS.current.set('tenantName', tenantName);
    this._modal = Modal.open({
      key: Modal.key(),
      drawer: true,
      title: FRONTAL_MANAGEMENT_LANG.EDIT,
      children: <>{this.editForm({ isNew: false })}</>,
      onOk: () => this.handleOk(record),
      afterClose: () => this.editDS.reset(),
    });
  }

  @Bind()
  async openUploadModal() {
    await this.editDS.create();
    await this.setState({ programCodeLabel: FRONTAL_MANAGEMENT_LANG.PROGRAM_CODE });
    this._modal = Modal.open({
      key: Modal.key(),
      drawer: true,
      title: FRONTAL_MANAGEMENT_LANG.UPLOAD,
      children: <>{this.editForm({ isNew: true })}</>,
      onOk: () => this.handleOk(),
      afterClose: () => this.editDS.reset(),
    });
  }

  openErrorStackModal(errorStack) {
    Modal.open({
      key: Modal.key(),
      title: FRONTAL_MANAGEMENT_LANG.ERROR_STACK,
      children: <TextArea value={errorStack} cols={300} rows={25} />,
      closable: true,
    });
  }

  @Bind()
  async handleUpdateStatus(statusCode, record) {
    if (statusCode === 'DISABLED') {
      record.set('statusCode', 'ENABLE');
    } else {
      record.set('statusCode', 'DISABLED');
    }
    await this.tableDS.submit();
  }

  @Bind()
  async handleOk() {
    if (await this.editDS.validate()) {
      await this.editDS.submit();
      await this.tableDS.query();
    } else {
      return false;
    }
  }

  @Bind()
  async handleIssue({ programId }) {
    const { selected } = this.issueDS;
    selected.forEach((e) => {
      const createData = e.toData();
      createData.programId = programId;
      this.issueDS.create(createData);
    });
    this.issueDS.submit();
  }

  get managementColumns() {
    return [
      {
        name: 'tenantName',
        width: 150,
      },
      {
        name: 'programCode',
        width: 200,
        renderer: ({ record, value }) => (
          <a onClick={() => this.openProgramInfoModal(record)}>{value}</a>
        ),
      },
      {
        name: 'programType',
        width: 200,
      },
      {
        name: 'programName',
        width: 150,
      },
      {
        name: 'programDesc',
        width: 200,
      },
      {
        name: 'creationDate',
        width: 200,
      },
      {
        name: 'statusCode',
        width: 120,
      },
      {
        header: FRONTAL_MANAGEMENT_LANG.OPERATOR,
        lock: 'right',
        align: 'center',
        width: 200,
        renderer: ({ record }) => {
          const { statusCode } = record.toData();
          const actions = [
            statusCode === 'ENABLE' && {
              ele: (
                <ButtonPermission type="text" onClick={() => this.openIssueModal(record)}>
                  {FRONTAL_MANAGEMENT_LANG.ISSUE}
                </ButtonPermission>
              ),
              key: 'status',
              len: 2,
              title: FRONTAL_MANAGEMENT_LANG.ISSUE,
            },
            {
              ele: (
                <ButtonPermission type="text" onClick={() => this.openEditModal(record.toData())}>
                  {FRONTAL_MANAGEMENT_LANG.EDIT}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: FRONTAL_MANAGEMENT_LANG.EDIT,
            },
            {
              ele:
                statusCode === 'DISABLED' ? (
                  <ButtonPermission
                    type="text"
                    onClick={() => this.handleUpdateStatus(statusCode, record)}
                  >
                    {FRONTAL_MANAGEMENT_LANG.ENABLED}
                  </ButtonPermission>
                ) : (
                  <ButtonPermission
                    type="text"
                    onClick={() => this.handleUpdateStatus(statusCode, record)}
                  >
                    {FRONTAL_MANAGEMENT_LANG.DISABLED}
                  </ButtonPermission>
                ),
              key: statusCode === 'DISABLED' ? 'enabled' : 'disabled',
              len: 2,
              title:
                statusCode === 'DISABLED'
                  ? FRONTAL_MANAGEMENT_LANG.ENABLED
                  : FRONTAL_MANAGEMENT_LANG.DISABLED,
            },
          ];
          return operatorRender(actions, record);
        },
      },
    ];
  }

  get issueDetailColumns() {
    return [
      {
        name: 'tenantName',
        width: 150,
      },
      {
        name: 'frontalCode',
        width: 180,
      },
      {
        name: 'requestUrl',
        width: 200,
      },
      {
        name: 'issueStatus',
        width: 120,
      },
      {
        name: 'errorStack',
        width: 240,
        renderer: ({ value }) => <a onClick={() => this.openErrorStackModal(value)}>{value}</a>,
      },
      {
        name: 'issueDate',
        width: 200,
      },
    ];
  }

  get issueColumns() {
    return [
      {
        name: 'tenantName',
        width: 150,
      },
      {
        name: 'frontalCode',
        width: 180,
      },
      {
        name: 'requestUrl',
        width: 200,
      },
      {
        name: 'statusCode',
        width: 120,
      },
    ];
  }

  render() {
    return (
      <>
        <Header title={FRONTAL_MANAGEMENT_LANG.HEADER}>
          <ButtonPermission type="primary" onClick={this.openUploadModal}>
            {FRONTAL_MANAGEMENT_LANG.UPLOAD}
          </ButtonPermission>
        </Header>
        <Content>
          <Row>
            <Col span={5}>
              <div
                style={{
                  width: 225,
                  height: 480,
                  borderStyle: 'solid',
                  borderWidth: 1,
                  borderColor: 'rgba(91, 187, 203, 1)',
                  borderRadius: 4,
                  marginLeft: -10,
                  padding: 5,
                }}
              >
                <Search
                  style={{ marginBottom: 8 }}
                  placeholder={FRONTAL_MANAGEMENT_LANG.TENANT_NAME}
                  onChange={this.searchTenant}
                />
                <Tree
                  selectable
                  defaultExpandedKeys={['all']}
                  onSelect={this.onSelect}
                  onCheck={this.onCheck}
                >
                  <TreeNode title="全部" key="all">
                    {this.state.tenantList.map((node) => {
                      return <TreeNode title={node.tenantName} key={node.tenantId} />;
                    })}
                  </TreeNode>
                </Tree>
                <Pagination
                  dataSet={this.treeDS}
                  showSizeChangerLabel={false}
                  showSizeChanger={false}
                  showTotal={(total, range) => `${range[0]}-${range[1]} 共 ${total} 页`}
                  onChange={(page) => this.setState({ currentPage: page })}
                  style={{
                    marginLeft: -20,
                    position: 'absolute',
                    bottom: 5,
                  }}
                />
                {/* <Pagination
                  // total={this.state.tenantTotal}
                  total={28}
                  showTotal={this.handleTotal}
                  pageSize={10}
                  // defaultCurrent={1}
                  size="small"
                  tiny
                  showSizeChanger={false}
                  onChange={this.handlePage}
                  style={{
                      // marginTop: 40,
                      position: "absolute",
                      bottom: 10,
                    }
                  }
                /> */}
              </div>
            </Col>
            <Col span={19}>
              <Table dataSet={this.tableDS} columns={this.managementColumns} />
            </Col>
          </Row>
        </Content>
      </>
    );
  }
}
