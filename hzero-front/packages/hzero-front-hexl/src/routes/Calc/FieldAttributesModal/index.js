import React, { Component } from 'react';
import { DataSet, Table, TextField, Button, Tabs, Form, Lov, Select } from 'choerodon-ui/pro';

import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { API_HOST, HZERO_HEXL } from 'utils/config';
import { isEmpty } from 'lodash';
import axios from 'axios';

import { templateCodeDs, configHdDs, configLnDs, hdAllDsConfig } from '../stores';

const { Column } = Table;
const { TabPane } = Tabs;
const TABLE_TYPE_MULTI_LINE = 'MULTI_LINE';
const TABLE_TYPE_SINGLE = 'SINGLE';
const COLUMN_TYPE = {
  DEFAULT: 'CHAR',
  DATE: 'DATE',
  NUMBER: 'NUMBER',
  STR: 'CHAR',
};

// 编码输入框
const excelCodeEditor = (curRecord, name) =>
  name === 'excelCode' ? (
    <TextField
      placeholder={intl.get('hzero.hexl.validation.code').d('只支持输入大写字母/数字/符号_-')}
    />
  ) : null;

export default class FieldAttributesModal extends Component {
  constructor(props) {
    super(props);

    // 模板头信息ds
    this.configHdSingleDs = new DataSet({
      ...configHdDs(),
      name: 'configHdSingleDs',
      events: {
        load: this.handleConfigHdSingleDsLoad,
      },
    });
    this.configHdSingleDs.setQueryParameter('tableType', TABLE_TYPE_SINGLE);
    this.configHdSingleDs.setQueryParameter('sheetName', this.props.sheetDS.toData()[0].sheetName);

    // 模板行信息ds
    this.configHdMultiDs = new DataSet({
      ...configHdDs(),
      name: 'configHdMultiDs',
      events: {
        load: this.handleConfigHdMultiDsLoad,
      },
    });
    this.configHdMultiDs.setQueryParameter('tableType', TABLE_TYPE_MULTI_LINE);
    this.configHdMultiDs.setQueryParameter('sheetName', this.props.sheetDS.toData()[0].sheetName);

    // 模板头信息字段ds
    this.configLnSingleDs = new DataSet({
      ...configLnDs(),
      name: 'configLnDs',
      events: {
        query: this.beforeConfigLnDsQuery,
      },
    });
    this.configLnSingleDs.setQueryParameter('tableType', TABLE_TYPE_SINGLE);

    // 模板行信息字段ds
    this.configLnMultiDs = new DataSet({
      ...configLnDs(),
      name: 'configLnMultiDs',
      events: {
        query: this.beforeConfigLnDsQuery,
      },
    });
    this.configLnMultiDs.setQueryParameter('tableType', TABLE_TYPE_MULTI_LINE);

    // 根据价目表record查头配置/按钮配置
    this.lnDataSetArr = [this.configHdSingleDs, this.configHdMultiDs];
    this.lnDataSetArr.forEach((ds) => {
      ds.setQueryParameter('templateId', this.props.record.get('templateId'));
      // ds.setQueryParameter('templateCode', this.props.record.get('templateCode'));
      // ds.setQueryParameter('description', this.props.record.get('description') || '');
      ds.query();
    });
    // 模板头及行信息 统一保存ds
    this.configHdAllDs = new DataSet({
      ...hdAllDsConfig(),
      events: {
        query: () => false,
      },
    });

    // 头信息 数据库表ds
    this.tableNameDs = new DataSet({
      autoCreate: true,
      fields: [
        {
          name: 'table',
          type: 'object',
          lovCode: 'HEXL.TABLE',
          dynamicProps: this.getTableLovProps,
        },
      ],
      events: {
        update: this.handleTableNameDsChange,
      },
    });

    // 行信息 数据库表ds
    this.tableNameLnDs = new DataSet({
      autoCreate: true,
      fields: [
        {
          name: 'table',
          type: 'object',
          lovCode: 'HEXL.TABLE',
          dynamicProps: this.getLnTableLovProps,
        },
      ],
      events: {
        update: this.handleTableNameLnDsChange,
      },
    });

    // 头信息 数据库表字段ds
    this.selectColDs = new DataSet({
      autoCreate: true,
      fields: [
        {
          name: 'columnName',
          type: 'object',
          lovCode: 'HEXL.COLUMN',
          dynamicProps: this.getColLovProps,
          multiple: true,
        },
      ],
      events: {
        update: this.handleSelectColDsChange,
      },
    });

    // 行信息 数据库表字段ds
    this.selectLnColDs = new DataSet({
      autoCreate: true,
      fields: [
        {
          name: 'columnName',
          type: 'object',
          lovCode: 'HEXL.COLUMN',
          dynamicProps: this.getLnColLovProps,
          multiple: true,
        },
      ],
      events: {
        update: this.handleLnSelectColDsChange,
      },
    });

    this.sheetNameDs = new DataSet({
      selection: 'single',
      transport: {
        read: ({ config }) => ({
          ...config,
          url: `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-templates/${this.props.record.get(
            'templateId'
          )}/sheet-names`,
          method: 'get',
        }),
      },
      autoQuery: true,
    });

    this.sheetDs = new DataSet({
      data: [
        {
          sheetName: this.props.sheetDS.toData()[0].sheetName,
          sheetCode: this.props.sheetDS.toData()[0].sheetCode,
        },
        // this.props.sheetDS.toData(),
      ],
      fields: [
        {
          name: 'sheetName',
          type: 'string',
          textField: 'sheetName',
          valueField: 'sheetName',
          options: this.sheetNameDs,
          required: true,
        },
        {
          name: 'sheetCode',
          type: 'string',
          required: true,
          maxLength: 20,
        },
      ],
      events: {
        update: this.handleSheetDsChange,
      },
    });

    // 实例化 模板ds
    this.templateDs = new DataSet({
      ...templateCodeDs(),
      fields: [
        {
          name: 'templateId',
          type: 'string',
        },
        {
          name: 'templateCode',
          type: 'string',
          label: intl.get('hzero.hexl.field.templateCode').d('模板代码'),
        },
        {
          name: 'templateVersion',
          type: 'number',
          label: intl.get('hzero.hexl.field.templateVersion').d('模板版本'),
        },
        {
          name: 'description',
          type: 'string',
          label: intl.get('hzero.hexl.field.description').d('描述'),
          maxLength: 255,
        },
        {
          name: 'callbackUrl',
          type: 'url',
          label: intl.get('hzero.hexl.field.callbackUrl').d('回调地址'),
          maxLength: 120,
        },
      ],
    });
    this.templateDs.setQueryParameter('templateId', this.props.record.get('templateId'));
  }

