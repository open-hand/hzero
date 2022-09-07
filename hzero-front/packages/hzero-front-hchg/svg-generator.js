const generator = require('typescript-react-svg-icon-generator');

const config = {
    svgDir: './src/assets/svg/',
    destination: './src/components/Icon/index.tsx',
};

generator(config);
