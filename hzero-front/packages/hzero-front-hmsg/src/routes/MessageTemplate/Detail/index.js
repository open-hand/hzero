/**
 * MessageTemplate - 消息模板明细维护
 * @date: 2018-7-26
 * @author: WH <heng.wei@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Cascader, Col, Form, Input, Row, Select, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import classNames from 'classnames';
import MdEditor from 'react-markdown-editor-lite';
import 'react-markdown-editor-lite/lib/index.css';
import ReactMarkdown from 'react-markdown';

import Switch from 'components/Switch';
import { Content, Header } from 'components/Page';
// import TinymceEditor from 'components/TinymceEditor';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { isTenantRoleLevel } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import {
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  ROW_HALF_WRITE_ONLY_CLASSNAME,
  ROW_LAST_CLASSNAME,
  ROW_READ_ONLY_CLASSNAME,
  ROW_WRITE_ONLY_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
} from 'utils/constants';
import StaticTextEditor from './StaticTextEditor';

import Drawer from './Drawer';
import styles from './index.less';

const formItemLayout = {
  labelCol: {
    span: 8,
  },
  wrapperCol: {
    span: 16,
  },
};

const formItemLayout3 = {
  labelCol: {
    span: 3,
  },
  wrapperCol: {
    span: 21,
  },
};

function getFieldsValueByWrappedComponentRef(ref) {
  if (ref.current) {
    const { form } = ref.current.props;
    return form.getFieldsValue();
  }
  return {};
}

/**
 * 消息模板明细组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} messageTemplate - 数据源
 * @reactProps {!boolean} loading - 数据加载是否完成
 * @reactProps {!boolean} detailLoading - 明细数据加载
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */
@connect(({ messageTemplate, loading }) => ({
  messageTemplate,
  loading:
    loading.effects['messageTemplate/updateTemplate'] ||
    loading.effects['messageTemplate/addTemplate'],
  detailLoading:
    !!loading.effects['messageTemplate/fetchCopyDetail'] ||
    !!loading.effects['messageTemplate/fetchDetail'],
  detailParaLoading:
    loading.effects['messageTemplate/fetchDetailPara'] ||
    loading.effects['messageTemplate/editPara'] ||
    loading.effects['messageTemplate/deletePara'],
  detailParaInitLoading: loading.effects['messageTemplate/initPara'],
  tenantRoleLevel: isTenantRoleLevel(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hmsg.messageTemplate', 'entity.tenant', 'entity.lang'] })
export default class Detail extends PureComponent {
  constructor(props) {
    super(props);
    this.filterFormRef = React.createRef();
    this.staticTextEditor = React.createRef();
    const {
      messageTemplate: {
        detail: { templateContent },
      },
      match: {
        params: { type, id },
      },
    } = this.props;
    this.state = {
      flag: false,
      editorType: 'RT',
      spinning: !isUndefined(id) && type !== 'copy',
      prevContent: templateContent,
      modalVisible: false,
      mdContent: '',
    };
  }

  /**
   * componentDidMount
   * render()调用后获取页面数据信息
   */
  componentDidMount() {
    this.handleSearch();
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const {
      messageTemplate: {
        detail: { templateContent, editorType },
      },
    } = nextProps;
    const nextState = {};

    if (editorType !== 'MD' && (templateContent || '') !== prevState.prevContent) {
      nextState.prevContent = templateContent || '';
    }
    return nextState;
  }

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const {
      dispatch,
      match,
      history: {
        location: { payload = {} },
      },
    } = this.props;
    const { templateId, isCopy } = payload;
    const { id } = match.params;
    if (!isUndefined(id) || isCopy) {
      dispatch({
        type: isCopy ? 'messageTemplate/fetchCopyDetail' : 'messageTemplate/fetchDetail',
        payload: {
          templateId: isCopy ? templateId : id,
        },
      }).then((res) => {
        if (res) {
          this.setState({ editorType: res.editorType });
          if (res.editorType === 'MD') {
            this.setState({ mdContent: res.templateContent });
          }
        }
      });
    }
    dispatch({
      type: 'messageTemplate/fetchType',
    });
    dispatch({
      type: 'messageTemplate/fetchLanguage',
    });
  }

  /**
   * 查询模板参数
   */
  @Bind()
  handleSearchArg(params = {}) {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    const { page } = params;
    const fieldValues = getFieldsValueByWrappedComponentRef(this.filterFormRef);
    dispatch({
      type: 'messageTemplate/fetchDetailPara',
      payload: {
        argName: fieldValues.argName,
        templateId: id,
        page,
      },
    });
  }

  /**
   * 模板参数初始化
   */
  @Bind()
  handleInit() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    dispatch({
      type: 'messageTemplate/initPara',
      payload: {
        templateId: id,
      },
    }).then((res) => {
      if (res) {
        this.handleSearchArg();
      }
    });
  }

  /**
   * 模板参数编辑
   */
  @Bind()
  handleEdit(data, flag) {
    const { dispatch, messageTemplate: { paraList = [] } = {} } = this.props;
    const newList = paraList.map((item) => {
      if (data.argId === item.argId) {
        return { ...item, _status: flag ? 'update' : '' };
      } else {
        return { ...item, _status: '' };
      }
    });
    dispatch({
      type: 'messageTemplate/updateState',
      payload: { paraList: newList },
    });
  }

  /**
   * 模板参数删除
   */
  @Bind()
  handleDelete(data) {
    const {
      dispatch,
      messageTemplate: { paraPagination = {} },
    } = this.props;
    const params = data;
    delete params._status;
    delete params.$form;
    dispatch({
      type: 'messageTemplate/deletePara',
      payload: data,
    }).then((res) => {
      if (res) {
        this.handleSearchArg({ page: paraPagination });
      }
    });
  }

  /**
   * 模板参数保存
   */
  @Bind()
  handleSave(record) {
    const {
      dispatch,
      messageTemplate: { paraPagination = [] },
    } = this.props;
    const temp = record;
    delete temp._status;
    dispatch({
      type: 'messageTemplate/editPara',
      payload: temp,
    }).then((res) => {
      if (res) {
        this.handleSearchArg({ page: paraPagination });
      }
    });
  }

  /**
   * 保存
   */
  @Bind()
  handelSaveOption() {
    const {
      form,
      dispatch,
      messageTemplate,
      match: { params: tempType },
    } = this.props;
    const { detail = {} } = messageTemplate;
    const { editorType, mdContent } = this.state;
    let templateContent = '';
    form.validateFields((err, values) => {
      if (!err) {
        if (editorType !== 'MD') {
          this.getEditData().then((data) => {
            if (data.text !== undefined) {
              templateContent = data.text;
              if (templateContent === '' || isUndefined(templateContent)) {
                this.setState({
                  flag: true,
                });
                return;
              }
              // 校验通过，进行保存操作
              let type = 'messageTemplate/updateTemplate'; // 默认操作：更新
              if (!detail.templateId || tempType.type) {
                // 新建
                type = 'messageTemplate/addTemplate';
              }
              const { categoryCode, ...others } = values;
              dispatch({
                type,
                payload: {
                  ...detail,
                  ...others,
                  editorType,
                  messageCategoryCode: categoryCode[0],
                  messageSubcategoryCode: categoryCode[1],
                  templateContent,
                },
              }).then((res) => {
                if (res) {
                  notification.success();
                  if (!detail.templateId) {
                    dispatch(
                      routerRedux.push({
                        pathname: `/hmsg/message-template/detail/${res.templateId}`,
                      })
                    );
                  }
                  dispatch({
                    type: 'messageTemplate/updateState',
                    payload: { detail: res },
                  });
                }
              });
              this.setState({
                flag: false,
              });
            }
          });
        } else if (mdContent) {
          templateContent = mdContent;
          if (templateContent === '' || isUndefined(templateContent)) {
            this.setState({
              flag: true,
            });
            return;
          }
          // 校验通过，进行保存操作
          let type = 'messageTemplate/updateTemplate'; // 默认操作：更新
          if (!detail.templateId || tempType.type) {
            // 新建
            type = 'messageTemplate/addTemplate';
          }
          const { categoryCode, ...others } = values;
          dispatch({
            type,
            payload: {
              ...detail,
              ...others,
              editorType,
              messageCategoryCode: categoryCode[0],
              messageSubcategoryCode: categoryCode[1],
              templateContent,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              if (!detail.templateId) {
                dispatch(
                  routerRedux.push({
                    pathname: `/hmsg/message-template/detail/${res.templateId}`,
                  })
                );
              }
              dispatch({
                type: 'messageTemplate/updateState',
                payload: { detail: res },
              });
            }
          });
          this.setState({
            flag: false,
          });
        } else {
          notification.warning({
            message: intl
              .get('hmsg.messageTemplate.view.message.alert.contentRequiredMd')
              .d('请输入Markdown文本内容'),
          });
        }
      } else {
        this.setState({
          flag: false,
        });
      }
    });
  }

  @Bind()
  handleOpen() {
    const { dispatch } = this.props;
    const { modalVisible } = this.state;
    if (modalVisible) {
      dispatch({
        type: 'messageTemplate/updateState',
        payload: {
          paraList: [],
          paraPagination: {},
        },
      });
    } else {
      this.handleSearchArg();
    }
    this.setState({ modalVisible: !modalVisible });
  }

  @Bind()
  onRichTextEditorChange(dataSource) {
    const {
      dispatch,
      messageTemplate: { detail },
    } = this.props;
    dispatch({
      type: 'messageTemplate/updateState',
      payload: {
        detail: {
          ...detail,
          templateContent: dataSource,
        },
      },
    });
    if (dataSource === '') {
      this.setState({
        flag: true,
      });
    } else {
      this.setState({
        flag: false,
      });
    }
  }

  @Bind()
  getEditData() {
    if (!this.staticTextEditor) {
      return Promise.reject();
    }
    return new Promise((resolve, reject) => {
      const { form } = this.props;
      form.validateFields((err, fieldsValue) => {
        if (err) {
          reject();
        } else {
          const { editor } = (this.staticTextEditor.staticTextEditor || {}).current;
          if (!editor || !editor.getData()) {
            return notification.warning({
              message: intl
                .get('hmsg.messageTemplate.view.message.alert.contentRequired')
                .d('请输入静态文本内容'),
            });
          }
          resolve({
            ...fieldsValue,
            text: editor.getData(),
          });
        }
      });
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form: { getFieldDecorator, getFieldValue },
      messageTemplate,
      loading,
      detailLoading,
      detailParaLoading,
      detailParaInitLoading,
      tenantRoleLevel,
      match: {
        params: { id },
        path,
      },
      history: {
        location: { payload = {} },
      },
    } = this.props;
    const {
      language = [],
      categoryTree = [],
      paraList = [],
      paraPagination = {},
      detail = {},
      copyDetail = {},
    } = messageTemplate;
    const { isCopy } = payload;
    const { flag, spinning, prevContent, modalVisible, editorType, mdContent } = this.state;
    return (
      <>
        <Header
          title={intl.get('hmsg.messageTemplate.view.message.title.detail').d('消息模板明细')}
          backPath="/hmsg/message-template/list"
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '消息模板-保存',
              },
            ]}
            onClick={this.handelSaveOption}
            type="primary"
            icon="save"
            loading={loading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.params`,
                type: 'button',
                meaning: '消息模板-查看模板参数',
              },
            ]}
            onClick={this.handleOpen}
            icon="eye"
            loading={loading}
            disabled={isEmpty(id)}
          >
            {intl.get('hmsg.messageTemplate.model.template.templateParam').d('模板参数')}
          </ButtonPermission>
          {editorType &&
            (editorType === 'RT' ? (
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.markdown`,
                    type: 'button',
                    meaning: '消息模板-切换MarkDown',
                  },
                ]}
                onClick={() => {
                  this.setState({ editorType: 'MD' });
                }}
                icon="switcher"
                loading={loading}
              >
                {intl.get('hmsg.messageTemplate.model.template.markdown').d('切换MarkDown')}
              </ButtonPermission>
            ) : (
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.richTextEditor`,
                    type: 'button',
                    meaning: '消息模板-切换富文本编辑器',
                  },
                ]}
                onClick={() => {
                  this.setState({ editorType: 'RT' });
                }}
                icon="switcher"
                loading={loading}
              >
                {intl
                  .get('hmsg.messageTemplate.model.template.richTextEditor')
                  .d('切换富文本编辑器')}
              </ButtonPermission>
            ))}
        </Header>
        <Content>
          <Spin spinning={spinning ? detailLoading : null}>
            <Form className={classNames(styles['template-form'], SEARCH_FORM_CLASSNAME)}>
              {!tenantRoleLevel && (
                <Row
                  {...EDIT_FORM_ROW_LAYOUT}
                  className={classNames({
                    [ROW_WRITE_ONLY_CLASSNAME]: isUndefined(detail.tenantId),
                    [ROW_READ_ONLY_CLASSNAME]: !isUndefined(detail.tenantId),
                  })}
                >
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...formItemLayout}
                      label={intl.get(`entity.tenant.tag`).d('租户')}
                      required={isUndefined(detail.tenantId)}
                    >
                      {getFieldDecorator('tenantId', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl.get(`entity.tenant.tag`).d('租户'),
                            }),
                          },
                        ],
                        initialValue: detail.tenantId,
                      })(
                        isUndefined(detail.tenantId) ? (
                          <Lov code="HPFM.TENANT" textValue={detail.tenantName} />
                        ) : (
                          <>{detail.tenantName}</>
                        )
                      )}
                    </Form.Item>
                  </Col>
                </Row>
              )}
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...formItemLayout}
                    label={intl
                      .get('hmsg.messageTemplate.model.template.templateCode')
                      .d('消息模板代码')}
                    required={isUndefined(detail.templateCode)}
                  >
                    {getFieldDecorator('templateCode', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hmsg.messageTemplate.model.template.templateCode')
                              .d('消息模板代码'),
                          }),
                        },
                        {
                          pattern: CODE_UPPER,
                          message: intl
                            .get('hzero.common.validation.codeUpper')
                            .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                        },
                      ],
                      initialValue: isCopy ? copyDetail.templateCode : detail.templateCode,
                    })(
                      isUndefined(detail.templateCode) || isCopy ? (
                        <Input trim typeCase="upper" inputChinese={false} />
                      ) : (
                        <span style={{ overflowWrap: 'break-word' }}>{detail.templateCode}</span>
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...formItemLayout}
                    label={intl
                      .get('hmsg.messageTemplate.model.template.templateName')
                      .d('消息模板名称')}
                  >
                    {getFieldDecorator('templateName', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hmsg.messageTemplate.model.template.templateName')
                              .d('消息模板名称'),
                          }),
                        },
                      ],
                      initialValue: isCopy ? copyDetail.templateName : detail.templateName,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...formItemLayout}
                    label={intl
                      .get('hmsg.messageTemplate.model.template.templateTitle')
                      .d('消息模板标题')}
                  >
                    {getFieldDecorator('templateTitle', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hmsg.messageTemplate.model.template.templateTitle')
                              .d('消息模板标题'),
                          }),
                        },
                      ],
                      initialValue: isCopy ? copyDetail.templateTitle : detail.templateTitle,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item label={intl.get(`entity.lang.tag`).d('语言')} {...formItemLayout}>
                    {getFieldDecorator('lang', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get(`entity.lang.tag`).d('语言'),
                          }),
                        },
                      ],
                      initialValue: isCopy ? copyDetail.lang : detail.lang,
                    })(
                      <Select>
                        {language.map((item) => (
                          <Select.Option key={item.code} value={item.code}>
                            {item.name}
                          </Select.Option>
                        ))}
                      </Select>
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...formItemLayout}
                    label={intl
                      .get('hmsg.messageTemplate.model.template.externalCode')
                      .d('外部编码')}
                  >
                    {getFieldDecorator('externalCode', {
                      rules: [
                        {
                          required: getFieldValue('templateTypeCode') === 'SMS',
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hmsg.messageTemplate.model.template.externalCode')
                              .d('外部编码'),
                          }),
                        },
                      ],
                      initialValue: detail.externalCode,
                    })(<Input trim />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...formItemLayout}
                    label={intl
                      .get('hmsg.messageTemplate.model.template.subcategoryCode')
                      .d('消息子类型')}
                  >
                    {getFieldDecorator('categoryCode', {
                      initialValue: [detail.messageCategoryCode, detail.messageSubcategoryCode],
                    })(
                      <Cascader
                        fieldNames={{ label: 'meaning', value: 'value', children: 'children' }}
                        options={categoryTree}
                        expandTrigger="hover"
                        placeholder=""
                      />
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...formItemLayout}
                    label={intl.get('hzero.common.status.enable').d('启用')}
                  >
                    {getFieldDecorator('enabledFlag', {
                      initialValue: isUndefined(detail.enabledFlag) ? 1 : detail.enabledFlag,
                    })(<Switch />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={classNames(styles['row-item2'], ROW_HALF_WRITE_ONLY_CLASSNAME)}
              >
                <Col style={{ width: '82%' }}>
                  <Form.Item
                    {...formItemLayout3}
                    label={
                      <span className={styles.templateContentLabel}>
                        {intl
                          .get('hmsg.messageTemplate.model.template.templateContent')
                          .d('消息模板内容')}
                      </span>
                    }
                    className={flag ? styles.templateContent : ''}
                  >
                    {editorType === 'MD' ? (
                      <MdEditor
                        config={{
                          view: {
                            menu: false,
                            md: true,
                            html: true,
                          },
                        }}
                        value={mdContent}
                        onChange={(html) => {
                          this.setState({ mdContent: html.text });
                        }}
                        style={{ height: '500px' }}
                        renderHTML={(text) => <ReactMarkdown source={text} />}
                      />
                    ) : (
                      (!detailLoading ||
                        (isCopy ? false : id === undefined) ||
                        copyDetail.templateContent ||
                        prevContent ||
                        mdContent) && (
                        <StaticTextEditor
                          key={id === undefined ? 'new' : id}
                          content={
                            // eslint-disable-next-line no-nested-ternary
                            id === undefined
                              ? isCopy
                                ? copyDetail.templateContent
                                : undefined
                              : prevContent
                          }
                          readOnly={false}
                          onRef={(staticTextEditor) => {
                            this.staticTextEditor = staticTextEditor;
                          }}
                        />
                      )
                    )}
                    {flag ? (
                      <span className={styles.templateContentError}>
                        {intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get(`hmsg.messageTemplate.model.template.templateContent`)
                            .d('消息模板内容'),
                        })}
                      </span>
                    ) : null}
                  </Form.Item>
                </Col>
              </Row>
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={classNames(
                  styles['row-item2'],
                  ROW_HALF_WRITE_ONLY_CLASSNAME,
                  ROW_LAST_CLASSNAME
                )}
              >
                <Col style={{ width: '82%' }}>
                  <Form.Item
                    {...formItemLayout3}
                    label={intl.get('hmsg.messageTemplate.model.template.sqlValue').d('SQL')}
                  >
                    {getFieldDecorator('sqlValue', {
                      initialValue: isCopy ? copyDetail.sqlValue : detail.sqlValue,
                    })(<Input.TextArea autosize={{ minRows: 5, maxRows: 7 }} />)}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
            <Drawer
              visible={modalVisible}
              id={id}
              path={path}
              dataSource={paraList}
              pagination={paraPagination}
              loading={detailParaInitLoading}
              fetchLoading={detailParaLoading}
              wrappedComponentRef={this.filterFormRef}
              onInit={this.handleInit}
              onCancel={this.handleOpen}
              onEdit={this.handleEdit}
              onDelete={this.handleDelete}
              onSearch={this.handleSearchArg}
              onOk={this.handleSave}
            />
          </Spin>
        </Content>
      </>
    );
  }
}
