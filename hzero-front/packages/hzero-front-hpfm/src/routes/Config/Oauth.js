/**
 * Oauth - 系统配置
 * @date: 2019-11-1
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2019, Hand
 */
import React, { Component } from 'react';
import { Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import Upload from 'components/Upload/UploadButton';

import intl from 'utils/intl';
import { BKT_PUBLIC } from 'utils/config';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

@Form.create({ fieldNameProp: null })
export default class Oauth extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const {
      languageList,
      form: { getFieldDecorator },
      config: { data = [] },
    } = this.props;
    let logoFileList = [];
    if (data.length > 0) {
      data.forEach(item => {
        switch (item.configCode) {
          case 'HOTH.LOGO_URL':
            logoFileList = [
              {
                uid: '-1',
                name: item.fileName,
                status: 'done',
                url: item.configValue,
              },
            ];
            break;
          default:
            break;
        }
      });
    }
    const loginTitle = this.findConfigField('HOTH.TITLE', data);
    const copyright = this.findConfigField('HOTH.COPYRIGHT', data);
    const loginLogo = this.findConfigField('HOTH.LOGO_URL', data);
    const languageFlag = this.findConfigField('HOTH.SHOW_LANGUAGE', data);
    const language = this.findConfigField('HOTH.DEFAULT_LANGUAGE', data);

    return (
      <Form>
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.loginTitle').d('登录页面标题')}
              {...formLayout}
            >
              {getFieldDecorator('loginTitle', {
                initialValue: loginTitle,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.config.model.config.loginTitle').d('登录页面标题'),
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.copyright').d('版权信息')}
              {...formLayout}
            >
              {getFieldDecorator('copyright', {
                initialValue: copyright,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.config.model.config.copyright').d('版权信息'),
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.languageFlag').d('是否展示多语言')}
              {...formLayout}
            >
              {getFieldDecorator('languageFlag', {
                initialValue: languageFlag === '1' ? 1 : 0,
              })(<Switch />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.logo').d('LOGO')}
              extra={intl
                .get('hzero.common.upload.support', {
                  type: '*.png;*.jpeg',
                })
                .d('上传格式：*.png;*.jpeg')}
              {...formLayout}
            >
              <Upload
                accept=".jpeg,.png"
                fileType="image/jpeg,image/png"
                single
                bucketName={BKT_PUBLIC}
                bucketDirectory="hpfm05"
                onUploadSuccess={this.onUploadSuccess}
                fileList={logoFileList}
                onRemoveSuccess={this.onCancelSuccess}
              />
            </FormItem>
            <FormItem wrapperCol={{ span: 15, offset: 7 }}>
              {getFieldDecorator('loginLogo', {
                initialValue: loginLogo,
              })(<div />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.language').d('默认语言')}
              {...formLayout}
            >
              {getFieldDecorator('language', {
                initialValue: language,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.config.model.config.language').d('默认语言'),
                    }),
                  },
                ],
              })(
                <Select>
                  {languageList.map(item => {
                    return (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    );
                  })}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * 从配置列表查找配置项
   * @param {Number|String} field 查询配置字段的 ID 或 Code
   * @param {Array} data 获取到的原配置数组
   */
  @Bind()
  findConfigField(field, data) {
    if (data.length > 0) {
      const dataFilter = data.find(item => {
        return item.configCode === field;
      });
      return dataFilter ? dataFilter.configValue : null;
    }
  }

  // 上传图片成功
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        loginLogo: file.response,
      });
    }
  }

  // 删除图片成功
  @Bind()
  onCancelSuccess() {
    const { form } = this.props;
    form.setFieldsValue({
      loginLogo: '',
    });
  }
}
