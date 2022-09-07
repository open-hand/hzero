/**
 * ManualInspection 手工发票查验
 * @date: 2019-8-25
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import { connect } from 'dva';
import React, { useEffect, useState } from 'react';
import Viewer from 'react-viewer';
import { Card, Col, Form, Input, Row, Select, Button, DatePicker, Spin } from 'hzero-ui';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, getDateFormat } from 'utils/utils';
import { closeTab } from 'utils/menuTab';
import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_2,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

import style from './index.less';
import 'react-viewer/dist/index.css';

import InvoiceDetail from './InvoiceDetail';

const dateFormat = getDateFormat();

const { Option } = Select;
// /**
//  * 手工发票查验
//  * @extends {Component} - React.Component
//  * @return React.element
//  */
// @connect(({ manualInspection, loading }) => ({
//   manualInspection,
//   tenantId: getCurrentOrganizationId(),
//   fetchConfigDetailLoading: loading.effects['manualInspection/fetchConfigDetail'],
//   createLoading: loading.effects['manualInspection/create'],
// }))
// @Form.create({ fieldNameProp: null })
// @formatterCollections({ code: ['hivc.manualInspection'] })
function ManualInspection(props) {
  // state = { FORM_ITEM_LAYOUT_COL: EDIT_FORM_ITEM_LAYOUT, previewImages: [], previewVisible: false };

  const {
    history,
    form,
    match: { params: { id = '' } = {} } = {},
    dispatch,
    createLoading = false,
    fetchConfigDetailLoading = false,
    manualInspection: { manualInspectionDetail = [], typeList = [] } = {},
  } = props;

  const headerTitle = intl.get('hivc.manualInspection.view.message.title.edit').d('手工发票查验');
  const { getFieldDecorator } = form;
  const invoiceAmountArr = ['01', '02', '03'];
  const checkCodeArr = ['04', '10', '11', '14'];

  const [FORM_ITEM_LAYOUT_COL, setFormItemLayoutCol] = useState(EDIT_FORM_ITEM_LAYOUT);
  const [previewImages, setPreviewImages] = useState([]);
  const [previewVisible, setPreviewVisible] = useState(false);

  /**
   * render()调用后获取数据
   */

  useEffect(() => {
    handleSearch();
    closeTab(`/hivc/inspection-history/create-detail`);
  }, []);

  /**
   * 根据路由id获取数据
   */
  const handleSearch = () => {
    const lovCodes = { typeList: 'HIVC.INVOICE_TYPE' };
    dispatch({
      type: 'manualInspection/init',
      payload: {
        lovCodes,
      },
    });
    dispatch({
      type: 'manualInspection/updateState',
      payload: {
        manualInspectionDetail: [],
      },
    });
    if (id) {
      dispatch({
        type: 'manualInspection/getDetail',
        payload: {
          resultId: id,
        },
      }).then(() => {});
      setFormItemLayoutCol(EDIT_FORM_ITEM_LAYOUT_COL_2);
    } else {
      setFormItemLayoutCol(EDIT_FORM_ITEM_LAYOUT);
    }
  };

  /**
   * 识别并查验发票
   */
  const handleCreate = () => {
    form.validateFields((err, values) => {
      if (!err) {
        const params = {
          ...values,
          invoiceDate: values.invoiceDate && values.invoiceDate.format('YYYYMMDD'),
        };
        dispatch({
          type: 'manualInspection/create',
          payload: params,
        }).then((res) => {
          if (res && res.code === '0001') {
            notification.success({
              message: res.message,
            });
            // switch (res[0].errorCode) {
            //   case '121800':
            //     notification.success({ message: res[0].message });
            //     break;
            //   default:
            //     notification.error({ message: res[0].message });
            //     break;
            // }
            if (res.data) {
              history.push({
                pathname: `/hivc/inspection-history/create-detail`,
                // search: queryString.stringify(res.data),
                payload: {
                  ...res.data,
                  type: 'manual',
                },
              });
              closeTab(`/hivc/manual-inspection/create`);
            }
          } else {
            notification.error({
              message: res.message,
            });
          }
        });
      }
    });
  };

  /**
   * 图片预览
   * @param {*} file
   */
  const handlePreview = (imgUrl) => {
    setPreviewImages([
      {
        src: imgUrl,
        alt: '', // 由于下方会显示 alt 所以这里给空字符串 file.name,
      },
    ]);
    setPreviewVisible(true);
    // this.setState({
    //   previewImages: [
    //     {
    //       src: imgUrl,
    //       alt: '', // 由于下方会显示 alt 所以这里给空字符串 file.name,
    //     },
    //   ],
    //   previewVisible: true,
    // });
  };

  /**
   * 图片预览取消
   */
  const handlePreviewCancel = () => {
    setPreviewImages([]);
    setPreviewVisible(false);
    // this.setState({
    //   previewImages: [],
    //   previewVisible: false,
    // });
  };

  /**
   * render
   * @returns React.element
   */
  return (
    <>
      <Spin spinning={fetchConfigDetailLoading} wrapperClassName={style['spin-container']}>
        <Header
          title={id ? intl.get('hivc.invoiceDetail.view.title.line').d('发票详情') : headerTitle}
          backPath={id ? '/hivc/inspection-history' : ''}
        >
          {!id && (
            <Button type="primary" icon="eye" onClick={handleCreate} loading={createLoading}>
              {intl.get('hivc.manualInspection.model.manualInspection.check').d('识别并查验')}
            </Button>
          )}
        </Header>
        <Content>
          {!id && (
            <Card
              key="invoice-header"
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>{intl.get('hivc.manualInspection.view.message.title').d('手工发票查验')}</h3>
              }
            >
              <Form className={EDIT_FORM_CLASSNAME}>
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...FORM_ITEM_LAYOUT_COL}
                      label={intl
                        .get('hivc.manualInspection.model.manualInspection.invoiceType')
                        .d('发票类型')}
                    >
                      {getFieldDecorator('invoiceType', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hivc.manualInspection.model.manualInspection.invoiceType')
                                .d('发票类型'),
                            }),
                          },
                        ],
                      })(
                        <Select
                          placeholder={intl
                            .get('hivc.manualInspection.model.manualInspection.billingDatePH')
                            .d('请选择')}
                          allowClear
                        >
                          {typeList.map((item) => (
                            <Option key={item.value} value={item.value}>
                              {item.meaning}
                            </Option>
                          ))}
                        </Select>
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hivc.manualInspection.model.manualInspection.invoiceCode')
                        .d('发票代码')}
                      {...FORM_ITEM_LAYOUT_COL}
                    >
                      {getFieldDecorator('invoiceCode', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hivc.manualInspection.model.manualInspection.invoiceCode')
                                .d('发票代码'),
                            }),
                          },
                          {
                            pattern: /^[a-zA-Z0-9]{10}([a-zA-Z0-9][a-zA-Z0-9]){0,2}$/,
                            message: intl
                              .get('hivc.manualInspection.validation.invoiceCode')
                              .d('10或12或14位数字或大小写字母'),
                          },
                        ],
                      })(
                        <Input
                          placeholder={intl
                            .get('hivc.manualInspection.model.manualInspection.invoiceCodePH')
                            .d('请输入10或12或14位数字或字母')}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hivc.manualInspection.model.manualInspection.invoiceNo')
                        .d('发票号码')}
                      {...FORM_ITEM_LAYOUT_COL}
                    >
                      {getFieldDecorator('invoiceNo', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hivc.manualInspection.model.manualInspection.invoiceNo')
                                .d('发票号码'),
                            }),
                          },
                          {
                            pattern: /^\d+$/,
                            message: intl
                              .get('hivc.manualInspection.validation.invoiceNo')
                              .d('仅能包含数字'),
                          },
                        ],
                      })(
                        <Input
                          trim
                          typeCase="upper"
                          inputChinese={false}
                          placeholder={intl
                            .get('hivc.manualInspection.model.manualInspection.invoiceNoPH')
                            .d('请输入')}
                        />
                      )}
                    </Form.Item>
                  </Col>
                </Row>
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hivc.manualInspection.model.manualInspection.invoiceDate')
                        .d('开票日期')}
                      {...FORM_ITEM_LAYOUT_COL}
                    >
                      {getFieldDecorator('invoiceDate', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hivc.manualInspection.model.manualInspection.invoiceDate')
                                .d('开票日期'),
                            }),
                          },
                        ],
                      })(
                        <DatePicker
                          placeholder={intl
                            .get('hivc.manualInspection.model.manualInspection.billingDatePH')
                            .d('请选择')}
                          format={dateFormat}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  {invoiceAmountArr.indexOf(form.getFieldValue('invoiceType')) !== -1 && (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        label={intl
                          .get('hivc.manualInspection.model.manualInspection.invoiceAmount')
                          .d('不含税金额')}
                        {...FORM_ITEM_LAYOUT_COL}
                      >
                        {getFieldDecorator('invoiceAmount', {
                          rules: [
                            {
                              required: true,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hivc.manualInspection.model.manualInspection.invoiceAmount')
                                  .d('不含税金额'),
                              }),
                            },
                            {
                              pattern: /^\d+(.\d+)?$/,
                              message: intl
                                .get('hivc.manualInspection.validation.invoiceAmount')
                                .d('仅且仅能包含数字和.'),
                            },
                          ],
                        })(
                          <Input
                            placeholder={intl
                              .get('hivc.manualInspection.model.manualInspection.invoiceAmountPH')
                              .d('请输入不含税金额')}
                            inputChinese={false}
                          />
                        )}
                      </Form.Item>
                    </Col>
                  )}
                  {checkCodeArr.indexOf(form.getFieldValue('invoiceType')) !== -1 && (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        label={intl
                          .get('hivc.manualInspection.model.manualInspection.checkCode')
                          .d('校验码')}
                        {...FORM_ITEM_LAYOUT_COL}
                      >
                        {getFieldDecorator('checkCode', {
                          rules: [
                            {
                              required: true,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hivc.manualInspection.model.manualInspection.checkCode')
                                  .d('校验码'),
                              }),
                            },
                            {
                              pattern: /^\d{6}$/,
                              message: intl
                                .get('hivc.manualInspection.validation.checkCode')
                                .d('仅能包含数字且长度为6'),
                            },
                          ],
                        })(
                          <Input
                            placeholder={intl
                              .get('hivc.manualInspection.model.manualInspection.checkCodePH')
                              .d('请输入校验码后6位')}
                            inputChinese={false}
                          />
                        )}
                      </Form.Item>
                    </Col>
                  )}
                </Row>
              </Form>
            </Card>
          )}
          {id &&
            manualInspectionDetail.map((item) => (
              <InvoiceDetail
                dispatch={dispatch}
                dataSource={item}
                onReview={handlePreview}
                key={item.checkHistResult.id}
              />
            ))}
          <Viewer
            noImgDetails
            noNavbar
            scalable={false}
            changeable={false}
            visible={previewVisible}
            onClose={handlePreviewCancel}
            images={previewImages}
          />
        </Content>
      </Spin>
    </>
  );
}

function mapStateToProps({ manualInspection, loading }) {
  return {
    manualInspection,
    tenantId: getCurrentOrganizationId(),
    fetchConfigDetailLoading: loading.effects['manualInspection/fetchConfigDetail'],
    createLoading: loading.effects['manualInspection/create'],
  };
}

function mapDispatchToProps(dispatch) {
  return { dispatch };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(
  Form.create({ fieldNameProp: null })(
    formatterCollections({ code: ['hivc.manualInspection'] })(ManualInspection)
  )
);
