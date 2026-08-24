# TPL

## 每个组件都需要拥有自己的获取数据的方法

```typescript
function getData(): Promise<any> {
  return Promise.reject();
}

function getValidationData(): Promise<any> {
  return Promise.reject();
}
```
