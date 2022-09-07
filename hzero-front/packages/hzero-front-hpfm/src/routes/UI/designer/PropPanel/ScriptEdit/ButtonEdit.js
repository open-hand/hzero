/**
 * ButtonEdit.js * @date 2018-12-11
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

/**
 * 要保证几种层级的命名都不能相同
 * param 表示有这个属性
 * param: 表示上级属性值有不同的分支
 *
 * action
 *  page:
 *    pageCode
 *    pageMeaning
 *    openType
 *      drawer:
 *        openPageTypeDrawer
 *      modal:
 *        openPageTypeModal
 *    params
 *    subEvents
 *    modalBtns
 *  action:
 *    actionEvent
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Form, Tabs, Row, Col, Select, Radio, Button, Tooltip, Input, Popconfirm } from 'hzero-ui';
import { forEach, join, map, isEmpty, filter } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { getEditTableData, getResponse, getCurrentOrganizationId } from 'utils/utils';

import Lov from 'components/Lov';
import EditTable from 'components/EditTable';
import {
  ACTION_CODE, // 按钮动作的方式
  ACTION_PAGE_TYPE, // 打开页面的方式
  PAGE_PARAM, // 页面参数类型
  PAGE_TYPE_MODAL, // 弹出模态框的样式
  paramSep, // 参数分隔
  pageParamOptions, //  页面参数类型 选项
  modalBtnSep, // 按钮分隔
  subEventSep, // 订阅事件分隔
  modalBtnPrefix, // 按钮前缀
  subEventPrefix, // 订阅事件前缀
  modalSubEvents, // 订阅事件
  clickActionOptions, // 按钮动作的方式 选项
  btnTypeOptions, // 按钮类型 选项
  pageTypeModalOptions, // 弹出模态框的样式 选项
  openPageOptions, // 打开页面的方式 选项
} from 'components/DynamicComponent/config';

import DynamicTable from 'components/DynamicComponent/DynamicTable';
import DynamicForm from 'components/DynamicComponent/DynamicForm';
import DynamicModal from 'components/DynamicComponent/DynamicModal';
import DynamicToolbar from 'components/DynamicComponent/DynamicToolbar';

import { queryTplAndScriptsByPageCode } from '@/services/uiPageService';
import DataType from '../../DataType';
import { attributeNameProp, attributeTypeProp, attributeValueProp } from '../../config';

import styles from '../index.less';

const { Item: FormItem } = Form;

const col2Layout = {
  span: 12,
};

// 分为两行, 有的需要跨行 用 formItemLayout1
// 不需要跨行的 用 formItemLayout2
const formItemLayout1 = {
  labelCol: {
    span: 3,
  },
  wrapperCol: {
    span: 21,
  },
};

const formItemLayout2 = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 18,
  },
};

function buildTplFuncOptGroup(tpl = {}) {
  if (tpl.templateCode) {
    let tplComponent;
    let tplName;
    switch (tpl.templateType) {
      case 'DynamicForm':
        tplComponent = DynamicForm;
        tplName = 'DynamicForm';
        break;
      case 'DynamicModal':
        tplComponent = DynamicModal;
        tplName = 'DynamicModal';
        break;
      case 'DynamicTable':
        tplComponent = DynamicTable;
        tplName = 'DynamicTable';
        break;
      case 'DynamicToolbar':
        tplComponent = DynamicToolbar;
        tplName = 'DynamicToolbar';
        break;
      default:
        break;
    }
    if (tplComponent) {
      if (tplComponent.exportFuncs && tplComponent.exportFuncs.length > 0) {
        return (
          <Select.OptGroup label={tpl.description} key={`${tpl.templateCode}-${tplName}`}>
            {map(tplComponent.exportFuncs, exportFuncStr => {
              return (
                <Select.Option
                  key={`${tpl.templateCode}-${tplName}-${exportFuncStr}`}
                  value={`this.ref[${tpl.templateCode}].${exportFuncStr}`}
                >
                  {intl
                    .get(tplComponent.exportFuncsInfo[exportFuncStr].descriptionIntlCode)
                    .d(tplComponent.exportFuncsInfo[exportFuncStr].descriptionIntlDefault)}
                </Select.Option>
              );
            })}
          </Select.OptGroup>
        );
      }
    }
  }
  return false;
}

function buildTplsFuncOptGroup(tpls) {
  const optGroups = [];
  forEach(tpls, tpl => {
    const optGroup = buildTplFuncOptGroup(tpl);
    if (optGroup) {
      optGroups.push(optGroup);
    }
  });
  return optGroups;
}

function buildSelectOption(opt) {
  return (
    <Select.Option key={opt.value} value={opt.value} title={opt.meaning}>
      {opt.meaning}
    </Select.Option>
  );
}

function buildRadioOption(opt) {
  return (
    <Radio key={opt.value} value={opt.value}>
      {opt.meaning}
    </Radio>
  );
}

function buildSelect(options, parentProps) {
  return React.createElement(Select, parentProps, map(options, buildSelectOption));
}

function buildRadioGroup(options, parentProps) {
  return React.createElement(Radio.Group, parentProps, map(options, buildRadioOption));
}

@Form.create({ fieldNameProp: null })
export default class ButtonEdit extends React.Component {
  state = {
    prevState: {}, // 存储用来比较 更新的变量
    extraTabKey: 'param',
    // 为按钮执行动作 设置选项
  };

  static defaultProps = {
    extraActions: [],
    extraParams: [],
    propsValue: {},
  };

  static propTypes = {
    extraActions: PropTypes.oneOfType([PropTypes.array, PropTypes.element]),
    extraParams: PropTypes.array,
    propsValue: PropTypes.object,
  };

  /**
   * 根据传进的属性 来生成 对应 打开页面 的参数
   */
  static getDerivedStateFromProps(nextProps, prevState) {
    const {
      propsValue, // 组件初始值
      extraParams, // 额外参数
      scripts, // 设置执行动作 当前页面所有的方法
      components, // 设置执行动作 当前页面所有的组件
    } = nextProps;
    const {
      // 在页面加载后 和 页面切换
      // 查询 pageCode 对应的 方法&tpl, 同时将这些设置成页面方法
      // pageScripts, // 设置模态框按钮执行动作 选择页面的所有方法
      // pageComponents, // 设置模态框按钮执行动作 选择页面的所有组件
      prevState: {
        propsValue: prevPropsValue, // 组件初识值
        extraParams: prevExtraParams, // 额外参数
        scripts: prevScripts, // 设置执行动作 当前页面所有的方法
        components: prevComponents, // 设置执行动作 当前页面所有的组件
        // pageScripts: prevPageScripts, // 设置模态框按钮执行动作 选择页面的所有方法
        // pageComponents: prevPageComponents, // 设置模态框按钮执行动作 选择页面的所有组件
      },
    } = prevState;
    const nextState = {};
    const nextPrevState = {
      propsValue: prevPropsValue,
      extraParams: prevExtraParams,
    };
    // 设置 编辑属性值
    if (propsValue && prevPropsValue !== propsValue) {
      const nextPropsValue = { ...propsValue };
      // 处理打开页面情况, 默认是打开页面
      if (!propsValue.action || propsValue.action === ACTION_CODE.page) {
        // 处理参数
        const params = [];
        const paramStream = (propsValue.params || '').split(paramSep);
        for (let i = 1; i <= paramStream.length; i++) {
          if (i % 3 === 0) {
            params.push({
              paramName: paramStream[i - 3],
              paramType: paramStream[i - 2],
              paramValue: paramStream[i - 1],
              _status: 'update',
            });
          }
        }
        nextPropsValue.params = params;
        // 处理订阅事件

        // 处理 Modal 按钮
        const modalBtns = [];
        if (propsValue.modalBtns) {
          for (let i = 0; i < propsValue.modalBtns.length; i++) {
            const modalBtnStream = (propsValue.modalBtns[i].value || '').split(modalBtnSep);
            modalBtns.push({
              btnName: modalBtnStream[0],
              btnType: modalBtnStream[1],
              btnAction: modalBtnStream[2],
              _status: 'update',
            });
          }
        } else {
          modalBtns.push({
            btnName: '确定',
            btnType: 'primary',
            btnAction: undefined,
            _status: 'create',
          });
          modalBtns.push({
            btnName: '取消',
            btnType: '',
            btnAction: undefined,
            _status: 'create',
          });
        }
        nextPropsValue.modalBtns = modalBtns;
        // 处理 订阅事件
        const subEvents = [];
        if (propsValue.subEvents) {
          for (let i = 0; i < propsValue.subEvents.length; i++) {
            const subEventStream = (propsValue.subEvents[i].value || '').split(modalBtnSep);
            subEvents.push({
              subEventListen: subEventStream[0],
              subEventAction: subEventStream[1],
              _status: 'update',
            });
          }
        } else {
          modalSubEvents.forEach(modalSubEvent => {
            subEvents.push({
              subEventListen: modalSubEvent.value,
              subEventAction: undefined,
              _status: 'create',
            });
          });
        }
        nextPropsValue.subEvents = subEvents;
      } else if (propsValue.action === ACTION_CODE.action) {
        // 执行动作不需要额外处理
      }
      nextState.propsValue = nextPropsValue;
      nextPrevState.propsValue = propsValue;
    }
    // 为打开页面的 参数选项 设置额外的参数选项
    if (extraParams && prevExtraParams !== extraParams) {
      const nextExtraParamMap = {};
      // 将 额外参数 数组转成 对象
      forEach(extraParams, p => {
        nextExtraParamMap[p.value] = p;
      });
      nextState.extraParamMap = nextExtraParamMap;
      nextState.extraParams = extraParams;
      nextPrevState.extraParams = extraParams;
    }
    // 为执行动作 设置选项
    if ((scripts && scripts !== prevScripts) || (components && components !== prevComponents)) {
      nextPrevState.scripts = scripts;
      nextPrevState.components = components;
      const travelScripts = scripts || [];
      const travelComponents = components || [];
      let actionOptions = [];
      const scriptPageActionOptions = [];
      for (let i = 0; i < travelScripts.length; i++) {
        scriptPageActionOptions.push(
          <Select.Option value={`this.${scripts[i].name}`} key={`page-script-${scripts[i].name}`}>
            {scripts[i].description}
          </Select.Option>
        );
      }
      // 给执行动作的选项加入 页面方法
      actionOptions.push(
        <Select.OptGroup label="页面方法" key="page-$-script">
          {scriptPageActionOptions}
        </Select.OptGroup>
      );
      actionOptions = actionOptions.concat(buildTplsFuncOptGroup(travelComponents));
      nextState.actionOptions = actionOptions;
    }
    // 为执行动作 设置额外的组件方法, 由父组件提供, 不需要处理 直接是好的 OptGroup
    if (isEmpty(nextState)) {
      return null;
    }
    nextState.prevState = nextPrevState;
    return nextState;
  }

  render() {
    return <Form>{this.renderLevel1()}</Form>;
  }

  componentDidMount() {
    // 加载 modal 按钮 执行动作
    const { propsValue = {}, onRef } = this.props;
    if (propsValue.action === ACTION_CODE.page) {
      if (
        propsValue.pageCode &&
        (propsValue.openType === ACTION_PAGE_TYPE.modal ||
          propsValue.openType === ACTION_PAGE_TYPE.drawer)
      ) {
        this.queryAndSetPageActions(propsValue.pageCode);
      }
    }
    if (onRef) {
      onRef(this);
    }
  }

  @Bind()
  queryAndSetPageActions(pageCode) {
    const { organizationId = getCurrentOrganizationId() } = this.props;
    // 查询页面的 方法&tpl
    queryTplAndScriptsByPageCode(organizationId, pageCode).then(res => {
      const page = getResponse(res);
      if (page) {
        const pageActions = [];
        const pageScriptActions = [];
        if (page.scripts) {
          for (let i = 0; i < page.scripts.length; i += 1) {
            pageScriptActions.push(
              <Select.Option value={page.scripts[i].name} key={page.scripts[i].name}>
                {page.scripts[i].description}
              </Select.Option>
            );
          }
        }
        pageActions.push(
          <Select.OptGroup label={page.description} key={`page-${pageCode}-script`}>
            {pageScriptActions}
          </Select.OptGroup>
        );
        if (page.tpls) {
          for (let i = 0; i < page.tpls.length; i += 1) {
            const componentActionOptions = [];
            switch (page.tpls[i].templateType) {
              case 'DynamicForm':
                componentActionOptions.push(
                  <Select.Option
                    value={`this.ref[${page.tpls[i].templateCode}].reset`}
                    key={`component-form-${page.tpls[i].templateCode}-reset`}
                  >
                    重置
                  </Select.Option>
                );
                componentActionOptions.push(
                  <Select.Option
                    value={`this.ref[${page.tpls[i].templateCode}].submit`}
                    key={`component-form-${page.tpls[i].templateCode}-submit`}
                  >
                    提交
                  </Select.Option>
                );
                break;
              case 'DynamicTable':
                componentActionOptions.push(
                  <Select.Option
                    value={`this.ref[${page.tpls[i].templateCode}].reload`}
                    key={`component-table-${page.tpls[i].templateCode}-reload`}
                  >
                    重新加载
                  </Select.Option>
                );
                componentActionOptions.push(
                  <Select.Option
                    value={`this.ref[${page.tpls[i].templateCode}].query`}
                    key={`component-table-${page.tpls[i].templateCode}-query`}
                  >
                    查询
                  </Select.Option>
                );
                break;
              // Toolbar 和 Modal 还没有自己的方法
              case 'DynamicToolbar':
                break;
              case 'DynamicModal':
                break;
              default:
                break;
            }
            pageActions.push(
              <Select.OptGroup
                label={page.tpls[i].description}
                key={`component-${page.tpls[i].templateCode}`}
              >
                {componentActionOptions}
              </Select.OptGroup>
            );
          }
        }
        this.setState({
          pageActions,
        });
      }
    });
  }

  /**
   * 打开页面 action, 页面改变后 需要重新查询 选择的页面拥有的方法
   * todo 需不需要清空 modal 按钮的事件
   * @param {String} pageCode
   */
  @Bind()
  handlePageChange(pageCode) {
    const { form } = this.props;
    const prevPageCode = form.getFieldValue('pageCode');
    if (prevPageCode !== pageCode) {
      this.queryAndSetPageActions(pageCode);
    }
  }

  /**
   * 按钮事件 一层层嵌套 上级影响下级
   */
  renderLevel1() {
    const { form } = this.props;
    const { propsValue = {} } = this.state;
    // 根据 action 去渲染 level2
    const initialAction = propsValue.action || ACTION_CODE.page; // action 是必输 且 是一个选项 不存在没有值;
    const action = form.getFieldValue('action') || initialAction;
    return (
      <React.Fragment>
        <Row>
          <Col {...col2Layout}>
            <FormItem {...formItemLayout2} key="action" label="动作">
              {form.getFieldDecorator('action', {
                initialValue: initialAction,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: '动作',
                    }),
                  },
                ],
              })(buildRadioGroup(clickActionOptions))}
            </FormItem>
          </Col>
        </Row>
        {this.renderLevel2(action)}
      </React.Fragment>
    );
  }

  renderLevel2(action) {
    switch (action) {
      case ACTION_CODE.page:
        return this.renderLevel2Page();
      case ACTION_CODE.action:
        return this.renderLevel2Action();
      default:
        break;
    }
    return null;
  }

  renderLevel2Page() {
    const { form } = this.props;
    const { propsValue = {}, extraTabKey } = this.state;
    const initialOpenType = propsValue.openType || ACTION_PAGE_TYPE.drawer;
    const openType = form.getFieldValue('openType') || initialOpenType;
    let openTypeCascade;
    switch (openType) {
      case ACTION_PAGE_TYPE.drawer:
        openTypeCascade = (
          <Col {...col2Layout}>
            <FormItem {...formItemLayout2} key="openPageTypeDrawer" label="滑出尺寸">
              {form.getFieldDecorator('openPageTypeDrawer', {
                initialValue: propsValue.openPageTypeDrawer || 520,
              })(
                <Radio.Group>
                  <Radio value={520}>520</Radio>
                  <Radio value={1000}>1000</Radio>
                </Radio.Group>
              )}
            </FormItem>
          </Col>
        );
        break;
      case ACTION_PAGE_TYPE.modal:
        openTypeCascade = (
          <Col {...col2Layout}>
            <FormItem
              {...formItemLayout2}
              key="openPageTypeModal"
              label={intl.get('hpfm.ui.model.field.linkButton.openPageTypeModal').d('弹出尺寸')}
            >
              {form.getFieldDecorator('openPageTypeModal', {
                initialValue: propsValue.openPageTypeModal || PAGE_TYPE_MODAL.w1,
              })(buildSelect(pageTypeModalOptions))}
            </FormItem>
          </Col>
        );
        break;
      case ACTION_PAGE_TYPE.open:
        break;
      default:
        break;
    }
    // 给 pageMeaning 设置值
    form.getFieldDecorator('pageMeaning', {
      initialValue: propsValue.pageMeaning,
    });
    return (
      <React.Fragment>
        {/* 页面 */}
        <Row>
          <Col>
            <FormItem {...formItemLayout1} key="pageCode" label="选择页面">
              {form.getFieldDecorator('pageCode', {
                initialValue: propsValue.pageCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: '页面',
                    }),
                  },
                ],
              })(
                <Lov
                  code="HPFM.UI_PAGE"
                  className="full-width"
                  textField="pageMeaning"
                  onChange={this.handlePageChange}
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          {/* 打开方式 */}
          <Col {...col2Layout}>
            <FormItem
              {...formItemLayout2}
              key="openType"
              label={intl.get('hpfm.ui.model.field.linkButton.openType').d('打开方式')}
            >
              {form.getFieldDecorator('openType', {
                initialValue: initialOpenType,
              })(buildSelect(openPageOptions))}
            </FormItem>
          </Col>
          {openTypeCascade}
        </Row>
        <Row>
          {/* 额外区域 参数 订阅事件 Modal 按钮 */}
          <Col>
            <Form.Item key="extra">
              <Tabs
                animated={false}
                activeKey={extraTabKey}
                tabBarExtraContent={
                  <React.Fragment>
                    {extraTabKey === 'param' && (
                      <div style={{ marginBottom: 10, textAlign: 'right' }}>
                        <Button icon="plus" onClick={this.handleParamsAdd} />
                      </div>
                    )}
                  </React.Fragment>
                }
                onChange={this.handleExtraTabChange}
              >
                <Tabs.TabPane forceRender key="param" tab="传递参数">
                  <EditTable
                    rowKey={this.getRowKey}
                    columns={this.paramColumns}
                    dataSource={propsValue.params}
                    pagination={false}
                    bordered
                  />
                </Tabs.TabPane>
                {openType === ACTION_PAGE_TYPE.modal || openType === ACTION_PAGE_TYPE.drawer
                  ? [
                    <Tabs.TabPane forceRender key="subEvent" tab="订阅事件">
                      <EditTable
                        rowKey={this.getRowKey}
                        columns={this.subEventColumns}
                        dataSource={propsValue.subEvents}
                        pagination={false}
                        bordered
                      />
                    </Tabs.TabPane>,
                    <Tabs.TabPane forceRender key="modalBtn" tab="按钮">
                      <EditTable
                        rowKey={this.getRowKey}
                        columns={this.modalBtnColumns}
                        dataSource={propsValue.modalBtns}
                        pagination={false}
                        bordered
                      />
                    </Tabs.TabPane>,
                    ]
                  : null}
              </Tabs>
            </Form.Item>
          </Col>
        </Row>
      </React.Fragment>
    );
  }

  renderLevel2Action() {
    const { form, extraActions } = this.props;
    const { propsValue = {}, actionOptions } = this.state;
    return (
      <React.Fragment>
        <Row>
          <Col {...col2Layout}>
            <FormItem {...formItemLayout2} key="actionEvent" label="执行动作">
              {form.getFieldDecorator('actionEvent', {
                initialValue: propsValue.actionEvent,
              })(
                <Select>
                  {actionOptions}
                  {extraActions}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
      </React.Fragment>
    );
  }

  // openType 页面参数表格相关
  getRowKey(record, index) {
    return index;
  }

  @Bind()
  handleParamsAdd() {
    const { propsValue = {} } = this.state;
    const { params = [] } = propsValue;
    this.setState({
      propsValue: {
        ...propsValue,
        params: [...params, { paramType: PAGE_PARAM.fixParam, _status: 'create' }],
      },
    });
  }

  paramColumns = [
    {
      title: '序号',
      key: 'orderSeq',
      width: 80,

      render(item, record, index) {
        return index + 1;
      },
    },
    {
      title: '参数名称',
      dataIndex: 'paramName',
      width: 200,
      render: (item, record, index) => {
        const { extraParamMap = {} } = this.state;
        const { getFieldDecorator, getFieldError, getFieldValue } = record.$form;
        const paramNameError = getFieldError('paramName');
        const hasError = !!paramNameError;
        const paramType = getFieldValue('paramType');
        const extraParam = extraParamMap[paramType] || {};
        let inputComponent;
        if (extraParam.getParamNameElement) {
          inputComponent = extraParam.getParamNameElement(item, record, index);
        } else {
          inputComponent = (
            <Input
              className={`${hasError ? styles['border-error'] : ''} full-width`}
              inputChinese={false}
              trim
            />
          );
        }
        return (
          <Tooltip title={join(paramNameError)} placement="bottom">
            {getFieldDecorator('paramName', {
              initialValue: item,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', { name: '参数名称' }),
                },
                {
                  pattern: new RegExp(`^[^${paramSep}]*$`),
                  message: `参数名称不能包含 ${paramSep}`,
                },
              ],
            })(inputComponent)}
          </Tooltip>
        );
      },
    },
    {
      title: '参数类型',
      dataIndex: 'paramType',
      width: 120,

      render: (item, record) => {
        const { getFieldDecorator, setFieldsValue, getFieldError, getFieldValue } = record.$form;
        const paramTypeError = getFieldError('paramType');
        const hasError = !!paramTypeError;
        const { extraParams, extraParamMap } = this.state;
        const paramType = getFieldValue('paramType');
        const extraParam = extraParamMap[paramType] || {};
        return (
          <Tooltip title={join(paramTypeError)} placement="bottom">
            {getFieldDecorator('paramType', {
              initialValue: item,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', { name: '参数类型' }),
                },
              ],
            })(
              <Select
                className={`${hasError ? styles['border-error'] : ''} full-width`}
                onChange={
                  (/* paramType */) => {
                    if (extraParam.getParamNameElement) {
                      setFieldsValue({ paramValue: undefined, paramName: undefined });
                    } else {
                      setFieldsValue({ paramValue: undefined });
                    }
                    // if(extraParam.getParamValueElement) {
                    //
                    // }
                  }
                }
              >
                {map([...pageParamOptions, ...extraParams], buildSelectOption)}
              </Select>
            )}
          </Tooltip>
        );
      },
    },
    {
      title: '参数值',
      dataIndex: 'paramValue',
      width: 200,
      render: (item, record, index) => {
        const { getFieldDecorator, getFieldValue, getFieldError } = record.$form;
        const paramValueError = getFieldError('paramValue');
        const hasError = !!paramValueError;
        const paramType = getFieldValue('paramType');
        const { extraParamMap } = this.state;
        const extraParam = extraParamMap[paramType] || {};
        let inputComponent;
        if (extraParam.getParamValueElement) {
          inputComponent = extraParam.getParamValueElement(item, record, index);
        } else {
          inputComponent = (
            <Input className={`${hasError ? styles['border-error'] : ''} full-width`} />
          );
        }
        if (inputComponent) {
          return (
            <Tooltip title={join(paramValueError)} placement="bottom">
              {getFieldDecorator('paramValue', {
                initialValue: item,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', { name: '参数值' }),
                  },
                  {
                    pattern: new RegExp(`^[^${paramSep}]*$`),
                    message: `参数值不能包含 ${paramSep}`,
                  },
                ],
              })(inputComponent)}
            </Tooltip>
          );
        }
        return null;
      },
    },
    {
      title: intl.get('hzero.common.button.delete').d('删除'),
      key: 'delete',

      width: 80,
      render: (item, record) => {
        return (
          <Form.Item>
            <Popconfirm
              title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
              onConfirm={() => this.handleRemoveParam(record)}
            >
              <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
            </Popconfirm>
          </Form.Item>
        );
      },
    },
  ];

  subEventColumns = [
    {
      title: '序号',
      key: 'orderSeq',
      width: 80,

      render(item, record, index) {
        const { getFieldDecorator } = record.$form;
        // 将序号放在数据中
        getFieldDecorator('orderSeq', {
          initialValue: index,
        });
        return index + 1;
      },
    },
    {
      title: '订阅事件',
      dataIndex: 'subEventListen',
      width: 200,
      render: (item, record) => {
        const { getFieldDecorator, getFieldError } = record.$form;
        const subEventListenError = getFieldError('subEventListen');
        const hasError = !!subEventListenError;
        return (
          <Tooltip title={join(subEventListenError)} placement="bottom">
            {getFieldDecorator('subEventListen', {
              initialValue: item,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', { name: '订阅事件' }),
                },
              ],
            })(
              <Select className={`${hasError ? styles['border-error'] : ''} full-width`} disabled>
                {map(modalSubEvents, buildSelectOption)}
              </Select>
            )}
          </Tooltip>
        );
      },
    },
    {
      title: '触发事件',
      dataIndex: 'subEventAction',
      width: 200,
      render: (item, record) => {
        const { getFieldDecorator, getFieldError } = record.$form;
        const subEventActionError = getFieldError('subEventAction');
        const hasError = !!subEventActionError;
        const { extraActions } = this.props;
        const { actionOptions } = this.state;
        return (
          <Tooltip title={join(subEventActionError)} placement="bottom">
            {getFieldDecorator('subEventAction', {
              initialValue: item,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', { name: '触发事件' }),
                },
              ],
            })(
              <Select className={`${hasError ? styles['border-error'] : ''} full-width`}>
                {actionOptions}
                {extraActions}
              </Select>
            )}
          </Tooltip>
        );
      },
    },
  ];

  modalBtnColumns = [
    {
      title: '序号',
      key: 'orderSeq',
      width: 80,

      render(item, record, index) {
        const { getFieldDecorator } = record.$form;
        // 将序号放在数据中
        getFieldDecorator('orderSeq', {
          initialValue: index,
        });
        return index + 1;
      },
    },
    {
      title: '按钮名称(按钮名称必须唯一)',
      dataIndex: 'btnName',
      width: 200,
      render: (item, record) => {
        const { getFieldDecorator, getFieldError } = record.$form;
        const btnNameError = getFieldError('btnName');
        const hasError = !!btnNameError;
        return (
          <Tooltip title={join(btnNameError)} placement="bottom">
            {getFieldDecorator('btnName', {
              initialValue: item,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', { name: '按钮编码' }),
                },
                {
                  pattern: new RegExp(`^[^${modalBtnSep}]*$`),
                  message: `按钮编码不能包含 ${modalBtnSep}`,
                },
              ],
            })(<Input className={`${hasError ? styles['border-error'] : ''} full-width`} trim />)}
          </Tooltip>
        );
      },
    },
    {
      title: '按钮类型',
      dataIndex: 'btnType',
      width: 120,
      render: (item, record) => {
        const { getFieldDecorator, getFieldError } = record.$form;
        const btnTypeError = getFieldError('btnType');
        const hasError = !!btnTypeError;
        return (
          <Tooltip title={join(btnTypeError)} placement="bottom">
            {getFieldDecorator('btnType', {
              initialValue: item,
              rules: [],
            })(
              <Select className={`${hasError ? styles['border-error'] : ''} full-width`}>
                {map(btnTypeOptions, buildSelectOption)}
              </Select>
            )}
          </Tooltip>
        );
      },
    },
    {
      title: '执行动作',
      dataIndex: 'btnAction',
      width: 200,
      render: (item, record) => {
        const { getFieldDecorator, getFieldError } = record.$form;
        const btnActionError = getFieldError('btnAction');
        const hasError = !!btnActionError;
        // pageEvents 页面的事件 extraEvents 额外的事件, 例如 表格组件的删除行
        const { pageActions = [] } = this.state;
        const { extraActions = [] } = this.props;
        return (
          <Tooltip title={join(btnActionError)} placement="bottom">
            {getFieldDecorator('btnAction', {
              initialValue: item,
            })(
              <Select className={`${hasError ? styles['border-error'] : ''} full-width`}>
                {pageActions}
                {extraActions}
              </Select>
            )}
          </Tooltip>
        );
      },
    },
    {
      title: intl.get('hzero.common.button.delete').d('删除'),
      key: 'delete',

      width: 80,
      render: (item, record) => {
        return (
          <Form.Item>
            <Popconfirm
              title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
              onConfirm={() => this.handleRemoveModalBtn(record)}
            >
              <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
            </Popconfirm>
          </Form.Item>
        );
      },
    },
  ];

  // Table 按钮事件
  handleRemoveParam(record) {
    const { propsValue = {} } = this.state;
    this.setState({
      propsValue: {
        ...propsValue,
        params: filter(propsValue.params, r => r !== record),
      },
    });
  }

  handleRemoveModalBtn(record) {
    const { propsValue = {} } = this.state;
    this.setState({
      propsValue: {
        ...propsValue,
        modalBtns: filter(propsValue.modalBtns, r => r !== record),
      },
    });
  }

  /**
   *
   * @returns Promise<object>
   */
  @Bind()
  getValidateData() {
    return this.getValidateLevel1Data();
  }

  /**
   * @return Promise<object>
   */
  getValidateLevel1Data() {
    const { form } = this.props;
    return new Promise((resolve, reject) => {
      form.validateFields((err, fieldsValue) => {
        if (err) {
          reject(err);
          return;
        }
        const { action } = fieldsValue;
        const validateData = { ...fieldsValue };
        const attrConfig = [];
        attrConfig.push({
          attributeName: 'action',
          attributeType: DataType.String,
          value: action,
        });
        this.getValidateLevel2Data(action, validateData, attrConfig).then(() => {
          resolve({ validateData, attrConfig });
        }, reject);
      });
    });
  }

  /**
   * 会改变 validateData
   * 将 level2 的数据放进去
   * @param {String} action
   * @param {object} validateData
   * @param {Array<{attributeName: String, attributeValue: any, dataType: String}>} attrConfig
   */
  getValidateLevel2Data(action, validateData, attrConfig) {
    switch (action) {
      case ACTION_CODE.page:
        return this.getValidateLevel2PageData(validateData, attrConfig);
      case ACTION_CODE.action:
        return this.getValidateLevel2ActionData(validateData, attrConfig);
      default:
        break;
    }
    return Promise.reject(new Error('不是已知的Action'));
  }

  getValidateLevel2PageData(validateData, attrConfig) {
    const { propsValue = {} } = this.state;
    const editParams = propsValue.params;
    // 获取参数
    if (editParams && editParams.length) {
      // 有参数
      const validateParams = getEditTableData(editParams);
      if (!validateParams.length) {
        return Promise.reject(new Error('参数校验失败'));
      }
      const paramsStream = [];
      for (let i = 0; i < validateParams.length; i += 1) {
        paramsStream.push(validateParams[i].paramName);
        paramsStream.push(validateParams[i].paramType);
        paramsStream.push(validateParams[i].paramValue);
      }
      // eslint-disable-next-line no-param-reassign
      validateData.params = paramsStream.join(paramSep);
    }
    // 获取订阅事件
    const editSubEvents = propsValue.subEvents;
    if (editSubEvents && editSubEvents.length) {
      // 有参数
      const validateSubEvents = getEditTableData(editSubEvents);
      if (!validateSubEvents.length) {
        return Promise.reject(new Error('按钮校验失败'));
      }
      const subEventsConfig = [];
      const subEvents = [];
      for (let i = 0; i < validateSubEvents.length; i += 1) {
        // modalBtn 是一个数组存储的数据
        subEvents.push({
          attributeType: DataType.String,
          value: `${validateSubEvents[i].subEventListen}${subEventSep}${validateSubEvents[i].subEventAction}`,
        });
        subEventsConfig.push({
          attributeName: `${subEventPrefix}[${i}]`,
          attributeType: DataType.String,
          value: `${validateSubEvents[i].subEventListen}${subEventSep}${validateSubEvents[i].subEventAction}`,
        });
      }
      // eslint-disable-next-line no-param-reassign
      validateData.subEvents = subEvents;
      attrConfig.push({
        attributeName: 'subEvents',
        value: subEventsConfig,
      });
    }
    // 获取Modal按钮 和 事件
    const editModalBtns = propsValue.modalBtns;
    if (editModalBtns && editModalBtns.length) {
      // 有参数
      const validateModalBtns = getEditTableData(editModalBtns);
      if (!validateModalBtns.length) {
        return Promise.reject(new Error('按钮校验失败'));
      }
      const modalBtnsConfig = [];
      const modalBtns = [];
      for (let i = 0; i < validateModalBtns.length; i += 1) {
        // modalBtn 是一个数组存储的数据
        modalBtns.push({
          attributeType: DataType.String,
          value: `${validateModalBtns[i].btnName}${modalBtnSep}${validateModalBtns[i].btnType}${modalBtnSep}${validateModalBtns[i].btnAction}`,
        });
        modalBtnsConfig.push({
          attributeName: `${modalBtnPrefix}[${i}]`,
          attributeType: DataType.String,
          value: `${validateModalBtns[i].btnName}${modalBtnSep}${validateModalBtns[i].btnType}${modalBtnSep}${validateModalBtns[i].btnAction}`,
        });
      }
      // eslint-disable-next-line no-param-reassign
      validateData.modalBtns = modalBtns;
      attrConfig.push({
        attributeName: 'modalBtns',
        value: modalBtnsConfig,
      });
    }
    attrConfig.push({
      attributeName: 'openType',
      attributeType: DataType.String,
      value: validateData.openType,
    });
    attrConfig.push({
      [attributeNameProp]: 'pageCode',
      [attributeValueProp]: validateData.pageCode,
      [attributeTypeProp]: DataType.String,
    });
    attrConfig.push({
      [attributeNameProp]: 'pageMeaning',
      [attributeTypeProp]: DataType.String,
      [attributeValueProp]: validateData.pageMeaning,
    });
    attrConfig.push({
      [attributeNameProp]: 'params',
      [attributeValueProp]: validateData.params,
      [attributeTypeProp]: DataType.String,
    });
    // 滑出
    if (validateData.openType === ACTION_PAGE_TYPE.drawer) {
      attrConfig.push({
        [attributeNameProp]: 'openPageTypeDrawer',
        [attributeValueProp]: validateData.openPageTypeDrawer,
        [attributeTypeProp]: DataType.Number,
      });
    }
    // 弹出
    if (validateData.openType === ACTION_PAGE_TYPE.modal) {
      attrConfig.push({
        [attributeNameProp]: 'openPageTypeModal',
        [attributeValueProp]: validateData.openPageTypeModal,
        [attributeTypeProp]: DataType.String,
      });
    }
    return Promise.resolve();
  }

  getValidateLevel2ActionData(validateData, attrConfig) {
    // warn 删除pageMeaning 的属性
    // eslint-disable-next-line no-param-reassign
    delete validateData.pageMeaning;
    if (validateData.actionEvent) {
      attrConfig.push({
        attributeName: 'actionEvent',
        attributeType: DataType.String,
        value: validateData.actionEvent,
      });
    }
    return Promise.resolve();
  }

  // extraTab
  @Bind()
  handleExtraTabChange(tabKey) {
    this.setState({
      extraTabKey: tabKey,
    });
  }
}
