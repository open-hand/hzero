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
export default class MultiDetail extends React.Component {
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
        params: { recordDetailId },
      },
    } = this.props;
    dispatch({
      type: 'ocrRecord/updateState',
      payload: {
        multiDetail: [],
      },
    });
    dispatch({
      type: 'ocrRecord/fetchMultiDetail',
      payload: {
        recordDetailId,
      },
    });
  }

  // 识别结果图预览
  @Bind()
  handlePreviewImg() {
    const {
      dispatch,
      match: {
        params: { resourceUrl1 },
      },
    } = this.props;
    dispatch({
      type: 'ocrRecord/redirect',
      payload: {
        bucketName: BKT_OCR,
        directory: 'hocr01',
        url: resourceUrl1,
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
      redirectImageLoading,
      ocrRecord: { multiDetail = [] },
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    return (
      <>
        <Header
          title={intl.get('hocr.commonOcr.view.title.multiDetail').d('一图多票详情')}
          backPath="/hocr/ocr-record/list"
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
          <Spin spinning={false}>
            {multiDetail.map(item => {
              if (item.ocrType === 'TRAIN_TICKET') {
                return (
                  <RenderTrain
                    key="train"
                    params={item.resultInfo}
                    originUrl={item.imageUrl}
                    onPreview={this.handlePreview}
                    redirectImageLoading={redirectImageLoading}
                    fetchOcrIdentifyDetailLoading={fetchOcrIdentifyDetailLoading}
                  />
                );
              }
              if (item.ocrType === 'QUOTA_INVOICE') {
                return (
                  <RenderQuota
                    key="quota"
                    params={item.resultInfo}
                    originUrl={item.imageUrl}
                    onPreview={this.handlePreview}
                    redirectImageLoading={redirectImageLoading}
                    fetchOcrIdentifyDetailLoading={fetchOcrIdentifyDetailLoading}
                  />
                );
              }
              if (item.ocrType === 'TAXI_INVOICE') {
                return (
                  <RenderTaxi
                    key="taxi"
                    params={item.resultInfo}
                    originUrl={item.imageUrl}
                    onPreview={this.handlePreview}
                    redirectImageLoading={redirectImageLoading}
                    fetchOcrIdentifyDetailLoading={fetchOcrIdentifyDetailLoading}
                  />
                );
              }
              if (item.ocrType === 'VAT_INVOICE') {
                return (
                  <RenderVat
                    key="vat"
                    params={item.resultInfo}
                    originUrl={item.imageUrl}
                    onPreview={this.handlePreview}
                    redirectImageLoading={redirectImageLoading}
                    fetchOcrIdentifyDetailLoading={fetchOcrIdentifyDetailLoading}
                  />
                );
              }
              if (item.ocrType === 'ID_CARD') {
                return (
                  <RenderId
                    key="id"
                    params={item.resultInfo}
                    originUrl={item.imageUrl}
                    onPreview={this.handlePreview}
                    redirectImageLoading={redirectImageLoading}
                    fetchOcrIdentifyDetailLoading={fetchOcrIdentifyDetailLoading}
                  />
                );
              }
              return null;
            })}
          </Spin>
        </Content>
      </>
    );
  }
}
