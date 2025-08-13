export function isArray(arr) {
  return Array.isArray(arr);
}

export function isValidArray(arr) {
  return Array.isArray(arr) && arr.length > 0;
}

export function isObject(obj) {
  return typeof obj === 'object' && obj !== null;
}

export function getCategoryValue(key, categories, mapKey, mapValue) {
  if (isValidArray(categories)) {
    const category = categories.find((v) => v[mapKey] === key);
    return category ? category[mapValue] : null;
  }
  return null;
}
