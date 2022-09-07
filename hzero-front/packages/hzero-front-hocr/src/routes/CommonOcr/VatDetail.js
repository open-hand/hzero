/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import {
  Button,
  Card,
  Col,
  DatePicker,
  Divider,
  Form,
  Input,
  InputNumber,
  Row,
  Spin,
} from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { toString } from 'lodash';
import moment from 'moment';

import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import EditTable from 'components/EditTable';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { getDateFormat, getEditTableData, tableScrollWidth } from 'utils/utils';
import { BKT_OCR } from 'utils/config';

import 'react-viewer/dist/index.css';
import { numberRender } from 'utils/renderer';

import {
  DEFAULT_DATE_FORMAT,
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;
const { TextArea } = Input;
const dateTimeFormat = getDateFormat();

@connect(({ commonOcr, loading, ocrRecord }) => ({
  commonOcr,
  ocrRecord,
  fetchOcrIdentifyDetailLoading: loading.effects['commonOcr/fetchOcrIdentifyDetail'], // OCR识别loading
  updateVatRecognizeDetailLoading: loading.effects['commonOcr/updateVatRecognizeDetail'], // OCR更新识别loading
  redirectImageLoading: loading.effects['ocrRecord/redirect'],
}))
@formatterCollections({ code: ['hocr.commonOcr'] })
@Form.create({ fieldNameProp: null })
export default class VatDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: '',
      previewVisible: false,
      objectVersionNumber: 1,
    };
  }

  componentDidMount() {
    // TODO: 调增值税发票详情接口
    const {
      dispatch,
      match: {
        params: { imageUrl, vatType },
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
      payload:
        vatType === 'roll'
          ? {
              imageUrl,
              code: 'VAT_INVOICE',
              vatType,
            }
          : {
              imageUrl,
              code: 'VAT_INVOICE',
            },
    }).then((res) => {
      if (res) {
        const detailInfo = res[0] && res[0].resultInfo;
        const dataSource = detailInfo && detailInfo.itemList;
        const editDataSource = dataSource.map((item) => ({
          ...item,
          _status: 'update',
        }));
        dispatch({
          type: 'commonOcr/updateState',
          payload: {
            editDataSource,
          },
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
    }).then((res) => {
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
  handleSaveDetail() {
    const {
      form,
      history,
      commonOcr: { editDataSource = [], ocrIdentifyDetail = [] },
      dispatch,
    } = this.props;
    const { objectVersionNumber } = this.state;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        // const { recordDtlId } = this.state;
        const { issueDate, total, subtotalAmount, subtotalTax, ...rest } = fieldsValue;
        const itemList = getEditTableData(editDataSource, ['_status']);
        const itemFilterList = itemList.map((item) => ({
          ...item,
          amount: toString(item.amount),
          unitPrice: toString(item.unitPrice),
          quantity: toString(item.quantity),
          tax: toString(item.tax),
          taxRate: toString(item.taxRate),
        }));
        const detailInfo = ocrIdentifyDetail[0] && ocrIdentifyDetail[0].resultInfo;
        const resultInfo = {
          ...rest,
          total: toString(total),
          subtotalAmount: toString(subtotalAmount),
          subtotalTax: toString(subtotalTax),
          issueDate: issueDate.format(DEFAULT_DATE_FORMAT),
          itemList: itemFilterList,
          vatInvoiceHeaderId: detailInfo.vatInvoiceHeaderId,
        };
        if (itemList.length === editDataSource.length) {
          dispatch({
            type: 'commonOcr/updateVatRecognizeDetail',
            payload: {
              ...detailInfo,
              ...resultInfo,
              objectVersionNumber,
            },
          }).then((res) => {
            if (res) {
              this.setState({
                objectVersionNumber: res.objectVersionNumber,
              });
              notification.success();
              history.push(`/hocr/common-ocr/list`);
            }
          });
        }
      }
    });
  }

  @Bind()
  handleAmountChange(e) {
    const { form } = this.props;
    const subtotalTax = form.getFieldValue('subtotalTax');
    form.setFieldsValue({
      total: numberRender(e + subtotalTax, 2, false),
      totalInWords: '',
    });
  }

  @Bind()
  handleTaxChange(e) {
    const { form } = this.props;
    const subtotalAmount = form.getFieldValue('subtotalAmount');
    form.setFieldsValue({
      total: numberRender(e + subtotalAmount, 2, false),
      totalInWords: '',
    });
  }

  render() {
    const {
      form,
      fetchOcrIdentifyDetailLoading,
      updateVatRecognizeDetailLoading,
      redirectImageLoading,
      commonOcr: { ocrIdentifyDetail = [], editDataSource = [] },
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    const detailInfo = ocrIdentifyDetail[0] && ocrIdentifyDetail[0].resultInfo;
    const columns = [
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.productName').d('商品名称'),
        dataIndex: 'name',
        render: (name, record) => {
          if (record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('name', {
                  initialValue: name,
                })(<Input />)}
              </FormItem>
            );
          } else {
            return name;
          }
        },
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.specification').d('规格型号'),
        dataIndex: 'specification',
        width: 180,
        render: (specification, record) => {
          if (record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('specification', {
                  initialValue: specification,
                })(<Input />)}
              </FormItem>
            );
          } else {
            return specification;
          }
        },
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.unit').d('单位'),
        dataIndex: 'unit',
        width: 100,
        render: (unit, record) => {
          if (record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('unit', {
                  initialValue: unit,
                })(<Input />)}
              </FormItem>
            );
          } else {
            return unit;
          }
        },
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.quantity').d('数量'),
        dataIndex: 'quantity',
        width: 100,
        render: (quantity, record) => {
          if (record._status === 'update') {
            const { getFieldDecorator, setFieldsValue, getFieldValue } = record.$form;
            const unitPrice = getFieldValue('unitPrice');
            const handleQuantityChange = (e) => {
              setFieldsValue({
                amount: numberRender(e * unitPrice, 2, false),
              });
            };
            return (
              <FormItem>
                {getFieldDecorator('quantity', {
                  initialValue: quantity,
                })(<InputNumber min={0} onChange={handleQuantityChange} />)}
              </FormItem>
            );
          } else {
            return quantity;
          }
        },
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.unitPrice').d('单价'),
        dataIndex: 'unitPrice',
        width: 180,
        render: (unitPrice, record) => {
          if (record._status === 'update') {
            const { getFieldDecorator, setFieldsValue, getFieldValue } = record.$form;
            const quantity = getFieldValue('quantity');
            const handleUnitPriceChange = (e) => {
              setFieldsValue({
                amount: numberRender(e * quantity, 2, false),
              });
            };
            return (
              <FormItem>
                {getFieldDecorator('unitPrice', {
                  initialValue: numberRender(unitPrice, 8, false),
                })(
                  <InputNumber
                    step={0.00000001}
                    precision={8}
                    formatter={(value) => `￥${value}`}
                    onChange={handleUnitPriceChange}
                  />
                )}
              </FormItem>
            );
          } else {
            return unitPrice;
          }
        },
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.amount').d('金额'),
        dataIndex: 'amount',
        width: 180,
        render: (amount, record) => {
          if (record._status === 'update') {
            const { getFieldDecorator, setFieldsValue, getFieldValue } = record.$form;
            const taxRate = getFieldValue('taxRate');
            const handleAmountChange = (e) => {
              setFieldsValue({
                tax: numberRender(e * taxRate * 0.01, 2, false),
              });
            };
            return (
              <FormItem>
                {getFieldDecorator('amount', {
                  initialValue: amount,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hocr.commonOcr.model.commonOcr.amount').d('金额'),
                      }),
                    },
                  ],
                })(
                  <InputNumber
                    step={0.01}
                    precision={2}
                    formatter={(value) => `￥${value}`}
                    onChange={handleAmountChange}
                  />
                )}
              </FormItem>
            );
          } else {
            return amount;
          }
        },
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.taxRate').d('税率'),
        dataIndex: 'taxRate',
        width: 120,
        render: (taxRate, record) => {
          if (record._status === 'update') {
            const { getFieldDecorator, setFieldsValue, getFieldValue } = record.$form;
            const amount = getFieldValue('amount');
            const handleTaxRateChange = (e) => {
              setFieldsValue({
                tax: numberRender(e * amount * 0.01, 2, false),
              });
            };
            return (
              <FormItem>
                {getFieldDecorator('taxRate', {
                  initialValue: taxRate,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hocr.commonOcr.model.commonOcr.taxRate').d('税率'),
                      }),
                    },
                  ],
                })(
                  <InputNumber
                    step={0.01}
                    precision={2}
                    formatter={(value) => `${value}%`}
                    onChange={handleTaxRateChange}
                  />
                )}
              </FormItem>
            );
          } else {
            return taxRate;
          }
        },
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.tax').d('税额'),
        dataIndex: 'tax',
        width: 180,
        render: (tax, record) => {
          if (record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('tax', {
                  initialValue: tax,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hocr.commonOcr.model.commonOcr.tax').d('税额'),
                      }),
                    },
                  ],
                })(<InputNumber step={0.01} precision={2} formatter={(value) => `￥${value}`} />)}
              </FormItem>
            );
          } else {
            return tax;
          }
        },
      },
    ];
    return (
      <>
        <Header
          title={intl.get('hocr.commonOcr.view.title.vatDetail').d('增值税发票详情')}
          backPath="/hocr/common-ocr/list"
        >
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSaveDetail}
            loading={updateVatRecognizeDetailLoading || fetchOcrIdentifyDetailLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button icon="eye" onClick={this.handlePreviewImg} loading={redirectImageLoading}>
            {intl.get('hzero.common.button.see').d('预览')}
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
            <Card
              key="vat-detail"
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{intl.get('hocr.commonOcr.view.title.vatDetail').d('增值税发票详情')}</h3>}
            >
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.ocrType').d('发票种类')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('type', {
                      initialValue: detailInfo && detailInfo.type,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.code').d('发票代码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('code', {
                      initialValue: detailInfo && detailInfo.code,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.number').d('发票号码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('number', {
                      initialValue: detailInfo && detailInfo.number,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.issueDate').d('开票日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('issueDate', {
                      initialValue:
                        detailInfo && detailInfo.issueDate
                          ? moment(detailInfo.issueDate)
                          : undefined,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.issueDate')
                              .d('开票日期'),
                          }),
                        },
                      ],
                    })(
                      <DatePicker
                        showTime={{ format: DEFAULT_DATE_FORMAT }}
                        format={dateTimeFormat}
                        placeholder=""
                      />
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.checkCode').d('校验码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('checkCode', {
                      initialValue: detailInfo && detailInfo.checkCode,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.attribution').d('发票归属地')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('attribution', {
                      initialValue: detailInfo && detailInfo.attribution,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.subTotalAmount').d('合计金额')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('subtotalAmount', {
                      initialValue: detailInfo && detailInfo.subtotalAmount,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.subTotalAmount')
                              .d('合计金额'),
                          }),
                        },
                      ],
                    })(
                      <InputNumber
                        step={0.01}
                        precision={2}
                        formatter={(value) => `￥${value}`}
                        onChange={this.handleAmountChange}
                      />
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.subTotalTax').d('合计税额')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('subtotalTax', {
                      initialValue: detailInfo && detailInfo.subtotalTax,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.subTotalTax')
                              .d('合计税额'),
                          }),
                        },
                      ],
                    })(
                      <InputNumber
                        step={0.01}
                        precision={2}
                        formatter={(value) => `￥${value}`}
                        onChange={this.handleTaxChange}
                      />
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.total').d('价税合计(小写)')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('total', {
                      initialValue: detailInfo && detailInfo.total,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.total')
                              .d('价税合计(小写)'),
                          }),
                        },
                      ],
                    })(
                      <InputNumber step={0.01} precision={2} formatter={(value) => `￥${value}`} />
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.totalInWords')
                      .d('价税合计(大写)')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('totalInWords', {
                      initialValue: detailInfo && detailInfo.totalInWords,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.receiver').d('收款人')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('receiver', {
                      initialValue: detailInfo && detailInfo.receiver,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.reviewer').d('复核人')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('reviewer', {
                      initialValue: detailInfo && detailInfo.reviewer,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.issuer').d('开票人')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('issuer', {
                      initialValue: detailInfo && detailInfo.issuer,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.remarks').d('备注')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('remarks', {
                      initialValue: detailInfo && detailInfo.remarks,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.encryptionBlock').d('密码区')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('encryptionBlock', {
                      initialValue: detailInfo && detailInfo.encryptionBlock,
                    })(<TextArea rows={3} />)}
                  </Form.Item>
                </Col>
              </Row>
              <Divider />
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.buyerName').d('购方名称')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('buyerName', {
                      initialValue: detailInfo && detailInfo.buyerName,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.sellerName').d('销售方名称')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('sellerName', {
                      initialValue: detailInfo && detailInfo.sellerName,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.buyerAddress')
                      .d('购方地址及电话')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('buyerAddress', {
                      initialValue: detailInfo && detailInfo.buyerAddress,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.sellerAddress')
                      .d('销售方地址及电话')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('sellerAddress', {
                      initialValue: detailInfo && detailInfo.sellerAddress,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.buyerId').d('购方纳税人识别号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('buyerId', {
                      initialValue: detailInfo && detailInfo.buyerId,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.sellerId')
                      .d('销售方纳税人识别号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('sellerId', {
                      initialValue: detailInfo && detailInfo.sellerId,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.buyerBank')
                      .d('购方开户行及账号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('buyerBank', {
                      initialValue: detailInfo && detailInfo.buyerBank,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.sellerBank')
                      .d('销售方开户行及账号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('sellerBank', {
                      initialValue: detailInfo && detailInfo.sellerBank,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
            </Card>
            <Card
              key="vat-row"
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{intl.get('hocr.commonOcr.view.title.line').d('发票行')}</h3>}
            >
              <Row>
                <Col>
                  <EditTable
                    bordered
                    rowKey="vatInvoiceLineId"
                    columns={columns}
                    pagination={false}
                    dataSource={editDataSource}
                    scroll={{ x: tableScrollWidth(columns) }}
                  />
                </Col>
              </Row>
            </Card>
          </Spin>
        </Content>
      </>
    );
  }
}
