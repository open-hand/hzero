export function isValidArray(arr) {
  return Array.isArray(arr) && arr.length > 0;
}

export function isNullOrUndefined(v) {
  return v == null;
}
