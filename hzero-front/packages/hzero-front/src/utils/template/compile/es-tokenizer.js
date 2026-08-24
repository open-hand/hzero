import isKeyword from 'is-keyword-js';
import jsTokens, { matchToToken } from 'js-tokens';

/**
 * 将逻辑表达式解释为 Tokens
 * @param {string} code
 * @return {Object[]}
 */
const esTokenizer = code => {
  const tokens = code
    .match(jsTokens)
    .map(value => {
      jsTokens.lastIndex = 0;
      return matchToToken(jsTokens.exec(value));
    })
    .map(token => {
      if (token.type === 'name' && isKeyword(token.value)) {
        // eslint-disable-next-line no-param-reassign
        token.type = 'keyword';
      }
      return token;
    });

  return tokens;
};

export default esTokenizer;
