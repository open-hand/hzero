# 个性化

分为`component`和`field`;

`component` 中存放 `field`;

每次更新`field`属性 
  需要 重写 field 的引用, 
  和 重新设置 currentEditField, 以便更新`DesignerPanel`

TODO 每次更新位置属性在 component 中更新

## TODO 将要的结构

```
|--Draw
|----PickBoxPanel
|------[Component|Field]
|----DesignerPanel
|------[Render]
|--------DrawDragComponent(DropComponent)
|----------Layout
|------------[wrapperFieldComponent]
|--------------CellControl
|----PropPanel
|------Component
|--------ComponentProp|FieldProp
```

### PickBoxPanel
```
|--PickBoxPanel           decide addable components by currentEditComponent
|----[Field|Component]    addable components
```

### DesignerPanel
```
|--Render                             deal component props, and capture event from children
|----DrawDragComponent(DropComponent)
|------Layout                         decide layout by component and fields
|--------[wrapperFieldComponent]      give Draw And Drop for field
|----------CellControl                real field and menu
```

### PropPanel
```
|--PropPanel                      decide edit component by currentEditComponent,
|----Component                    capture event from children, decide children is component or field
|------ComponentProp|FieldProp    form for props
```


## triggerFunction

### CellControl
```
onActiveField
onRemoveField
...otherFunction
```

### DropComponent Bubble to Render
### Render
```
bubble:
  onAddComponent
  onAddField
  onSwapComponent
self:
  onActiveComponent
```


### DynamicTable Component



## 数据流向

### Prop

#### DynamicTable

- LinkButton
```
  fetch:

  field: {
    config: [
      {
        [attributeNameProp]: "[btnKey][attrName]",
        [attributeValueProp]: "value",
        [attributeTypeProp]: DataType.**,
        // ...
      },
    ],
  }
  
  init deal: index by orderSeq

  field: {
    config: [
      // ...
    ],
    btns: [
      [
        {
          [attributeNameProp]: "[btnKey][attrName]",
          [attributeValueProp]: "value",
          [attributeTypeProp]: DataType.**,
        },
      ],
    ],
  },
  
  parse: orderSeq is index

  field: {
    config: [
      {
        [attributeNameProp]: "[btnKey][attrName]",
        [attributeValueProp]: "value",
        [attributeTypeProp]: DataType.**,
        // ...
      },
    ],
  }
  
```