  componentDidMount() {
    this.props.triggerRef(this);
  }

  // 头信息hd load事件
  handleConfigHdSingleDsLoad = ({ dataSet }) => {
    const description = this.props.record.get('description') || '';
    const templateId = this.props.record.get('templateId') || '';
    const templateCode = this.props.record.get('templateCode') || '';
    if (
      dataSet &&
      dataSet.length &&
      dataSet.length === 1 &&
      dataSet.get(0) &&
      dataSet.get(0).get('configHdId')
    ) {
      dataSet.get(0).set('templateDescription', description);
      dataSet.get(0).set('templateCode', templateCode);
      this.configLnSingleDs.setQueryParameter('configHdId', dataSet.get(0).get('configHdId') || '');
      this.configLnSingleDs
        .getField('configHdId')
        .set('defaultValue', dataSet.get(0).get('configHdId') || '');
      this.configLnSingleDs.query();
    } else {
      dataSet.create({
        templateId,
        templateCode,
        description,
        templateDescription: description,
        tableType: TABLE_TYPE_SINGLE,
      });
      this.configLnSingleDs.removeAll();
      // this.configLnSingleDs.setQueryParameter('configHdId', '-1');
      // this.configLnSingleDs.query();
    }
    this.configHdSingleDs
      .getField('tableNameLov')
      .setLovPara(
        'serviceName',
        this.configHdSingleDs &&
          this.configHdSingleDs.get(0) &&
          this.configHdSingleDs.get(0).toData() &&
          this.configHdSingleDs.get(0).toData().serviceCode
      );
    this.configHdMultiDs
      .getField('tableNameLov')
      .setLovPara(
        'serviceName',
        this.configHdSingleDs &&
          this.configHdSingleDs.get(0) &&
          this.configHdSingleDs.get(0).toData() &&
          this.configHdSingleDs.get(0).toData().serviceCode
      );
  };

