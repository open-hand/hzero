/**
 * 企业信息 - 工商注册登记
 * @date: 2018-7-15
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Input, InputNumber, Button, Select, Cascader, Row, Col, DatePicker } from 'hzero-ui';
import moment from 'moment';
import { isUndefined, find, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getAccessToken, getCurrentOrganizationId } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import { BKT_PUBLIC } from 'utils/config';

import Checkbox from 'components/Checkbox';
import Upload from 'components/Upload/UploadButton';

import logo from 'hzero-front/lib/assets/reg-logo.png';

const { Item: FormItem } = Form;
const { Option } = Select;

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};

const submitFormLayout = {
  wrapperCol: { span: 14, offset: 6 },
};

@connect(({ company, group, loading }) => ({
  company,
  group,
  tenantId: getCurrentOrganizationId(),
  saving: loading.effects['company/createCompany'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: 'hpfm.enterprise' })
export default class LegalForm extends PureComponent {
  componentDidMount() {
    const { dispatch, onRef, tenantId } = this.props;
    if (onRef) onRef(this);
    dispatch({
      type: 'company/init',
      // 查询国家的接口 需要 enabledFlag = 1
      payload: { tenantId, enabledFlag: 1 },
    });
  }

  @Bind()
  saveAndNext() {
    const {
      data = {},
      form,
      dispatch,
      callback,
      group: { groupData },
    } = this.props;

    form.validateFields((err, fieldsValue) => {
      if (err) return;

      let payload = {
        ...data,
        ...fieldsValue,
      };

      if (payload.domesticForeignRelation === 0) {
        const {
          unifiedSocialCode,
          companyType,
          taxpayerType,
          licenceEndDate,
          longTermFlag,
          ...otherFieldValues
        } = payload;
        payload = otherFieldValues;
      }
      if (!isUndefined(payload.registeredRegionId)) {
        payload.registeredRegionId =
          payload.registeredRegionId[payload.registeredRegionId.length - 1];
      }

      payload.buildDate = moment(payload.buildDate).format(DEFAULT_DATE_FORMAT);

      if (payload.licenceEndDate) {
        payload.licenceEndDate = moment(payload.licenceEndDate).format(DEFAULT_DATE_FORMAT);
      }

      if (groupData && groupData[0]) {
        payload.groupId = groupData[0].groupId;
      }
      payload.tenantId = getCurrentOrganizationId();
      dispatch({
        type: 'company/createCompany',
        payload,
      }).then((res) => {
        if (res) {
          form.setFieldsValue({
            objectVersionNumber: res.objectVersionNumber,
          });
          if (callback) {
            callback(res);
          }
        }
      });
    });
  }

  @Bind()
  handleCountryChange(value) {
    this.fetchProvinceCity(value);
  }

  @Bind()
  handleLongTermFlagChange(event) {
    const { form } = this.props;
    if (event.target.value === 0) {
      form.setFieldsValue({
        licenceEndDate: null,
      });
    }
  }

  fetchProvinceCity(countryId) {
    if (!isEmpty(countryId)) {
      const { dispatch } = this.props;
      dispatch({
        type: `company/queryProvinceCity`,
        payload: countryId,
      });
    }
  }

  fetchRegionIds(id, cityList = []) {
    if (!id) return;
    const stack = [];
    const deepSearch = (children) => {
      let found = false;
      children.forEach((item) => {
        if (!found) {
          if (item.regionId === id) {
            found = true;
          } else if (!found && item.children && item.children.length > 0) {
            found = deepSearch(item.children);
          }
          if (found) stack.push(item);
        }
      });
      return found;
    };
    deepSearch(cityList);
    return stack.reverse().map((item) => item.regionId);
  }

  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        licenceUrl: file.response,
      });
    }
  }

  @Bind()
  onRemoveSuccess() {
    const { form } = this.props;
    form.setFieldsValue({
      licenceUrl: null,
    });
  }

  uploadButton;

  @Bind()
  uploadRef(upload) {
    this.uploadButton = upload;
  }

  isChinaCountry(countryId) {
    const { company = {} } = this.props;
    const { countryList = [] } = company;
    if (!isEmpty(countryId)) {
      const china = find(countryList, { countryId });
      if (!isUndefined(china)) {
        return china.countryCode === 'CN';
      }
    }
    return false;
  }

  render() {
    const {
      form,
      data = {},
      company = {},
      saving = false,
      buttonText = intl.get('hzero.common.button.save').d('保存'),
    } = this.props;
    const { companyType = [], taxpayerType = [], countryList = [], cityList } = company;
    const { domesticForeignRelation = 1, longTermFlag = 1, licenceFileName, licenceUrl } = data;
    const fileList = [];
    if (licenceUrl && this.uploadButton) {
      if (licenceUrl && this.uploadButton) {
        fileList.push({
          uid: licenceUrl,
          name: licenceFileName,
          // thumbUrl: licenceUrl,
          url: licenceUrl,
        });
      }
    }
    const { getFieldDecorator } = form;

    const regionIds = this.fetchRegionIds(data.registeredRegionId, cityList);
    const accessToken = getAccessToken();
    const headers = {};
    if (accessToken) {
      headers.Authorization = `bearer ${accessToken}`;
    }

    return (
      <Form style={{ marginTop: 8, width: 720 }}>
        <img src={logo} height={160} style={{ marginLeft: 100, marginBottom: 30 }} alt="logo" />
        <FormItem wrapperCol={{ span: 14, offset: 6 }}>
          {getFieldDecorator('domesticForeignRelation', {
            initialValue: domesticForeignRelation,
          })(
            <Checkbox>
              {intl.get('hpfm.enterprise.view.message.domesticForeignRelation').d('我是境内机构')}
            </Checkbox>
          )}
        </FormItem>
        {form.getFieldValue('domesticForeignRelation') === 1 && (
          <FormItem
            {...formItemLayout}
            label={intl
              .get('hpfm.enterprise.model.legal.unifiedSocialCode')
              .d('统一社会信用代码号')}
            hasFeedback
          >
            {getFieldDecorator('unifiedSocialCode', {
              initialValue: data.unifiedSocialCode,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hpfm.enterprise.model.legal.unifiedSocialCode')
                      .d('统一社会信用代码号'),
                  }),
                },
                {
                  pattern: /^[A-Z0-9]{18}$/,
                  message: intl
                    .get('hpfm.enterprise.model.legal.unifiedSocialCodeRule')
                    .d('由18位大写字母或数字组成'),
                },
              ],
            })(<Input inputChinese={false} style={{ width: '200px' }} typeCase="upper" />)}
          </FormItem>
        )}
        <FormItem
          {...formItemLayout}
          label={intl.get('hpfm.enterprise.model.legal.companyName').d('企业名称')}
          hasFeedback
        >
          {getFieldDecorator('companyName', {
            initialValue: data.companyName,
            validateTrigger: 'onBlur',
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.enterprise.model.legal.companyName').d('企业名称'),
                }),
              },
            ],
          })(<Input />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={intl.get('hpfm.enterprise.model.legal.shortName').d('企业简称')}
        >
          {getFieldDecorator('shortName', {
            initialValue: data.shortName,
          })(<Input />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={intl
            .get('hpfm.enterprise.model.legal.organizingInstitutionCode')
            .d('组织机构代码')}
        >
          {getFieldDecorator('organizingInstitutionCode', {
            initialValue: data.organizingInstitutionCode,
            rules: [
              {
                message: intl
                  .get(`hpfm.enterprise.view.message.organizingInstitutionCode`)
                  .d('由大写字母及数字组成'),
                pattern: /^[A-Z0-9]+$/,
              },
            ],
          })(<Input typeCase="upper" inputChinese={false} trimAll />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={intl.get('hpfm.enterprise.model.legal.dunsCode').d('邓白氏编码')}
        >
          {getFieldDecorator('dunsCode', {
            initialValue: data.dunsCode,
            rules: [
              {
                required: form.getFieldValue('domesticForeignRelation') === 0,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.enterprise.model.legal.dunsCode').d('邓白氏编码'),
                }),
              },
            ],
          })(<Input style={{ width: '200px' }} />)}
        </FormItem>
        {form.getFieldValue('domesticForeignRelation') === 1 && (
          <FormItem
            {...formItemLayout}
            label={intl.get('hpfm.enterprise.model.legal.companyType').d('企业类型')}
          >
            {getFieldDecorator('companyType', {
              initialValue: data.companyType,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.enterprise.model.legal.companyType').d('企业类型'),
                  }),
                },
              ],
            })(
              <Select style={{ width: '200px' }}>
                {companyType.map((item) => {
                  return (
                    <Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
        )}
        {form.getFieldValue('domesticForeignRelation') === 1 && (
          <FormItem
            {...formItemLayout}
            label={intl.get('hpfm.enterprise.model.legal.taxpayerType').d('纳税人标识')}
          >
            {getFieldDecorator('taxpayerType', {
              initialValue: data.taxpayerType,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.enterprise.model.legal.taxpayerType').d('纳税人标识'),
                  }),
                },
              ],
            })(
              <Select style={{ width: '200px' }}>
                {taxpayerType.map((item) => {
                  return (
                    <Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
        )}
        <FormItem
          {...formItemLayout}
          label={intl.get('hpfm.enterprise.view.message.registeredAddress').d('注册地址')}
          required
        >
          <Col span={8}>
            <FormItem>
              {getFieldDecorator('registeredCountryId', {
                initialValue: data.registeredCountryId,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.enterprise.model.legal.registeredCountryId')
                        .d('注册地址'),
                    }),
                  },
                ],
              })(
                <Select
                  showSearch
                  onBlur={this.handleCountryChange}
                  filterOption={(input, option) =>
                    option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                >
                  {countryList.map((item) => {
                    return (
                      <Option key={item.countryId} value={item.countryId}>
                        {item.countryName}
                      </Option>
                    );
                  })}
                </Select>
              )}
            </FormItem>
          </Col>
          {this.isChinaCountry(form.getFieldValue('registeredCountryId')) && (
            <Col span={15} offset={1}>
              <FormItem>
                {getFieldDecorator('registeredRegionId', {
                  initialValue: regionIds,
                })(
                  <Cascader
                    fieldNames={{ label: 'regionName', value: 'regionId' }}
                    options={cityList}
                    placeholder={intl
                      .get('hpfm.enterprise.model.legal.registeredRegionId')
                      .d('注册地址')}
                    showSearch={{
                      filter(inputValue, path) {
                        return path.some(
                          (option) =>
                            option.regionName.toLowerCase().indexOf(inputValue.toLowerCase()) > -1
                        );
                      },
                    }}
                  />
                )}
              </FormItem>
            </Col>
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={intl.get('hpfm.enterprise.model.legal.addressDetail').d('详细地址')}
          required
        >
          {getFieldDecorator('addressDetail', {
            initialValue: data.addressDetail,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.enterprise.model.legal.addressDetail').d('详细地址'),
                }),
              },
            ],
          })(<Input />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={intl.get('hpfm.enterprise.view.message.legalRepName').d('法定代表人')}
        >
          {getFieldDecorator('legalRepName', {
            initialValue: data.legalRepName,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.enterprise.model.legal.legalRepName').d('法定代表人'),
                }),
              },
            ],
          })(<Input style={{ width: '200px' }} />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={intl.get('hpfm.enterprise.view.message.registeredCapital').d('注册资本')}
        >
          {getFieldDecorator('registeredCapital', {
            initialValue: data.registeredCapital,
          })(
            <InputNumber
              style={{ width: '100px', display: 'inline-block' }}
              precision={0}
              min={0}
            />
          )}
          <span style={{ paddingLeft: '12px' }}>
            {intl.get('hzero.common.currency.ten.thousand').d('万元')}
          </span>
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={intl.get('hpfm.enterprise.view.message.buildDate').d('成立日期')}
        >
          {getFieldDecorator('buildDate', {
            initialValue: data.buildDate ? moment(data.buildDate) : null,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.enterprise.model.legal.buildDate').d('成立日期'),
                }),
              },
            ],
          })(<DatePicker style={{ width: '200px' }} placeholder="" />)}
        </FormItem>
        {form.getFieldValue('domesticForeignRelation') === 1 && (
          <FormItem
            {...formItemLayout}
            label={intl.get('hpfm.enterprise.view.message.licenceEndDate').d('营业期限')}
          >
            <Row>
              <Col span={12}>
                {getFieldDecorator('licenceEndDate', {
                  initialValue: data.licenceEndDate ? moment(data.licenceEndDate) : null,
                  rules: [
                    {
                      required: form.getFieldValue('longTermFlag') === 0,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.enterprise.view.message.licenceEndDate').d('营业期限'),
                      }),
                    },
                  ],
                })(
                  <DatePicker
                    disabled={form.getFieldValue('longTermFlag') === 1}
                    style={{ width: '200px' }}
                  />
                )}
              </Col>
              <Col span={10} offset={2}>
                {getFieldDecorator('longTermFlag', {
                  initialValue: longTermFlag,
                })(
                  <Checkbox onChange={this.handleLongTermFlagChange}>
                    {intl.get('hpfm.enterprise.view.message.longTerm').d('长期')}
                  </Checkbox>
                )}
              </Col>
            </Row>
          </FormItem>
        )}
        <FormItem
          {...formItemLayout}
          label={intl.get('hpfm.enterprise.view.message.businessScope').d('经营范围')}
        >
          {getFieldDecorator('businessScope', {
            initialValue: data.businessScope,
          })(<Input.TextArea rows={6} />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={
            form.getFieldValue('domesticForeignRelation') === 1
              ? intl.get('hpfm.enterprise.view.message.businessLicense').d('营业执照')
              : intl.get('hpfm.enterprise.view.message.registrationCertificate').d('企业登记证件')
          }
          extra={intl
            .get('hzero.common.upload.support', { type: '*.jpg;*.png;*.jpeg;*.pdf' })
            .d('上传格式：*.jpg;*.png;*.jpeg;*.pdf')}
        >
          {getFieldDecorator('licenceUrl', {
            initialValue: data.licenceUrl,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name:
                    form.getFieldValue('domesticForeignRelation') === 1
                      ? intl.get('hpfm.enterprise.view.message.businessLicense').d('营业执照')
                      : intl
                          .get('hpfm.enterprise.view.message.registrationCertificate')
                          .d('企业登记证件'),
                }),
              },
            ],
          })(<div />)}
          <Upload
            onRef={this.uploadRef}
            fileType="image/jpeg;image/png"
            single
            bucketName={BKT_PUBLIC}
            bucketDirectory="hpfm06"
            fileList={fileList}
            onUploadSuccess={this.onUploadSuccess}
            onRemoveSuccess={this.onRemoveSuccess}
            text={
              form.getFieldValue('domesticForeignRelation') === 1
                ? intl.get('hpfm.enterprise.view.message.businessLicense')
                : intl.get('hpfm.enterprise.view.message.registrationCertificate')
            }
          />
        </FormItem>
        {getFieldDecorator('enabledFlag', {
          initialValue: 1,
        })(<div />)}
        {getFieldDecorator('objectVersionNumber', {
          initialValue: data.objectVersionNumber,
        })(<div />)}
        <FormItem {...submitFormLayout}>
          <Button type="primary" onClick={this.saveAndNext} loading={saving}>
            {buttonText}
          </Button>
        </FormItem>
      </Form>
    );
  }
}
