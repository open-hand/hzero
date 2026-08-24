/**
 * Detail 服务器上传配置明细页-抽屉
 * @date: 2019-7-4
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Form, Modal, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import Lov from 'components/Lov';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      record: {},
    };
  }

  @Bind()
  onHandleSelectService(rowKey, record) {
    const { form, sourceType } = this.props;
    form.setFieldsValue({
      sourceName: sourceType === 'C' ? record.clusterName : record.serverName,
    });
    this.setState({ record: sourceType === 'C' ? record.clusterCode : record.serverCode });
  }

  @Bind()
  handleOk() {
    const { form, onOk, iniData } = this.props;
    const { record } = this.state;

    form.validateFields((err, fieldsValue) => {
      if (!err) {
        if (JSON.stringify(record) === '{}') {
          const fieldValues = {
            ...iniData,
            ...fieldsValue,
            sourceCode: iniData.sourceCode,
            sourceId: iniData.sourceId,
          };
          onOk(fieldValues);
        } else {
          const fieldValues = {
            ...iniData,
            ...fieldsValue,
            sourceCode: record,
          };
          onOk(fieldValues);
        }
        this.setState({ record: {} });
      }
    });
  }

  render() {
    const {
      modalVisible,
      title,
      loading,
      onCancel,
      form,
      sourceType,
      iniData,
      editFlag,
    } = this.props;
    const { enabledFlag, sourceCode, sourceName, rootDir } = iniData;
    const { getFieldDecorator } = form;

    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Form>
          <Row>
            <Col>
              <FormItem
                label={intl
                  .get('hfile.serverUpload.model.serverUpload.sourceCode')
                  .d('服务器/集群编码')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('sourceId', {
                  initialValue: sourceCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hfile.serverUpload.model.serverUpload.sourceCode')
                          .d('服务器/集群编码'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    allowClear
                    textValue={sourceCode}
                    code={sourceType === 'C' ? 'HPFM.SERVER_CLUSTER' : 'HPFM.SERVER'}
                    onChange={this.onHandleSelectService}
                    disabled={editFlag}
                  />
                )}
              </FormItem>
              <FormItem
                label={intl
                  .get('hfile.serverUpload.model.serverUpload.sourceName')
                  .d('服务器/集群名称')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('sourceName', {
                  initialValue: sourceName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hfile.serverUpload.model.serverUpload.sourceName')
                          .d('服务器/集群名称'),
                      }),
                    },
                  ],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col>
              <FormItem
                label={intl.get('hfile.serverUpload.model.serverUpload.rootDir').d('根目录')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('rootDir', {
                  initialValue: rootDir,
                })(<Input trim />)}
              </FormItem>
            </Col>
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hzero.common.status.enable').d('启用')}
              >
                {form.getFieldDecorator('enabledFlag', {
                  initialValue: enabledFlag === 0 ? enabledFlag : 1,
                })(<Switch />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}