  // 行信息hd load事件
  handleConfigHdMultiDsLoad = ({ dataSet }) => {
    const description = this.props.record.get('description') || '';
    const templateId = this.props.record.get('templateId') || '';
    const templateCode = this.props.record.get('templateCode') || '';
    if (
      dataSet &&
      dataSet.length &&
      dataSet.length === 1 &&
      dataSet.get(0) &&
      dataSet.get(0).get('configHdId')
    ) {
      dataSet.get(0).set('description', description);
      dataSet.get(0).set('templateCode', templateCode);
      this.configLnMultiDs.setQueryParameter('configHdId', dataSet.get(0).get('configHdId') || '');
      this.configLnMultiDs
        .getField('configHdId')
        .set('defaultValue', dataSet.get(0).get('configHdId') || '');
      this.configLnMultiDs.query();
    } else {
      dataSet.create({
        templateId,
        templateCode,
        description,
        tableType: TABLE_TYPE_MULTI_LINE,
      });
      this.configLnMultiDs.removeAll();
      // this.configLnMultiDs.setQueryParameter('configHdId', '-1');
      // this.configLnMultiDs.query();
    }
  };

  beforeConfigLnDsQuery = ({ params }) => {
    if (!params.configHdId || params.configHdId === undefined) {
      notification.warning({
        message: intl.get('hzero.hexl.validation.saveHd').d('请先保存头信息'),
      });
      return false;
    }
    return true;
  };

  // 模板头自动生成行
  autoHdSingle = () => {
    if (
      this.configHdSingleDs &&
      this.configHdSingleDs.get(0) &&
      this.configHdSingleDs.get(0).toData() &&
      this.configHdSingleDs.get(0).toData().configHdId
    ) {
      axios({
        method: 'post',
        url: `${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-lns/generate?configHdId=${
          this.configHdSingleDs.get(0).toData().configHdId
        }`,
        data: {
          serviceCode: this.configHdSingleDs.get(0).get('serviceCode'),
          tableName: this.configHdSingleDs.get(0).get('tableName'),
        },
      })
        .then((res) => {
          if (res) {
            notification.success({ message: res.message });
            this.configLnSingleDs.query();
          }
        })
        .catch((err) => {
          notification.error({ message: err.message });
        });
    } else {
      notification.error({
        message: intl
          .get('hzero.hexl.notification.need.serviceTable')
          .d('请先选择保存头信息再进行自动生成'),
      });
    }
  };

  // 模板行自动生成行
  autoHdMulti = () => {
    if (
      this.configHdMultiDs &&
      this.configHdMultiDs.get(0) &&
      this.configHdMultiDs.get(0).toData() &&
      this.configHdMultiDs.get(0).toData().configHdId
    ) {
      axios({
        method: 'post',
        url: `${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-lns/generate?configHdId=${
          this.configHdMultiDs.get(0).toData().configHdId
        }`,
        data: {
          serviceCode: this.configHdSingleDs.get(0).get('serviceCode'),
          tableName: this.configHdMultiDs.get(0).get('tableName'),
        },
      })
        .then((res) => {
          if (res.failed) {
            notification.success({ message: res.message });
            this.configLnMultiDs.query();
          }
        })
        .catch((err) => {
          notification.error({ message: err.message });
        });
    } else {
      notification.error({
        message: intl
          .get('hzero.hexl.notification.need.serviceTable')
          .d('请先选择保存头信息再进行自动生成'),
      });
    }
  };

