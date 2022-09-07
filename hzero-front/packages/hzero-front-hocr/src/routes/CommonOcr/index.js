/**
 * OCR- 通用OCR
 * @date: 2018-9-3
 * @author: XL <liang.xiong@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Card, Col, Form, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import Icons from 'components/Icons';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';

import IdDrawer from './IdDrawer';
import TextDrawer from './TextDrawer';
import MultiDrawer from './MultiDrawer';
import VatDrawer from './VatDrawer';
import TrainDrawer from './TrainDrawer';
import LicenseDrawer from './LicenseDrawer';
import TaxiDrawer from './TaxiDrawer';
import SingleDrawer from './SingleDrawer';

@connect(({ commonOcr, loading, ocrRecord }) => ({
  commonOcr,
  ocrRecord,
  fetchOcrIdentifyDetailLoading: loading.effects['commonOcr/fetchOcrIdentifyDetail'], // OCR识别loading
  updateRecognizeDetailLoading: loading.effects['commonOcr/updateRecognizeDetail'], // OCR更新识别loading
  updateTextRecognizeDetailLoading: loading.effects['commonOcr/updateTextRecognizeDetail'],
}))
@formatterCollections({ code: ['hocr.commonOcr'] })
@Form.create({ fieldNameProp: null })
export default class CommonOcr extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      codeType: '',
      idVisible: false,
      multiVisible: false,
      licenseVisible: false,
      trainVisible: false,
      taxiVisible: false,
      textVisible: false,
      vatVisible: false,
      singleVisible: false,
      fileList: [], // 文件上传列表
      modalTitle: '',
    };
  }

  @Bind()
  handleClickId() {
    this.setState({
      codeType: 'ID_CARD',
      idVisible: true,
    });
  }

  @Bind()
  handleClickTrain() {
    this.setState({
      codeType: 'TRAIN_TICKET',
      trainVisible: true,
      modalTitle: intl.get('hocr.commonOcr.view.title.train').d('火车票识别'),
    });
  }

  @Bind()
  handleClickLicense() {
    this.setState({
      codeType: 'BUSINESS_LICENSE',
      licenseVisible: true,
      modalTitle: intl.get('hocr.commonOcr.view.title.licenseReg').d('营业执照识别'),
    });
  }

  @Bind()
  handleClickText() {
    this.setState({
      codeType: 'GENERAL_BASIC',
      textVisible: true,
    });
  }

  @Bind()
  handleClickTaxi() {
    this.setState({
      codeType: 'TAXI_INVOICE',
      taxiVisible: true,
      modalTitle: intl.get('hocr.commonOcr.view.title.taxi').d('出租车票识别'),
    });
  }

  @Bind()
  handleClickVat() {
    this.setState({
      codeType: 'VAT_INVOICE',
      vatVisible: true,
      modalTitle: intl.get('hocr.commonOcr.view.title.vatReg').d('增值税发票识别'),
    });
  }

  @Bind()
  handleClickMulti() {
    this.setState({
      codeType: 'MULTI_IMAGE',
      multiVisible: true,
    });
  }

  @Bind()
  handleClickSingle() {
    this.setState({
      codeType: 'INTELLIGENT_SINGLE_IMAGE',
      singleVisible: true,
      modalTitle: intl.get('hocr.commonOcr.view.title.singleReg').d('智能单图识别'),
    });
  }

  @Bind()
  handleCancelId() {
    this.setState({
      codeType: '',
      idVisible: false,
    });
  }

  @Bind()
  handleCancelText() {
    this.setState({
      codeType: '',
      textVisible: false,
    });
  }

  @Bind()
  handleCancelLicense() {
    this.setState({
      codeType: '',
      licenseVisible: false,
    });
  }

  @Bind()
  handleCancelTrain() {
    this.setState({
      codeType: '',
      trainVisible: false,
    });
  }

  @Bind()
  handleCancelTaxi() {
    this.setState({
      codeType: '',
      taxiVisible: false,
    });
  }

  @Bind()
  handleCancelVat() {
    this.setState({
      codeType: '',
      vatVisible: false,
    });
  }

  @Bind()
  handleCancelMulti() {
    this.setState({
      codeType: '',
      multiVisible: false,
    });
  }

  @Bind()
  handleCancelSingle() {
    this.setState({
      codeType: '',
      singleVisible: false,
    });
  }

  /**
   * 文本识别
   * @param {*} params
   */
  @Bind()
  handleTextIdentify(params) {
    const { imageUrl } = params;
    const { dispatch } = this.props;
    dispatch({
      type: 'commonOcr/fetchOcrIdentifyDetail',
      payload: {
        imageUrl,
        code: 'GENERAL_BASIC',
      },
    }).then(res => {
      if (res) {
        notification.success();
      }
    });
  }

  /**
   * 身份证识别
   * @param {*} params
   */
  @Bind()
  handleIdIdentify(params) {
    const { history } = this.props;
    const { imageUrlBack, imageUrlFront } = params;
    this.setState({
      idVisible: false,
    });
    history.push(
      `/hocr/common-ocr/id-detail/${encodeURIComponent(imageUrlBack)}/${encodeURIComponent(
        imageUrlFront
      )}`
    );
  }

  /**
   * 增值税识别
   * @param {*} params
   */
  @Bind()
  handleVatIdentify(params) {
    const { history } = this.props;
    const { imageUrl, vatType = 'notRoll' } = params;
    this.setState({
      vatVisible: false,
    });
    history.push(`/hocr/common-ocr/vat-detail/${encodeURIComponent(imageUrl)}/${vatType}`);
  }

  /**
   * 营业执照识别
   * @param {*} params
   */
  @Bind()
  handleLicenseIdentify(params) {
    const { history } = this.props;
    const { imageUrl } = params;
    this.setState({
      licenseVisible: false,
    });
    history.push(`/hocr/common-ocr/license-detail/${encodeURIComponent(imageUrl)}`);
  }

  /**
   * 出租车票识别
   * @param {*} params
   */
  @Bind()
  handleTaxiIdentify(params) {
    const { history } = this.props;
    const { imageUrl } = params;
    this.setState({
      taxiVisible: false,
    });
    history.push(`/hocr/common-ocr/taxi-detail/${encodeURIComponent(imageUrl)}`);
  }

  /**
   * 火车票识别
   * @param {*} params
   */
  @Bind()
  handleTrainIdentify(params) {
    const { imageUrl } = params;
    const { history } = this.props;
    this.setState({
      trainVisible: false,
    });
    history.push(`/hocr/common-ocr/train-detail/${encodeURIComponent(imageUrl)}`);
  }

  /**
   * 一图多票识别
   * @param {*} params
   */
  @Bind()
  handleMultiIdentify(params) {
    const { history } = this.props;
    const { imageUrl } = params;
    this.setState({
      multiVisible: false,
    });
    history.push(`/hocr/common-ocr/multi-detail/${encodeURIComponent(imageUrl)}`);
  }

  /**
   * 一图多票识别
   * @param {*} params
   */
  @Bind()
  handleSingleIdentify(params) {
    const { history } = this.props;
    const { imageUrl } = params;
    this.setState({
      singleVisible: false,
    });
    history.push(`/hocr/common-ocr/single-detail/${encodeURIComponent(imageUrl)}`);
  }

  /**
   * OCR识别-保存文本
   * @param {*} params
   */
  @Bind()
  handleSaveTextIdentify(params) {
    const { dispatch } = this.props;
    dispatch({
      type: 'commonOcr/updateTextRecognizeDetail',
      payload: {
        ...params,
      },
    }).then(res => {
      if (res) {
        notification.success();
      }
    });
  }

  render() {
    const {
      codeType,
      idVisible,
      multiVisible,
      textVisible,
      vatVisible,
      trainVisible,
      taxiVisible,
      licenseVisible,
      fileList,
      modalTitle,
      singleVisible,
    } = this.state;
    const {
      commonOcr: { ocrIdentifyDetail = [], editDataSource = [] },
      updateTextRecognizeDetail,
      fetchOcrIdentifyDetailLoading,
    } = this.props;

    const idDrawerProps = {
      title: intl.get('hocr.commonOcr.model.view.title.idIdentify').d('身份证识别'),
      visible: idVisible,
      codeType,
      onCancelId: this.handleCancelId,
      ocrIdentifyDetail,
      onOk: this.handleIdIdentify,
    };

    const textDrawerProps = {
      title: intl.get('hocr.commonOcr.model.view.title.textIdentify').d('文本识别'),
      visible: textVisible,
      codeType,
      onCancelText: this.handleCancelText,
      fileList,
      onOk: this.handleTextIdentify,
      ocrIdentifyDetail,
      onSave: this.handleSaveTextIdentify,
      editDataSource,
      updateTextRecognizeDetail,
      fetchOcrIdentifyDetailLoading,
    };

    const trainDrawerProps = {
      title: modalTitle,
      visible: trainVisible,
      codeType,
      onCancelTrain: this.handleCancelTrain,
      ocrIdentifyDetail,
      onOk: this.handleTrainIdentify,
    };

    const licenseDrawerProps = {
      title: modalTitle,
      visible: licenseVisible,
      codeType,
      onCancelLicense: this.handleCancelLicense,
      onOk: this.handleLicenseIdentify,
    };

    const taxiDrawerProps = {
      title: modalTitle,
      visible: taxiVisible,
      codeType,
      onCancelTaxi: this.handleCancelTaxi,
      ocrIdentifyDetail,
      onOk: this.handleTaxiIdentify,
    };

    const vatDrawerProps = {
      title: modalTitle,
      visible: vatVisible,
      codeType,
      onCancelVat: this.handleCancelVat,
      ocrIdentifyDetail,
      onOk: this.handleVatIdentify,
    };

    const multiDrawerProps = {
      title: intl.get('hocr.commonOcr.model.view.title.intlMultiIdentify').d('智能多图识别'),
      visible: multiVisible,
      codeType,
      ocrIdentifyDetail,
      onCancelMulti: this.handleCancelMulti,
      onOk: this.handleMultiIdentify,
    };

    const singleDrawerProps = {
      title: modalTitle,
      visible: singleVisible,
      codeType,
      onCancelSingle: this.handleCancelSingle,
      ocrIdentifyDetail,
      onOk: this.handleSingleIdentify,
    };

    return (
      <>
        <Header title={intl.get('hocr.commonOcr.view.title.commonOcr').d('通用OCR')} />
        <Content>
          <div style={{ padding: '30px' }}>
            <Row gutter={24}>
              <Col span={6}>
                <Card
                  bordered
                  hoverable
                  style={{ cursor: 'pointer', textAlign: 'center', border: '3px solid #eee' }}
                  onClick={this.handleClickText}
                >
                  <Icons type="workflow1" size="80" color="#008fb3" />
                  <h2>{intl.get('hocr.commonOcr.model.commonOcr.model.text').d('文本')}</h2>
                </Card>
              </Col>
              <Col span={6}>
                <Card
                  bordered
                  hoverable
                  style={{ cursor: 'pointer', textAlign: 'center', border: '3px solid #eee' }}
                  onClick={this.handleClickLicense}
                >
                  <Icons type="license" size="80" color="#4d0080" />
                  <h2>{intl.get('hocr.commonOcr.model.commonOcr.model.license').d('营业执照')}</h2>
                </Card>
              </Col>
              <Col span={6}>
                <Card
                  bordered
                  hoverable
                  style={{ cursor: 'pointer', textAlign: 'center', border: '3px solid #eee' }}
                  onClick={this.handleClickId}
                >
                  <Icons type="id-card" size="80" color="#4dd2ff" />
                  <h2>{intl.get('hocr.commonOcr.model.commonOcr.model.idCard').d('身份证')}</h2>
                </Card>
              </Col>
              <Col span={6}>
                <Card
                  bordered
                  hoverable
                  style={{ cursor: 'pointer', textAlign: 'center', border: '3px solid #eee' }}
                  onClick={this.handleClickTaxi}
                >
                  <Icons type="taxi" size="80" color="#003d99" />
                  <h2>{intl.get('hocr.commonOcr.model.commonOcr.model.taxi').d('出租车票')}</h2>
                </Card>
              </Col>
            </Row>
            <Row gutter={24} style={{ marginTop: '24px' }}>
              <Col span={6} onClick={this.handleClick}>
                <Card
                  bordered
                  hoverable
                  style={{ cursor: 'pointer', textAlign: 'center', border: '3px solid #eee' }}
                  onClick={this.handleClickVat}
                >
                  <Icons type="vat" size="80" color="#191966" />
                  <h2>{intl.get('hocr.commonOcr.model.commonOcr.model.vat').d('增值税发票')}</h2>
                </Card>
              </Col>
              <Col span={6}>
                <Card
                  bordered
                  hoverable
                  style={{ cursor: 'pointer', textAlign: 'center', border: '3px solid #eee' }}
                  onClick={this.handleClickTrain}
                >
                  <Icons type="train" size="80" color="#006622" />
                  <h2>{intl.get('hocr.commonOcr.model.commonOcr.model.train').d('火车票')}</h2>
                </Card>
              </Col>
              <Col span={6}>
                <Card
                  bordered
                  hoverable
                  style={{ cursor: 'pointer', textAlign: 'center', border: '3px solid #eee' }}
                  onClick={this.handleClickMulti}
                >
                  <Icons type="multi" size="80" color="#666699" />
                  <h2>
                    {intl.get('hocr.commonOcr.model.commonOcr.model.intlMulti').d('智能多图')}
                  </h2>
                </Card>
              </Col>
              <Col span={6}>
                <Card
                  bordered
                  hoverable
                  style={{ cursor: 'pointer', textAlign: 'center', border: '3px solid #eee' }}
                  onClick={this.handleClickSingle}
                >
                  <Icons type="image" size="80" color="orange" />
                  <h2>
                    {intl.get('hocr.commonOcr.model.commonOcr.model.intlSingle').d('智能单图')}
                  </h2>
                </Card>
              </Col>
            </Row>
          </div>
          <IdDrawer {...idDrawerProps} />
          <MultiDrawer {...multiDrawerProps} />
          <TextDrawer {...textDrawerProps} />
          <VatDrawer {...vatDrawerProps} />
          <TrainDrawer {...trainDrawerProps} />
          <LicenseDrawer {...licenseDrawerProps} />
          <TaxiDrawer {...taxiDrawerProps} />
          <SingleDrawer {...singleDrawerProps} />
        </Content>
      </>
    );
  }
}
