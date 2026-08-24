# 和平台级的区别

## ! 和平台级没有区别, 布局继承不了

## TODO

### 组件中的异同

1. 根据 传进来的变量不同来判断
2. CellControl的删除, 如果是平台级将 字段放在 removeSiteFields
3. 字段的 fieldName 如果是平台级不能更改

## 完成

### 查询和存储方法

1. 查询方法, 在model 中修改 将 visiableFlag 为 0 且 siteFlag 为 1 的 field 放到removeSiteFields 中, 将 visiableFlag 为 1 的保留 
2. 保存方法, 需要逐层级比较, 存储租户的和本次修改的, 以及所有删除的平台级的字段

## 预留

1. `./utils` 中的内容是更加优化的层级属性
