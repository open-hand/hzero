const { appPath } = require('./config/paths');

module.exports = {
  parser: '@typescript-eslint/parser',
  extends: ['airbnb', 'prettier'],
  env: {
    browser: true,
    node: true,
    es6: true,
    mocha: true,
    jest: true,
    jasmine: true,
  },
  plugins: ['hzero', '@typescript-eslint'],
  rules: {
    'no-underscore-dangle': [1],
    'no-restricted-globals': [1],
    'no-use-before-define': [1],
    'no-restricted-syntax': [1],
    'consistent-return': [1],
    'no-plusplus': [1],
    'no-nested-ternary': [1],
    'react/destructuring-assignment': [1],
    'react/sort-comp': [1],
    'import/prefer-default-export': [1],
    // can't reg alias now
    'import/no-unresolved': [1],
    'sort-vars': [0],
    'sort-keys': [0],
    'sort-imports': [0],
    // will change in production
    'no-debugger': [1],
    // will change in production
    // need to see config detail
    'import/extensions': [
      1,
      {
        ...['js', 'ts','jsx','tsx'].reduce((importExtensions, ext) => ({...importExtensions, [ext]: 'never'}), {}),
        ...['less','png','svg','jpeg','jpg',''].reduce((importExtensions, ext) => ({...importExtensions, [ext]: 'always'}), {}),
      },
    ],
    // React.FC why need propTypes
    'react/prop-types': [1],
    // why move dep to runtime
    'import/no-extraneous-dependencies': [1],
    'hzero/file-exists': [
      1,
      {
        excludes: [],
        extensions: ['', '.js', '.jsx', '.ts', '.tsx', '.d.ts', '.less', '.css', '.scss', '.sass'],
        alias: [
          {
            source: 'utils',
            alias: 'hzero-front/lib/utils',
          },
          {
            source: 'components',
            alias: 'hzero-front/lib/components',
          },
          {
            source: 'services',
            alias: 'hzero-front/lib/services',
          },
          {
            source: '@/',
            alias: `${appPath}/src/`,
          },
        ],
      },
    ],
    'hzero/no-custom-tag': [
      1,
      {
        'no-all': true,
        customElementTags: [],
      },
    ],
    'react/jsx-filename-extension': [1, { extensions: ['.js'] }],
    'global-require': [1],
    'react/jsx-wrap-multilines': [
      'error',
      {
        declaration: 'parens-new-line',
        assignment: 'parens-new-line',
        return: 'parens-new-line',
        arrow: 'parens-new-line',
        condition: 'parens-new-line',
        logical: 'parens-new-line',
        prop: 'ignore',
      },
    ],
    'comma-dangle': [
      'error',
      {
        arrays: 'always-multiline',
        objects: 'always-multiline',
        imports: 'always-multiline',
        exports: 'always-multiline',
        functions: 'ignore',
      },
    ],
    'require-yield': [1],
    'no-trailing-spaces': [2],
    semi: [2],
    // 关闭 js 的 lint
    'no-unused-vars': [0],
    // 打开 ts 的 lint
    '@typescript-eslint/no-unused-vars': [
      'error',
      {
        vars: 'all',
        args: 'after-used',
        ignoreRestSiblings: true,
      },
    ],
    'no-multi-spaces': [2],
    'key-spacing': [2],
    'comma-spacing': [2],
    curly: [2],
  },
  parserOptions: {
    parser: 'babel-eslint',
    ecmaFeatures: {
      legacyDecorators: true,
    },
  },
  settings: {
    polyfills: ['fetch', 'promises'],
    'import/resolver': {
      alias: {
        map: [
          ['@', './src'],
          ['components', 'hzero-front/lib/components'],
          ['utils', 'hzero-front/lib/utils'],
          ['services', 'hzero-front/lib/services'],
        ],
        extensions: ['.ts', '.tsx', '.js', '.jsx', '.json'],
      },
    },
  },
  overrides: [
    {
      files: ['*.ts', '*.tsx'],
      excludedFiles: ['*.d.ts'],
    },
  ],
};
