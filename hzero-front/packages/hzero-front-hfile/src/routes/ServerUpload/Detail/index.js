/**
 * Detail 服务器上传配置明细页
 * @date: 2019-7-4
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import React, { Component } from 'react';
import { Card, Col, Form, Input, Row, Select, Table, Spin, Modal } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isNil } from 'lodash';

import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
import Switch from 'components/Switch';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { enableRender, operatorRender } from 'utils/renderer';
import { getCurrentOrganizationId, isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

import Drawer from './Drawer';

const { Option } = Select;
/**
 * 服务器上传配置-行数据管理组件
 * @extends {Component} - React.Component
 * @return React.element
 */
@connect(({ serverUpload, loading }) => ({
  serverUpload,
  isSiteFlag: !isTenantRoleLevel(),
  tenantId: getCurrentOrganizationId(),
  fetchConfigDetailLoading: loading.effects['serverUpload/fetchConfigDetail'],
  createConfigLoading: loading.effects['serverUpload/createConfig'],
  saveConfigLoading: loading.effects['serverUpload/saveConfig'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hfile.serverUpload', 'entity.tenant'] })
export default class Detail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      editFlag: false,
      sourceType: '',
      targetItem: {},
      index: 0,
    };
  }

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    this.handleSearch();
  }

  /**
   * 根据路由id获取数据
   */
  @Bind()
  handleSearch() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    const lovCodes = { typeList: 'HFLE.SERVER.SOURCE_TYPE' };
    dispatch({
      type: 'serverUpload/init',
      payload: {
        lovCodes,
      },
    });
    dispatch({
      type: 'serverUpload/updateState',
      payload: {
        serverUploadDetail: {},
        serverConfigLineList: [],
      },
    });
    if (id !== 'create') {
      dispatch({
        type: 'serverUpload/fetchConfigDetail',
        payload: {
          configId: id,
        },
      }).then(() => {
        const {
          serverUpload: { serverUploadDetail },
        } = this.props;
        this.setState({ sourceType: serverUploadDetail.sourceType });
      });
    }
  }

  /**
   * 新建上传类型变动
   */
  @Bind()
  typeChange(value) {
    const { dispatch } = this.props;
    this.setState({ sourceType: value });

    dispatch({
      type: 'serverUpload/updateState',
      payload: {
        serverConfigLineList: [],
      },
    });
  }

  /**
   * 新建配置
   */
  @Bind()
  handleCreate() {
    const {
      dispatch,
      form,
      tenantId,
      history,
      serverUpload: { serverUploadDetail, serverConfigLineList },
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const params = {
          ...values,
        };
        dispatch({
          type: 'serverUpload/createConfig',
          payload: { tenantId, ...serverUploadDetail, ...params, serverConfigLineList },
        }).then(res => {
          if (res) {
            notification.success();
            history.push(`/hfile/server-upload/detail/${res.configId}`);
            this.handleSearch();
          }
        });
      }
    });
  }

  /**
   * 保存配置
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      form,
      serverUpload: { serverUploadDetail, serverConfigLineList },
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const params = {
          ...values,
        };
        dispatch({
          type: 'serverUpload/saveConfig',
          payload: { ...serverUploadDetail, ...params, serverConfigLineList },
        }).then(res => {
          if (res) {
            notification.success();
            this.handleSearch();
          }
        });
      }
    });
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}, flag, index) {
    const { form } = this.props;
    if (isNil(form.getFieldValue('sourceType'))) {
      Modal.confirm({
        title: intl.get('hzero.common.message.confirm.title').d('提示'),
        content: intl.get(`hzero.common.title.content`).d('必须先选择上传类型'),
      });
    } else {
      this.handleModalVisible(true);
      if (flag) {
        this.setState({ targetItem: record, index });
      }
      this.setState({ editFlag: flag });
    }
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    this.setState({ targetItem: {} });
    this.handleModalVisible(false);
  }

  /**
   * 模态窗确认
   */
  @Bind()
  handleConfirm(fieldValues) {
    const {
      dispatch,
      serverUpload: { serverConfigLineList = [] },
    } = this.props;
    const array = serverConfigLineList;
    const { editFlag, index } = this.state;
    if (editFlag) {
      array[index] = fieldValues;
    } else {
      array.push(fieldValues);
    }
    dispatch({
      type: 'serverUpload/updateState',
      payload: { serverConfigLineList: array },
    });
    this.hideModal();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form,
      fetchConfigDetailLoading = false,
      createConfigLoading = false,
      saveConfigLoading = false,
      match,
      isSiteFlag,
      serverUpload: { serverUploadDetail = {}, typeList = [], serverConfigLineList = [] },
    } = this.props;
    const { id } = match.params;
    const { targetItem = {}, modalVisible, editFlag, sourceType } = this.state;
    const headerTitle = intl.get('hfile.serverUpload.view.message.title.edit').d('服务器上传配置');
    const title = editFlag
      ? intl.get('hfile.serverUpload.view.message.drawer.edit').d('编辑')
      : intl.get('hfile.serverUpload.view.message.drawer.add').d('添加');
    const { getFieldDecorator } = form;
    const drawerProps = {
      title,
      modalVisible,
      sourceType,
      iniData: targetItem,
      onOk: this.handleConfirm,
      onCancel: this.hideModal,
      editFlag,
    };
    const columns = [
      {
        title: intl.get('hfile.serverUpload.model.serverUpload.sourceCode').d('服务器/集群编码'),
        dataIndex: 'sourceCode',
        width: 220,
      },
      {
        title: intl.get('hfile.serverUpload.model.serverUpload.sourceName').d('服务器/集群名称'),
        dataIndex: 'sourceName',
      },
      {
        title: intl.get('hfile.serverUpload.model.serverUpload.rootDir').d('根目录'),
        dataIndex: 'rootDir',
        width: 200,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        width: 120,
        render: (_, record, index) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <a
                type="text"
                permissionList={[
                  {
                    code: `${match.path}.button.edit`,
                    type: 'button',
                    meaning: '服务器上传配置明细-编辑',
                  },
                ]}
                onClick={() => this.showModal(record, true, index)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ];
    return (
      <Spin spinning={fetchConfigDetailLoading}>
        <Header title={headerTitle} backPath="/hfile/server-upload/list">
          {id === 'create' ? (
            <ButtonPermission
              type="primary"
              icon="save"
              permissionList={[
                {
                  code: `${match.path}.button.create`,
                  type: 'button',
                  meaning: '服务器上传配置明细-新建',
                },
              ]}
              onClick={this.handleCreate}
              loading={createConfigLoading}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </ButtonPermission>
          ) : (
            <ButtonPermission
              type="primary"
              icon="save"
              permissionList={[
                {
                  code: `${match.path}.button.save`,
                  type: 'button',
                  meaning: '服务器上传配置明细-保存',
                },
              ]}
              onClick={this.handleSave}
              loading={saveConfigLoading}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </ButtonPermission>
          )}
        </Header>
        <Content>
          <Card
            key="server-upload-header"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hfile.serverUpload.view.message.title').d('服务器上传配置')}</h3>}
          >
            <Form className={EDIT_FORM_CLASSNAME}>
              <Row {...EDIT_FORM_ROW_LAYOUT} style={{ display: 'flex', flexFlow: 'row wrap' }}>
                {isSiteFlag && (
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hzero.common.model.tenantName').d('租户')}
                    >
                      {getFieldDecorator('tenantId', {
                        initialValue: serverUploadDetail.tenantId,
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl.get('hzero.common.model.tenantName').d('租户'),
                            }),
                          },
                        ],
                      })(
                        id !== 'create' ? (
                          <span>{serverUploadDetail.tenantName}</span>
                        ) : (
                          <Lov
                            textValue={serverUploadDetail.tenantName}
                            code="HPFM.TENANT"
                            allowClear
                          />
                        )
                      )}
                    </Form.Item>
                  </Col>
                )}

                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hfile.serverUpload.model.serverUpload.configCode')
                      .d('配置编码')}
                  >
                    {getFieldDecorator('configCode', {
                      initialValue: serverUploadDetail.configCode,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hfile.serverUpload.model.serverUpload.configCode')
                              .d('配置编码'),
                          }),
                        },
                        {
                          pattern: CODE_UPPER,
                          message: intl
                            .get('hzero.common.validation.codeUpper')
                            .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                        },
                      ],
                    })(
                      id !== 'create' ? (
                        <span>{serverUploadDetail.configCode}</span>
                      ) : (
                        <Input trim typeCase="upper" inputChinese={false} />
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hfile.serverUpload.model.serverUpload.description').d('描述')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('description', {
                      initialValue: serverUploadDetail.description,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hfile.serverUpload.model.serverUpload.sourceType')
                      .d('上传类型')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('sourceType', {
                      initialValue: serverUploadDetail.sourceType,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hfile.serverUpload.model.serverUpload.sourceType')
                              .d('上传类型'),
                          }),
                        },
                      ],
                    })(
                      id !== 'create' ? (
                        <span>{serverUploadDetail.sourceTypeMeaning}</span>
                      ) : (
                        <Select onChange={this.typeChange} allowClear disabled={id !== 'create'}>
                          {typeList.map(item => (
                            <Option key={item.value} value={item.value}>
                              {item.meaning}
                            </Option>
                          ))}
                        </Select>
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hfile.serverUpload.model.serverUpload.defaultRootDir')
                      .d('默认根目录')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('rootDir', {
                      initialValue: serverUploadDetail.rootDir,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hfile.serverUpload.model.serverUpload.rootDir')
                              .d('根目录'),
                          }),
                        },
                      ],
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hzero.common.status.enable').d('启用')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('enabledFlag', {
                      initialValue:
                        serverUploadDetail.enabledFlag === 0 ? serverUploadDetail.enabledFlag : 1,
                    })(<Switch />)}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Card>
          <Card
            key="server-config-line"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            title={
              <h3>{intl.get('hfile.serverUpload.view.title.line').d('服务器上传配置明细')}</h3>
            }
          >
            <div className="table-list-operator">
              <ButtonPermission
                type="primary"
                permissionList={[
                  {
                    code: `${match.path}.button.addTemplate`,
                    type: 'button',
                    meaning: '服务器上传配置明细-添加',
                  },
                ]}
                onClick={() => this.showModal({}, false)}
              >
                {intl.get('hfile.serverUpload.view.button.addTemplate').d('添加')}
              </ButtonPermission>
            </div>
            <Table
              bordered
              rowKey="configLineId"
              columns={columns}
              scroll={{ x: tableScrollWidth(columns) }}
              dataSource={serverConfigLineList}
              pagination={false}
            />
          </Card>
          <Drawer {...drawerProps} />
        </Content>
      </Spin>
    );
  }
}
