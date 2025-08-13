/**
 * industryCategory - 国标品类定义
 * @date: 2018-7-24
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { Button, Col, Form, Input, Modal, Row, Table } from 'hzero-ui';
import lodash from 'lodash';
import { Bind } from 'lodash-decorators';

import TLEditor from 'components/TLEditor';
import { Content, Header } from 'components/Page';
import Switch from 'components/Switch';
import cacheComponent from 'components/CacheComponent';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import { getCurrentOrganizationId } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';

import styles from './IndustryCategory.less';

/**
 * 使用 Form.Item 组件
 */
const FormItem = Form.Item;

/**
 * 国标品类弹框编辑
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} modalVisible - 控制modal显示/隐藏属性
 * @reactProps {Function} handleSave - 数据保存
 * @reactProps {Function} handleModal - 控制modal显示隐藏方法
 * @reactProps {Object} topIndustry - 一级行业
 * @reactProps {Object} currentLevel - 当前等级
 * @reactProps {Object} secondIndustry - 二级行业
 * @return React.element
 */
const CreateForm = Form.create({ fieldNameProp: null })((props) => {
  const {
    form,
    modalVisible,
    handleSave,
    editRowData,
    modalTitle,
    handleModal,
    currentLevel,
    topIndustry = {},
    secondIndustry = {},
    loading,
  } = props;
  const {
    industryCode,
    industryName,
    enabledFlag,
    categoryCode,
    categoryName,
    _token,
  } = editRowData;
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        handleSave(fieldsValue, form, currentLevel);
      }
    });
  };
  const cancelHandle = () => {
    form.resetFields();
    handleModal(false);
  };
  const formLayout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 15 },
  };
  return (
    <Modal
      title={modalTitle}
      visible={modalVisible}
      onOk={okHandle}
      width={600}
      confirmLoading={loading}
      onCancel={() => cancelHandle()}
    >
      <Form>
        {currentLevel !== 0 && (
          <div>
            <FormItem
              {...formLayout}
              label={intl
                .get('hpfm.industryCategory.model.industryCategory.topCode')
                .d('一级行业代码')}
            >
              {form.getFieldDecorator('topIndustry#industryCode', {
                initialValue: currentLevel === 1 ? industryCode : topIndustry.industryCode,
                rules:
                  currentLevel === 1
                    ? [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.industryCategory.model.industryCategory.topCode')
                              .d('一级行业代码'),
                          }),
                        },
                        {
                          max: 30,
                          message: intl.get('hzero.common.validation.max', {
                            max: 30,
                          }),
                        },
                        {
                          pattern: CODE_UPPER,
                          message: intl
                            .get('hzero.common.validation.codeUpper')
                            .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                        },
                      ]
                    : null,
              })(
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  disabled={currentLevel !== 1 || !!industryCode}
                />
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl
                .get('hpfm.industryCategory.model.industryCategory.topName')
                .d('一级行业名称')}
            >
              {form.getFieldDecorator('topIndustry#industryName', {
                initialValue: currentLevel === 1 ? industryName : topIndustry.industryName,
                rules:
                  currentLevel === 1
                    ? [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.industryCategory.model.industryCategory.topName')
                              .d('一级行业名称'),
                          }),
                        },
                        {
                          max: 40,
                          message: intl.get('hzero.common.validation.max', {
                            max: 40,
                          }),
                        },
                      ]
                    : null,
              })(
                <TLEditor
                  label={intl
                    .get('hpfm.industryCategory.model.industryCategory.topName')
                    .d('一级行业名称')}
                  field="industryName"
                  token={_token}
                  disabled={currentLevel !== 1}
                />
              )}
            </FormItem>
          </div>
        )}
        {(currentLevel === 2 || currentLevel === 3) && (
          <div>
            <FormItem
              {...formLayout}
              label={intl
                .get('hpfm.industryCategory.model.industryCategory.secondCode')
                .d('二级行业代码')}
            >
              {form.getFieldDecorator('secondIndustry#industryCode', {
                initialValue: currentLevel === 2 ? industryCode : secondIndustry.industryCode,
                rules:
                  currentLevel === 2
                    ? [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.industryCategory.model.industryCategory.secondCode')
                              .d('二级行业代码'),
                          }),
                        },
                        {
                          max: 30,
                          message: intl.get('hzero.common.validation.max', {
                            max: 30,
                          }),
                        },
                        {
                          pattern: CODE_UPPER,
                          message: intl
                            .get('hzero.common.validation.codeUpper')
                            .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                        },
                      ]
                    : null,
              })(
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  disabled={currentLevel !== 2 || !!industryCode}
                />
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl
                .get('hpfm.industryCategory.model.industryCategory.secondName')
                .d('二级行业名称')}
            >
              {form.getFieldDecorator('secondIndustry#industryName', {
                initialValue: currentLevel === 2 ? industryName : secondIndustry.industryName,
                rules:
                  currentLevel === 2
                    ? [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.industryCategory.model.industryCategory.secondName')
                              .d('二级行业名称'),
                          }),
                        },
                        {
                          max: 40,
                          message: intl.get('hzero.common.validation.max', {
                            max: 40,
                          }),
                        },
                      ]
                    : null,
              })(
                <TLEditor
                  label={intl
                    .get('hpfm.industryCategory.model.industryCategory.secondName')
                    .d('二级行业名称')}
                  field="industryName"
                  token={_token}
                  disabled={currentLevel !== 2}
                />
              )}
            </FormItem>
          </div>
        )}
        {currentLevel === 3 && (
          <div>
            <FormItem
              {...formLayout}
              label={intl
                .get('hpfm.industryCategory.model.industryCategory.categoryCode')
                .d('品类代码')}
            >
              {form.getFieldDecorator('categoryCode', {
                initialValue: categoryCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.industryCategory.model.industryCategory.categoryCode')
                        .d('品类代码'),
                    }),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(<Input trim typeCase="upper" inputChinese={false} disabled={!!categoryCode} />)}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl
                .get('hpfm.industryCategory.model.industryCategory.categoryName')
                .d('品类名称')}
            >
              {form.getFieldDecorator('categoryName', {
                initialValue: categoryName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.industryCategory.model.industryCategory.categoryName')
                        .d('品类名称'),
                    }),
                  },
                  {
                    max: 40,
                    message: intl.get('hzero.common.validation.max', {
                      max: 40,
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl
                    .get('hpfm.industryCategory.model.industryCategory.categoryName')
                    .d('品类名称')}
                  field="categoryName"
                  token={_token}
                />
              )}
            </FormItem>
          </div>
        )}
        <FormItem {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
          {form.getFieldDecorator('enabledFlag', {
            initialValue: enabledFlag === undefined ? 1 : enabledFlag,
          })(<Switch />)}
        </FormItem>
      </Form>
    </Modal>
  );
});

