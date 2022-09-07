/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { tableScrollWidth } from 'utils/utils';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { BKT_OCR } from 'utils/config';

import FilterForm from './FilterForm';
import TextDrawer from './TextDrawer';

function getRefFieldsValue(ref) {
  if (ref.current) {
    return ref.current.props.form.getFieldsValue();
  }
  return {};
}

@connect(({ ocrRecord, loading }) => ({
  ocrRecord,
  fetchOcrRecordListLoading: loading.effects['ocrRecord/fetchOcrRecordList'],
  fetchOcrRecordDetailLoading: loading.effects['ocrRecord/fetchOcrRecordDetail'],
  fetchRecognizeDetailLoading: loading.effects['ocrRecord/fetchRecognizeDetail'],
  fetchRecognizeListLoading: loading.effects['ocrRecord/fetchRecognizeList'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: 'hocr.corRecord' })
export default class OcrRecord extends React.Component {
  constructor(props) {
    super(props);
    this.oneSearchFormRef = React.createRef();
    this.state = {
      textVisible: false,
      textUrl: '',
    };
  }

  form;

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'ocrRecord/init',
    });
    this.fetchOcrRecordList();
  }

  @Bind()
  fetchOcrRecordList(params = {}) {
    const {
      dispatch,
      ocrRecord: { pagination = {} },
    } = this.props;
    dispatch({
      type: 'ocrRecord/fetchOcrRecordList',
      payload: {
        page: pagination,
        ...params,
      },
    });
  }

  /**
   * 表单查询
   */
  @Bind()
  handleOneSearchFormSearch() {
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    fieldsValue.recognizeDateFrom =
      fieldsValue.recognizeDateFrom &&
      fieldsValue.recognizeDateFrom.format(DEFAULT_DATETIME_FORMAT);
    fieldsValue.recognizeDateTo =
      fieldsValue.recognizeDateTo && fieldsValue.recognizeDateTo.format(DEFAULT_DATETIME_FORMAT);
    this.fetchOcrRecordList({
      ...fieldsValue,
      page: {},
    });
  }

  @Bind()
  handleTableChange(pagination = {}) {
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    fieldsValue.recognizeDateFrom =
      fieldsValue.recognizeDateFrom &&
      fieldsValue.recognizeDateFrom.format(DEFAULT_DATETIME_FORMAT);
    fieldsValue.recognizeDateTo =
      fieldsValue.recognizeDateTo && fieldsValue.recognizeDateTo.format(DEFAULT_DATETIME_FORMAT);
    this.fetchOcrRecordList({
      page: pagination,
      ...fieldsValue,
    });
  }

  @Bind()
  handleTextCancel() {
    this.setState({
      textVisible: false,
    });
    this.handleRefresh();
  }

  @Bind()
  handleRefresh() {
    const { dispatch } = this.props;
    dispatch({
      type: 'ocrRecord/updateState',
      payload: {
        ocrRecordDetail: {},
        recognizeList: [],
      },
    });
  }

  @Bind()
  handleFetchDetail(record) {
    const { history, dispatch } = this.props;
    if (record.recognizeType === 'BUSINESS_LICENSE') {
      history.push(
        `/hocr/ocr-record/license-detail/${record.recordDetailId}/:${encodeURIComponent(
          record.resourceUrl1
        )}`
      );
    } else if (record.recognizeType === 'ID_CARD') {
      history.push(`/hocr/ocr-record/id-detail/${record.recordDetailId}/${record.recordId}`);
    } else if (record.recognizeType === 'TAXI_INVOICE') {
      history.push(
        `/hocr/ocr-record/taxi-detail/${record.recordDetailId}/${encodeURIComponent(
          record.resourceUrl1
        )}`
      );
    } else if (record.recognizeType === 'TRAIN_TICKET') {
      history.push(
        `/hocr/ocr-record/train-detail/${record.recordDetailId}/${encodeURIComponent(
          record.resourceUrl1
        )}`
      );
    } else if (record.recognizeType === 'GENERAL_BASIC') {
      this.setState({
        textVisible: true,
      });
      dispatch({
        type: 'ocrRecord/fetchOcrRecordDetail',
        payload: {
          recordId: record.recordId,
        },
      }).then((res) => {
        if (res) {
          dispatch({
            type: 'ocrRecord/redirect',
            payload: {
              bucketName: BKT_OCR,
              directory: 'hocr01',
              url: res.resourceUrl1,
            },
          }).then((item) => {
            this.setState({
              textUrl: item,
            });
          });
        }
      });
      dispatch({
        type: 'ocrRecord/fetchTextDetail',
        payload: {
          recordDetailId: record.recordDetailId,
        },
      });
    } else if (record.recognizeType === 'VAT_INVOICE') {
      history.push(
        `/hocr/ocr-record/vat-detail/${record.recordDetailId}/:${encodeURIComponent(
          record.resourceUrl1
        )}`
      );
    } else if (record.recognizeType === 'QUOTA_INVOICE') {
      history.push(
        `/hocr/ocr-record/quota-detail/${record.recordDetailId}/:${encodeURIComponent(
          record.resourceUrl1
        )}`
      );
    } else if (record.recognizeType === 'MULTI_IMAGE') {
      history.push(
        `/hocr/ocr-record/multi-detail/${record.recordId}/:${encodeURIComponent(
          record.resourceUrl1
        )}`
      );
    }
  }

  render() {
    const {
      fetchOcrRecordListLoading,
      fetchOcrRecordDetailLoading,
      fetchRecognizeListLoading,
      ocrRecord: {
        ocrRecordList = [],
        pagination = {},
        ocrTypeList = [],
        ocrRecordDetail = {},
        textDetail = {},
      },
    } = this.props;
    const { textVisible, textUrl } = this.state;
    const textDrawerProps = {
      title: intl.get('hocr.ocrRecord.view.title.textdetail').d('文本详情'),
      visible: textVisible,
      onTextCancel: this.handleTextCancel,
      ocrRecordDetail,
      textDetail,
      fetchOcrRecordDetailLoading,
      fetchRecognizeListLoading,
      textUrl,
    };
    const columns = [
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.typeMeaning').d('识别类型'),
        width: 160,
        dataIndex: 'typeMeaning',
      },
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.fileUrl').d('文件路径'),
        width: 500,
        dataIndex: 'resourceUrl1',
      },
      {
        title: intl.get('hocr.ocrRecord.model.auditConfig.realName').d('识别人'),
        dataIndex: 'realName',
      },
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.recognizeTime').d('识别时间'),
        width: 160,
        dataIndex: 'recognizeDate',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        fixed: 'right',
        dataIndex: 'edit',
        render: (text, record) => (
          <span className="action-link">
            <a
              onClick={() => {
                this.handleFetchDetail(record);
              }}
            >
              {intl.get('hzero.common.button.detail').d('详情')}
            </a>
          </span>
        ),
      },
    ];

    return (
      <>
        <Header title={intl.get('hocr.ocrRecord.view.message.title.ocrRecord').d('OCR记录')} />
        <Content>
          <FilterForm
            wrappedComponentRef={this.oneSearchFormRef}
            onSearch={this.handleOneSearchFormSearch}
            ocrTypeList={ocrTypeList}
          />
          <Table
            bordered
            rowKey="recordId"
            loading={fetchOcrRecordListLoading}
            dataSource={ocrRecordList}
            columns={columns}
            pagination={pagination}
            onChange={this.handleTableChange}
            scroll={{ x: tableScrollWidth(columns) }}
          />
          <TextDrawer {...textDrawerProps} />
        </Content>
      </>
    );
  }
}
