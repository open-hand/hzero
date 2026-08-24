import React, { Component } from 'react';
import { Form, Drawer, Input, Button, Select, Alert } from 'hzero-ui';
import { Bind, Debounce } from 'lodash-decorators';
import isEmpty from 'lodash/isEmpty';
import { connect } from 'dva';

import notification from 'utils/notification';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { getSingleTenantValueCode } from '@/utils/constConfig';
import styles from './style/index.less';

const FormItem = Form.Item;
const { Option } = Select;

@Form.create({ fieldNameProp: null })
@connect(({ loading = {}, flexModel: { tables = [] } }) => ({
  tables,
  fetchTablesListLoading: loading.effects['flexModel/fetchTablesList'],
  saveModelLoading: loading.effects['flexModel/saveModel'],
}))
export default class ModalEditor extends Component {
  state = {
    serviceName: '',
    supportMultiLang: 0,
    filterTables: [],
  };

  @Bind()
  @Debounce(300)
  fetchTables(serviceName) {
    this.setState({ serviceName });
    // 更换服务时清空上次选择的表
    this.props.form.setFieldsValue({ modelTable: '' });
    if (serviceName) {
      this.props.dispatch({
        type: 'flexModel/fetchTablesList',
        params: { serviceName },
      });
    }
  }

  @Bind()
  handleSearchTable(value) {
    const { tables = [] } = this.props;
    const filterTables = tables.filter((item) => item.tableName.includes(value));
    this.setState({ filterTables });
  }

  @Bind()
  handleChangeTable(value) {
    const { form, tables } = this.props;
    const table = tables.find((item) => item.tableName === value) || {};
    const { tableName = '', tableRemarks = '', supportMultiLang } = table;
    this.setState({ supportMultiLang: supportMultiLang ? 1 : 0 });
    let modelCode = form.getFieldValue('modelCode');
    let modelName = form.getFieldValue('modelName');
    // if (!modelCode) {
    modelCode = tableName.toUpperCase();
    // }
    // if (!modelName) {
    modelName = tableRemarks || tableName; // 默认是描述，其次取表名
    // }
    form.setFieldsValue({
      modelCode,
      modelName,
    });
  }

  @Bind()
  saveModal() {
    const { form, dispatch, handleCreate } = this.props;
    const { supportMultiLang } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        const params = {
          ...values,
          supportMultiLang,
        };
        dispatch({
          type: 'flexModel/saveModel',
          params,
        }).then((res) => {
          if (!isEmpty(res)) {
            this.setState({
              serviceName: '',
              filterTables: [],
            });
            notification.success();
            handleCreate(res.modelId);
          }
        });
      }
    });
  }

  @Bind()
  closeModal() {
    this.setState({
      serviceName: '',
      filterTables: [],
    });
    this.props.handleClose();
  }

  render() {
    const { serviceName, filterTables } = this.state;
    const {
      visible,
      form: { getFieldDecorator },
      saveModelLoading,
    } = this.props;
    return (
      <Drawer
        title={intl.get('hpfm.flexModel.view.message.title.importModel').d('导入数据模型')}
        closable
        onClose={this.closeModal}
        visible={visible}
        destroyOnClose
        width={400}
      >
        <Alert
          className={styles['model-editor-alert']}
          message={intl
            .get('hpfm.flexModel.view.message.title.alertMessage')
            .d('导入指定服务下的某张表的数据模型')}
          type="info"
          showIcon
        />
        <Form
          layout="vertical"
          className={styles['model-editor-form']}
          style={{ marginBottom: 50 }}
        >
          <FormItem label={intl.get('hpfm.flexModel.model.flexModel.serviceName').d('服务名称')}>
            {getFieldDecorator('serviceName', {
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.flexModel.model.flexModel.serviceName').d('服务名称'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.flexModel.model.flexModel.serviceName')
                        .d('服务名称')}不能为空`
                    ),
                },
              ],
            })(
              <Lov
                code={getSingleTenantValueCode('HCNF.ROUTE.SERVICE_CODE')}
                textValue={serviceName}
                onChange={(e) => this.fetchTables(e)}
              />
            )}
          </FormItem>
          <FormItem label={intl.get('hpfm.flexModel.model.flexModel.modelTable').d('模型表')}>
            {getFieldDecorator('modelTable', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.flexModel.model.flexModel.modelTable').d('模型表'),
                  }),
                },
                {
                  max: 128,
                  message: intl.get('hzero.common.validation.max', {
                    max: 128,
                  }),
                },
              ],
            })(
              <Select
                allowClear
                disabled={!serviceName}
                showSearch
                defaultActiveFirstOption={false}
                showArrow={false}
                filterOption={false}
                onSearch={this.handleSearchTable}
                onChange={this.handleChangeTable}
                notFoundContent={null}
              >
                {filterTables.map((item) => (
                  <Option key={item.tableName}>{item.tableName}</Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem label={intl.get('hpfm.flexModel.model.flexModel.modelCode').d('模型编码')}>
            {getFieldDecorator('modelCode', {
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.flexModel.model.flexModel.modelCode').d('模型编码'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.flexModel.model.flexModel.modelCode')
                        .d('模型编码')}不能为空`
                    ),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
                {
                  pattern: CODE_UPPER,
                  message: intl
                    .get('hzero.common.validation.codeUpper')
                    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
              ],
            })(<Input trim inputChinese={false} typeCase="upper" />)}
          </FormItem>
          <FormItem label={intl.get('hpfm.flexModel.model.flexModel.modelName').d('模型名称')}>
            {getFieldDecorator('modelName', {
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.flexModel.model.flexModel.modelName').d('模型名称'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.flexModel.model.flexModel.modelName')
                        .d('模型名称')}不能为空`
                    ),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(<Input />)}
          </FormItem>
        </Form>
        <div className={styles['model-bottom-button']}>
          <Button onClick={this.closeModal} style={{ marginRight: 8 }} disabled={saveModelLoading}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button
            type="primary"
            loading={saveModelLoading}
            htmlType="submit"
            onClick={this.saveModal}
          >
            {intl.get('hzero.common.button.save.import').d('导入')}
          </Button>
        </div>
      </Drawer>
    );
  }
}
