// const http = require('http');
// const chalk = require('chalk');
// const inquirer = require('inquirer');
// const ora = require('ora');
// const moment = require('moment');

// const spinner = new ora();
// const ignores = ['hzero-front-app', 'hzero-front-demo', 'hzero-schematics'];
// const npmRepositories = 'http://nexus.saas.hand-china.com/content/repositories/hzero-ui/';

// async function getVersions(modules, tag) {
//   spinner.color = 'yellow';
//   spinner.start();
//   spinner.text = `${chalk.yellow(`......\n`)}`;
//   await http.get(`${npmRepositories}/${modules}`, (res) => {
//     const moduleChunks = []
//     res.on('data', (chunk) => {
//       moduleChunks.push(chunk);
//     });
//     res.on('end', () => {
//       const info = JSON.parse(moduleChunks.map(item => item.toString()).join(''));
//       if (tag === 'latest') {
//         const distTags = info['dist-tags'];
//         const { time = {} } = info;
//         console.log(`${chalk.yellow('version: ')}${chalk.green(distTags['latest'])}, ${chalk.yellow('release time: ')}${chalk.green(moment(time[distTags['latest']]).format('YYYY-MM-DD'))}`)
//         console.log();
//       } else {
//         const { versions = {}, time = {} } = info;
//         const reverseVersions = Object.keys(versions).sort();
//         reverseVersions.forEach((item) => {
//           console.log(`${chalk.yellow('version: ')}${chalk.green(item)}, ${chalk.yellow('release time: ')}${chalk.green(moment(time[item]).format('YYYY-MM-DD'))}`);
//           console.log();
//         })
//       }
//       spinner.color = 'green';
//       spinner.text = `${chalk.green(`succeed`)}`;
//       spinner.succeed();
//     }).on('error', (e) => {
//       console.error(`${chalk.red(e.message)}`);
//     });
//   })
// }

// http.get(npmRepositories, (res) => {
//   const chunks = [];
//   const { statusCode } = res;

//   let error;
//   if (statusCode !== 200) {
//     error = new Error(`请求失败，状态码: ${statusCode}`);
//   }
//   if (error) {
//     console.error(chalk.red(error.message));
//     // 消费响应数据来释放内存。
//     res.resume();
//     return;
//   }
//   res.on('data', (chunk) => chunks.push(chunk));
//   res.on('end', () => {
//     const hzeroPkgs = [];
//     chunks.map(item => item.toString()).join().split('\n').map((line) => {
//       return line.trim();;
//     }).forEach((item) => {
//       const m = item.match(/<td><a\ href="http:\/\/nexus\.saas\.hand-china\.com\/content\/repositories\/hzero-ui\/(hzero(-front)?(-\w+)?)\/">\1\/<\/a><\/td>$/);
//       if (m && m[1]) {
//         if (!ignores.includes(m[1])) {
//           hzeroPkgs.push(m[1]);
//         }
//       }
//     })
//     inquirer.prompt([
//       {
//         type: 'list',
//         name: 'modules',
//         message: 'Please select the module you want to view ?',
//         choices: hzeroPkgs,
//       }
//     ]).then(({ modules }) => {
//       inquirer.prompt({
//         type: 'list',
//         name: 'tag',
//         message: 'Please select the type of version you want to view ?',
//         choices: ['latest', 'all'],
//       }).then(({ tag }) => {
//         getVersions(modules, tag);
//       })
//     })
//   }).on('error', (e) => {
//     console.error(`${chalk.red(e.message)}`);
//   });
// });

const http = require('http');
const chalk = require('chalk');
const inquirer = require('inquirer');
const ora = require('ora');