  // 头信息数据库表 lov选择事件
  handleTableNameDsChange = ({ value }) => {
    this.configHdSingleDs.get(0).set('tableName', value.table);
  };

  // 头信息数据库表 lov动态属性
  getTableLovProps = () => {
    if (this.configHdSingleDs && this.configHdSingleDs.get(0)) {
      return {
        lovPara: {
          serviceName: this.configHdSingleDs.get(0).get('serviceCode'),
        },
      };
    }
    return {};
  };

  // 头信息 数据库表lov ref
  tableLovRef = React.createRef();

  // 头信息 数据库表lov 触发器
  triggerTableLov = () => {
    if (
      this.configHdSingleDs &&
      this.configHdSingleDs.get(0) &&
      this.configHdSingleDs.get(0).toData() &&
      this.configHdSingleDs.get(0).toData().serviceCode
    ) {
      this.tableNameDs.reset();
      this.tableLovRef.current.openModal();
    } else {
      notification.error({
        message: intl.get('hzero.hexl.notification.need.serviceCode').d('请先选择服务编码'),
      });
    }
  };

  // 行信息数据库表 lov选择事件
  handleTableNameLnDsChange = ({ value }) => {
    this.configHdMultiDs.get(0).set('tableName', value.table);
  };

  // 行信息数据库表 lov动态属性
  getLnTableLovProps = () => {
    if (this.configHdSingleDs && this.configHdSingleDs.get(0)) {
      return {
        lovPara: {
          serviceName: this.configHdSingleDs.get(0).get('serviceCode'),
        },
      };
    }
    return {};
  };

  // 行信息 数据库表lov ref
  lnTableLovRef = React.createRef();

  // 行信息 数据库表lov 触发器
  triggerLnTableLov = () => {
    if (
      this.configHdSingleDs &&
      this.configHdSingleDs.get(0) &&
      this.configHdSingleDs.get(0).toData() &&
      this.configHdSingleDs.get(0).toData().serviceCode
    ) {
      this.tableNameLnDs.reset();
      this.lnTableLovRef.current.openModal();
    } else {
      notification.error({
        message: intl.get('hzero.hexl.notification.need.serviceCode').d('请先选择服务编码'),
      });
    }
  };

  // 头信息数据库字段 lov选择事件
  handleSelectColDsChange = ({ value }) => {
    const selectedArr = value;
    selectedArr.forEach((obj) => {
      const { columnName, columnComment } = obj;
      let columnType = '';
      if (!obj.javaType) {
        columnType = COLUMN_TYPE.DEFAULT;
      } else if (obj.javaType.toLowerCase().indexOf('date') >= 0) {
        columnType = COLUMN_TYPE.DATE;
      } else if (obj.javaType.toLowerCase().indexOf('long') >= 0) {
        columnType = COLUMN_TYPE.NUMBER;
      } else if (obj.javaType.toLowerCase().indexOf('string') >= 0) {
        columnType = COLUMN_TYPE.STR;
      } else {
        columnType = COLUMN_TYPE.DEFAULT;
      }
      this.configLnSingleDs.create(
        {
          columnName,
          prompt: columnComment,
          columnType,
        },
        0
      );
    });
  };

  // 头信息数据库字段 lov动态属性
  getColLovProps = () => {
    if (this.configHdSingleDs && this.configHdSingleDs.get(0)) {
      return {
        lovPara: {
          tableName: this.configHdSingleDs.get(0).get('tableName'),
          serviceName: this.configHdSingleDs.get(0).get('serviceCode'),
        },
      };
    }
    return {};
  };

  // 行信息数据库字段 lov选择事件
  handleLnSelectColDsChange = ({ value }) => {
    const selectedArr = value;
    selectedArr.forEach((obj) => {
      const { columnName, columnComment } = obj;
      let columnType = '';
      if (!obj.javaType) {
        columnType = COLUMN_TYPE.DEFAULT;
      } else if (obj.javaType.toLowerCase().indexOf('date') >= 0) {
        columnType = COLUMN_TYPE.DATE;
      } else if (obj.javaType.toLowerCase().indexOf('long') >= 0) {
        columnType = COLUMN_TYPE.NUMBER;
      } else if (obj.javaType.toLowerCase().indexOf('string') >= 0) {
        columnType = COLUMN_TYPE.STR;
      } else {
        columnType = COLUMN_TYPE.DEFAULT;
      }
      this.configLnMultiDs.create(
        {
          columnName,
          prompt: columnComment,
          columnType,
        },
        0
      );
    });
  };

