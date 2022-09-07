/**
 * EditModal - 编辑测试用例弹窗
 * @date: 2019/6/13
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Input, Modal, Popconfirm, Spin, Tabs, Tag } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/mode/javascript/javascript'; // javascript/json 样式
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/dracula.css';

import EditTable from 'components/EditTable';
import Upload from 'components/Upload/UploadButton';
import intl from 'utils/intl';
import { BKT_HITF } from 'utils/config';
import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

import EditModalForm from './EditModalForm';
import ParamTable from './ParamsTable';
import styles from '../index.less';

const { TabPane } = Tabs;

/**
 * 编辑测试用例弹窗
 * @extends {Component} - Component
 * @reactProps {boolean} visible - 是否显示用例详情
 * @reactProps {boolean} loading - 加载中标志
 * @reactProps {boolean} confirmLoading - 保存中标志
 * @reactProps {array} testCaseParams - 参数数据
 * @reactProps {object} testCaseDetail - 参数详情
 * @reactProps {number} documentId - 文档ID
 * @reactProps {number} interfaceId - 接口ID
 * @reactProps {object} paramsWithValues - 所有参数及其备选值
 * @reactProps {array} mimeTypes - BODY的类型值集
 * @reactProps {Function} onCancel - 关闭用例详情弹窗
 * @reactProps {Function} onDelete - 删除用例参数
 * @reactProps {Function} onEditLine - 编辑列
 * @reactProps {Function} onSave - 保存参数
 * @return React.element
 */
