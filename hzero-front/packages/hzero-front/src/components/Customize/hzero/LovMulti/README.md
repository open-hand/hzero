# LOV多选组件说明

LOV多选组件是在原LOV组件的基础上重构而成，因此LOV的部分属性在在多选LOV上也适用。

### 属性列表
多选lov的所有组件属性如下，除标记属性外，其他属性同LOV组件
```tsx
interface LovProps {
  value?: string;
  disabled?: boolean;
  lovOptions?: any;
  extSetMap?: any;
  form?: any;
  originTenantId?: number;
  code?: string;
  displayData?: string[]; // 多选
  queryParams?: any;
  queryInputProps?: any;
  style?: React.CSSProperties;
  className?: string;
  allowClear?: boolean;
  delimma?: string; // 多选，默认“/”
  onOk?: any;
  onChange?: any;
  onClear?: any;
  onClick?: any;
  onCancel?: any;
  isDbc2Sbc?: boolean;
  translateData: any; // 多选
  showAll: boolean; // 多选
  viewOnly: boolean; // 多选
}
```

### 属性说明

- showAll: 多选LOV默认最多显示5条记录，后续显示为 `...`，该属性为true时可显示全部多选项。
- viewOnly: 为true时组件以只读模式。
- delimma: 字符串，指定多个 `显示值` 之间的分隔符。
- value: 字符串，多值以逗号分隔。
- displayData：字符串数组，和translateData属性作为配合使用，优先级低于translateData。
- translateData: 存储多选value和其对应翻译的键值对，优先级最高。
> 如value为 `"1,2,3"`，displayData为 `[test1,test2]`，translateData为 `{2: "test4"}`，最终显示为 `"test1/test4/3"`。

### 弹框功能说明

- 全选: 将符合当前左侧查询条件的所有数据追加到已选列表
- 查找(右侧): 在已选列表中查找
- 清除: 清空已选的数据

### 其他说明

- 多选LOV组件必须配置值集翻译，否则弹框右侧部分情况下无法展示所选记录
- URL类型的值集，注意数据返回格式必须是标准的分页结构