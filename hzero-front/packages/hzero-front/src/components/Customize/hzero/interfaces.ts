import { ReactElement, ReactNode } from 'react';

export interface Item {
  item: any;
}
export interface ItemCollectons {
  [fieldCode: string]: Item;
}
export interface FormItem {
  item: ReactElement | ReactNode;
  row: number;
  col: number;
  colProps?: any;
  rowProps?: any;
  [extProp: string]: any;
}
export interface ColumnItem extends Item {
  seq: number;
}

export interface FormItemCollectons {
  [fieldCode: string]: FormItem;
}
export interface ColumnCollectons {
  [fieldCode: string]: ColumnItem;
}
export interface UnitConfig {
  fields?: FieldConfig[];
  maxCol?: number;
  readOnly?: boolean;
  unitCode?: string;
  unitType?: string;
  unitAlias?: UnitAlias[];
  labelCol?: number;
  wrapperCol?: number;
}
export interface FieldConfig {
  fieldCode: string;
  fieldName?: string;
  seq?: number;
  formRow?: number;
  formCol?: number;
  fixed?: string;
  width?: number;
  fieldType?: string;
  defaultActive?: number;
  required?: number;
  editable?: number;
  visible?: number;
  asText?: number;
  multipleFlag?: number;
  defaultValueMeaning?: any;
  lovMappings?: {
    sourceCode: string;
    targetCode: string;
  }[];
  lovCode?: string;
  linkTitle?: string;
  linkHref?: string;
  linkNewWindow?: string;
  numberMax?: number;
  numberMin?: number;
  areaMaxLine?: number;
  textMaxLength?: number;
  textMinLength?: number;
  numberPrecision?: number;
  labelCol?: number;
  wrapperCol?: number;
  defaultValue?: any;
  renderRule?: string;
  renderOptions?: string;
  dateFormat?: string;
  conValidDTO?: ConValid;
  defaultValueCon?: ConValid;
  conditionHeaderDTOs?: ConditionHeaderDTO[];
  selfValid?: Function; // 非接口返回属性
  paramList?: ParamList[];
  [extProp: string]: any;
}
export interface ParamList {
  paramKey: string;
  paramType: string;
  paramValue?: string;
  paramUnitCode?: string;
  paramFieldCode?: string;
}
export interface ConValid {
  conValidList: {
    conExpression?: string;
    errorMessage?: string;
  }[];
  conLineList: {
    conCode: string | number;
    sourceFieldCode: string;
    sourceUnitCode: string;
    conExpression: string;
    targetType: string;
    targetFieldCode?: string;
    targetValue: any;
  }[];
}
export interface ConditionHeaderDTO {
  conType: string;
  conExpression?: string;
  lines: {
    conCode: string | number;
    sourceFieldCode: string;
    sourceUnitCode: string;
    conExpression: string;
    targetType: string;
    targetFieldCode?: string;
    targetValue: any;
  }[];
}

export interface UnitAlias {
  unitCode: string;
  alias: string;
}