  // 行信息数据库字段 lov动态属性
  getLnColLovProps = () => {
    if (
      this.configHdMultiDs &&
      this.configHdMultiDs.get(0) &&
      this.configHdSingleDs &&
      this.configHdSingleDs.get(0)
    ) {
      return {
        lovPara: {
          tableName: this.configHdMultiDs.get(0).get('tableName'),
          serviceName: this.configHdSingleDs.get(0).get('serviceCode'),
        },
      };
    }
    return {};
  };

  // 头信息 数据库字段lov ref
  selLovRef = React.createRef();

  // 头信息 数据库字段lov 触发器
  triggerSelLov = () => {
    if (
      this.configHdSingleDs &&
      this.configHdSingleDs.get(0) &&
      this.configHdSingleDs.get(0).toData() &&
      this.configHdSingleDs.get(0).toData().serviceCode &&
      this.configHdSingleDs.get(0).toData().tableName
    ) {
      this.selLovRef.current.openModal();
    } else {
      notification.error({
        message: intl
          .get('hzero.hexl.notification.need.serviceCodeAndTableName')
          .d('请先选择服务编码和数据库表名'),
      });
    }
  };

  // 行信息 数据库字段lov ref
  lnSelLovRef = React.createRef();

  // 行信息 数据库字段lov 触发器
  triggerLnSelLov = () => {
    if (
      this.configHdSingleDs &&
      this.configHdSingleDs.get(0) &&
      this.configHdSingleDs.get(0).toData() &&
      this.configHdSingleDs.get(0).toData().serviceCode &&
      this.configHdMultiDs &&
      this.configHdMultiDs.get(0) &&
      this.configHdMultiDs.get(0).toData() &&
      this.configHdMultiDs.get(0).toData().tableName
    ) {
      this.lnSelLovRef.current.openModal();
    } else {
      notification.error({
        message: intl
          .get('hzero.hexl.notification.need.serviceCodeAndTableName')
          .d('请先选择服务编码和数据库表名'),
      });
    }
  };

  handleSheetDsChange = ({ name, value }) => {
    if (name === 'sheetName') {
      if (!value) {
        notification.warning({
          message: intl
            .get('hzero.hexl.validation.beforeHandleSheetDsChange')
            .d('请选择模板sheet页'),
        });
      } else {
        this.configHdSingleDs.setQueryParameter('sheetName', value);
        this.configHdSingleDs.query();
        this.configHdMultiDs.setQueryParameter('sheetName', value);
        this.configHdMultiDs.query();
        this.sheetNameDs.query();
        // sheetCode联动
        const sheetIndex = this.sheetNameDs
          .toData()
          .findIndex((sheet) => sheet.sheetName === value);
        if (!(sheetIndex === -1)) {
          const newSheetCode = this.sheetNameDs.toData()[sheetIndex].sheetCode;
          this.sheetDs.get(0).set('sheetCode', newSheetCode);
        }
      }
    }
  };

  optionRenderer = ({ text }) => <div style={{ width: '100%' }}>{text}</div>;

