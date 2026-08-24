/**
 * 静态文本管理 详情
 * FIXME: 使用 route-path (判断 编辑 新建 查看) 需要使用 record.tenantId !== organizationId 来判断权限
 * @date 2018-12-25
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import querystring from 'querystring';
import { Col, DatePicker, Form, Input, Row, Select, Spin, Tooltip } from 'hzero-ui';
import moment from 'moment';

import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import { getCurrentLanguage, getCurrentOrganizationId, getDateTimeFormat } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  DEFAULT_DATE_FORMAT,
  DEFAULT_DATETIME_FORMAT,
  DEFAULT_TIME_FORMAT,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  ROW_READ_ONLY_CLASSNAME,
  ROW_READ_WRITE_CLASSNAME,
  ROW_WRITE_ONLY_CLASSNAME,
} from 'utils/constants';
import notification from 'utils/notification';
import { CODE_UPPER } from 'utils/regExp';
import { dateTimeRender } from 'utils/renderer';

import StaticTextEditor from './StaticTextEditor';

/**
 * 选择渲染
 * @param {boolean} test
 * @param {React.CElement} component
 * @param {React.CElement} otherComponent
 * @returns {React.CElement}
 */
function optionComponent(test, component, otherComponent) {
  return test ? component : otherComponent;
}

const inputComponentPolyfillStyle = {
  width: '100%',
};

@connect(({ loading, staticTextOrg }) => {
  return {
    createStaticTextOneLoading: loading.effects['staticTextOrg/createStaticTextOne'],
    updateStaticTextOneLoading: loading.effects['staticTextOrg/updateStaticTextOne'],
    fetchStaticTextOneLoading: loading.effects['staticTextOrg/fetchStaticTextOne'],
    staticText: staticTextOrg.detail,
    lov: staticTextOrg.lov,
    organizationId: getCurrentOrganizationId(),
    currentLang: getCurrentLanguage(),
  };
})
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['entity.company', 'entity.lang', 'hpfm.staticText'] })
export default class StaticTextDetailOrg extends React.Component {
  state = {
    isCreate: true,
    isChildren: false,
    extraParams: {},
  };

