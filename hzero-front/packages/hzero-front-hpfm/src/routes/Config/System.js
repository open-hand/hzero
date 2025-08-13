/**
 * System - 系统配置
 * @date: 2019-11-1
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2019, Hand
 */
import React, { Component } from 'react';
import { Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Upload from 'components/Upload/UploadButton';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import { BKT_PUBLIC, MULTIPLE_SKIN_ENABLE } from 'utils/config';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

@Form.create({ fieldNameProp: null })
export default class System extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const {
      form: { getFieldDecorator },
      languageList = [],
      config: {
        data = [],
        lov: {
          menuLayout: lovMenuLayout = [],
          menuLayoutTheme: lovMenuLayoutTheme = [],
          roleMergeFlag: lovRoleMergeFlag = [],
          roleMergeFlag: watermarkFlag = [],
        },
      },
    } = this.props;
    let iconFileList = [];
    let faviconFileList = [];
    if (data.length > 0) {
      data.forEach((item) => {
        switch (item.configCode) {
          case 'LOGO':
            iconFileList = [
              {
                uid: '-1',
                name: item.fileName,
                status: 'done',
                url: item.configValue,
              },
            ];
            break;
          case 'FAVICON':
            faviconFileList = [
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
    const title = this.findConfigField('TITLE', data);
    const logo = this.findConfigField('LOGO', data);
    const favicon = this.findConfigField('FAVICON', data);
    const menuLayout = this.findConfigField('MENU_LAYOUT', data);
    const menuLayoutTheme = this.findConfigField('MENU_LAYOUT_THEME', data);
    const roleMergeFlag = this.findConfigField('ROLE_MERGE', data);
    const titleData = this.findConfigData('TITLE', data);
    const defaultLanguage = this.findConfigData('TENANT_DEFAULT_LANGUAGE', data);
    const watermark = this.findConfigField('WATERMARK', data);

    let isUed = false;
    try {
      isUed = MULTIPLE_SKIN_ENABLE ? JSON.parse(MULTIPLE_SKIN_ENABLE) : false;
    } catch (e) {
      isUed = false;
    }
    return (
      <Form>
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.title').d('系统标题')}
              {...formLayout}
            >
              {getFieldDecorator('title', {
                initialValue: title,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.config.model.config.title').d('系统标题'),
                    }),
                  },
                  {
                    max: 80,
                    message: intl.get('hzero.common.validation.max', { max: 80 }),
                  },
                ],
              })(
                <TLEditor
                  label={intl.get('hpfm.config.model.config.title').d('系统标题')}
                  field="configValue"
                  token={titleData && titleData._token}
                />
              )}
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
                fileList={iconFileList}
                onRemoveSuccess={this.onCancelSuccess}
              />
            </FormItem>
            <FormItem wrapperCol={{ span: 15, offset: 7 }}>
              {getFieldDecorator('logo', {
                initialValue: logo,
              })(<div />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.favicon').d('favicon')}
              extra={intl
                .get('hzero.common.upload.support', {
                  type: '*.png;*.ico',
                })
                .d('上传格式：*.png;*.ico')}
              {...formLayout}
            >
              <Upload
                single
                accept=".png,.ico"
                fileType="image/png,image/vnd.microsoft.icon,image/x-icon	"
                bucketName={BKT_PUBLIC}
                bucketDirectory="hpfm05"
                onUploadSuccess={this.handleFaviconUploadSuccess}
                fileList={faviconFileList}
                onRemoveSuccess={this.handleCancelFaviconUploadSuccess}
              />
            </FormItem>
            <FormItem wrapperCol={{ span: 15, offset: 7 }}>
              {getFieldDecorator('favicon', {
                initialValue: favicon,
              })(<div />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.menuLayout').d('菜单布局')}
              {...formLayout}
            >
              {getFieldDecorator('menuLayout', {
                initialValue: menuLayout,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.config.model.config.menuLayout').d('菜单布局'),
                    }),
                  },
                ],
              })(
                <Select>
                  {lovMenuLayout.map((item) => {
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
        {isUed && (
          <Row>
            <Col span={16}>
              <FormItem
                label={intl.get('hpfm.config.model.config.menuLayoutTheme').d('菜单布局主题')}
                {...formLayout}
              >
                {getFieldDecorator('menuLayoutTheme', {
                  initialValue: menuLayoutTheme,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.config.model.config.menuLayoutTheme')
                          .d('菜单布局主题'),
                      }),
                    },
                  ],
                })(
                  <Select>
                    {lovMenuLayoutTheme.map((item) => {
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
        )}
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.roleMergeFlag').d('角色合并')}
              {...formLayout}
            >
              {getFieldDecorator('roleMergeFlag', {
                initialValue: roleMergeFlag,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.config.model.config.roleMergeFlag').d('角色合并'),
                    }),
                  },
                ],
              })(
                <Select>
                  {lovRoleMergeFlag.map((item) => {
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
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.defaultLanguage').d('默认语言配置')}
              {...formLayout}
            >
              {getFieldDecorator('defaultLanguage', {
                initialValue: defaultLanguage?.configValue || '',
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.config.model.config.defaultLanguage').d('默认语言配置'),
                    }),
                  },
                ],
              })(
                <Select>
                  {languageList.map((item) => {
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
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.watermark').d('水印')}
              {...formLayout}
            >
              {getFieldDecorator('watermark', {
                initialValue: watermark,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.config.model.config.watermark').d('水印'),
                    }),
                  },
                ],
              })(
                <Select>
                  {watermarkFlag.map((item) => {
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
      const dataFilter = data.find((item) => {
        return item.configCode === field;
      });
      return dataFilter ? dataFilter.configValue : null;
    }
  }

  /**
   * 从配置列表查找配置项
   * @param {Number|String} field 查询配置字段的 ID 或 Code
   * @param {Array} data 获取到的原配置数组
   */
  @Bind()
  findConfigData(field, data) {
    if (data.length > 0) {
      const dataFilter = data.find((item) => {
        return item.configCode === field;
      });
      return dataFilter ? dataFilter : null;
    }
  }

  // 上传图片成功
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        logo: file.response,
      });
    }
  }

  // 删除图片成功
  @Bind()
  onCancelSuccess() {
    const { form } = this.props;
    form.setFieldsValue({
      logo: '',
    });
  }

  /**
   * 上传 favicon 成功
   */
  @Bind()
  handleFaviconUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        favicon: file.response,
      });
    }
  }

  /**
   * 删除 favicon 成功
   */
  @Bind()
  handleCancelFaviconUploadSuccess() {
    const { form } = this.props;
    form.setFieldsValue({
      favicon: '',
    });
  }
}
