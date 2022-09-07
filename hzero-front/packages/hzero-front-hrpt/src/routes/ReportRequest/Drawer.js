import React from 'react';
import { Form, Input, Modal, Button, Col, Spin, DatePicker, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import intl from 'utils/intl';
import { getDateTimeFormat } from 'utils/utils';

const FormItem = Form.Item;
const { TextArea } = Input;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};
const dateFormat = getDateTimeFormat();
@Form.create({ fieldNameProp: null })
export default class JobGroupForm extends React.PureComponent {
  @Bind()
  handleExport() {
    const { onExport, initData } = this.props;
    if (onExport) {
      onExport(initData);
    }
  }

  render() {
    const {
      form,
      initData,
      visible,
      onCancel,
      tenantRoleLevel,
      onOk,
      fetchRequestDetailLoading = false,
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      tenantName,
      requester,
      requestParam,
      reportCode,
      reportName,
      startDate,
      endDate,
      requestStatusMeaning,
      requestMessage,
      fileUrl,
    } = initData;
    return (
      <Modal
        destroyOnClose
        title={intl.get('hzero.common.view.title.detail').d('详情')}
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={onCancel}
        footer={[
          <Button key="detail" type="primary" onClick={onOk}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
        ]}
      >
        <Spin spinning={fetchRequestDetailLoading}>
          {!tenantRoleLevel && (
            <Form.Item label={intl.get('entity.tenant.tag').d('租户')} {...formLayout}>
              {getFieldDecorator('tenantId', {
                initialValue: tenantName,
              })(<Input disabled />)}
            </Form.Item>
          )}
          <Form.Item
            label={intl.get('hrpt.reportRequest.model.reportRequest.requester').d('请求人名称')}
            {...formLayout}
          >
            {getFieldDecorator('requester', {
              initialValue: requester,
            })(<Input disabled />)}
          </Form.Item>
          <FormItem
            label={intl.get('hrpt.reportRequest.model.reportRequest.requestParam').d('请求参数')}
            {...formLayout}
          >
            {getFieldDecorator('requestParam', {
              initialValue: requestParam,
            })(<TextArea rows={5} disabled />)}
          </FormItem>
          <Form.Item
            label={intl.get('hrpt.common.report.reportCode').d('报表代码')}
            {...formLayout}
          >
            {getFieldDecorator('reportCode', {
              initialValue: reportCode,
            })(<Input disabled />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hrpt.common.report.reportName').d('报表名称')}
            {...formLayout}
          >
            {getFieldDecorator('reportName', {
              initialValue: reportName,
            })(<Input disabled />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hrpt.reportRequest.model.reportRequest.startDate').d('开始时间')}
            {...formLayout}
          >
            {getFieldDecorator('startDate', {
              initialValue: startDate && moment(startDate, dateFormat),
            })(
              <DatePicker style={{ width: '100%' }} placeholder="" format={dateFormat} disabled />
            )}
          </Form.Item>
          <Form.Item label={intl.get('hrpt.common.view.endTime').d('结束时间')} {...formLayout}>
            {getFieldDecorator('endDate', {
              initialValue: endDate && moment(endDate, dateFormat),
            })(
              <DatePicker style={{ width: '100%' }} placeholder="" format={dateFormat} disabled />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hrpt.common.view.requestStatus').d('运行状态')}
            {...formLayout}
          >
            {getFieldDecorator('requestStatusMeaning', {
              initialValue: requestStatusMeaning,
            })(<Input disabled />)}
          </Form.Item>
          <FormItem
            label={intl.get('hrpt.reportRequest.model.reportRequest.requestMessage').d('请求消息')}
            {...formLayout}
          >
            {getFieldDecorator('requestMessage', {
              initialValue: requestMessage,
            })(<TextArea rows={5} disabled />)}
          </FormItem>
          {fileUrl && (
            <Row>
              <Col offset={6}>
                <Button
                  type="primary"
                  onClick={this.handleExport}
                  // loading={exportPending}
                >
                  {intl.get(`hrpt.common.report.export`).d('导出结果')}
                </Button>
              </Col>
            </Row>
          )}
        </Spin>
      </Modal>
    );
  }
}
