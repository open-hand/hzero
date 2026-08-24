const { readFileAsync } = require('hzero-front-util/bin/buildin/fileUtils');
const { resolve } = require('hzero-front-util/bin/buildin/pathUtils');
const { formatString } = require('hzero-front-util/bin/buildin/stringUtils');
const { travelAllProjects } = require('hzero-front-util/bin/buildin/projectUtils');

travelAllProjects(async (projectName, projectPath) => {
  let hasError = false;
  try {
    const content = await readFileAsync(resolve(projectPath, 'locale/code-scan/process/file-error.log'));
    const contentString = content.toString();
    if (contentString !== '\'\'\n') {
      throw true;
    }
  } catch (e) {
    if (e === true) {
      console.log(formatString('%12s has error intl', projectName));
    }
  }
}).catch(console.error);
