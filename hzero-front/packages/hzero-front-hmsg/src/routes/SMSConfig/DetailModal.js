import React, { PureComponent } from 'react';
import { Modal, Form, Input, Select } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

const FormItem = Form.Item;
const { Option } = Select;
/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

/**
 * 新建或编辑模态框数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onAdd - 添加确定的回调函数
 * @reactProps {Function} onEdit - 编辑确定的回调函数
 * @reactProps {Object} serverTypeList - 服务类型
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {String} anchor - 模态框弹出方向
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class DetailModal extends PureComponent {
  // 点击确认回调
  @Bind()
  onOk() {
    const { form, onAdd, isCreate, isCopy, tableRecord, onEdit } = this.props;
    const { _token, serverId, objectVersionNumber } = tableRecord;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        if (isCreate || isCopy) {
          onAdd(values);
        } else {
          onEdit({ _token, serverId, objectVersionNumber, ...values });
        }
      }
    });
  }

  render() {
    const {
      modalVisible,
      onCancel = () => {},
      anchor,
      serverTypeList,
      tableRecord,
      isCreate,
      saving,
      tenantRoleLevel,
      title,
      isCopy,
      filterStrategyList = [],
    } = this.props;
    const { getFieldDecorator = () => {} } = this.props.form;
    return (
      <Modal
        destroyOnClose
        title={isCopy ? intl.get('hzero.common.button.copy').d('复制') : title}
        width={520}
        onCancel={onCancel}
        onOk={this.onOk}
        visible={modalVisible}
        confirmLoading={saving}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
      >
        <Form>
          {!tenantRoleLevel && (
            <FormItem
              label={intl.get('hmsg.smsConfig.model.smsConfig.tenant').d('租户')}
              {...formLayout}
            >
              {getFieldDecorator('tenantId', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hmsg.smsConfig.model.smsConfig.tenant').d('租户'),
                    }),
                  },
                ],
                initialValue: tableRecord ? tableRecord.tenantId : '',
              })(
                <Lov code="HPFM.TENANT" textValue={tableRecord.tenantName} disabled={!isCreate} />
              )}
            </FormItem>
          )}
          <FormItem label={intl.get('hmsg.common.view.accountCode').d('账户代码')} {...formLayout}>
            {getFieldDecorator('serverCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hmsg.common.view.accountCode').d('账户代码'),
                  }),
                },
                {
                  pattern: CODE_UPPER,
                  message: intl
                    .get('hzero.common.validation.codeUpper')
                    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
              ],
              initialValue: tableRecord ? tableRecord.serverCode : '',
            })(<Input trim typeCase="upper" inputChinese={false} disabled={!isCreate} />)}
          </FormItem>
          <FormItem label={intl.get('hmsg.common.view.accountName').d('账户名称')} {...formLayout}>
            {getFieldDecorator('serverName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hmsg.common.view.accountName').d('账户名称'),
                  }),
                },
              ],
              initialValue: tableRecord.serverName ? tableRecord.serverName : '',
            })(
              <TLEditor
                label={intl.get('hmsg.common.view.accountName').d('账户名称')}
                field="serverName"
                token={tableRecord ? tableRecord._token : null}
              />
            )}
          </FormItem>
          <FormItem
            label={intl.get('hmsg.smsConfig.model.smsConfig.signName').d('短信签名')}
            {...formLayout}
          >
            {getFieldDecorator('signName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hmsg.smsConfig.model.smsConfig.signName').d('短信签名'),
                  }),
                },
              ],
              initialValue: isCopy ? '' : tableRecord.signName,
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hmsg.smsConfig.model.smsConfig.serverTypeCode').d('服务类型')}
            {...formLayout}
          >
            {getFieldDecorator('serverTypeCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hmsg.smsConfig.model.smsConfig.serverTypeCode').d('服务类型'),
                  }),
                },
              ],
              initialValue: tableRecord.serverTypeCode ? tableRecord.serverTypeCode : '',
            })(
              <Select allowClear>
                {serverTypeList &&
                  serverTypeList.map((item) => (
                    <Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
              </Select>
            )}
          </FormItem>
          <FormItem
            label={intl.get('hmsg.smsConfig.model.smsConfig.EndPoint').d('EndPoint')}
            {...formLayout}
          >
            {getFieldDecorator('endPoint', {
              rules: [
                {
                  required: false,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hmsg.smsConfig.model.smsConfig.EndPoint').d('EndPoint'),
                  }),
                },
              ],
              initialValue: isCopy ? '' : tableRecord.endPoint,
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hmsg.smsConfig.model.smsConfig.AccessKey').d('AccessKey')}
            {...formLayout}
          >
            {getFieldDecorator('accessKey', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hmsg.smsConfig.model.smsConfig.AccessKey').d('AccessKey'),
                  }),
                },
              ],
              initialValue: isCopy ? '' : tableRecord.accessKey,
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hmsg.smsConfig.model.smsConfig.accessKeySecret').d('AccessKeySecret')}
            {...formLayout}
          >
            {getFieldDecorator('accessKeySecret', {
              rules: [
                {
                  required: false,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hmsg.smsConfig.model.smsConfig.accessKeySecret')
                      .d('AccessKeySecret'),
                  }),
                },
                {
                  max: 110,
                  message: intl.get('hzero.common.validation.max', {
                    max: 110,
                  }),
                },
              ],
              initialValue: tableRecord.accessKeySecret ? tableRecord.accessKeySecret : '',
            })(
              <Input
                type="password"
                placeholder={
                  !isCreate ? intl.get('hzero.common.validation.notChange').d('未更改') : ''
                }
              />
            )}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl.get('hmsg.email.model.email.filterStrategy').d('安全策略')}
          >
            {getFieldDecorator('filterStrategy', {
              initialValue: tableRecord.filterStrategy,
            })(
              <Select allowClear>
                {filterStrategyList.map((item) => (
                  <Select.Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </FormItem>
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
