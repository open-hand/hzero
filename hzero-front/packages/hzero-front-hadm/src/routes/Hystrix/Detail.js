/* eslint-disable no-nested-ternary */
/**
 * FIXME: 国际化
 * Detail - 熔断设置详情页
 * @date: 2018-9-15
 * @author: LZH <zhaohui.liu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, hand
 */
import React, { Component } from 'react';
import { Button, Form, Col, Row, Input, Modal, Table, Spin, Card } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';

import { Header, Content } from 'components/Page';
import Lov from 'components/Lov';
import Switch from 'components/Switch';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { enableRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import {
  DETAIL_CARD_TABLE_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT,
} from 'utils/constants';

import DetailFilter from './DetailFilter';
import DetailDrawer from './DetailDrawer';

/**
 *熔断详情设置
 * @extends {Component} - React.Component
 * @reactProps {object}
 */
@connect(({ hadmHystrix, loading }) => ({
  fetchHeaderInformationLoading: loading.effects['hadmHystrix/fetchHeaderInformation'],
  fetchDetailListLoading: loading.effects['hadmHystrix/fetchDetailList'],
  addLoading: loading.effects['hadmHystrix/add'],
  deleteLoading: loading.effects['hadmHystrix/deleteDetails'],
  refreshLoading: loading.effects['hadmHystrix/refresh'],
  hadmHystrix,
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hadm.hystrix'] })
export default class Detail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      conf: {},
      serviceName: '',
      selectedRows: [],
      oldHeaderInformation: {},
      modalVisible: false,
    };
  }

  componentDidMount() {
    const { match, dispatch } = this.props;
    const { confId } = match.params;
    this.handleSearch({ confId });
    this.getHeaderInformation();
    dispatch({
      type: 'hadmHystrix/updateState',
      payload: {
        detailList: [],
        headerInformation: {},
      },
    });
  }

  /**
   * 获取头信息
   */
  @Bind()
  getHeaderInformation() {
    const { dispatch, match } = this.props;
    const { confId } = match.params;
    dispatch({
      type: 'hadmHystrix/fetchHeaderInformation',
      payload: { confId },
    })
      .then((res) => {
        if (res) {
          const {
            hadmHystrix: { headerInformation },
          } = this.props;
          dispatch({
            type: 'hadmHystrix/fetchProperNameList',
            payload: {
              lovCode: 'HPFM.HYSTRIX_CONF_PROPS',
              parentValue: headerInformation.confKey,
            },
          });
          this.setState({
            serviceName: headerInformation.serviceName,
            oldHeaderInformation: headerInformation,
          });
        }
      })
      .then((res) => {
        if (res) {
          dispatch({
            type: 'hadmHystrix/fetchConfTypeCodeList',
            payload: { lovCode: 'HADM.HYSTRIX_CONF_TYPE' },
          });
        }
      });
  }

  /**
   * 搜索细则
   * @param {object} fields
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, match } = this.props;
    const { confId } = match.params;
    dispatch({
      type: 'hadmHystrix/fetchDetailList',
      payload: { ...fields, confId },
    });
  }

  /**
   * 批量删除细则
   */
  @Bind()
  handleDeleteDetails() {
    const { dispatch, match } = this.props;
    const { selectedRows } = this.state;
    const { confId } = match.params;
    if (selectedRows.length > 0) {
      const onOk = () => {
        dispatch({
          type: 'hadmHystrix/deleteDetails',
          payload: selectedRows,
        }).then((res) => {
          if (res) {
            notification.success();
            this.handleSearch({ confId });
            this.setState({ selectedRows: [] });
          }
        });
      };
      Modal.confirm({
        title: intl.get(`hzero.common.message.confirm.remove`).d('确定删除选中数据？'),
        onOk,
      });
    } else {
      notification.warning({
        message: intl.get('hzero.common.message.confirm.selected.atLeast').d('请至少选择一行数据'),
      });
    }
  }

  /**
   * 新增细则
   */
  @Bind()
  addDetails(fields) {
    const {
      dispatch,
      hadmHystrix: { headerInformation },
    } = this.props;
    const { confId } = headerInformation;
    const { conf } = this.state;
    const item = { ...conf, ...fields };
    this.setState({ conf: { ...conf, fields } });
    dispatch({
      type: 'hadmHystrix/add',
      payload: { ...headerInformation, hystrixConfLines: [item] },
    }).then((res) => {
      if (res) {
        this.hideModal();
        notification.success();
        this.handleSearch({ confId });
        this.getHeaderInformation();
      }
    });
  }

  /**
   * 修改当前行信息
   */
  @Bind()
  editLine(record) {
    const {
      hadmHystrix: { propertyNameList },
    } = this.props;
    const property = propertyNameList.find((e) => e.value === record.propertyName);
    const propertyRemark = property.meaning;
    this.setState({ conf: { ...record, propertyRemark }, modalVisible: true });
  }

  /**
   * 保存头信息
   */
  @Bind()
  handleSave() {
    const { dispatch, form } = this.props;
    let headerInformationChangeFlag = false; // 判断头信息是否改变，改变则为true，不变为false
    const { oldHeaderInformation } = this.state;
    const { objectVersionNumber, confKey, confId, _token } = oldHeaderInformation;
    form.validateFields((err, values) => {
      if (!err) {
        const {
          confTypeCode,
          enabledFlag,
          remark,
          serviceConfLabel,
          serviceName,
          serviceConfProfile,
        } = values;
        const newHeaderInformation = {
          confId,
          confKey,
          objectVersionNumber,
          _token,
          confTypeCode,
          enabledFlag,
          remark,
          serviceConfLabel,
          serviceName,
          serviceConfProfile,
        };
        for (const key in newHeaderInformation) {
          if (newHeaderInformation[key] !== oldHeaderInformation[key]) {
            // 当新的头与旧的头有一条字段的值不一样，就将headerInformationChangeFlag设置成true
            headerInformationChangeFlag = true;
            break;
          }
        }
        if (headerInformationChangeFlag) {
          dispatch({
            type: 'hadmHystrix/add',
            payload: newHeaderInformation,
          }).then((res) => {
            if (res && !res.failed) {
              notification.success();
              this.handleSearch();
              this.getHeaderInformation();
            }
          });
        } else {
          notification.warning({
            message: intl.get(`hadm.hystrix.view.message.title.noChange`).d('未修改数据'),
          });
        }
      }
    });
  }

  /**
   * 新建熔断类型展示模态框
   */
  @Bind()
  showModal() {
    this.setState({ conf: { enabledFlag: 1 }, modalVisible: true });
  }

  /**
   * 隐藏模态框
   */
  @Bind()
  hideModal() {
    const { saving = false } = this.props;
    if (!saving) {
      this.setState({ modalVisible: false });
    }
  }

  /**
   * 刷新头
   * */
  @Bind()
  handleRefreshDetail() {
    const {
      dispatch,
      hadmHystrix: { headerInformation },
    } = this.props;
    const { confId } = headerInformation;
    dispatch({
      type: 'hadmHystrix/refresh',
      payload: [{ confId }],
    }).then((res) => {
      if (res) {
        notification.success();
        this.getHeaderInformation();
      }
    });
  }

  /**
   * 选择规则/
   * @param {array} selectedRowKeys
   * @param {array} selectedRows
   */
  @Bind()
  handleRowSelectedChange(selectedRowKeys, selectedRows) {
    this.setState({ selectedRows });
  }

  @Bind()
  onHandleSelect(rowKey) {
    const { serviceName } = this.state;
    const {
      dispatch,
      hadmHystrix: { headerInformation },
    } = this.props;
    if (serviceName === '' && rowKey) {
      this.props.form.setFieldsValue({ serviceName: rowKey });
      this.setState({ serviceName: rowKey });
    } else if (rowKey === null) {
      dispatch({
        type: 'hadmHystrix/updateState',
        payload: {
          headerInformation: { ...headerInformation, serviceName: rowKey, serviceConfLabel: null },
        },
      });
      this.props.form.resetFields('serviceConfLabel', []);
      this.setState({ serviceName: '' });
    } else if (serviceName !== rowKey) {
      dispatch({
        type: 'hadmHystrix/updateState',
        payload: {
          headerInformation: { ...headerInformation, serviceName: rowKey, serviceConfLabel: null },
        },
      });
      this.props.form.resetFields('serviceConfLabel', []);
      this.setState({ serviceName: rowKey });
    }
  }

  @Bind()
  onHandleSelectServiceConfLabel(rowKey, record) {
    const {
      dispatch,
      hadmHystrix: { headerInformation },
    } = this.props;
    dispatch({
      type: 'hadmHystrix/updateState',
      payload: { headerInformation: { ...headerInformation, serviceConfLabel: record.name } },
    });
    this.props.form.setFieldsValue({ serviceConfLabel: record.name });
  }

  @Bind()
  onHandleStandardTableChange(pagination) {
    const {
      hadmHystrix: { detailQuery },
    } = this.props;
    this.handleSearch({
      page: pagination.current - 1,
      size: pagination.pageSize,
      ...detailQuery,
    });
  }

  render() {
    const {
      form,
      match,
      location: { search },
      fetchHeaderInformationLoading,
      fetchDetailListLoading,
      addLoading,
      deleteLoading,
      refreshLoading,
      form: { getFieldDecorator },
      hadmHystrix: {
        headerInformation,
        detailList,
        detailPagination,
        propertyNameList,
        confTypeCodeList,
      },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const basePath = '/hadm/hystrix';
    const { serviceName, conf, selectedRows, modalVisible } = this.state;
    const nowConf = confTypeCodeList.find((e) => e.value === headerInformation.confKey);
    const columns = [
      {
        title: intl.get(`hadm.hystrix.model.hystrix.propertyName`).d('参数代码'),
        width: 120,
        dataIndex: 'propertyName',
      },
      {
        title: intl.get(`hadm.hystrix.model.hystrix.propertyRemark`).d('参数描述'),
        dataIndex: 'propertyRemark',
        render: (val, record) => {
          const data = propertyNameList.find((e) => e.value === record.propertyName);
          if (data) {
            return data.meaning;
          }
        },
      },
      {
        title: intl.get(`hadm.hystrix.model.hystrix.propertyValue`).d('参数值'),
        width: 200,
        dataIndex: 'propertyValue',
      },
      {
        title: intl.get(`hzero.common.view.description`).d('描述'),
        dataIndex: 'remark',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        key: 'enabledFlag',
        width: 80,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 60,
        render: (val, record) => (
          <a
            onClick={() => {
              this.editLine(record);
            }}
          >
            {intl.get('hzero.common.button.edit').d('编辑')}
          </a>
        ),
      },
    ];
    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n.confLineId),
      onChange: this.handleRowSelectedChange,
    };
    const detailFilterProps = {
      propertyNameList,
      onFilterChange: this.handleSearch,
      showModel: this.showModel,
      addDetails: this.addDetails,
      handleDeleteDetails: this.handleDetails,
    };
    const detailDrawerProps = {
      propertyNameList,
      headerInformation,
      addLoading,
      onOk: this.addDetails,
      onCancel: this.hideModal,
      anchor: 'right',
      data: conf,
      visible: modalVisible,
      title: conf.confLineId
        ? intl.get(`hadm.hystrix.view.title.editForm`).d('编辑值')
        : intl.get(`hadm.hystrix.view.title.createForm`).d('创建值'),
    };
    return (
      <>
        <Header
          title={intl.get(`hadm.hystrix.view.title.hystrix.detail`).d('熔断规则定义')}
          backPath={
            match.path.indexOf('/private') === 0
              ? `/private${basePath}/list?access_token=${accessToken}`
              : `${basePath}/list`
          }
        >
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSave}
            loading={fetchHeaderInformationLoading || addLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button
            type="default"
            icon="sync"
            onClick={this.handleRefreshDetail}
            loading={refreshLoading || fetchHeaderInformationLoading}
          >
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </Button>
        </Header>
        <Content>
          <Spin spinning={fetchHeaderInformationLoading}>
            <Card
              key="event-rule"
              bordered={false}
              className={DETAIL_CARD_TABLE_CLASSNAME}
              title={
                <h3>{intl.get(`hadm.hystrix.view.title.hystrix.detail`).d('熔断规则定义')}</h3>
              }
            >
              <Form>
                <Row {...SEARCH_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get(`hadm.hystrix.model.hystrix.confKey`).d('类型')}
                    >
                      {nowConf && nowConf.meaning}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get(`hadm.hystrix.model.hystrix.confTypeCode`).d('代码')}
                    >
                      {getFieldDecorator('confTypeCode', {
                        initialValue: headerInformation.confTypeCode,
                      })(<span>{headerInformation.confTypeCode}</span>)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get(`hadm.hystrix.model.hystrix.refreshTime`).d('刷新时间')}
                    >
                      {headerInformation.refreshTime}
                    </Form.Item>
                  </Col>
                </Row>
                <Row {...SEARCH_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get(`hadm.hystrix.model.hystrix.refreshMessage`).d('刷新消息')}
                    >
                      {headerInformation.refreshMessage}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get(`hadm.hystrix.model.hystrix.refreshStatus`).d('刷新状态')}
                    >
                      {headerInformation.refreshStatus === 1
                        ? intl.get('hadm.hystrix.model.hystrix.refreshSuccess').d('刷新成功')
                        : headerInformation.refreshStatus === 0
                        ? intl.get('hadm.hystrix.model.hystrix.refreshFailed').d('刷新失败')
                        : intl.get('hadm.hystrix.model.hystrix.noRefreshSuccess').d('未刷新')}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get(`hzero.common.view.description`).d('描述')}
                    >
                      {getFieldDecorator('remark', {
                        initialValue: headerInformation.remark,
                      })(<Input />)}
                    </Form.Item>
                  </Col>
                </Row>
                <Row {...SEARCH_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get(`hadm.hystrix.model.hystrix.serviceName`).d('服务')}
                    >
                      {getFieldDecorator('serviceName', {
                        initialValue: headerInformation.serviceName,
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl.get(`hadm.hystrix.model.hystrix.serviceName`).d('服务'),
                            }),
                          },
                        ],
                      })(
                        <Lov
                          textValue={headerInformation.serviceName}
                          onChange={this.onHandleSelect}
                          code="HADM.SERVICE"
                          lovOptions={{
                            displayField: 'serviceCode',
                            valueField: 'serviceCode',
                          }}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl
                        .get(`hadm.hystrix.model.hystrix.serviceConfLabel`)
                        .d('服务配置标签')}
                    >
                      {getFieldDecorator('serviceConfLabel', {
                        initialValue: headerInformation.serviceConfLabel,
                      })(
                        <Lov
                          disabled={!serviceName}
                          textValue={headerInformation.serviceConfLabel}
                          code="HADM.SERVICE_CONFIG"
                          onChange={this.onHandleSelectServiceConfLabel}
                          queryParams={
                            serviceName
                              ? { serviceName }
                              : { serviceName: headerInformation.serviceName }
                          }
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl
                        .get(`hadm.hystrix.model.hystrix.serviceConfProfile`)
                        .d('服务配置Profile')}
                    >
                      {getFieldDecorator('serviceConfProfile', {
                        initialValue: headerInformation.serviceConfProfile,
                      })(<Input />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hzero.common.status.enable').d('启用')}
                    >
                      {form.getFieldDecorator('enabledFlag', {
                        initialValue: headerInformation.enabledFlag,
                      })(<Switch />)}
                    </Form.Item>
                  </Col>
                </Row>
              </Form>
            </Card>
          </Spin>
          <Card
            key="event-rule"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            title={<h3>{intl.get(`hadm.hystrix.view.title.hystrixRow`).d('参数行')}</h3>}
          >
            <div className="table-list-search">
              <DetailFilter {...detailFilterProps} />
            </div>
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <div className="table-list-operator">
                <Button type="primary" onClick={this.showModal}>
                  {intl.get('hzero.common.button.create').d('新建')}
                </Button>
                <Button
                  loading={deleteLoading}
                  onClick={this.handleDeleteDetails}
                  style={{ marginLeft: 8 }}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </Button>
              </div>
            </div>
            <Table
              bordered
              rowSelection={rowSelection}
              columns={columns}
              scroll={{ x: tableScrollWidth(columns) }}
              loading={fetchDetailListLoading}
              dataSource={detailList}
              rowKey="confLineId"
              pagination={detailPagination}
              onChange={this.onHandleStandardTableChange}
            />
          </Card>
          <DetailDrawer {...detailDrawerProps} />
        </Content>
      </>
    );
  }
}
