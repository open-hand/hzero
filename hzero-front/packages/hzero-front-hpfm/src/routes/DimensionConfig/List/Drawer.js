/**
 * DimensionConfig - 数据维度配置-新建侧滑
 * @date: 2019-7-17
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';
import SideBar from 'components/Modal/SideBar';

import { isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

/**
 * 新建数据维度
 * @extends {Component} - React.Component
 * @reactProps {Boolean} confirmLoading - 保存加载标志
 * @reactProps {Boolean} modalVisible - 是否显示
 * @reactProps {Function} onSave - 保存表单
 * @reactProps {Function} onCancel - 关闭侧滑
 * @reactProps {Function} onParentLovChange - 父级维度LOV切换
 * @reactProps {Function} onRef - 绑定组件
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class CreateForm extends React.Component {
  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) onRef(this);
  }

  resetForm() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  onOk() {
    const { form, onSave } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      onSave({ ...fieldsValue, lovTypeCode: 'IDP' });
    });
  }

  renderForm() {
    const { form, onParentLovChange = (e) => e } = this.props;
    return (
      <Form>
        <Form.Item
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          label={intl.get('hpfm.dimensionConfig.model.header.dimensionCode').d('维度代码')}
        >
          {form.getFieldDecorator('lovCode', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.dimensionConfig.model.header.dimensionCode').d('维度代码'),
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
          label={intl.get('hpfm.dimensionConfig.model.header.dimensionName').d('维度名称')}
        >
          {form.getFieldDecorator('lovName', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.dimensionConfig.model.header.dimensionName').d('维度名称'),
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
              label={intl.get('hpfm.dimensionConfig.model.header.dimensionName').d('维度名称')}
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
          wrapperCol={{ span: 16 }}
          label={intl.get('hpfm.dimension.model.header.parentDimension').d('父级维度')}
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
        <Form.Item
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          label={intl.get('hpfm.valueList.model.header.description').d('描述')}
        >
          {form.getFieldDecorator('description', {
            rules: [
              {
                max: 480,
                message: intl.get('hzero.common.validation.max', {
                  max: 480,
                }),
              },
            ],
          })(
            <TLEditor
              label={intl.get('hpfm.valueList.model.header.description').d('描述')}
              field="description"
            />
          )}
        </Form.Item>
      </Form>
    );
  }

  render() {
    const { modalVisible, onCancel, confirmLoading = false, title, ...otherProps } = this.props;
    return (
      <SideBar
        title={intl.get('hpfm.dimension.view.title.create').d('新建数据维度')}
        width={520}
        visible={modalVisible}
        onCancel={onCancel}
        onOk={this.onOk}
        confirmLoading={confirmLoading}
        {...otherProps}
      >
        {this.renderForm()}
      </SideBar>
    );
  }
}
