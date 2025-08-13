/**
 * fileUpload - 文件上传配置
 * @date: 2018-9-19
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Col, Form, Input, Row, Select } from 'hzero-ui';
import { connect } from 'dva';
import { filter, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import echarts from 'echarts';

import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
// import { Pie } from 'components/Charts';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';

import TableList from './TableList';
import Drawer from './Drawer';
import styles from './index.less';

const { Option } = Select;
const formItemLayout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 16 },
};
const tableOperatorStyle = {
  textAlign: 'right',
};

@connect(({ fileUpload, loading, user }) => ({
  fileUpload,
  user,
  fetchFileLoading: loading.effects['fileUpload/queryFileList'],
  saveHeaderLoading: loading.effects['fileUpload/saveHeader'],
  detailLoading: loading.effects['fileUpload/getUploadDetail'],
  saveDetailLoading:
    loading.effects['fileUpload/addConfigDetail'] || loading.effects['fileUpload/editConfigDetail'],
  tenantId: getCurrentOrganizationId(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hfile.fileUpload'] })
export default class FileUpload extends PureComponent {
  state = {
    visible: false,
    isCreate: false,
    nowTenantId: undefined, // 改变租户
    nowTenantName: undefined, // 租户名称
    newFileFormat: [], // 文件格式
    totalCapacity: 0,
    redisUsedCapacity: 0,
    totalCapacityUnit: 'MB',
  };

  ref = React.createRef();

  componentDidMount() {
    this.queryFileList();
    const { dispatch } = this.props;
    const lovCodes = {
      fileTypeList: 'HFLE.CONTENT_TYPE',
      fileFormatsList: 'HFLE.FILE_FORMAT',
      fileUnitList: 'HFLE.STORAGE_UNIT',
    };
    dispatch({
      type: 'fileUpload/init',
      payload: {
        lovCodes,
      },
    });
    this.renderECharts();
  }

  componentWillUnmount() {
    clearTimeout(this.timer);
  }

  componentDidUpdate() {
    const {
      fileUpload: { fileData = {} },
    } = this.props;
    const { totalCapacity, redisUsedCapacity, totalCapacityUnit } = this.state;
    if (
      fileData.redisUsedCapacity !== redisUsedCapacity ||
      fileData.totalCapacity !== totalCapacity ||
      fileData.totalCapacityUnit !== totalCapacityUnit
    ) {
      this.renderECharts();
    }
  }

  /**
   * 获取文件列表
   *
   * @param {*} [params={}]
   * @memberof FileUpload
   */
  @Bind()
  queryFileList() {
    const { dispatch, tenantId } = this.props;
    this.setState({
      nowTenantId: tenantId,
    });
    dispatch({
      type: 'fileUpload/queryFileList',
      payload: { tenantId },
    });
  }

  /**
   * 获取当前租户下的文件列表
   */
  @Bind()
  queryNowTenantFileList(params = {}) {
    const { dispatch } = this.props;
    const { nowTenantId } = this.state;
    dispatch({
      type: 'fileUpload/queryFileList',
      payload: { page: isEmpty(params) ? {} : params, tenantId: nowTenantId },
    });
  }

  /**
   * 获取文件类型
   *
   * @memberof FileUpload
   */
  @Bind()
  queryFiletype() {
    const { dispatch } = this.props;
    dispatch({
      type: 'fileUpload/queryFiletype',
    });
  }

  /**
   * 获取文件格式
   *
   * @memberof FileUpload
   */
  @Bind()
  queryFileFormat() {
    const { dispatch } = this.props;
    dispatch({
      type: 'fileUpload/queryFileFormat',
    });
  }

  /**
   * 获取单位
   *
   * @memberof FileUpload
   */
  @Bind()
  queryFileUnit() {
    const { dispatch } = this.props;
    dispatch({
      type: 'fileUpload/queryFileUnit',
    });
  }

  /**
   * 改变租户
   *
   * @param {*} val
   * @memberof FileUpload
   */
  @Bind()
  changeOrganizationId(val, item) {
    const { dispatch, form } = this.props;
    form.resetFields();
    dispatch({
      type: 'fileUpload/queryFileList',
      payload: { tenantId: val },
    });
    this.setState({
      nowTenantId: val,
      nowTenantName: item.tenantName,
    });
  }

  /**
   * 打开新增模态框
   *
   * @memberof FileUpload
   */
  @Bind()
  showModal() {
    const {
      dispatch,
      fileUpload: { fileFormatsList = [] },
    } = this.props;
    dispatch({
      type: 'fileUpload/updateState',
      payload: {
        fileDetail: {},
      },
    });
    this.setState({
      visible: true,
      isCreate: true,
      newFileFormat: fileFormatsList,
    });
  }

  /**
   * 关闭模态框
   *
   * @memberof FileUpload
   */
  @Bind()
  closeModal() {
    this.setState({
      visible: false,
      isCreate: false,
    });
  }

  /**
   * 获取表格中的数据,打开编辑模态框
   *
   * @param {*} record
   * @memberof FileUpload
   */
  @Bind()
  getRecordData(record) {
    const {
      dispatch,
      fileUpload: { fileFormatsList = [] },
    } = this.props;
    const { nowTenantId } = this.state;
    const { contentType = [] } = record;
    const newFileFormat = filter(
      fileFormatsList,
      (item) => contentType.indexOf(item.parentValue) >= 0
    );
    dispatch({
      type: 'fileUpload/updateState',
      payload: {
        fileDetail: {},
      },
    });
    dispatch({
      type: 'fileUpload/getUploadDetail',
      payload: {
        uploadConfigId: record.uploadConfigId,
        tenantId: nowTenantId,
      },
    });
    this.setState({
      visible: true,
      isCreate: false,
      newFileFormat,
    });
  }

  /**
   * 改变文件分类，设置文件格式状态
   * @param {*} newFileFormat
   */
  @Bind()
  changeFileFormats(newFileFormat) {
    this.setState({
      newFileFormat,
    });
  }

  // 保存头
  @Bind()
  handleSaveHeader() {
    const {
      dispatch,
      form,
      fileUpload: { fileData = {} },
    } = this.props;
    const { nowTenantId } = this.state;
    const {
      _token,
      objectVersionNumber,
      capacityConfigId,
      redisUsedCapacity,
      usedCapacity,
      listConfig = {},
    } = fileData;
    form.validateFields((err, values) => {
      if (!err) {
        dispatch({
          type: 'fileUpload/saveHeader',
          payload: {
            tenantId: nowTenantId,
            ...values,
            objectVersionNumber,
            capacityConfigId,
            _token,
          },
        }).then((res) => {
          if (res) {
            notification.success();
            dispatch({
              type: 'fileUpload/updateState',
              payload: {
                fileData: {
                  ...res,
                  redisUsedCapacity,
                  usedCapacity,
                  listConfig,
                },
              },
            });
          }
        });
      }
    });
  }

  /**
   * 新建文件上传详细配置
   *
   * @param {*} value
   * @memberof FileUpload
   */
  @Bind()
  handleAddConfigDetail(value) {
    const {
      dispatch,
      fileUpload: { pagination = {} },
    } = this.props;
    const { nowTenantId } = this.state;
    dispatch({
      type: 'fileUpload/addConfigDetail',
      payload: { tenantId: nowTenantId, ...value },
    }).then((res) => {
      if (res) {
        this.closeModal();
        notification.success();
        this.queryNowTenantFileList(pagination);
      }
    });
  }

  /**
   * 编辑文件上传详细配置
   *
   * @param {*} value
   * @memberof FileUpload
   */
  @Bind()
  handleEditConfigDetail(value) {
    const {
      dispatch,
      fileUpload: { pagination = {} },
    } = this.props;
    const { nowTenantId } = this.state;
    dispatch({
      type: 'fileUpload/editConfigDetail',
      payload: { tenantId: nowTenantId, ...value },
    }).then((res) => {
      if (res) {
        this.closeModal();
        notification.success();
        this.queryNowTenantFileList(pagination);
      }
    });
  }

  /**
   *  删除文件上传详细配置
   *
   * @param {*} values
   * @memberof FormManage
   */
  @Bind()
  handleDeleteConfigDetail(values) {
    const {
      dispatch,
      fileUpload: { pagination = {} },
    } = this.props;
    dispatch({
      type: 'fileUpload/deleteConfigDetail',
      payload: values,
    }).then((res) => {
      if (res) {
        this.queryNowTenantFileList(pagination);
        notification.success();
      }
    });
  }

  @Bind()
  renderForm() {
    const {
      fileUpload: { fileUnitList = [], fileData = {} },
      saveHeaderLoading,
      match,
    } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { totalCapacity, storageSize } = fileData;
    const fileMaxUnitSelector = getFieldDecorator('storageUnit', {
      initialValue: fileData.storageUnit ? fileData.storageUnit : 'MB',
    })(
      <Select style={{ width: '65px' }}>
        {fileUnitList &&
          fileUnitList.map((item) => (
            <Option value={item.value} key={item.value}>
              {item.meaning}
            </Option>
          ))}
      </Select>
    );
    const fileCapacityUnitSelector = getFieldDecorator('totalCapacityUnit', {
      initialValue: fileData.totalCapacityUnit ? fileData.totalCapacityUnit : 'MB',
    })(
      <Select style={{ width: '65px' }}>
        {fileUnitList &&
          fileUnitList.map((item) => (
            <Option value={item.value} key={item.value}>
              {item.meaning}
            </Option>
          ))}
      </Select>
    );
    // const piePercent =
    //   ((fileData.redisUsedCapacity
    //     ? fileData.totalCapacityUnit === 'MB'
    //       ? Math.round(fileData.redisUsedCapacity / 1024 / 1024)
    //       : Math.round(fileData.redisUsedCapacity / 1024)
    //     : 0) /
    //     (fileData.totalCapacity ? fileData.totalCapacity : 1)) *
    //   100;
    return (
      <Form>
        <Row type="flex">
          <Col span={12}>
            <Row type="flex" gutter={24} align="bottom">
              <Col span={16}>
                <Form.Item
                  label={intl.get('hfile.fileUpload.model.fileUpload.totalCapacity').d('最大容量')}
                  {...formItemLayout}
                >
                  {getFieldDecorator('totalCapacity', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hfile.fileUpload.model.fileUpload.totalCapacity')
                            .d('最大容量'),
                        }),
                      },
                      {
                        min: 0,
                        pattern: /^\d+$/,
                        message: intl
                          .get('hfile.fileUpload.view.message.patternValidate')
                          .d('请输入大于等于0的整数'),
                      },
                    ],
                    initialValue: totalCapacity,
                  })(<Input type="number" addonAfter={fileCapacityUnitSelector} />)}
                </Form.Item>
              </Col>
            </Row>
            <Row type="flex" gutter={24} align="bottom">
              <Col span={16}>
                <Form.Item
                  label={intl
                    .get('hfile.fileUpload.model.fileUpload.storageSize')
                    .d('文件大小限制')}
                  {...formItemLayout}
                >
                  {getFieldDecorator('storageSize', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hfile.fileUpload.model.fileUpload.storageSize')
                            .d('文件大小限制'),
                        }),
                      },
                      {
                        min: 0,
                        pattern: /^\d+$/,
                        message: intl
                          .get('hfile.fileUpload.view.message.patternValidate')
                          .d('请输入大于等于0的整数'),
                      },
                    ],
                    initialValue: storageSize,
                  })(<Input type="number" addonAfter={fileMaxUnitSelector} />)}
                </Form.Item>
              </Col>
              <Col span={4} className={styles.buttonSave}>
                <Form.Item>
                  <ButtonPermission
                    data-code="search"
                    type="primary"
                    htmlType="submit"
                    onClick={this.handleSaveHeader}
                    loading={saveHeaderLoading}
                    permissionList={[
                      {
                        code: `${match.path}.button.save`,
                        type: 'button',
                        meaning: '文件上传配置-保存',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.save').d('保存')}
                  </ButtonPermission>
                </Form.Item>
              </Col>
            </Row>
          </Col>
          <Col span={12}>
            <Col span={8} className={styles.pie}>
              <div ref={this.ref} style={{ height: 200 }} />
            </Col>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * 渲染已使用容量的显示图表
   */
  @Bind()
  renderECharts() {
    if (this.ref.current) {
      const eTable = echarts.init(this.ref.current);
      const {
        fileUpload: { fileData = {} },
      } = this.props;
      this.setState({
        totalCapacity: fileData.totalCapacity,
        redisUsedCapacity: fileData.redisUsedCapacity,
        totalCapacityUnit: fileData.totalCapacityUnit,
      });
      // eslint-disable-next-line no-nested-ternary
      const redisUsedCapacity = fileData.redisUsedCapacity
        ? fileData.totalCapacityUnit === 'MB'
          ? Math.round(fileData.redisUsedCapacity / 1024 / 1024)
          : Math.round(fileData.redisUsedCapacity / 1024)
        : 0;
      const pieTotal = `${redisUsedCapacity}/${
        fileData.totalCapacity ? fileData.totalCapacity : 0
      }(${fileData.totalCapacityUnit ? fileData.totalCapacityUnit : 'MB'})`;
      const capacityNumber = fileData.totalCapacity ? fileData.totalCapacity : 0;
      const unusedCapacity = capacityNumber - redisUsedCapacity;
      const data =
        capacityNumber === 0
          ? [
              {
                value: 0,
                name: intl
                  .get('hfile.fileUpload.model.fileUpload.redisUnusedCapacity')
                  .d('未使用容量'),
              },
            ]
          : [
              {
                value: redisUsedCapacity,
                name: intl
                  .get('hfile.fileUpload.model.fileUpload.redisUsedCapacity')
                  .d('已使用容量'),
              },
              {
                value: unusedCapacity > 0 ? unusedCapacity : 0,
                name: intl
                  .get('hfile.fileUpload.model.fileUpload.redisUnusedCapacity')
                  .d('未使用容量'),
              },
            ];
      const option = {
        title: {
          text: `${intl
            .get('hfile.fileUpload.model.fileUpload.redisUsedCapacity')
            .d('已使用容量')}\n\n${pieTotal}`,
          left: 'center',
          top: 'center',
          textStyle: {
            fontWeight: '500',
            fontSize: 13,
            color: 'rgba(0, 0, 0, 0.85)',
          },
        },
        color: capacityNumber === 0 ? ['#dadada'] : ['#29bece', '#dadada'],
        tooltip: {},
        series: [
          {
            type: 'pie',
            radius: ['65%', '90%'],
            label: {
              normal: {
                show: false,
                position: 'center',
              },
            },
            labelLine: {
              normal: {
                show: false,
              },
            },
            data,
          },
        ],
      };
      eTable.setOption(option);
    } else {
      this.timer = setTimeout(() => {
        this.renderECharts();
      }, 200);
    }
  }

  render() {
    const {
      match,
      user: {
        currentUser: { organizationId, tenantName },
      },
      fileUpload: {
        fileData: { listConfig = {} },
        fileTypeList = [],
        fileFormatsList = [],
        fileUnitList = [],
        pagination = {},
        fileDetail = {},
      },
      fetchFileLoading,
      saveDetailLoading,
      detailLoading = false,
    } = this.props;
    const { visible, isCreate, newFileFormat = [], nowTenantId, nowTenantName } = this.state;
    const drawerProps = {
      visible,
      isCreate,
      detailLoading,
      fileDetail,
      fileTypeList,
      fileFormatsList,
      fileUnitList,
      newFileFormat,
      anchor: 'right',
      saving: saveDetailLoading,
      onCancel: this.closeModal,
      onAdd: this.handleAddConfigDetail,
      onEdit: this.handleEditConfigDetail,
      onChangeFileFormats: this.changeFileFormats,
    };
    const listProps = {
      pagination,
      listConfig,
      match,
      loading: fetchFileLoading,
      onGetRecordData: this.getRecordData,
      onDelete: this.handleDeleteConfigDetail,
      onChangePage: this.queryNowTenantFileList,
    };
    return (
      <>
        <Header title={intl.get('hfile.fileUpload.view.message.title').d('文件上传配置')}>
          {!isTenantRoleLevel() && (
            <Lov
              style={{ width: 200, marginLeft: 8 }}
              value={organizationId}
              textValue={nowTenantId === organizationId ? tenantName : nowTenantName}
              code="HPFM.TENANT"
              onChange={(val, item) => {
                this.changeOrganizationId(val, item);
              }}
              allowClear={false}
            />
          )}
        </Header>
        <Content>
          {this.renderForm()}
          <div className="table-list-search" style={tableOperatorStyle}>
            <ButtonPermission
              type="primary"
              permissionList={[
                {
                  code: `${match.path}.button.add`,
                  type: 'button',
                  meaning: '文件上传配置-添加详细配置',
                },
              ]}
              onClick={() => this.showModal()}
            >
              {intl.get('hfile.fileUpload.view.button.add').d('添加详细配置')}
            </ButtonPermission>
          </div>
          <TableList {...listProps} />
        </Content>
        <Drawer {...drawerProps} />
      </>
    );
  }
}