export default class EditModal extends Component {
  state = {
    rawValue: '',
  };

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    if (nextProps.testCaseDetail !== this.props.testCaseDetail) {
      const { testCaseDetail, testCaseParams } = nextProps;
      if (!isEmpty(testCaseParams) && testCaseDetail.reqRawFlag) {
        this.setState({
          rawValue: ((testCaseParams.BODY || [])[0] || {}).parameterValueLongtext || '',
        });
      }
    }
  }

  /**
   * 保存raw的值
   */
  @Bind()
  getRawValue() {
    const { testCaseParams } = this.props;
    let originRawData = {};
    if (testCaseParams && testCaseParams.BODY) {
      [originRawData] = testCaseParams.BODY;
    }
    return {
      ...originRawData,
      parameterValueLongtext: this.state.rawValue,
      paramType: 'BODY',
      paramValueType: 'STRING',
    };
  }

  /**
   * 保存详情
   */
  @Bind()
  handleSave() {
    const { onSave } = this.props;
    const formValues = this.formRef.getFormValue();
    onSave(formValues);
  }

  /**
   * 代码编辑器输入时
   * @param {object} editor - 代码编辑器
   * @param {*} data - 数据
   * @param {*} value - 代码编辑器当前内容
   */
  @Bind()
  handleChangeValue(editor, data, value) {
    this.setState({
      rawValue: value,
    });
  }

  // 上传成功
  @Bind()
  onUploadSuccess(file, record) {
    if (file) {
      record.$form.setFieldsValue({
        parameterValueLongtext: file.response,
      });
    }
  }

  // 删除成功
  @Bind()
  onCancelSuccess(file, record) {
    if (file) {
      record.$form.setFieldsValue({
        parameterValueLongtext: '',
      });
    }
  }

  render() {
    const {
      onCancel,
      onCreate,
      visible,
      testCaseParams,
      testCaseDetail,
      loading,
      confirmLoading,
      mimeTypes,
      usecaseTypes,
      paramsWithValues,
      onDelete,
      onEditLine,
      onCleanLine,
    } = this.props;
    const { rawValue } = this.state;
    const paramNameValues = {};
    if (paramsWithValues && paramsWithValues.request) {
      paramNameValues.HEADER = paramsWithValues.request.HEADER || [];
      paramNameValues.PATH = paramsWithValues.request.PATH || [];
      paramNameValues.GET = paramsWithValues.request.GET || [];
      paramNameValues.BODY = paramsWithValues.request.BODY || [];
    }
    let currentMimeType = 'multipart/form-data';
    let resRawType = 'text/plain';
    if (testCaseDetail.reqMimeType) {
      if (mimeTypes && mimeTypes.find((item) => item.meaning === testCaseDetail.reqMimeType)) {
        currentMimeType = testCaseDetail.reqMimeType;
      } else {
        currentMimeType = 'raw';
        resRawType = testCaseDetail.reqMimeType;
      }
    }
    const bodyColumns = [
      {
        title: intl.get('hitf.services.model.services.param').d('参数'),
        dataIndex: 'parameterName',
        width: 200,
      },
      {
        title: intl.get('hitf.services.model.services.paramValue').d('参数值'),
        dataIndex: 'parameterValueLongtext',
        render: (val, record) => {
          if (record._status === 'update') {
            const result = [
              <Form.Item>
                {record.$form.getFieldDecorator('parameterValueLongtext', {
                  initialValue: val,
                })(record.paramValueType === 'FILE' ? <div /> : <Input />)}
              </Form.Item>,
            ];
            if (record.paramValueType === 'FILE') {
              result.unshift(
                <Form.Item className={styles['hitf-testcase-file-item']}>
                  <Upload
                    single
                    listType="text"
                    bucketName={BKT_HITF}
                    bucketDirectory="hitf01"
                    onUploadSuccess={(file) => this.onUploadSuccess(file, record)}
                    fileList={
                      record.parameterValueLongtext
                        ? [
                            {
                              uid: '-1',
                              status: 'done',
                              name: record.parameterValueLongtext,
                              url: record.parameterValueLongtext,
                            },
                          ]
                        : []
                    }
                    onRemove={(file) => this.onCancelSuccess(file, record)}
                  />
                </Form.Item>
              );
            }
            return result;
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'action',
        width: 120,
        fixed: 'right',
        render: (_, record) => {
          const operators = [];
          if (record._status === 'update') {
            operators.push({
              key: 'cancel',
              ele: (
                <a onClick={() => onEditLine(record, false)}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          } else if (record.paramValueType !== 'OBJECT' && record.paramValueType !== 'ARRAY') {
            operators.push(
              {
                key: 'edit',
                ele: (
                  <a onClick={() => onEditLine(record, true)}>
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </a>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'delete',
                ele: (
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    placement="topRight"
                    onConfirm={() => onDelete(record)}
                  >
                    <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              }
            );
          }
          return operatorRender(operators, record);
        },
      },
    ];

    const style = {
      marginBottom: '15px',
    };
    const editModalFormProps = {
      testCaseDetail,
      usecaseTypes,
      wrappedComponentRef: (form) => {
        this.formRef = form;
      },
    };
    return (
      <Modal
        title={intl.get('hitf.document.view.title.edit.testcase').d('编辑测试用例')}
        visible={visible}
        destroyOnClose
        width="50%"
        onCancel={() => onCancel()}
        onOk={() => this.handleSave()}
        confirmLoading={confirmLoading}
      >
        <Spin spinning={loading}>
          <div className={styles['hitf-testcase-edit-modal']}>
            <EditModalForm {...editModalFormProps} />
            <Tabs
              defaultActiveKey="params"
              animated={false}
              className={styles['top-tabs']}
              onChange={this.changeTab}
              forceRender
            >
              <TabPane
                tab={intl.get('hitf.document.view.title.requestHeader').d('请求头部')}
                key="header"
              >
                <ParamTable
                  type="HEADER"
                  dataSource={testCaseParams.HEADER || []}
                  modelData={paramNameValues.HEADER}
                  onCreate={onCreate}
                  onCleanLine={onCleanLine}
                  onEditLine={onEditLine}
                  onDelete={onDelete}
                />
              </TabPane>
              <TabPane
                tab={intl.get('hitf.document.view.title.queryParams').d('GET/URL参数')}
                key="get"
              >
                <ParamTable
                  type="GET"
                  dataSource={testCaseParams.GET || []}
                  modelData={paramNameValues.GET}
                  onCreate={onCreate}
                  onCleanLine={onCleanLine}
                  onEditLine={onEditLine}
                  onDelete={onDelete}
                />
              </TabPane>
              <TabPane
                tab={intl.get('hitf.document.view.title.pathParams').d('路径参数')}
                key="path"
              >
                <ParamTable
                  type="PATH"
                  dataSource={testCaseParams.PATH || []}
                  modelData={paramNameValues.PATH}
                  onCreate={onCreate}
                  onCleanLine={onCleanLine}
                  onEditLine={onEditLine}
                  onDelete={onDelete}
                />
              </TabPane>
              {testCaseDetail.requestMethod !== 'GET' && (
                <TabPane
                  tab={intl.get('hitf.document.view.title.bodyParams').d('BODY参数')}
                  key="body"
                >
                  <Tag color="green">{currentMimeType}</Tag>
                  {currentMimeType === 'raw' && <Tag color="green">{resRawType}</Tag>}
                  {currentMimeType === 'application/json' && (
                    <Tag color="green">
                      {intl.get('hitf.document.view.message.structure').d('最外层结构为')}:{' '}
                      {testCaseDetail.reqRootType || 'json'}
                    </Tag>
                  )}
                  <div style={{ marginTop: '10px' }}>
                    {currentMimeType === 'raw' ? (
                      <CodeMirror
                        autoScroll
                        className={styles['hzero-codemirror']}
                        value={rawValue}
                        options={{
                          mode: 'javascript',
                          lineNumbers: true,
                        }}
                        onBeforeChange={this.handleChangeValue}
                      />
                    ) : (
                      <EditTable
                        dataSource={testCaseParams.BODY || []}
                        columns={bodyColumns}
                        defaultExpandAllRows
                        bordered
                        pagination={false}
                        style={style}
                        rowKey="interfaceUsecaseParamId"
                        scroll={{ x: tableScrollWidth(bodyColumns) }}
                      />
                    )}
                  </div>
                </TabPane>
              )}
            </Tabs>
          </div>
        </Spin>
      </Modal>
    );
  }
}
