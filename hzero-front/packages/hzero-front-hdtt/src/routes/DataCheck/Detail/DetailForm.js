/**
 * DetailForm - 数据核对详情表单
 * @date: 2019/7/29
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import { isObject } from 'lodash';
import { Form, Row, Col, Modal, Button } from 'hzero-ui';

import { EDIT_FORM_ROW_LAYOUT, FORM_COL_3_LAYOUT, EDIT_FORM_ITEM_LAYOUT } from 'utils/constants';
import { isTenantRoleLevel } from 'utils/utils';

import jsonFormat from '../../../components/JsonFormat';

const FormItem = Form.Item;

/**
 * 数据核对详情表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {!Object} dataSource - 数据源
 * @reactProps {boolean} loading - 数据加载是否完成
 * @return React.element
 */
export default class DetailForm extends PureComponent {
  state = {
    isShowMessage: false,
    message: '', // 处理信息
  };

  /**
   * 显示处理消息内容模态框
   * @param {string} text - 处理信息
   */
  @Bind()
  handleOpenMsgModal(text) {
    const obj = this.handleTransObj(text);
    const content = isObject(obj) ? jsonFormat(obj) : text;
    this.setState({
      isShowMessage: true,
      message: content,
    });
  }

  /**
   * JSON类型处理
   * @param {string} - JSON字符串
   */
  @Bind()
  handleTransObj(str) {
    let result = str;
    try {
      result = JSON.parse(str);
    } catch (err) {
      return null;
    }
    return result;
  }

  /**
   * 关闭处理消息内容模态框
   */
  @Bind()
  handleCloseMsgModal() {
    this.setState({
      isShowMessage: false,
    });
  }

  render() {
    const { isShowMessage, message } = this.state;
    const { dataSource } = this.props;
    const isTenant = isTenantRoleLevel();
    const {
      batchNum,
      chkLevelMeaning,
      sourceService,
      sourceDs,
      sourceDb,
      sourceTable,
      targetService,
      targetDs,
      targetDb,
      targetTable,
      tenantName,
      processStatusMeaning,
      processTime,
      processMsg,
      remark,
    } = dataSource;
    return (
      <>
        <Form layout="inline" className="more-fields-search-form">
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.batchNum').d('核对批次')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {batchNum}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.chkLevel').d('核对层级')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {chkLevelMeaning}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.sourceService').d('生产服务')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {sourceService}
              </FormItem>
            </Col>
          </Row>
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.sourceDs').d('生产数据源')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {sourceDs}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.sourceDb').d('生产DB')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {sourceDb}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.sourceTable').d('生产表名')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {sourceTable}
              </FormItem>
            </Col>
          </Row>
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.targetService').d('消费服务')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {targetService}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.targetDs').d('消费数据源')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {targetDs}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.targetDb').d('消费DB')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {targetDb}
              </FormItem>
            </Col>
          </Row>
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.targetTable').d('消费表名')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {targetTable}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hzero.common.status').d('状态')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {processStatusMeaning}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.remark').d('备注')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {remark}
              </FormItem>
            </Col>
          </Row>
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            {!isTenant && (
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hzero.common.model.common.tenantId').d('租户')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {tenantName}
                </FormItem>
              </Col>
            )}
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.processTime').d('处理时间')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {processTime}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.processMsg').d('处理消息')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                <a onClick={() => this.handleOpenMsgModal(processMsg)}>
                  {processMsg && processMsg.length ? `${processMsg.substring(0, 15)}...` : ''}
                </a>
              </FormItem>
            </Col>
          </Row>
        </Form>
        <Modal
          visible={isShowMessage}
          destroyOnClose
          maskClosable
          title={intl.get('hdtt.dataCheck.model.dataCheck.processMsg').d('处理消息')}
          onCancel={this.handleCloseMsgModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseMsgModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <pre style={{ maxHeight: '350px' }}>
            <code>{message}</code>
          </pre>
        </Modal>
      </>
    );
  }
}
