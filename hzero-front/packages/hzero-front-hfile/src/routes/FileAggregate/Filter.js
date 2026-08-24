import React, { PureComponent } from 'react';
import { Button, Col, DatePicker, Form, Input, Row, Select } from 'hzero-ui';
import { Bind, Throttle } from 'lodash-decorators';
import moment from 'moment';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getDateTimeFormat, isTenantRoleLevel } from 'utils/utils';
import {
  DEBOUNCE_TIME,
  DEFAULT_DATETIME_FORMAT,
  FORM_COL_4_LAYOUT,
  FORM_COL_3_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;
const { Option } = Select;

/**
 * 查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} fetchMessageList - 查询
 * @reactProps {Function} onStoreFormValues - 存储表单值
 * @reactProps {Object} statusList - 状态
 * @return React.element
 */
const displayBlockStyle = {
  display: '',
};

const displayNoneStyle = {
  display: 'none',
};

@Form.create({ fieldNameProp: null })
export default class Filter extends PureComponent {
  state = {
    expandForm: false,
  };

  /**
   * 提交查询表单
   *
   * @memberof QueryForm
   */
  @Bind()
  handleSearch() {
    const { form, onSearch, onStoreFormValues } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        let values = { ...fieldsValue };
        values = {
          fromCreateDate: fieldsValue.fromCreateDate
            ? fieldsValue.fromCreateDate.format(DEFAULT_DATETIME_FORMAT)
            : undefined,
          toCreateDate: fieldsValue.toCreateDate
            ? fieldsValue.toCreateDate.format(DEFAULT_DATETIME_FORMAT)
            : undefined,
        };
        onSearch({ ...fieldsValue, ...values });
        onStoreFormValues({ ...fieldsValue, ...values });
      }
    });
  }

  /**
   * 重置表单
   *
   * @memberof QueryForm
   */
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  componentWillUnmount() {
    this.toggleForm.cancel();
  }

  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const {
      fileFormatList = [],
      fileTypeList = [],
      fileUnitList = [],
      sourceList = [],
    } = this.props;
    const { expandForm } = this.state;
    const fileMinUnitSelector = getFieldDecorator('fileMinUnit', {
      initialValue: 'KB',
    })(
      <Select style={{ width: '65px' }}>
        {fileUnitList &&
          fileUnitList.map(item => (
            <Option value={item.value} key={item.value}>
              {item.meaning}
            </Option>
          ))}
      </Select>
    );
    const fileMaxUnitSelector = getFieldDecorator('fileMaxUnit', {
      initialValue: 'KB',
    })(
      <Select style={{ width: '65px' }}>
        {fileUnitList &&
          fileUnitList.map(item => (
            <Option value={item.value} key={item.value}>
              {item.meaning}
            </Option>
          ))}
      </Select>
    );
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" align="bottom">
          <Col span={18}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hfile.fileAggregate.model.fileAggregate.attachmentUuid').d('批号')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('attachmentUUID')(<Input />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hfile.fileAggregate.model.fileAggregate.bucketName').d('分组')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('bucketName')(<Input />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hfile.fileAggregate.model.fileAggregate.fileType').d('文件类型')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('fileType')(
                  <Select allowClear>
                    {fileTypeList &&
                      fileTypeList.map(item => (
                        <Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row
          {...SEARCH_FORM_ROW_LAYOUT}
          type="flex"
          align="bottom"
          style={expandForm ? displayBlockStyle : displayNoneStyle}
        >
          <Col span={18}>
            {!isTenantRoleLevel() && (
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hfile.fileAggregate.model.fileAggregate.tenantId').d('租户')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
                </FormItem>
              </Col>
            )}
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hfile.fileAggregate.model.fileAggregate.fileName').d('文件名')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('fileName')(<Input />)}
              </FormItem>
            </Col>
            {!isTenantRoleLevel() ? (
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl
                    .get('hfile.fileAggregate.model.fileAggregate.fileFormat')
                    .d('文件格式')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('fileFormat')(
                    <Select allowClear>
                      {fileFormatList &&
                        fileFormatList.map(item => (
                          <Option value={item.value} key={item.value}>
                            {item.meaning}
                          </Option>
                        ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
            ) : (
              <>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hfile.fileAggregate.model.fileAggregate.fileFormat')
                      .d('文件格式')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('fileFormat')(
                      <Select allowClear>
                        {fileFormatList &&
                          fileFormatList.map(item => (
                            <Option value={item.value} key={item.value}>
                              {item.meaning}
                            </Option>
                          ))}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl.get('hzero.common.time.creation.from').d('创建日期从')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('fromCreateDate')(
                      <DatePicker
                        showTime
                        placeholder=""
                        format={getDateTimeFormat()}
                        disabledDate={currentDate =>
                          getFieldValue('toCreateDate') &&
                          moment(getFieldValue('toCreateDate')).isBefore(currentDate, 'day')}
                      />
                    )}
                  </FormItem>
                </Col>
              </>
            )}
          </Col>
          <Col span={18}>
            {!isTenantRoleLevel() && (
              <>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl.get('hzero.common.time.creation.from').d('创建日期从')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('fromCreateDate')(
                      <DatePicker
                        showTime
                        placeholder=""
                        format={getDateTimeFormat()}
                        disabledDate={currentDate =>
                          getFieldValue('toCreateDate') &&
                          moment(getFieldValue('toCreateDate')).isBefore(currentDate, 'day')}
                      />
                    )}
                  </FormItem>
                </Col>
              </>
            )}

            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hzero.common.time.creation.to').d('创建日期至')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('toCreateDate')(
                  <DatePicker
                    showTime
                    placeholder=""
                    format={getDateTimeFormat()}
                    disabledDate={currentDate =>
                      getFieldValue('fromCreateDate') &&
                      moment(getFieldValue('fromCreateDate')).isAfter(currentDate, 'day')}
                  />
                )}
              </FormItem>
            </Col>
            {!isTenantRoleLevel() ? (
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hfile.fileAggregate.model.fileAggregate.realName').d('上传人')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('realName')(<Input />)}
                </FormItem>
              </Col>
            ) : (
              <>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl.get('hfile.fileAggregate.model.fileAggregate.realName').d('上传人')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('realName')(<Input />)}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hfile.fileAggregate.model.fileAggregate.fileMinSize')
                      .d('文件最小')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('fileMinSize', {
                      rules: [
                        {
                          min: 0,
                          pattern: /^\d+$/,
                          message: intl
                            .get('hfile.fileAggregate.view.message.patternValidate')
                            .d('请输入大于等于0的整数'),
                        },
                      ],
                    })(<Input type="number" addonAfter={fileMinUnitSelector} />)}
                  </FormItem>
                </Col>
              </>
            )}
          </Col>
          <Col span={18}>
            {!isTenantRoleLevel() && (
              <>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hfile.fileAggregate.model.fileAggregate.fileMinSize')
                      .d('文件最小')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('fileMinSize', {
                      rules: [
                        {
                          min: 0,
                          pattern: /^\d+$/,
                          message: intl
                            .get('hfile.fileAggregate.view.message.patternValidate')
                            .d('请输入大于等于0的整数'),
                        },
                      ],
                    })(<Input type="number" addonAfter={fileMinUnitSelector} />)}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hfile.fileAggregate.model.fileAggregate.fileMaxSize')
                      .d('文件最大')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('fileMaxSize', {
                      rules: [
                        {
                          min: 0,
                          pattern: /^\d+$/,
                          message: intl
                            .get('hfile.fileAggregate.view.message.patternValidate')
                            .d('请输入大于等于0的整数'),
                        },
                      ],
                    })(<Input type="number" addonAfter={fileMaxUnitSelector} />)}
                  </FormItem>
                </Col>
              </>
            )}

            {!isTenantRoleLevel() ? (
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl
                    .get('hfile.fileAggregate.model.fileAggregate.directory')
                    .d('上传目录')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('directory')(<Input />)}
                </FormItem>
              </Col>
            ) : (
              <>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hfile.fileAggregate.model.fileAggregate.fileMaxSize')
                      .d('文件最大')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('fileMaxSize', {
                      rules: [
                        {
                          min: 0,
                          pattern: /^\d+$/,
                          message: intl
                            .get('hfile.fileAggregate.view.message.patternValidate')
                            .d('请输入大于等于0的整数'),
                        },
                      ],
                    })(<Input type="number" addonAfter={fileMaxUnitSelector} />)}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hfile.fileAggregate.model.fileAggregate.directory')
                      .d('上传目录')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('directory')(<Input />)}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hfile.fileAggregate.model.fileAggregate.serverCode')
                      .d('服务器编码')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('serverCode')(<Input trim inputChinese={false} />)}
                  </FormItem>
                </Col>
              </>
            )}
          </Col>
          <Col span={18}>
            {!isTenantRoleLevel() && (
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl
                    .get('hfile.fileAggregate.model.fileAggregate.serverCode')
                    .d('服务器编码')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('serverCode')(<Input trim inputChinese={false} />)}
                </FormItem>
              </Col>
            )}

            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl
                  .get('hfile.fileAggregate.model.fileAggregate.sourceTypeMeaning')
                  .d('来源类型')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('sourceType')(
                  <Select allowClear>
                    {sourceList &&
                      sourceList.map(item => (
                        <Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Col>
        </Row>
      </Form>
    );
  }
}
