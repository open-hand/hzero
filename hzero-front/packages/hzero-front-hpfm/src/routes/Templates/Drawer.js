/**
 * 系统管理--模板维护
 * @date 2019-6-26
 * @author: XL <liang.xiong@hand-china.com>
 */
import React, { Component } from 'react';
import { Form, Input, Modal, Select } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';
import Upload from 'components/Upload/UploadButton';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { BKT_PLATFORM } from 'utils/config';

const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

const noFormStyle = {
  display: 'none',
};

/**
 * 新建或编辑模态框数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onAdd - 添加确定的回调函数
 * @reactProps {Function} onEdit - 编辑确定的回调函数
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {String} anchor - 模态框弹出方向
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends Component {
  @Bind()
  onOk() {
    const { form, onAdd, isCreate, tableRecord, onEdit } = this.props;
    const { _token, templateId, objectVersionNumber } = tableRecord;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        if (isCreate) {
          onAdd(values);
        } else {
          onEdit({ _token, templateId, objectVersionNumber, ...values });
        }
      }
    });
  }

  // 上传图片成功
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        templateAvatar: file.response,
      });
    }
  }

  // 删除图片成功
  @Bind()
  onCancelSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        templateAvatar: '',
      });
    }
  }

  render() {
    const {
      modalVisible,
      onCancel,
      saving,
      anchor,
      tableRecord,
      isCreate,
      fileList = [],
      dataTenantLevel = [],
      isTenantRoleLevel = false,
    } = this.props;
    const { getFieldDecorator } = this.props.form;
    return (
      <Modal
        destroyOnClose
        width={520}
        title={
          isCreate
            ? intl.get('hpfm.hpfmTemplate.view.message.create').d('新建模板')
            : intl.get('hpfm.hpfmTemplate.view.message..edit').d('编辑模板')
        }
        visible={modalVisible}
        onCancel={onCancel}
        onOk={this.onOk}
        confirmLoading={saving}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
      >
        <Form>
          {!isTenantRoleLevel && (
            <FormItem label={intl.get('entity.tenant.tag').d('租户')} {...formLayout}>
              {getFieldDecorator('tenantId', {
                initialValue: tableRecord.tenantId,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.tenant.tag').d('租户'),
                    }),
                  },
                ],
              })(
                <Lov disabled={!isCreate} code="HPFM.TENANT" textValue={tableRecord.tenantName} />
              )}
            </FormItem>
          )}
          <FormItem
            label={intl.get('hpfm.hpfmTemplate.model.portalTemplate.templateCode').d('模板代码')}
            {...formLayout}
          >
            {getFieldDecorator('templateCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hpfm.hpfmTemplate.model.portalTemplate.templateCode')
                      .d('模板代码'),
                  }),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', { max: 30 }),
                },
                {
                  pattern: CODE_UPPER,
                  message: intl
                    .get('hzero.common.validation.codeUpper')
                    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
              ],
              initialValue: tableRecord ? tableRecord.templateCode : '',
            })(<Input trim typeCase="upper" inputChinese={false} disabled={!isCreate} />)}
          </FormItem>
          <FormItem
            label={intl.get('hpfm.hpfmTemplate.model.portalTemplate.templateName').d('模板名称')}
            {...formLayout}
          >
            {getFieldDecorator('templateName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hpfm.hpfmTemplate.model.portalTemplate.templateName')
                      .d('模板名称'),
                  }),
                },
                {
                  max: 255,
                  message: intl.get('hzero.common.validation.max', { max: 255 }),
                },
              ],
              initialValue: tableRecord.templateName ? tableRecord.templateName : '',
            })(
              <TLEditor
                label={intl
                  .get('hpfm.hpfmTemplate.model.portalTemplate.templateName')
                  .d('模板名称')}
                field="templateName"
                token={tableRecord ? tableRecord._token : ''}
              />
            )}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.hpfmTemplate.model.portalTemplate.templateAvatar')
              .d('模板缩略图')}
            {...formLayout}
            // eslint-disable-next-line
            required={true}
            extra={intl
              .get('hzero.common.upload.support', {
                type: '*.png;*.jpeg',
              })
              .d('上传格式：*.png;*.jpeg')}
          >
            <Upload
              accept="image/jpeg,image/png"
              single
              bucketName={BKT_PLATFORM}
              bucketDirectory="hpfm03"
              onUploadSuccess={this.onUploadSuccess}
              fileList={fileList}
              onRemove={this.onCancelSuccess}
            />
          </FormItem>
          <FormItem wrapperCol={{ span: 15, offset: 6 }}>
            {getFieldDecorator('templateAvatar', {
              rules: [
                {
                  validator: (rule, value, callback) => {
                    if (!value) {
                      callback(
                        new Error(
                          intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.hpfmTemplate.model.portalTemplate.templateAvatar')
                              .d('模板缩略图'),
                          })
                        )
                      );
                    } else {
                      callback();
                    }
                  },
                },
              ],
              initialValue: tableRecord.templateAvatar,
            })(<div />)}
          </FormItem>
          <FormItem
            label={intl.get('hpfm.hpfmTemplate.model.portalTemplate.templatePath').d('模板路径')}
            {...formLayout}
          >
            {getFieldDecorator('templatePath', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hpfm.hpfmTemplate.model.portalTemplate.templatePath')
                      .d('模板路径'),
                  }),
                },
                {
                  max: 60,
                  message: intl.get('hzero.common.validation.max', { max: 60 }),
                },
              ],
              initialValue: tableRecord.templatePath ? tableRecord.templatePath : '',
            })(<Input />)}
          </FormItem>
          {!isTenantRoleLevel && (
            <FormItem
              label={intl.get('hpfm.hpfmTemplate.model.portalTemplate.templateLevel').d('层级')}
              {...formLayout}
            >
              {getFieldDecorator('templateLevelCode', {
                initialValue: tableRecord.templateLevelCode || 'SITE',
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.hpfmTemplate.model.portalTemplate.templateLevel')
                        .d('层级'),
                    }),
                  },
                ],
              })(
                <Select>
                  {dataTenantLevel.map((item) => {
                    return (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    );
                  })}
                </Select>
              )}
            </FormItem>
          )}
          {isTenantRoleLevel && (
            <FormItem
              style={noFormStyle}
              label={intl.get('hpfm.hpfmTemplate.model.portalTemplate.templateLevel').d('层级')}
              {...formLayout}
            >
              {getFieldDecorator('templateLevelCode', {
                initialValue: tableRecord.templateLevelCode || 'SITE',
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.hpfmTemplate.model.portalTemplate.templateLevel')
                        .d('层级'),
                    }),
                  },
                ],
              })(
                <Select>
                  {dataTenantLevel.map((item) => {
                    return (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    );
                  })}
                </Select>
              )}
            </FormItem>
          )}
          <FormItem label={intl.get('hzero.common.status.enable').d('启用')} {...formLayout}>
            {getFieldDecorator('enabledFlag', {
              initialValue: isUndefined(tableRecord.enabledFlag) ? 1 : tableRecord.enabledFlag,
            })(<Switch />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
