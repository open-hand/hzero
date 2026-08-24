/**
 * FileUploadModal
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-09
 * @copyright 2019-07-09 © HAND
 */

import React, { Component } from 'react';
import { Button, Col, Form, Input, Modal, Radio, Row, Switch as HzeroSwitch } from 'hzero-ui';
import { isNil } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import Upload from 'components/Upload';
import FileChunkUploader from 'components/FileChunkUploader';
import Lov from 'components/Lov';

import { HZERO_HFLE } from 'utils/config';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { filterNullValueObject, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import notification from 'utils/notification';

@Form.create({ fieldNameProp: null })
export default class FileUploadModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      // 上传组件的key
      uploadKey: uuid(),
    };
  }

  /**
   * 在上传文件模态框 打开后 校验数据 是否正确
   */
  @Bind()
  handleAfterUploadOpen() {
    const { form } = this.props;
    form.validateFields(err => {
      if (err) {
        const requestError = [];
        Object.keys(err).forEach(erFieldCode => {
          requestError.push(...(err[erFieldCode].errors || []).map(er => er.message));
        });
        notification.warning({
          message: <pre>{`${requestError.map(r => `  ${r}`).join('\n')}`}</pre>,
        });
      }
    });
  }

  /**
   * 上传组件的模态框关闭后 清空 fileList
   */
  @Bind()
  handleAfterUploadClose() {
    this.setState({
      uploadKey: uuid(),
    });
  }

  /**
   * 传递给后端的数据
   */
  @Bind()
  uploadData() {
    const { form } = this.props;
    const fieldsValue = form.getFieldsValue();
    const { type, isDirectory, bucketName, ...uploadData } = fieldsValue;
    return filterNullValueObject(uploadData);
  }

  /**
   * 文件名字段
   */
  renderFileName() {
    const { form } = this.props;
    return (
      <Col>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hfile.fileAggregate.model.fileAggregate.fileName').d('文件名')}
        >
          {form.getFieldDecorator('fileName')(<Input />)}
        </Form.Item>
      </Col>
    );
  }

  /**
   * 对象存储上传
   * 租户 和 桶名 为空时 不能上传文件
   */
  renderObsUpload() {
    const { form, isTenant, organizationId } = this.props;
    const { uploadKey } = this.state;
    const isDirectory = form.getFieldValue('isDirectory');
    const bucketName = form.getFieldValue('bucketName');
    let currentTenantId = organizationId;
    if (!isTenant) {
      currentTenantId = form.getFieldValue('tenantId');
    }
    const uploadDisabled = !bucketName || (!isTenant && isNil(currentTenantId));
    return (
      <Col>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hfile.fileAggregate.view.button.upload').d('文件上传')}
        >
          <Upload
            directory={isDirectory}
            disabled={uploadDisabled}
            bucketName={bucketName}
            organizationId={currentTenantId}
            action={`${HZERO_HFLE}/v1/${currentTenantId}/files/multipart`}
            uploadData={this.uploadData}
            afterOpenUploadModal={this.handleAfterUploadOpen}
            onCloseUploadModal={this.handleAfterUploadClose}
            key={uploadKey}
          />
        </Form.Item>
      </Col>
    );
  }

  /**
   * 服务器上传
   * 租户 和 服务器上传配置编码 为空时 不能上传文件
   */
  renderEcsUpload() {
    const { form, isTenant, organizationId } = this.props;
    const { uploadKey } = this.state;
    const configCode = form.getFieldValue('configCode');
    const path = form.getFieldValue('path');
    let currentTenantId = organizationId;
    if (!isTenant) {
      currentTenantId = form.getFieldValue('tenantId');
    }
    const uploadDisabled = !configCode || (!isTenant && isNil(currentTenantId));
    return (
      <Col>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hfile.fileAggregate.view.button.upload').d('文件上传')}
        >
          <FileChunkUploader
            key={uploadKey}
            type="server"
            componentType="hzero"
            modalProps={{
              width: '1000px',
            }}
            organizationId={currentTenantId}
            configCode={configCode}
            path={path}
            disabled={uploadDisabled}
            title={intl.get('hfile.fileAggregate.view.button.upload').d('文件上传')}
          />
        </Form.Item>
      </Col>
    );
  }

  /**
   * 对象存储上传 其他表单字段
   */
  renderObsUploadForm() {
    const { form } = this.props;
    return (
      <>
        <Col>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hfile.fileAggregate.model.fileAggregate.bucketName').d('分组')}
          >
            {form.getFieldDecorator('bucketName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hfile.fileAggregate.model.fileAggregate.bucketName').d('分组'),
                  }),
                },
                {
                  pattern: /^[a-z0-9-]*$/g,
                  message: intl
                    .get('hfile.fileAggregate.view.validation.bucketName')
                    .d('只能由小写字母、数字，"-"组成'),
                },
              ],
            })(<Input trim inputChinese={false} typeCase="lower" />)}
          </Form.Item>
        </Col>
        <Col>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hfile.fileAggregate.model.fileAggregate.directory').d('上传目录')}
          >
            {form.getFieldDecorator('directory')(<Input />)}
          </Form.Item>
        </Col>
        <Col>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl
              .get('hfile.fileAggregate.model.fileAggregate.storageCode')
              .d('存储配置编码')}
          >
            {form.getFieldDecorator('storageCode')(<Input />)}
          </Form.Item>
        </Col>
        {this.renderFileName()}
        {this.renderObsUpload()}
      </>
    );
  }

  /**
   * 服务器上传 其他表单字段
   */
  renderEcsUploadForm() {
    const { form } = this.props;
    const lovCode = isTenantRoleLevel() ? 'HFLE.SERVER_CONFIG.ORG' : 'HFLE.SERVER_CONFIG';
    return (
      <>
        <Col>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hfile.fileAggregate.model.fileAggregate.configCode').d('上传配置编码')}
          >
            {form.getFieldDecorator('configCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hfile.fileAggregate.model.fileAggregate.configCode')
                      .d('上传配置编码'),
                  }),
                },
              ],
            })(
              <Lov
                code={lovCode}
                disabled={
                  isTenantRoleLevel() ? false : form.getFieldValue('tenantId') === undefined
                }
                textField="configCode"
                textValue="configCode"
                queryParams={{
                  tenantId: isTenantRoleLevel()
                    ? getCurrentOrganizationId()
                    : form.getFieldValue('tenantId'),
                }}
              />
            )}
          </Form.Item>
        </Col>
        <Col>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hfile.fileAggregate.model.fileAggregate.path').d('上传目录')}
          >
            {form.getFieldDecorator('path')(<Input />)}
          </Form.Item>
        </Col>
        {this.renderEcsUpload()}
      </>
    );
  }

  render() {
    const { visible = false, isTenant = false, onCancel, form } = this.props;
    const currentType = form.getFieldValue('type') || 'obs';
    return (
      <Modal
        destroyOnClose
        visible={visible}
        title={intl.get('hfile.fileAggregate.view.button.upload').d('文件上传')}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={onCancel}
        footer={[
          <Button type="primary" onClick={onCancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>,
        ]}
      >
        <Form>
          <Row>
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hfile.fileAggregate.model.fileAggregate.type').d('类型')}
              >
                {form.getFieldDecorator('type', {
                  initialValue: 'obs',
                })(
                  <Radio.Group>
                    <Radio value="obs">
                      {intl.get('hfile.fileAggregate.model.fileAggregate.type.obs').d('对象存储')}
                    </Radio>
                    <Radio value="ecs">
                      {intl.get('hfile.fileAggregate.model.fileAggregate.type.ecs').d('服务器存储')}
                    </Radio>
                  </Radio.Group>
                )}
              </Form.Item>
            </Col>
            {currentType === 'obs' && (
              <Col>
                <Form.Item
                  label={intl
                    .get('hfile.fileAggregate.model.fileAggregate.isDirectory')
                    .d('文件夹上传')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('isDirectory', {
                    initialValue: false,
                    valuePropName: 'checked',
                  })(<HzeroSwitch />)}
                </Form.Item>
              </Col>
            )}
            {!isTenant && (
              <Col>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('entity.tenant.tag').d('租户')}
                >
                  {form.getFieldDecorator('tenantId', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('entity.tenant.tag').d('租户'),
                        }),
                      },
                    ],
                  })(<Lov code="HPFM.TENANT" />)}
                </Form.Item>
              </Col>
            )}
            {currentType === 'obs' ? this.renderObsUploadForm() : this.renderEcsUploadForm()}
          </Row>
        </Form>
      </Modal>
    );
  }
}