/**
 * 国标品类定义
 * @extends {Component} - React.Component
 * @reactProps {Object} industryCategory - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hpfm.industryCategory'] })
@connect(({ industryCategory, loading }) => ({
  industryCategory,
  tenantId: getCurrentOrganizationId(),
  fetchTopLoading: loading.effects['industryCategory/fetchTopCategory'],
  fetchSecondLoading: loading.effects['industryCategory/fetchSecondCategory'],
  fetchCategoryLoading: loading.effects['industryCategory/fetchCategory'],
  updateIndustryLoading: loading.effects['industryCategory/updateIndustry'],
  updateCategoryLoading: loading.effects['industryCategory/updateCategory'],
  addIndustryLoading: loading.effects['industryCategory/addIndustry'],
  addCategoryLoading: loading.effects['industryCategory/addCategory'],
}))
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hpfm/mdm/industry-category' })
export default class IndustryCategory extends Component {
  /**
   * 内部状态
   */
  state = {
    modalVisible: false,
    editRowData: {},
    modalTitle: '',
    currentLevel: 0,
    topIndustry: {},
    secondIndustry: {},
    updateFlag: false,
    topCurrentRow: 0,
    secondCurrentRow: 0,
  };

  /**
   * 挂载后执行方法
   */
  componentDidMount() {
    this.queryTopIndustry();
  }

  /**
   * 查询一级行业
   * @param {String} industryName 行业名称
   */
  @Bind()
  queryTopIndustry() {
    const { dispatch, tenantId, form } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        dispatch({
          type: 'industryCategory/fetchTopCategory',
          payload: {
            tenantId,
            industryName: fieldsValue.topIndustryName,
          },
        }).then((response) => {
          dispatch({
            type: 'industryCategory/queryTopCategory',
            payload: response,
          });
          if (response && response[0]) {
            this.setState({
              topIndustry: response[0],
              topCurrentRow: 0,
            });
            this.querySecondIndustry(response[0].industryId);
          }
        });
      }
    });
  }

  /**
   * 查询二级行业
   * @param {String} industryId 行业id
   * @param {String} industryName 行业名称
   */
  @Bind()
  querySecondIndustry(industryId) {
    const { dispatch, tenantId, form } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        if (industryId) {
          dispatch({
            type: 'industryCategory/fetchSecondCategory',
            payload: {
              tenantId,
              parentIndustryId: industryId,
              industryName: fieldsValue.secondIndustryName,
            },
          }).then((response) => {
            dispatch({
              type: 'industryCategory/querySecondCategory',
              payload: response,
            });
            if (response && response[0]) {
              this.setState({
                secondIndustry: response[0],
                secondCurrentRow: 0,
              });
              this.queryCategory(response[0].industryId);
            } else {
              // 当二级行业没有数据时，重置当前的二级行业对象
              this.setState({ secondIndustry: {} });
              dispatch({
                type: 'industryCategory/clearCategory',
                payload: [],
              });
            }
          });
        }
      }
    });
  }

  /**
   * 查询品类
   * @param {String} industryId 行业id
   * @param {String} categoryName 品类名称
   */
  @Bind()
  queryCategory(industryId) {
    const { dispatch, tenantId, form } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        if (industryId) {
          dispatch({
            type: 'industryCategory/fetchCategory',
            payload: {
              tenantId,
              industryId,
              categoryName: fieldsValue.categoryName,
            },
          }).then((response) => {
            dispatch({
              type: 'industryCategory/queryCategory',
              payload: response,
            });
          });
        }
      }
    });
  }

  /**
   * 点击查询按钮后的统一处理事件
   * @param {number} level 等级
   */
  @Bind()
  queryValue(level) {
    const { topIndustry, secondIndustry } = this.state;
    if (level === 1) {
      this.queryTopIndustry();
    } else if (level === 2) {
      this.querySecondIndustry(topIndustry.industryId);
    } else if (level === 3) {
      this.queryCategory(secondIndustry.industryId);
    }
  }

  /**
   * 通过不同的等级设置弹出modal的标题
   * @param {number} level 等级
   */
  @Bind()
  setModalTitle(level) {
    if (level === 1) {
      this.setState({
        modalTitle: intl
          .get('hpfm.industryCategory.view.message.title.modal.top')
          .d('一级行业维护'),
        currentLevel: 1,
      });
    } else if (level === 2) {
      this.setState({
        modalTitle: intl
          .get('hpfm.industryCategory.view.message.title.modal.second')
          .d('二级行业维护'),
        currentLevel: 2,
      });
    } else if (level === 3) {
      this.setState({
        modalTitle: intl
          .get('hpfm.industryCategory.view.message.title.modal.category')
          .d('品类维护'),
        currentLevel: 3,
      });
    } else {
      this.setState({
        modalTitle: '',
        currentLevel: 0,
      });
    }
  }

  /**
   * 控制modal弹框的显示和隐藏
   * @param {boolean} flag 显示/隐藏标记
   * @param {number} [level=0] 等级
   * @param {Object} [record={}] 当前行记录
   */
  @Bind()
  handleModal(flag, level = 0, record = {}) {
    this.setState({
      modalVisible: flag,
      editRowData: record,
    });
    if (record.industryId) {
      this.setState({
        updateFlag: true,
      });
    } else {
      this.setState({
        updateFlag: false,
      });
    }
    this.setModalTitle(level);
    if (!flag) {
      this.setState({
        editRowData: {},
        modalTitle: '',
        currentLevel: 0,
        updateFlag: false,
      });
    }
  }

  /**
   * 设定二级行业数据
   * @param {Object} record 当前行数据
   */
  @Bind()
  setSecondIndustry(record) {
    if (record && record.industryId) {
      this.setState({
        topIndustry: record,
      });
      this.querySecondIndustry(record.industryId);
    }
  }

  /**
   * 设定品类的数据
   * @param {Object} record 当前行数据
   */
  @Bind()
  setCategory(record) {
    if (record && record.industryId) {
      this.setState({
        secondIndustry: record,
      });
      this.queryCategory(record.industryId);
    }
  }

  /**
   * 成功操作
   * @param {Object} form 当前form
   */
  @Bind()
  successAction(form) {
    form.resetFields();
    notification.success();
    this.handleModal(false);
  }

  /**
   * 数据保存
   * @param {Object} fieldsValue form中数据
   * @param {Object} form form表单
   * @param {number} level 等级
   */
  @Bind()
  saveData(fieldsValue, form, level) {
    const { dispatch, tenantId } = this.props;
    const { topIndustry, secondIndustry } = this.state;
    if (level === 1) {
      dispatch({
        type: 'industryCategory/addIndustry',
        payload: {
          tenantId,
          industryCode: lodash.trim(fieldsValue['topIndustry#industryCode']),
          industryName: fieldsValue['topIndustry#industryName'],
          enabledFlag: fieldsValue.enabledFlag,
        },
      }).then((response) => {
        if (response) {
          this.successAction(form);
          this.queryTopIndustry();
        }
      });
    } else if (level === 2) {
      dispatch({
        type: 'industryCategory/addIndustry',
        payload: {
          tenantId,
          industryCode: lodash.trim(fieldsValue['secondIndustry#industryCode']),
          industryName: fieldsValue['secondIndustry#industryName'],
          enabledFlag: fieldsValue.enabledFlag,
          parentIndustryId: this.state.topIndustry.industryId,
        },
      }).then((response) => {
        if (response) {
          this.successAction(form);
          this.querySecondIndustry(topIndustry.industryId);
        }
      });
    } else if (level === 3) {
      dispatch({
        type: 'industryCategory/addCategory',
        payload: {
          tenantId,
          categoryCode: lodash.trim(fieldsValue.categoryCode),
          categoryName: fieldsValue.categoryName,
          enabledFlag: fieldsValue.enabledFlag,
          industryId: this.state.secondIndustry.industryId,
        },
      }).then((response) => {
        if (response) {
          this.successAction(form);
          this.queryCategory(secondIndustry.industryId);
        }
      });
    }
  }

  /**
   * 更新数据
   * @param {Object} fieldsValue form表单数据
   * @param {Object} form form表单
   * @param {number} level 等级
   */
  @Bind()
  updateData(fieldsValue, form, level) {
    const { dispatch, tenantId } = this.props;
    const { editRowData, topIndustry, secondIndustry } = this.state;
    if (level === 1) {
      dispatch({
        type: 'industryCategory/updateIndustry',
        payload: {
          tenantId,
          ...editRowData,
          industryCode: lodash.trim(fieldsValue['topIndustry#industryCode']),
          industryName: fieldsValue['topIndustry#industryName'],
          enabledFlag: fieldsValue.enabledFlag,
          _tls: fieldsValue._tls,
        },
      }).then((response) => {
        if (response) {
          this.successAction(form);
          this.queryTopIndustry();
        }
      });
    } else if (level === 2) {
      dispatch({
        type: 'industryCategory/updateIndustry',
        payload: {
          tenantId,
          ...editRowData,
          industryCode: lodash.trim(fieldsValue['secondIndustry#industryCode']),
          industryName: fieldsValue['secondIndustry#industryName'],
          enabledFlag: fieldsValue.enabledFlag,
          _tls: fieldsValue._tls,
        },
      }).then((response) => {
        if (response) {
          this.successAction(form);
          this.querySecondIndustry(topIndustry.industryId);
        }
      });
    } else if (level === 3) {
      dispatch({
        type: 'industryCategory/updateCategory',
        payload: {
          tenantId,
          ...editRowData,
          categoryCode: lodash.trim(fieldsValue.categoryCode),
          categoryName: fieldsValue.categoryName,
          enabledFlag: fieldsValue.enabledFlag,
          _tls: fieldsValue._tls,
        },
      }).then((response) => {
        if (response) {
          this.successAction(form);
          this.queryCategory(secondIndustry.industryId);
        }
      });
    }
  }

  /**
   * 控制数据保存或者更新
   * @param {Object} fieldsValue
   * @param {Object} form
   * @param {number} level
   */
  @Bind()
  handleSave(fieldsValue, form, level) {
    const { updateFlag } = this.state;
    if (updateFlag) {
      this.updateData(fieldsValue, form, level);
    } else {
      this.saveData(fieldsValue, form, level);
    }
  }

  /**
   * 渲染查询结构
   * @param {number} level 区分等级
   * @returns
   */
  renderForm(level) {
    const {
      industryCategory: { topData = [], secondData = [] },
      match,
    } = this.props;
    const { getFieldDecorator } = this.props.form;
    switch (level) {
      case 1:
        return (
          <React.Fragment>
            <Row>
              <Col span={6}>
                <div style={{ fontSize: '14px', fontWeight: 'bold' }}>
                  {intl.get('hpfm.industryCategory.view.message.title.table.top').d('一级行业')}
                </div>
              </Col>
            </Row>
            <Row>
              <Form layout="inline">
                <Col span={12}>
                  <FormItem>
                    {getFieldDecorator('topIndustryName')(
                      <Input
                        placeholder={intl
                          .get('hpfm.industryCategory.model.industryCategory.topName')
                          .d('一级行业名称')}
                        style={{ width: '180px' }}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem>
                    <Button onClick={() => this.queryValue(1)} htmlType="submit">
                      {intl.get('hzero.common.button.search').d('查询')}
                    </Button>
                    <ButtonPermission
                      type="primary"
                      permissionList={[
                        {
                          code: `${match.path}.button.topCreate`,
                          type: 'button',
                          meaning: '国标品类一级行业定义-新建',
                        },
                      ]}
                      onClick={() => this.handleModal(true, 1)}
                      style={{ marginLeft: '4px' }}
                    >
                      {intl.get('hzero.common.button.create').d('新建')}
                    </ButtonPermission>
                  </FormItem>
                </Col>
              </Form>
            </Row>
          </React.Fragment>
        );
      case 2:
        return (
          <React.Fragment>
            <Row>
              <Col span={6}>
                <div style={{ fontSize: '14px', fontWeight: 'bold' }}>
                  {intl.get('hpfm.industryCategory.view.message.title.table.second').d('二级行业')}
                </div>
              </Col>
            </Row>
            <Row>
              <Form layout="inline">
                <Col span={12}>
                  <FormItem>
                    {getFieldDecorator('secondIndustryName')(
                      <Input
                        placeholder={intl
                          .get('hpfm.industryCategory.model.industryCategory.secondName')
                          .d('二级行业名称')}
                        style={{ width: '180px' }}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem>
                    <Button onClick={() => this.queryValue(2)} htmlType="submit">
                      {intl.get('hzero.common.button.search').d('查询')}
                    </Button>
                    <ButtonPermission
                      type="primary"
                      disabled={topData.length === 0}
                      permissionList={[
                        {
                          code: `${match.path}.button.secondCreate`,
                          type: 'button',
                          meaning: '国标品类二级行业定义-新建',
                        },
                      ]}
                      onClick={() => this.handleModal(true, 2)}
                      style={{ marginLeft: '4px' }}
                    >
                      {intl.get('hzero.common.button.create').d('新建')}
                    </ButtonPermission>
                  </FormItem>
                </Col>
              </Form>
            </Row>
          </React.Fragment>
        );
      case 3:
        return (
          <React.Fragment>
            <Row>
              <Col span={6}>
                <div style={{ fontSize: '14px', fontWeight: 'bold' }}>
                  {intl.get('hpfm.industryCategory.view.message.title.table.category').d('品类')}
                </div>
              </Col>
            </Row>
            <Row>
              <Form layout="inline">
                <Col span={12}>
                  <FormItem>
                    {getFieldDecorator('categoryName')(
                      <Input
                        placeholder={intl
                          .get('hpfm.industryCategory.model.industryCategory.categoryName')
                          .d('品类名称')}
                        style={{ width: '180px' }}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem>
                    <Button onClick={() => this.queryValue(3)} htmlType="submit">
                      {intl.get('hzero.common.button.search').d('查询')}
                    </Button>
                    <ButtonPermission
                      type="primary"
                      disabled={secondData.length === 0}
                      permissionList={[
                        {
                          code: `${match.path}.button.categoryCreate`,
                          type: 'button',
                          meaning: '国标品类品类定义-新建',
                        },
                      ]}
                      onClick={() => this.handleModal(true, 3)}
                      style={{ marginLeft: '4px' }}
                    >
                      {intl.get('hzero.common.button.create').d('新建')}
                    </ButtonPermission>
                  </FormItem>
                </Col>
              </Form>
            </Row>
          </React.Fragment>
        );
      default:
        return <React.Fragment />;
    }
  }

  /**
   * 渲染方法
   * @returns
   */
  render() {
    const {
      modalVisible,
      editRowData,
      modalTitle,
      currentLevel,
      topIndustry,
      secondIndustry,
      topCurrentRow,
      secondCurrentRow,
    } = this.state;
    const {
      industryCategory: { topData = [], secondData = [], categoryData = [] },
      match,
      fetchTopLoading,
      fetchSecondLoading,
      fetchCategoryLoading,
      updateIndustryLoading,
      updateCategoryLoading,
      addIndustryLoading,
      addCategoryLoading,
    } = this.props;
    const topColumns = [
      {
        title: intl.get('hpfm.industryCategory.model.industryCategory.topName').d('一级行业名称'),
        dataIndex: 'industryCode',
        // width: 200,
        render: (text, record) => (
          <div>
            <span style={{ color: '#b9b9b9' }}>{text}</span>&nbsp;{record.industryName}
          </div>
        ),
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 75,
        render: (_, record) => {
          const operators = [
            {
              key: 'value',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.topEdit`,
                      type: 'button',
                      meaning: '国标品类一级行业定义-编辑',
                    },
                  ]}
                  onClick={() => this.handleModal(true, 1, record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 3,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    const secondColumns = [
      {
        title: intl
          .get('hpfm.industryCategory.model.industryCategory.secondName')
          .d('二级行业名称'),
        dataIndex: 'industryCode',
        // width: 200,
        render: (text, record) => (
          <div>
            <span style={{ color: '#b9b9b9' }}>{text}</span>&nbsp;{record.industryName}
          </div>
        ),
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 75,
        render: (_, record) => {
          const operators = [
            {
              key: 'value',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.secondEdit`,
                      type: 'button',
                      meaning: '国标品类二级行业定义-编辑',
                    },
                  ]}
                  onClick={() => this.handleModal(true, 2, record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 3,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    const categoryColumns = [
      {
        title: intl.get('hpfm.industryCategory.model.industryCategory.categoryName').d('品类名称'),
        dataIndex: 'categoryCode',
        // width: 200,
        render: (text, record) => (
          <div>
            <span style={{ color: '#b9b9b9' }}>{text}</span>&nbsp;{record.categoryName}
          </div>
        ),
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'categoryName',
        width: 75,
        render: (_, record) => {
          const operators = [
            {
              key: 'value',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.categoryEdit`,
                      type: 'button',
                      meaning: '国标品类品类定义-编辑',
                    },
                  ]}
                  onClick={() => this.handleModal(true, 3, record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 3,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    const parentMethods = {
      handleModal: this.handleModal,
      handleSave: this.handleSave,
    };

    return (
      <React.Fragment>
        <Header
          title={intl
            .get('hpfm.industryCategory.view.message.title.industryCategory')
            .d('国标品类定义')}
        />
        <Content>
          <Row gutter={24}>
            <Col span={8}>
              <div className="table-list-search">{this.renderForm(1)}</div>
              <Table
                columns={topColumns}
                rowKey="industryId"
                dataSource={topData}
                // className="industry-table"
                resizable={false}
                bordered
                pagination={false}
                loading={fetchTopLoading}
                rowClassName={(_, index) =>
                  index === topCurrentRow ? styles.currencyRowActive : styles.currencyRow
                }
                onRow={(record, index) => ({
                  onClick: () => {
                    this.setState({
                      topCurrentRow: index,
                      secondCurrentRow: 0,
                    });
                    this.setSecondIndustry(record);
                  },
                })}
              />
            </Col>
            <Col span={8}>
              <div className="table-list-search">{this.renderForm(2)}</div>
              <Table
                columns={secondColumns}
                dataSource={secondData}
                resizable={false}
                rowKey="industryId"
                // className="industry-table"
                bordered
                pagination={false}
                loading={fetchSecondLoading}
                rowClassName={(_, index) =>
                  index === secondCurrentRow ? styles.currencyRowActive : styles.currencyRow
                }
                onRow={(record, index) => ({
                  onClick: () => {
                    this.setState({
                      secondCurrentRow: index,
                    });
                    this.setCategory(record);
                  },
                })}
              />
            </Col>
            <Col span={8}>
              <div className="table-list-search">{this.renderForm(3)}</div>
              <Table
                columns={categoryColumns}
                dataSource={categoryData}
                resizable={false}
                // className="industry-table"
                rowKey="categoryId"
                bordered
                pagination={false}
                loading={fetchCategoryLoading}
              />
            </Col>
          </Row>
          <CreateForm
            {...parentMethods}
            modalVisible={modalVisible}
            editRowData={editRowData}
            modalTitle={modalTitle}
            currentLevel={currentLevel}
            topIndustry={topIndustry}
            secondIndustry={secondIndustry}
            loading={
              updateIndustryLoading ||
              updateCategoryLoading ||
              addIndustryLoading ||
              addCategoryLoading
            }
          />
        </Content>
      </React.Fragment>
    );
  }
}
