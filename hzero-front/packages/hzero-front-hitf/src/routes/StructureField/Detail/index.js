/**
 * StructureFieldDetail - 结构字段详情
 * @date: 2020-4-7
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { DataSet, Table, Form, Output, Modal, CodeArea, Col, Row } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import notification from 'utils/notification';
import JSONFormatter from 'choerodon-ui/pro/lib/code-area/formatters/JSONFormatter';
import HTMLFormatter from 'choerodon-ui/pro/lib/code-area/formatters/HTMLFormatter';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import axios from 'axios';
import {
  ENABLED_FLAG_FIELDS,
  STRUCTURE_COMPOSITION_FIELDS,
  STRUCTURE_FIELD_TYPE_FIELDS,
  STRUCTURE_FIELD_TYPE_VALUES,
} from '@/constants/CodeConstants';
import StructureFieldHeaderDS from '../../../stores/StructureField/StructureFieldHeaderDS';
import StructureFieldLineDS from '../../../stores/StructureField/StructureFieldLineDS';

import StructureFieldLineDrawer from './StructureFieldLineDrawer';

const organizationId = getCurrentOrganizationId();
const viewModalKey = Modal.key();

/**
 * 结构字段详情
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hitf.structureField', 'view.validation', 'hitf.chargeSet'] })
export default class StructureFieldDetail extends Component {
  // 行页面数据源
  lineFormDS;

  constructor(props) {
    super(props);

    // 结构字段行 数据源
    this.tableDS = new DataSet({
      ...StructureFieldLineDS(),
      autoQuery: false,
    });
    // 结构字段头 数据源
    this.formDS = new DataSet({
      ...StructureFieldHeaderDS(),
      autoQuery: false,
    });

    this.state = {
      editFlag: false, // 页面是否可以编辑
      basicInformationHidden: false, // 基础信息是否显示
      fieldLineHidden: false, // 字段明细是否显示
      structureReviewHidden: false, // 结构预览是否显示
      previewType: 'REST', // 结构预览类型
      previewContent: {}, // 结构预览内容
    };
  }

  async componentDidMount() {
    const { match } = this.props;
    // 查看/编辑页面
    if (match && match.params && match.params.headerId) {
      // 结构字段头行查询 设置查询参数
      this.formDS.queryParameter.headerId = match.params.headerId;
      this.tableDS.queryParameter.headerId = match.params.headerId;
      await this.formDS.query();
      await this.tableDS.query();

      // 结构预览数据
      const previewContent = await this.preview();

      this.setState({
        // 状态=启用 页面不可编辑
        editFlag: ENABLED_FLAG_FIELDS.YES !== this.formDS.current.get('enabledFlag'),
        // 结构预览数据
        previewContent,
      });
    }
  }

  /**
   * 删除 并返回
   * @param record 结构字段数据
   */
  @Bind()
  delete(record) {
    const { history } = this.props;

    this.formDS.delete(record).then((res) => {
      if (!res.failed) {
        history.goBack();
      }
    });
  }

  /**
   * 返回 启用/禁用 对应的多语言
   * @param {0|1} value 启用状态
   */
  @Bind()
  enableFlagRender(value) {
    return value === 1
      ? intl.get('hzero.common.status.enable').d('启用')
      : intl.get('hzero.common.status.disable').d('禁用');
  }

  /**
   * 结构预览查询
   */
  @Bind()
  async preview() {
    const headerId = this.formDS.current.get('headerId');
    const { previewType } = this.state;
    const url = isTenantRoleLevel()
      ? `${HZERO_HITF}/v1/${organizationId}/structure-field-lines/structuralPreview/${headerId}?previewType=${previewType}`
      : `${HZERO_HITF}/v1/structure-field-lines/structuralPreview/${headerId}?previewType=${previewType}`;
    return axios.get(url);
  }

  /**
   * 收起/展开
   * @param {*} type 类型 basicInformation/structureFieldLine/structureReview
   */
  @Bind()
  handleToggle(type) {
    if (type === 'basicInformation') {
      const { basicInformationHidden } = this.state;
      this.setState({
        basicInformationHidden: !basicInformationHidden,
      });
    } else if (type === 'structureFieldLine') {
      const { fieldLineHidden } = this.state;
      this.setState({
        fieldLineHidden: !fieldLineHidden,
      });
    } else if (type === 'structureReview') {
      const { structureReviewHidden } = this.state;
      this.setState({
        structureReviewHidden: !structureReviewHidden,
      });
    }
  }

  /**
   * 保存 行数据
   */
  @Bind()
  async saveLine() {
    // 先判断数据是否填写完整
    if (await this.lineFormDS.validate()) {
      await this.lineFormDS.submit();
      await this.tableDS.query();

      // 结构预览数据
      const previewContent = await this.preview();

      this.setState({
        // 结构预览数据
        previewContent,
      });
    } else {
      notification.info({
        message: intl.get('hitf.structureField.view.message.validate').d('请先完善必输内容'),
      });
      return false;
    }
  }

  /**
   * 获取字段类型下拉框的值
   */
  @Bind()
  getFieldTypeList() {
    let fieldTypeList = [
      STRUCTURE_FIELD_TYPE_VALUES.OBJECT,
      STRUCTURE_FIELD_TYPE_VALUES.ARRAY,
      STRUCTURE_FIELD_TYPE_VALUES.STRING,
      STRUCTURE_FIELD_TYPE_VALUES.DIGITAL,
      STRUCTURE_FIELD_TYPE_VALUES.BOOL,
    ];
    // 构建方式=【行结构】 字段类型下拉框去掉【对象】【数组】
    if (STRUCTURE_COMPOSITION_FIELDS.ROW === this.formDS.current.get('composition')) {
      fieldTypeList = [
        STRUCTURE_FIELD_TYPE_VALUES.STRING,
        STRUCTURE_FIELD_TYPE_VALUES.DIGITAL,
        STRUCTURE_FIELD_TYPE_VALUES.BOOL,
      ];
    }
    return fieldTypeList;
  }

  /**
   * 新建/编辑行页面
   * @param {string} openType - 页面打开类型 CREATE/EDIT
   * @param {object} record - 行记录
   */
  @Bind()
  async openDrawer(openType, record) {
    // 新建/编辑行页面数据源
    this.lineFormDS = new DataSet({
      ...StructureFieldLineDS(this.getFieldTypeList()),
      autoQuery: false,
      queryParameter: {
        lineId: record && record.get('lineId'),
      },
    });

    // 编辑，需要查询数据
    if (openType === 'EDIT') {
      await this.lineFormDS.query();
    } else {
      await this.lineFormDS.create();
      // 新建，设置headerId
      this.lineFormDS.current.set('headerId', this.formDS.current.get('headerId'));
      // 非根节点
      if (record) {
        this.lineFormDS.current.set('parentId', record.get('lineId'));
        this.lineFormDS.current.set('parentName', record.get('fieldName'));
      }
    }

    // 组件参数
    const detialProps = {
      formDS: this.lineFormDS,
      openType,
    };
    this.modal = Modal.open({
      drawer: true,
      key: viewModalKey,
      style: { width: '33%' },
      okCancel: true,
      title:
        openType === 'EDIT'
          ? intl.get('hitf.structureField.view.title.structureFieldLine.edit').d('编辑结构')
          : intl.get('hitf.structureField.view.title.structureFieldLine.create').d('新建结构'),
      children: <StructureFieldLineDrawer {...detialProps} />,
      okText: intl.get('hzero.common.button.save').d('保存'),
      onOk: this.saveLine,
    });
  }

  /**
   * 启用/禁用
   * @param {string} type - 类型 ENABLED/DISABLED
   * @param {object} record - 行记录
   */
  @Bind()
  enableOrDisable(type, record) {
    const url = isTenantRoleLevel()
      ? `${HZERO_HITF}/v1/${organizationId}/structure-field-lines/enable`
      : `${HZERO_HITF}/v1/structure-field-lines/enable`;

    let title = intl.get('hitf.structureField.view.meaasge.confirm.enable').d('确定启用？');
    let message = intl.get('hitf.structureField.view.meaasge.enable.success').d('启用成功');
    let failedMsg = intl
      .get('hitf.structureField.view.meaasge.enable.wait')
      .d('启用失败，请稍后再试。');

    if (type === 'DISABLED') {
      title = intl.get('hitf.structureField.view.meaasge.confirm.disable').d('确定禁用？');
      message = intl.get('hitf.structureField.view.meaasge.disable.success').d('禁用成功');
      failedMsg = intl
        .get('hitf.structureField.view.meaasge.disable.wait')
        .d('禁用失败，请稍后再试。');
    }

    Modal.confirm({
      title,
      onOk: async () => {
        // 请求参数
        const data = {
          ...record.toData(),
          enabledFlag: type !== 'DISABLED',
        };

        try {
          const res = await axios.put(url, data);
          if (res && res.failed) {
            notification.error({
              message: res.message,
            });
          } else {
            // 启用/禁用成功 刷新数据
            this.tableDS.query();
            notification.success({
              message,
            });
          }
        } catch (err) {
          notification.error({
            message: failedMsg,
          });
        }
      },
    });
  }

  /**
   * REST/SOAP 按钮点击事件
   * @param {string} type - 类型 REST/SOAP
   */
  @Bind()
  async changePreviewType(type) {
    this.state.previewType = type;

    // 结构预览数据
    const previewContent = await this.preview();

    this.setState({
      // 结构预览类型
      previewType: type,
      // 结构预览数据
      previewContent,
    });
  }

  render() {
    const {
      editFlag,
      basicInformationHidden,
      fieldLineHidden,
      structureReviewHidden,
      previewType,
      previewContent,
    } = this.state;
    const {
      match: { path },
    } = this.props;
    const columns = [
      {
        name: 'fieldName',
        width: 160,
      },
      {
        name: 'fieldType',
      },
      {
        name: 'fieldDesc',
      },
      {
        name: 'enabledFlag',
        renderer: ({ value }) => enableRender(value),
      },
      {
        name: 'seqNum',
      },
      {
        name: 'formatMask',
      },
      {
        name: 'defaultValue',
      },
      {
        name: 'remark',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        lock: 'right',
        align: 'center',
        width: 200,
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.addLine`,
                      type: 'button',
                      meaning: '结构字段定义-行新建',
                    },
                  ]}
                  onClick={() => this.openDrawer('CREATE', record)}
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </ButtonPermission>
              ),
              key: 'addLine',
              len: 2,
              title: intl.get('hzero.common.button.create').d('新建'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '结构字段定义-编辑',
                    },
                  ]}
                  onClick={() => this.openDrawer('EDIT', record)}
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
                      code: `${path}.button.enable`,
                      type: 'button',
                      meaning: '结构字段定义-启用',
                    },
                  ]}
                  onClick={() => this.enableOrDisable('ENABLED', record)}
                >
                  {intl.get('hzero.common.button.enable').d('启用')}
                </ButtonPermission>
              ),
              key: 'enable',
              len: 2,
              title: intl.get('hzero.common.button.enable').d('启用'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.disable`,
                      type: 'button',
                      meaning: '结构字段定义-禁用',
                    },
                  ]}
                  onClick={() => this.enableOrDisable('DISABLED', record)}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              key: 'disable',
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '结构字段定义-删除',
                    },
                  ]}
                  onClick={() => this.tableDS.delete(record)}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          // 构建方式=【树结构】+字段类型=【对象/数组】才可以【新建】
          const tempActions = actions.filter((item) => {
            if (record.get('enabledFlag') === ENABLED_FLAG_FIELDS.YES) {
              return STRUCTURE_COMPOSITION_FIELDS.TREE === this.formDS.current.get('composition') &&
                (STRUCTURE_FIELD_TYPE_FIELDS.OBJECT === record.get('fieldType') ||
                  STRUCTURE_FIELD_TYPE_FIELDS.ARRAY === record.get('fieldType'))
                ? ['disable', 'edit', 'delete', 'addLine'].includes(item.key)
                : ['disable', 'edit', 'delete'].includes(item.key);
            } else {
              return STRUCTURE_COMPOSITION_FIELDS.TREE === this.formDS.current.get('composition') &&
                (STRUCTURE_FIELD_TYPE_FIELDS.OBJECT === record.get('fieldType') ||
                  STRUCTURE_FIELD_TYPE_FIELDS.ARRAY === record.get('fieldType'))
                ? ['enable', 'edit', 'delete', 'addLine'].includes(item.key)
                : ['enable', 'edit', 'delete'].includes(item.key);
            }
          });
          return operatorRender(tempActions, record, { limit: 4 });
        },
        hidden: !editFlag,
      },
    ];
    return (
      <>
        <Header
          title={intl
            .get('hitf.structureField.view.message.title.structureFieldLine')
            .d('结构字段明细')}
          backPath="/hitf/structure-field/list"
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '结构字段明细-删除',
              },
            ]}
            icon="delete"
            type="default"
            onClick={() => this.delete(this.formDS.current)}
            disabled={!editFlag}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            key="basicInformation"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>
                {intl.get('hitf.structureField.view.message.title.basicInformation').d('基本信息')}
                <span
                  onClick={() => this.handleToggle('basicInformation')}
                  style={{ cursor: 'pointer', marginLeft: '20px', color: '#29BECE' }}
                >
                  {basicInformationHidden
                    ? `${intl.get('hzero.common.button.expand').d('展开')}∨`
                    : `${intl.get('hzero.common.button.up').d('收起')}∧`}
                </span>
              </h3>
            }
          >
            {!basicInformationHidden && (
              <Form dataSet={this.formDS} columns={3}>
                <Output name="structureName" />
                <Output name="structureNum" />
                <Output name="structureCategory" />
                <Output name="structureDesc" />
                <Output name="bizUsage" />
                <Output name="enabledFlag" renderer={({ value }) => this.enableFlagRender(value)} />
                <Output name="composition" />
              </Form>
            )}
          </Card>
          {this.formDS.current && (
            <Row>
              {/* 构建方式!=【树结构】 只展示【字段明细】 不展示【结构预览】 */}
              <Col
                span={
                  STRUCTURE_COMPOSITION_FIELDS.TREE !== this.formDS.current.get('composition')
                    ? 24
                    : 12
                }
              >
                {this.tableDS && (
                  <Card
                    key="structureFieldLine"
                    bordered={false}
                    className={DETAIL_CARD_CLASSNAME}
                    title={
                      <>
                        <h3 style={{ height: '28px' }}>
                          {intl
                            .get('hitf.structureField.model.card.structureFieldLine')
                            .d('字段明细')}
                          <span
                            onClick={() => this.handleToggle('structureFieldLine')}
                            style={{ cursor: 'pointer', marginLeft: '20px', color: '#29BECE' }}
                          >
                            {fieldLineHidden
                              ? `${intl.get('hzero.common.button.expand').d('展开')}∨`
                              : `${intl.get('hzero.common.button.up').d('收起')}∧`}
                          </span>
                          <span style={{ float: 'right' }}>
                            {/* 是否启用=禁用 且 (构建方式=行结构 或者 构建方式=树结构且字段明细为空) 显示新建按钮 */}
                            {editFlag &&
                              (STRUCTURE_COMPOSITION_FIELDS.ROW ===
                                this.formDS.current.get('composition') ||
                                (STRUCTURE_COMPOSITION_FIELDS.TREE ===
                                  this.formDS.current.get('composition') &&
                                  this.tableDS.toData().length === 0)) && (
                                <ButtonPermission
                                  permissionList={[
                                    {
                                      code: `${path}.button.structureFieldLine.create`,
                                      type: 'button',
                                      meaning: '字段明细-新建',
                                    },
                                  ]}
                                  type="primary"
                                  onClick={() => this.openDrawer('CREATE')}
                                  disabled={!editFlag}
                                >
                                  {intl.get('hzero.common.button.create').d('新建')}
                                </ButtonPermission>
                              )}
                          </span>
                        </h3>
                      </>
                    }
                  >
                    {!fieldLineHidden && (
                      <Table
                        dataSet={this.tableDS}
                        disabled={!editFlag}
                        columns={columns}
                        queryBar="none"
                        mode={
                          STRUCTURE_COMPOSITION_FIELDS.TREE ===
                          this.formDS.current.get('composition')
                            ? 'tree'
                            : 'list'
                        }
                        editMode="inline"
                      />
                    )}
                  </Card>
                )}
              </Col>
              {/* 构建方式=【树结构】 展示【结构预览】 */}
              {STRUCTURE_COMPOSITION_FIELDS.TREE === this.formDS.current.get('composition') && (
                <Col span={12} style={{ paddingLeft: '10px' }}>
                  <Card
                    key="structureReview"
                    bordered={false}
                    className={DETAIL_CARD_CLASSNAME}
                    title={
                      <>
                        <h3>
                          {intl
                            .get('hitf.structureField.view.message.title.structureReview')
                            .d('结构预览')}
                          <span
                            onClick={() => this.handleToggle('structureReview')}
                            style={{ cursor: 'pointer', marginLeft: '20px', color: '#29BECE' }}
                          >
                            {structureReviewHidden
                              ? `${intl.get('hzero.common.button.expand').d('展开')}∨`
                              : `${intl.get('hzero.common.button.up').d('收起')}∧`}
                          </span>
                          <span style={{ float: 'right' }}>
                            <ButtonPermission
                              permissionList={[
                                {
                                  code: `${path}.button.structureReview.rest`,
                                  type: 'button',
                                  meaning: '结构预览-REST',
                                },
                              ]}
                              type="default"
                              onClick={() => this.changePreviewType('REST')}
                              // style={{ lineHeight: 'inherit', height: 'inherit' }}
                            >
                              {intl.get('hitf.structureField.view.button.rest').d('REST')}
                            </ButtonPermission>
                            <ButtonPermission
                              permissionList={[
                                {
                                  code: `${path}.button.structureReview.soap`,
                                  type: 'button',
                                  meaning: '结构预览-SOAP',
                                },
                              ]}
                              type="primary"
                              onClick={() => this.changePreviewType('SOAP')}
                              style={{
                                marginLeft: '8px',
                                // lineHeight: 'inherit',
                                // height: 'inherit',
                              }}
                            >
                              {intl.get('hitf.structureField.view.button.soap').d('SOAP')}
                            </ButtonPermission>
                          </span>
                        </h3>
                      </>
                    }
                  >
                    {!structureReviewHidden && (
                      <CodeArea
                        formatter={previewType === 'REST' ? JSONFormatter : HTMLFormatter}
                        options={{ mode: 'application/json', readOnly: 'nocursor' }}
                        value={
                          previewType === 'REST' ? JSON.stringify(previewContent) : previewContent
                        }
                        style={{ height: 300 }}
                      />
                    )}
                  </Card>
                </Col>
              )}
            </Row>
          )}
        </Content>
      </>
    );
  }
}
