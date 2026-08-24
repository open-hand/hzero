/**
 * Form - 菜单编辑-表单
 * @date: 2019-6-27
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.5
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Input, InputNumber, Select, Switch } from 'hzero-ui';
import { toSafeInteger, isEmpty } from 'lodash';

import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';
import IconsPicker from 'components/IconsPicker';

import intl from 'utils/intl';
import { CODE_LOWER } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { isTenantRoleLevel } from 'utils/utils';

import ParentDirInput from './ParentDirInput';
import DirModal from './DirModal';

const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;

const viewMessagePrompt = 'hiam.tenantMenu.view.message';
const modelPrompt = 'hiam.tenantMenu.model.tenantMenu';
const commonPrompt = 'hzero.common';
const tenantRoleLevel = isTenantRoleLevel();

@Form.create({ fieldNameProp: null })
export default class EditorForm extends PureComponent {
  constructor(props) {
    super(props);
    this.getMenuType = this.getMenuType.bind(this);
  }

  state = {
    dirModelDataSource: [],
    dirPagination: {},
    dirModelVisible: false,
    currentParentDir: {},
  };

  codeValidator(rule, value, callback) {
    const {
      handleCheckMenuDirExists = (e) => e,
      // codePrefix,
      organizationId,
      form: { getFieldsValue = (e) => e },
    } = this.props;
    const { level, type, codePrefix } = getFieldsValue();
    handleCheckMenuDirExists({
      code: `${codePrefix}.${value}`,
      level,
      type,
      tenantId: organizationId,
    }).then((res) => {
      if (res && res.failed) {
        callback(res.message);
      }
      callback();
    });
  }

  codePrefixValidator() {
    this.props.form.validateFields(['code'], { force: true });
  }

  onDirModalOk(record) {
    const {
      form: { setFieldsValue = (e) => e },
      isCopy,
    } = this.props;
    this.setState({
      currentParentDir: record,
    });
    if (isCopy && record.code) {
      setFieldsValue({ codePrefix: `${record.code}.` }, () => {
        this.props.form.validateFields(['code'], { force: true });
      });
    }
    setFieldsValue({ parentId: record.id });
  }

  openDirModal() {
    const {
      form: { getFieldValue = (e) => e },
    } = this.props;
    const params = tenantRoleLevel
      ? { size: 10, page: 0 }
      : { size: 10, page: 0, level: getFieldValue('level') };
    this.handleSearch(params);
    // handleQueryDir({
    //   size: 10,
    //   page: 0,
    //   level: getFieldValue('level') }, dirModelDataSource => {
    //     const { dataSource, pagination } = dirModelDataSource;
    //     this.setState({
    //       dirModelDataSource: dataSource,
    //       dirPagination: pagination,
    //     });
    // });
    this.setState({
      dirModelVisible: true,
    });
  }

  closeDirModal() {
    this.setState({
      dirModelVisible: false,
    });
  }

  getMenuType() {
    return [
      {
        value: 'menu',
        title: intl.get(`${viewMessagePrompt}.menu.menu`).d('菜单'),
      },
      {
        value: 'dir',
        title: intl.get(`${viewMessagePrompt}.menu.dir`).d('目录'),
      },
      {
        value: 'root',
        title: intl.get(`${viewMessagePrompt}.menu.root`).d('预置目录'),
      },
    ];
  }

  onLevelChange() {
    const {
      form: { setFields = (e) => e },
      handleSetDataSource = (e) => e,
    } = this.props;
    setFields({
      parentId: { value: undefined, error: undefined },
    });

    this.setState({
      currentParentDir: {},
    });
    handleSetDataSource({ permissions: [], parentId: null, parentName: null });
  }

  onTypeChange(type) {
    const {
      dataSource = {},
      form: { setFields = (e) => e, setFieldsValue = (e) => e },
      handleSetDataSource = (e) => e,

      menuPrefixList = [],
    } = this.props;
    const { parentName, parentId, code } = dataSource;
    setFieldsValue({
      codePrefix: type === 'root' ? menuPrefixList.length > 0 && menuPrefixList[0].value : code,
    });
    setFields({
      dir: { error: undefined },
      code: {
        value: '',
        error: undefined,
      },
      name: {},

      parentId: {
        value: parentId,
        error: undefined,
      },
      route: {},
    });
    this.setState({
      currentParentDir: {
        name: parentName,
      },
    });
    handleSetDataSource({ type, permissions: [], parentName: null });
  }

  parserSort(value) {
    return toSafeInteger(value);
  }

  handleSearch(params) {
    const {
      handleQueryDir = (e) => e,
      form: { getFieldValue = (e) => e },
    } = this.props;
    const fields = tenantRoleLevel
      ? { size: 10, page: 0 }
      : { size: 10, page: 0, level: getFieldValue('level') };
    handleQueryDir({ ...fields, ...params }, (dirModelDataSource) => {
      const { dataSource, pagination } = dirModelDataSource;
      this.setState({
        dirModelDataSource: dataSource,
        dirPagination: pagination,
      });
    });
  }

  handleDeselect(value) {
    const { menuLabels = [] } = this.props;
    if (!isEmpty(menuLabels)) {
      const temp = menuLabels.filter((item) => item.label.name === value);
      if (temp[0] && temp[0].assignType === 'A') {
        const labels = this.props.form.getFieldValue('labels');
        setTimeout(() => {
          this.props.form.setFieldsValue({
            labels,
          });
        }, 0);
      }
    }
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e, getFieldValue = (e) => e, getFieldsValue = (e) => e },
      editable,
      dataSource = {},
      dirModalLoading = false,
      menuPrefixList = [],
      menuTypeList = [],
      isCopy,
      recordCreateFlag,
      isEdit,
      siteLabelList,
      tenantLabelList,
      menuLabels,
    } = this.props;
    const {
      dirModelVisible,
      currentParentDir,
      dirModelDataSource = [],
      dirPagination = {},
    } = this.state;
    const {
      // enabledFlag = true,
      description,
      icon,
      parentId,
      tenantId = null,
      name,
      parentName,
      type = '',
      quickIndex,
      route,
      sort,
      levelPath,
      virtualFlag = 0,
      _token,
      code,
      tenantName,
    } = dataSource;
    const dirModalProps = {
      getFieldsValue,
      visible: dirModelVisible,
      onOk: this.onDirModalOk.bind(this),
      onCancel: this.closeDirModal.bind(this),
      dataSource: dirModelDataSource,
      pagination: dirPagination,
      defaultSelectedRow: {
        id: getFieldValue('parentId'),
        name: currentParentDir.name,
      },
      handleSearch: this.handleSearch.bind(this),
      loading: dirModalLoading,
    };

    // 区分行上编辑，新建，过滤类别
    let menuFilterTypeList = [...menuTypeList];
    if (!editable) {
      if (recordCreateFlag) {
        // eslint-disable-next-line no-nested-ternary
        menuFilterTypeList = isCopy
          ? type === 'dir'
            ? menuTypeList.filter((item) => item.value === 'root' || item.value === 'dir') // 复制的是自设目录时，可创建为根目录或自设目录，其他只能创建相同的类别
            : menuTypeList.filter((item) => item.value === type)
          : menuTypeList.filter((item) => item.value !== 'root');
      } else {
        menuFilterTypeList = menuTypeList.filter((item) => item.value === 'root');
      }
    }

    let parentCode;

    if (levelPath) {
      const pathArr = levelPath.split('|');
      parentCode = pathArr[pathArr.length - 2];
    }
    let initialValue;
    if (isCopy) {
      if (levelPath) {
        const pathArr = levelPath.split('|');
        parentCode = pathArr[pathArr.length - 2];
      }
      menuPrefixList.forEach((item) => {
        if (code.startsWith(item.value)) {
          parentCode = item.value;
        }
      });
      initialValue =
        type === 'root' || getFieldValue('type') === 'root'
          ? menuPrefixList.length > 0 && menuPrefixList[0].value
          : parentCode;
    } else {
      initialValue = recordCreateFlag
        ? `${code}.`
        : menuPrefixList.length > 0 && menuPrefixList[0].value;
    }

    const selectBefore = getFieldDecorator('codePrefix', {
      initialValue,
      // validateTrigger: 'onSelect',
      rules: [{ validator: this.codePrefixValidator.bind(this) }],
      // validateFirst: true,
    })(
      <Select disabled={getFieldValue('type') !== 'root'} style={{ width: 'auto' }}>
        {menuPrefixList.map((n) => (
          <Select.Option key={n.value} value={n.value}>
            {n.meaning}.
          </Select.Option>
        ))}
      </Select>
    );

    const defaultPrompt = {
      root: {
        code: intl.get(`${modelPrompt}.dirCode`).d('目录编码'),

        codeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.dirCode`).d('目录编码'),
        }),

        name: intl.get(`${modelPrompt}.dirName`).d('目录名称'),

        nameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.dirName`).d('目录名称'),
        }),
      },
      link: {
        code: intl.get(`${modelPrompt}.linkCode`).d('链接编码'),
        codeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.linkCode`).d('链接编码'),
        }),
        name: intl.get(`${modelPrompt}.linkName`).d('链接名称'),
        nameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.linkName`).d('链接名称'),
        }),
        parentName: intl.get(`${modelPrompt}.ownDir`).d('所属目录'),
        parentNameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.ownDir`).d('所属目录'),
        }),
        routeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.route`).d('路由'),
        }),
      },
      'inner-link': {
        code: intl.get(`${modelPrompt}.linkCode`).d('链接编码'),
        codeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.linkCode`).d('链接编码'),
        }),
        name: intl.get(`${modelPrompt}.linkName`).d('链接名称'),
        nameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.linkName`).d('链接名称'),
        }),
        parentName: intl.get(`${modelPrompt}.ownDir`).d('所属目录'),
        parentNameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.ownDir`).d('所属目录'),
        }),
        routeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.route`).d('路由'),
        }),
      },
      window: {
        code: intl.get(`${modelPrompt}.windowCode`).d('窗口编码'),
        codeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.windowCode`).d('窗口编码'),
        }),
        name: intl.get(`${modelPrompt}.windowName`).d('窗口名称'),
        nameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.窗口Name`).d('窗口名称'),
        }),
        parentName: intl.get(`${modelPrompt}.ownDir`).d('所属目录'),
        parentNameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.ownDir`).d('所属目录'),
        }),
        routeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.route`).d('路由'),
        }),
      },
      dir: {
        code: intl.get(`${modelPrompt}.dirCode`).d('目录编码'),

        codeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.dirCode`).d('目录编码'),
        }),

        name: intl.get(`${modelPrompt}.dirName`).d('目录名称'),

        nameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.dirName`).d('目录名称'),
        }),

        parentName: intl.get(`${modelPrompt}.parentName`).d('上级目录'),

        parentNameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.parentName`).d('上级目录'),
        }),

        routeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.route`).d('路由'),
        }),
      },
      menu: {
        code: intl.get(`${modelPrompt}.menuCode`).d('菜单编码'),

        codeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.menuCode`).d('菜单编码'),
        }),

        name: intl.get(`${modelPrompt}.menuName`).d('菜单名称'),

        nameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.menuName`).d('菜单名称'),
        }),

        parentName: intl.get(`${modelPrompt}.ownDir`).d('所属目录'),

        parentNameMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.ownDir`).d('所属目录'),
        }),

        routeMessage: intl.get(`${commonPrompt}.validation.notNull`, {
          name: intl.get(`${modelPrompt}.route`).d('路由'),
        }),
      },
    };

    return (
      <>
        <Form>
          <FormItem label={intl.get(`${modelPrompt}.type`).d('类别')} {...MODAL_FORM_ITEM_LAYOUT}>
            {getFieldDecorator('type', {
              initialValue: type,
              rules: [
                {
                  required: true,
                  message: intl.get(`${commonPrompt}.validation.notNull`, {
                    name: intl.get(`${modelPrompt}.type`).d('类别'),
                  }),
                },
              ],
            })(
              <Select disabled={editable} onChange={this.onTypeChange.bind(this)}>
                {menuFilterTypeList.map((n) => (
                  <Option key={n.value} value={n.value}>
                    {n.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </FormItem>
          {isCopy && (
            <FormItem
              label={intl.get(`${modelPrompt}.tenantId`).d('租户')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('selectedTenantId', {
                initialValue: tenantId,
                rules: [
                  {
                    required: true,
                    message: intl.get(`${commonPrompt}.validation.notNull`, {
                      name: intl.get(`${modelPrompt}.tenantId`).d('租户'),
                    }),
                  },
                ],
              })(<Lov code="HPFM.TENANT" textValue={tenantName} />)}
            </FormItem>
          )}
          <FormItem label={defaultPrompt[type].code} required {...MODAL_FORM_ITEM_LAYOUT}>
            {getFieldDecorator('code', {
              initialValue: recordCreateFlag ? undefined : code,
              ...(!editable
                ? {
                    validateFirst: true,
                    validateTrigger: 'onBlur',
                    rules: [
                      {
                        required: true,
                        message: defaultPrompt[type].codeMessage,
                      },
                      {
                        pattern: CODE_LOWER,
                        message: intl
                          .get('hzero.common.validation.codeLower')
                          .d('全小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                      },
                      {
                        max:
                          128 -
                          (getFieldValue('codePrefix') ? getFieldValue('codePrefix').length : 0) -
                          1,
                        message: intl.get('hzero.common.validation.max', {
                          max: 128,
                        }),
                      },
                      { validator: this.codeValidator.bind(this) },
                    ],
                  }
                : {}),
            })(
              <Input
                trim
                inputChinese={false}
                addonBefore={editable ? null : selectBefore}
                disabled={editable}
              />
            )}
          </FormItem>
          <FormItem label={defaultPrompt[type].name} {...MODAL_FORM_ITEM_LAYOUT}>
            {getFieldDecorator('name', {
              initialValue: name,
              rules: [
                { required: true, message: defaultPrompt[type].nameMessage },
                {
                  max: 64,
                  message: intl
                    .get(`${commonPrompt}.validation.max`, {
                      max: 64,
                    })
                    .d(`长度不能超过${64}个字符`),
                },
              ],
            })(
              <TLEditor
                label={defaultPrompt[type].name}
                field="name"
                inputSize={{ zh: 64, en: 64 }}
                token={_token}
                disabled={editable && !isEdit}
              />
            )}
          </FormItem>
          {type !== 'root' && getFieldValue('type') !== 'root' && (
            <FormItem required label={defaultPrompt[type].parentName} {...MODAL_FORM_ITEM_LAYOUT}>
              {getFieldDecorator('parentId', {
                initialValue: parentId,
                validateFirst: true,
                validate: [
                  {
                    rules: [
                      {
                        message: defaultPrompt[type].parentNameMessage,
                        required: true,
                      },
                    ],
                  },
                ],
              })(
                <ParentDirInput
                  textValue={currentParentDir.name || parentName}
                  onClick={this.openDirModal.bind(this)}
                  disabled={editable}
                />
              )}
            </FormItem>
          )}
          <FormItem
            label={intl.get(`${modelPrompt}.quickIndex`).d('快速索引')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('quickIndex', {
              initialValue: quickIndex,
              rules: [
                {
                  max: 30,
                  message: intl.get(`${commonPrompt}.validation.max`, {
                    max: 30,
                  }),
                },
                {
                  pattern: /^[a-zA-Z0-9]*$/,
                  message: intl
                    .get('hiam.tenantMenu.view.validation.quickIndex')
                    .d('快速索引只能由字母和数字组成'),
                },
              ],
            })(<Input inputChinese={false} disabled={editable && !isEdit} />)}
          </FormItem>
          <FormItem label={intl.get(`${modelPrompt}.route`).d('路由')} {...MODAL_FORM_ITEM_LAYOUT}>
            {getFieldDecorator('route', {
              initialValue: route,
              rules: [
                {
                  max: 1000,
                  message: intl
                    .get(`${commonPrompt}.validation.max`, {
                      max: 1000,
                    })
                    .d(`长度不能超过${1000}个字符`),
                },
              ],
              ...(type === 'menu' || type === 'link' || type === 'inner-link'
                ? {
                    rules: [
                      { required: true, message: defaultPrompt[type].routeMessage },
                      {
                        max: 1000,
                        message: intl
                          .get(`${commonPrompt}.validation.max`, {
                            max: 1000,
                          })
                          .d(`长度不能超过${1000}个字符`),
                      },
                    ],
                  }
                : {}),
            })(<Input disabled={editable && !isEdit} />)}
          </FormItem>
          <FormItem label={intl.get(`${modelPrompt}.sort`).d('序号')} {...MODAL_FORM_ITEM_LAYOUT}>
            {getFieldDecorator('sort', {
              initialValue: sort || 0,
            })(
              <InputNumber
                min={0}
                step={1}
                parser={this.parserSort.bind(this)}
                disabled={editable && !isEdit}
              />
            )}
          </FormItem>
          <FormItem label={intl.get(`${modelPrompt}.icon`).d('图标')} {...MODAL_FORM_ITEM_LAYOUT}>
            {getFieldDecorator('icon', {
              initialValue: icon,
            })(<IconsPicker field="icon" allowClear isButton disabled={editable && !isEdit} />)}
          </FormItem>
          {type === 'menu' && (
            <FormItem
              label={intl.get(`${modelPrompt}.virtualFlag`).d('是否虚拟菜单')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('virtualFlag', {
                initialValue: virtualFlag === 1,
                valuePropName: 'checked',
              })(<Switch disabled={editable && !isEdit} />)}
            </FormItem>
          )}
          <FormItem
            label={intl.get(`${modelPrompt}.label`).d('菜单标签')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('labels', {
              initialValue: menuLabels.map((item) => item.label.name) || [],
            })(
              <Select
                disabled={editable && !isEdit}
                mode="multiple"
                onDeselect={this.handleDeselect.bind(this)}
              >
                {(getFieldValue('level') === 'site' ? siteLabelList : tenantLabelList).map((n) => (
                  <Select.Option key={n.name} value={n.name}>
                    {n.name}
                  </Select.Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem
            label={intl.get(`${modelPrompt}.description`).d('描述')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('description', {
              initialValue: description,
              rules: [
                {
                  max: 300,
                  message: intl
                    .get(`${commonPrompt}.validation.max`, {
                      max: 300,
                    })
                    .d(`长度不能超过${300}个字符`),
                },
              ],
            })(<TextArea autosize disabled={editable && !isEdit} />)}
          </FormItem>
        </Form>
        <DirModal {...dirModalProps} />
      </>
    );
  }
}
