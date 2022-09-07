import React, { PureComponent } from 'react';
import { Button, Form, Input, Modal, Spin, Table, Row, Col } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { getCurrentOrganizationId } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';
import { SEARCH_FORM_ROW_LAYOUT, SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

import LegalForm from './LegalForm';

const FormItem = Form.Item;

@connect(({ loading, company }) => ({
  loading,
  company,
  detailLoading: loading.effects['company/queryCompany'],
  fetchLoading: loading.effects['company/fetchCompany'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: 'hpfm.company' })
export default class Company extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      companyId: '',
      organizationId: getCurrentOrganizationId(),
    };
  }

  componentDidMount() {
    this.fetchCompany();
  }

  @Bind()
  fetchCompany(params = {}) {
    const { dispatch, form } = this.props;
    const { organizationId } = this.state;
    dispatch({
      type: 'company/fetchCompany',
      payload: { organizationId, ...form.getFieldsValue(), ...params },
    });
  }

  @Bind()
  handleSearchCompany() {
    this.fetchCompany();
  }

  @Bind()
  handleResetSearch() {
    this.props.form.resetFields();
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  @Bind()
  showModal(record) {
    const { dispatch } = this.props;
    const { organizationId } = this.state;
    const { companyId } = record;
    if (companyId || companyId === 0) {
      dispatch({
        type: 'company/updateState',
        payload: { companyDetail: {} },
      });
      dispatch({
        type: 'company/queryCompany',
        payload: {
          companyId: record.companyId,
          organizationId,
        },
      });
      this.setState({
        companyId,
      });
    } else {
      dispatch({
        type: 'company/updateState',
        payload: { companyDetail: {} },
      });
      this.setState({
        companyId: '',
      });
    }
    this.handleModalVisible(true);
  }

  @Bind()
  handleCompanyAble(record, flag) {
    const { dispatch } = this.props;
    dispatch({
      type: `company/${flag ? 'enableCompany' : 'disableCompany'}`,
      payload: {
        body: record,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchCompany();
      }
    });
  }

  renderForm() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form>
        <Row gutter={24} type="flex" align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col span={8}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.company.model.company.organizationCode').d('公司编码')}
            >
              {getFieldDecorator('companyNum')(<Input trim typeCase="upper" />)}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.company.model.company.companyName').d('公司名称')}
            >
              {getFieldDecorator('companyName')(<Input />)}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem>
              <Button onClick={this.handleResetSearch}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                type="primary"
                htmlType="submit"
                style={{ marginLeft: 8 }}
                onClick={this.handleSearchCompany}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    const {
      detailLoading,
      fetchLoading,
      match,
      company: { companyList = [], companyDetail } = {},
    } = this.props;
    const { companyId, modalVisible } = this.state;
    const columns = [
      {
        title: intl.get('hpfm.company.model.company.organizationCode').d('公司编码'),
        width: 150,
        dataIndex: 'companyNum',
      },
      {
        title: intl.get('hpfm.company.model.company.companyName').d('公司名称'),
        dataIndex: 'companyName',
        render: (text, record) => (
          <ButtonPermission
            type="text"
            permissionList={[
              {
                code: `${match.path}.button.edit`,
                type: 'button',
                meaning: '公司-编辑',
              },
            ]}
            onClick={() => {
              this.showModal(record);
            }}
          >
            {text}
          </ButtonPermission>
        ),
      },
      {
        title: intl.get('hpfm.company.model.company.shortName').d('公司简称'),
        width: 200,
        dataIndex: 'shortName',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        align: 'center',
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        dataIndex: 'edit',
        width: 100,
        render: (text, record) => {
          const operators = [];
          if (record.enabledFlag === 1) {
            operators.push({
              key: 'disable',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.disable`,
                      type: 'button',
                      meaning: '公司-禁用',
                    },
                  ]}
                  onClick={() => {
                    this.handleCompanyAble(record, false);
                  }}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
            });
          } else {
            operators.push({
              key: 'enable',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.enable`,
                      type: 'button',
                      meaning: '公司-启用',
                    },
                  ]}
                  onClick={() => {
                    this.handleCompanyAble(record, true);
                  }}
                >
                  {intl.get('hzero.common.status.enable').d('启用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.status.enable').d('启用'),
            });
          }
          return operatorRender(operators);
        },
      },
    ];
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.company.view.message.title').d('公司')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '公司-新建',
              },
            ]}
            onClick={() => this.showModal({})}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content noCard>
          <div className="table-list-search">{this.renderForm()}</div>
          <Table
            bordered
            rowKey="companyId"
            loading={fetchLoading}
            dataSource={companyList}
            columns={columns}
            pagination={false}
          />
          <Modal
            destroyOnClose
            visible={modalVisible}
            width="800px"
            bodyStyle={{ padding: 0 }}
            wrapClassName="ant-modal-sidebar-right"
            transitionName="move-right"
            title={`${
              companyId
                ? intl.get('hpfm.company.view.message.title.modal.edit').d('公司信息')
                : intl.get('hpfm.company.view.message.title.modal.create').d('新建公司')
            }`}
            onCancel={() => this.handleModalVisible(false)}
            footer={null}
          >
            <Spin spinning={companyId ? detailLoading : false}>
              <LegalForm
                data={companyDetail}
                callback={() => {
                  notification.success();
                  this.handleModalVisible(false);
                  this.fetchCompany();
                }}
              />
            </Spin>
          </Modal>
        </Content>
      </React.Fragment>
    );
  }
}
