import {buildWebsite} from './builder/builder.js'

function getDataFilePath() {
  return './data/' + getDataFilePathFragment() + ".js";
}
function getDataFilePathFragment() {
  let path = window.location.pathname;
  if (path === '/') { return 'index'; }

  let page = path.substr(path.lastIndexOf('/') + 1).replace('.html', '');

  switch (page) {
    case 'index': return 'index';
    case 'media': return 'media';
    case 'news': return 'news';
    case 'docs': return 'commands';
    default: return 'none';
  }
}

$(async function () {
  let path = getDataFilePath();
  console.log('Loading data: ' + path);
  let data = await import(path);

  buildWebsite(await data.getData());
});
