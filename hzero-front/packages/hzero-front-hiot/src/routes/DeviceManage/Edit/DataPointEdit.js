/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-10 16:29:09
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 设备编辑页面-数据点信息-数据点编辑页面
 */
import React from 'react';
import {
  Button,
  Form,
  TextField,
  TextArea,
  Row,
  Col,
  NumberField,
  DataSet,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import moment from 'moment';

import { STATUS_TYPE, POINT_TYPE, CONTROL_INT_TYPE } from '@/utils/constants';
import { dataPointEditDS, deviceManageSaveDS } from '@/stores/deviceManageDS';
import EnumOptions from '../../DataPointTemplate/EnumOptions';

@formatterCollections({ code: ['hiot.dataPointTemplate'] })
export default class DataPointEdit extends React.Component {
  constructor(props) {
    super(props);
    const {
      match: { params = {} } = {},
      location: { state = {} },
    } = this.props;
    const { deviceId } = params;
    if (!state.dataPoint) {
      this.backToDevicePage(deviceId);
      return;
    }
    const { category: dataPointType } = state.dataPoint;
    this.dataPointEditDS = new DataSet(dataPointEditDS());
    this.dataPointEditDS.create(state.dataPoint);
    this.state = {
      deviceId,
      dataPointType,
      deviceInfo: state.deviceInfo,
    };
  }

  // 绑定子组件--可选项组件
  @Bind()
  onRef(e = {}) {
    this.optionElement = e;
  }

  // 选择数据点类型为控制参数-数值的表单
  @Bind()
  renderControlIntForm() {
    return (
      <Row>
        <Col span={16}>
          <Form dataSet={this.dataPointEditDS} columns={2}>
            <TextField name="propertyCode" disabled />
            <TextField name="guid" disabled />
            <TextField name="propertyName" disabled />
            <TextField name="unitCode" disabled />
            <TextField name="categoryMeaning" disabled />
            <TextField name="dataTypeMeaning" disabled />
            <NumberField name="minValue" min={-999999999} max={999999999} step={1} />
            <NumberField name="maxValue" min={-999999999} max={999999999} step={1} />
            <NumberField name="valuePrecision" min={0} max={3} step={1} />
            <TextArea name="description" rowSpan={2} />
          </Form>
        </Col>
      </Row>
    );
  }

  // 选择数据点类型为状态的表单
  @Bind()
  renderStatusForm() {
    const enumOptionsProps = {
      type: 'enum', // dataType
      onRef: this.onRef,
      disabled: true,
    };
    return (
      <>
        <Row>
          <Col span={16}>
            <Form dataSet={this.dataPointEditDS} columns={2}>
              <TextField name="propertyCode" disabled />
              <TextField name="guid" disabled />
              <TextField name="propertyName" disabled />
              {/* <TextField name='typeNameMeaning' required /> */}
              <TextArea name="description" rowSpan={2} />
              <NumberField
                name="reportInterval"
                min={0}
                max={999999}
                step={1}
                addonAfter="S"
                disabled
              />
              <TextField name="categoryMeaning" disabled />
              <TextField name="dataTypeMeaning" disabled />
              {/* <Lov noCache name="alertModel" /> */}
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
      </>
    );
  }

  // 选择数据点类型为测量点的表单
  @Bind()
  renderPointForm() {
    return (
      <Row>
        <Col span={16}>
          <Form dataSet={this.dataPointEditDS} columns={2}>
            <TextField name="propertyCode" disabled />
            <TextField name="guid" disabled />
            <TextField name="propertyName" disabled />
            <TextField name="unitCode" disabled />
            <NumberField
              name="reportInterval"
              min={0}
              max={999999}
              step={1}
              addonAfter="S"
              disabled
            />
            <NumberField name="valuePrecision" min={0} max={3} step={1} disabled />
            <TextField name="categoryMeaning" disabled />
            <TextField name="dataTypeMeaning" disabled />
            <NumberField name="minValue" min={-999999999} max={999999999} step={1} />
            <NumberField name="maxValue" min={-999999999} max={999999999} step={1} />
            {/* <Lov noCache name="alertModel" /> */}
            <TextArea name="description" rowSpan={2} />
          </Form>
        </Col>
      </Row>
    );
  }

  // 基本信息表单--根据数据点类型判断
  @Bind()
  renderBasicInfoForm() {
    const { dataPointType } = this.state;
    if (dataPointType === STATUS_TYPE) {
      return this.renderStatusForm();
    } else if (dataPointType === POINT_TYPE) {
      return this.renderPointForm();
    } else if (dataPointType === CONTROL_INT_TYPE) {
      return this.renderControlIntForm();
    }
  }

  /**
   * 跳转到设备编辑页
   * @param deviceId
   */
  @Bind()
  backToDevicePage(deviceId) {
    this.props.history.push(`/hiot/device/manage/edit/${deviceId}`);
  }

  /**
   * 保存编辑信息
   */
  @Bind()
  async handleDataPointEditSave() {
    const { deviceId, deviceInfo } = this.state;
    try {
      const updatedData = this.dataPointEditDS.created.map(({ data }) => {
        const { alertModel: { alertModelId } = {}, ...tmpData } = data;
        return alertModelId ? { ...tmpData, alertModelId } : { ...tmpData };
      });
      const deviceManageSaveDSObj = new DataSet(deviceManageSaveDS());
      deviceManageSaveDSObj.create({
        thing: {
          ...deviceInfo,
          buyingTime: deviceInfo && moment(deviceInfo.buyingTime).format('YYYY-MM-DD HH:mm:ss'),
        },
        updateProperties: updatedData, // 数据点信息更新
      });
      const res = await deviceManageSaveDSObj.submit();
      if (!res) {
        return false;
      } else {
        this.backToDevicePage(deviceId);
      }
    } catch (err) {
      // notification.error({ message: `操作失败:${err.message}` });
    }
  }

  render() {
    const { deviceId } = this.props.match.params;
    return (
      <>
        <Header
          title={intl.get('hiot.dataPointTemplate.view.title.create.dataPoint').d('数据点编辑')}
          backPath={`/hiot/device/manage/edit/${deviceId}`}
        >
          <Button color="primary" icon="save" onClick={this.handleDataPointEditSave}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          <Card bordered={false} title={intl.get('hiot.common.view.baseInfo').d('基本信息')}>
            {this.renderBasicInfoForm()}
          </Card>
          {/* <Card
            bordered={false}
            title={intl.get('hiot.dataPointTemplate.view.title.dataPointType').d('数据点类型')}
          >
            <Row>
              <Col span={16}>
                <Form disabled dataSet={this.dataPointEditDS} columns={2}>
                  <TextField name="typeCode" />
                  <TextField name="typeName" />
                </Form>
              </Col>
            </Row>
          </Card> */}
        </Content>
      </>
    );
  }
}
