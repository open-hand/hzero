/**
 * LovSetting - 值集视图配置窗口
 * @date: 2018-6-26
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Modal, Tag } from 'hzero-ui';
import lodash, { isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import cacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';
import CustomTable from 'components/CustomTable';
import { Button as ButtonPermission } from 'components/Permission';
import TLEditor from 'components/TLEditor';

import { enableRender, operatorRender } from 'utils/renderer';
import { filterNullValueObject, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import formatterCollections from 'utils/intl/formatterCollections';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

import PreviewModal from './PreviewModal';
import SearchForm from './SearchForm';
import CopyValue from './CopyValue';

/**
 * 使用 Form.Item 组件
 */
const FormItem = Form.Item;
/**
 * lov弹框编辑
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} modalVisible - 控制modal显示/隐藏属性
 * @reactProps {Function} handleAdd - 数据保存
 * @reactProps {Function} showCreateModal - 控制modal显示隐藏方法
 * @return React.element
 */
const CreateForm = Form.create({ fieldNameProp: null })(props => {
  const { form, modalVisible, handleAdd, showCreateModal, confirmLoading, isSiteFlag } = props;
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      handleAdd({ ...fieldsValue, viewCode: lodash.trim(fieldsValue.viewCode) }, form);
    });
  };
  return (
    <Modal
      title={intl.get('hpfm.lov.view.message.title.modal.lovSetting').d('新建值集视图')}
      visible={modalVisible}
      onOk={okHandle}
      confirmLoading={confirmLoading}
      width={520}
      onCancel={() => showCreateModal(false, form)}
      wrapClassName="ant-modal-sidebar-right"
      transitionName="move-right"
    >
      <React.Fragment>
        <FormItem
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.lov.model.lov.viewCode').d('视图代码')}
        >
          {form.getFieldDecorator('viewCode', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.lov.model.lov.viewCode').d('视图代码'),
                }),
              },
              {
                max: 80,
                message: intl.get('hzero.common.validation.max', {
                  max: 80,
                }),
              },
              {
                pattern: CODE_UPPER,
                message: intl
                  .get('hzero.common.validation.codeUpper')
                  .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
              },
            ],
          })(<Input trim typeCase="upper" inputChinese={false} />)}
        </FormItem>
        <FormItem
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.lov.model.lov.viewName').d('视图名称')}
        >
          {form.getFieldDecorator('viewName', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.lov.model.lov.viewName').d('视图名称'),
                }),
              },
              {
                max: 80,
                message: intl.get('hzero.common.validation.max', {
                  max: 80,
                }),
              },
            ],
          })(
            <TLEditor
              label={intl.get('hpfm.lov.model.lov.viewName').d('视图名称')}
              field="viewName"
            />
          )}
        </FormItem>
        <FormItem
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.lov.model.lov.valueField').d('值字段名')}
        >
          {form.getFieldDecorator('valueField', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.lov.model.lov.valueField').d('值字段名'),
                }),
              },
              {
                max: 30,
                message: intl.get('hzero.common.validation.max', {
                  max: 30,
                }),
              },
            ],
          })(<Input inputChinese={false} />)}
        </FormItem>
        <FormItem
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.lov.model.lov.displayField').d('显示字段名')}
        >
          {form.getFieldDecorator('displayField', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.lov.model.lov.displayField').d('显示字段名'),
                }),
              },
              {
                max: 30,
                message: intl.get('hzero.common.validation.max', {
                  max: 30,
                }),
              },
            ],
          })(<Input inputChinese={false} />)}
        </FormItem>
        {isSiteFlag && (
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('entity.tenant.name').d('租户名称')}
          >
            {form.getFieldDecorator('tenantId')(
              <Lov
                code="HPFM.TENANT"
                textField="tenantName"
                onChange={() => {
                  form.resetFields('lovId');
                }}
              />
            )}
          </FormItem>
        )}
        <FormItem
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.lov.model.lov.lovId').d('值集')}
        >
          {form.getFieldDecorator('lovId', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.lov.model.lov.lovId').d('值集'),
                }),
              },
            ],
          })(
            <Lov
              code={!isSiteFlag ? 'HPFM.LOV.LOV_DETAIL.ORG' : 'HPFM.LOV.LOV_DETAIL'}
              queryParams={{
                tenantId:
                  form.getFieldValue('tenantId') !== undefined
                    ? form.getFieldValue('tenantId')
                    : '',
                lovQueryFlag: 1,
              }}
            />
          )}
        </FormItem>
      </React.Fragment>
    </Modal>
  );
});

