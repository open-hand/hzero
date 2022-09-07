import React, { PureComponent } from 'react';
import {
  Form,
  Input,
  Spin,
  Row,
  Col,
  DatePicker,
  InputNumber,
  Button,
  Select,
  Radio,
  Checkbox,
} from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import Lov from 'components/Lov';
import ValueList from 'components/ValueList';

import intl from 'utils/intl';
import { getDateTimeFormat, getCurrentOrganizationId } from 'utils/utils';

import styles from './index.less';

const organizationId = getCurrentOrganizationId();
/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 16 },
};

const CheckboxGroup = Checkbox.Group;
/**
 * 数据集查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      dateTimeFormat: getDateTimeFormat(),
      expandForm: false,
    };
  }

  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  // 表单重置
  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  getCurrentComponent(item) {
    const { dateTimeFormat } = this.state;
    let component;
    const style = {
      width: `${item.width}px`,
      height: `${item.height}px`,
    };
    const list = item.value || [];
    switch (item.type) {
      case 'Lov': // Lov
        component = (
          <Lov
            code={item.valueSource}
            originTenantId={organizationId}
            style={style}
            queryParams={{ tenantId: organizationId }}
          />
        );
        break;
      case 'Input': // 文本
        component = <Input style={style} />;
        break;
      case 'Checkbox': // 多选框
        component = (
          <CheckboxGroup>
            {list.map((n) => {
              return <Checkbox value={n.value}>{n.meaning}</Checkbox>;
            })}
          </CheckboxGroup>
        );
        break;
      case 'Select': // 下拉框
        component = item.multipled ? (
          <ValueList
            mode="multiple"
            style={style}
            options={item.value}
            allowClear={!item.isRequired}
          />
        ) : (
          <ValueList style={style} options={item.value} allowClear={!item.isRequired} />
        );
        break;
      case 'Radiobox': // 单择框
        component = (
          <Radio.Group>
            {list.map((n) => {
              return <Radio value={n.value}>{n.meaning}</Radio>;
            })}
          </Radio.Group>
        );
        break;
      case 'DatePicker': // 日期选择框
        component = <DatePicker style={style} placeholder="" format={this.props.dateFormat} />;
        break;
      case 'DatetimePicker': // 日期时间选择框
        component = <DatePicker style={style} showTime placeholder="" format={dateTimeFormat} />;
        break;
      case 'InputNumber': // 数字框
        component = <InputNumber style={style} />;
        break;
      case 'MulInput': // 多值文本框
        component = <Select style={style} mode="tags" open={false} allowClear bordered={false} />;
        break;
      default:
        component = <Input style={style} />;
        break;
    }
    return component;
  }

  // 渲染参数组件
  @Bind()
  renderParamGroup(paramList = []) {
    const {
      form: { getFieldDecorator },
      dateFormat,
    } = this.props;
    return paramList.map((item) => {
      let { defaultValue } = item;
      if (item.type === 'Select' || item.type === 'Lov' || item.type === 'Checkbox') {
        const newValue = Array.isArray(item.value) ? item.value : item.value.split(',');
        const defaultFlag = newValue.some((items) => items.value === item.defaultValue);
        if (item.multipled) {
          defaultValue = item.defaultValue && defaultFlag ? item.defaultValue.split(',') : [];
        }
        defaultValue = defaultFlag ? item.defaultValue : undefined;
      } else if (item.type === 'DatePicker') {
        defaultValue = item.defaultValue ? moment(item.defaultValue, dateFormat) : undefined;
      } else if (item.type === 'MulInput') {
        defaultValue = item.defaultValue ? item.defaultValue.split(',') : [];
      }

      return (
        <Col span={8} key={item.name}>
          <Form.Item label={item.text} {...formLayout} style={{ margin: 0 }}>
            {getFieldDecorator(`${item.name}`, {
              initialValue: defaultValue,
              rules: [
                {
                  required: item.formElement === 'Checkbox' ? false : item.isRequired !== 0,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: item.text,
                    })
                    .d(`${item.text}不能为空`),
                },
              ],
            })(this.getCurrentComponent(item))}
          </Form.Item>
        </Col>
      );
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { fetchParamsLoading, formElements = [] } = this.props;
    const { expandForm } = this.state;
    return (
      <>
        {/* <Divider orientation="left">{intl.get('hrpt.reportQuery.view.message.reportParams').d('报表参数')}</Divider> */}
        {formElements.length !== 0 && (
          <div className={styles['model-title']}>
            {intl.get('hrpt.reportQuery.view.message.reportParams').d('报表参数')}
          </div>
        )}
        <Spin spinning={fetchParamsLoading}>
          {formElements.length > 3 ? (
            <>
              <Row type="flex" gutter={24}>
                <Col span={18}>{this.renderParamGroup(formElements.slice(0, 3))}</Col>
                {formElements.length !== 0 && (
                  <Col span={6}>
                    <Button onClick={this.toggleForm} style={{ marginRight: 10 }}>
                      {expandForm
                        ? intl.get('hzero.common.button.collected').d('收起查询')
                        : intl.get('hzero.common.button.viewMore').d('更多查询')}
                    </Button>
                    <Button onClick={this.handleReset}>
                      {intl.get('hzero.common.button.reset').d('重置')}
                    </Button>
                  </Col>
                )}
              </Row>
              <Row type="flex" gutter={24} style={{ display: expandForm ? '' : 'none' }}>
                <Col span={18}>{this.renderParamGroup(formElements.slice(3))}</Col>
              </Row>
            </>
          ) : (
            <Row type="flex" gutter={24}>
              <Col span={18}>{this.renderParamGroup(formElements)}</Col>

              {formElements.length !== 0 && (
                <Col span={6}>
                  <Form.Item>
                    <Button onClick={this.handleReset}>
                      {intl.get('hzero.common.button.reset').d('重置')}
                    </Button>
                  </Form.Item>
                </Col>
              )}
            </Row>
          )}
        </Spin>
      </>
    );
  }
}
