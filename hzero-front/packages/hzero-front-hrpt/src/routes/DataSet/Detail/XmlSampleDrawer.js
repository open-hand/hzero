import React, { PureComponent } from 'react';
import { Modal, Form, Input, Button, Table } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { valueMapMeaning } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
// import { HZERO_RPT } from 'utils/config';
// import request from 'utils/request';
// import notification from 'utils/notification';

// import style from './index.less';
import XmlCodemirror from './XmlCodemirror';
/**
 * Form.Item 组件label、wrapper长度比例划分
 */
/**
 * 参数-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 操作对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  /**
   * state初始化
   */
  state = {};

  /**
   * 获取xml示例数据
   */
  @Bind()
  getXmlSample() {
    const { form, onGetXmlSample, queryParams = [] } = this.props;
    form.validateFields((err) => {
      const newQueryParams =
        queryParams &&
        queryParams.map((item) => ({
          name: item.name,
          dataType: item.dataType,
          defaultValue: form.getFieldValue(`defaultValue#${item.ordinal}`),
        }));
      if (isEmpty(err)) {
        onGetXmlSample(newQueryParams, form);
      }
    });
  }

  // /**
  //  * 导出xml文件
  //  */
  // @Bind()
  // getExportFile() {
  //   const { form, queryParams = [], headerForm } = this.props;
  //   form.validateFields(err => {
  //     const newQueryParams =
  //       queryParams &&
  //       queryParams.map(item => {
  //         return {
  //           name: item.name,
  //           dataType: item.dataType,
  //           defaultValue: form.getFieldValue(`defaultValue#${item.ordinal}`),
  //         };
  //       });
  //     if (isEmpty(err)) {
  //       const requestUrl = `${HZERO_RPT}/datasets/xml-sample-file`;

  //       headerForm.validateFields(
  //         ['sqlText', 'datasourceCode', 'datasetName'],
  //         (headerErr, fieldValues) => {
  //           if (isEmpty(headerErr)) {
  //             const { datasetName, ...otherFieldValues } = fieldValues;
  //             this.setState({ exportPending: true });
  //             request(requestUrl, {
  //               method: 'POST',
  //               query: {
  //                 ...otherFieldValues,
  //                 name: datasetName,
  //                 queryParams: JSON.stringify(newQueryParams),
  //               },
  //               responseType: 'blob',
  //             })
  //               .then(blob => {
  //                 // 创建隐藏的可下载链接
  //                 if (!isUndefined(blob)) {
  //                   const eleLink = document.createElement('a');
  //                   eleLink.download = 'xml导出结果.xml';
  //                   eleLink.style.display = 'none';
  //                   eleLink.href = window.URL.createObjectURL(blob);
  //                   // 触发点击
  //                   document.body.appendChild(eleLink);
  //                   eleLink.click();
  //                   // 然后移除
  //                   document.body.removeChild(eleLink);
  //                 }
  //                 this.setState({
  //                   exportPending: false,
  //                 });
  //               })
  //               .catch(error => {
  //                 notification.error({
  //                   description: error,
  //                 });
  //                 this.setState({
  //                   exportPending: false,
  //                 });
  //               });
  //           }
  //         }
  //       );
  //     }
  //   });
  // }
  /**
   * 导出文件
   */
  @Bind()
  getExportFile() {
    const { xmlSampleContent = {}, datasetCode } = this.props;
    if (!isUndefined(xmlSampleContent.content)) {
      const d = new Blob([xmlSampleContent.content], { type: 'xml' });
      if (window.navigator && window.navigator.msSaveOrOpenBlob) {
        // 判断为 IE浏览器
        window.navigator.msSaveOrOpenBlob(d, `${datasetCode}.xml`);
      } else {
        const objectURL = window.URL.createObjectURL(d);

        const link = document.createElement('a');
        // 触发点击
        link.href = objectURL;
        link.download = `${datasetCode}.xml`;
        link.click();
      }
    }
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      anchor,
      visible,
      form,
      queryParams = [],
      onCancel,
      onOk,
      dataType,
      loading,
      xmlSampleContent = {},
    } = this.props;
    const { getFieldDecorator } = form;
    const codeMirrorProps = {
      value: xmlSampleContent.content,
    };
    const columns = [
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.paramsName').d('参数名'),
        dataIndex: 'name',
        width: 150,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.dataType').d('数据类型'),
        dataIndex: 'dataType',
        width: 150,
        render: (val) => valueMapMeaning(dataType, val),
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.defaultValue').d('默认值'),
        dataIndex: 'defaultValue',
        render: (val, record) => (
          <Form.Item style={{ marginBottom: '0px' }}>
            {getFieldDecorator(`defaultValue#${record.ordinal}`, {
              initialValue: record.defaultValue,
              rules: [
                {
                  required: record.isRequired === 1,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: record.name,
                  }),
                },
              ],
            })(<Input style={{ width: '200px' }} />)}
          </Form.Item>
        ),
      },
    ];
    return (
      <Modal
        destroyOnClose
        title={intl.get('hrpt.reportDataSet.view.button.xmlSample').d('XML示例')}
        width={600}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onCancel={onCancel}
        footer={
          <Button type="primary" onClick={onOk}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>
        }
      >
        <Table
          bordered
          rowKey="ordinal"
          loading={loading}
          dataSource={queryParams}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          pagination={false}
        />
        <Form>
          <Form.Item style={{ marginTop: '14px', marginBottom: '0px' }}>
            <Button data-code="search" type="primary" htmlType="submit" onClick={this.getXmlSample}>
              {intl.get('hrpt.reportDataSet.view.button.getXmlSample').d('获取XML')}
            </Button>
            <Button
              data-code="reset"
              style={{ marginLeft: 8 }}
              onClick={() => this.getExportFile()}
              disabled={isUndefined(xmlSampleContent.content)}
            >
              {intl.get('hrpt.reportDataSet.view.button.exportFile').d('导出文件')}
            </Button>
          </Form.Item>
          <Form.Item
            label={intl.get('hrpt.reportDataSet.model.reportDataSet.xmlSample').d('XML示例')}
            labelCol={{
              span: 3,
            }}
            wrapperCol={{
              span: 24,
            }}
          >
            {getFieldDecorator(
              'xmlSample',
              {}
            )(<XmlCodemirror codeMirrorProps={codeMirrorProps} />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
