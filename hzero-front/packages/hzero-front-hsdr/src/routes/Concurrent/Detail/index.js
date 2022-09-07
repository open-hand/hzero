/**
 * Concurrent-detail - 并发管理器/请求定义明细
 * @date: 2018-9-10
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Card, Col, Form, Input, Row } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import classNames from 'classnames';
import { isArray, isEmpty, isNil } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { getCurrentOrganizationId } from 'utils/utils';
import { CODE_UPPER, EMAIL } from 'utils/regExp';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

import ListTable from './ListTable';
import Drawer from './Drawer';
import styles from './index.less';

/**
 * 审批规则头-行数据管理组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} concurrent - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ concurrent, loading }) => ({
  concurrent,
  fetchDetail: loading.effects['concurrent/fetchConcurrentDetail'],
  creating: loading.effects['concurrent/createConcurrent'],
  updating: loading.effects['concurrent/updateConcurrent'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hsdr.concurrent'] })
@Form.create({ fieldNameProp: null })
export default class Detail extends Component {
  /**
   * state初始化
   */
  state = {
    targetItem: {}, // 表格中的一条记录
    disabled: false,
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    this.handleSearch();
  }

  @Bind()
  handleSearch() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (id !== 'create') {
      dispatch({
        type: 'concurrent/fetchConcurrentDetail',
        payload: {
          concurrentId: id,
        },
      });
    } else {
      dispatch({
        type: 'concurrent/updateState',
        payload: {
          concurrentDetail: {},
        },
      });
    }
  }

  /**
   * 新增参数
   */
  @Bind()
  handleAddLine() {
    const {
      concurrent: { paramFormatList = [], editTypeList = [] },
      dispatch,
    } = this.props;
    if (
      (isArray(paramFormatList) && isEmpty(paramFormatList)) ||
      (isArray(editTypeList) && isEmpty(editTypeList))
    ) {
      dispatch({
        type: 'concurrent/init',
      });
    }
    this.setState({ drawerVisible: true, targetItem: {} });
  }

  /**
   * 保存
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      form,
      match,
      tenantId,
      concurrent: { concurrentDetail = {} },
    } = this.props;
    const { paramList = [] } = concurrentDetail;
    form.validateFields((err, values) => {
      if (!err) {
        const anotherParameters = paramList.map((item) => ({
          ...item,
          concParamId: !isNil(item.concParamId) ? item.concParamId : null,
          tenantId,
        }));
        if (match.params.id === 'create') {
          dispatch({
            type: 'concurrent/createConcurrent', // 新增逻辑
            payload: {
              tenantId,
              concurrentId: null,
              paramList: [...anotherParameters],
              ...values,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              dispatch(
                routerRedux.push({
                  pathname: `/hsdr/concurrent/detail/${res.concurrentId}`,
                })
              );
              this.handleSearch();
            }
          });
        } else {
          dispatch({
            type: 'concurrent/updateConcurrent', // 更新逻辑
            payload: {
              tenantId,
              ...concurrentDetail,
              paramList: [...anotherParameters],
              ...values,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              dispatch({
                type: 'concurrent/updateState',
                payload: {
                  concurrentDetail: res,
                },
              });
              // this.handleSearch();
            }
          });
        }
      }
    });
  }

  /**
   * 参数列表- 行编辑
   * @param {object} record - 规则对象
   */
  @Bind()
  handleEditContent(record) {
    const {
      concurrent: { paramFormatList = [], editTypeList = [] },
      dispatch,
    } = this.props;
    if (
      (isArray(paramFormatList) && isEmpty(paramFormatList)) ||
      (isArray(editTypeList) && isEmpty(editTypeList))
    ) {
      dispatch({
        type: 'concurrent/init',
      });
    }
    this.setState({ drawerVisible: true, targetItem: { ...record } });
  }

  /**
   * 参数列表- 行删除
   * @param {obejct} record - 规则对象
   */
  @Bind()
  handleDeleteContent(record) {
    const {
      dispatch,
      concurrent: { concurrentDetail = {} },
    } = this.props;
    const { paramList = [], ...otherValues } = concurrentDetail;
    const recordParameterId = record.concParamId;
    if (recordParameterId) {
      dispatch({
        type: 'concurrent/deleteLine',
        payload: record,
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleSearch();
        }
      });
    } else {
      const newParamList = paramList.filter((item) => recordParameterId !== item.concParamId);
      dispatch({
        type: 'concurrent/updateState',
        payload: {
          concurrentDetail: { paramList: [...newParamList], ...otherValues },
        },
      });
    }
  }

  /**
   * 新增滑窗保存操作
   * @param {object} values - 保存数据
   */
  @Bind()
  handleSaveContent(values) {
    const {
      dispatch,
      concurrent: { concurrentDetail = {} },
    } = this.props;
    const { paramList = [], ...otherValues } = concurrentDetail;
    const value = {
      ...values,
    };
    dispatch({
      type: 'concurrent/updateState',
      payload: {
        concurrentDetail: { paramList: [...paramList, value], ...otherValues },
      },
    });
    this.setState({ drawerVisible: false, targetItem: {} });
  }

  // 编辑保存滑窗
  @Bind()
  handleEditOk(values) {
    const {
      dispatch,
      concurrent: { concurrentDetail = {} },
    } = this.props;
    const { paramList = [], ...otherValues } = concurrentDetail;
    const newList = paramList.map((item) => {
      if (item.concParamId === values.concParamId) {
        return { ...item, ...values };
      }
      return item;
    });
    dispatch({
      type: 'concurrent/updateState',
      payload: { concurrentDetail: { paramList: newList, ...otherValues } },
    });
    this.setState({ drawerVisible: false, targetItem: {} });
  }

  /**
   * 滑窗取消操作
   */
  @Bind()
  handleCancelOption() {
    this.setState({
      drawerVisible: false,
      targetItem: {},
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form,
      fetchDetail,
      creating,
      updating,
      match,
      concurrent: {
        concurrentDetail = {},
        paramsType = [],
        paramFormatList = [],
        editTypeList = [],
      },
    } = this.props;
    const { targetItem = {}, drawerVisible = false, disabled = false } = this.state;
    const headerTitle =
      match.params.id === 'create'
        ? intl.get('hsdr.concurrent.view.message.title.add').d('请求定义 - 新增')
        : intl.get('hsdr.concurrent.view.message.title.edit').d('请求定义 - 编辑');
    const title = targetItem.concParamId
      ? intl.get('hsdr.concurrent.view.message.drawer.edit').d('编辑参数')
      : intl.get('hsdr.concurrent.view.message.drawer.add').d('新增参数');
    const { getFieldDecorator } = form;
    const listProps = {
      path: match.path,
      paramsType,
      loading: fetchDetail,
      dataSource: concurrentDetail.paramList,
      editContent: this.handleEditContent,
      deleteContent: this.handleDeleteContent,
    };
    const drawerProps = {
      title,
      paramsType,
      paramFormatList,
      editTypeList,
      anchor: 'right',
      visible: drawerVisible,
      itemData: targetItem,
      onOk: this.handleSaveContent,
      onCancel: this.handleCancelOption,
      onEditOk: this.handleEditOk,
    };
    return (
      <>
        <Header title={headerTitle} backPath="/hsdr/concurrent/list">
          <ButtonPermission
            type="primary"
            icon="save"
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '请求定义详情-保存',
              },
            ]}
            loading={creating || updating}
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            key="concurrent"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hsdr.concurrent.view.message.requestDefine').d('请求定义')}</h3>}
            loading={creating || updating || fetchDetail}
          >
            <Form className={classNames(styles['header-form'])}>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hsdr.concurrent.model.concurrent.concCode').d('请求编码')}
                    required={!concurrentDetail.concurrentId}
                  >
                    {getFieldDecorator('concCode', {
                      initialValue: concurrentDetail.concCode,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hsdr.concurrent.model.concurrent.concCode')
                              .d('请求编码'),
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
                      concurrentDetail.concurrentId ? (
                        <>{concurrentDetail.concCode}</>
                      ) : (
                        <Input trim typeCase="upper" inputChinese={false} />
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hsdr.concurrent.model.concurrent.concDescription')
                      .d('请求描述')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('concDescription', {
                      initialValue: concurrentDetail.concDescription,
                    })(disabled ? <>{concurrentDetail.concDescription}</> : <Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hsdr.concurrent.model.concurrent.alarmEmail').d('报警邮件')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('alarmEmail', {
                      initialValue: concurrentDetail.alarmEmail,
                      rules: [
                        {
                          pattern: EMAIL,
                          message: intl
                            .get('hsdr.concurrent.view.validation.alarmEmail')
                            .d('格式有误'),
                        },
                      ],
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hsdr.concurrent.model.concurrent.concName').d('请求名称')}
                    required={!disabled}
                  >
                    {getFieldDecorator('concName', {
                      initialValue: concurrentDetail.concName,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hsdr.concurrent.model.concurrent.concName')
                              .d('请求名称'),
                          }),
                        },
                      ],
                    })(
                      disabled ? (
                        <>{concurrentDetail.concName}</>
                      ) : (
                        <TLEditor
                          label={intl
                            .get('hsdr.concurrent.model.concurrent.concName')
                            .d('请求名称')}
                          field="concName"
                          token={concurrentDetail ? concurrentDetail._token : null}
                        />
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hsdr.concurrent.model.concurrent.executableName')
                      .d('可执行名称')}
                    required={!concurrentDetail.concurrentId}
                  >
                    {getFieldDecorator('executableId', {
                      initialValue: concurrentDetail.executableId,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hsdr.concurrent.model.concurrent.executableName')
                              .d('可执行名称'),
                          }),
                        },
                      ],
                    })(
                      concurrentDetail.concurrentId ? (
                        <>{concurrentDetail.executableName}</>
                      ) : (
                        <Lov textValue={concurrentDetail.executableName} code="HSDR.EXECUTABLE" />
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hzero.common.status').d('状态')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('enabledFlag', {
                      initialValue:
                        concurrentDetail.enabledFlag === undefined
                          ? 1
                          : concurrentDetail.enabledFlag,
                    })(<Switch />)}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Card>
          <Card
            key="concurrent-line"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            title={<h3>{intl.get('hsdr.concurrent.view.tab.param').d('参数')}</h3>}
          >
            <div className="table-operator">
              <ButtonPermission
                type="primary"
                permissionList={[
                  {
                    code: `${match.path}.button.addParam`,
                    type: 'button',
                    meaning: '请求定义详情-新增参数',
                  },
                ]}
                onClick={this.handleAddLine}
              >
                {intl.get('hsdr.concurrent.view.option.addParam').d('新增参数')}
              </ButtonPermission>
            </div>
            <ListTable {...listProps} />
          </Card>
          <Drawer {...drawerProps} />
        </Content>
      </>
    );
  }
}