/**
 * lov定义
 * @extends {Component} - React.Component
 * @reactProps {Object} [history={}]
 * @reactProps {Object} lovSetting - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ lovSetting, loading }) => ({
  lovSetting,
  isSiteFlag: !isTenantRoleLevel(),
  currentTenantId: getCurrentOrganizationId(),
  list: lovSetting.data,
  copyLoading: loading.effects['lovSetting/copyLovView'],
  loading: loading.effects['lovSetting/fetchLovList'],
  confirmLoading: loading.effects['lovSetting/addLovValue'],
}))
@formatterCollections({
  code: ['hpfm.lov', 'entity.tenant'],
})
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hpfm/lov-view/lov-view-list' })
export default class LovSetting extends React.Component {
  constructor(props) {
    super(props);
    this.searchForm = {};
    this.state = {
      modalVisible: false,
      tenantStatus: {
        display: 'none',
        required: false,
      },
      previewVisible: false,
      viewCode: '',
      viewTenantId: '',
      copyValueVisible: false,
      copyValueData: {}, // 复制选择的值集
    };
  }

  componentDidMount() {
    const {
      lovSetting: { pagination } = {},
      location: { state: { _back } = {} },
    } = this.props;
    this.handleSearch(isUndefined(_back) ? {} : pagination);
  }

  /**
   * 列表查询
   * @param {Object} pagination 查询参数
   */
  @Bind()
  handleSearch(pagination = {}) {
    const { dispatch = e => e } = this.props;
    const form = this.searchForm.props && this.searchForm.props.form;
    const params = isUndefined(form) ? {} : form.getFieldsValue();
    const filterValues = filterNullValueObject(params);
    dispatch({
      type: 'lovSetting/fetchLovList',
      payload: {
        page: pagination,
        ...filterValues,
      },
    }).then(res => {
      this.searchCallback(res);
    });
  }

  /**
   *
   * @param {object} ref - SearchForm子组件对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.searchForm = ref;
  }

  @Bind()
  handleLovCopy(record) {
    const { dispatch, lovSetting: { pagination = {} } = {}, isSiteFlag } = this.props;
    if (isSiteFlag) {
      this.setState({ copyValueVisible: true, copyValueData: record });
      return;
    }
    dispatch({
      type: 'lovSetting/copyLovView',
      payload: { viewCode: record.viewCode, viewHeaderId: record.viewHeaderId },
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  @Bind()
  handleCopyValue(data) {
    const { dispatch, lovSetting: { pagination = {} } = {} } = this.props;
    const {
      copyValueData: { viewCode, viewHeaderId },
    } = this.state;
    dispatch({
      type: 'lovSetting/copyLovView',
      payload: { ...data, viewCode, viewHeaderId },
    }).then(res => {
      if (res) {
        this.setState({ copyValueVisible: false, copyValueData: {} });
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  @Bind()
  hideCopyValue() {
    this.setState({ copyValueVisible: false });
  }

  render() {
    const {
      match,
      loading,
      isSiteFlag,
      currentTenantId,
      copyLoading = false,
      lovSetting: { list = {}, pagination = {} },
    } = this.props;
    const { copyValueVisible = false } = this.state;

    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };

    const copyValueProps = {
      visible: copyValueVisible,
      loading: copyLoading,
      onOk: this.handleCopyValue,
      onCancel: this.hideCopyValue,
    };

    // const otherProps = {
    //   color: 'red',
    //   background: 'white',
    // };

    const columns = [
      isSiteFlag && {
        title: intl.get('entity.tenant.name').d('租户名称'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hpfm.lov.model.lov.viewCode').d('视图代码'),
        dataIndex: 'viewCode',
        width: 300,
      },
      {
        title: intl.get('hpfm.lov.model.lov.viewName').d('视图名称'),
        dataIndex: 'viewName',
      },
      {
        title: intl.get('hpfm.lov.model.lov.sourceCode').d('值集编码'),
        dataIndex: 'lovCode',
        width: 300,
      },
      {
        title: intl.get('hpfm.lov.model.lov.lovName').d('值集名称'),
        dataIndex: 'lovName',
        width: 300,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 90,
        render: enableRender,
      },
      !isSiteFlag && {
        title: intl.get('hzero.common.source').d('来源'),
        width: 110,
        render: (_, record) =>
          currentTenantId === record.tenantId ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'setting',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.setting`,
                      type: 'button',
                      meaning: '值集视图-配置',
                    },
                  ]}
                  onClick={() => {
                    this.showEditModal(record);
                  }}
                >
                  {intl.get('hpfm.lov.view.option.setting').d('配置')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hpfm.lov.view.option.setting').d('配置'),
            },
            {
              key: 'copy',
              ele: isSiteFlag
                ? record.tenantId === currentTenantId && (
                <ButtonPermission
                  type="text"
                  permissionList={[
                        {
                          code: `${match.path}.button.copy`,
                          type: 'button',
                          meaning: '值集视图-复制',
                        },
                      ]}
                  onClick={() => this.handleLovCopy(record)}
                >
                  {intl.get('hzero.common.button.copy').d('复制')}
                </ButtonPermission>
                  )
                : record.tenantId !== currentTenantId && (
                <ButtonPermission
                  type="text"
                  permissionList={[
                        {
                          code: `${match.path}.button.copy`,
                          type: 'button',
                          meaning: '值集视图-复制',
                        },
                      ]}
                  onClick={() => this.handleLovCopy(record)}
                >
                  {intl.get('hzero.common.button.copy').d('复制')}
                </ButtonPermission>
                  ),
              len: 2,
              title: intl.get('hzero.common.button.copy').d('复制'),
            },
            {
              key: 'preview',
              ele: (
                <Lov
                  code={record.viewCode}
                  isButton
                  originTenantId={record.tenantId}
                  queryParams={{
                    tenantId: record.tenantId,
                  }}
                  // TODO: 使用了没有暴露的属性 prefixCls
                  href={undefined}
                  prefixCls=""
                >
                  {intl.get('hpfm.lov.view.option.preview').d('预览')}
                </Lov>
              ),
              len: 5,
              title: intl.get('hpfm.lov.view.option.preview').d('预览'),
              noTooltip: true,
            },
          ];
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    const tableProps = {
      columns,
      loading,
      pagination,
      bordered: true,
      rowKey: 'viewHeaderId',
      rowSelection: null,
      dataSource: list.content,
      onChange: this.handleSearch,
    };

    return (
      <React.Fragment>
        {this.renderHeader()}
        <Content>
          <div className="table-list-search">
            <SearchForm {...filterProps} />
          </div>
          <CustomTable {...tableProps} />
          {copyValueVisible && <CopyValue {...copyValueProps} />}
        </Content>
        {this.renderOther()}
      </React.Fragment>
    );
  }

  createForm; // 侧边栏内部引用

  /**
   * 跳转维护页面
   * @param {Object} record 编辑时候传入的当前行值
   */
  @Bind()
  showEditModal(record) {
    const { history, dispatch } = this.props;
    dispatch({
      type: 'lovSetting/updateState',
      payload: {
        headData: {},
        rowData: {},
      },
    });
    history.push(`/hpfm/lov-view/detail/${record.viewHeaderId}`);
  }

  /**
   * 新增
   * @param {Object} fieldsValue 传递的filedvalue
   */
  @Bind()
  handleAdd(fieldsValue, form) {
    const { dispatch, history } = this.props;
    const defaultData = {
      delayLoadFlag: 0,
      enabledFlag: 1,
    };
    const data = {
      ...fieldsValue,
      ...defaultData,
      pageSize: 10,
    };
    dispatch({
      type: 'lovSetting/addLovValue',
      payload: data,
    }).then(response => {
      if (response) {
        this.setState({
          modalVisible: false,
        });
        form.resetFields();
        notification.success();
        history.push(`/hpfm/lov-view/detail/${response.viewHeaderId}`);
      }
    });
  }

  /**
   * 刷新
   */
  @Bind()
  refreshValue() {
    const { dispatch } = this.props;
    const data = {
      page: 0,
      size: 10,
    };
    dispatch({
      type: 'lovSetting/fetchLovList',
      payload: data,
    }).then(() => {
      this.setState({
        selectedRows: [],
      });
      this.handleFormReset();
    });
  }

  /**
   * 删除
   */
  @Bind()
  deleteValue() {
    const { dispatch } = this.props;
    let deleteArr = [];
    const onOk = () => {
      const datas = this.state.selectedRows;
      if (datas) {
        for (let i = 0; i < datas.length; i++) {
          const data = {
            viewHeaderId: datas[i].viewHeaderId,
            objectVersionNumber: datas[i].objectVersionNumber,
          };
          deleteArr.push(data);
        }
      } else {
        deleteArr = [];
      }
      dispatch({
        type: 'lovSetting/deleteLovValue',
        payload: deleteArr,
      }).then(response => {
        if (response) {
          this.refreshValue();
          notification.success();
        }
      });
    };
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk,
    });
  }

  /**
   * 控制modal弹出层的展示和隐藏
   * @param {boolean} flag 显示/隐藏标记
   */
  @Bind()
  showCreateModal(flag, form) {
    this.setState({
      modalVisible: !!flag,
    });
    if (!flag) {
      form.resetFields();
    }
  }

  /**
   * 预览
   * @param {Object} record 行数据
   */
  @Bind()
  showLovComponent(flag, record) {
    if (flag === false && this.createForm) {
      this.createForm.resetForm();
    }
    this.setState({
      previewVisible: flag,
    });
    if (record) {
      this.setState({
        viewCode: record.viewCode,
        viewTenantId: record.tenantId,
      });
    }
    if (!flag) {
      this.setState({
        viewCode: '',
      });
    }
  }

  /**
   * 搜索回调
   */
  @Bind()
  searchCallback() {
    this.setState({
      selectedRows: [],
    });
  }

  /**
   * 其他结构渲染
   * @returns
   */
  renderOther() {
    const { modalVisible, tenantStatus, previewVisible, viewCode, viewTenantId } = this.state;
    const { confirmLoading, isSiteFlag } = this.props;
    const parentMethods = {
      handleAdd: this.handleAdd,
      showCreateModal: this.showCreateModal,
    };
    return (
      <div>
        <CreateForm
          {...parentMethods}
          modalVisible={modalVisible}
          tenantStatus={tenantStatus}
          confirmLoading={confirmLoading}
          isSiteFlag={isSiteFlag}
          onRef={ref => {
            this.createForm = ref;
          }}
        />
        <PreviewModal
          tenantId={viewTenantId}
          viewCode={viewCode}
          showLovComponent={this.showLovComponent}
          previewVisible={previewVisible}
        />
      </div>
    );
  }

  /**
   * 渲染头部
   * @returns
   */
  renderHeader() {
    const { selectedRows = [] } = this.state;
    const { match } = this.props;
    return (
      <Header title={intl.get('hpfm.lov.view.message.title.lovSetting').d('值集视图配置')}>
        <ButtonPermission
          icon="plus"
          type="primary"
          permissionList={[
            {
              code: `${match.path}.button.create`,
              type: 'button',
              meaning: '值集视图-新建',
            },
          ]}
          onClick={() => this.showCreateModal(true)}
        >
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
        {selectedRows.length > 0 && (
          <ButtonPermission
            icon="minus"
            permissionList={[
              {
                code: `${match.path}.button.delete`,
                type: 'button',
                meaning: '值集视图-删除',
              },
            ]}
            onClick={this.deleteValue}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        )}
      </Header>
    );
  }
}
