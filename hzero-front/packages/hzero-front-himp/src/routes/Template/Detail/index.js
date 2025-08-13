/**
 * template - 通用导入主页面
 * @since 2019-1-28
 * @author wangjiacheng <jiacheng.wang@hand-china.com>
 * @version 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Button, Card, Col, Form, Input, Row, Select, Modal } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import classNames from 'classnames';

import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
import Switch from 'components/Switch';
import Upload from 'components/Upload/UploadButton';
import TLEditor from 'components/TLEditor';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, getEditTableData, isTenantRoleLevel } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';
import { BKT_HIMP } from 'utils/config';

import styles from './index.less';
import SheetTable from './SheetTable';

const { Option } = Select;
/**
 * Form.Item 组件label、wrapper长度比例划分
 */

/**
 * 通用模板行页面
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ template, loading }) => ({
  template,
  detailLoading: loading.effects['template/queryOneHeader'],
  saveLoading: loading.effects['template/update'] || loading.effects['template/create'],
  organizationId: getCurrentOrganizationId(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['himp.template', 'entity.template'] })
export default class Detail extends PureComponent {
  state = {
    templateTypeCode: '',
  };

  ref = React.createRef();

  componentDidMount() {
    this.queryHeader();
    const { dispatch } = this.props;
    const lovCodes = {
      typeList: 'HIMP.TEMPLATE.TEMPLATETYPE',
      sheetList: 'HIMP.IMPORT_SHEET',
    };
    dispatch({
      type: 'template/init',
      payload: {
        lovCodes,
      },
    });
  }

  queryHeader() {
    const { dispatch, match, organizationId } = this.props;
    const { id } = match.params;
    if (id !== 'create') {
      dispatch({
        type: 'template/queryOneHeader',
        payload: {
          templateId: id,
          organizationId,
        },
      });
    }
  }

  componentWillUnmount() {
    this.props.dispatch({
      type: 'template/updateState',
      payload: {
        headerData: {},
        templateTargetList: [],
      },
    });
  }

  /**
   * 保存，验证头数据和sheet行数据
   */
  @Bind()
  save() {
    const { form, template } = this.props;
    const { headerData = {}, templateTargetList = [] } = template;
    const editList = templateTargetList.filter(
      (item) => item._status === 'create' || item._status === 'update'
    );
    const params = getEditTableData(templateTargetList, ['id']);
    form.validateFields((err, values) => {
      if (headerData.templateUrl && values.templateUrl === headerData.templateUrl) {
        Modal.info({
          title: intl.get('himp.template.message.confirm.syncTemplate').d('请同步调整自定义模板'),
          onOk: () => {
            this.sendRequest(err, values, params, editList, headerData);
          },
          okText: intl.get('hzero.common.button.ok').d('确定'),
        });
      } else {
        this.sendRequest(err, values, params, editList, headerData);
      }
    });
  }

  /**
   * 发送保存请求
   * @param {object} err
   * @param {object} values
   * @param {object} params
   * @param {array} editList
   * @param {object} headerData
   * @memberof Detail
   */
  @Bind()
  sendRequest(err, values, params, editList, headerData) {
    const { dispatch, history, match } = this.props;
    const {
      params: { id },
    } = match;
    if (!err) {
      // 可以单独保存头数据
      if (Array.isArray(params) && editList.length > 0) {
        if (params.length > 0) {
          dispatch({
            type: `template/${headerData.id !== undefined ? 'update' : 'create'}`,
            payload: {
              ...headerData,
              ...values,
              templateTargetList: params,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              if (id === 'create') {
                history.push(`/himp/template/detail/${res.id}`);
                this.queryHeader();
              } else {
                this.queryHeader();
              }
            }
          });
        }
      } else {
        dispatch({
          type: `template/${headerData.id !== undefined ? 'update' : 'create'}`,
          payload: {
            ...headerData,
            ...values,
          },
        }).then((res) => {
          if (res) {
            notification.success();
            if (id === 'create') {
              history.push(`/himp/template/detail/${res.id}`);
            } else {
              this.queryHeader();
            }
          }
        });
      }
    } else {
      this.ref.current.forceUpdate();
    }
  }

  /**
   * 上传文件成功时调用
   * @param {object} file
   */
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        templateUrl: file.response,
      });
    }
  }

  /**
   * 删除文件后调用
   * @param {object} file
   */
  @Bind()
  onCancelSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        templateUrl: '',
      });
    }
  }

  render() {
    const {
      detailLoading = false,
      saveLoading = false,
      form: { getFieldDecorator, getFieldValue, setFieldsValue },
      template,
      match,
    } = this.props;
    const { templateTypeCode } = this.state;
    const {
      params: { id },
    } = match;
    const { headerData, code = {}, templateTargetList = [] } = template;
    const {
      templateCode,
      description,
      enabledFlag,
      fragmentFlag,
      prefixPatch,
      tenantId,
      tenantName,
      templateUrl,
      templateFileName,
      templateName = '',
      templateType = 'C',
      _token,
    } = headerData;
    const fileList = templateUrl
      ? [
          {
            uid: '-1',
            name: templateFileName,
            status: 'done',
            url: templateUrl,
          },
        ]
      : [];
    return (
      <>
        <Header
          title={intl.get(`himp.template.view.message.title.templateDetail`).d('导入模板管理明细')}
          backPath="/himp/template/list"
        >
          <Button onClick={this.save} type="primary" icon="save" loading={saveLoading}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          <Card
            key="import-template-header"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            loading={detailLoading}
            title={
              <h3>{intl.get(`himp.template.view.message.title.commonTemplate`).d('通用导入')}</h3>
            }
          >
            <Form className={classNames('more-fields-search-form', styles['template-form'])}>
              {!isTenantRoleLevel() && (
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('himp.template.model.template.tenantName').d('租户')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('tenantId', {
                        initialValue: tenantId,
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl.get('himp.template.model.template.tenantName').d('租户'),
                            }),
                          },
                        ],
                      })(
                        match.params.id !== 'create' ? (
                          <>{tenantName}</>
                        ) : (
                          <Lov textValue={tenantName} code="HPFM.TENANT" />
                        )
                      )}
                    </Form.Item>
                  </Col>
                </Row>
              )}
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get(`entity.template.name`).d('模板名称')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('templateName', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get(`entity.template.name`).d('模板名称'),
                          }),
                        },
                      ],
                      initialValue: templateName,
                    })(
                      <TLEditor
                        label={intl.get(`entity.template.name`).d('模板名称')}
                        field="templateName"
                        token={_token}
                      />
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get(`entity.template.code`).d('模板代码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('templateCode', {
                      initialValue: templateCode,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get(`entity.template.code`).d('模板代码'),
                          }),
                        },
                        {
                          pattern: CODE_UPPER,
                          message: intl
                            .get('hzero.common.validation.codeUpper')
                            .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                        },
                        {
                          max: 30,
                          message: intl.get('hzero.common.validation.max', {
                            max: 30,
                          }),
                        },
                      ],
                    })(
                      match.params.id !== 'create' ? (
                        <>{templateCode}</>
                      ) : (
                        <Input trim typeCase="upper" inputChinese={false} />
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get(`entity.template.type`).d('模板类型')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('templateType', {
                      rules: [
                        {
                          required: true,
                          message: intl.get(`hzero.common.validation.notNull`, {
                            name: intl.get(`entity.template.type`).d('模板类型'),
                          }),
                        },
                      ],
                      initialValue: templateType,
                    })(
                      <Select
                        onChange={(val) => {
                          this.setState({ templateTypeCode: val });
                          if (val === 'S') {
                            setFieldsValue({ prefixPatch: '' });
                          }
                        }}
                        disabled={templateTargetList.length > 0}
                      >
                        {(code['HIMP.TEMPLATE.TEMPLATETYPE'] || []).map((n) => (
                          <Option key={n.value} value={n.value}>
                            {n.meaning}
                          </Option>
                        ))}
                      </Select>
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get(`himp.template.model.template.prefixPatch`).d('客户端路径前缀')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('prefixPatch', {
                      rules: [
                        {
                          required: getFieldValue('templateType') === 'C',
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('himp.template.model.template.prefixPatch')
                              .d('客户端路径前缀'),
                          }),
                        },
                      ],
                      initialValue: prefixPatch,
                    })(<Input disabled={getFieldValue('templateType') !== 'C'} />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get(`himp.template.model.template.description`).d('描述')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('description', {
                      initialValue: description,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get(`himp.template.model.template.piece`).d('分片')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('fragmentFlag', {
                      initialValue: match.params.id === 'create' ? 0 : fragmentFlag,
                    })(<Switch />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get(`himp.template.model.template.customTemplate`).d('自定义模板')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <Upload
                      accept=".xlsx"
                      single
                      fileList={fileList}
                      bucketName={BKT_HIMP}
                      bucketDirectory="himp01"
                      onUploadSuccess={this.onUploadSuccess}
                      onRemove={this.onCancelSuccess}
                    />
                  </Form.Item>
                  <Form.Item {...EDIT_FORM_ITEM_LAYOUT}>
                    {getFieldDecorator('templateUrl', {
                      initialValue: templateUrl,
                    })(<div />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get(`hzero.common.status.enable`).d('启用')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('enabledFlag', {
                      initialValue: match.params.id === 'create' ? 1 : enabledFlag,
                    })(<Switch />)}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Card>
          <Card
            key="import-template-line"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            title={<h3>{intl.get(`himp.template.view.message.title.sheetTitle`).d('Sheet页')}</h3>}
          >
            <SheetTable
              ref={this.ref}
              loading={detailLoading}
              templateType={templateTypeCode || templateType}
              detailId={id}
            />
          </Card>
        </Content>
      </>
    );
  }
}
