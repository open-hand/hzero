/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-08 15:05:10
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 数据点模板详情/编辑页面
 */
import React from 'react';
import {
  Col,
  DataSet,
  Form,
  IntlField,
  Lov,
  NumberField,
  Row,
  TextArea,
  TextField,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import intl from 'utils/intl';

import { dataPointTemplateDS } from '@/stores/dataPointTemplateDS';
import EnumOptions from './EnumOptions';
import {
  CONTROL_BOOL_TYPE,
  CONTROL_INT_TYPE,
  DATA_TYPE,
  EDIT_ACTION,
  POINT_TYPE,
  STATUS_TYPE,
} from '@/utils/constants';

export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    const { match: { params = {} } = {} } = this.props;
    const { action, templateId } = params;
    this.dataPointTemplateDS = new DataSet(dataPointTemplateDS());
    this.dataPointTemplateShowDS = new DataSet(dataPointTemplateDS());
    this.state = {
      dataPointType: null, // 数据点模版类型
      action,
      templateId,
      dataType: null, // 数据类型
    };
  }

  componentDidMount() {
    const { templateId } = this.state;
    this.dataPointTemplateDS.setQueryParameter('propertyModelId', templateId);
    this.dataPointTemplateDS.query().then((resp) => {
      if (resp) {
        let dataPointType;
        const { propertyType } = resp;
        const { category, dataType } = propertyType;
        if ([POINT_TYPE, STATUS_TYPE].includes(category)) {
          dataPointType = category;
        } else if (dataType === DATA_TYPE.NUMBER) {
          dataPointType = CONTROL_INT_TYPE;
        } else if (dataType === DATA_TYPE.BOOL || dataType === DATA_TYPE.ENUM) {
          dataPointType = CONTROL_BOOL_TYPE;
        } else {
          dataPointType = null;
        }
        this.dataPointTemplateShowDS.create({ propertyType }, 0);
        this.setState({
          dataPointType,
          dataType,
          category,
        });
      }
    });
  }

  /**
   * 编辑页面的字段
   */
  @Bind()
  handleEdit() {
    this.setState({
      action: EDIT_ACTION,
    });
  }

  /**
   * 保存数据点模板
   */
  @Bind()
  async handleDataPointTemplateSave() {
    const { dataType, dataPointType } = this.state;
    const { options = [] } = this.optionElement ? this.optionElement.state : {};
    try {
      let validFlag = true;
      if (
        options.length === 0 &&
        dataType === DATA_TYPE.ENUM &&
        [CONTROL_BOOL_TYPE, STATUS_TYPE].includes(dataPointType)
      ) {
        notification.error({
          message: intl.get('hiot.common.view.message.selectNotEmpty').d('可选项不能为空'),
        });
        return;
      }
      for (const option of options) {
        if (option.nameStatus === 'error' || option.codeStatus === 'error') {
          validFlag = false;
          break;
        }
      }
      const optionsStr = JSON.stringify(
        options.map(({ select, ...item }) => ({ ...item, select: select ? 1 : 0 }))
      );
      this.dataPointTemplateDS.get(0).set('propertyModel.options', optionsStr);
      if (validFlag) {
        const notUpdate = this.dataPointTemplateDS.updated.length === 0;
        const subSuccess = await this.dataPointTemplateDS.submit();
        if (notUpdate || subSuccess) {
          this.props.history.push('/hiot/data-point/template/list');
        }
      } else {
        notification.error({
          message: intl.get('hiot.common.view.message.validFail').d('校验失败'),
        });
      }
    } catch (err) {
      //
    }
  }

  // 绑定子组件--可选项
  @Bind()
  onRef(e = {}) {
    this.optionElement = e;
  }

  // 未选择数据点类型时的表单
  @Bind()
  renderBasicForm() {
    const { action } = this.state;
    return (
      <Row>
        <Col span={16}>
          <Form dataSet={this.dataPointTemplateDS} columns={2}>
            <TextField name="propertyModelCode" disabled />
            <IntlField name="propertyModelName" disabled />
            {/* <TextField name='typeNameMeaning' required /> */}
            <TextArea
              name="description"
              rowSpan={2}
              colSpan={1.5}
              disabled={action !== EDIT_ACTION}
            />
          </Form>
        </Col>
      </Row>
    );
  }

  // 数据点类型为状态的表单
  @Bind()
  renderStatusForm() {
    const { dataType, action, category } = this.state;
    const {
      propertyModel: { options },
    } = this.dataPointTemplateDS.get(0).toData();
    const enumOptionsProps = {
      category,
      type: dataType, // dataType
      onRef: this.onRef,
      disabled: action !== EDIT_ACTION,
      optionsSource: options && JSON.parse(options),
    };
    return (
      <div>
        <Row>
          <Col span={16}>
            <Form dataSet={this.dataPointTemplateDS} columns={2}>
              <TextField name="propertyModelCode" disabled />
              <IntlField name="propertyModelName" disabled />
              <NumberField
                name="reportInterval"
                min={0}
                max={999999}
                step={1}
                addonAfter="S"
                disabled={action !== EDIT_ACTION}
              />
              <TextArea
                name="description"
                rowSpan={2}
                colSpan={2}
                disabled={action !== EDIT_ACTION}
              />
            </Form>
          </Col>
        </Row>
        <Row>
          <Col span={8}>
            <Form columns={3}>
              <EnumOptions {...enumOptionsProps} />
            </Form>
          </Col>
        </Row>
      </div>
    );
  }

  // 数据点类型为测量点的表单
  @Bind()
  renderPointForm() {
    const { action } = this.state;
    return (
      <Row>
        {this.renderPointCommonForm()}
        <Col span={18}>
          <Form dataSet={this.dataPointTemplateDS} columns={2}>
            <NumberField
              name="reportInterval"
              min={0}
              max={999999}
              step={1}
              addonAfter="S"
              disabled={action !== EDIT_ACTION}
            />
            <NumberField
              name="minValue"
              min={-999999999}
              max={999999999}
              step={1}
              disabled={action !== EDIT_ACTION}
            />
            <NumberField
              name="maxValue"
              min={-999999999}
              max={999999999}
              step={1}
              disabled={action !== EDIT_ACTION}
            />
            <TextArea
              name="description"
              rowSpan={2}
              colSpan={2}
              disabled={action !== EDIT_ACTION}
            />
          </Form>
        </Col>
      </Row>
    );
  }

  // 数据点类型为控制参数-布尔的表单
  @Bind()
  renderControlBoolForm() {
    const { dataType, action, category } = this.state;
    const {
      propertyModel: { options },
    } = this.dataPointTemplateDS.get(0).toData();
    const enumOptionsProps = {
      category,
      type: dataType, // 数据类型
      onRef: this.onRef,
      disabled: action !== EDIT_ACTION,
      optionsSource: options && JSON.parse(options),
    };
    return (
      <div>
        <Row>
          <Col span={18}>
            <Form dataSet={this.dataPointTemplateDS} columns={2}>
              <TextField name="propertyModelCode" disabled />
              <IntlField name="propertyModelName" disabled />
              <TextArea name="description" rowSpan={2} disabled={action !== EDIT_ACTION} />
            </Form>
          </Col>
        </Row>
        <Row>
          <Col span={8}>
            <Form columns={3}>
              <EnumOptions {...enumOptionsProps} />
            </Form>
          </Col>
        </Row>
      </div>
    );
  }

  // 数据点类型为控制参数-数值的表单
  @Bind()
  renderControlIntForm() {
    const { action } = this.state;
    return (
      <Row>
        {this.renderPointCommonForm()}
        <Col span={18}>
          <Form dataSet={this.dataPointTemplateDS} columns={2}>
            <NumberField
              name="minValue"
              min={-999999999}
              max={999999999}
              step={1}
              disabled={action !== EDIT_ACTION}
            />
            <NumberField
              name="maxValue"
              min={-999999999}
              max={999999999}
              step={1}
              disabled={action !== EDIT_ACTION}
            />
            <TextArea name="description" rowSpan={2} disabled={action !== EDIT_ACTION} />
          </Form>
        </Col>
      </Row>
    );
  }

  // 数据点类型为测量点/控制参数-数值时的公用表单部分
  @Bind()
  renderPointCommonForm() {
    const { action } = this.state;
    return (
      <Row>
        <Col span={18}>
          <Form dataSet={this.dataPointTemplateDS} columns={2}>
            <TextField name="propertyModelCode" disabled />
            <IntlField name="propertyModelName" disabled />
            <Lov noCache name="unitCode" disabled={action !== EDIT_ACTION} />
            <NumberField
              name="valuePrecision"
              min={0}
              max={3}
              step={1}
              disabled={action !== EDIT_ACTION}
            />
          </Form>
        </Col>
      </Row>
    );
  }

  // 基本信息表单--根据数据点类型来判断
  @Bind()
  renderBasicInfoForm() {
    const { dataPointType } = this.state;
    if (dataPointType === null) {
      return this.renderBasicForm();
    } else if (dataPointType === STATUS_TYPE) {
      return this.renderStatusForm();
    } else if (dataPointType === POINT_TYPE) {
      return this.renderPointForm();
    } else if (dataPointType === CONTROL_BOOL_TYPE) {
      return this.renderControlBoolForm();
    } else if (dataPointType === CONTROL_INT_TYPE) {
      return this.renderControlIntForm();
    }
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const { action } = this.state;
    return (
      <>
        <Header
          title={
            action === EDIT_ACTION
              ? intl.get('hiot.dataPointTemplate.view.title.edit.header').d('数据点编辑')
              : intl.get('hiot.dataPointTemplate.view.title.detail.header').d('数据点详情')
          }
          backPath="/hiot/data-point/template/list"
        >
          {action === EDIT_ACTION ? (
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}.button.save`,
                  type: 'button',
                  meaning: '添加数据点模版-保存',
                },
              ]}
              color="primary"
              icon="save"
              onClick={this.handleDataPointTemplateSave}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </ButtonPermission>
          ) : (
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}.button.edit`,
                  type: 'button',
                  meaning: '添加数据点模版-编辑',
                },
              ]}
              color="primary"
              icon="mode_edit"
              onClick={this.handleEdit}
            >
              {intl.get('hzero.common.button.edit').d('编辑')}
            </ButtonPermission>
          )}
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={intl.get('hiot.common.view.baseInfo').d('基本信息')}
          >
            {this.renderBasicInfoForm()}
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={intl.get('hiot.dataPointTemplate.model.dpt.dataPointType').d('数据点类型')}
          >
            <Row>
              <Col span={18}>
                <Form disabled dataSet={this.dataPointTemplateShowDS} columns={2}>
                  <TextField name="typeCode" />
                  <TextField name="typeName" />
                  <TextField name="categoryMeaning" />
                  <TextField name="dataTypeMeaning" />
                </Form>
              </Col>
            </Row>
          </Card>
        </Content>
      </>
    );
  }
}
