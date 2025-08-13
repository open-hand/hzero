import React from 'react';
import { Form, Input, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';
import SideBar from 'components/Modal/SideBar';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';

const { Option } = Select;
const { TextArea } = Input;

@Form.create({ fieldNameProp: null })
export default class CreateForm extends React.Component {
  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) onRef(this);
  }

  render() {
    const { modalVisible, hideModal, confirmLoading = false, title, ...otherProps } = this.props;
    return (
      <SideBar
        title={title}
        visible={modalVisible}
        onCancel={hideModal}
        onOk={this.onOk}
        confirmLoading={confirmLoading}
        {...otherProps}
      >
        {this.renderForm()}
      </SideBar>
    );
  }

  resetForm() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  onOk() {
    const { form, handleAdd } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      handleAdd(fieldsValue);
    });
  }

  renderForm() {
    const { form, lovType = [], requestMethods = [], onParentLovChange = (e) => e } = this.props;
    return (
      <Form>
        <Form.Item
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          label={intl.get('hpfm.valueList.model.header.lovCode').d('值集编码')}
        >
          {form.getFieldDecorator('lovCode', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.valueList.model.header.lovCode').d('值集编码'),
                }),
              },
              {
                pattern: CODE_UPPER,
                message: intl
                  .get('hzero.common.validation.codeUpper')
                  .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
              },
              {
                max: 60,
                message: intl.get('hzero.common.validation.max', {
                  max: 60,
                }),
              },
            ],
          })(<Input trim typeCase="upper" inputChinese={false} />)}
        </Form.Item>
        <Form.Item
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          label={intl.get('hpfm.valueList.model.header.lovName').d('值集名称')}
        >
          {form.getFieldDecorator('lovName', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.valueList.model.header.lovName').d('值集名称'),
                }),
              },
              {
                max: 240,
                message: intl.get('hzero.common.validation.max', {
                  max: 240,
                }),
              },
            ],
          })(
            <TLEditor
              label={intl.get('hpfm.valueList.model.header.lovName').d('值集名称')}
              field="lovName"
            />
          )}
        </Form.Item>
        {!isTenantRoleLevel() && (
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 8 }}
            label={intl.get('hpfm.valueList.model.header.tenantId').d('所属租户')}
          >
            {form.getFieldDecorator('tenantId')(
              <Lov
                style={{ width: 200 }}
                code="HPFM.TENANT"
                onChange={() => {
                  form.resetFields('parentLovCode');
                }}
              />
            )}
          </Form.Item>
        )}
        <Form.Item
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 8 }}
          label={intl.get('hpfm.valueList.model.header.lovTypeCode').d('值集类型')}
        >
          {form.getFieldDecorator('lovTypeCode', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.valueList.model.header.lovTypeCode').d('值集类型'),
                }),
              },
            ],
          })(
            <Select style={{ width: '100%' }}>
              {lovType.map((item) => (
                <Option value={item.value} key={item.value}>
                  {item.meaning}
                </Option>
              ))}
            </Select>
          )}
        </Form.Item>
        {form.getFieldsValue().lovTypeCode === 'URL' ||
        form.getFieldsValue().lovTypeCode === 'SQL' ? (
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.valueList.model.header.routeName').d('目标路由名')}
          >
            {form.getFieldDecorator('routeName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.valueList.model.header.routeName').d('目标路由名'),
                  }),
                },
              ],
            })(
              <Lov
                style={{ width: '100%' }}
                code={
                  isTenantRoleLevel() ? 'HADM.ROUTE.SERVICE_PATH.ORG' : 'HADM.ROUTE.SERVICE_PATH'
                }
              />
            )}
          </Form.Item>
        ) : null}
        {form.getFieldsValue().lovTypeCode === 'URL' ? (
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.valueList.model.header.customUrl').d('查询 URL')}
          >
            {form.getFieldDecorator('customUrl', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.valueList.model.header.customUrl').d('查询 URL'),
                  }),
                },
              ],
            })(<Input />)}
          </Form.Item>
        ) : null}
        {form.getFieldsValue().lovTypeCode === 'URL' ? (
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.valueList.model.header.requestMethod').d('请求方式')}
          >
            {form.getFieldDecorator('requestMethod', {
              initialValue: 'GET',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.valueList.model.header.requestMethod').d('请求方式'),
                  }),
                },
              ],
            })(
              <Select style={{ width: '100%' }}>
                {requestMethods.map((item) => (
                  <Option value={item.value} key={item.value}>
                    {item.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </Form.Item>
        ) : null}
        {form.getFieldsValue().lovTypeCode === 'SQL' ? (
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.valueList.model.header.customSql').d('查询 SQL')}
          >
            {form.getFieldDecorator('customSql', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.valueList.model.header.customSql').d('查询 SQL'),
                  }),
                },
              ],
            })(<TextArea rows={12} />)}
          </Form.Item>
        ) : null}
        {form.getFieldsValue().lovTypeCode === 'IDP' ? (
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.valueList.model.header.parentLovCode').d('父级值集')}
          >
            {form.getFieldDecorator('parentLovCode')(
              <Lov
                style={{ width: '100%' }}
                code={
                  isTenantRoleLevel() ? 'HPFM.LOV.LOV_DETAIL_CODE.ORG' : 'HPFM.LOV.LOV_DETAIL_CODE'
                }
                queryParams={{
                  lovQueryFlag: 1,
                  lovTypeCode: 'IDP',
                  // eslint-disable-next-line no-nested-ternary
                  tenantId: !isTenantRoleLevel()
                    ? form.getFieldValue('tenantId') !== undefined
                      ? form.getFieldValue('tenantId')
                      : ''
                    : '',
                }}
                onOk={onParentLovChange}
              />
            )}
          </Form.Item>
        ) : null}
        <Form.Item
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          label={intl.get('hpfm.valueList.model.header.description').d('描述')}
        >
          {form.getFieldDecorator('description')(
            <TLEditor
              label={intl.get('hpfm.valueList.model.header.description').d('描述')}
              field="description"
            />
          )}
        </Form.Item>
      </Form>
    );
  }
}