const spinner = new ora();
const ignores = ['hzero-front-app', 'hzero-front-demo', 'hzero-schematics'];
const npmRepositories = 'http://nexus.saas.hand-china.com/service/rest/repository/browse/hzero-ui/';
async function getVersions(modules, tag) {
  spinner.color = 'yellow';
  spinner.start();
  spinner.text = `${chalk.yellow(`......\n`)}`;
  await http.get(`${npmRepositories}${modules}/`, (res) => {
    const moduleChunks = []
    res.on('data', (chunk) => {
      moduleChunks.push(chunk);
    });
    res.on('end', () => {
    let hzeroPkgs = ''
        const info ={}
        const time ={}
        const versions ={}
        moduleChunks.map(item => item.toString()).join().split('\n').map((line) => {
        return line.trim();
      }).forEach((item) => {
      const m = item.match(/<td><a\ href="http:\/\/nexus\.saas\.hand-china\.com\/repository\/hzero-ui\/(hzero(-front)?(-\w+)?)\/-\/(hzero(-front)?(-\S+)?\.tgz)">(hzero(-front)?(-\S+)?\.tgz)<\/a><\/td>$/);
     const n= new Date(item)
        if (m && m[1]) {
          if (!ignores.includes(m[1])) {
          const p =m[4].slice(0,m[4].length-4);
            hzeroPkgs=p;
            versions[p]=''
          }
        }
        if(item &&!Number(item)&&n instanceof Date && !isNaN(n.getTime())){
          const d =new Date(item)
          time[hzeroPkgs]=d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate(); 
        }
      })
      info.time =time;
      info.versions =versions;
      if (tag === 'latest') {
        const { versions = {}, time = {} } = info;
        const reverseVersions = Object.keys(versions).sort();
        console.log(`${chalk.yellow('version: ')}${chalk.green(reverseVersions.reverse()[0])}, ${chalk.yellow('release time: ')}${chalk.green(time[reverseVersions.reverse()[0]])}`)
        console.log();
      } else {
        const { versions = {}, time = {} } = info;
        const reverseVersions = Object.keys(versions).sort();
        const b=[]
        reverseVersions.forEach((item) => {
          if(item){
            console.log(`${chalk.yellow('version: ')}${chalk.green(item)}, ${chalk.yellow('release time: ')}${chalk.green(time[item])}`);
            console.log();
            b.push(time[item])
          }
        })
      }
      spinner.color = 'green';
      spinner.text = `${chalk.green(`succeed`)}`;
      spinner.succeed();
    }).on('error', (e) => {
      console.error(`${chalk.red(e.message)}`);
    });
  })
}

http.get(npmRepositories, (res) => {
  const chunks = [];
  const { statusCode } = res;

  let error;
  if (statusCode !== 200) {
    error = new Error(`请求失败，状态码: ${statusCode}`);
  }

  if (error) {
    console.error(chalk.red(error.message));
    // 消费响应数据来释放内存。
    res.resume();
    return;
  }
  res.on('data', (chunk) => chunks.push(chunk));
  res.on('end', () => {
    const hzeroPkgs = [];
    chunks.map(item => item.toString()).join().split('\n').map((line) => {
      return line.trim();
    }).forEach((item) => {
      const m = item.match(/<td><a\ href="(hzero(-front)?(-\w+)?)\/">(hzero(-front)?(-\w+)?)<\/a><\/td>$/);
      if (m && m[1]) {
        if (!ignores.includes(m[1])) {
          hzeroPkgs.push(m[1]);
        }
      }
    })
    inquirer.prompt([
      {
        type: 'list',
        name: 'modules',
        message: 'Please select the module you want to view ?',
        choices: hzeroPkgs,
      }
    ]).then(({ modules }) => {
      inquirer.prompt({
        type: 'list',
        name: 'tag',
        message: 'Please select the type of version you want to view ?',
        choices: ['latest', 'all'],
      }).then(({ tag }) => {
        getVersions(modules, tag);
        http.get(npmRepositories, (res) => {

});
      })
    })
  }).on('error', (e) => {
    console.error(`${chalk.red(e.message)}`);
  });
});