  // 字段属性全部保存
  saveColumnConfig = async () => {
    try {
      const singleStatus = this.configHdSingleDs.totalCount >= 1 ? 'update' : 'create';
      const multiStatus = this.configHdMultiDs.totalCount >= 1 ? 'update' : 'create';
      this.configHdAllDs.removeAll();
      this.configHdAllDs.create({
        ...this.configHdSingleDs.toData()[0],
        excelTemplateConfigLns: this.configLnSingleDs.toJSONData(),
        ...this.sheetDs.toData()[0],
        status: singleStatus,
      });
      this.configHdAllDs.create({
        ...this.configHdMultiDs.toData()[0],
        excelTemplateConfigLns: this.configLnMultiDs.toJSONData(),
        ...this.sheetDs.toData()[0],
        status: multiStatus,
      });
      const validFlag = await Promise.all([
        this.sheetDs.validate(),
        this.configHdSingleDs.validate(),
        this.configHdMultiDs.validate(),
        this.configLnSingleDs.validate(),
        this.configLnMultiDs.validate(),
        this.templateDs.validate(),
      ]);
      const allValidFlag = !(validFlag.filter((flag) => flag !== true).length > 0);
      if (allValidFlag) {
        // const res = await Promise.all([this.configHdAllDs.submit()]);
        const single = this.configHdAllDs.toData()[0];
        const multi = this.configHdAllDs.toData()[1];
        const axiosStatus = singleStatus === 'update' ? 'put' : 'post';
        const res = await Promise.all([
          axios({
            // method: 'put',
            method: axiosStatus,
            url: `${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds/batch/template/all`,
            data: {
              excelTemplate: this.templateDs.toData()[0],
              headers: [single, multi],
            },
          }),
        ]);
        if (!isEmpty(res) && res.failed && res.message) {
          notification.error({ message: res.message });
        } else {
          notification.success({ message: res.message });
          await this.configHdSingleDs.query();
          await this.configHdMultiDs.query();
          await this.templateDs.setQueryParameter(
            'templateId',
            this.props.record.get('templateId')
          );
          await this.templateDs.query();
        }
      } else {
        notification.error({
          message: intl.get('hzero.common.notification.invalid').d('校验不通过'),
        });
      }
    } catch (err) {
      // notification.error({ message: err });
      // notification.error({
      //   message: `${intl.get('hzero.common.notification.error').d('操作失败')}:${err.message}`,
      // });
    }
  };

  serviceCodeLovChange = (record) => {
    if (record) {
      this.configHdSingleDs.getField('tableNameLov').setLovPara('serviceName', record.serviceCode);
      this.configHdMultiDs.getField('tableNameLov').setLovPara('serviceName', record.serviceCode);
    } else {
      this.configHdSingleDs.getField('tableNameLov').setLovPara('serviceName', '');
      this.configHdMultiDs.getField('tableNameLov').setLovPara('serviceName', '');
    }
  };

