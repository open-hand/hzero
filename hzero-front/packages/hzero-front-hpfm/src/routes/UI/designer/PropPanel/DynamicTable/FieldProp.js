/**
 * FieldProp.js
 * @author WY
 * @date 2018-10-03
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Input, Checkbox, Divider, Icon, Popconfirm, Select, Button } from 'hzero-ui';
import { forEach, isFunction, map, sortBy, filter } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import ValueList from 'components/ValueList';
import { hiddenColumnSep } from 'components/DynamicComponent/DynamicTable/utils';
import DynamicTable from 'components/DynamicComponent/DynamicTable';

import intl from 'utils/intl';
import notification from 'utils/notification';

import {
  attributeNameProp,
  attributeTypeProp,
  attributeValueProp,
  fieldLabelProp,
  fieldNameProp,
} from '../../config';
import DataType from '../../DataType';

// import ButtonPropModal from './ButtonPropModal';
import ButtonEditModal from '../ScriptEdit/ButtonEditModal';
import BtnTitleEditModal from './BtnTitleEditModal';

const addButtonStyle = { marginLeft: 10 };
const linkButtonStyle = {
  width: 150,
  marginBottom: 4,
  marginRight: 10,
};
const removeIconStyle = { cursor: 'pointer' };

const buttonStyle = {
  marginLeft: 10,
  marginRight: 10,
  cursor: 'pointer',
};

@Form.create({
  fieldNameProp: null,
  onValuesChange(props, changedValues, allValues) {
    if (isFunction(props.onValuesChange)) {
      props.onValuesChange(props, changedValues, allValues);
    }
  },
})
export default class FieldProp extends React.Component {
  state = {
    linkButtonEditProps: {},
    btnPropEditModalVisible: false,
    linkBtnTitleEditModalProps: {},
  };

  render() {
    const { field } = this.props;
    const { btnPropEditModalVisible, linkButtonEditProps, linkBtnTitleEditModalProps } = this.state;
    const propValues = {};
    forEach(field.config, prop => {
      propValues[prop[attributeNameProp]] = prop[attributeValueProp];
    });
    return (
      <Form>
        {this.renderFieldCommonProps(propValues)}
        <Divider />
        {this.renderFieldTypeProps(propValues)}
        {btnPropEditModalVisible && (
          <ButtonEditModal
            key={linkButtonEditProps.btnKey}
            buttonEditProps={linkButtonEditProps}
            visible={btnPropEditModalVisible}
            onOk={this.handleLinkButtonModalOk}
            onCancel={this.handleLinkButtonModalCancel}
          />
        )}
        {linkBtnTitleEditModalProps.visible && (
          <BtnTitleEditModal
            onCancel={this.closeBtnTitleEditModal}
            onSave={this.changeLinkButtonName}
            {...linkBtnTitleEditModalProps}
          />
        )}
      </Form>
    );
  }

  renderFieldCommonProps() {
    const { form, field } = this.props;
    return (
      <React.Fragment>
        <Form.Item key="autoSize">
          {form.getFieldDecorator('autoSize', {
            initialValue: +field.width === 0,
            valuePropName: 'checked',
          })(<Checkbox>{intl.get('hpfm.ui.model.fieldAttr.autoSize').d('宽度自适应')}</Checkbox>)}
        </Form.Item>
        <Form.Item key={fieldNameProp} label="字段名">
          {form.getFieldDecorator(fieldNameProp, {
            initialValue: field[fieldNameProp],
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.ui.field.columnField.fieldName').d('字段名'),
                }),
              },
            ],
          })(<Input />)}
        </Form.Item>
        <Form.Item key={fieldLabelProp} label="标题">
          {form.getFieldDecorator(fieldLabelProp, {
            initialValue: field[fieldLabelProp],
          })(<Input />)}
        </Form.Item>
        <Form.Item key="align" label="对齐方式">
          {form.getFieldDecorator('align', {
            initialValue: field.align,
          })(
            <ValueList
              options={[
                {
                  value: 'left',
                  meaning: intl.get('hpfm.ui.field.columnField.alignLeft').d('居左'),
                },
                {
                  value: 'center',
                  meaning: intl.get('hpfm.ui.field.columnField.alignCenter').d('居中'),
                },
                {
                  value: 'right',
                  meaning: intl.get('hpfm.ui.field.columnField.alignRight').d('居右'),
                },
              ]}
            />
          )}
        </Form.Item>
        {/* visibleFlag {1,0} 隐藏(没有用了) */}
        {/* <Form.Item */}
        {/* key="visiableFlag" */}
        {/* > */}
        {/* {form.getFieldDecorator('visiableFlag', { */}
        {/* initialValue: field.visiableFlag === 0 ? 0 : 1, */}
        {/* })( */}
        {/* <Checkbox checkedValue={0} unCheckedValue={1}> */}
        {/* {intl.get('hpfm.ui.field.visiableFlag').d('隐藏')} */}
        {/* </Checkbox> */}
        {/* )} */}
        {/* </Form.Item> */}
      </React.Fragment>
    );
  }

  renderFieldTypeProps(propValues) {
    const { field } = this.props;
    const renderFunc = `renderFieldType${field.componentType}Props`;
    if (isFunction(this[renderFunc])) {
      return this[renderFunc](propValues);
    }
    return null;
  }

  renderFieldTypeLinkButtonProps() {
    const { field } = this.props;
    return (
      <React.Fragment>
        <Form.Item
          colon={false}
          label={
            <React.Fragment>
              {intl.get('hpfm.ui.field.linkButton').d('LinkButton')}
              :&nbsp;
              <a style={addButtonStyle} onClick={this.handleLinkButtonAddBtn}>
                {intl.get('hzero.common.button.create').d('新建')}
              </a>
            </React.Fragment>
          }
        />
        {map(sortBy(field.btns, 'orderSeq'), (btnConfig, index) => {
          const btnProps = {};
          forEach(btnConfig, attr => {
            btnProps[attr[attributeNameProp]] = attr[attributeValueProp];
          });
          const { btnKey } = btnProps;
          return (
            <Form.Item key={btnKey} className="ant-form-item-without-help">
              <Button
                style={linkButtonStyle}
                onClick={() => {
                  this.openPropEditModal(btnKey, btnProps, btnConfig, index);
                }}
              >
                {btnProps.title}
              </Button>
              <Icon
                type="edit"
                style={buttonStyle}
                onClick={() => {
                  this.openBtnTitleEditModal(btnKey, btnProps, btnConfig, index);
                }}
              />
              <Popconfirm
                title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
                onConfirm={() => this.handleLinkButtonRemoveBtn(index)}
              >
                <Icon type="close" style={removeIconStyle} />
              </Popconfirm>
            </Form.Item>
          );
        })}
      </React.Fragment>
    );
  }

  // renderFieldTypeColumnFieldProps(propValues = {}) {
  //   const { form } = this.props;
  //   return (null);
  // }

  // 修改 按钮的名称
  @Bind()
  changeLinkButtonName(title) {
    const { field } = this.props;
    const {
      linkBtnTitleEditModalProps: { btnConfig, index },
    } = this.state;
    const titleAttrName = 'title';
    field.btns[index] = map(btnConfig, attr => {
      if (attr[attributeNameProp] === titleAttrName) {
        return {
          ...attr,
          [attributeValueProp]: title,
        };
      } else {
        return attr;
      }
    });
    this.closeBtnTitleEditModal();
  }

  @Bind()
  openBtnTitleEditModal(btnKey, btnProps, btnConfig, index) {
    this.setState({
      linkBtnTitleEditModalProps: {
        btnKey,
        btnProps,
        btnConfig,
        index,
        visible: true,
        title: btnProps.title,
      },
    });
  }

  @Bind()
  closeBtnTitleEditModal() {
    this.setState({
      linkBtnTitleEditModalProps: {
        visible: false,
      },
    });
  }

  /**
   * 打开 按钮 属性编辑的 模态框
   */
  @Bind()
  openPropEditModal(btnKey, btnProps, btnConfig, index) {
    // const {config = {}} = this.props;
    this.setState({
      btnPropEditModalVisible: true,
      linkButtonRecord: {
        btnKey,
        btnProps,
        btnConfig,
        index,
      },
      linkButtonEditProps: {
        extraParams: this.getButtonEditExtraParams(),
        propsValue: btnProps,
        // scripts: config.scripts,
        // components: config.fields,
        extraActions: this.getTableExtraActions(),
      },
    });
  }

  @Bind()
  getTableExtraActions() {
    const {
      component: { description },
    } = this.props;
    const tableInternalFuncsOptions = map(DynamicTable.internalFuncs, internalFuncStr => {
      return (
        <Select.Option value={internalFuncStr} key={internalFuncStr}>
          {intl
            .get(DynamicTable.internalFuncsInfo[internalFuncStr].descriptionIntlCode)
            .d(DynamicTable.internalFuncsInfo[internalFuncStr].descriptionIntlDefault)}
        </Select.Option>
      );
    });
    return (
      <Select.OptGroup label={`${description}Table事件`}>
        {tableInternalFuncsOptions}
      </Select.OptGroup>
    );
  }

  /**
   * 获取额外的参数类型
   * @return {{value: string, meaning: string, getParamValueElement: (function(*, *, *): *)}[]}
   */
  getButtonEditExtraParams() {
    const { component = {} } = this.props;
    const columns = (component && component.fields) || [];
    const hiddenColumns = (component && component.hiddenColumns) || [];
    return [
      {
        value: 'c',
        meaning: '列参数',
        getParamValueElement: () => {
          return (
            <Select>
              <Select.OptGroup label="列参数">
                {map(columns, column => {
                  return (
                    <Select.Option value={column.fieldName} key={column.fieldName}>
                      {column.fieldLabel}
                    </Select.Option>
                  );
                })}
              </Select.OptGroup>
              <Select.OptGroup label="隐藏域">
                {map(hiddenColumns, hiddenColumn => {
                  const [value, meaning] = hiddenColumn[attributeValueProp].split(hiddenColumnSep);
                  return (
                    <Select.Option value={value} key={value}>
                      {meaning}
                    </Select.Option>
                  );
                })}
              </Select.OptGroup>
            </Select>
          );
        },
      },
    ];
  }

  @Bind()
  handleLinkButtonAddBtn(e) {
    e.preventDefault();
    const { field } = this.props;
    const newBtnKey = uuid();
    field.btns.push([
      {
        [attributeNameProp]: 'title',
        [attributeValueProp]: field[fieldLabelProp],
        [attributeTypeProp]: DataType.String,
      },
      {
        [attributeNameProp]: 'btnKey',
        [attributeValueProp]: newBtnKey,
        [attributeTypeProp]: DataType.String,
      },
    ]);
    this.forceUpdate(); // todo 更新 属性编辑界面
  }

  @Bind()
  handleLinkButtonRemoveBtn(removeIndex) {
    const { field } = this.props;
    if (field.btns.length <= 1) {
      notification.warning({ message: 'LinkButton 至少要有一个按钮' });
      return;
    }
    field.btns = filter(field.btns, (btn, index) => index !== removeIndex);
    this.forceUpdate(); // todo 更新 属性编辑界面
  }

  @Bind()
  handleLinkButtonModalOk({ attrConfig }) {
    const { field } = this.props;
    const {
      linkButtonRecord: { index, btnKey, btnProps },
    } = this.state;

    // orderSeq btnKey title
    field.btns[index] = [
      ...attrConfig,
      {
        attributeName: 'btnKey',
        value: btnKey,
        attributeType: DataType.String,
      },
      {
        attributeName: 'orderSeq',
        value: btnProps.orderSeq,
        attributeType: DataType.String,
      },
      {
        attributeName: 'title',
        value: btnProps.title,
        attributeType: DataType.String,
      },
    ];
    // todo 更新 field
    // this.props.updateField(component, {...field, btns: {...field.btns, [btnKey]: btnConfig}})
    this.setState({
      btnPropEditModalVisible: false,
      linkButtonEditProps: {},
      linkButtonRecord: {},
    });
  }

  @Bind()
  handleLinkButtonModalCancel() {
    this.setState({
      btnPropEditModalVisible: false,
      linkButtonEditProps: {},
      linkButtonRecord: {},
    });
  }
}

if (process.env.NODE_ENV === 'production') {
  FieldProp.displayName = 'DynamicTable(FieldProp)';
}
