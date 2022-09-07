import React from 'react';
import {
  Button,
  Checkbox as OriginCheckbox,
  Col,
  Form,
  Input,
  Modal,
  // Radio,
  Row,
  Table,
  Tabs,
} from 'hzero-ui';
import { cloneDeep, isArray, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
// import uuid from 'uuid/v4';

import Checkbox from 'components/Checkbox';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { createPagination, isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

import AuthData from './AuthData';
import styles from '../index.less';

const CheckboxGroup = OriginCheckbox.Group;
// const RadioGroup = Radio.Group;

@formatterCollections({ code: ['hiam.roleManagement', 'entity.tenant'] })
@Form.create({ fieldNameProp: null })
export default class AuthDrawer extends React.Component {
  constructor(props) {
    super(props);
    const {
      roleAuth = {},
      roleAuth: { content = [] },
      roleAuthScopeCode = [],
      roleAuthScopeTypeCode = [],
    } = props;
    const selectedRowKeys = content
      .filter((item) => !!item.docEnabledFlag)
      .map((item) => item.authDocTypeId);
    this.state = {
      currentAuthScope: null, // 当前选中的范围
      authUserDefaultValue: [], // 数据为用户范围时的选中值
      selectedRow: {}, // 选中的单据行数据
      selectedRowKeys, // 选择项
      key: 'uuid', // 单据主键
      roleAuth,
      content: [],
      roleAuthScopeCode,
      roleAuthScopeTypeCode,
      authDefaultValue: [], // 权限维度默认选中的列表
      enableChangeList: [],
      msgChangeList: [],
      scopeChangeList: [],
      authChangeList: [],
      isSiteFlag: !isTenantRoleLevel(),
    };
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { selectedRowKeys: prevSelectedRowKeys } = prevState;
    const {
      roleAuth,
      roleAuth: { content = [] },
      roleAuthScopeCode,
      roleAuthScopeTypeCode,
    } = nextProps;
    // 后端返回的数据中没有唯一标识，处理数据给一个唯一标识
    const stateContent = content.map((item) => ({
      ...item,
      uuid: `${item.authDocTypeId}-${item.authScopeCode}`,
    }));
    const selectedRowKeys = isEmpty(prevSelectedRowKeys)
      ? content.filter((item) => !!item.docEnabledFlag).map((item) => item.authDocTypeId)
      : prevSelectedRowKeys;
    return {
      ...prevState,
      roleAuth,
      content: stateContent,
      selectedRowKeys,
      roleAuthScopeCode,
      roleAuthScopeTypeCode,
    };
  }

  @Bind()
  handleCancel() {
    const { onCancel = (e) => e } = this.props;
    onCancel(this.resetState);
  }

  @Bind()
  handleOk() {
    const { form, onOk = (e) => e } = this.props;
    const { scopeChangeList, authChangeList, enableChangeList } = this.state;

    form.validateFields((err, fieldsValue) => {
      const { searchDocTypeName, ...msgFlags } = fieldsValue;
      const checkedList = Object.keys(msgFlags)
        .filter((item) => !!msgFlags[item])
        .map((item) => item.split('#')[0] * 1);

      const {
        // roleAuth: { content = [] },
        key,
        content = [],
      } = this.state;
      const dataClone = cloneDeep(content);
      const currentEnableChangeList = dataClone.filter((item) =>
        item.docEnabledFlag ? false : item.authControlType === 'ONLY_CONFIG'
      );
      const newMsgChangeList = dataClone
        .filter((item) =>
          !!item.docEnabledFlag && item.msgFlag
            ? checkedList.findIndex((e) => e === item[key]) === -1
            : checkedList.findIndex((e) => e === item[key]) !== -1
        )
        .map((item) => {
          delete item.roleAuthorityLines; // eslint-disable-line
          return {
            ...item,
            msgFlag: !item.msgFlag ? 1 : 0,
          };
        });
      const data = this.createSaveData(
        [...currentEnableChangeList, ...enableChangeList],
        newMsgChangeList,
        scopeChangeList,
        authChangeList
      );
      onOk(data, this.resetState);
      // this.resetState();
    });
  }

  @Bind()
  onRow(record) {
    const { roleAuthorityLines = [] } = record;
    let authDefaultValue = [];
    if (roleAuthorityLines) {
      authDefaultValue = roleAuthorityLines
        .filter((item) => !!item.enabledFlag)
        .map((item) => item.authDimId);
    }
    const authUserDefaultValue =
      (roleAuthorityLines && [
        roleAuthorityLines
          .filter((item) => item.dimensionType === 'USER' && !!item.enabledFlag)
          .map((item) => item.authDimId)[0],
      ]) ||
      [];
    this.setState({
      authDefaultValue,
      authUserDefaultValue,
      selectedRow: record,
      currentAuthScope: record.authScopeCode,
    });
  }

  /**
   * 为当前点击行添加 class
   * @param {Object} record - 行数据
   */
  @Bind()
  addHighlight(record) {
    const { selectedRow } = this.state;
    return record.uuid === selectedRow.uuid ? styles['auth-row-hover'] : '';
  }

  /**
   * 勾选单据项列表时，添加或删除单据项
   * @param {*} selectedRowKeys - 选中的单据项列表
   */
  // @Bind()
  // onSelectChange(rowkeys = []) {
  //   const {
  //     key,
  //     enableChangeList = [],
  //     roleAuth: { content = [] },
  //   } = this.state;
  //   const otherEnableChangeList = differenceBy(enableChangeList, content, 'authDocTypeId'); // 其他页面改变启用的单据列表

  //   const dataClone = cloneDeep(content);
  //   const currentEnableChangeList = dataClone
  //     .filter(item =>
  //       item.docEnabledFlag
  //         ? rowkeys.findIndex(e => e === item[key]) === -1
  //         : rowkeys.findIndex(e => e === item[key]) !== -1
  //     )
  //     .map(item => ({
  //       ...item,
  //       docEnabledFlag: item.docEnabledFlag ? 0 : 1,
  //     }));
  //   this.setState({
  //     enableChangeList: [...otherEnableChangeList, ...currentEnableChangeList],
  //   });
  // }

  @Bind()
  handleSearch() {
    this.handlePagination();
  }

  @Bind()
  handlePagination(pagination) {
    const { form, handleQuery = (e) => e } = this.props;
    const params = {
      page: pagination ? pagination.current - 1 : 0,
      size: pagination ? pagination.pageSize : 10,
      docTypeName: form.getFieldValue('searchDocTypeName'),
    };
    handleQuery(params);
  }

  // @Bind()
  // handleDelete() {
  //   const { handleDelete = e => e } = this.props;
  //   const { selectedRow } = this.state;
  //   handleDelete(selectedRow);
  // }

  @Bind()
  resetState() {
    this.setState({
      currentAuthScope: null, // 当前选中的范围
      authUserDefaultValue: [], // 用户范围下当前选中
      selectedRow: {}, // 选中的单据行数据
      roleAuth: {},
      roleAuthScopeCode: null,
      roleAuthScopeTypeCode: null,
      authDefaultValue: [],
      enableChangeList: [],
      msgChangeList: [],
      scopeChangeList: [],
      authChangeList: [],
    });
  }

  /**
   * 消息发送变更事件
   * @param {Array} checkedList - 消息发送标志启用列表
   */
  // @Bind()
  // msgOnChange() {
  //   const { form } = this.props;
  //   form.validateFields((err, fieldsValue) => {
  //     const { searchDocTypeName, ...msgFlags } = fieldsValue;
  //     const checkedList = Object.keys(msgFlags)
  //       .filter(item => !!msgFlags[item])
  //       .map(item => item.split('#')[0] * 1);

  //     const {
  //       roleAuth: { content = [] },
  //       key,
  //       selectedRow,
  //     } = this.state;
  //     const dataClone = cloneDeep(content);

  //     const newMsgChangeList = dataClone
  //       .filter(item =>
  //         !!item.docEnabledFlag && item.msgFlag
  //           ? checkedList.findIndex(e => e === item[key]) === -1
  //           : checkedList.findIndex(e => e === item[key]) !== -1
  //       )
  //       .map(item => {
  //         delete item.roleAuthorityLines; // eslint-disable-line
  //         return {
  //           ...item,
  //           msgFlag: !item.msgFlag ? 1 : 0,
  //         };
  //       });
  //     this.setState({
  //       msgChangeList: newMsgChangeList,
  //     });

  //     const index = newMsgChangeList.findIndex(item => item[key] === selectedRow[key]);

  //     if (index !== -1) {
  //       this.setState({
  //         selectedRow: {
  //           ...selectedRow,
  //           msgFlag: newMsgChangeList[index].msgFlag,
  //         },
  //       });
  //     }
  //   });
  // }

  /* eslint-disable no-param-reassign */

  /**
   * 延迟 onChange 事件执行，解决先于 onRow 事件执行问题
   * @param {Object} e - RadioGroup Change 事件
   */
  @Bind()
  scopeOnChangeLater(e) {
    setTimeout(() => {
      this.scopeOnChange(e);
    }, 0);
    // if(!e.target.checked) {
    //   this.setState({
    //     selectedRow: [],
    //   });
    // }
  }

  /**
   * 权限范围类型变更事件
   * @param {Object} e - 变更事件对象
   */
  @Bind()
  scopeOnChange(e) {
    const {
      selectedRow,
      // roleAuth: { content = [] },
      scopeChangeList = [],
      key,
      content = [],
    } = this.state;
    const dataClone = cloneDeep(content);
    const newScopeChangeList = cloneDeep(scopeChangeList);
    const sourceRow = dataClone.find((item) => item[key] === selectedRow[key]); // 找到原始单据项
    const index = scopeChangeList.findIndex((item) => item[key] === selectedRow[key]);
    // eslint-disable-next-line no-constant-condition
    // if (e.target.checked) {
    if (index !== -1) {
      if (e && e.target && sourceRow.docEnabledFlag !== e.target.checked) {
        newScopeChangeList.splice(index, 1, {
          ...newScopeChangeList[index],
          docEnabledFlag: e.target.checked,
        });
        this.setState({
          scopeChangeList: newScopeChangeList.map((item) => {
            delete item.msgFlag;
            delete item.roleAuthorityLines;
            return item;
          }),
        });
      } else {
        newScopeChangeList.splice(index, 1);
        this.setState({
          scopeChangeList: newScopeChangeList.map((item) => {
            delete item.msgFlag;
            delete item.roleAuthorityLines;
            return item;
          }),
        });
      }
    } else {
      this.setState({
        scopeChangeList: [
          ...scopeChangeList,
          {
            ...sourceRow,
            docEnabledFlag: e.target.checked,
          },
        ].map((item) => {
          delete item.msgFlag;
          delete item.roleAuthorityLines;
          return item;
        }),
      });
    }
    // } else if (index !== -1) {
    //   newScopeChangeList.splice(index, 1);
    //   this.setState({
    //     scopeChangeList: newScopeChangeList.map(item => {
    //       delete item.msgFlag;
    //       delete item.roleAuthorityLines;
    //       return item;
    //     }),
    //   });
    // }
    if (e.target.checked === 0) {
      if (selectedRow.authScopeCode === 'USER') {
        this.authOnChange({});
      } else {
        this.authOnChange([]);
      }
    }
    this.setState({
      currentAuthScope: selectedRow.authScopeCode,
      selectedRow: {
        ...selectedRow,
        docEnabledFlag: e.target.checked,
      },
    });
    // 未选中的时候， 不显示权限值
    // if(!e.target.checked) {
    //   this.onRow(selectedRow);
    //   this.setState({
    //     selectedRow: [],
    //   });
    // }
  }

  /**
   * 权限维度变更事件
   * @param {Array} checkedList - 选中的权限项列表
   */
  @Bind()
  authOnChange(checkedList) {
    const {
      selectedRow,
      // roleAuth: { content = [] },
      authChangeList = [],
      key,
      content = [],
    } = this.state;
    const dataClone = cloneDeep(content);
    const newAuthChangeList = cloneDeep(authChangeList);
    const sourceRow = dataClone.find((item) => item[key] === selectedRow[key]) || []; // 找到原始单据项
    const sourceList = sourceRow.roleAuthorityLines || []; // 查找原始 List
    let editRow;
    if (isArray(checkedList)) {
      editRow = {
        ...sourceRow,
        roleAuthorityLines: !isEmpty(checkedList)
          ? sourceList
              .filter((item) =>
                item.enabledFlag
                  ? checkedList.findIndex((e) => e === item.authDimId) === -1
                  : checkedList.findIndex((e) => e === item.authDimId) !== -1
              )
              .map((item) => ({ ...item, enabledFlag: !item.enabledFlag ? 1 : 0 }))
          : sourceList.map((i) => ({ ...i, enabledFlag: 0 })),
      }; // 将变更过的权限列表项添加
    } else {
      editRow = {
        ...sourceRow,
        roleAuthorityLines: !isEmpty(checkedList)
          ? sourceList
              .filter((item) => item.authDimId === checkedList.authDimId)
              .map((item) => ({ ...item, enabledFlag: 1 }))
          : sourceList.filter((item) => !!item.enabledFlag).map((i) => ({ ...i, enabledFlag: 0 })),
      };
    }

    const index = authChangeList.findIndex((item) => item[key] === editRow[key]);

    if (isEmpty(editRow.roleAuthorityLines)) {
      if (index !== -1) {
        newAuthChangeList.splice(index, 1);
        this.setState({
          authChangeList: newAuthChangeList.map((item) => {
            delete item.msgFlag;
            return item;
          }),
        });
      }
    } else if (index !== -1) {
      newAuthChangeList.splice(index, 1, editRow);
      this.setState({
        authChangeList: newAuthChangeList.map((item) => {
          delete item.msgFlag;
          return item;
        }),
      });
    } else {
      this.setState({
        authChangeList: [...newAuthChangeList, editRow].map((item) => {
          delete item.msgFlag;
          return item;
        }),
      });
    }

    if (!isEmpty(checkedList)) {
      this.scopeOnChange({ target: { checked: 1 } });
    }

    if (!isArray(checkedList)) {
      this.setState({
        authUserDefaultValue: checkedList ? [checkedList.authDimId] : [],
      });
    } else {
      this.setState({
        authDefaultValue: checkedList,
      });
    }

    // 选择权限维度项时触发范围修改
    if (!isArray(checkedList)) {
      const { currentAuthScope } = this.state;
      if (currentAuthScope !== 'USER') {
        const params = {
          target: {
            value: 'USER',
          },
        };
        this.scopeOnChange(params);
      }
    } else {
      const { currentAuthScope } = this.state;
      if (currentAuthScope !== 'BIZ') {
        const params = {
          target: {
            value: 'BIZ',
          },
        };
        this.scopeOnChange(params);
      }
    }
    const { roleAuthorityLines } = selectedRow;
    const newList = roleAuthorityLines || [];
    this.setState({
      selectedRow: {
        ...selectedRow,
        roleAuthorityLines: newList.map((item) => {
          const idx = editRow.roleAuthorityLines.findIndex((e) => e.authDimId === item.authDimId);
          if (idx !== -1) {
            return {
              ...item,
              enabledFlag: editRow.roleAuthorityLines[idx].enabledFlag,
            };
          } else {
            return item;
          }
        }),
      },
    });
  }

  /**
   * 合并数据进行展示
   * @param {Array} sourceData - 原始数据
   * @param {Array} msgChangeList - 消息发送标志变化的数据
   * @param {Array} scopeChangeList - 权限范围变化的数据
   * @param {Array} authChangeList - 权限维度变化的数据
   */
  @Bind()
  mergeData(
    sourceData = [],
    enableChangeList = [],
    msgChangeList = [],
    scopeChangeList = [],
    authChangeList = []
  ) {
    const { key } = this.state;
    const data = sourceData.map((item) => {
      const enableIndex = enableChangeList.findIndex((e) => e[key] === item[key]);
      const msgIndex = msgChangeList.findIndex((e) => e[key] === item[key]);
      const scopeIndex = scopeChangeList.findIndex((e) => e[key] === item[key]);
      const authIndex = authChangeList.findIndex((e) => e[key] === item[key]);
      const newRoleAuthorityLines = cloneDeep(item.roleAuthorityLines);

      if (authIndex !== -1) {
        const changeList = authChangeList[authIndex].roleAuthorityLines;
        changeList.forEach((e) => {
          const index = newRoleAuthorityLines.findIndex((line) => line.authDimId === e.authDimId);
          newRoleAuthorityLines.splice(index, 1, e);
        });
      }
      return {
        ...item,
        docEnabledFlag:
          enableIndex !== -1 ? enableChangeList[enableIndex].docEnabledFlag : item.docEnabledFlag,
        msgFlag: msgIndex !== -1 ? msgChangeList[msgIndex].msgFlag : item.msgFlag,
        authScopeCode:
          scopeIndex !== -1 ? scopeChangeList[scopeIndex].authScopeCode : item.authScopeCode,
        roleAuthorityLines: newRoleAuthorityLines,
      };
    });
    return data;
  }

  @Bind()
  createSaveData(
    enableChangeList = [],
    msgChangeList = [],
    scopeChangeList = [],
    authChangeList = []
  ) {
    const { key } = this.state;
    const newEnableChangeList = enableChangeList.map((item) => {
      const { roleAuthorityLines, msgFlag, ...other } = item;
      return {
        ...other,
      };
    });
    const saveList = [...newEnableChangeList];

    msgChangeList.forEach((item) => {
      const index = saveList.findIndex((e) => e[key] === item[key]);
      if (index !== -1) {
        saveList.splice(index, 1, {
          ...saveList[index],
          msgFlag: item.msgFlag,
        });
      } else {
        saveList.push(item);
      }
    });

    scopeChangeList.forEach((item) => {
      const index = saveList.findIndex((e) => e[key] === item[key]);
      if (index !== -1) {
        saveList.splice(index, 1, {
          ...saveList[index],
          authScopeCode: item.authScopeCode,
        });
      } else {
        saveList.push(item);
      }
    });

    authChangeList.forEach((item) => {
      const index = saveList.findIndex((e) => e[key] === item[key]);
      if (index !== -1) {
        saveList.splice(index, 1, {
          ...saveList[index],
          roleAuthorityLines: item.roleAuthorityLines,
        });
      } else {
        saveList.push(item);
      }
    });

    return saveList
      .map((item) => {
        const auths = item.roleAuthorityLines || [];
        // 判断是否具备 auth 变更
        return isArray(item.roleAuthorityLines)
          ? {
              ...item,
              roleAuthorityLines: auths.filter((e) => !!e.authDimId), // 过滤非修改权限范围类型内的权限维度以及未启用的权限维度
            }
          : item;
      })
      .filter((item) => !(isArray(item.roleAuthorityLines) && isEmpty(item.roleAuthorityLines)));
  }

  render() {
    const {
      dispatch,
      path,
      form,
      saveLoading,
      initLoading,
      title,
      modalVisible,
      roleId,
      tenantId,
      roleDataAuthorityCompany,
      roleDataAuthorityPurorg,
      roleDataAuthorityManagement,
      roleDataAuthorityPuragent = {},
      roleDataAuthorityLovView = {},
      roleDataAuthorityDataSource = {},
      roleDataAuthorityValueList = {},
      roleDataAuthorityDataGroup = {},
      fetchTabListLoading,
      updateCompanyLoading,
      fetchCompanyLoading,
      refreshCompanyLoading,
      addPurOrgLoading,
      fetchPurOrgLoading,
      fetchPurOrgModalLoading,
      addPuragentLoading,
      fetchPuragentLoading,
      fetchModalPuragentLoading,
      addValueListLoading,
      fetchValueListLoading,
      fetchModalValueListLoading,
      addLovViewLoading,
      fetchLovViewLoading,
      fetchModalLovViewLoading,
      addDataSourceLoading,
      fetchDataSourceLoading,
      fetchModalDataSourceLoading,
      addDataGroupLoading,
      fetchDataGroupLoading,
      fetchModalDataGroupLoading,
    } = this.props;
    const {
      selectedRow,
      roleAuthScopeCode = [],
      key,
      currentAuthScope, // 当前选中的维度类型
      authUserDefaultValue, // 数据为用户范围时的选中值
      roleAuth,
      // roleAuth: { content = [] },
      authDefaultValue = [], // 默认选中的列表
      enableChangeList = [],
      msgChangeList = [],
      scopeChangeList = [],
      authChangeList = [],
      isSiteFlag,
      content,
    } = this.state;
    const dataSource = this.mergeData(
      content,
      enableChangeList,
      msgChangeList,
      scopeChangeList,
      authChangeList
    );
    const bizRoleAuthorityLines =
      (selectedRow.roleAuthorityLines &&
        selectedRow.roleAuthorityLines.filter((item) => item.dimensionType === 'BIZ')) ||
      [];

    const userRoleAuthorityLines =
      (selectedRow.roleAuthorityLines &&
        selectedRow.roleAuthorityLines.filter((item) => item.dimensionType === 'USER')) ||
      [];

    const columns = [
      {
        title: intl.get('hiam.roleManagement.model.role.docType').d('单据'),
        dataIndex: 'docTypeName',
        width: 370,
      },
      {
        title: intl.get('hiam.roleManagement.model.role.authRang').d('权限维度范围'),
        width: 250,
        dataIndex: 'msgFlag',
        render: (_, record) => {
          this.enabledFlag = null;
          scopeChangeList.forEach((i) => {
            if (i.uuid === record.uuid) {
              this.enabledFlag = i.docEnabledFlag;
            }
          });
          this.isChanged = false;
          scopeChangeList.forEach((i) => {
            if (i.uuid === record.uuid) {
              this.isChanged = true;
            }
          });
          return (
            // <CheckboxGroup value={record.authScopeCode} onChange={this.scopeOnChangeLater}>
            <>
              {roleAuthScopeCode
                .filter((item) => item.meaning === record.authScopeMeaning)
                .map((item) => (
                  <Checkbox
                    // value={item.value}
                    // key={item.value}
                    checked={
                      // eslint-disable-next-line no-nested-ternary
                      record.authControlType === 'ONLY_CONFIG'
                        ? 1
                        : null || (this.isChanged && scopeChangeList.length > 0)
                        ? this.enabledFlag
                        : record.docEnabledFlag
                    }
                    disabled={record.authControlType === 'ONLY_CONFIG'}
                    style={{
                      marginRight: 0,
                      padding: '0 10px',
                      // display: item.meaning === record.authScopeMeaning ? '' : 'none',
                    }}
                    onChange={this.scopeOnChangeLater}
                  >
                    {item.meaning}
                  </Checkbox>
                ))}
            </>
            // </CheckboxGroup>
          );
        },
      },
    ];

    const authDim =
      !isEmpty(selectedRow) &&
      (currentAuthScope === 'USER' ? (
        // <RadioGroup
        //   value={authUserDefaultValue}
        //   onChange={this.authOnChange}
        //   style={{ width: '100%' }}
        // >
        //   <Row>
        //     {userRoleAuthorityLines.map((item) => (
        //       <Col span={24} key={item.authDimId}>
        //         <Radio onClick={this.authOnChange} value={item.authDimId} key={item.authDimId} style={{ height: 30 }}>
        //           {item.authTypeMeaning}
        //         </Radio>
        //       </Col>
        //     ))}
        //   </Row>
        // </RadioGroup>
        <CheckboxGroup
          style={{ width: '100%' }}
          onChange={(e) => {
            const dataClone = cloneDeep(content);
            const sourceRow = dataClone.find((item) => item[key] === selectedRow[key]) || []; // 找到原始单据项
            const sourceList = sourceRow.roleAuthorityLines || []; // 查找原始 List
            sourceList.filter((item) => item.authDimId === e[0]);
            this.authOnChange(
              sourceList.filter((item) => item.authDimId === e[e.length - 1])[0] || {}
            );
          }}
          value={authUserDefaultValue}
        >
          <Row>
            {userRoleAuthorityLines.map((item) => (
              <Col span={24} key={item.authDimId}>
                <Checkbox style={{ height: 30 }} key={item.authDimId} value={item.authDimId}>
                  {item.authTypeMeaning}
                </Checkbox>
              </Col>
            ))}
          </Row>
        </CheckboxGroup>
      ) : (
        <CheckboxGroup
          style={{ width: '100%' }}
          onChange={this.authOnChange}
          value={authDefaultValue}
        >
          <Row>
            {bizRoleAuthorityLines.map((item) => (
              <Col span={24} key={item.authDimId}>
                <Checkbox style={{ height: 30 }} key={item.authDimId} value={item.authDimId}>
                  {item.authTypeMeaning}
                </Checkbox>
              </Col>
            ))}
          </Row>
        </CheckboxGroup>
      ));

    const authDataProps = {
      path,
      dispatch,
      tenantId,
      roleId,
      roleDataAuthorityLovView,
      roleDataAuthorityDataSource,
      roleDataAuthorityDataGroup,
      roleDataAuthorityValueList,
      authorityCompany: roleDataAuthorityCompany,
      authorityPurorg: roleDataAuthorityPurorg,
      authorityManagement: roleDataAuthorityManagement,
      authorityPuragent: roleDataAuthorityPuragent,
      fetchTabListLoading,
      // 公司loading
      updateCompanyLoading,
      fetchCompanyLoading,
      refreshCompanyLoading,
      // 采购组织loading
      addPurOrgLoading,
      fetchPurOrgLoading,
      fetchPurOrgModalLoading,
      // 采购员
      addPuragentLoading,
      fetchPuragentLoading,
      fetchModalPuragentLoading,
      // 值集
      addValueListLoading,
      fetchValueListLoading,
      fetchModalValueListLoading,
      // 值集视图
      addLovViewLoading,
      fetchLovViewLoading,
      fetchModalLovViewLoading,
      // 数据源
      addDataSourceLoading,
      fetchDataSourceLoading,
      fetchModalDataSourceLoading,
      // 数据组
      addDataGroupLoading,
      fetchDataGroupLoading,
      fetchModalDataGroupLoading,
    };

    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        width="1200px"
        visible={modalVisible}
        confirmLoading={saveLoading}
        onCancel={this.handleCancel}
        onOk={this.handleOk}
      >
        {isSiteFlag ? (
          <Row gutter={24}>
            <Col span={16}>
              <Form>
                <Row type="flex" gutter={24} align="bottom">
                  <Col span={8}>
                    <Form.Item
                      {...SEARCH_FORM_ITEM_LAYOUT}
                      label={intl.get('hiam.roleManagement.model.role.docType').d('单据')}
                    >
                      {form.getFieldDecorator('searchDocTypeName')(<Input />)}
                    </Form.Item>
                  </Col>
                  <Col span={8}>
                    <Form.Item {...SEARCH_FORM_ITEM_LAYOUT}>
                      <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                        {intl.get('hzero.common.button.search').d('查询')}
                      </Button>
                    </Form.Item>
                  </Col>
                </Row>
              </Form>
              <Table
                bordered
                rowKey={key}
                style={{
                  marginTop: 16,
                }}
                rowClassName={this.addHighlight}
                onChange={this.handlePagination}
                onRow={(record) => ({
                  onClick: () => this.onRow(record),
                })}
                dataSource={dataSource}
                columns={columns}
                scroll={{ x: tableScrollWidth(columns) }}
                loading={initLoading}
                hideDefaultSelections
                pagination={{
                  ...createPagination(roleAuth),
                  simple: true,
                }}
              />
            </Col>
            <Col span={8}>
              {!isEmpty(selectedRow) ? (
                <>
                  <Row>
                    <Col>
                      <div
                        style={{
                          marginTop: 55,
                          height: 48,
                          lineHeight: '48px',
                          borderRadius: '2px 2px 0 0',
                          textAlign: 'center',
                          border: '1px solid #e8e8e8',
                          borderBottomColor: '#cecece',
                          fontWeight: 'bold',
                          background: '#f5f5f5',
                        }}
                      >
                        {intl.get('hiam.roleManagement.view.title.dimControl').d('控制维度')}
                      </div>
                    </Col>
                  </Row>
                  <Row>
                    <Col>
                      <div
                        style={{
                          padding: '20px',
                          border: '1px solid #e8e8e8',
                          borderTopWidth: 0,
                          minHeight: 350,
                        }}
                      >
                        {authDim}
                      </div>
                    </Col>
                  </Row>
                </>
              ) : null}
            </Col>
          </Row>
        ) : (
          <Tabs defaultActiveKey="1" animated={false}>
            <Tabs.TabPane
              key="1"
              tab={intl.get('hiam.roleManagement.view.message.title.auth').d('权限维度')}
            >
              <Row gutter={24}>
                <Col span={16}>
                  <Form>
                    <Row type="flex" gutter={24} align="bottom">
                      <Col span={8}>
                        <Form.Item
                          {...SEARCH_FORM_ITEM_LAYOUT}
                          label={intl.get('hiam.roleManagement.model.role.docType').d('单据')}
                        >
                          {form.getFieldDecorator('searchDocTypeName')(<Input />)}
                        </Form.Item>
                      </Col>
                      <Col span={8}>
                        <Form.Item {...SEARCH_FORM_ITEM_LAYOUT}>
                          <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                            {intl.get('hzero.common.button.search').d('查询')}
                          </Button>
                        </Form.Item>
                      </Col>
                    </Row>
                  </Form>
                  <Table
                    bordered
                    rowKey={key}
                    style={{
                      marginTop: 16,
                    }}
                    rowClassName={this.addHighlight}
                    onChange={this.handlePagination}
                    onRow={(record) => ({
                      onClick: () => this.onRow(record),
                    })}
                    dataSource={dataSource}
                    columns={columns}
                    scroll={{ x: tableScrollWidth(columns) }}
                    loading={initLoading}
                    hideDefaultSelections
                    pagination={{
                      ...createPagination(roleAuth),
                      simple: true,
                    }}
                  />
                </Col>
                <Col span={8}>
                  {!isEmpty(selectedRow) ? (
                    <>
                      <Row>
                        <Col>
                          <div
                            style={{
                              marginTop: 55,
                              height: 48,
                              lineHeight: '48px',
                              borderRadius: '2px 2px 0 0',
                              textAlign: 'center',
                              border: '1px solid #e8e8e8',
                              borderBottomColor: '#cecece',
                              fontWeight: 'bold',
                              background: '#f5f5f5',
                            }}
                          >
                            {intl.get('hiam.roleManagement.view.title.dimControl').d('控制维度')}
                          </div>
                        </Col>
                      </Row>
                      <Row>
                        <Col>
                          <div
                            style={{
                              padding: '20px',
                              border: '1px solid #e8e8e8',
                              borderTopWidth: 0,
                              minHeight: 350,
                            }}
                          >
                            {authDim}
                          </div>
                        </Col>
                      </Row>
                    </>
                  ) : null}
                </Col>
              </Row>
            </Tabs.TabPane>
            <Tabs.TabPane
              key="2"
              tab={intl.get('hiam.roleManagement.view.message.title.authData').d('权限数据')}
            >
              <AuthData {...authDataProps} />
            </Tabs.TabPane>
          </Tabs>
        )}
      </Modal>
    );
  }
}