  render() {
    return (
      <div>
        <Form labelWidth={120} columns={3}>
          <TextField
            name="templateCode"
            dataSet={this.templateDs}
            disabled
            label={intl.get('hzero.hexl.field.templateCode').d('模板代码')}
          />
          <TextField
            name="description"
            dataSet={this.templateDs}
            label={intl.get('hzero.hexl.field.templateDescription').d('模板描述')}
          />
          <Select
            name="sheetName"
            primitiveValue="true"
            dataSet={this.sheetDs}
            optionRenderer={this.optionRenderer}
            newLine
            label={intl.get('hzero.hexl.field.sheetName').d('标签页')}
          />
          <TextField
            name="sheetCode"
            dataSet={this.sheetDs}
            label={intl.get('hzero.hexl.field.sheetCode').d('标签代码')}
          />
          <TextField
            name="callbackUrl"
            dataSet={this.templateDs}
            colSpan={2}
            newLine
            placeholder={intl
              .get('hzero.hexl.help.httpUrl')
              .d(
                '接口地址前必须添加协议，http或者https，例如：http://hzero-webexcel/v1/0/xxx ; 仅支持post请求'
              )}
            label={intl.get('hzero.hexl.field.callbackUrl').d('回调地址')}
          />
        </Form>
        <Tabs key="tabs">
          <TabPane tab={intl.get('hzero.hexl.prompt.columnConfigHd').d('模板头信息')}>
            <Form dataSet={this.configHdSingleDs} labelWidth={120} columns={6}>
              <Lov
                name="serviceCodeLov"
                newLine
                colSpan={2}
                noCache
                label={intl.get('hzero.hexl.field.serviceCode').d('服务编码')}
                onChange={this.serviceCodeLovChange}
              />
              <Lov
                name="tableNameLov"
                colSpan={3}
                label={intl.get('hzero.hexl.field.tableName').d('数据库表名')}
                // clearButton
                // suffix={<Icon type="search" onClick={this.triggerTableLov} />}
                addonAfter={
                  <>
                    <Button
                      funcType="flat"
                      icon="table"
                      onClick={this.autoHdSingle}
                      style={{ height: 26 }}
                    >
                      {intl.get('hzero.hexl.field.autoLn').d('自动生成')}
                    </Button>
                    <Button
                      funcType="flat"
                      icon="table"
                      onClick={this.triggerSelLov}
                      style={{ height: 26 }}
                    >
                      {intl.get('hzero.hexl.field.selectColumn').d('选择字段')}
                    </Button>
                  </>
                }
              />
            </Form>
            <Lov
              dataSet={this.tableNameDs}
              name="table"
              ref={this.tableLovRef}
              hidden
              movable
              noCache
            />
            <Lov
              dataSet={this.selectColDs}
              name="columnName"
              ref={this.selLovRef}
              hidden
              movable
              noCache
            />
            <Table
              key="price-list-config-ln-single"
              highLightRow={false}
              buttons={['add', 'delete']}
              dataSet={this.configLnSingleDs}
              pagination={{ position: 'bottom' }}
            >
              <Column name="excelCode" editor={excelCodeEditor} sortable />
              <Column name="columnName" editor sortable />
              <Column name="prompt" editor sortable />
              <Column name="columnType" editor sortable />
              <Column name="precisions" editor sortable />
              <Column name="hideRowFlag" editor sortable />
              <Column name="hideColumnFlag" editor sortable />
            </Table>
          </TabPane>
          <TabPane tab={intl.get('hzero.hexl.prompt.templateHdMulti').d('模板行信息')}>
            <Form dataSet={this.configHdMultiDs} labelWidth={120} columns={6}>
              <TextField
                name="multiLineFrom"
                label={intl.get('hzero.hexl.field.multilineFrom').d('多行范围从')}
                newLine
              />
              <TextField
                name="multiLineTo"
                label={intl.get('hzero.hexl.field.multilineTo').d('多行范围到')}
              />
              <Lov
                name="tableNameLov"
                colSpan={3}
                label={intl.get('hzero.hexl.field.tableName').d('数据库表名')}
                // clearButton
                // suffix={<Icon type="search" onClick={this.triggerLnTableLov} />}
                addonAfter={
                  <>
                    <Button
                      funcType="flat"
                      icon="table"
                      onClick={this.autoHdMulti}
                      style={{ height: 26 }}
                    >
                      {intl.get('hzero.hexl.field.autoLn').d('自动生成')}
                    </Button>
                    <Button
                      funcType="flat"
                      icon="table"
                      onClick={this.triggerLnSelLov}
                      style={{ height: 26 }}
                    >
                      {intl.get('hzero.hexl.field.selectColumn').d('选择字段')}
                    </Button>
                  </>
                }
              />
            </Form>
            <Lov
              dataSet={this.tableNameLnDs}
              name="table"
              ref={this.lnTableLovRef}
              hidden
              movable
              noCache
            />
            <Lov
              dataSet={this.selectLnColDs}
              name="columnName"
              ref={this.lnSelLovRef}
              hidden
              movable
              noCache
            />
            <Table
              key="price-list-config-ln-multi"
              highLightRow={false}
              buttons={['add', 'delete']}
              dataSet={this.configLnMultiDs}
              pagination={{ position: 'bottom' }}
            >
              <Column name="excelCode" editor={excelCodeEditor} sortable />
              <Column name="columnName" editor sortable />
              <Column name="prompt" editor sortable />
              <Column name="columnType" editor sortable />
              <Column name="precisions" editor sortable />
              <Column name="hideRowFlag" editor sortable />
              <Column name="hideColumnFlag" editor sortable />
            </Table>
          </TabPane>
        </Tabs>
      </div>
    );
  }
}