  staticTextEditorRef;

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'staticTextOrg/fetchLanguage',
    });
    this.initData();
  }

  render() {
    const { isView = false, isCreate = true, isChildren, extraParams = {} } = this.state;
    const {
      staticText: { record: oriRecord = {} },
      lov: { language = [] },
      createStaticTextOneLoading,
      updateStaticTextOneLoading,
      fetchStaticTextOneLoading = false,
      form,
      match,
      organizationId,
    } = this.props;
    const record = isCreate ? {} : oriRecord;
    const updateLoading = createStaticTextOneLoading || updateStaticTextOneLoading;
    const interactiveLoading = updateLoading || fetchStaticTextOneLoading || false;
    const addonCode = (
      <Tooltip
        title={`${extraParams.parentTextCode || ''} ${extraParams.parentTextCode ? '.' : ''}`}
      >
        <div style={{ textOverflow: 'ellipsis', maxWidth: '120px', overflow: 'hidden' }}>
          {`${extraParams.parentTextCode || ''} ${extraParams.parentTextCode ? '.' : ''}`}
        </div>
      </Tooltip>
    );
    const textCodeAddon = isChildren ? addonCode : undefined;
    const textCodeInitial = isCreate
      ? ''
      : (record.textCode || '').replace(
          `${extraParams.parentTextCode || ''} ${extraParams.parentTextCode ? '.' : ''}`,
          ''
        );
    return (
      <React.Fragment>
        <Header
          title={
            isCreate
              ? intl.get('hpfm.staticText.view.message.title.create').d('创建')
              : intl.get('hpfm.staticText.view.message.title.edit').d('编辑')
          }
          backPath="/hpfm/static-text-org/list"
        >
          {isView ? null : (
            <ButtonPermission
              loading={updateLoading}
              icon="save"
              type="primary"
              permissionList={[
                {
                  code: `${match.path}.button.save`,
                  type: 'button',
                  meaning: '静态文本管理(租户)-保存',
                },
              ]}
              onClick={this.handleSaveBtnClick}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </ButtonPermission>
          )}
          <div>
            <span>{intl.get('entity.lang.tag').d('语言')}: </span>
            {form.getFieldDecorator('lang', {
              initialValue: record.lang || 'zh_CN',
            })(
              optionComponent(
                isView,
                <React.Fragment>
                  {
                    (language.find((item) => (record.language || 'zh_CN') === item.value) || {})
                      .meaning
                  }
                </React.Fragment>,
                <Select disabled={isView} onChange={this.handleChangeLanguage}>
                  {language.map((n) => (
                    <Select.Option key={n.code} value={n.code}>
                      {n.name}
                    </Select.Option>
                  ))}
                </Select>
              )
            )}
          </div>
        </Header>
        <Content>
          <Spin spinning={interactiveLoading}>
            <Form>
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={isCreate ? ROW_READ_ONLY_CLASSNAME : ROW_READ_WRITE_CLASSNAME}
              >
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.staticText.model.staticText.code').d('编码')}
                  >
                    {form.getFieldDecorator('textCode', {
                      initialValue: textCodeInitial,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hpfm.staticText.model.staticText.code').d('编码'),
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
                      optionComponent(
                        isCreate,
                        <Input
                          trim
                          typeCase="upper"
                          inputChinese={false}
                          addonBefore={textCodeAddon}
                        />,
                        <React.Fragment>{record.textCode}</React.Fragment>
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('entity.company.tag').d('公司')}
                  >
                    {form.getFieldDecorator('companyId', {
                      initialValue: record.companyId,
                    })(
                      optionComponent(
                        isView,
                        <React.Fragment>{record.companyName}</React.Fragment>,
                        <Lov
                          code="HPFM.COMPANY"
                          textField="companyName"
                          textValue={record.companyName}
                          queryParams={{
                            tenantId: organizationId,
                          }}
                        />
                      )
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={isView ? ROW_READ_ONLY_CLASSNAME : ROW_WRITE_ONLY_CLASSNAME}
              >
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hzero.common.date.active.from').d('有效日期从')}
                  >
                    {form.getFieldDecorator('startDate', {
                      initialValue: record.startDate
                        ? moment(record.startDate)
                        : moment(moment(new Date()).format(DEFAULT_DATE_FORMAT)),
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hzero.common.date.active.from').d('有效日期从'),
                          }),
                        },
                      ],
                    })(
                      optionComponent(
                        isView,
                        <React.Fragment>{dateTimeRender(record.startDate)}</React.Fragment>,
                        <DatePicker
                          format={getDateTimeFormat()}
                          showTime={{
                            defaultValue: moment('00:00:00', DEFAULT_TIME_FORMAT),
                          }}
                          disabledDate={(currentDate) =>
                            form.getFieldValue('endDate') &&
                            moment(form.getFieldValue('endDate')).isBefore(currentDate)
                          }
                          style={inputComponentPolyfillStyle}
                        />
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hzero.common.date.active.to').d('有效日期至')}
                  >
                    {form.getFieldDecorator('endDate', {
                      initialValue: record.endDate ? moment(record.endDate) : null,
                    })(
                      optionComponent(
                        isView,
                        <React.Fragment>{dateTimeRender(record.endDate)}</React.Fragment>,
                        <DatePicker
                          format={getDateTimeFormat()}
                          showTime={{
                            defaultValue: moment('00:00:00', DEFAULT_TIME_FORMAT),
                          }}
                          disabledDate={(currentDate) =>
                            form.getFieldValue('startDate') &&
                            moment(form.getFieldValue('startDate')).isAfter(currentDate)
                          }
                          style={inputComponentPolyfillStyle}
                        />
                      )
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={isView ? ROW_READ_ONLY_CLASSNAME : ROW_WRITE_ONLY_CLASSNAME}
              >
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.staticText.model.staticText.title').d('标题')}
                  >
                    {form.getFieldDecorator('title', {
                      initialValue: record.title,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hpfm.staticText.model.staticText.title').d('标题'),
                          }),
                        },
                        {
                          max: 120,
                          message: intl.get('hzero.common.validation.max', {
                            max: 120,
                          }),
                        },
                      ],
                    })(
                      optionComponent(
                        isView,
                        <React.Fragment>{record.title}</React.Fragment>,
                        <Input />
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.staticText.model.staticText.description').d('描述')}
                  >
                    {form.getFieldDecorator('description', {
                      initialValue: record.description,
                      rules: [
                        {
                          max: 240,
                          message: intl.get('hzero.common.validation.max', {
                            max: 240,
                          }),
                        },
                      ],
                    })(
                      optionComponent(
                        isView,
                        <React.Fragment>{record.description}</React.Fragment>,
                        <Input />
                      )
                    )}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
            {(isCreate || !fetchStaticTextOneLoading) && (
              <StaticTextEditor
                content={isCreate ? undefined : record.text}
                onRef={this.handleStaticTextEditorRef}
                disabled={isView}
              />
            )}
          </Spin>
        </Content>
      </React.Fragment>
    );
  }

  @Bind()
  fetchStaticTextOne(textId, lang) {
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'staticTextOrg/fetchStaticTextOne',
      payload: {
        organizationId,
        textId,
        params: {
          lang,
        },
      },
    });
  }

  @Bind()
  initData() {
    const {
      match: { params },
      location: { search },
      currentLang = 'zh_CN',
    } = this.props;
    const { action = 'create' } = params || {};
    let isCreate = true;
    let isChildren = false;
    let isView = false;
    const extraParams = querystring.parse(search.substring(1));
    switch (action) {
      case 'create':
        if (extraParams.parentId) {
          isChildren = true;
        }
        break;
      case 'edit':
        isCreate = false;
        break;
      case 'view':
        if (extraParams.parentId) {
          isChildren = true;
        }
        isCreate = false;
        isView = true;
        break;
      default:
        break;
    }
    if (!isCreate) {
      this.fetchStaticTextOne(extraParams.textId, currentLang);
    }
    /**
     * action isCreate isChildren isView
     * create 1         1/0       0
     * view   0         1/0       1
     * edit   0         1/0       0
     */
    // 设置 当前编辑的 静态文本的状态
    this.setState({
      isView,
      isCreate,
      isChildren,
      extraParams,
    });
  }

  @Bind()
  handleChangeLanguage(lang) {
    const { form } = this.props;
    const { isCreate = true, extraParams } = this.state;
    if (!isCreate) {
      form.resetFields();
      this.fetchStaticTextOne(extraParams.textId, lang);
    }
  }

  @Bind()
  getEditData() {
    if (!this.staticTextEditorRef) {
      return Promise.reject();
    }
    return new Promise((resolve, reject) => {
      const { form } = this.props;
      form.validateFields((err, fieldsValue) => {
        if (err) {
          reject();
        } else {
          const { editor } = (this.staticTextEditorRef.staticTextEditor || {}).current;
          if (!editor || !editor.getData()) {
            return notification.warning({
              message: intl
                .get('hpfm.staticText.view.message.alert.contentRequired')
                .d('请输入静态文本内容'),
            });
          }
          resolve({
            ...fieldsValue,
            text: editor.getData(),
            startDate: fieldsValue.startDate
              ? fieldsValue.startDate.format(DEFAULT_DATETIME_FORMAT)
              : undefined,
            endDate: fieldsValue.endDate
              ? fieldsValue.endDate.format(DEFAULT_DATETIME_FORMAT)
              : undefined,
          });
        }
      });
    });
  }

  @Bind()
  handleSaveBtnClick() {
    const { isCreate } = this.state;
    this.getEditData().then(
      (data) => {
        const { dispatch, organizationId } = this.props;
        const { extraParams = {}, isChildren } = this.state;
        let updatePromise;
        if (isCreate) {
          const params = {};
          if (isChildren) {
            params.parentId = extraParams.parentId;
            if (!data.textCode.startsWith(extraParams.parentTextCode)) {
              params.textCode = `${extraParams.parentTextCode || ''}${
                extraParams.parentTextCode ? '.' : ''
              }${data.textCode}`;
            }
          }
          updatePromise = dispatch({
            type: 'staticTextOrg/createStaticTextOne',
            payload: {
              organizationId,
              params: {
                ...data,
                ...params,
              },
            },
          });
        } else {
          const {
            staticText: { record },
          } = this.props;
          const params = {};
          if (isChildren) {
            if (!data.textCode.startsWith(extraParams.parentTextCode)) {
              params.textCode = `${extraParams.parentTextCode || ''}${
                extraParams.parentTextCode ? '.' : ''
              }${data.textCode}`;
            }
          }
          updatePromise = dispatch({
            type: 'staticTextOrg/updateStaticTextOne',
            payload: {
              organizationId,
              params: {
                ...record,
                ...data,
                ...params,
              },
            },
          });
        }
        updatePromise.then((res) => {
          if (res) {
            notification.success();
            this.reloadAfterSave(res);
          }
        });
      },
      () => {
        // 表单或者 编辑组件内部已经报错了
      }
    );
  }

  @Bind()
  handleStaticTextEditorRef(staticTextEditorRef) {
    this.staticTextEditorRef = staticTextEditorRef;
  }

  @Bind()
  reloadAfterSave(res) {
    const { extraParams } = this.state;
    this.setState({
      isCreate: false,
      extraParams: {
        ...extraParams,
        ...res,
      },
    });
    this.fetchStaticTextOne(res.textId, res.lang);
  }
}
