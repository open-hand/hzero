/**
 * ViewModal
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-30
 * @copyright 2019-05-30 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Button, Col, Form, Modal, Row, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { EDIT_FORM_CLASSNAME, EDIT_FORM_ROW_LAYOUT, FORM_COL_2_LAYOUT } from 'utils/constants';
import { tableScrollWidth } from 'utils/utils';

import A from '@/components/ProxyComponent/A';

const rowKey = (record, index) => index;

function computeResult(record) {
  const ret = {
    extractionResult: [],
    finalResult: [],
  };
  if (record) {
    //
    if (record.result1) {
      ret.extractionResult.push(...record.result1);
    }
    if (record.result2) {
      ret.finalResult.push(...record.result2);
    }
  }
  return ret;
}

export default class EditFormModal extends Component {
  static propTypes = {
    languageMessage: PropTypes.object.isRequired,
    onClose: PropTypes.func.isRequired,
  };

  constructor(props) {
    super(props);
    const { record } = props;
    const { extractionResult = [], finalResult = [] } = computeResult(record);
    this.state = {
      prevRecord: record,
      extractionResult,
      finalResult,
    };
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { visible, record } = nextProps;
    const { prevRecord } = prevState;
    if (visible && record !== prevRecord) {
      return computeResult(record);
    }
    return null;
  }

  // Modal
  @Bind()
  handleModalClose() {
    const { onClose } = this.props;
    onClose();
  }

  // Table
  getColumns() {
    const { languageMessage } = this.props;
    return [
      {
        dataIndex: 'tagType',
        title: languageMessage.model.textExtractionLog.tagType,
        width: 150,
      },
      {
        dataIndex: 'tagValue',
        title: languageMessage.model.textExtractionLog.tagValue,
      },
    ];
  }

  @Bind()
  handleViewOriData(result) {
    const { languageMessage } = this.props;
    Modal.info({
      title: languageMessage.view.title.viewOriData,
      content: <pre>{JSON.stringify(result, 0, 2)}</pre>,
      maskClosable: true,
      width: 520,
    });
  }

  @Bind()
  renderExtractionTitle() {
    const { languageMessage, record = {} } = this.props;
    return (
      <h4>
        {languageMessage.view.title.extractionResult}
        <A onClick={this.handleViewOriData} record={record.result1}>
          {languageMessage.view.btn.viewOriData}
        </A>
      </h4>
    );
  }

  @Bind()
  renderFinalTitle() {
    const { languageMessage, record = {} } = this.props;
    return (
      <h4>
        {languageMessage.view.title.finalResult}
        <A onClick={this.handleViewOriData} record={record.result2}>
          {languageMessage.view.btn.viewOriData}
        </A>
      </h4>
    );
  }

  render() {
    const { visible = false, languageMessage } = this.props;
    const { extractionResult = [], finalResult = [] } = this.state;
    const columns = this.getColumns();
    return (
      <Modal
        destroyOnClose
        width={1000}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        title={languageMessage.view.title.view}
        onCancel={this.handleModalClose}
        maskClosable
        footer={<Button onClick={this.handleModalClose}>{languageMessage.common.btn.close}</Button>}
      >
        <Form className={EDIT_FORM_CLASSNAME}>
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_2_LAYOUT}>
              <Table
                bordered
                pagination={false}
                rowKey={rowKey}
                title={this.renderExtractionTitle}
                dataSource={extractionResult}
                columns={columns}
                scroll={{ x: tableScrollWidth(columns) }}
              />
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <Table
                bordered
                pagination={false}
                rowKey={rowKey}
                title={this.renderFinalTitle}
                dataSource={finalResult}
                columns={columns}
                scroll={{ x: tableScrollWidth(columns) }}
              />
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}
