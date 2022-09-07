import merge from 'lodash/merge';
import {
  // types:
  Operators,
  Widgets,
  Fields,
  Config,
  Types,
  Conjunctions,
  Settings,
  LocaleSettings,
  Funcs,
} from '@/components/react-awesome-query-builder';
// eslint-disable-next-line camelcase
import zh_CN from 'hzero-ui/lib/locale-provider/zh_CN';
import AntdConfig from '@/components/react-awesome-query-builder/config/antd';
import getLang from '@/langs/commonLang';

const InitialConfig = AntdConfig;

const fields: Fields = {
  // 'my-text-test': {
  //   label: 'my-text-test',
  //   type: 'text',
  //   operators: [
  //     'equal',
  //     'not_equal',
  //     'less',
  //     'less_or_equal',
  //     'greater',
  //     'greater_or_equal',
  //     'is_empty',
  //     'is_not_empty',
  //     'select_any_in',
  //     'select_not_any_in',
  //   ],
  // },
  // 'parentName': {
  //   label: 'parentName',
  //   type: 'text',
  //   operators: [
  //     'equal',
  //     'not_equal',
  //     'less',
  //     'less_or_equal',
  //     'greater',
  //     'greater_or_equal',
  //     'is_empty',
  //     'is_not_empty',
  //     'select_any_in',
  //     'select_not_any_in',
  //   ],
  // },
};

const conjunctions: Conjunctions = {
  AND: merge(InitialConfig.conjunctions.AND, {
    label: getLang('AND'),
  }),
  OR: merge(InitialConfig.conjunctions.OR, {
    label: getLang('OR'),
  }),
};

const operators: Operators = {
  ...InitialConfig.operators,
  equal: merge(InitialConfig.operators.equal, {
    label: getLang('EQUAL'),
  }),
  not_equal: merge(InitialConfig.operators.not_equal, {
    label: getLang('NOT_EQUAL'),
  }),
  less: merge(InitialConfig.operators.less, {
    label: getLang('LESS'),
  }),
  less_or_equal: merge(InitialConfig.operators.less_or_equal, {
    label: getLang('LESS_OR_EQUAL'),
  }),
  greater: merge(InitialConfig.operators.greater, {
    label: getLang('GREATER'),
  }),
  greater_or_equal: merge(InitialConfig.operators.greater_or_equal, {
    label: getLang('GREATER_OR_EQUAL'),
  }),
  is_empty: merge(InitialConfig.operators.is_empty, {
    label: getLang('IS_EMPTY'),
  }),
  is_not_empty: merge(InitialConfig.operators.is_not_empty, {
    label: getLang('IS_NOT_EMPTY'),
  }),
  // select_any_in: merge(InitialConfig.operators.select_any_in, {
  //   label: '包含',
  // }),
  // select_not_any_in: merge(InitialConfig.operators.select_not_any_in, {
  //   label: '不包含',
  // }),
};

const widgets: Widgets = {
  ...InitialConfig.widgets,
  text: merge(InitialConfig.widgets.text, {
    valuePlaceholder: getLang('VALUE'),
  }),
};

const types: Types = {
  ...InitialConfig.types,
  text: merge(InitialConfig.types.text, {
    widgets: {
      text: {
        operators: [
          'equal',
          'not_equal',
          'less',
          'less_or_equal',
          'greater',
          'greater_or_equal',
          'is_empty',
          'is_not_empty',
          // 'select_any_in',
          // 'select_not_any_in',
        ],
      },
    },
  }),
};

const localeSettings: LocaleSettings = {
  locale: {
    short: 'zh',
    full: 'zh_CN',
    antd: zh_CN,
  },
  valueLabel: getLang('VALUE'),
  valuePlaceholder: getLang('VALUE'),
  fieldLabel: getLang('CONDITION_FIELD'),
  operatorLabel: getLang('CONDITION'),
  fieldPlaceholder: getLang('CONDITION_FIELD'),
  operatorPlaceholder: getLang('CONDITION'),
  deleteLabel: '',
  addGroupLabel: getLang('ADD_CONDITION_GROUP'),
  addRuleLabel: getLang('ADD_CONDITION'),
  delGroupLabel: '',
  notLabel: getLang('NOT'),
  valueSourcesPopupTitle: 'Select value source',
  removeRuleConfirmOptions: {
    title: getLang('DELETE_CONDITION_CONFIRM'),
    okText: getLang('SURE'),
    okType: 'danger',
  },
  removeGroupConfirmOptions: {
    title: getLang('DELETE_GROUP_CONFIRM'),
    okText: getLang('SURE'),
    okType: 'danger',
  },
};

const settings: Settings = {
  ...InitialConfig.settings,
  ...localeSettings,

  valueSourcesInfo: {},
  // canReorder: false,
  // canRegroup: false,
  showNot: false,
  // showLabels: true,
  maxNesting: 3,
  fieldSeparator: '。',
  canLeaveEmptyGroup: true, // after deletion
  customFieldSelectProps: {
    showSearch: true,
  },

  // 组件是否禁用
  immutableGroupsMode: false,
  immutableFieldsMode: false,
  immutableOpsMode: false,
  immutableValuesMode: false,

  // renderField: (props) => <TextField {...props} />,
  // renderOperator: (props) => <FieldDropdown {...props} />,
  // renderFunc: (props) => <FieldSelect {...props} />,
};

const funcs: Funcs = {};

const config: Config = {
  conjunctions,
  operators,
  widgets,
  types,
  settings,
  fields,
  funcs,
};

export default config;
