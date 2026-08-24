const caches = {
  __data: Object.create(null),

  set(key, val) {
    this.__data[key] = val;
  },

  get(key) {
    return this.__data[key];
  },

  reset() {
    this.__data = {};
  },
};

export default caches;
