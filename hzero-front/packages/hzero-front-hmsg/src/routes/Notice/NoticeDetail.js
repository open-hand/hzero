/**
 * notice - 公告管理-详情页面
 * @date: 2018-9-20
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { isEmpty, isNil } from 'lodash';
import { Button, Cascader, Col, DatePicker, Form, Input, Row, Select, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import classnames from 'classnames';

import { Content, Header } from 'components/Page';
import UploadModal from 'components/Upload';

import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { getCurrentOrganizationId, getDateTimeFormat, isTenantRoleLevel } from 'utils/utils';
import {
  DEFAULT_DATETIME_FORMAT,
  DETAIL_EDIT_FORM_CLASSNAME,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  ROW_LAST_CLASSNAME,
  ROW_WRITE_ONLY_CLASSNAME,
} from 'utils/constants';
import { BKT_MSG, HZERO_FILE } from 'utils/config';

import StaticTextEditor from './StaticTextEditor';
import styles from './style.less';
// import SystemNoticePublishDrawer from './SystemNoticePublishDrawer';

const FormItem = Form.Item;
const { Option, OptGroup } = Select;

const formItemLayout = {
  labelCol: {
    span: 8,
  },
  wrapperCol: {
    span: 16,
  },
};

const formItemLayout2 = {
  labelCol: {
    span: 3,
  },
  wrapperCol: {
    span: 21,
  },
};

@connect(({ loading, hmsgNotice }) => ({
  hmsgNotice,
  createNoticeLoading: loading.effects['hmsgNotice/createNotice'],
  updateNoticeLoading: loading.effects['hmsgNotice/updateNotice'],
  publicNoticeLoading: loading.effects['hmsgNotice/publicNotice'],
  queryNoticeLoading: loading.effects['hmsgNotice/queryNotice'],
  organizationId: getCurrentOrganizationId(),
  isTenantRole: isTenantRoleLevel(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: 'hmsg.notice' })
export default class NoticeDetail extends React.PureComponent {
  constructor(props) {
    super(props);
    this.staticTextEditor = React.createRef();
    this.state = {
      attachmentUuid: '',
      prevContent: '',
    };
  }

  componentDidMount() {
    const {
      dispatch,
      match: {
        params: { noticeId },
      },
    } = this.props;
    dispatch({
      type: 'hmsgNotice/init',
    });
    if (noticeId !== 'create') {
      this.queryNoticeDetail({ noticeId }).then((res) => {
        // if (res && res.attachmentUuid) {
        // 如果没有 attachmentUUID 就会去请求一个新的 UUID
        if (res) {
          this.handleUuid(res);
          this.setState({ prevContent: res.noticeContent.noticeBody });
          if (res.attachmentUuid) {
            dispatch({
              type: 'hmsgNotice/queryFileList',
              payload: {
                attachmentUUID: res.attachmentUuid,
                bucketName: BKT_MSG,
                directory: 'hmsg01',
              },
            });
          }
        }
      });
    } else {
      dispatch({
        type: 'hmsgNotice/updateState',
        payload: {
          noticeDetail: {
            noticeContent: {
              noticeBody: '',
            },
          },
        },
      });
      this.handleUuid();
    }
  }

  // static getDerivedStateFromProps(nextProps, prevState) {
  //   const {
  //     hmsgNotice: {
  //       noticeDetail: {
  //         noticeContent: { noticeBody },
  //       },
  //     },
  //   } = nextProps;
  //   if ((noticeBody || '') !== prevState.prevContent) {
  //     return {
  //       prevContent: noticeBody || '',
  //     };
  //   }
  //   return null;
  // }

  /**
   * @function fetchNoticeDetail - 查询公告详情
   * @param {object} params - 查询参数
   */
  queryNoticeDetail(params = {}) {
    const {
      dispatch,
      organizationId,
      match: {
        params: { noticeId },
      },
    } = this.props;
    return dispatch({
      type: 'hmsgNotice/queryNotice',
      payload: { organizationId, noticeId, ...params },
    });
  }

  /**
   * 文件上传
   */
  @Bind()
  uploadImage(payload, file) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'hmsgNotice/uploadImage',
      payload,
      file,
    });
  }

  /**
   * 返回 Promise, 数据校验失败 reject(), 新建 resolve('create'), 保存 resolve('update');
   * @function handleCreateNotice - 保存公告信息
   */
  @Bind()
  handleCreateNotice() {
    const {
      dispatch,
      form,
      organizationId,
      hmsgNotice: { noticeDetail = {} },
      history,
      match: {
        params: { noticeId },
      },
    } = this.props;
    const { noticeContent = {} } = noticeDetail;
    // const { noticeBody = '' } = noticeContent;

    let params = {};
    let noticeBody = '';
    return new Promise((resolve, reject) => {
      form.validateFields((err, fieldsValue) => {
        if (isEmpty(err)) {
          this.getEditData().then((data) => {
            if (data.text !== undefined) {
              noticeBody = data.text;

              if (noticeDetail.noticeId) {
                params = {
                  type: 'hmsgNotice/updateNotice',
                  payload: {
                    ...noticeDetail,
                    ...fieldsValue,
                    startDate: moment(fieldsValue.startDate).format(DEFAULT_DATETIME_FORMAT),
                    endDate:
                      (fieldsValue.endDate &&
                        moment(fieldsValue.endDate).format(DEFAULT_DATETIME_FORMAT)) ||
                      '',
                    attachmentUuid: this.state.attachmentUuid,
                    receiverTypeCode: fieldsValue.receiverTypeCode[0],
                    noticeCategoryCode: fieldsValue.receiverTypeCode[1] || '',
                    noticeContent: {
                      ...noticeContent,
                      noticeBody,
                    },
                  },
                };
              } else {
                params = {
                  type: 'hmsgNotice/createNotice',
                  payload: {
                    ...fieldsValue,
                    startDate: moment(fieldsValue.startDate).format(DEFAULT_DATETIME_FORMAT),
                    endDate:
                      (fieldsValue.endDate &&
                        moment(fieldsValue.endDate).format(DEFAULT_DATETIME_FORMAT)) ||
                      '',
                    attachmentUuid: this.state.attachmentUuid,
                    receiverTypeCode: fieldsValue.receiverTypeCode[0],
                    noticeCategoryCode: fieldsValue.receiverTypeCode[1] || '',
                    tenantId: organizationId,
                    statusCode: 'DRAFT',
                    noticeContent: {
                      noticeBody,
                    },
                  },
                };
              }
              resolve(
                dispatch(params).then((res) => {
                  if (res) {
                    notification.success();
                    dispatch({
                      type: 'hmsgNotice/updateState',
                      payload: { noticeDetail: res },
                    });
                    if (noticeId === 'create') {
                      history.push(`/hmsg/notices/detail/${res.noticeId}`);
                      return 'create';
                    } else {
                      return 'update';
                    }
                  }
                })
              );
            } else {
              return notification.warning({
                message: intl
                  .get('hmsg.notice.view.message.alert.noticeContentRequired')
                  .d('请输入公告内容'),
              });
            }
          });
          // if (!noticeBody) {

          // }
        } else {
          reject();
        }
      });
    }).then((type) => {
      if (type === 'create') {
        const {
          hmsgNotice: {
            noticeDetail: { noticeId: responseNoticeId },
          },
        } = this.props;
        this.queryNoticeDetail({ noticeId: responseNoticeId }).then((res) => {
          if (res) {
            this.setState({ prevContent: res.noticeContent.noticeBody });
            this.handleUuid(res);
            if (res.attachmentUuid) {
              dispatch({
                type: 'hmsgNotice/queryFileList',
                payload: {
                  attachmentUUID: res.attachmentUuid,
                  bucketName: BKT_MSG,
                  directory: 'hmsg01',
                },
              });
            }
          }
        });
      }
      return type;
    });
  }

  /**
   * handleUuid - 获取uuid
   * @param {object} data - 报价模板头数据
   *  @param {string} data.attachmentUuid - 文件上传下载所需的uuid
   */
  @Bind()
  handleUuid(data = {}) {
    const { dispatch } = this.props;
    if (data.attachmentUuid) {
      this.setState({
        attachmentUuid: data.attachmentUuid,
      });
    } else {
      dispatch({
        type: 'hmsgNotice/fetchUuid',
      }).then((res) => {
        if (res) {
          this.setState({
            attachmentUuid: res.content,
          });
        }
      });
    }
  }

  /**
   * changeFileList - 格式化已经上传的文件列表
   * @param {array} response 请求返回的文件列表
   * @returns 格式化后的文件列表
   */
  @Bind()
  changeFileList(response) {
    return response.map((res, index) => ({
      uid: index,
      name: res.fileName,
      status: 'done',
      url: res.fileUrl,
    }));
  }

  /**
   * removeFile - 删除文件
   * @param {object} file - 删除的文件对象
   */
  @Bind()
  removeFile(file) {
    const {
      dispatch,
      hmsgNotice: { noticeDetail },
    } = this.props;
    dispatch({
      type: 'hmsgNotice/removeFile',
      payload: {
        bucketName: BKT_MSG,
        directory: 'hmsg01',
        attachmentUUID: this.state.attachmentUuid || noticeDetail.attachmentUuid,
        urls: [file.url],
      },
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  }

  @Bind()
  getEditData() {
    if (!this.staticTextEditor) {
      return Promise.reject();
    }
    return new Promise((resolve, reject) => {
      const { form } = this.props;
      form.validateFields((err, fieldsValue) => {
        if (err) {
          reject();
        } else {
          const { editor } = (this.staticTextEditor.staticTextEditor || {}).current;
          if (!editor || !editor.getData()) {
            return notification.warning({
              message: intl
                .get('hmsg.notice.view.message.alert.contentRequired')
                .d('请输入静态文本内容'),
            });
          }
          resolve({
            ...fieldsValue,
            text: editor.getData(),
          });
        }
      });
    });
  }

  @Bind()
  renderForm() {
    const {
      form,
      hmsgNotice: { noticeType = [], langList = [], noticeDetail = {}, noticeCascaderType = [] },
      queryNoticeLoading = false,
      isTenantRole,
      organizationId,
      match: {
        params: { noticeId },
      },
    } = this.props;
    const { attachmentUuid, prevContent } = this.state;
    const {
      title,
      noticeTypeCode,
      noticeTypeMeaning,
      receiverTypeCode,
      receiverTypeMeaning,
      noticeCategoryCode,
      noticeCategoryMeaning,
      lang = 'zh_CN',
      startDate,
      endDate,
    } = noticeDetail;
    const { getFieldDecorator } = form;
    return (
      <Form className={DETAIL_EDIT_FORM_CLASSNAME}>
        <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_WRITE_ONLY_CLASSNAME}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hmsg.notice.model.notice.title').d('公告标题')}
              {...formItemLayout}
            >
              {getFieldDecorator('title', {
                initialValue: queryNoticeLoading ? undefined : title,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hmsg.notice.model.notice.title').d('公告标题'),
                    }),
                  },
                  {
                    max: 100,
                    message: intl.get('hzero.common.validation.max', {
                      max: 100,
                    }),
                  },
                  {
                    validator: (rule, value, callback) => {
                      const emoji = new RegExp(/\uD83C[\uDF00-\uDFFF]|\uD83D[\uDC00-\uDE4F]/g);
                      if (value && emoji.test(value)) {
                        callback(
                          new Error(
                            intl
                              .get('hmsg.notice.view.validation.titleNotContainEmoji')
                              .d('标题不能含有表情符号')
                          )
                        );
                      } else {
                        callback();
                      }
                    },
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hmsg.notice.model.notice.receiverTypeMeaning').d('公告类型')}
              {...formItemLayout}
            >
              {getFieldDecorator('noticeTypeCode', {
                initialValue: queryNoticeLoading ? undefined : noticeTypeCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hmsg.notice.model.notice.receiverTypeMeaning').d('公告类型'),
                    }),
                  },
                ],
              })(
                isNil(noticeTypeCode) ? (
                  <Select>
                    {noticeType.map((item) => (
                      <OptGroup label={item.meaning} key={item.value}>
                        {item.children &&
                          item.children.map((items) => (
                            <Option value={items.value} key={items.value}>
                              {items.meaning}
                            </Option>
                          ))}
                      </OptGroup>
                    ))}
                  </Select>
                ) : (
                  <>{noticeTypeMeaning}</>
                )
              )}
            </FormItem>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_WRITE_ONLY_CLASSNAME}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hmsg.notice.model.notice.receiverTypeCode').d('发布对象类别')}
              {...formItemLayout}
            >
              {getFieldDecorator('receiverTypeCode', {
                // eslint-disable-next-line no-nested-ternary
                initialValue: queryNoticeLoading
                  ? []
                  : receiverTypeCode
                  ? [receiverTypeCode, noticeCategoryCode]
                  : [],
                rules: [
                  {
                    type: 'array',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hmsg.notice.model.notice.receiverTypeCode').d('发布对象类别'),
                    }),
                  },
                ],
              })(
                isNil(receiverTypeCode) ? (
                  <Cascader
                    allowClear={false}
                    options={noticeCascaderType}
                    fieldNames={{
                      label: 'meaning',
                      value: 'value',
                      children: 'children',
                    }}
                    placeholder=""
                    expandTrigger="hover"
                  />
                ) : (
                  <>{[receiverTypeMeaning, noticeCategoryMeaning].filter(Boolean).join(' > ')}</>
                )
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem label={intl.get('entity.lang.tag').d('语言')} {...formItemLayout}>
              {getFieldDecorator('lang', {
                initialValue: queryNoticeLoading ? undefined : lang,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.lang.tag').d('语言'),
                    }),
                  },
                ],
              })(
                <Select>
                  {langList.map((item) => (
                    <Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_WRITE_ONLY_CLASSNAME}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hzero.common.date.active.from').d('有效日期从')}
              {...formItemLayout}
            >
              {getFieldDecorator('startDate', {
                initialValue: queryNoticeLoading
                  ? undefined
                  : startDate && moment(startDate, DEFAULT_DATETIME_FORMAT),
                rules: [
                  {
                    type: 'object',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hzero.common.date.active.from').d('有效日期从'),
                    }),
                  },
                ],
              })(
                <DatePicker
                  style={{ width: '100%' }}
                  allowClear={false}
                  placeholder=""
                  format={getDateTimeFormat()}
                  showTime={{ format: DEFAULT_DATETIME_FORMAT }}
                  disabledDate={(current) => {
                    if (form.getFieldValue('endDate')) {
                      return moment(current).isAfter(form.getFieldValue('endDate'), 'day');
                    } else {
                      return moment(current).isBefore(moment(form.getFieldValue('endDate')), 'day');
                    }
                  }}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hzero.common.date.active.to').d('有效日期至')}
              {...formItemLayout}
            >
              {getFieldDecorator('endDate', {
                initialValue: queryNoticeLoading
                  ? undefined
                  : endDate && moment(endDate, DEFAULT_DATETIME_FORMAT),
              })(
                <DatePicker
                  style={{ width: '100%' }}
                  placeholder=""
                  format={getDateTimeFormat()}
                  disabledDate={(current) =>
                    form.getFieldValue('startDate') &&
                    moment(current).isBefore(form.getFieldValue('startDate'), 'day')
                  }
                />
              )}
            </FormItem>
          </Col>
        </Row>
        {queryNoticeLoading || (
          <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_WRITE_ONLY_CLASSNAME}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hzero.common.upload.text').d('上传附件')}
                {...formItemLayout}
              >
                <UploadModal
                  action={
                    isTenantRole
                      ? `${HZERO_FILE}/v1/${organizationId}/files/attachment/multipart`
                      : `${HZERO_FILE}/v1/files/attachment/multipart`
                  }
                  attachmentUUID={attachmentUuid}
                  bucketName={BKT_MSG}
                  bucketDirectory="hmsg01"
                />
              </FormItem>
            </Col>
          </Row>
        )}
        <Row
          {...EDIT_FORM_ROW_LAYOUT}
          className={classnames(styles['row-item2'], ROW_LAST_CLASSNAME)}
        >
          <Col style={{ width: '83%' }}>
            <FormItem
              {...formItemLayout2}
              label={intl.get('hmsg.notice.model.notice.noticeContent').d('公告内容')}
            >
              {/* {!queryNoticeLoading&&( */}
              {(noticeId === 'create' || prevContent) && (
                <StaticTextEditor
                  key={noticeId === 'create' ? 'new' : noticeId}
                  content={noticeId === 'create' ? undefined : prevContent}
                  readOnly={false}
                  onRef={(staticTextEditor) => {
                    this.staticTextEditor = staticTextEditor;
                  }}
                />
              )}
              {/* )} */}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    const {
      updateNoticeLoading = false,
      createNoticeLoading = false,
      publicNoticeLoading = false,
      queryNoticeLoading = false,
      hmsgNotice: { noticeDetail },
    } = this.props;
    return (
      <>
        <Header
          title={intl.get('hmsg.notice.view.message.title.detail').d('公告明细')}
          backPath="/hmsg/notices/list"
        >
          <Button
            icon="save"
            type="primary"
            loading={noticeDetail.noticeId ? updateNoticeLoading : createNoticeLoading}
            onClick={this.handleCreateNotice}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content className={styles['hmsg-notice']}>
          <Spin
            spinning={
              queryNoticeLoading ||
              publicNoticeLoading ||
              updateNoticeLoading ||
              createNoticeLoading
            }
          >
            {this.renderForm()}
          </Spin>
        </Content>
      </>
    );
  }
}
