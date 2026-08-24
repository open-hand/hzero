/**
 * 静态文本管理 详情
 * @date 2018-12-25
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import querystring from 'querystring';
import { Col, DatePicker, Form, Input, Row, Select, Spin, Tooltip } from 'hzero-ui';
import moment from 'moment';

import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import { getCurrentLanguage, getDateTimeFormat } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  DEFAULT_DATE_FORMAT,
  DEFAULT_DATETIME_FORMAT,
  DEFAULT_TIME_FORMAT,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  ROW_READ_WRITE_CLASSNAME,
  ROW_WRITE_ONLY_CLASSNAME,
} from 'utils/constants';
import notification from 'utils/notification';
import { CODE_UPPER } from 'utils/regExp';

import StaticTextEditor from './StaticTextEditor';

/**
 *
 * @param {boolean} test
 * @param {React.CElement} component
 * @param {React.CElement} otherComponent
 * @returns {React.CElement}
 */
function optionComponent(test, component, otherComponent) {
  return test ? component : otherComponent;
}

/**
 *
 * @param {boolean} test
 * @param {React.CElement} component
 * @param {React.CElement} otherComponent
 * @returns {React.CElement}
 */
// function npOptionComponent(test, component, otherComponent) {
//   return test ? otherComponent : component;
// }

const inputComponentPolyfillStyle = {
  width: '100%',
};

@connect(({ loading, staticText }) => {
  return {
    createStaticTextOneLoading: loading.effects['staticText/createStaticTextOne'],
    updateStaticTextOneLoading: loading.effects['staticText/updateStaticTextOne'],
    fetchStaticTextOneLoading: loading.effects['staticText/fetchStaticTextOne'],
    staticText: staticText.detail,
    lov: staticText.lov,
    currentLang: getCurrentLanguage(),
  };
})
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['entity.company', 'entity.tenant', 'entity.lang', 'hpfm.staticText'],
})
export default class StaticTextDetail extends React.Component {
  state = {
    isCreate: true,
    isChildren: false,
    extraParams: {},
  };

  staticTextEditorRef;

  @Bind()
  getCompanyLovQueryParams() {
    const {
      form,
      staticText: { record: oriRecord = {} },
    } = this.props;
    const { isCreate = true } = this.state;
    const record = isCreate ? {} : oriRecord;
    return {
      tenantId: form.getFieldValue('tenantId') || record.tenantId,
      enabledFlag: 1,
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'staticText/fetchLanguage',
    });
    this.initData();
  }

  render() {
    const { isCreate = true, isChildren, extraParams = {} } = this.state;
    const {
      staticText: { record: oriRecord = {} },
      lov: { language = [] },
      createStaticTextOneLoading,
      updateStaticTextOneLoading,
      fetchStaticTextOneLoading = false,
      form,
      match,
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
    const companyDisabled =
      isUndefined(form.getFieldValue('tenantId')) && isUndefined(record.tenantId);
    return (
      <React.Fragment>
        <Header
          title={
            isCreate
              ? intl.get('hpfm.staticText.view.message.title.create').d('创建')
              : intl.get('hpfm.staticText.view.message.title.edit').d('编辑')
          }
          backPath="/hpfm/static-text/list"
        >
          <ButtonPermission
            loading={updateLoading}
            icon="save"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '静态文本管理(平台)-保存',
              },
            ]}
            onClick={this.handleSaveBtnClick}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <div>
            <span>{intl.get('entity.lang.tag').d('语言')}: </span>
            {form.getFieldDecorator('lang', {
              initialValue: record.lang || 'zh_CN',
            })(
              <Select onChange={this.handleChangeLanguage}>
                {language.map((n) => (
                  <Select.Option key={n.code} value={n.code}>
                    {n.name}
                  </Select.Option>
                ))}
              </Select>
            )}
          </div>
        </Header>
        <Content>
          <Spin spinning={interactiveLoading}>
            <Form>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col
                  {...FORM_COL_3_LAYOUT}
                  className={
                    isCreate && !companyDisabled
                      ? ROW_WRITE_ONLY_CLASSNAME
                      : ROW_READ_WRITE_CLASSNAME
                  }
                >
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
                    label={intl.get('entity.tenant.tag').d('租户')}
                  >
                    {form.getFieldDecorator('tenantId', {
                      initialValue: record.tenantId,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('entity.tenant.tag').d('租户'),
                          }),
                        },
                      ],
                    })(
                      optionComponent(
                        isCreate,
                        <Lov
                          code="HPFM.TENANT"
                          textField="tenantName"
                          textValue={record.tenantName}
                        />,
                        <React.Fragment>{record.tenantName}</React.Fragment>
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
                      // npOptionComponent(
                      // companyDisabled,
                      <Lov
                        code="HPFM.COMPANY"
                        textField="companyName"
                        textValue={record.companyName}
                        queryParams={this.getCompanyLovQueryParams}
                        disabled={companyDisabled}
                      />
                      // <React.Fragment>{record.companyName}</React.Fragment>
                      // )
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
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
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
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
                    })(<Input />)}
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
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
            </Form>

            {(isCreate || !fetchStaticTextOneLoading) && (
              <StaticTextEditor
                content={isCreate ? undefined : record.text}
                onRef={this.handleStaticTextEditorRef}
              />
            )}
          </Spin>
        </Content>
      </React.Fragment>
    );
  }

  @Bind()
  fetchStaticTextOne(textId, lang) {
    const { dispatch } = this.props;
    dispatch({
      type: 'staticText/fetchStaticTextOne',
      payload: {
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
      default:
        break;
    }
    if (!isCreate) {
      this.fetchStaticTextOne(extraParams.textId, currentLang);
    }
    // 设置 当前编辑的 静态文本的状态
    this.setState({
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
        const { dispatch } = this.props;
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
            type: 'staticText/createStaticTextOne',
            payload: {
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
            type: 'staticText/updateStaticTextOne',
            payload: {
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
