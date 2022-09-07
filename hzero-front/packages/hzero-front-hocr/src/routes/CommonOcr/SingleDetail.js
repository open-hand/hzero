/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import { Form, Button, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { BKT_OCR } from 'utils/config';

import 'react-viewer/dist/index.css';

import RenderQuota from './RenderQuota';
import RenderTaxi from './RenderTaxi';
import RenderTrain from './RenderTrain';
import RenderVat from './RenderVat';
import RenderId from './RenderId';

// const FormItem = Form.Item;
// const { TextArea } = Input;

@connect(({ commonOcr, loading, ocrRecord }) => ({
  commonOcr,
  ocrRecord,
  fetchOcrIdentifyDetailLoading: loading.effects['commonOcr/fetchOcrIdentifyDetail'], // OCR识别loading
  updateTrainRecognizeDetailLoading: loading.effects['commonOcr/updateTrainRecognizeDetail'], // OCR更新识别loading
  updateTaxiRecognizeDetailLoading: loading.effects['commonOcr/updateTaxiRecognizeDetail'], // OCR更新识别loading
  updateQuotaRecognizeDetailLoading: loading.effects['commonOcr/updateQuotaRecognizeDetail'], // OCR更新识别loading
  updateVatRecognizeDetailLoading: loading.effects['commonOcr/updateVatRecognizeDetail'], // OCR更新识别loading
  updateIdRecognizeDetailLoading: loading.effects['commonOcr/updateIdRecognizeDetail'],
  redirectImageLoading: loading.effects['ocrRecord/redirect'],
}))
@formatterCollections({ code: ['hocr.commonOcr'] })
@Form.create({ fieldNameProp: null })
export default class SingleDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: '',
      previewVisible: false,
    };
  }

  componentDidMount() {
    const {
      dispatch,
      match: {
        params: { imageUrl },
      },
    } = this.props;
    dispatch({
      type: 'commonOcr/updateState',
      payload: {
        ocrIdentifyDetail: [],
      },
    });
    dispatch({
      type: 'commonOcr/fetchOcrIdentifyDetail',
      payload: {
        imageUrl,
        code: 'INTELLIGENT_SINGLE_IMAGE',
      },
    }).then(res => {
      if (res) {
        res.forEach(item => {
          if (item.ocrType === 'UNKNOWN') {
            notification.error({
              message: JSON.parse(item.resultInfo).msg,
            });
          }
        });
        notification.success();
      }
    });
  }

  // 识别结果图预览
  @Bind()
  handlePreviewImg() {
    const {
      dispatch,
      match: {
        params: { imageUrl },
      },
    } = this.props;
    dispatch({
      type: 'ocrRecord/redirect',
      payload: {
        bucketName: BKT_OCR,
        directory: 'hocr01',
        url: imageUrl,
      },
    }).then(res => {
      if (res) {
        this.setState({
          previewImages: [
            {
              src: res,
              alt: '',
            },
          ],
          previewVisible: true,
        });
      }
    });
  }

  /**
   * 取消预览
   */
  @Bind()
  handlePreviewCancel() {
    this.setState({
      previewImages: [],
      previewVisible: false,
    });
  }

  @Bind()
  handleSaveQuota(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'commonOcr/updateQuotaRecognizeDetail',
      payload: {
        ...params,
      },
    });
  }

  @Bind()
  handleSaveTaxi(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'commonOcr/updateTaxiRecognizeDetail',
      payload: {
        ...params,
      },
    });
  }

  @Bind()
  handleSaveTrain(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'commonOcr/updateTrainRecognizeDetail',
      payload: {
        ...params,
      },
    });
  }

  @Bind()
  handleSaveVat(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'commonOcr/updateVatRecognizeDetail',
      payload: params,
    });
  }

  @Bind()
  handleSaveId(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'commonOcr/updateIdRecognizeDetail',
      payload: params,
    });
  }

  @Bind()
  handlePreview(url) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'ocrRecord/redirect',
      payload: {
        bucketName: BKT_OCR,
        directory: 'hocr01',
        url,
      },
    });
  }

  render() {
    const {
      fetchOcrIdentifyDetailLoading,
      updateTrainRecognizeDetailLoading,
      updateTaxiRecognizeDetailLoading,
      updateQuotaRecognizeDetailLoading,
      updateVatRecognizeDetailLoading,
      updateIdRecognizeDetailLoading,
      redirectImageLoading,
      commonOcr: { ocrIdentifyDetail = [] },
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    return (
      <>
        <Header
          title={intl.get('hocr.commonOcr.view.title.singleDetail').d('智能单图详情')}
          backPath="/hocr/common-ocr/list"
        >
          <Button
            icon="eye"
            type="primary"
            onClick={this.handlePreviewImg}
            loading={redirectImageLoading}
          >
            {intl.get('hzero.common.button.seeOrigin').d('原图预览')}
          </Button>
        </Header>
        <Content>
          <Viewer
            noImgDetails
            noNavbar
            scalable={false}
            changeable={false}
            visible={previewVisible}
            onClose={this.handlePreviewCancel}
            images={previewImages}
          />
          <Spin spinning={fetchOcrIdentifyDetailLoading}>
            <>
              {ocrIdentifyDetail.map(item => {
                if (item.ocrType === 'TRAIN_TICKET') {
                  return (
                    <RenderTrain
                      onSaveTrain={this.handleSaveTrain}
                      params={item.resultInfo}
                      originUrl={item.imageUrl}
                      onPreview={this.handlePreview}
                      redirectImageLoading={redirectImageLoading}
                      fetchOcrIdentifyDetailLoading={fetchOcrIdentifyDetailLoading}
                      updateTrainRecognizeDetailLoading={updateTrainRecognizeDetailLoading}
                    />
                  );
                }
                if (item.ocrType === 'QUOTA_INVOICE') {
                  return (
                    <RenderQuota
                      onSaveQuota={this.handleSaveQuota}
                      params={item.resultInfo}
                      originUrl={item.imageUrl}
                      onPreview={this.handlePreview}
                      redirectImageLoading={redirectImageLoading}
                      fetchOcrIdentifyDetailLoading={fetchOcrIdentifyDetailLoading}
                      updateTrainRecognizeDetailLoading={updateQuotaRecognizeDetailLoading}
                    />
                  );
                }
                if (item.ocrType === 'TAXI_INVOICE') {
                  return (
                    <RenderTaxi
                      onSaveTaxi={this.handleSaveTaxi}
                      params={item.resultInfo}
                      originUrl={item.imageUrl}
                      onPreview={this.handlePreview}
                      redirectImageLoading={redirectImageLoading}
                      fetchOcrIdentifyDetailLoading={fetchOcrIdentifyDetailLoading}
                      updateTrainRecognizeDetailLoading={updateTaxiRecognizeDetailLoading}
                    />
                  );
                }
                if (item.ocrType === 'VAT_INVOICE') {
                  return (
                    <RenderVat
                      onSaveVat={this.handleSaveVat}
                      params={item.resultInfo}
                      originUrl={item.imageUrl}
                      onPreview={this.handlePreview}
                      redirectImageLoading={redirectImageLoading}
                      fetchOcrIdentifyDetailLoading={fetchOcrIdentifyDetailLoading}
                      updateVatRecognizeDetailLoading={updateVatRecognizeDetailLoading}
                    />
                  );
                }
                if (item.ocrType === 'ID_CARD') {
                  return (
                    <RenderId
                      onSaveId={this.handleSaveId}
                      params={item.resultInfo}
                      originUrl={item.imageUrl}
                      onPreview={this.handlePreview}
                      redirectImageLoading={redirectImageLoading}
                      fetchOcrIdentifyDetailLoading={fetchOcrIdentifyDetailLoading}
                      updateIdRecognizeDetailLoading={updateIdRecognizeDetailLoading}
                    />
                  );
                }
                return null;
              })}
            </>
          </Spin>
        </Content>
      </>
    );
  }
}
