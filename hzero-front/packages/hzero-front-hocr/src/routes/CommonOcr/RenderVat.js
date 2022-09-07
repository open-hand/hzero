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
import { Bind } from 'lodash-decorators';
import { toString } from 'lodash';
import moment from 'moment';

import EditTable from 'components/EditTable';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { getDateFormat, getEditTableData, tableScrollWidth } from 'utils/utils';

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

@Form.create({ fieldNameProp: null })
export default class RenderVat extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: '',
      previewVisible: false,
      objectVersionNumber: 1,
      editDataSource: [],
    };
  }

  componentDidMount() {
    const { params } = this.props;
    const { itemList } = params;
    this.setState({
      editDataSource: itemList.map((item) => ({
        ...item,
        _status: 'update',
      })),
    });
  }

  // 识别结果图预览
  @Bind()
  handlePreviewImg() {
    const { onPreview, originUrl } = this.props;
    if (originUrl) {
      onPreview(originUrl).then((res) => {
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
    } else {
      notification.error({
        message: intl.get('hocr.ocrRecord.view.message.title.failedImg').d('图片加载失败'),
      });
    }
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
  handleSaveVatDetail() {
    const { form, params, onSaveVat } = this.props;
    const { editDataSource, objectVersionNumber } = this.state;
    const { itemList: originList, ...rest } = params;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const { issueDate, total, subtotalAmount, subtotalTax, ...r } = fieldsValue;
        const itemList = getEditTableData(editDataSource, ['_status']);
        const itemFilterList = itemList.map((item) => ({
          ...item,
          amount: toString(item.amount),
          unitPrice: toString(item.unitPrice),
          quantity: toString(item.quantity),
          tax: toString(item.tax),
          taxRate: toString(item.taxRate),
        }));
        const resultInfo = {
          ...r,
          total: toString(total),
          subtotalAmount: toString(subtotalAmount),
          subtotalTax: toString(subtotalTax),
          issueDate: issueDate.format(DEFAULT_DATE_FORMAT),
          itemList: itemFilterList,
          vatInvoiceHeaderId: params.vatInvoiceHeaderId,
        };
        const dataSource = {
          ...rest,
          ...resultInfo,
          objectVersionNumber,
        };
        onSaveVat(dataSource).then((res) => {
          if (res) {
            notification.success();
            this.setState({
              objectVersionNumber: res.objectVersionNumber,
            });
          }
        });
      }
    });
  }

  // @Bind()
  // handleSaveDetail() {
  //   const {
  //     form,
  //     history,
  //     params,
  //   } = this.props;
  //   const { objectVersionNumber } = this.state;
  //   form.validateFields((err, fieldsValue) => {
  //     if (!err) {
  //       // const { recordDtlId } = this.state;
  //       const { issueDate, total, subtotalAmount, subtotalTax, ...rest } = fieldsValue;
  //       const itemList = getEditTableData(editDataSource, ['_status']);
  //       const itemFilterList = itemList.map(item => ({
  //         ...item,
  //         amount: toString(item.amount),
  //         unitPrice: toString(item.unitPrice),
  //         quantity: toString(item.quantity),
  //         tax: toString(item.tax),
  //         taxRate: toString(item.taxRate),
  //       }));
  //       const detailInfo = ocrIdentifyDetail[0] && ocrIdentifyDetail[0].resultInfo;
  //       const resultInfo = {
  //         ...rest,
  //         total: toString(total),
  //         subtotalAmount: toString(subtotalAmount),
  //         subtotalTax: toString(subtotalTax),
  //         issueDate: issueDate.format(DEFAULT_DATE_FORMAT),
  //         itemList: itemFilterList,
  //         vatInvoiceHeaderId: detailInfo.vatInvoiceHeaderId,
  //       };
  //       if (itemList.length === editDataSource.length) {
  //         dispatch({
  //           type: 'commonOcr/updateVatRecognizeDetail',
  //           payload: {
  //             ...detailInfo,
  //             ...resultInfo,
  //             objectVersionNumber,
  //           },
  //         }).then(res => {
  //           if (res) {
  //             this.setState({
  //               objectVersionNumber: res.objectVersionNumber,
  //             });
  //             notification.success();
  //             history.push(`/hocr/common-ocr/list`);
  //           }
  //         });
  //       }
  //     }
  //   });
  // }

  /**
   *
   * @param {} e
   * 行内编辑-数据联动
   */

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
      params,
      form,
      fetchOcrIdentifyDetailLoading,
      updateVatRecognizeDetailLoading,
      redirectImageLoading,
    } = this.props;
    const { previewVisible, previewImages, editDataSource } = this.state;
    console.log(numberRender(0, 2, false, false));
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
                    initialValue: params && params.type,
                  })(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.code').d('发票代码')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('code', {
                    initialValue: params && params.code,
                  })(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.number').d('发票号码')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('number', {
                    initialValue: params && params.number,
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
                    initialValue: params && params.issueDate ? moment(params.issueDate) : undefined,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hocr.commonOcr.model.commonOcr.issueDate').d('开票日期'),
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
                    initialValue: params && params.checkCode,
                  })(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.attribution').d('发票归属地')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('attribution', {
                    initialValue: params && params.attribution,
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
                    initialValue: params && params.subtotalAmount,
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
                    initialValue: params && params.subtotalTax,
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
                    initialValue: params && params.total,
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
                  })(<InputNumber step={0.01} precision={2} formatter={(value) => `￥${value}`} />)}
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
                    initialValue: params && params.totalInWords,
                  })(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.receiver').d('收款人')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('receiver', {
                    initialValue: params && params.receiver,
                  })(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.reviewer').d('复核人')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('reviewer', {
                    initialValue: params && params.reviewer,
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
                    initialValue: params && params.issuer,
                  })(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.remarks').d('备注')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('remarks', {
                    initialValue: params && params.remarks,
                  })(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.encryptionBlock').d('密码区')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('encryptionBlock', {
                    initialValue: params && params.encryptionBlock,
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
                    initialValue: params && params.buyerName,
                  })(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.sellerName').d('销售方名称')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('sellerName', {
                    initialValue: params && params.sellerName,
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
                    initialValue: params && params.buyerAddress,
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
                    initialValue: params && params.sellerAddress,
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
                    initialValue: params && params.buyerId,
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
                    initialValue: params && params.sellerId,
                  })(<Input />)}
                </Form.Item>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_2_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.buyerBank').d('购方开户行及账号')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('buyerBank', {
                    initialValue: params && params.buyerBank,
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
                    initialValue: params && params.sellerBank,
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
          <Row>
            <Col style={{ display: 'flex', justifyContent: 'flex-end', marginRight: 15 }}>
              <Button
                type="eye"
                onClick={this.handlePreviewImg}
                style={{ marginRight: '6px' }}
                loading={redirectImageLoading}
              >
                {intl.get('hzero.common.button.see').d('预览')}
              </Button>
              <Button
                type="primary"
                loading={fetchOcrIdentifyDetailLoading || updateVatRecognizeDetailLoading}
                onClick={this.handleSaveVatDetail}
              >
                {intl.get('hzero.common.button.save').d('保存')}
              </Button>
            </Col>
          </Row>
        </Spin>
      </>
    );
  }
}
