import React from 'react';
import { Button, Form, Input, Popconfirm, Select, InputNumber } from 'hzero-ui';
import { connect } from 'dva';
import { Link } from 'dva/router';
import uuid from 'uuid/v4';
import { Bind } from 'lodash-decorators';

import EditTable from 'components/EditTable';
import Lov from 'components/Lov';
import Checkbox from 'components/Checkbox';
import TLEditor from 'components/TLEditor';

import {
  addItemToPagination,
  delItemToPagination,
  isTenantRoleLevel,
  tableScrollWidth,
} from 'utils/utils';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { CODE } from 'utils/regExp';

@connect(({ template, loading }) => ({
  template,
  queryLines: loading.effects['template/queryLines'],
}))
export default class SheetTable extends React.Component {
  @Bind()
  handleCreateSheet() {
    const {
      dispatch,
      template: { templateTargetList = [], templateTargetPagination },
    } = this.props;
    dispatch({
      type: 'template/updateState',
      payload: {
        templateTargetList: [
          {
            _status: 'create',
            id: uuid(),
            sheetIndex: '',
            sheetName: '',
            datasourceCode: '',
            tableName: '',
            ruleScriptCode: '',
            enabledFlag: 1,
          },
          ...templateTargetList,
        ],
        templateTargetPagination: addItemToPagination(
          templateTargetList.length,
          templateTargetPagination
        ),
      },
    });
  }

  /**
   * handleDeleteSheet - 删除未保存的行，分页减少1
   * @param {object} record - 行数据
   */
  @Bind()
  handleDeleteSheet(record) {
    const {
      dispatch,
      template: { templateTargetList = [], templateTargetPagination },
    } = this.props;
    const newList = templateTargetList.filter((item) => item.id !== record.id);
    dispatch({
      type: 'template/updateState',
      payload: {
        templateTargetList: newList,
        pagination: delItemToPagination(templateTargetList.length, templateTargetPagination),
      },
    });
  }

  /**
   * handleSheetEdit - 设置行数据的编辑状态
   * @param {object} record - 行数据
   * @param {boolean} flag - 编辑标识
   */
  @Bind()
  handleSheetEdit(record, flag) {
    const {
      template: { templateTargetList = [] },
      dispatch,
    } = this.props;
    const newList = templateTargetList.map((item) => {
      if (record.id === item.id) {
        return { ...item, _status: flag ? 'update' : '' };
      } else {
        return item;
      }
    });
    dispatch({
      type: 'template/updateState',
      payload: { templateTargetList: newList },
    });
  }

  render() {
    const {
      template: { templateTargetList = [], code = {} },
      queryLines = false,
      detailId,
      templateType,
    } = this.props;
    const columns = [
      {
        title: intl.get('himp.template.view.title.pageNumber').d('页序号'),
        width: 200,
        dataIndex: 'sheetIndexMeaning',
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('sheetIndex', {
                  initialValue: `${record.sheetIndex}`,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('himp.template.view.title.pageNumber').d('页序号'),
                      }),
                    },
                  ],
                })(
                  <Select style={{ width: 150 }}>
                    {(code['HIMP.IMPORT_SHEET'] || []).map((n) => (
                      <Select.Option key={n.value} value={n.value}>
                        {n.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('himp.template.view.title.pageName').d('页名称'),
        width: 200,
        dataIndex: 'sheetName',
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('sheetName', {
                  initialValue: val,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('himp.template.view.title.pageName').d('页名称'),
                      }),
                    },
                  ],
                })(
                  <TLEditor
                    label={intl.get('himp.template.view.title.pageName').d('页名称')}
                    field="sheetName"
                    token={record._token}
                  />
                )}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('himp.template.view.title.dataSource').d('数据源'),
        dataIndex: 'datasourceDesc',
        width: 200,
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('datasourceCode', {
                  initialValue: record.datasourceCode,
                  rules: [
                    {
                      required: templateType === 'S',
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('himp.template.view.title.dataSource').d('数据源'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    allowClear={false}
                    textValue={record.datasourceDesc}
                    code={isTenantRoleLevel() ? 'HPFM.DATASOURCE' : 'HPFM.SITE.DATASOURCE'}
                    queryParams={{ enabledFlag: 1, dsPurposeCode: 'DI' }}
                  />
                )}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('himp.template.view.title.startLine').d('导入起始行'),
        width: 200,
        dataIndex: 'startLine',
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('startLine', {
                  initialValue: val === undefined ? 2 : val,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('himp.template.view.title.startLine').d('导入起始行'),
                      }),
                    },
                  ],
                })(<InputNumber min={2} step={1} />)}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('himp.template.view.title.officialDataTableName').d('正式数据表名'),
        dataIndex: 'tableName',
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('tableName', {
                  initialValue: val,
                  rules: [
                    {
                      required: templateType === 'S',
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('himp.template.view.title.officialDataTableName')
                          .d('正式数据表名'),
                      }),
                    },
                    {
                      pattern: CODE,
                      message: intl
                        .get('hzero.common.validation.code')
                        .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                    },
                  ],
                })(<Input trim inputChinese={false} />)}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('himp.template.view.title.validationRule').d('校验规则'),
        dataIndex: 'scriptDescription',
        width: 200,
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('ruleScriptCode', {
                  initialValue: record.ruleScriptCode,
                })(
                  <Lov
                    allowClear={false}
                    textValue={record.scriptDescription}
                    code="HIMP.IMPORT_SCRIPT"
                    queryParams={{ enabledFlag: 1 }}
                  />
                )}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get(`hzero.common.status.enable`).d('启用'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('enabledFlag', {
                  initialValue: record.enabledFlag,
                })(<Checkbox />)}
              </Form.Item>
            );
          } else {
            return enableRender(val);
          }
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 160,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          if (record._status === 'update') {
            operators.push({
              key: 'cancel',
              ele: (
                <a onClick={() => this.handleSheetEdit(record, false)}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          } else if (record._status === 'create') {
            operators.push({
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get(`himp.template.view.message.title.confirmDelete`).d('确定删除？')}
                  onConfirm={() => this.handleDeleteSheet(record)}
                  style={{ textAlign: 'center' }}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          } else {
            if (record.id !== undefined) {
              operators.push({
                key: 'delete',
                ele: (
                  <Link to={`/himp/template/column/${detailId}/${record.id}/${templateType}`}>
                    {intl.get('himp.template.model.template.editTemplateCol').d('维护模板列')}
                  </Link>
                ),
                len: 5,
                title: intl.get('himp.template.model.template.editTemplateCol').d('维护模板列'),
              });
            }
            operators.push({
              key: 'edit',
              ele: (
                <a onClick={() => this.handleSheetEdit(record, true)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <>
        <div className="table-list-operator">
          <Button onClick={this.handleCreateSheet} type="primary">
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </div>
        <EditTable
          bordered
          rowKey="id"
          loading={queryLines}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={templateTargetList}
          pagination={false}
          // FIXME: 这边没有 loadLines 方法, 也没有继承
          onChange={(page) => this.loadLines(page)}
        />
      </>
    );
  }
}
