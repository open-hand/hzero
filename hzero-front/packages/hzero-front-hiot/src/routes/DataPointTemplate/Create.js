/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-08 09:33:05
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 数据点模板新建页面
 * 这个页面分为基本信息和数据点类型部分，其中数据点类型部分仅作为显示用，且整个功能公用一个ds，所以可以发现下面多创建了一个ds实例。
 * 因为详情的数据结构与创建的数据结构不一致，所以需要一个额外的ds来显示
 */
import React from 'react';
import {
  Button,
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

import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import intl from 'utils/intl';

import { dataPointTemplateDS } from '@/stores/dataPointTemplateDS';
import {
  CONTROL_BOOL_TYPE,
  CONTROL_INT_TYPE,
  DATA_TYPE,
  POINT_TYPE,
  STATUS_TYPE,
} from '@/utils/constants';
import EnumOptions from './EnumOptions';

@formatterCollections({ code: ['hiot.dataPointTemplate', 'hiot.common'] })
export default class Create extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dataPointType: null,
      dataType: null, // 数据类型
    };
    this.dataPointTemplateDS = new DataSet(dataPointTemplateDS());
    this.dataPointTemplateShowDS = new DataSet(dataPointTemplateDS());
  }

  componentDidMount() {
    this.dataPointTemplateShowDS.create({}, 0);
    this.dataPointTemplateDS.create({ _status: 'create' }, 0);
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
      this.dataPointTemplateDS.get(0).set('options', optionsStr);
      if (validFlag) {
        const res = await this.dataPointTemplateDS.submit();
        if (!res) {
          return false;
        }
        this.props.history.push('/hiot/data-point/template/list');
      } else {
        notification.error({
          message: intl.get('hiot.common.view.message.validFail').d('校验失败'),
        });
      }
    } catch (err) {
      //
    }
  }

  /**
   * 修改数据点类型
   */
  @Bind()
  handleChangeDataType(value) {
    const { category, categoryName, dataType, dataTypeMeaning, typeName, typeCode } = value || {};
    this.dataPointTemplateShowDS.get(0).set({
      propertyType: value,
    });
    let dataPointType;
    if ([POINT_TYPE, STATUS_TYPE].includes(category)) {
      dataPointType = category;
    } else if (dataType === DATA_TYPE.NUMBER) {
      dataPointType = CONTROL_INT_TYPE;
    } else if (dataType === DATA_TYPE.BOOL || dataType === DATA_TYPE.ENUM) {
      dataPointType = CONTROL_BOOL_TYPE;
    } else {
      this.setState({
        dataPointType: null,
        dataType,
      });
      return null;
    }
    this.dataPointTemplateDS.get(0).set({
      typeCode,
      typeName,
      propertyType: value,
      typeNameMeaning: dataPointType,
      dataTypeMeaning,
      categoryMeaning: categoryName,
    });
    this.setState({
      dataPointType,
      dataType,
      category,
    });
  }

  // 绑定子组件--可选项
  @Bind()
  onRef(e = {}) {
    this.optionElement = e;
  }

  // 选择数据点类型为状态的表单
  @Bind()
  renderStatusForm() {
    const { dataType, category } = this.state;
    const enumOptionsProps = {
      category,
      type: dataType, // dataType
      onRef: this.onRef,
      disabled: ![DATA_TYPE.ENUM, DATA_TYPE.BOOL].includes(dataType),
    };
    return (
      <Row>
        {this.renderBasicForm()}
        <Col span={16}>
          <Form dataSet={this.dataPointTemplateDS} columns={2}>
            <NumberField name="reportInterval" min={0} max={999999} step={1} addonAfter="S" />
          </Form>
          <Row>
            <Col span={12}>
              <EnumOptions {...enumOptionsProps} />
            </Col>
          </Row>
        </Col>
      </Row>
    );
  }

  // 选择数据点类型为测量点的表单
  @Bind()
  renderPointForm() {
    return (
      <Row>
        <Col span={16}>
          <Form dataSet={this.dataPointTemplateDS} columns={2}>
            <TextField name="propertyModelCode" />
            <IntlField name="propertyModelName" />
            <Lov noCache name="typeNameCode" onChange={this.handleChangeDataType} />
            <Lov noCache name="unitCode" />
            <NumberField name="valuePrecision" min={0} max={3} step={1} />
            <NumberField name="reportInterval" min={0} max={999999} step={1} addonAfter="S" />
            <NumberField name="minValue" min={-999999999} max={999999999} step={1} />
            <NumberField name="maxValue" min={-999999999} max={999999999} step={1} />
            <TextArea name="description" rowSpan={2} />
          </Form>
        </Col>
      </Row>
    );
  }

  // 选择数据点类型为控制参数-布尔的表单
  @Bind()
  renderControlBoolForm() {
    const { dataType, dataPointType, category } = this.state;
    const enumOptionsProps = {
      category,
      dataPointType,
      type: dataType, // dataType
      onRef: this.onRef,
      disabled: ![DATA_TYPE.ENUM, DATA_TYPE.BOOL].includes(dataType),
    };
    return (
      <Row>
        {this.renderBasicForm()}
        <Col span={8}>
          <Form dataSet={this.dataPointTemplateDS} columns={2}>
            <EnumOptions {...enumOptionsProps} />
          </Form>
        </Col>
      </Row>
    );
  }

  // 选择数据点类型为控制参数-数值的表单
  @Bind()
  renderControlIntForm() {
    return (
      <Row>
        <Col span={16}>
          <Form dataSet={this.dataPointTemplateDS} columns={2}>
            <TextField name="propertyModelCode" />
            <IntlField name="propertyModelName" />
            <Lov noCache name="typeNameCode" onChange={this.handleChangeDataType} />
            <Lov noCache name="unitCode" />
            <NumberField name="valuePrecision" min={0} max={3} step={1} />
            <NumberField name="minValue" min={-999999999} max={999999999} step={1} />
            <NumberField name="maxValue" min={-999999999} max={999999999} step={1} />
            <TextArea name="description" rowSpan={2} />
          </Form>
        </Col>
      </Row>
    );
  }

  // 未选择数据点类型时的表单
  @Bind()
  renderBasicForm() {
    return (
      <Row>
        <Col span={16}>
          <Form dataSet={this.dataPointTemplateDS} columns={2}>
            <TextField name="propertyModelCode" />
            <IntlField name="propertyModelName" />
            <Lov noCache name="typeNameCode" onChange={this.handleChangeDataType} />
            {/* <TextField name='typeNameMeaning' required /> */}
            <TextArea name="description" rowSpan={2} />
          </Form>
        </Col>
      </Row>
    );
  }

  // 基本信息表单--选择数据点类型时判断
  @Bind()
  renderBasicInfoForm() {
    const { dataPointType } = this.state;
    if (dataPointType === null) {
      return this.renderBasicForm();
    }
    if (dataPointType === STATUS_TYPE) {
      return this.renderStatusForm();
    }
    if (dataPointType === POINT_TYPE) {
      return this.renderPointForm();
    }
    if (dataPointType === CONTROL_BOOL_TYPE) {
      return this.renderControlBoolForm();
    }
    if (dataPointType === CONTROL_INT_TYPE) {
      return this.renderControlIntForm();
    }
  }

  render() {
    return (
      <>
        <Header
          title={intl.get('hiot.dataPointTemplate.view.title.create.header').d('新建数据点')}
          backPath="/hiot/data-point/template/list"
        >
          <Button color="primary" icon="save" onClick={this.handleDataPointTemplateSave}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
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
              <Col span={16}>
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
